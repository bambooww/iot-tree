package org.iottree.driver.zigbee.zspec.event;

import org.iottree.driver.zigbee.zcl.ZCLFrame;

public class ZclPayload
{
	public int clusterID;
	public String address ; //: number | string;

	public int endpoint;
	public int linkquality;
	public int groupID;
	public boolean wasBroadcast=false;
	public int destinationEndpoint;
	
	//head and payload
	public ZCLFrame frame;
	
}
