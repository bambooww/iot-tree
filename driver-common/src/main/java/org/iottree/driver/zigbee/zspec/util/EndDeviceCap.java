package org.iottree.driver.zigbee.zspec.util;

import java.util.ArrayList;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * End Device Capabilities
 * @author jason.zhu
 *
 */
public class EndDeviceCap
{
	/**
	 Specifies the MAC capabilities of the device.
	 BIT:
 0 – Alternate PAN Coordinator
 1 – Device type: 1- ZigBee Router; 0 – End Device
 2 – Power Source: 1 Main powered
 3 – Receiver on when Idle
 4 – Reserved
 5 – Reserved
 6 – Security capability
 7 – Reserved
	 */
	private static String[] OPTS = new String[] {
			"Alternate PAN Coordinator", //0
			"Device type: 1- ZigBee Router; 0 – End Device", //1
			"Power Source: 1 Main powered", //2  & 0x04
			"Receiver on when Idle", //3  // & 0x08
			"", //4
			"", //5
			"Security capability", //6
			""
	};
	
	int cap ;
	
	public EndDeviceCap(int cap)
	{
		this.cap = cap ;
	}
	
	public int getCapVal()
	{
		return this.cap ;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof EndDeviceCap))
			return false;
		return this.cap == ((EndDeviceCap)obj).cap ;
	}
	
	public boolean isZigbeeRouter()
	{
		return (this.cap & 0x02) > 0 ;
	}
	
	public boolean isEndDevice()
	{
		return !isZigbeeRouter() ;
	}
	
	public String getDeviceTypeT()
	{
		if(isZigbeeRouter())
			return "router";
		else
			return "end_device";
	}
	
	public boolean isAlterPANCoor()
	{
		return (this.cap & 0x01) > 0 ;
	}
	
	public boolean isMainPower()
	{
		return (this.cap & 0x04) > 0 ;
	}
	
	public boolean isRecverOnWhenIdle()
	{
		return (this.cap & 0x08) > 0 ;
	}
	
	public boolean isSecurityCap()
	{
		return (this.cap & (1<<6))>0 ;
	}
	
	public ArrayList<String> getOptionsList()
	{
		ArrayList<String> rets = new ArrayList<>() ;
		for(int i = 0 ; i < OPTS.length ; i ++)
		{
			int v = 1<<i ;
			if((this.cap & v)>0)
			{
				if(Convert.isNotNullEmpty(OPTS[i]))
					rets.add(OPTS[i]) ;
			}
		}
		return rets ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().put("cap_v",this.cap).put("is_router",this.isZigbeeRouter()).put("is_end_dev",this.isEndDevice())
				.put("is_alter_pan_coor", this.isAlterPANCoor()).put("is_main_power", this.isMainPower())
				.put("is_recv_on_idle", this.isRecverOnWhenIdle()).put("is_security_cap", this.isSecurityCap()) ;
	}
	
	public String toString()
	{
		return Convert.combineWith(this.getOptionsList(), ',');
	}
}
