package org.iottree.core.alert;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.queue.HandleResult;
import org.iottree.core.util.queue.IObjHandler;
import org.iottree.core.util.queue.QueueThread;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONArray;
import org.json.JSONObject;

public class AlertManager
{
	private static HashMap<String,AlertManager> prjid2mgr = new HashMap<>() ;
	
	@SuppressWarnings("unused")
	public static AlertManager getInstance(String prjid)
	{
		AlertManager instance = prjid2mgr.get(prjid) ;
		if(instance!=null)
			return instance ;
		
		synchronized(AlertManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new AlertManager(prjid) ;
			prjid2mgr.put(prjid, instance) ;
			return instance ;
		}
	}
	
	//String prjId = null ;
	UAPrj prj = null ;
	
	File prjDir = null ;
	
	private LinkedHashMap<String,AlertHandler> alertHandlers = null ;
	
	private LinkedHashMap<String,AlertOut> alertOuts = null ;
	
	private AlertManager(String prjid)
	{
		this.prj = UAManager.getInstance().getPrjById(prjid) ;
		if(this.prj==null)
			throw new IllegalArgumentException("no prj found with id="+prjid) ;
		this.prjDir = prj.getPrjSubDir();
		
		try
		{
			alertHandlers =  loadHandlers();
			alertOuts = loadOuts() ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			throw new RuntimeException(ee.getMessage()) ;
		}
	}
	
	public UAPrj getPrj()
	{
		return prj ;
	}
	
	public LinkedHashMap<String,AlertHandler> getHandlers()
	{
		return alertHandlers ;
	}
	

	public AlertHandler getHandlerById(String id)
	{
		return this.alertHandlers.get(id) ;
	}
	
	public void setHandler(AlertHandler ah) throws Exception
	{
		ah.prj = this.prj ;
		this.alertHandlers.put(ah.getId(), ah) ;
		this.saveHandlers();
	}
	
	public void setHandlerByJSON(JSONObject jo) throws Exception
	{
		//String tp = jo.getString("_tp");
		AlertHandler ah = new AlertHandler();
		DataTranserJSON.injectJSONToObj(ah, jo) ;
		if(Convert.isNullOrEmpty(ah.id))
			ah.id = CompressUUID.createNewId() ;
		this.setHandler(ah);
	}
	
	public void setHandlerInOutIds(JSONArray jarr) throws Exception
	{
		int len = jarr.length() ;
		boolean bdirty=false;
		for(int i = 0 ; i < len ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			String id = jo.getString("id") ;
			String alert_uids = jo.getString("alert_uids") ;
			String out_ids = jo.getString("out_ids") ;
			AlertHandler ah = this.getHandlerById(id) ;
			if(ah==null)
				continue ;
			ah.setInOutIds(alert_uids, out_ids);
			bdirty=true;
		}
		
		if(bdirty)
			this.saveHandlers();
	}
	
	public boolean delHandlerById(String id) throws Exception
	{	
		AlertHandler ao = this.alertHandlers.remove(id) ;
		if(ao==null)
			return false;
		this.saveHandlers();
		return true ;
	}
	
	
	private LinkedHashMap<String, AlertHandler> loadHandlers() throws Exception
	{
		LinkedHashMap<String, AlertHandler> n2st = new LinkedHashMap<>();
		
		File f = new File(prjDir, "alert_handlers.xml");
		if (!f.exists())
			return n2st;

		XmlData xd = XmlData.readFromFile(f);
		List<XmlData> xds = xd.getSubDataArray("handlers");
		if (xds == null)
			return n2st;
		for (XmlData tmpxd : xds)
		{
			AlertHandler o = new AlertHandler();
			if (!DataTranserXml.injectXmDataToObj(o, tmpxd))
				continue;
			o.prj = this.prj ;
			n2st.put(o.getId(), o);
		}
		return n2st;
	}
	
	public void saveHandlers() throws Exception
	{
		XmlData xd = new XmlData();
		List<XmlData> xds = xd.getOrCreateSubDataArray("handlers");
		for (AlertHandler st : getHandlers().values())
		{
			XmlData xd0 = DataTranserXml.extractXmlDataFromObj(st);
			//xd0.setParamValue("_alert_cn_", st.getClass().getCanonicalName());
			xds.add(xd0);
		}
		File f = new File(prjDir, "alert_handlers.xml");
		XmlData.writeToFile(xd, f);
	}
	
	public LinkedHashMap<String,AlertOut> getOuts()
	{
		return this.alertOuts ;
	}
	
	public AlertOut getOutById(String id)
	{
		return this.alertOuts.get(id) ;
	}
	
	
	private void clearCache()
	{
		for(AlertHandler ah:this.alertHandlers.values())
			ah.clearCache();
	}
	
	public void setOut(AlertOut ao) throws Exception
	{
		ao.prj = this.prj ;
		this.alertOuts.put(ao.getId(), ao) ;
		this.saveOuts();
		
		//clear
		clearCache();
	}
	
	
	
	public void setOutByJSON(JSONObject jo) throws Exception
	{
		String tp = jo.getString("_tp");
		AlertOut ao = AlertOut.newInsByTp(tp) ;
		if(ao==null)
			throw new Exception("unknown tp="+tp) ;
		DataTranserJSON.injectJSONToObj(ao, jo) ;
		if(Convert.isNullOrEmpty(ao.id))
			ao.id = CompressUUID.createNewId() ;
		this.setOut(ao);
	}
	
	private LinkedHashMap<String,AlertOut> loadOuts() throws Exception
	{
		LinkedHashMap<String,AlertOut> outs = new LinkedHashMap<>();
		
		File f = new File(prjDir, "alert_outs.xml");
		if (!f.exists())
			return outs;

		XmlData xd = XmlData.readFromFile(f);
		List<XmlData> xds = xd.getSubDataArray("outs");
		if (xds == null)
			return outs;
		for (XmlData tmpxd : xds)
		{
			String tp = tmpxd.getParamValueStr("_tp") ;
			AlertOut ao = AlertOut.newInsByTp(tp) ;
			if(ao==null)
				continue ;
			
			if (!DataTranserXml.injectXmDataToObj(ao, tmpxd))
				continue;
			ao.prj = this.prj ;
			outs.put(ao.getId(),ao) ;
		}
		return outs;
	}
	
	
	
	public boolean delOutById(String id) throws Exception
	{	
		AlertOut ao = this.alertOuts.remove(id) ;
		if(ao==null)
			return false;
		this.saveOuts();
		return true ;
	}
	
	public void saveOuts() throws Exception
	{
		XmlData xd = new XmlData();
		List<XmlData> xds = xd.getOrCreateSubDataArray("outs");
		if(this.alertOuts!=null)
		{
			for (AlertOut st : alertOuts.values())
			{
				XmlData xd0 = DataTranserXml.extractXmlDataFromObj(st);
				xd0.setParamValue("_tp", st.getOutTp());
				xds.add(xd0);
			}
		}
		
		File f = new File(prjDir, "alert_outs.xml");
		XmlData.writeToFile(xd, f);
	}
	
	IObjHandler<AlertItem> queH = new IObjHandler<AlertItem>() {

		@Override
		public int processFailedRetryTimes()
		{
			return 0;
		}

		@Override
		public long processRetryDelay(int retrytime)
		{
			return 0;
		}

		@Override
		public HandleResult processObj(AlertItem o, int retrytime) throws Exception
		{
			RT_handleAlertItem(o) ;
			return HandleResult.Succ;
		}

		@Override
		public long handlerInvalidWait()
		{
			return 0;
		}

		@Override
		public void processObjDiscard(AlertItem o) throws Exception
		{
			
		}
		
	} ;
	
	private QueueThread<AlertItem> queTh = new QueueThread<>(queH) ;
	
	public void RT_start()
	{
		queTh.start();
	}
	
	public void RT_stop()
	{
		queTh.stop();
	}
	
	private void RT_handleAlertItem(AlertItem ai)
	{
		if(this.alertHandlers==null)
			return ;
		this.alertHandlers.forEach((id,ah)->{
			if(!ah.isEnable())
				return ;
			ah.RT_processOutAsyn(ai) ;
		});
	}
	
	public void RT_fireAlert(ValAlert va,Object cur_val)
	{
		
		if(!queTh.isRunning())
			return ; //discard
		AlertItem ai = new AlertItem(va,cur_val) ;
		
		if(this.alertHandlers!=null)
		{
			this.alertHandlers.forEach((id,ah)->{
				if(!ah.isEnable())
					return ;
				if(!ah.checkValAlertRelated(va))
					return ;
				
				ah.RT_processSelfSyn(ai) ;
			});
		}
		//process out
		queTh.enqueue(ai) ;
	}
	
//	public void RT_fireReleased(ValAlert va,Object cur_val)
//	{
//		
//	}
}
