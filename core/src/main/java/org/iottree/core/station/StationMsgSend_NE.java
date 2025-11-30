package org.iottree.core.station;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.msgnet.util.TopicMsg;
import org.iottree.core.msgnet.util.ValPack;
import org.iottree.core.station.StationLocSaver.Item;
import org.iottree.core.station.StationLocal.PrjSynPm;
import org.iottree.core.station.StationLocal.QItem;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.java_websocket.enums.ReadyState;
import org.json.JSONObject;

/**
 * for edge station msgnode send json msg to platform
 * 
 * this node is used to send special data to platfrom
 * 
 * @author jason.zhu
 *
 */
public class StationMsgSend_NE extends MNNodeEnd implements IMNRunner 
{
	static ILogger log = LoggerManager.getLogger(StationMsgSend_NE.class) ;
	
	public static final String TP = "station_msg_send" ;
	
	String topic = null ;
	
	boolean bZip = false;
	
	boolean bFailedSave = false;
	
	String saveName = null ;
	
	int saveMaxNum = -1 ;
	
	@Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPTitle()
	{
		return "Station Send To Platform";
	}

	@Override
	public String getColor()
	{
		return "#1d90ad";
	}
	
	
	@Override
	public String getIcon()
	{
		return "\\uf148";
	}
	
	public String getTopic()
	{
		return this.topic ;
	}
	
	public boolean isZipSend()
	{
		return this.bZip ;
	}
	
	@Override
	public boolean isFitForPrj(UAPrj prj)
	{
		if(prj==null)
			return false;
		return StationLocal.getInstance()!=null;//.isStationValid() ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(StationLocal.getInstance()==null)
		{
			failedr.append("Station Local is not valid") ;
			return false;
		}
		if(Convert.isNullOrEmpty(this.topic))
		{
			failedr.append("Station Local is not valid") ;
			return false;
		}
		
		if(bFailedSave && Convert.isNullOrEmpty(this.saveName))
		{
			failedr.append("no save name set") ;
			return false;
		}
		
		return true ;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("topic", this.topic) ;
		jo.put("zip", this.bZip) ;
		jo.put("failed_save",this.bFailedSave) ;
		jo.putOpt("save_name", this.saveName) ;
		jo.putOpt("save_maxn", this.saveMaxNum) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.topic = jo.optString("topic") ;
		this.bZip = jo.optBoolean("zip",false) ;
		this.bFailedSave = jo.optBoolean("failed_save", false) ;
		this.saveName = jo.optString("save_name") ;
		this.saveMaxNum = jo.optInt("save_maxn", -1) ;
	}
	
//	private UAPrj prj0 = null ;
//	
//	private UAPrj getPrj()
//	{
//		if(prj0!=null)
//			return prj0 ;
//		
//		prj0 = this.getBelongTo().getBelongTo().getBelongToPrj() ;
//		return prj0 ;
//	}
	
	static class QItem
	{
		
		String key ;
		
		ValPack vp ;
		
		public QItem(String key, ValPack vp)
		{
			this.key = key ;
			this.vp = vp ;
		}
	}
	
	private LinkedList<QItem> packQue = new LinkedList<>() ;
	
	private synchronized void enque(QItem vp)
	{
		packQue.addLast(vp);
	}
	
	private QItem deque()
	{
		if(packQue.size()<=0)
			return null ;
		synchronized(this)
		{
			return packQue.removeFirst();
		}
	}
	
	private List<QItem> dequeAll()
	{
		int s = packQue.size() ;
		if(s<=0)
			return null ;
		
		ArrayList<QItem> rets = new ArrayList<>(s) ;
		synchronized(this)
		{
			for(int i = 0 ; i < s; i ++)
				rets.add(packQue.removeFirst()) ;
		}
		return rets ;
	}
	
	private StationLocSaver getSaver()
	{
		if(!this.bFailedSave || Convert.isNullOrEmpty(this.saveName))
			return null;
		//saver
		return StationLocSaver.getSaverNor(this.saveName) ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		StationLocal sl = StationLocal.getInstance() ;
		if(sl==null||!sl.isStationValid() || Convert.isNullOrEmpty(this.topic))
			return null;
		
		UAPrj prj = this.getBelongTo().getBelongTo().getBelongToPrj() ;
		if(prj==null)
			return null ;
		
		ValPack vp = new ValPack(msg.getPayload(),this.bZip) ;
		String keyid = IdCreator.newSeqId() ;
		QItem qi = new QItem(keyid,vp) ;
		enque(qi) ;
		
		RT_start(null);
		
		return null;
	}
	
	private long lastSendDT = -1 ;
	
	private long lastSaveDT = -1 ;

	private int RT_chkSendOrSave(QItem qi) //throws Exception
	{
		StationLocal sl = StationLocal.getInstance() ;
		if(sl==null || !sl.isStationValid())
			return -1;
		
		boolean bconnok = sl.isConnReady();
		
		if(bconnok)
		{
			PSCmdPrjMsg cmd = new PSCmdPrjMsg() ;
			cmd.asStationLocalPayloadPrj(qi.key,this.topic,qi.vp.pkOut(),false,getPrj()) ;
			StringBuilder failedr = new StringBuilder() ; 
			if(sl.RT_sendCmd(cmd, failedr))
			{
				lastSendDT = System.currentTimeMillis() ;
				return 1;
			}
		}
		
		StationLocSaver locsaver = getSaver() ;
		if(locsaver==null)
			return -1;
		
		locsaver.RT_putItemBuffered(qi.key, qi.vp.pkOut(),this.saveMaxNum);
		lastSaveDT = System.currentTimeMillis() ;
		if(log.isTraceEnabled())
		{
			long dt =  IdCreator.extractTimeInMillInSeqId(qi.key) ;
			String dtstr = Convert.toFullYMDHMS(new Date(dt)) ;
			log.trace(" ["+this.getTitle()+"] save local ["+dtstr+"]  "+this.saveName+" loc_num="+locsaver.RT_getSavedBufferedNum()+" qlen="+packQue.size());
		}
		return 0 ;
	}
	
	private void RT_checkHisReSend()
	{
		if(!this.bFailedSave || Convert.isNullOrEmpty(this.saveName))
			return ;
		
		StationLocal sl = StationLocal.getInstance() ;
		if(sl==null || !sl.isStationValid())
			return ;

		//saver
		StationLocSaver locsaver = getSaver() ;
		if(locsaver==null)
			return  ;
		
		long savedn = locsaver.RT_getSavedBufferedNum() ;
		if(savedn<=0)
			return  ;
		
		try
		{
			boolean bconnok = sl.isConnReady();
			if(!bconnok)
			{
				locsaver.RT_flushBuffered(this.saveMaxNum) ;
				return ;
			}
			List<Item> his_items = locsaver.getLastItems(3) ;
			if(his_items.size()>0)
			{
				for(Item item:his_items)
				{
					if(log.isTraceEnabled())
					{
						log.trace(" Station ["+this.getTitle()+"] send key="+item.getKey());
					}
									
					PSCmdPrjMsg cmd = new PSCmdPrjMsg() ;
					cmd.asStationLocalPayloadPrj(item.key,this.topic,item.msg,true,getPrj()) ;
					StringBuilder failedr = new StringBuilder() ; 
					if(!sl.RT_sendCmd(cmd, failedr))
					{
						if(log.isDebugEnabled())
							log.debug("RT_checkHisReSend err"+failedr.toString());
						return ;
					}
				}
				locsaver.deleteBatchByItems(his_items) ;
				
				if(log.isTraceEnabled())
				{
					log.trace(" Station ["+this.getTitle()+"] send his num="+his_items.size()+" local saved left num="+locsaver.RT_getSavedBufferedNum());
				}
			}
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
				log.debug("RT_checkHisReSend "+e.getMessage(),e);
		}
	}
	
	private boolean bRun = false;
	
	private Thread procTh = null ;
	
	private boolean RT_runInLoop() //throws Exception
	{
		QItem qi = deque() ;
		if(qi!=null)
			RT_chkSendOrSave(qi);
		// check on tick
		RT_checkHisReSend() ;
		
		StationLocSaver locsaver = getSaver() ;
			
		if(qi==null && (locsaver==null || locsaver.RT_getSavedBufferedNum()<=0))
			return false;
		return true ;
	}

	private Runnable runner = new Runnable()
	{
		public void run()
		{
			try
			{
				while(bRun)
				{
					UTIL_sleep(10) ;
					if(!RT_runInLoop())
						break ;
				}
			}
			finally
			{
				synchronized(this)
				{
					procTh = null ;
					bRun = false;
				}
			}
		}
	};

	@Override
	public synchronized boolean RT_start(StringBuilder failedr)
	{
		if (bRun)
			return true;

		bRun = true;
		procTh = new Thread(runner);
		procTh.start();
		return true;
	}

	@Override
	public synchronized void RT_stop()
	{
		Thread th = procTh;
		if (th != null)
			th.interrupt();
		bRun = false;
		procTh = null;
	}

	@Override
	public boolean RT_isRunning()
	{
		return bRun;
	}

	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		return false;
	}
	
	/**
	 * false will not support runner
	 * @return
	 */
	public boolean RT_runnerEnabled()
	{
		return true ;
	}
	
	/**
	 * true will not support manual trigger to start
	 * @return
	 */
	public boolean RT_runnerStartInner()
	{
		return false;
	}
	

	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder() ;
		divsb.append("<div class='rt_blk'>") ;
		if(lastSendDT>0)
			divsb.append("&nbsp;Last send ").append(Convert.calcDateGapToNow(lastSendDT));
		if(lastSaveDT>0)
			divsb.append("&nbsp;Last save").append(Convert.calcDateGapToNow(lastSaveDT));
		StationLocSaver locsaver = this.getSaver() ;
		if(locsaver!=null)
			divsb.append(" loc saved num="+locsaver.RT_getSavedBufferedNum()) ;
		divsb.append("</div>") ;
		divblks.add(new DivBlk("station_msg_send",divsb.toString())) ;
		
		super.RT_renderDiv(divblks);
	}
	
}
