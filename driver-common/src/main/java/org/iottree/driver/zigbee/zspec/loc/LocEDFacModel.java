package org.iottree.driver.zigbee.zspec.loc;

import org.iottree.driver.zigbee.zspec.util.PowerSrc;
import org.json.JSONObject;

/**
 * 本地设备相关配置定义
 * 比如：一个EndDevice设备供电方式无法之间查询到=unknown，则可以通过此配置获取
 * @author jason.zhu
 *
 */
public class LocEDFacModel
{
	String facName ; //厂家名称
	String model ; //设备型号
	
	PowerSrc powerSrcDef = null ;
	
	public LocEDFacModel(String fac_n,String model)
	{
		this.facName = fac_n ;
		this.model = model ;
	}
	
	public String getFacName()
	{
		return this.facName ;
	}
	
	public String getModel()
	{
		return model ;
	}
	
	public String getFacModelKey()
	{
		return calFacModelKey(this.facName,this.model) ;
	}
	
	public static String calFacModelKey(String fac_n,String model)
	{
		return fac_n+"_$_"+model ;
	}
	/**
	 * 获得缺省的供电方式
	 * @return
	 */
	public PowerSrc getPowerSrcDef()
	{
		return this.powerSrcDef ;
	}
	
	void fromJO(JSONObject jo)
	{
		int p = jo.optInt("power_src_def", -1) ;
		this.powerSrcDef = PowerSrc.valOfInt(p) ;
	}
}
