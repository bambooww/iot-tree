package org.iottree.core.station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.station.PlatInsWSServer.SessionItem;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.queue.QueTriggerThread;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * station info in platform
 * 
 * @author zzj
 *
 */
public class PStation
{
	private static ILogger log=  LoggerManager.getLogger(PStation.class) ;
	
	String id = null ;
	
	String title = null ;
	
	String key = null ;
	

//	public PStation() //(String id,String title,String key)
//	{
//
//	}
	
	PStation(String id,String title,String key)
	{
		this.id = id ;
		this.title = title ;
		this.key = key ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public String getTitle()
	{
		if(this.title==null)
			return "" ;
		return this.title ;
	}
	
	public String getKey()
	{
		if(this.key==null)
			return "" ;
		
		return key ;
	}
	
	
	private transient ArrayList<UAPrj> relatedPrjs = null ;
	/**
	 * 获取接收端对应项目——项目名称=stationid_prjname
	 * @return
	 */
	public List<UAPrj> getRelatedPrjs()
	{
		if(relatedPrjs!=null)
			return relatedPrjs; 
		
		synchronized(this)
		{
			ArrayList<UAPrj> rets = new ArrayList<>() ;
			//String prefix = this.id+"_" ;
			for(UAPrj prj:UAManager.getInstance().listPrjs())
			{
				PStation ps = prj.getPrjPStationInsDef() ;
				if(ps==null)
					continue ;
				if(this.id.equals(ps.getId()))
					rets.add(prj) ;
	//			String prjn = prj.getName() ;
	//			if(prjn.startsWith(prefix))
	//				rets.add(prj) ;
			}
			
			relatedPrjs = rets ;
			return rets ;
		}
	}
	
	public UAPrj getRelatedPrjByRStationPrjN(String prjname)
	{
		List<UAPrj> rprjs = this.getRelatedPrjs() ;
		if(rprjs.size()<=0)
			return null ;
		for(UAPrj prj:rprjs)
		{
			String rprjn = prj.getPrjRStationPrjName();
			if(Convert.isNullOrEmpty(rprjn))
				rprjn = prj.getName() ;
			if(prjname.equals(rprjn))
				return prj ;
		}
//		String prefix = this.id+"_" ;
//		if(!prjname.startsWith(prefix))
//			prjname = prefix+prjname ;
//		
//		return UAManager.getInstance().getPrjByName(prjname) ;
		return null ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id) ;
		jo.putOpt("t", title) ;
		jo.putOpt("key", this.key) ;
		return jo ;
	}
	
	public static PStation fromJO(JSONObject jo)
	{
		String id = jo.optString("id") ;
		if(Convert.isNullOrEmpty(id))
			return null ;
		PStation ps = new PStation(id,jo.optString("t"),jo.optString("key")) ;
//		ps.id = id ;
//		ps.title = jo.optString("t") ;
//		ps.key = jo.optString("key") ;
		return ps ;
	}
	
	// rt
	
	public static class PrjST
	{
		String prjName = null ;
		
		boolean bRun = false;
		
		boolean bAutoStart ;
		
		private boolean dataSynEn = false;
		
		private long dataSynIntvMs = 10000 ;
		
		boolean bFailedKeep = false;
		
		long keepMaxLen = -1 ;
		
		private long lastRecvedDT = -1 ;
		
		private JSONObject lastRecvedData = null ;
		
		public PrjST(String prjn,boolean b_run,boolean bautostart,
				boolean datasyn_en,long datasyn_intv,boolean failed_keep,long keep_max_len)
		{
			this.prjName = prjn ;
			this.bRun = b_run ;
			this.bAutoStart = bautostart ;
			this.dataSynEn = datasyn_en ;
			this.dataSynIntvMs = datasyn_intv ;
			this.bFailedKeep = failed_keep ;
			this.keepMaxLen = keep_max_len ;
		}
		
		public String getPrjName()
		{
			return this.prjName ;
		}
		
		public boolean isRunning()
		{
			return bRun ;
		}
		
		public boolean isAutoStart()
		{
			return this.bAutoStart ;
		}
		

		public void setDataSynIntv(boolean b_enable,long intv_ms)
		{
			this.dataSynEn = b_enable ;
			this.dataSynIntvMs = intv_ms ;
		}
		
		public boolean isDataSynEnable()
		{
			return this.dataSynEn ;
		}
		
		public long getDataSynIntvMs()
		{
			return this.dataSynIntvMs ;
		}
		
		public boolean isFailedKeep()
		{
			return this.bFailedKeep ;
		}
		
		public long getKeepMaxLen()
		{
			return this.keepMaxLen ;
		}
		
		public long getLastRecvDT()
		{
			return this.lastRecvedDT ;
		}
		
		public JSONObject getLastRecvedData()
		{
			return lastRecvedData;
		}
		
		public void setFailedKeep(boolean b_failed_keep,long keep_max_len)
		{
			this.bFailedKeep = b_failed_keep ;
			this.keepMaxLen = keep_max_len ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("prj_name", this.prjName) ;
			jo.put("run", this.bRun) ;
			jo.put("data_syn_en", this.dataSynEn) ;
			jo.put("data_syn_intv", this.dataSynIntvMs) ;
			jo.put("last_recved_dt", this.lastRecvedDT) ;
			
			jo.put("failed_keep", this.bFailedKeep) ;
			jo.put("keep_max_len", this.keepMaxLen) ;
			return jo ;
		}
	}
	
	private PlatInsWSServer.SessionItem lastSessionItem = null ;
	
	private PlatInsWSServer.SessionItem sessionItem = null ;
	
	private String clientIP = null ;
	//private int clientPort = -1 ; 
	private long clientOpenDT =-1 ;
	
	private List<PrjST> prjSTs = null ;
	
	void RT_updateLocalState(PlatInsWSServer.SessionItem si,List<PrjST> prjsts)
	{
		if(sessionItem!=si)
		{
			lastSessionItem = this.sessionItem ;
			sessionItem = si ;
		}
		prjSTs = prjsts ;
		clientIP = si.getClientIP() ;
		//clientPort = si.
		clientOpenDT = si.openDT ;
	}
	
	
	
	
	public List<PrjST> RT_getPrjSTs()
	{
		return this.prjSTs ;
	}
	
	public PrjST RT_getPrjST(String prjname)
	{
		List<PrjST> prjs = this.prjSTs ;
		if(prjs==null)
			return null ;
		
		for(PrjST prj:prjs)
		{
			if(prj.prjName.equals(prjname))
				return prj ;
		}
		return null ;
	}
	
	public String RT_getClientIP()
	{
		return this.clientIP ;
	}
	
	public long RT_getClientOpenDT()
	{
		return this.clientOpenDT ;
	}
	
	
	private QueTriggerThread.Handler<PSCmdPrjRtData> qttH = new QueTriggerThread.Handler<PSCmdPrjRtData>() {

		public int getQueMultiMum()
		{
			return 50 ;//0 - single
		}
		
		@Override
		public void onQueObj(PSCmdPrjRtData t)
		{
			try
			{
				t.RT_onRecvedInPlatform(null,PStation.this);
				if(log.isTraceEnabled())
				{
					log.trace(" Station ["+id+"] his rt handled que len="+queTT.getQueLen());
				}
			}
			catch(Exception eee)
			{
				eee.printStackTrace();
			}
		}

		@Override
		public void onQueObjs(List<PSCmdPrjRtData> ts)
		{
			try
			{
				PSCmdPrjRtData.onRecvedMultiHisInPlatform(ts,PStation.this);
				if(log.isTraceEnabled())
				{
					log.trace(" Station ["+id+"] his handled multi "+ts.size()+" que len="+queTT.getQueLen());
				}
			}
			catch(Exception eee)
			{
				eee.printStackTrace();
			}
		}};

	private QueTriggerThread<PSCmdPrjRtData> queTT = new QueTriggerThread<>(qttH,10000) ;
	

	void RT_onMsg(SessionItem si,byte[] msg)
	{
		if(log.isTraceEnabled())
		{
			log.trace(" Station ["+this.id+"] RT_onMsg bs len="+msg.length);
		}
		
		PSCmd cmd = PSCmd.parseFrom(msg) ;
		if(cmd==null)
			return ;
		
		if(cmd instanceof PSCmdPrjRtData)
		{
			PSCmdPrjRtData cmdrt = (PSCmdPrjRtData)cmd;
			if(cmdrt.isHis())
			{//历史数据异步处理
				queTT.enqueue(cmdrt);
				if(log.isTraceEnabled())
				{
					log.trace(" Station ["+this.id+"] his rt que len="+queTT.getQueLen());
				}
				return ;
			}
		}
		
		if(log.isTraceEnabled())
		{
			log.trace(" Station ["+this.id+"] RT_onMsg cmd="+cmd);
		}
		
		try
		{
			cmd.RT_onRecvedInPlatform(si,this);
		}
		catch(Throwable eee)
		{
			eee.printStackTrace();
		}
	}
	
	
	
	private SessionItem getSessionItem()
	{
		return sessionItem ;
	}
	
	public boolean RT_startStopPrj(String prjname,boolean bstart) //,Boolean b_autostart)
	{
		SessionItem si = this.getSessionItem() ;
		if(si==null)
			return false;
		
		PSCmdPrjStartStop cmd = new PSCmdPrjStartStop() ;
		cmd.asPrjStartStop(prjname, bstart) ;//,b_autostart) ;
		si.sendCmd(cmd);
		return true ;
	}
	
	public boolean RT_rebootStation(StringBuilder failedr)
	{
		SessionItem si = this.getSessionItem() ;
		if(si==null)
		{
			failedr.append("no session found") ;
			return false;
		}
		
		PSCmdReboot cmd = new PSCmdReboot() ;
		si.sendCmd(cmd);
		return true ;
	}
	
	public boolean RT_writeTag(String prjname,String tagpath,String strv,boolean need_ack,StringBuilder failedr)
	{
		SessionItem si = this.getSessionItem() ;
		if(si==null)
		{
			failedr.append("no session found") ;
			return false;
		}
		
		PSCmdTagW cmd = new PSCmdTagW().asPrjWriteTag(prjname, tagpath, strv,need_ack) ;
		si.sendCmd(cmd);
		return true ;
	}
	
	
	public boolean RT_setSynPM(String prjname,boolean b_autostart,boolean datasyn_en,long datasyn_intv,boolean failed_keep,long keep_max_len)
	{
		SessionItem si = this.getSessionItem() ;
		if(si==null)
		{
			//failedr.append("no session found") ;
			return false;
		}
		
		PSCmdPrjSynPM cmd = new PSCmdPrjSynPM() ;
		cmd.asPrjPM(prjname, b_autostart, datasyn_en, datasyn_intv,failed_keep,keep_max_len) ;
		si.sendCmd(cmd);
		return true ;
	}
	
	public boolean RT_downPrj(String prjname,StringBuilder failedr) throws IOException
	{
		SessionItem si = this.getSessionItem() ;
		if(si==null)
		{
			failedr.append("no session found") ;
			return false;
		}
		
		PrjST prjst = RT_getPrjST(prjname) ;
		if(prjst!=null&&prjst.bRun)
		{
			failedr.append("Station Local Prj ["+prjname+"] is running") ;
			return false;
		}
		
		UAPrj prj = UAManager.getInstance().getPrjByName(prjname) ;
		if(prj==null)
		{
			failedr.append("no Prj ["+prjname+"] found") ;
			return false;
		}
		
		PSCmdPrjUpdate cmd = new PSCmdPrjUpdate() ;
		cmd.asToBePackPrj(prj) ;
		si.sendCmd(cmd);
		return true ;
	}
	
	public boolean triggerUploadPrj(String prjname,StringBuilder failedr)
	{
		SessionItem si = this.getSessionItem() ;
		if(si==null)
		{
			failedr.append("no session found") ;
			return false;
		}
		
		PSCmdPrjUpTrigger cmd = new PSCmdPrjUpTrigger() ;
		cmd.asUpPrjname(prjname) ;
		si.sendCmd(cmd);
		return true ;
	}
	
	public PSCmdDirSyn.DirDiff RT_synDirDiff(String module,String path,long timeout,StringBuilder failedr) throws Exception
	{
		SessionItem si = this.getSessionItem() ;
		if(si==null)
		{
			failedr.append("no session found") ;
			return null;
		}
		
		PSCmdDirSyn cmd = new PSCmdDirSyn() ;
		cmd.asCheckDiff(module, path) ;
		si.sendCmd(cmd);
		
		Object recvob = RT_waitRecvObj(timeout) ;
		if(recvob==null||!(recvob instanceof PSCmdDirSyn.DirDiff))
		{
			failedr.append("no diff return") ;
			return null ;
		}
		
		return (PSCmdDirSyn.DirDiff)recvob ;
	}
	
	private JSONObject RT_synDirSynRecved = null;
	
	private String downloadToken = null ;
	
	private long downloadStartDT = -1 ;
	
	/**
	 * call by station_dl.jsp
	 * @param token
	 * @return
	 */
	public boolean RT_synDirCheckDownloadToken(String token)
	{
		if(this.downloadStartDT<=0)
			return false;
		if(System.currentTimeMillis()-this.downloadStartDT>1800000)
			return false; //time out
		return token.equals(this.downloadToken) ;
	}
	
	
	public boolean RT_synDirSyn(String module,String path,
			ArrayList<String> add_subfs,ArrayList<String> update_subfs,ArrayList<String> del_subfs,
			long timeout,StringBuilder failedr) throws Exception
	{
		SessionItem si = this.getSessionItem() ;
		if(si==null)
		{
			failedr.append("no session found") ;
			return false;
		}
		
		PSCmdDirSyn cmd = new PSCmdDirSyn() ;
		downloadStartDT = System.currentTimeMillis() ;
		downloadToken = UUID.randomUUID().toString() ;
		cmd.asDoSyn(downloadToken,module, path,add_subfs, update_subfs,del_subfs) ;
		si.sendCmd(cmd);
		RT_synDirSynRecved = new JSONObject() ;
		return true ;
	}
	
	void RT_fireRecvedSyn(String op,String subf)
	{
		if(RT_synDirSynRecved==null)
			return ;
		
		JSONArray jarr = RT_synDirSynRecved.optJSONArray(op);
		if(jarr==null)
		{
			jarr = new JSONArray() ;
			RT_synDirSynRecved.put(op,jarr) ;
		}
		jarr.put(subf) ;
	}
	
	public JSONObject RT_getSynDirSynRecved()
	{
		return RT_synDirSynRecved ;
	}
	
	private Object RT_recvObj = null ;
	
	void RT_fireRecvObj(Object obj)
	{
		RT_recvObj = obj ;
	}
	
	public Object RT_waitRecvObj(long timeout)
	{
		RT_recvObj = null ;
		long st = System.currentTimeMillis() ;
		while(System.currentTimeMillis()-st<timeout)
		{
			try
			{
				Thread.sleep(10) ;
			}catch(Exception ee) {}
			
			if(RT_recvObj!=null)
			{
				Object ret = RT_recvObj ;
				RT_recvObj = null;
				return ret ;
			}
		}
		return null ;
	}
	
	void RT_onRecvedRTData(UAPrj prj,String key,JSONObject rt_jo,boolean b_his) throws Exception
	{
		if(b_his)
			return ;
		
		PrjST pst = this.RT_getPrjST(prj.getName()) ;
		if(pst==null)
			return ;
		
		pst.lastRecvedDT = System.currentTimeMillis() ;
		pst.lastRecvedData = rt_jo ;
	}
	
	
	/**
	 * 获得对应站点实时状态
	 * @return
	 */
	public JSONObject RT_toStatusJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("station_id", this.id) ;
		jo.put("client_connok",this.sessionItem!=null&&this.sessionItem.isConnOk()) ;
		if(this.lastSessionItem!=null)
		{
			jo.putOpt("client_last_opendt", lastSessionItem.openDT) ;
			jo.putOpt("client_last_closedt", lastSessionItem.closeDT) ;
		}
		jo.putOpt("client_ip", this.clientIP) ;
		//jo.putOpt("client_port", this.clientIP) ;
		if(this.sessionItem!=null)
		{
			jo.putOpt("client_opendt", this.sessionItem.openDT) ;
			jo.putOpt("client_closedt", this.sessionItem.closeDT) ;
		}
		JSONArray jarr = new JSONArray() ;
		List<PrjST> psts = this.RT_getPrjSTs() ;
		if(psts!=null)
		{
			for(PrjST pst:psts)
			{
				jarr.put(pst.toJO()) ;
			}
		}
		jo.put("prjs_st",jarr) ;
		return jo ;
	}
}
