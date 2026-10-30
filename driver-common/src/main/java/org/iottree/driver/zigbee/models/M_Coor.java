package org.iottree.driver.zigbee.models;

import java.util.ArrayList;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.ember.EmberConsts;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * for coor save (backup) or restore
 * @author jason.zhu
 *
 */
public class M_Coor
{
	public int radio_ch ;

	public int security_lvl = EmberConsts.SECURITY_LEVEL_Z3;
	
	public int nwk_updateid ;
	
	public String coor_ieee_addr ;
	
	public String ezsp_ver ;
	
	//trust center link key
	public String ezsp_tc_linkkey ;
	
	public String znp_ver ;
	
	public String znp_tc_linkkey_seed ; //trust center link key seed
	
	public M_NwkOptions nwk_opts ;
	
	public M_NwkKeyInf nwk_key_inf ;
	
	public ArrayList<M_Device> devices = new ArrayList<>() ;
	
	public boolean checkPanId(int panid)
	{
		if(this.nwk_opts==null)
			return false;
		return this.nwk_opts.panId == panid ;
	}
	
	public boolean checkExtPanId(String ext_panid)
	{
		if(this.nwk_opts==null)
			return false;
		if(Convert.isNullOrEmpty(this.nwk_opts.extPanId))
			return false;
		return this.nwk_opts.extPanId.equalsIgnoreCase(ext_panid) ;
	}
	
	public boolean checkNwkKey(String nwk_key)
	{
		if(this.nwk_opts==null)
			return false;
		if(Convert.isNullOrEmpty(this.nwk_opts.networkKey))
			return false;
		return this.nwk_opts.networkKey.equalsIgnoreCase(nwk_key) ;
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject().putOpt("radio_ch",this.radio_ch).put("security_lvl", this.security_lvl)
				.put("nwk_updateid", this.nwk_updateid).putOpt("coor_ieee_addr", coor_ieee_addr)
				.putOpt("ezsp_ver", this.ezsp_ver).putOpt("ezsp_tc_linkkey", ezsp_tc_linkkey)
				.putOpt("znp_ver", znp_ver).putOpt("znp_tc_lk_seed", znp_tc_linkkey_seed);
		
		if(nwk_opts!=null)
			ret.put("nwk_opts", this.nwk_opts.toJO()) ;
		
		if(this.nwk_key_inf!=null)
			ret.put("nwk_key_inf", this.nwk_key_inf.toJO()) ;
		
		JSONArray jarr = new JSONArray() ;
		ret.put("devices", jarr) ;
		for(M_Device d:this.devices)
		{
			jarr.put(d.toJO()) ;
		}
		
		return ret ;
	}
	
	public static M_Coor fromJO(JSONObject jo)
	{
		M_Coor r = new M_Coor() ;
		r.radio_ch = jo.optInt("radio_ch",-1);
		r.security_lvl = jo.optInt("security_lvl",EmberConsts.SECURITY_LEVEL_Z3) ;
		r.nwk_updateid  = jo.optInt("nwk_updateid",-1);
		r.coor_ieee_addr = jo.getString("coor_ieee_addr") ;
		r.ezsp_ver = jo.optString("ezsp_ver");
		r.ezsp_tc_linkkey = jo.optString("ezsp_tc_linkkey");
		r.znp_ver = jo.optString("ezsp_ver");
		r.znp_tc_linkkey_seed = jo.optString("znp_tc_lk_seed");
		
		JSONObject tmpjo = jo.optJSONObject("nwk_opts") ;
		if(tmpjo!=null)
			r.nwk_opts = M_NwkOptions.fromJO(tmpjo) ;
		
		tmpjo = jo.optJSONObject("nwk_key_inf") ;
		if(tmpjo!=null)
			r.nwk_key_inf = M_NwkKeyInf.fromJO(tmpjo) ;

		JSONArray tmpjarr = jo.getJSONArray("devices") ;
		if(tmpjarr!=null)
		{
			int n = tmpjarr.length() ;
			for(int i = 0 ; i <  n; i ++)
			{
				tmpjo = tmpjarr.getJSONObject(i);
				M_Device md = M_Device.fromJO(tmpjo);
				if(md==null)
					continue ;
				r.devices.add(md) ;
			}
		}
		
		return r ;
	}
}
