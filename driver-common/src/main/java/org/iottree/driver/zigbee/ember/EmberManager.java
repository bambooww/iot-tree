package org.iottree.driver.zigbee.ember;

import org.iottree.core.ConnPt;
import org.iottree.core.UACh;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.driver.zigbee.ZigbeeManager;
import org.iottree.driver.zigbee.ember.ezsp.*;
import org.iottree.driver.zigbee.ember.req_rsp.VersionRR;
import org.iottree.driver.zigbee.ember.util.EzspError;
import org.iottree.driver.zigbee.ember.util.EzspStatus;
import org.iottree.driver.zigbee.zspec.ZSDriver;
import org.iottree.driver.zigbee.zspec.util.RunRes;

/**
 * Host状态机运行
 * 
 * @author jason.zhu
 *
 */
public class EmberManager
{
	protected static ILogger log = LoggerManager.getLogger(EmberManager.class);
	
	public static EmberManager getInstanceByPath(String path,StringBuilder failedr)
	{
		ZSDriver drv = ZigbeeManager.getZSDriverByPath(path,failedr) ;
		if(drv==null)
			return null ;
		EmberDriver z_dd = (EmberDriver)drv ;
		return z_dd.getManager() ;
	}
	
	EmberDriver driver ;
	
	private int hostST = 0 ;
	
	protected EmberCoor coor = null;
	
	EmberSlideWin slideWin;
	
	private int ezspVer = VersionCmd.EZSP_DESIRED_PROTOCOL_VERSION;
	
	//一开始使用传统方式发送EZSP reset 和 version指令
	// 接收到version之后，根据版本使之false，进入新的EZSP格式
	private boolean bLegacy = true;
	
	/**
	 * EZSPFrame seq num
	 */
	private transient int ezspSeq = 0 ;
	
	private EZSPWaiterEvt eventH = null;
	
	EmberManager(EmberDriver drv)
	{
		this.driver = drv ;
		//coor = new ZStackCoor(this) ;
		log.setCurrentLogLevel(ILogger.LOG_LEVEL_DEBUG);
		
		this.slideWin = new EmberSlideWin(this);//,5,100) ;
		this.eventH = new EZSPWaiterEvt(this,null,1000) ;
	}
	
	public EmberDriver getDriver()
	{
		return this.driver ;
	}
	
	public EmberCoor getCoor()
	{
		if(coor!=null)
			return this.coor ;
		return coor = new EmberCoor(this.driver,this) ;
	}
	
	public EZSPWaiterEvt getEvtHandle()
	{
		return this.eventH ;
	}
	
	public String getAdapterTitle()
	{
		UACh ch = this.driver.getBelongToCh() ;
		ConnPt connpt = null;
		try
		{
			connpt = ch.getConnPt() ;
		}
		catch ( Exception e)
		{
		}
		String r = "" ;
		if(connpt!=null)
			r = connpt.getName()+"->";
		r += ch.getTitle()+"["+ch.getName()+"]" ;
		return r ;
	}
	
	UACh getBelongToCh()
	{
		return this.driver.getBelongToCh() ;
	}
	
	public int getEZSPVersion()
	{
		return this.ezspVer ;
	}
	
	public boolean isLegacy()
	{
		return this.bLegacy ;
	}
	
	/**
	 * 设置EZSPFrame使用传统格式
	 * @param b
	 * @return
	 */
	public void setLegacy(boolean b)
	{
		this.bLegacy = b ;
	}
	
	public synchronized void RT_sendFrmData(ASHFrmData fd) throws Exception
	{
		if(!this.driver.isBindConnPtReady())
			throw new EzspError(EzspStatus.NOT_CONNECTED) ;
		
		fd.getEZSPFrame().seq = this.ezspSeq ;
		//System.out.println(" *** ezsp frame seq="+this.ezspSeq) ;
		this.ezspSeq = (this.ezspSeq+1) % 0xFF ;
		this.slideWin.pushFrmData(fd) ;
		if(log.isDebugEnabled())
			log.debug("send_frm_data="+fd.getEZSPFrame().toShortString());
	}
	
	public void RT_sendReset()
	{
		ASHFrmRst rst = new ASHFrmRst() ;
		this.slideWin.queSendingFrm(rst) ;
	}
	
	public void RT_sendVersionCmd() throws Exception
	{
		VersionCmd vc = new VersionCmd(this,this.ezspVer) ;
		ASHFrmData fd = ASHFrmData.createFrmDataCmd(this,vc) ;
		RT_sendFrmData(fd) ;
	}

	synchronized void RT_onUnknownResp(String hex_str)
	{
		System.out.println(" unknown resp="+hex_str);
	}
	
	private transient EZSPReqRsp curReqRsp = null ;
	
	private transient EZSPWaiter curWaiter = null ;
	
	void RT_WIN_onFrameData(ASHFrmData fd)
	{
		//System.out.println("on frame data recved="+fd) ;
		EZSPRsp f = (EZSPRsp)fd.getEZSPFrame() ;
		if(log.isDebugEnabled())
			log.debug("recv_frm_data="+f.toShortString()) ;

		if(f!=null)
		{
			if(curReqRsp!=null)
			{
				if(curReqRsp.RT_onRspRecv(f))
					return ;
			}
			
			if(this.curWaiter!=null)
			{
				if(this.curWaiter.RT_onRspRecv(fd,f))
					return ;
			}
		}
		
		this.eventH.RT_onEZSPFrmCallback(fd.getEZSPFrmIDUnpacked(),fd.getEZSPFrmBufUnpacked(), f);
	}
	
	void RT_WIN_onFrameRstAck(ASHFrmRstAck rstack)
	{
		this.hostST = 1 ;
	}
	
	/**
	 * called by EmberDriver
	 * @param m_recv
	 * @throws Exception
	 */
	synchronized void RT_onRecvFromDriver(ASHFrame m_recv) throws Exception
	{
		//System.out.println("on frame recved="+m_recv) ;
		this.slideWin.RT_onRecvFrm(m_recv);
	}
	
	public boolean RT_synReqRsp(EZSPReqRsp rr,StringBuilder failedr) throws Exception
	{
		try
		{
			synchronized(this)
			{
				this.curReqRsp = rr ;
			}
			
			if(rr.debugOut)
				System.out.println("ReqRsp req_msg="+rr.reqCmd.toEZSPPackOrErr()) ;
			
			ASHFrmData fd = ASHFrmData.createFrmDataCmd(this, rr.reqCmd) ;
			this.RT_sendFrmData(fd) ;
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
			//pushToCache(rr) ;
			
			synchronized(this)
			{
				this.curReqRsp = null ;
			}
		}
	}
	
	
	/**
	 * 等待一个特定的消息到来，直到时间过期
	 */
	public EZSPRsp RT_synWaitFor(EZSPWaiter waiter)
	{
		waiter.startWait();
		try
		{
			synchronized(this)
			{
				this.curWaiter = waiter ;
			}
			
			int end_res =0 ;
			while((end_res=waiter.RT_checkEnd())==0)
			{
				sleep(5);
			}
			if(end_res<0)
			{
				return null;
			}
			return waiter.getWaitRsp() ;
		}
		finally
		{
			synchronized(this)
			{
				this.curWaiter = null ;
			}
		}
	}
	
	private transient long RT_lastSend = -1 ;
	
	private boolean RT_sendWait(long ms)
	{
		boolean b = System.currentTimeMillis()-RT_lastSend<ms ;
		if(b) return true;
		RT_lastSend = System.currentTimeMillis() ;
		return false; //not wait ;
	}
	
	public static final int RT_ST_INIT_BEFORE_START = 3 ;
	public static final int RT_ST_STARTUP = 4 ;
	public static final int RT_ST_INIT_AFTER_START = 5 ;
	public static final int RT_ST_INTERVIEW = 6 ;
	public static final int RT_ST_NORMAL_RUNNING = 7 ;
	/**
	 * state machine to run coor schedule
	 */
	private void RT_runStateMachineLoop() throws Exception
	{
		StringBuilder failedr = new StringBuilder() ;
//		EmberDriver drv = this.getDriver() ;
		switch(this.hostST)
		{
		case 0: //定时发送rst指令
			if(RT_sendWait(8000))
				return ;
			try
			{
				log.info(getAdapterTitle()+" send reset") ;
				RT_sendReset() ;
			}
			catch ( Exception e)
			{
				if(log.isWarnEnabled())
					log.warn("RT_sendReset err",e);
				//e.printStackTrace();
			}
			break ;
		case 1: //send version
			VersionRsp ver_rsp = VersionRR.ezspGetVersion(this,this.ezspVer,failedr) ;
			if(ver_rsp!=null)
			{
				this.ezspVer = ver_rsp.getProtoVer() ;
				this.bLegacy = this.ezspVer<8 ; //当 Host 与 NCP 协商的 EZSP 版本 ≥ 8 时，可以使用扩展帧格式；旧版本设备仍使用传统格式
				log.info(getAdapterTitle()+" find EZSP version of NCP="+this.ezspVer+" switch to this ver");
				hostST = 2 ;
				return ;
			}
			sleep(5000) ;
//			try
//			{
//				this.RT_sendVersionCmd();
//			}
//			catch ( Exception e)
//			{
//				e.printStackTrace();
//				sleep(5) ;
//			}
			
			return ;
		case 2: //send version again,switch to current EZSP proto version
			ver_rsp = VersionRR.ezspGetVersion(this,this.ezspVer,failedr) ;
			if(ver_rsp!=null)
			{
				this.ezspVer = ver_rsp.getProtoVer() ;
				this.bLegacy = this.ezspVer<8 ; //当 Host 与 NCP 协商的 EZSP 版本 ≥ 8 时，可以使用扩展帧格式；旧版本设备仍使用传统格式
				log.info(getAdapterTitle()+" find EZSP version of NCP="+this.ezspVer+" switch ver ok");
				hostST = RT_ST_INIT_BEFORE_START ;
				return ;
			}
			sleep(5000) ;
//			if(RT_sendWait(5000))
//				return ;
//			try
//			{
//				this.RT_sendVersionCmd();
//			}
//			catch ( Exception e)
//			{
//				e.printStackTrace();
//				sleep(5) ;
//			}
			//sleep(5000) ;
			return   ;
		case RT_ST_INIT_BEFORE_START: //in normal state
			if(getCoor().RT_init_before_start(failedr))
			{
				log.info(getAdapterTitle()+" init_before_start succ");
				hostST = RT_ST_STARTUP ;
			}
			else
			{
				if(log.isWarnEnabled())
					log.warn(getAdapterTitle()+" RT_init_before_start failed:"+failedr);
				sleep(1000) ;
			}
			break ;
		case RT_ST_STARTUP:
			if(getCoor().RT_startup(failedr))
			{
				if(log.isDebugEnabled())
					log.debug("startup succ");
				hostST = RT_ST_INIT_AFTER_START ;
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
			if(getCoor().RT_init_after_start(failedr))
			{
				if(log.isDebugEnabled())
					log.debug("init_after_start succ");
				hostST = RT_ST_INTERVIEW ;
			}
			else
			{
				if(log.isWarnEnabled())
					log.warn("init_after_start failed:"+failedr);
				sleep(1000) ;
			}
			break ;
		case RT_ST_INTERVIEW:
			if(getCoor().RT_interview(failedr))
			{
				if(log.isDebugEnabled())
					log.debug("RT_interview succ,then goto normal running");
				hostST = RT_ST_NORMAL_RUNNING ;
			}
			else
			{
				if(log.isWarnEnabled())
					log.warn("init_after_start failed:"+failedr);
				sleep(1000) ;
			}
			break ;
		case RT_ST_NORMAL_RUNNING:
			RunRes res = getCoor().RT_onCoorNorRunning() ;
			if(res==RunRes.reset)
				RT_reset(false) ;
			else if(res==RunRes.reset_disconn)
				RT_reset(true) ;
			sleep(5) ;
			break ;
		default:
			this.hostST = 0 ;
			break ;
		}
	}
	
	public int RT_getHostState()
	{
		return this.hostST ;
	}
	
	private Runnable runner = new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						hostST = 0 ; //restart;
						while(thread!=null)
						{
							try
							{
								RT_runStateMachineLoop() ; //run state machine
							}
							catch(Exception  ee)
							{
								ee.printStackTrace();
								sleep(5) ;
							}
						}
					}
					finally
					{
						thread = null ;
						if(slideWin!=null)
						{
							slideWin.RT_stop();
						}
					}
				}
			};
	
	private Thread thread = null ;
	
	public synchronized void RT_start()
	{
		if(this.thread!=null)
			return ;
		
		slideWin = new EmberSlideWin(this);//,5,100) ;
		slideWin.RT_start();
		
		this.thread = new Thread(runner) ;
		this.thread.start();
	}
	
	public synchronized void RT_stop()
	{
		if(this.thread==null)
			return ;
		
		slideWin.RT_stop();
		
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
		bLegacy = true ;
		ezspVer = VersionCmd.EZSP_DESIRED_PROTOCOL_VERSION;
		slideWin.reset();
		this.hostST = 0 ;
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
	
	static void sleep(int ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch(Exception ee)
		{}
	}
}
