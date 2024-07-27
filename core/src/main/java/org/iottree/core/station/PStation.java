package org.iottree.core.station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.iottree.core.IOCBox;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UAPrj;
import org.iottree.core.station.PlatformWSServer.SessionItem;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
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
	

	public PStation()
	{}
	
	public String getId()
	{
		return id ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public String getKey()
	{
		return key ;
	}
	
	/**
	 * 获取接收端对应项目——项目名称=stationid_prjname
	 * @return
	 */
	public List<UAPrj> getRelatedPrjs()
	{
		ArrayList<UAPrj> rets = new ArrayList<>() ;
		String prefix = this.id+"_" ;
		for(UAPrj prj:UAManager.getInstance().listPrjs())
		{
			String prjn = prj.getName() ;
			if(prjn.startsWith(prefix))
				rets.add(prj) ;
		}
		return rets ;
	}
	
	public UAPrj getRelatedPrj(String prjname)
	{
		String prefix = this.id+"_" ;
		if(!prjname.startsWith(prefix))
			prjname = prefix+prjname ;
		
		return UAManager.getInstance().getPrjByName(prjname) ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id) ;
		jo.putOpt("t", title) ;
		jo.putOpt("key", this.key) ;
		//jo.putOpt("data_syn_en",this.dataSynEn) ;
		//jo.putOpt("data_syn_intv",this.dataSynIntvMs) ;
		
		return jo ;
	}
	
	public static PStation fromJO(JSONObject jo)
	{
		String id = jo.optString("id") ;
		if(Convert.isNullOrEmpty(id))
			return null ;
		PStation ps = new PStation() ;
		ps.id = id ;
		ps.title = jo.optString("t") ;
		ps.key = jo.optString("key") ;
		//ps.dataSynEn = jo.optBoolean("data_syn_en",false) ;
		//ps.dataSynIntvMs = jo.optLong("data_syn_intv", 10000) ;
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
		
		
		public PrjST(String prjn,boolean b_run,boolean bautostart,
				boolean datasyn_en,long datasyn_intv)
		{
			this.prjName = prjn ;
			this.bRun = b_run ;
			this.bAutoStart = bautostart ;
			this.dataSynEn = datasyn_en ;
			this.dataSynIntvMs = datasyn_intv ;
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
		
	}
	
	private PlatformWSServer.SessionItem sessionItem = null ;
	private String clientIP = null ;
	private long clientOpenDT =-1 ;
	
	private List<PrjST> prjSTs = null ;
	
	void RT_updateLocalState(PlatformWSServer.SessionItem si,List<PrjST> prjsts)
	{
		sessionItem = si ;
		prjSTs = prjsts ;
		clientIP = si.getClientIP() ;
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

	void RT_onMsg(SessionItem si,byte[] msg)
	{
		if(log.isTraceEnabled())
		{
			log.trace(" Station ["+this.id+"] RT_onMsg bs len="+msg.length);
		}
		
		PSCmd cmd = PSCmd.parseFrom(msg) ;
		if(cmd==null)
			return ;
		
		if(log.isTraceEnabled())
		{
			log.trace(" Station ["+this.id+"] RT_onMsg cmd="+cmd);
		}
		
		try
		{
			cmd.RT_onRecvedInPlatform(si,this);
		}
		catch(Exception eee)
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
	
	
	public boolean RT_setSynPM(String prjname,boolean b_autostart,boolean datasyn_en,long datasyn_intv)
	{
		SessionItem si = this.getSessionItem() ;
		if(si==null)
		{
			//failedr.append("no session found") ;
			return false;
		}
		
		PSCmdPrjSynPM cmd = new PSCmdPrjSynPM() ;
		cmd.asPrjPM(prjname, b_autostart, datasyn_en, datasyn_intv) ;
		si.sendCmd(cmd);
		return true ;
	}
	
	public boolean RT_updatePrj(String prjname,StringBuilder failedr) throws IOException
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
}
