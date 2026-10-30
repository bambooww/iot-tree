package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.SendBroadcastCmd;
import org.iottree.driver.zigbee.ember.ezsp.SendMulticastCmd;
import org.iottree.driver.zigbee.ember.ezsp.SendRsp;
import org.iottree.driver.zigbee.ember.ezsp.SendUnicastCmd;
import org.iottree.driver.zigbee.ember.ezsp.SetSrcRouteDiscModeCmd;
import org.iottree.driver.zigbee.ember.ezsp.SetSrcRouteDiscModeRsp;
import org.iottree.driver.zigbee.ember.types.ApsFrame;
import org.iottree.driver.zigbee.ember.types.EmberSrcRouteDiscMode;
import org.iottree.driver.zigbee.ember.types.OutgoingMsgType;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.iottree.driver.zigbee.zspec.ZSpecConsts;

public class MsgRR
{
	public static long setSrcRouteDiscMode(EmberManager em,EmberSrcRouteDiscMode mode) throws Exception
	{
		SetSrcRouteDiscModeCmd cmd = new SetSrcRouteDiscModeCmd(em,mode) ;
		CommonRR<SetSrcRouteDiscModeRsp> rr = new CommonRR<>(em, cmd,SetSrcRouteDiscModeRsp.class) ;
		if (!em.RT_synReqRsp(rr, null))
			return -1;
		return rr.getReturnRsp().getRemainingTime() ;
	}
	
	
	
	/**
     * Sends a broadcast message as per the Zigbee specification.
     * @param alias uint16_t (unused in v13-) The aliased source from which we send the broadcast.
     *        This must be SL_ZIGBEE_NULL_NODE_ID if we do not need an aliased source
     * @param destination The destination to which to send the broadcast. This must be one of the three Zigbee broadcast addresses.
     * @param nwkSequence uint8_t (unused in v13-) The alias nwk sequence number. This won't be used if there is no aliased source.
     * @param apsFrame EmberApsFrame * The APS frame for the message.
     * @param radius uint8_t The message will be delivered to all nodes within radius hops of the sender.
     *        A radius of zero is converted to EMBER_MAX_HOPS.
     * @param messageTag uint8_t (v14+: uint16_t) A value chosen by the Host.
     *        This value is used in the ezspMessageSentHandler response to refer to this message.
     * @param messageContents uint8_t * The broadcast message.
     * @returns An SLStatus value indicating success or the reason for failure.
     * @returns uint8_t * The sequence number that will be used when this message is transmitted.
     */
	public static SendRsp ezspSendBroadcast(EmberManager em,int alias_addr,int dest_addr,int nwk_seq,ApsFrame aps_frm,
			int radius,int msg_tag,byte[] msg_cont) throws Exception
	{
		SendBroadcastCmd cmd = new SendBroadcastCmd(em, alias_addr, dest_addr, nwk_seq, aps_frm,
				radius, msg_tag,msg_cont) ;
		CommonRR<SendRsp> rr = new CommonRR<>(em, cmd,SendRsp.class) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.getReturnRsp() ;
	}
	
	/**
     * Sends a unicast message as per the Zigbee specification.
     * The message will arrive at its destination only if there is a known route to the destination node.
     * Setting the ENABLE_ROUTE_DISCOVERY option will cause a route to be discovered if none is known.
     * Setting the FORCE_ROUTE_DISCOVERY option will force route discovery.
     * Routes to end-device children of the local node are always known.
     * Setting the APS_RETRY option will cause the message to be retransmitted until either a matching acknowledgement is received
     * or three transmissions have been made.
     * Note: Using the FORCE_ROUTE_DISCOVERY option will cause the first transmission to be consumed by a route request as part of discovery,
     * so the application payload of this packet will not reach its destination on the first attempt. If you want the packet to reach its destination,
     * the APS_RETRY option must be set so that another attempt is made to transmit the message with its application payload
     * after the route has been constructed.
     * Note: When sending fragmented messages, the stack will only assign a new APS sequence number for the first fragment of the message
     * (i.e., SL_ZIGBEE_APS_OPTION_FRAGMENT is set and the low-order byte of the groupId field in the APS frame is zero).
     * For all subsequent fragments of the same message, the application must set the sequence number field in the APS frame
     * to the sequence number assigned by the stack to the first fragment.
     * @param type Specifies the outgoing message type.
     *        Must be one of EMBER_OUTGOING_DIRECT, EMBER_OUTGOING_VIA_ADDRESS_TABLE, or EMBER_OUTGOING_VIA_BINDING.
     * @param indexOrDestination Depending on the type of addressing used, this is either the NodeId of the destination,
     *        an index into the address table, or an index into the binding table.
     * @param apsFrame EmberApsFrame * The APS frame which is to be added to the message.
     * @param messageTag uint8_t (v14+: uint16_t) A value chosen by the Host.
     *        This value is used in the ezspMessageSentHandler response to refer to this message.
     * @param messageContents uint8_t * Content of the message.
     * @returns An SLStatus value indicating success or the reason for failure.
     * @returns uint8_t * The sequence number that will be used when this message is transmitted.
     */
	public static SendRsp ezspSendUnicast(EmberManager em,OutgoingMsgType out_msg_tp,int idx_or_dst,ApsFrame aps_frm,
			int msg_tag,byte[] msg_cont) throws Exception
	{
		SendUnicastCmd cmd = new SendUnicastCmd(em,  out_msg_tp,idx_or_dst,aps_frm,msg_tag,msg_cont) ;
		CommonRR<SendRsp> rr = new CommonRR<>(em, cmd,SendRsp.class) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.getReturnRsp() ;
	}
	
	/**
     * Sends a multicast message to all endpoints that share a specific multicast ID and are within a specified number of hops of the sender.
     * @param apsFrame EmberApsFrame * The APS frame for the message. The multicast will be sent to the groupId in this frame.
     * @param hops uint8_t The message will be delivered to all nodes within this number of hops of the sender.
     *        A value of zero is converted to EMBER_MAX_HOPS.
     * @param broadcastAddr uint16_t (unused in v13-) The number of hops that the message will be forwarded by devices
     *        that are not members of the group.
     *        A value of 7 or greater is treated as infinite.
     * @param alias uint16_t (unused in v13-) The alias source address. This must be SL_ZIGBEE_NULL_NODE_ID if we do not need an aliased source
     * @param nwkSequence uint8_t (unused in v13-) The alias sequence number. This won't be used if there is no aliased source.
     * @param messageTag uint8_t (v14+: uint16_t) A value chosen by the Host.
     *        This value is used in the ezspMessageSentHandler response to refer to this message.
     * @param messageContents uint8_t * The multicast message.
	 * @throws Exception 
     * @returns An SLStatus value. For any result other than SLStatus.OK, the message will not be sent.
     * - SLStatus.OK - The message has been submitted for transmission.
     * - SLStatus.INVALID_INDEX - The bindingTableIndex refers to a non-multicast binding.
     * - SLStatus.NETWORK_DOWN - The node is not part of a network.
     * - SLStatus.MESSAGE_TOO_LONG - The message is too large to fit in a MAC layer frame.
     * - SLStatus.ALLOCATION_FAILED - The free packet buffer pool is empty.
     * - SLStatus.BUSY - Insufficient resources available in Network or MAC layers to send message.
     * @returns uint8_t * The sequence number that will be used when this message is transmitted.
     */
	public static SendRsp ezspSendMulticast(EmberManager em,
    		ApsFrame aps_frm,int hops,int broadcast_addr,int alias, //: NodeId,
	        int nwk_seq, //: number,
	        int msg_tag, //: number,
	        byte[] msg_cont //: Buffer,
    ) throws Exception  //: Promise<[SLStatus, apsSequence: number]>
    	{
		SendMulticastCmd cmd = new SendMulticastCmd(em,  aps_frm, hops, broadcast_addr, alias, nwk_seq,
				 msg_tag,msg_cont) ;
		CommonRR<SendRsp> rr = new CommonRR<>(em, cmd,SendRsp.class) ;
		if (!em.RT_synReqRsp(rr, null))
			return null;
		return rr.getReturnRsp() ;
    }
	
}
