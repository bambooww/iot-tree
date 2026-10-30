package org.iottree.driver.zigbee.models;

import java.util.List;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class M_NwkOptions
{
	public int panId;
	public String extPanId;
	public List<Integer> channelList;
	public String networkKey;
	public boolean networkKeyDistribute;
	public boolean hasDefaultExtendedPanId;
	
	public JSONObject toJO()
	{
		return new JSONObject().put("pan_id", this.panId).putOpt("ext_panid", this.extPanId)
				.putOpt("ch_list", Convert.combineWith(this.channelList, ',')).put("nwk_key", this.networkKey)
				.put("nwk_key_distribute", networkKeyDistribute)
				.put("has_def_ext_panid", hasDefaultExtendedPanId) ;
	}
	
	public static M_NwkOptions fromJO(JSONObject jo)
	{
		M_NwkOptions r = new M_NwkOptions() ;
		r.panId = jo.optInt("pan_id",-1) ;
		r.extPanId = jo.optString("ext_panid") ;
		r.channelList = Convert.splitStrAsInts(jo.optString("ch_list"), ",") ;
		r.networkKey = jo.optString("nwk_key") ;
		r.networkKeyDistribute = jo.optBoolean("nwk_key_distribute",false) ;
		r.hasDefaultExtendedPanId = jo.optBoolean("has_def_ext_panid",false) ;
		return r ;
	}
}
