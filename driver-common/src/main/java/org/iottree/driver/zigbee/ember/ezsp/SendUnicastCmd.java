package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.ApsFrame;
import org.iottree.driver.zigbee.ember.types.OutgoingMsgType;
import org.json.JSONObject;
/**
 * Sends a unicast message as per the ZigBee specification. The message will arrive at its destination 
 * only if there is a known route to the destination node. 
 * Setting the ENABLE_ROUTE_DISCOVERY option will cause a route to be discovered if none is known. 
 * Setting the FORCE_ROUTE_DISCOVERY option will force route discovery. Routes to end-device children of the local node are always known. 
 * Setting the APS_RETRY option will cause the message to be retransmitted until either a matching acknowledgement is received or three transmissions have been made. 
 * Note: Using the FORCE_ROUTE_DISCOVERY option will cause the first transmission to be consumed by a route request
 *  as part of discovery, so the application payload of this packet will not reach its destination on the first attempt. 
 *  If you want the packet to reach its destination, the APS_RETRY option must be set so that another attempt is made to transmit the message 
 *  with its application payload after the route has been constructed. 
 *  
 *  Note: When sending fragmented messages, the stack will only assign a new APS sequence number for the first fragment 
 *  of the message (i.e., SL_ZIGBEE_APS_OPTION_FRAGMENT is set and the low-order byte of the groupId field 
 *  in the APS frame is zero). For all subsequent fragments of the same message, the application must set the sequence number field 
 *  in the APS frame to the sequence number assigned by the stack to the first fragment.
 *  
 * @author jason.zhu
 */
public class SendUnicastCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0034 ;
	public static final String FRAME_NAME = "sendUnicast" ;
	
	/**
	 * Specifies the outgoing message type. Must be one of SL_ZIGBEE_OUTGOING_DIRECT, 
	 * SL_ZIGBEE_OUTGOING_VIA_ADDRESS_TABLE, or SL_ZIGBEE_OUTGOING_VIA_BINDING.
	 */
	OutgoingMsgType out_msg_tp = OutgoingMsgType.DIRECT ;
	
	/**
	 * Depending on the type of addressing used, this is either the sl_802154_short_addr_t of the destination,
	 *  an index into the address table, or an index into the binding table.
	 *  sl_802154_short_addr_t indexOrDestination
	 */
	int idx_or_dst ;

	/**
	 * The APS frame for the message.
	 * sl_zigbee_aps_frame_t apsFrame
	 */
	ApsFrame aps_frm ;
	
	/**
	 * A value chosen by the Host. This value is used in the ezspMessageSentHandler response to refer to this message.
	 * uint16_t messageTag
	 */
	int msg_tag ;
	
//	/**
//	 * The length of the messageContents parameter in bytes.
//	 * uint8_t messageLength
//	 */
//	int msg_len = 0 ;
	
	/**
	 * The broadcast message.
	 * uint8_t[] messageContents
	 */
	byte[] msg_cont = null ;
	
	public SendUnicastCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public SendUnicastCmd(EmberManager emgr,OutgoingMsgType out_msg_tp,int idx_or_dst,ApsFrame aps_frm,
			int msg_tag,byte[] msg_cont)
	{
		super(emgr) ;
		this.out_msg_tp=out_msg_tp;
		this.idx_or_dst = idx_or_dst;
		this.aps_frm = aps_frm ;
		this.msg_tag = msg_tag;
		this.msg_cont = msg_cont ;
//		if(msg_cont!=null)
//			msg_len = msg_cont.length ;
	}
	
	@Override
	public int getFrmID()
	{
		return FRAME_ID;
	}

	@Override
	public String getFrmName()
	{
		return FRAME_NAME ;
	}

	
	@Override
	protected int getParamsLen()
	{
		if(this.getEZSPVersion()<0x0e)
			return 15+(msg_cont!=null?msg_cont.length:0);
		else
			return 16+(msg_cont!=null?msg_cont.length:0);
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		bs[offset] = (byte)this.out_msg_tp.getIntVal() ;
		DataUtil.setUInt16(this.idx_or_dst, bs, offset+1); //1-2
		this.aps_frm.pack(bs, offset+3) ; //3-13
		
		if(this.getEZSPVersion()<0x0e)
		{
			bs[offset+14] = (byte)this.msg_tag ;
			bs[offset+15] = (byte)(msg_cont!=null?msg_cont.length:0) ;
			if(msg_cont!=null&&msg_cont.length>0)
				System.arraycopy(msg_cont, 0, bs, offset+16, msg_cont.length);
		}
		else
		{
			DataUtil.setUInt16(this.msg_tag, bs, offset+14); //14-15
			bs[offset+16] = (byte)(msg_cont!=null?msg_cont.length:0) ;
			if(msg_cont!=null&&msg_cont.length>0)
				System.arraycopy(msg_cont, 0, bs, offset+17, msg_cont.length);
		}
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		this.out_msg_tp = OutgoingMsgType.valOfInt(bs[offset] & 0xFF) ;
		this.idx_or_dst = DataUtil.getUInt16(bs, offset+1); //1-2
		this.aps_frm = new ApsFrame();
		this.aps_frm.unpack(bs, offset+3) ; //3-13
		
		if(this.getEZSPVersion()<0x0e)
		{
			this.msg_tag = bs[14] & 0xFF ;
			int mlen = bs[15] & 0xFF ;
			if(mlen>0)
			{
				this.msg_cont = new byte[mlen] ;
				System.arraycopy(bs, offset+16, msg_cont, 0, msg_cont.length);
			}
		}
		else
		{
			this.msg_tag = DataUtil.getUInt16(bs, offset+14); //14-15
			int mlen = bs[16] & 0xFF ;
			if(mlen>0)
			{
				this.msg_cont = new byte[mlen] ;
				System.arraycopy(bs, offset+17, msg_cont, 0, msg_cont.length);
			}
		}
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("out_msg_tp",this.out_msg_tp).put("idx_or_dst",this.idx_or_dst)
				.put("aps_frm", this.aps_frm.toJO())
				.put("msg_tag", this.msg_tag).put("msg_cont", Convert.byteArray2HexStr(this.msg_cont, "-"));
	}
}
