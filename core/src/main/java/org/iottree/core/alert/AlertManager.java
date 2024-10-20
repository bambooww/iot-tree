package org.iottree.core.alert;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.store.SourceJDBC;
import org.iottree.core.store.StoreManager;
import org.iottree.core.store.gdb.DataTable;
import org.iottree.core.store.gdb.autofit.JavaColumnInfo;
import org.iottree.core.store.gdb.autofit.JavaForeignKeyInfo;
import org.iottree.core.store.gdb.autofit.JavaTableInfo;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.queue.HandleResult;
import org.iottree.core.util.queue.IObjHandler;
import org.iottree.core.util.queue.QueueThread;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.XmlVal;
import org.json.JSONArray;
import org.json.JSONObject;

public class AlertManager  extends JSObMap
{
	private static HashMap<String,AlertManager> prjid2mgr = new HashMap<>() ;
	
	
	public static AlertManager getInstance(String prjid)
	{
		AlertManager instance = prjid2mgr.get(prjid) ;
		if(instance!=null)
			return instance ;
		
		synchronized(AlertManager.class)
		{
			instance = prjid2mgr.get(prjid) ;
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
	
	AlertDef alertDef = null ;
	
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
			alertDef = loadAlertDef() ;
			
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
	
	
	// alert def
	
	private AlertDef loadAlertDef() throws Exception
	{
		AlertDef ret = new AlertDef() ;
		File f = new File(prjDir, "alert_def.xml");
		if (!f.exists())
			return ret ;

		JSONObject jo = Convert.readFileJO(f) ;
		if(jo==null)
			return ret ;
		ret.LVL_fromJO(jo) ;
		return ret;
	}
	
	private void saveAlertDef() throws Exception
	{
		JSONObject jo = this.alertDef.LVL_toJO() ;
		File f = new File(prjDir, "alert_def.xml");
		Convert.writeFileJO(f, jo);
	}
	
	public AlertDef getAlertDef()
	{
		return this.alertDef ;
	}
	
	public AlertDef setAlertDefByJO(JSONObject jo) throws Exception
	{
		this.alertDef.LVL_fromJO(jo) ;
		this.saveAlertDef();
		return this.alertDef ;
	}
	
	// ---------------------  handler -------------- replaced by msg net
	@Deprecated
	public LinkedHashMap<String,AlertHandler> getHandlers()
	{
		return alertHandlers ;
	}
	
//	private List<AlertHandler> list_handlers()
//	{
//		ArrayList<AlertHandler>
//		alertHandlers.values()
//	}

	public AlertHandler getHandlerById(String id)
	{
		return this.alertHandlers.get(id) ;
	}
	
	public AlertHandler getHandlerByName(String name)
	{
		for(AlertHandler ah:this.alertHandlers.values())
		{
			if(name.equals(ah.getName()))
				return ah ;
		}
		return null ;
	}
	
	public void setHandler(AlertHandler ah) throws Exception
	{
		String n = ah.getName() ;
		if(Convert.isNullOrEmpty(n))
			throw new IllegalArgumentException("Alert Handler name cannot be null or empty") ;
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(n, true, sb))
			throw new IllegalArgumentException(sb.toString()) ;
		
		AlertHandler old_ah = this.getHandlerByName(n) ;
		if(old_ah!=null)
		{
			if(!old_ah.getId().equals(ah.getId()))
				throw new IllegalArgumentException("Alert Handler with name="+n+" is already existed!") ;
		}
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

		public void initHandler()
		{
			RT_init() ;
		}
		
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
	
	private void RT_init()
	{
		
		if(this.alertHandlers!=null)
		{
			this.alertHandlers.forEach((id,ah)->{
				if(!ah.isEnable())
					return ;
				ah.RT_initHandler() ;
			});
		}
	}
	
	private void RT_handleAlertItem(AlertItem ai)
	{
		if(this.alertHandlers==null)
			return ;
		ValAlert va = ai.getValAlert() ;
		this.alertHandlers.forEach((id,ah)->{
			if(!ah.isEnable())
				return ;
			if(!ah.checkValAlertRelated(va))
				return ;
			//ai.setHandler(ah);
			ah.RT_processRecordAsyn(ai);
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
				//ai.setHandler(ah);
				ah.RT_processSelfSyn(ai) ;
			});
		}
		//process out
		queTh.enqueue(ai) ;
	}
	
	private JavaTableInfo tableInfo = null ;
	
	public JavaTableInfo getAlertsTableInfo()
	{
		if(tableInfo!=null)
			return tableInfo;
		
		ArrayList<JavaColumnInfo> norcols = new ArrayList<JavaColumnInfo>();
		JavaColumnInfo pkcol = null;
		ArrayList<JavaForeignKeyInfo> fks = new ArrayList<JavaForeignKeyInfo>();

		pkcol = new JavaColumnInfo("AutoId",true, XmlVal.XmlValType.vt_string, 30,
				false, false,"", false,-1,"",false,false);
		
		
		
		norcols.add(new JavaColumnInfo("TriggerDT",false, XmlVal.XmlValType.vt_date, -1,
				true, false,"TriggerDT_idx", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("ReleaseDT",false, XmlVal.XmlValType.vt_date, -1,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("Handler",false, XmlVal.XmlValType.vt_string, 40,
				false, false,"", false,-1, "",false,false));
//		norcols.add(new JavaColumnInfo(this.getColValid(),false, XmlVal.XmlValType.vt_int16, 2,
//				false, false,"", false,-1, "",false,false));
//		
		int tag_maxlen = 20 ;
		for(UATag tag:this.prj.listTagsAll())
		{
			String np = tag.getNodePath() ;
			int len = np.length() ;
			if(len>tag_maxlen)
				tag_maxlen = len ;
		}
		norcols.add(new JavaColumnInfo("Tag",false, XmlVal.XmlValType.vt_string, tag_maxlen,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("Type",false, XmlVal.XmlValType.vt_string, 20,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("Value",false, XmlVal.XmlValType.vt_string, 20,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("Level",false, XmlVal.XmlValType.vt_int16, 2,
				false, false,"", false,-1, "",false,false));
		
		norcols.add(new JavaColumnInfo("Prompt",false, XmlVal.XmlValType.vt_string, 200,
				false, false,"", false,-1, "",false,false));
//		norcols.add(new JavaColumnInfo(this.getColAlertInf(),false, XmlVal.XmlValType.vt_string, MAX_ALERT_INF_LEN,
//				false, false,"", false,-1, "",false,false));

		String tablename = calTableName() ;
		tableInfo = new JavaTableInfo(tablename, pkcol, norcols, fks);
		return tableInfo;
	}
	
	private String calTableName()
	{
		return "alerts_"+prj.getName() ;
	}
	
//	void RT_initHISRecord()
//	{
//		
//	}
	
	
	public List<String> HIS_getRecordOuterSorNames()
	{
		ArrayList<String> rets = new ArrayList<>() ;
		if(this.alertHandlers!=null)
		{
			this.alertHandlers.forEach((id,ah)->{
				if(!ah.isEnable())
					return ;
				if(!ah.isOuterRecord())
					return ;
				String sorn = ah.getOuterRecordSor() ;
				if(Convert.isNotNullEmpty(sorn))
					rets.add(sorn) ;
			});
		}
		return rets ;
	}
	/**
	 * 
	 * @param sor_name null or empty means inner
	 * @param start_dt
	 * @param end_dt
	 * @param handler_name
	 * @param pageidx
	 * @param pagesize
	 * @return
	 * @throws Exception
	 */
	public DataTable HIS_selectRecord(String sor_name,Date start_dt,Date end_dt,String handler_name,int pageidx,int pagesize)
		throws Exception
	{
		SourceJDBC sor = null;
		if(Convert.isNotNullEmpty(sor_name))
			sor = (SourceJDBC)StoreManager.getSourceByName(sor_name) ;
		else
			sor = StoreManager.getInnerSource(prj.getName()) ;
		if(sor==null)
			throw new IllegalArgumentException("no SourceJDBC found with name="+sor_name) ;
		
		String tablename = this.calTableName() ;
		return AlertHandler.selectRecords(sor.getConnPool(),tablename,start_dt,end_dt,handler_name,pageidx,pagesize) ;
	}
	
	
	
	
	
//	public void RT_fireReleased(ValAlert va,Object cur_val)
//	{
//		
//	}
}
