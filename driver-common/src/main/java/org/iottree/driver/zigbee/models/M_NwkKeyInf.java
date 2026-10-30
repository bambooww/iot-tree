package org.iottree.driver.zigbee.models;

import org.json.JSONObject;

public class M_NwkKeyInf
{
	public int seq_num ;
	
	public long frame_counter = 0 ;
	
	public JSONObject toJO()
	{
		return new JSONObject().put("seq_num", this.seq_num).putOpt("frame_counter", this.frame_counter);
	}
	
	public static M_NwkKeyInf fromJO(JSONObject jo)
	{
		M_NwkKeyInf r = new M_NwkKeyInf() ;
		r.seq_num = jo.optInt("seq_num",-1) ;
		r.frame_counter = jo.optLong("frame_counter",0) ;
		return r ;
	}
}
