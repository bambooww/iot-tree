package org.iottree.driver.zigbee.zstack;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UAPrj;
import org.iottree.core.UAUtil;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.zspec.util.RunRes;

public class ZStackManager
{
	protected static ILogger log = LoggerManager.getLogger(ZStackManager.class);
	
	public static ZStackManager getInstanceByPath(String path,StringBuilder failedr)
	{
		UANode node = UAUtil.findNodeByPath(path) ;
		if(node==null)
		{
			failedr.append("no node found") ;
			return null;
		}
		
		DevDriver dd = null ;
		if(node instanceof UACh)
		{
			UACh ch  = (UACh)node ;
			dd = ch.getDriver() ;
		}
		else if(node instanceof UADev)
		{
			UADev dev = (UADev)node ;
			dd = dev.getRelatedDrv() ;
		}
		
		if(dd==null||Convert.isNullOrEmpty(dd.getDriverMgrUrl()))
		{
			failedr.append("no driver or no driver url found") ;
			return null;
		}
		
		ZStackDriver z_dd = null;
		if(!(dd instanceof ZStackDriver))
		{
			failedr.append("not ZStackDriver") ;
			return null;
		}
		 z_dd = (ZStackDriver)dd ;
		return z_dd.getZStackManager() ;
	}
	
	ZStackDriver driver ;
	
	private transient LinkedList<MsgReqRsp> cachedMRR = new LinkedList<>() ;
	
	private transient int MRR_MAX = 100 ;
	
	private transient int TRANS_ID = 0 ;
	
	protected ZStackCoor coor = null;
	
	
	ZStackManager(ZStackDriver zsd)
	{
		this.driver = zsd ;
		//coor = new ZStackCoor(this) ;
		log.setCurrentLogLevel(ILogger.LOG_LEVEL_DEBUG);
	}
	
	public ZStackDriver getDriver()
	{
		return this.driver ;
	}
	
	UACh getBelongToCh()
	{
		return this.driver.getBelongToCh() ;
	}
	
	public ZStackCoor getCoor()
	{
		if(coor!=null)
			return this.coor ;
		return coor = new ZStackCoor(this) ;
	}
	
	public synchronized int get_next_trans_id()
	{
		TRANS_ID ++ ;
		if(TRANS_ID>255)
			TRANS_ID = 0 ;
		return TRANS_ID ;
	}
	/**
	 * 列举
	 * @return
	 */
	public synchronized List<MsgReqRsp> listCachedMRR()
	{
		ArrayList<MsgReqRsp> rets =new ArrayList<>() ;
		Iterator<MsgReqRsp> iter = cachedMRR.descendingIterator() ;
		while(iter.hasNext())
		{
			rets.add(iter.next()) ;
		}
		return rets ;
	}
	
	public MsgReqRsp getCachedMRR(String uid)
	{
		List<MsgReqRsp> mrrs = listCachedMRR() ;
		for(MsgReqRsp mrr:mrrs)
		{
			if(uid.equals(mrr.getUID()))
				return mrr ;
		}
		return null ;
	}
	
	private synchronized void pushToCache(MsgReqRsp mrr)
	{
		cachedMRR.add(mrr) ;
		if(cachedMRR.size()>MRR_MAX)
			cachedMRR.removeFirst() ;
	}
	
	synchronized void RT_onUnknownResp(String hex_str)
	{
		System.out.println(" unknown resp="+hex_str);
	}
	
	private transient MsgReqRsp curReqRsp = null ;

	synchronized void RT_onMsgRecv(MsgRecv m_recv)
	{
		if(this.curReqRsp!=null)
		{
			if(this.curReqRsp.RT_onMsgRecv(m_recv))
				return ;
		}
		
		try
		{
			// 独立的一些callback
			this.getCoor().RT_onCoorIndCallback(m_recv);
		}
		catch(Exception ee)
		{
			if(log.isWarnEnabled())
				log.warn("RT_onCoorIndCallback:"+m_recv,ee);
		}
	}
	
	/**
	 * 支持调试测试使用的
	 * @param msg_hex
	 * @param failedr
	 * @return
	 * @throws Exception
	 */
	public boolean RT_triggerMsgRecv(String msg_hex,StringBuilder failedr) throws Exception
	{
		byte[] bs = Convert.hexStr2ByteArray(msg_hex) ;
		Msg m = MsgManager.unpackedMsg(bs, failedr) ;
		if(m==null)
			return false;
		if(!(m instanceof MsgRecv))
		{
			failedr.append("not MsgRecv") ;
			return false;
		}
		
		RT_onMsgRecv((MsgRecv)m) ;
		return true ;
	}

	public boolean RT_synReqRsp(MsgReqRsp rr,StringBuilder failedr) throws Exception
	{
		try
		{
			synchronized(this)
			{
				this.curReqRsp = rr ;
			}
			
			if(rr.debugOut)
				System.out.println("ReqRsp req_msg="+rr.sreqMsg.getPackHexStr()) ;
			
			if(!this.driver.RT_sendReq(rr.sreqMsg, failedr))
				return false;
			rr.reqEndDT = System.currentTimeMillis() ;
			int end_res =0 ;
			while((end_res=rr.RT_checkEnd())==0)
			{
				Thread.sleep(5);
			}
			if(end_res<0)
			{
				if(failedr!=null)
					failedr.append(rr.getErrInf()) ;
				return false;
			}
			return true ;
		}
		finally
		{
			pushToCache(rr) ;
			
			synchronized(this)
			{
				this.curReqRsp = null ;
			}
		}
	}
	
	public static final int RT_ST_PING = 0 ;
	public static final int RT_ST_INIT_BEFORE_START = 1 ;
	public static final int RT_ST_STARTUP = 2 ;
	public static final int RT_ST_INIT_AFTER_START = 3 ;
	public static final int RT_ST_INTERVIEW = 4 ;
	public static final int RT_ST_NORMAL_RUNNING = 5 ;
	
	private transient int rt_st = RT_ST_PING ;
	/**
	 * state machine to run coor schedule
	 */
	private void RT_runInLoop() throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
		ZStackCoor coor = getCoor() ;
		switch(rt_st)
		{
		case RT_ST_PING: //ping
			if(coor.RT_ping(failedr))
			{
				if(log.isDebugEnabled())
					log.debug("ping succ");
				rt_st = RT_ST_INIT_BEFORE_START ;
			}
			else
			{
				if(log.isWarnEnabled())
					log.warn("ping failedr:"+failedr);
				sleep(3000) ;
			}
			break ;
		case RT_ST_INIT_BEFORE_START: //RT_init_before_start
			if(coor.RT_init_before_start(failedr))
			{
				if(log.isDebugEnabled())
					log.debug("init_before_start succ");
				rt_st = RT_ST_STARTUP ;
			}
			else
			{
				if(log.isWarnEnabled())
					log.warn("RT_init_before_start failed:"+failedr);
				sleep(1000) ;
			}
			break ;
		case RT_ST_STARTUP: // start up coor
			if(coor.RT_startup(failedr))
			{
				if(log.isDebugEnabled())
					log.debug("startup succ");
				rt_st = RT_ST_INIT_AFTER_START ;
			}
			else
			{
				if(log.isWarnEnabled())
					log.warn("RT_startup failed:"+failedr);
				sleep(1000) ;
				RT_reset(true) ; //force restart and reconn
			}
			break ;
		case RT_ST_INIT_AFTER_START: // register endpoint
			if(coor.RT_init_after_start(failedr))
			{
				if(log.isDebugEnabled())
					log.debug("init_after_start succ");
				rt_st = RT_ST_INTERVIEW ;
			}
			else
			{
				if(log.isWarnEnabled())
					log.warn("init_after_start failed:"+failedr);
				sleep(1000) ;
			}
			break ;
		case RT_ST_INTERVIEW: // inter view
			if(coor.RT_interview(failedr))
			{
				if(log.isDebugEnabled())
					log.debug("RT_interview succ,then goto normal running");
				rt_st = RT_ST_NORMAL_RUNNING ;
			}
			else
			{
				if(log.isWarnEnabled())
					log.warn("init_after_start failed:"+failedr);
				sleep(1000) ;
			}
			break ;
		case RT_ST_NORMAL_RUNNING: //normal working - ctrl etc
			RunRes res = coor.RT_onCoorNorRunning() ;
			if(res==RunRes.reset)
				RT_reset(false) ;
			else if(res==RunRes.reset_disconn)
				RT_reset(true) ;
			break; 
		}
	}
	
	public int RT_getState()
	{
		return this.rt_st ;
	}
	
	private Runnable runner = new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						rt_st = 0 ; //restart;
						
						while(thread!=null)
						{
							try
							{
								RT_runInLoop() ;
								
								sleep(5);
							}
							catch(Exception  ee)
							{
								ee.printStackTrace();
							}
						}
					}
					finally
					{
						thread = null ;
					}
				}
			};
	
	private Thread thread = null ;
	
	public synchronized void RT_start()
	{
		if(this.thread!=null)
			return ;
		
		this.thread = new Thread(runner) ;
		this.thread.start();
	}
	
	public synchronized void RT_stop()
	{
		if(this.thread==null)
			return ;
		try
		{
			this.thread.interrupt(); 
		}
		finally
		{
			this.thread = null ;
		}
	}
	
	public synchronized void RT_reset(boolean dis_conn)
	{
		if(dis_conn)
		{
			disconnDrvConnPt();
		}
		
		rt_st = RT_ST_PING ;
	}
	
	public synchronized void RT_restart(boolean dis_conn)
	{
		RT_stop() ;
		if(dis_conn)
		{
			disconnDrvConnPt();
		}
		sleep(200) ;
		RT_start() ;
	}
	
	private void disconnDrvConnPt()
	{
		ConnPtStream cps = this.driver.getBindConnPtStream() ;
		if(cps!=null)
			cps.disconnect(); //
	}
	
	public boolean RT_isRunning()
	{
		return this.thread!=null ;
	}
	
	private static void sleep(int ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch(Exception ee)
		{}
	}
}
