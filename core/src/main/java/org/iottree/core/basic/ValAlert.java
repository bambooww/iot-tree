package org.iottree.core.basic;

import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsDef;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

/**
 * for alert config to other objs like tag
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
		vt.tag = tag ;
		DataTranserJSON.injectJSONToObj(vt, jo) ;
		String id = jo.optString("id") ;
		if(Convert.isNullOrEmpty(id))
			vt.id = CompressUUID.createNewId();
		return vt ;
	}
	
	UATag tag = null ;
	
	@data_val
	private String id = null ;
	
	@data_val
	private String name = null ;
	
	ValAlertTp alertTp = null;//ValAlertTp.on_off;
	
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
	
	
	private transient boolean bTrigged = false;
	
	private transient long lastTriggedDT = -1 ;
	
	private transient Number lastTriggedVal = null ;
	
	private transient long lastReleasedDT = -1 ;
	
	
	public ValAlert()
	{
		//this.tag = tag ;
		this.id = CompressUUID.createNewId();
	}
	
	public ValAlert copyMe(UATag tag,boolean b_cp_id)
	{
		ValAlert r = new ValAlert() ;
		r.tag = tag ;
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
	
	public void setBelongTo(UATag t)
	{
		this.tag = t ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
	@JsDef
	public String getUid()
	{
		return this.tag.getNodePath()+"-"+this.id ;
	}
	
	public String getName()
	{
		return this.name ;
	}

	public ValAlertTp getAlertTp()
	{
		return alertTp;
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
		return alertTp.getTitleEn() ;
	}
	
	@JsDef
	public boolean isEnable()
	{
		return this.alertEnable ;
	}

//	public int getAlertGroup()
//	{
//		return alertGroup;
//	}
//
//	public void setAlertGroup(int alertGroup)
//	{
//		this.alertGroup = alertGroup;
//	}
//
//	public int getAlertLvl()
//	{
//		return alertLvl;
//	}
//
//	public void setAlertLvl(int alertLvl)
//	{
//		this.alertLvl = alertLvl;
//	}

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
	
	public String toTitleStr()
	{
		return this.alertTp.getTitleEn()+this.paramStr1+"("+this.alertPrompt+")" ;
	}
	
	public JSONObject toJO() throws Exception
	{
		JSONObject jo = DataTranserJSON.extractJSONFromObj(this) ;
		String tpt = this.getAlertTp().getTitleEn() ;
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
	
	
	private void RT_trigger(Number val)
	{
		this.bTrigged = true ;
		this.lastTriggedDT = System.currentTimeMillis() ;
		this.lastTriggedVal = val ;
	}
	
	private void RT_release()
	{
		 this.bTrigged =false;
		 this.lastReleasedDT = System.currentTimeMillis() ;
	}
	
	private transient Number lastV = null ;
	
	public void RT_fireValChged(Number curv)
	{
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
					RT_release();
			}
			else
			{
				if(alertTp.checkTrigger(lastV, curv))
					RT_trigger(curv) ;
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
	
	@JsDef
	public long RT_last_trigged_dt()
	{
		return this.lastTriggedDT ;
	}
	
	@JsDef
	public Number RT_last_trigged_val()
	{
		return this.lastTriggedVal ;
	}
	
	@JsDef
	public long RT_last_released_dt()
	{
		return this.lastReleasedDT ;
	}
}
