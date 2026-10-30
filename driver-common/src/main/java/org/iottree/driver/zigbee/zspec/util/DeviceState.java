package org.iottree.driver.zigbee.zspec.util;


public class DeviceState
{
	private static String[] STATES=new String[] {
			"Initialized - not started automatically", //0x00
			"Initialized - not connected to anything", //1
			"Discovering PAN's to join", //2
			"Joining a PAN", //3
			"Rejoining a PAN, only for end devices", //4
			"Joined but not yet authenticated by trust center", //5
			"Started as device after authentication", //6
			"Device joined, authenticated and is a router", //6
			"Starting as ZigBee Coordinator", //8
			"Started as ZigBee Coordinator", //9
			"Device has lost information about its parent"  //ox0A
	};
	
	private int stateV ;
	
	public DeviceState(int v)
	{
		if(v<0||v>0x0A)
			throw new IllegalArgumentException("state value in [0x00,0x0A]") ;
		this.stateV = v ;
	}

	public int getStateV()
	{
		return this.stateV ;
	}
	
	public String getStateT()
	{
		return STATES[this.stateV] ;
	}
	
	public boolean isZigbeeCoorStarted()
	{
		return this.stateV==0x09 ;
	}
	
	public String toString()
	{
		return "0x"+String.format("%02X", this.stateV)+"-"+this.getStateT() ;
	}
}
