package org.iottree.core.basic.ce;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * EO
 * 
 * Exchange Object for different instance/process in different pos
 * @author jason.zhu
 *
 */
public abstract class ExchgObj
{
	private ExchgModule belongTo = null ;
	
	public abstract String getExchgName() ;
	
	public abstract String getExchgTP() ;
	
	public abstract String getExchgTitle() ;
	
	protected abstract void setExchgBasic(String tp,String name,String title) ;
	
	public JSONObject toExchgJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("n",this.getExchgName()) ;
		jo.put("tp", this.getExchgTP()) ;
		jo.putOpt("t", this.getExchgTitle()) ;
		JSONObject pmjo = this.toExchgPmJO();
		jo.putOpt("pm", pmjo) ;
		return jo ;
	}
	
	protected abstract JSONObject toExchgPmJO() ;
	
	public boolean fromExchgJO(ExchgModule module,JSONObject jo)
	{
		String n = jo.optString("n") ;
		if(Convert.isNullOrEmpty(n))
			return false;
		String t = jo.optString("t") ;
		String tp = jo.optString("tp") ;
		this.setExchgBasic(tp, n, t);
		JSONObject pmjo= jo.optJSONObject("pm") ;
		
		return fromExchgPmJO(pmjo);
	}
	
	protected abstract boolean fromExchgPmJO(JSONObject pmjo) ;
}
