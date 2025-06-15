package org.iottree.core.alert;

import java.util.List;

import org.iottree.core.UANode;
import org.iottree.core.UATag;
import org.iottree.core.basic.ValEvent;
import org.iottree.core.basic.ValEventTp;
import org.iottree.core.cxt.IJsProp;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsDef;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * 
 * @author jason.zhu
 *
 */
public class AlertItem extends JSObMap implements IJsProp
{
	AlertHandler ah = null ;
	
	UATag tag = null ;
	
	ValEvent vA = null;
	
	boolean bTriggerd = false;
	
	boolean bReleased = false;
	
	long triggerDT = -1 ;
	
	long releaseDT = -1 ;
	
	Object curV = null ;
	
	public AlertItem()
	{}

	public AlertItem(ValEvent va,Object curv)
	{
		this.vA = va ;
		this.tag = va.getBelongTo() ;
		this.triggerDT = va.RT_last_trigger_dt() ;
		if(va.RT_is_triggered())
		{
			this.bTriggerd = true ;
		}
		else
		{
			this.bReleased = true ;
			this.releaseDT = va.RT_last_released_dt() ;
		}
		
		curV = curv ;
	}
	
	void setHandler(AlertHandler ah)
	{
		this.ah = ah ;
	}
	
	public AlertHandler getHandler()
	{
		return this.ah ;
	}
	
	public UATag getTag()
	{
		return this.tag ;
	}
	
	//@JsDef
	public ValEvent getValAlert()
	{
		return this.vA ;
	}
	
	/**
	 * unique id for alert item
	 * @return
	 */
	//@JsDef
	public String getUID()
	{
		return this.vA.RT_get_trigger_uid() ;
	}
	
	public long getTriggerDT()
	{
		return this.triggerDT ;
	}
	
	//@JsDef
	public boolean isTriggered()
	{
		return this.bTriggerd ;
	}
	
	
	//@JsDef
	public boolean isReleased()
	{
		return this.bReleased ;
	}
	
	public long getReleaseDT()
	{
		return this.releaseDT ;
	}
	
	public Object getCurVal()
	{
		return this.curV ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("uid", this.getUID());
		jo.put("triggered", this.bTriggerd);
		jo.put("released", this.bReleased);
		jo.put("t_dt", this.triggerDT) ;
		jo.put("r_dt", this.releaseDT) ;
		jo.putOpt("val", curV) ;
		jo.putOpt("tt", tag.getTitle()) ;
		jo.putOpt("tp", Convert.plainToHtml(vA.getEventTitle())) ;
		jo.putOpt("prompt", this.vA.getEventPrompt()) ;
		return jo ;
	}
	
	private JsProp jsP = null;

	@Override
	public JsProp toJsProp()
	{
		if(jsP!=null)
			return jsP ;
		jsP = new JsProp("$alert",this,null,true,"AlertItem","Alert item in input env") ;
		return jsP;
	}
	
	@Override
	public void constructSubForCxtHelper()
	{
		//make shure  handler and alert_tp propwill show in Context tree;
		this.vA = new ValEvent() ;
		this.vA.setEventTp(ValEventTp.ALL[0]);
		this.ah = new AlertHandler() ;
	}
	
	@Override
	public Object JS_get(String  key)
	{
		Object ob = super.JS_get(key) ;
		if(ob!=null)
			return ob ;
		
		switch(key)
		{
		case "uid":
			return this.getUID() ;
		case "alert_tp":
			return this.vA.getEventTp() ;
		case "alert_title":
			return this.vA.getEventTitle() ;
		case "prompt":
			return this.vA.getEventPrompt() ;
		case "handler":
			return this.ah ;
//		case "level":
//			return this.ah.getLevel() ;
		case "triggered":
			return this.bTriggerd ;
		case "trigger_dt":
			return this.triggerDT ;
//		case "trigger_color":
//			return this.ah.getTriggerColor() ;
		case "released":
			return this.bReleased ;
		case "release_dt":
			return this.releaseDT ;
//		case "release_color":
//			return this.ah.getReleaseColor() ;
		case "str_val":
			if(curV==null)
				return null ;
			return curV.toString() ;
		case "tag_title":
			return this.tag.getTitle() ;
		case "tag_path":
			return this.tag.getNodePath() ;
		
		}
		return null;
	}
	
	public List<JsProp> JS_props()
	{
		List<JsProp> ss = super.JS_props() ;
		ss.add(new JsProp("uid",null,String.class,false,"UID","Alert Item Triggered UID")) ;
		ss.add(new JsProp("alert_tp",null,ValEventTp.class,true,"Alert Type","Alert Type")) ;
		ss.add(new JsProp("alert_title",null,String.class,false,"Alert Title","Alert Type with Parameters")) ;
		ss.add(new JsProp("prompt",null,String.class,false,"Alert Prompt","")) ;
		ss.add(new JsProp("handler",null,AlertHandler.class,true,"Alert Handler","")) ;
		//ss.add(new JsProp("level",null,Integer.class,false,"Alert Level","")) ;
		ss.add(new JsProp("triggered",null,Boolean.class,false,"Is Triggered","Is Triggered Alert Item")) ;
		ss.add(new JsProp("trigger_dt",null,Long.class,false,"Trigger Time MS","Trigger Time in milliseconds,-1 will return when not triggered")) ;
		//ss.add(new JsProp("trigger_color",null,String.class,false,"Trigger Color","")) ;
		ss.add(new JsProp("released",null,Boolean.class,false,"Is Released","Is Released Alert Item")) ;
		ss.add(new JsProp("release_dt",null,Long.class,false,"Release Time MS","Release Time in milliseconds,-1 will return when not triggered")) ;
		//ss.add(new JsProp("release_color",null,String.class,false,"Release Color","")) ;
		ss.add(new JsProp("str_val",null,String.class,false,"Val","Value as string")) ;
		ss.add(new JsProp("tag_title",null,String.class,false,"Tag Title","")) ;
		ss.add(new JsProp("tag_path",null,String.class,false,"Tag Path","")) ;

		return ss ;
	}
}
