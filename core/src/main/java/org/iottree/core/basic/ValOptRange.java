package org.iottree.core.basic;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class ValOptRange extends ValOption
{
	public static final String TP = "range" ;
	
	String minStr = null ;
	
	String maxStr = null;
	
	@Override
	public String getTP()
	{
		return TP;
	}
	
	public String getMinStr()
	{
		if(minStr==null)
			return "" ;
		return this.minStr ;
	}
	
	public String getMaxStr()
	{
		if(maxStr==null)
			return "" ;
		return this.maxStr ;
	}
	
	public Long getMinInt()
	{
		if(Convert.isNullOrEmpty(this.minStr))
			return null ;
		return Long.parseLong(this.minStr) ;
	}
	
	public Long getMaxInt()
	{
		if(Convert.isNullOrEmpty(this.maxStr))
			return null ;
		return Long.parseLong(this.maxStr) ;
	}
	
	public Double getMinDouble()
	{
		if(Convert.isNullOrEmpty(this.minStr))
			return null ;
		return Double.parseDouble(this.minStr) ;
	}
	
	public Double getMaxDouble()
	{
		if(Convert.isNullOrEmpty(this.maxStr))
			return null ;
		return Double.parseDouble(this.maxStr) ;
	}
	
	@Override
	public String getOptTitle()
	{
		return "["+this.getMinStr()+","+this.getMaxStr()+"]";
	}
	
	@Override
	public String RT_getValOptTitle(Object val)
	{
		return null ;
	}

	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		jo.putOpt("min", this.minStr) ;
		jo.putOpt("max", this.maxStr) ;
		return jo ;
	}
	
	protected void fromJO(JSONObject jo)
	{
		this.minStr = jo.optString("min") ;
		this.maxStr = jo.optString("max") ;
	}
}
