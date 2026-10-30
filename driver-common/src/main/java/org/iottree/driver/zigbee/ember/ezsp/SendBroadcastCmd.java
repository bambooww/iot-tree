package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.ApsFrame;
import org.json.JSONObject;

public class SendBroadcastCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0036 ;
	public static final String FRAME_NAME = "sendBroadcast" ;
	
	/**
	 * The aliased source from which we send the broadcast. This must be SL_ZIGBEE_NULL_NODE_ID 
	 *   if we do not need an aliased source
	 * sl_802154_short_addr_t
	 */
	int alias_addr = -1 ;
	
	/**
	 * The destination to which to send the broadcast. This must be one of the three ZigBee broadcast addresses.
	 * sl_802154_short_addr_t destination
	 */
	int dest_addr ;

	/**
	 * The alias nwk sequence number. this won't be used if there is no aliased source.
	 * uint8_t nwkSequence
	 */
	int nwk_seq = -1;
	
	/**
	 * The APS frame for the message.
	 * sl_zigbee_aps_frame_t apsFrame
	 */
	ApsFrame aps_frm ;
	
	/**
	 * The message will be delivered to all nodes within radius hops of the sender. 
	 * A radius of zero is converted to EMBER_MAX_HOPS.
	 * uint8_t radius
	 */
	int radius ;
	
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
	
	public SendBroadcastCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public SendBroadcastCmd(EmberManager emgr,int alias_addr,int dest_addr,int nwk_seq,ApsFrame aps_frm,
			int radius,int msg_tag,byte[] msg_cont)
	{
		super(emgr) ;
		this.alias_addr=alias_addr;
		this.dest_addr = dest_addr;
		this.nwk_seq = nwk_seq;
		this.aps_frm = aps_frm ;
		this.radius = radius ;
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
			return 16+(msg_cont!=null?msg_cont.length:0);
		else
			return 19+(msg_cont!=null?msg_cont.length:0);
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(this.getEZSPVersion()<0x0e)
		{
			DataUtil.setUInt16(this.dest_addr, bs, offset); //0-1
			this.aps_frm.pack(bs, offset+2) ; //2-12
			bs[offset+13] = (byte)this.radius ;
			bs[offset+14] = (byte)this.msg_tag ;
			bs[offset+15] = (byte)(msg_cont!=null?msg_cont.length:0) ;
			if(msg_cont!=null&&msg_cont.length>0)
				System.arraycopy(msg_cont, 0, bs, offset+16, msg_cont.length);
		}
		else
		{
			DataUtil.setUInt16(this.alias_addr, bs, offset); //0-1
			DataUtil.setUInt16(this.dest_addr, bs, offset+2); //2-3
			bs[offset+4] = (byte)nwk_seq ;
			this.aps_frm.pack(bs, offset+5) ; //5-15
			bs[offset+16] = (byte)this.radius ;
			bs[offset+17] = (byte)this.msg_tag ;
			bs[offset+18] = (byte)(msg_cont!=null?msg_cont.length:0) ;
			if(msg_cont!=null&&msg_cont.length>0)
				System.arraycopy(msg_cont, 0, bs, offset+19, msg_cont.length);
		}
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(this.getEZSPVersion()<0x0e)
		{
			this.dest_addr = DataUtil.getUInt16(bs, offset); //0-1
			this.aps_frm = new ApsFrame();
			this.aps_frm.unpack(bs, offset+2) ; //2-12
			this.radius = bs[offset+13] & 0xFF ;
			this.msg_tag = bs[offset+14] & 0xFF ;
			int mlen = bs[offset+15] & 0xFF ;
			if(mlen>0)
			{
				this.msg_cont = new byte[mlen] ;
				System.arraycopy(bs, offset+16, msg_cont, 0, msg_cont.length);
			}
		}
		else
		{
			this.alias_addr = DataUtil.getUInt16(bs, offset); //0-1
			this.dest_addr = DataUtil.getUInt16(bs, offset+2); //2-3
			this.nwk_seq = bs[offset+4] & 0xFF ;
			this.aps_frm = new ApsFrame();
			this.aps_frm.unpack(bs, offset+5) ; //5-15
			this.radius = bs[offset+16] & 0xFF ;
			this.msg_tag = bs[offset+17] & 0xFF ;
			int mlen = bs[offset+18] & 0xFF ;
			if(mlen>0)
			{
				this.msg_cont = new byte[mlen] ;
				System.arraycopy(bs, offset+19, msg_cont, 0, msg_cont.length);
			}
		}
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("alias_addr",this.alias_addr).put("dst_addr",this.dest_addr).put("nwk_seq", this.nwk_seq)
				.put("aps_frm", this.aps_frm.toJO()).put("radius", this.radius)
				.put("msg_tag", this.msg_tag).put("msg_cont", Convert.byteArray2HexStr(this.msg_cont, "-"));
	}
}
