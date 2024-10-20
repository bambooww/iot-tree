package org.iottree.core.basic;

import java.util.List;

import org.iottree.core.UANode;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.alert.AlertDef;
import org.iottree.core.alert.AlertManager;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsDef;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

/**
 * for alert config to other objs like tag
 * 
 * Value Event or Alert
 * 
 * @author jason.zhu
 */
@data_class
public class ValAlert extends JSObMap
{
	public static ValAlert parseValAlert(UATag tag,String str) throws Exception
	{
		if(Convert.isNullOrEmpty(str))
			return null ;
		
		JSONObject jo = new JSONObject(str) ;
		return parseValAlert(tag,jo) ;
	}
	
	public static ValAlert parseValAlert(UATag tag,JSONObject jo) throws Exception
	{
		if(jo==null)
			return null ;
		
		ValAlert vt = new ValAlert() ;
		//vt.tag = tag ;
		vt.setBelongTo(tag) ;
		DataTranserJSON.injectJSONToObj(vt, jo) ;
		String id = jo.optString("id") ;
		if(Convert.isNullOrEmpty(id))
			vt.id = CompressUUID.createNewId();
		return vt ;
	}
	
	private UAPrj prj = null ;
	
	private UATag tag = null ;
	
	@data_val
	private String id = null ;
	
	@data_val
	private String name = null ;
	
	
	ValAlertTp alertTp = null;//ValAlertTp.on_off;
	
	//private boolean bAlertInit=false;
	
	
	@data_val(param_name = "tp")
	private int get_TP()
	{
		if(alertTp==null)
			return 0 ;
		
		return alertTp.getTpVal() ;
	}
	@data_val(param_name = "tp")
	private void set_TP(int v)
	{
		alertTp = ValAlertTp.createTp(this,v);
	}
	
	@data_val(param_name="en")
	boolean alertEnable = true ;
	
//	/**
//	 * 0 - 255
//	 */
//	@data_val(param_name = "group")
//	int alertGroup = 0 ;
//	
//	/**
//	 * 0-100
//	 */
//	@data_val(param_name = "lvl")
//	int alertLvl = 0 ;
	
	/**
	 * 基准/指定值/指定位
	 */
	@data_val(param_name = "param1")
	String paramStr1 = null ;
	
	/**
	 * 触发误差/指定值
	 */
	@data_val(param_name = "param2")
	String paramStr2 = null ;
	
	/**
	 * 解除误差
	 */
	@data_val(param_name = "param3")
	String paramStr3 = null ;
	
	@data_val(param_name = "prompt")
	String alertPrompt = null ;
	
	/**
	 * 1-5
	 */
	@data_val(param_name = "lvl")
	private int alertLvl = 5 ;
	
	private transient boolean bTrigged = false;
	
	/**
	 * every time this alert is trigger,new UID
	 * will created
	 */
	private transient String triggerUID = null ;
	
	private transient long lastTriggedDT = -1 ;
	
	private transient Object lastTriggedVal = null ;
	
	private transient long lastReleasedDT = -1 ;
	
	
	public ValAlert()
	{
		//this.tag = tag ;
		this.id = CompressUUID.createNewId();
	}
	
	public ValAlert copyMe(UATag tag,boolean b_cp_id)
	{
		ValAlert r = new ValAlert() ;
		r.setBelongTo(tag);
		
		if(b_cp_id)
			r.id = this.id ;
		r.name = this.name ;
		r.alertTp = this.alertTp ;
//		r.alertGroup = this.alertGroup ;
//		r.alertLvl = this.alertLvl ;
		r.paramStr1 = this.paramStr1 ;
		r.paramStr2 = this.paramStr2 ;
		r.paramStr3 = this.paramStr3 ;
		r.alertPrompt = this.alertPrompt ;
		return r ;
	}
	
	public UATag getBelongTo()
	{
		return this.tag ;
	}
	
	public UAPrj getPrj()
	{
		if(this.prj!=null)
			return this.prj ;
		
		this.prj = this.tag.getBelongToPrj() ;
		return this.prj ;
	}
	
	public void setBelongTo(UATag t)
	{
		this.tag = t ;
		this.prj = t.getBelongToPrj() ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
	@JsDef
	public String getUid()
	{
		UAPrj prj = this.getPrj() ;
		if(prj==null)
			throw new RuntimeException("tag is not belong to project") ;
		
		return this.tag.getNodeCxtPathIn(prj)+"-"+this.id ;
	}
	

	public static ValAlert getAlertByUID(UAPrj prj,String alert_uid)
	{
		int k = alert_uid.indexOf('-') ;
		if(k<=0)
			throw new IllegalArgumentException("invalid alert UID "+alert_uid) ;
		String tagpath = alert_uid.substring(0,k) ;
		String va_id = alert_uid.substring(k+1) ;
		UANode uan = prj.getDescendantNodeByPath(tagpath) ;
		if(!(uan instanceof UATag))
			return null ;
		UATag tag = (UATag)uan ;
		return tag.getValAlertById(va_id) ;
	}
	
	
	public String getName()
	{
		return this.name ;
	}

	public ValAlertTp getAlertTp()
	{
		return alertTp;
	}
	
	public String getAlertTitle()
	{
		return alertTp.calValAlertTitle(this) ;
	}

	public void setAlertTp(ValAlertTp tp)
	{
		this.alertTp= tp ;
	}
	
	@JsDef
	public String getTpName()
	{
		return alertTp.getName() ;
	}
	
	@JsDef
	public String getTpTitle()
	{
		return alertTp.getTitle() ;
	}
	
	@JsDef
	public boolean isEnable()
	{
		return this.alertEnable ;
	}

	public String getParamStr1()
	{
		return paramStr1;
	}

	public void setParamStr1(String paramStr1)
	{
		this.paramStr1 = paramStr1;
	}

	public String getParamStr2()
	{
		return paramStr2;
	}

	public void setParamStr2(String paramStr2)
	{
		this.paramStr2 = paramStr2;
	}

	public String getParamStr3()
	{
		return paramStr3;
	}

	public void setParamStr3(String paramStr3)
	{
		this.paramStr3 = paramStr3;
	}

	@JsDef
	public String getAlertPrompt()
	{
		return alertPrompt;
	}

	public void setAlertPrompt(String s)
	{
		this.alertPrompt = s;
	}
	
	public int getAlertLvl()
	{
		return this.alertLvl ;
	}
	
	public String toTitleStr()
	{
		return this.getAlertTitle()+"("+this.alertPrompt+")" ;
	}
	
	public JSONObject toJO() throws Exception
	{
		JSONObject jo = DataTranserJSON.extractJSONFromObj(this) ;
		String tpt = this.getAlertTp().getTitle() ;
		jo.put("tpt", tpt) ;
		return jo ;
	}
	
	public List<JsProp> JS_props()
	{
		List<JsProp> ps = super.JS_props() ;
		
		return ps ;
	}
	
	public Object JS_get(String  key)
	{
		return null ;
	}
	
	
	
	private void RT_trigger(Object cur_val)
	{
		this.bTrigged = true ;
		this.triggerUID = IdCreator.newSeqId();//CompressUUID.createNewId() ;
		this.lastTriggedDT = System.currentTimeMillis() ;
		this.lastTriggedVal = cur_val ;
		
		//check handler
		UAPrj prj = this.getPrj() ;
		AlertManager.getInstance(prj.getId()).RT_fireAlert(this,cur_val) ;
		MNManager.getInstance(prj).RT_TAG_triggerEvt(this,cur_val);
	}
	
	private void RT_release(Object cur_val)
	{
		 this.bTrigged =false;
		 this.lastReleasedDT = System.currentTimeMillis() ;
		 //
		 AlertManager.getInstance(getPrj().getId()).RT_fireAlert(this,cur_val) ;
		 MNManager.getInstance(prj).RT_TAG_releaseEvt(this,cur_val);
	}
	
	private transient Number lastV = null ;
	
	public void RT_fireValChged(Object inputv)
	{
		if(!this.alertEnable)
			return ;
		Number curv = null ;
		if(inputv instanceof Number)
		{
			curv = (Number)inputv ;
		}
		else if(inputv instanceof Boolean)
		{
			curv = ((Boolean)inputv).booleanValue()?1:0;
		}
		else
		{
			return ;//do nothing
		}
		
		if(alertTp.isNeedLastVal())
		{
			if(lastV==null)
			{
				lastV = curv ;
				return ;
			}
		}
		
		try
		{
			if(this.bTrigged)
			{
				if(alertTp.checkRelease(lastV, curv))
					RT_release(inputv);
			}
			else
			{
				if(alertTp.checkTrigger(lastV, curv))
					RT_trigger(inputv) ;
			}
		}
		finally
		{
			lastV = curv ;
		}
	}

	@JsDef
	public boolean RT_is_triggered()
	{
		return this.bTrigged ;
	}
	
	public String RT_get_trigger_uid()
	{
		return this.triggerUID;
	}
	
	@JsDef
	public long RT_last_trigger_dt()
	{
		return this.lastTriggedDT ;
	}
	
	@JsDef
	public Object RT_last_trigger_val()
	{
		return this.lastTriggedVal ;
	}
	
	@JsDef
	public long RT_last_released_dt()
	{
		return this.lastReleasedDT ;
	}
	
	public JSONObject RT_get_triggered_jo()
	{
		if(!this.bTrigged)
			return null ;
		
		JSONObject jo = new JSONObject() ;
		String evtid = RT_get_trigger_uid() ;
		UATag tag = getBelongTo() ;
		jo.put("evt_id", evtid) ;
		jo.put("tag_id", tag.getId()) ;
		jo.put("tag_path", tag.getNodeCxtPathInPrj()) ;
		jo.put("tag_tpath", tag.getNodeCxtPathTitleIn(this.prj)) ;
		jo.put("tag_t", tag.getTitle()) ;
		jo.putOpt("name", this.name) ;
		jo.put("lvl", this.alertLvl) ;
		jo.put("trigger_v", ""+this.RT_last_trigger_val()) ;
		jo.put("trigger_dt", this.RT_last_trigger_dt()) ;
		jo.put("evt_tp", this.getTpName()) ;
		jo.put("evt_tpt", this.getTpTitle()) ;
		jo.putOpt("evt_prompt", this.getAlertPrompt()) ;
		boolean btri =  this.RT_is_triggered();
		jo.put("triggered",btri) ;
		return jo ;
	}
	
	public JSONObject RT_get_release_jo()
	{
		if(this.bTrigged)
			return null ;
		
		JSONObject jo = new JSONObject() ;
		String evtid = RT_get_trigger_uid() ;
		UATag tag = getBelongTo() ;
		jo.put("evt_id", evtid) ;
		jo.put("tag_id", tag.getId()) ;
		jo.put("tag_path", tag.getNodeCxtPathInPrj()) ;
		jo.put("trigger_dt", this.RT_last_trigger_dt()) ;
		jo.put("release_dt", this.RT_last_released_dt()) ;
		jo.put("evt_tp", this.getTpName()) ;
		jo.put("evt_tpt", this.getTpTitle()) ;
		jo.putOpt("evt_prompt", this.getAlertPrompt()) ;
		boolean btri =  this.RT_is_triggered();
		jo.put("released", !btri) ;
		return jo ;
	}
}
