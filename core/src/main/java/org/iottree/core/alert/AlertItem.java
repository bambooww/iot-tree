package org.iottree.core.alert;

import org.iottree.core.UATag;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * 
 * @author jason.zhu
 *
 */
public class AlertItem
{
	UATag tag = null ;
	
	ValAlert vA = null;
	
	boolean bTriggerd = false;
	
	boolean bReleased = false;
	
	long dt = -1 ;
	
	Object curV = null ;
	
	
	
	public AlertItem(ValAlert va,Object curv)
	{
		this.vA = va ;
		this.tag = va.getBelongTo() ;
		if(va.RT_is_triggered())
		{
			this.bTriggerd = true ;
			this.dt = va.RT_last_trigged_dt() ;
		}
		else
		{
			this.bReleased = true ;
			this.dt = va.RT_last_released_dt() ;
		}
		
		curV = curv ;
	}
	
	public UATag getTag()
	{
		return this.tag ;
	}
	
	public ValAlert getValAlert()
	{
		return this.vA ;
	}
	
	public boolean isTriggered()
	{
		return this.bTriggerd ;
	}
	
	public boolean isReleased()
	{
		return this.bReleased ;
	}
	
	public Object getCurVal()
	{
		return this.curV ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("triggered", this.bTriggerd);
		jo.put("released", this.bReleased);
		jo.put("dt", dt) ;
		jo.putOpt("val", curV) ;
		jo.putOpt("tt", tag.getTitle()) ;
		jo.putOpt("tp", Convert.plainToHtml(vA.getAlertTitle())) ;
		jo.putOpt("prompt", this.vA.getAlertPrompt()) ;
		return jo ;
	}
}
