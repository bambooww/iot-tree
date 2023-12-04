package org.iottree.core.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.iottree.core.UAPrj;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.cxt.IJsProp;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONArray;
import org.json.JSONObject;

@data_class
public class AlertHandler extends JSObMap //implements IJsProp
{
	
	@data_val
	String id = null ;
	
	@data_val(param_name = "en")
	boolean bEnable = true ;
	
	@data_val(param_name = "n")
	private String name = "" ;
	
	@data_val(param_name = "t")
	private String title = "" ;
	
	@data_val(param_name = "d")
	private String desc="" ;
	
	
	/**
	 * tag paths in this handler
	 */
	
	List<String> alertUids = new ArrayList<>() ;
	
	@data_val(param_name = "alert_uids")
	private String get_AlertUids()
	{
		if(alertUids==null)
			return "" ;
		return Convert.combineStrWith(alertUids, ',') ;
	}
	@data_val(param_name = "alert_uids")
	private void set_AlertUids(String str)
	{
		alertUids = Convert.splitStrWith(str, ",") ;
	}
	
	List<String> alertOutIds = new ArrayList<>() ;
	@data_val(param_name = "out_ids")
	private String get_OutIds()
	{
		if(alertOutIds==null)
			return "" ;
		return Convert.combineStrWith(alertOutIds, ',') ;
	}
	@data_val(param_name = "out_ids")
	private void set_OutIds(String str)
	{
		alertOutIds = Convert.splitStrWith(str, ",") ;
	}
	
	@data_val(param_name = "lvl")
	int alertLevel = 0 ;
	
	@data_val(param_name = "trigger_en")
	boolean bTriggerEn = true ;
	
	@data_val(param_name = "trigger_c")
	String triggerColor = null ;
	
	@data_val(param_name = "release_en")
	boolean bReleaseEn = true ;
	
	@data_val(param_name = "release_c")
	String releaseColor = null ;
	
	transient UAPrj prj = null ;
	
	transient List<AlertOut> alertOuts = null ;
	
	//transient List<ValAlert> alertOuts = null ;
	
	public AlertHandler()
	{
		this.id = CompressUUID.createNewId() ;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return this.name ;
	}

	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public String getTitle()
	{
		return title;
	}

	public String getDesc()
	{
		return desc;
	}
	
	public int getLevel()
	{
		return this.alertLevel ;
	}
	
	public boolean isTriggerEn()
	{
		return this.bTriggerEn ;
	}
	
	public String getTriggerColor()
	{
		if(this.triggerColor==null)
			return "" ;
		
		return this.triggerColor ;
	}
	
	public boolean isReleaseEn()
	{
		return this.bReleaseEn ;
	}
	
	public String getReleaseColor()
	{
		if(this.releaseColor==null)
			return "" ;
		return this.releaseColor ;
	}
	
	public List<String> getAlertUids()
	{
		return this.alertUids ;
	}
	
	public boolean checkAlertUid(String alert_uid)
	{
		if(this.alertUids==null)
			return false;
		return this.alertUids.contains(alert_uid);
	}
	
	public boolean checkValAlertRelated(ValAlert va)
	{
		return this.checkAlertUid(va.getUid()) ;
	}

	public List<String> getAlertOutIds()
	{
		return this.alertOutIds ;
	}
	
	public synchronized List<AlertOut> getAlertOuts()
	{
		if(alertOuts!=null)
			return alertOuts;
		
		AlertManager amgr = AlertManager.getInstance(this.prj.getId()) ;
		ArrayList<AlertOut> aos = new ArrayList<>() ;
		if(this.alertOutIds!=null)
		{
			for(String outid:this.alertOutIds)
			{
				AlertOut ao = amgr.getOutById(outid) ;
				if(ao!=null)
					aos.add(ao) ;
			}
		}
		this.alertOuts = aos ;
		return aos ;
	}
	
	public synchronized void clearCache()
	{
		alertOuts = null ;
	}
	
	public synchronized void setInOutIds(String alert_uids,String out_ids)
	{
		this.alertUids = Convert.splitStrWith(alert_uids, ",") ;
		this.alertOutIds = Convert.splitStrWith(out_ids, ",") ;
		clearCache() ;
	}
	
	public JSONObject toJO() //throws Exception
	{
		JSONObject jo = DataTranserJSON.extractJSONFromObj(this) ;
		List<AlertOut> aos = getAlertOuts() ;
		JSONArray jarr = new JSONArray() ;
		for(AlertOut ao:aos)
		{
			jarr.put(ao.toJO()) ;
		}
		jo.put("outs", jarr) ;
		return jo ;
	}
	
	public JSONObject RT_toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id);
		jo.putOpt("n", this.name) ;
		jo.putOpt("t", this.title) ;
		jo.put("lvl", this.alertLevel);
		jo.put("trigger_en", this.bTriggerEn);
		jo.put("release_en", this.bReleaseEn);
		jo.putOpt("trigger_c", this.triggerColor) ;
		jo.putOpt("release_c", this.releaseColor) ;
		return jo ;
	}
	
	private transient HashMap<String,AlertItem> rt_vaId2ai = new HashMap<>() ;
	
	public List<AlertItem> RT_getAlertItems()
	{
		ArrayList<AlertItem> rets = new ArrayList<>(rt_vaId2ai.size()) ;
		synchronized(this)
		{
			rets.addAll(rt_vaId2ai.values()) ;
		}
		return rets;
	}
	
	synchronized void RT_processSelfSyn(AlertItem ai)
	{
		ValAlert va = ai.getValAlert() ;
		String vaid = va.getId() ;
		if(ai.bReleased)
			rt_vaId2ai.remove(vaid) ;
		else if(ai.bTriggerd)
			rt_vaId2ai.put(vaid, ai) ;
	}
	
	void RT_processOutAsyn(AlertItem ai)
	{
		//ValAlert va = ai.getValAlert() ;
		
		List<AlertOut> aos = getAlertOuts() ;
		if(aos==null||aos.size()<=0)
			return ;
		ai.setHandler(this);
		for(AlertOut ao:aos)
		{
			try
			{
				ao.sendAlert(ai.getUID(),ai);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
//	private JsProp jsP = null;
//
//	@Override
//	public JsProp toJsProp()
//	{
//		if(jsP!=null)
//			return jsP ;
//		jsP = new JsProp("$alert",this,null,true,"AlertItem","Alert item in input env") ;
//		return jsP;
//	}
	
	@Override
	public Object JS_get(String  key)
	{
		Object ob = super.JS_get(key) ;
		if(ob!=null)
			return ob ;
		
		switch(key)
		{
		case "id":
			return this.id ;
		case "name":
			return this.name ;
		case "title":
			return this.title ;
		case "trigger_color":
			return this.triggerColor ;
		case "release_color":
			return this.releaseColor ;
		case "level":
			return this.alertLevel ;
		}
		return null;
	}
	
	@Override
	public List<JsProp> JS_props()
	{
		List<JsProp> ss = super.JS_props() ;
		ss.add(new JsProp("id",null,String.class,false,"Handler ID","")) ;
		ss.add(new JsProp("name",null,String.class,false,"Handler Name","Handler unique name in project")) ;
		ss.add(new JsProp("title",null,String.class,false,"Handler Title","")) ;
		ss.add(new JsProp("trigger_color",null,String.class,false,"Trigger Color","")) ;
		ss.add(new JsProp("release_color",null,String.class,false,"Release Color","")) ;
		ss.add(new JsProp("level",null,Integer.class,false,"Alert Level","")) ;
		return ss ;
	}
}
