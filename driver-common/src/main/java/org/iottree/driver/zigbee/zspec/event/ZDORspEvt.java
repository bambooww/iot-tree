package org.iottree.driver.zigbee.zspec.event;

import org.iottree.driver.zigbee.zspec.ZSController;

public class ZDORspEvt implements ZSController.IEvent
{
	public int groupId ;
	
	public int clusterId ;
	
	public int src_ep ;
	
	public int dst_ep ;
	
	public int src_addr ;
	
	public int linkQual  ;
	
	public boolean bBroadcast ;
	
	public byte[] pld ;
}
