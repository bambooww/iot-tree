package org.iottree.driver.zigbee.models;

import org.json.JSONObject;

public class M_LinkKey
{
public String sec_key ;
	
	public long out_frm_cc ;
	
	public long in_frm_cc ;
	
	public JSONObject toJO()
	{
		return new JSONObject().putOpt("sec_key",this.sec_key).put("out_frm_cc", this.out_frm_cc)
				.put("in_frm_cc", this.in_frm_cc) ;
	}
	
	public static M_LinkKey fromJO(JSONObject jo)
	{
		M_LinkKey r = new M_LinkKey() ;
		r.sec_key = jo.optString("sec_key") ; ;
		r.out_frm_cc = jo.optLong("out_frm_cc") ;
		r.in_frm_cc =  jo.optLong("in_frm_cc") ;
		return r ;
	}
}

