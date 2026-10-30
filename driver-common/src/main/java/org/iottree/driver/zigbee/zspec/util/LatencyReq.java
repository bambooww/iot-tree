package org.iottree.driver.zigbee.zspec.util;

public class LatencyReq
{
	private static String[] LRS=new String[] {
			"no latency", //0x00
			"fast beacons",
			"-slow beacons"
	};
	
	private int latencyReq ;
	
	public LatencyReq(int v)
	{
		if(v<0||v>0x02)
			throw new IllegalArgumentException("LatencyReq value in [0x00,0x02]") ;
		this.latencyReq = v ;
	}

	public int getLatencyReq()
	{
		return this.latencyReq ;
	}
	
	public String getTitle()
	{
		return LRS[this.latencyReq] ;
	}
	
	public String toString()
	{
		return "0x"+String.format("%02X", this.latencyReq)+"-"+this.getTitle() ;
	}
}
