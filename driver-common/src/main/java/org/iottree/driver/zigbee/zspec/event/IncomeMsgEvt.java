package org.iottree.driver.zigbee.zspec.event;

import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zspec.*;

public interface IncomeMsgEvt extends ZSController.IEvent
{
	/**
	 * Specifies the group ID of the device
	 * @return
	 */
	public int getGroupId() ;
	
	/**
	 * Specifies the cluster Id (only the LSB is used in V1.0 networks.)
	 * @return
	 */
	public int getClusterId() ;
	
	public ZCLCluster getCluster() ;
	
	public int getSrcEndpoint() ;
	
	public int getDstEndpoint();
	
	public int getSrcAddr() ; // sender
	
	public boolean getWasBroadcast();
	
	public int getLinkQuality();
	
	/**
	 * unpack zcl frame in payload
	 * @return
	 */
	public ZCLFrame getZCLFrame(StringBuilder failedr);
//	clusterID: apsFrame.clusterId,
//    header: Zcl.Header.fromBuffer(messageContents),
//    address: sender,
//    ZCLFrame zcl_frm;
//    endpoint: apsFrame.sourceEndpoint,
//    linkquality: lastHopLqi,
//    groupID: apsFrame.groupId,
//    wasBroadcast: type === EmberIncomingMessageType.BROADCAST || type === EmberIncomingMessageType.BROADCAST_LOOPBACK,
//    destinationEndpoint: apsFrame.destinationEndpoint,
}
