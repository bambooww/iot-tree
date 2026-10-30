package org.iottree.driver.zigbee.ember.util;

import java.util.ArrayList;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.ember.types.NwkParams;

public class NwkCache
{
	public String ieee_addr  = null;
	
//	public int panID = -1 ;
//	
//	public String extPanID = null ;
	
	public String networkKey = null ;
	
	public NwkParams params = null;// new NwkParams() ;
	
	public ArrayList<Integer> chList = new ArrayList<>() ;
	
	
	public String toString()
	{
		return "ieee="+ieee_addr+" params="+params.toJO() ;
	}
	
	public boolean chkExtPanID(String ext_panid)
	{
		if(this.params==null)
			return false;
		if(Convert.isNullOrEmpty(this.params.extPanId))
			return false;
		return this.params.extPanId.equalsIgnoreCase(ext_panid) ;
	}
	
	public boolean chkNetworkKey(String nwk_key)
	{
		if(Convert.isNullOrEmpty(this.networkKey))
			return false;
		return this.networkKey.equalsIgnoreCase(nwk_key) ;
	}
	
	public boolean includeCh(int radioch)
	{
		if(this.chList==null)
			return false;
		return this.chList.contains(radioch) ;
	}
	
//	public static final NwkCache getDef()
//	{
//		NwkCache ret = new NwkCache();
//		ret.panID = 0x1a62 ;
//		ret.ieee_addr = "1234567812345678" ;
//		ret.extPanID= "dddddddddddddddd";
//		ret.chList.add(11);
//		ret.networkKey= "01030507091113150002040608101213";
//		//ret.n
//		    // networkKeyDistribute?: boolean;
//		return ret ;
//	}
}
