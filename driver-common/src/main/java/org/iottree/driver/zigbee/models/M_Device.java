package org.iottree.driver.zigbee.models;

import org.json.JSONObject;

public class M_Device
{
	public int nwk_addr =-1; //short addr; not used for restore-不能用来恢复数据
	public String ieee_addr ;
	public boolean b_dir_child ; // is direct child or not
	public String link_key;
	public long rx_counter;
	public long tx_counter ;
	
	public JSONObject toJO()
	{
		return new JSONObject().putOpt("nwk_addr",this.nwk_addr).putOpt("ieee_addr", this.ieee_addr)
				.putOpt("link_key", this.link_key).put("rx_cc",this.rx_counter).put("tx_cc", this.tx_counter) ;
	}
	
	public static M_Device fromJO(JSONObject jo)
	{
		M_Device r = new M_Device() ;
		r.nwk_addr = jo.optInt("nwk_addr", -1) ;
		r.ieee_addr = jo.optString("ieee_addr") ;
		r.link_key = jo.optString("link_key") ;
		r.rx_counter = jo.optLong("rx_cc") ;
		r.tx_counter =  jo.optLong("tx_cc") ;
		return r ;
	}
}
