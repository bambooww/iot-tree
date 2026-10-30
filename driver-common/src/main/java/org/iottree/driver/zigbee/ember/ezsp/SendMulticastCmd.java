package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.ApsFrame;
import org.iottree.driver.zigbee.ember.types.OutgoingMsgType;
import org.json.JSONObject;

public class SendMulticastCmd extends EZSPCmd
{
	public static final int  ZA_MAX_HOPS = 12;
	
	public static final int FRAME_ID = 0x0038 ;
	public static final String FRAME_NAME = "sendMulticast" ;
	
	

	/**
	 * The APS frame for the message.
	 * sl_zigbee_aps_frame_t apsFrame
	 */
	ApsFrame aps_frm ;
	
	int hops ;
	
	int broadcast_addr; 
	
	int alias ; //NodeId
	
	int nwk_seq ;
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
	
	public SendMulticastCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public SendMulticastCmd(EmberManager emgr,ApsFrame aps_frm,int hops,int broadcast_addr,int alias,int nwk_seq,
			int msg_tag,byte[] msg_cont)
	{
		super(emgr) ;
		this.aps_frm = aps_frm ;
		this.hops = hops ;
		this.broadcast_addr = broadcast_addr ;
		this.alias = alias ;
		this.nwk_seq = nwk_seq ;
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
			return 18+(msg_cont!=null?msg_cont.length:0);
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		this.aps_frm.pack(bs, offset) ; //0-10
		bs[offset+11] = (byte)hops ;
		if(this.getEZSPVersion()<0x0e)
		{
			bs[offset+12] = (byte)ZA_MAX_HOPS ;
			bs[offset+13] = (byte)this.msg_tag ;
			bs[offset+14] = (byte)(msg_cont!=null?msg_cont.length:0) ;
			if(msg_cont!=null&&msg_cont.length>0)
				System.arraycopy(msg_cont, 0, bs, offset+15, msg_cont.length);
		}
		else
		{
			DataUtil.setUInt16(this.broadcast_addr, bs, offset+12); //12-13
			bs[offset+14] = (byte)this.alias ;
			bs[offset+15] = (byte)this.nwk_seq ;
			bs[offset+16] = (byte)this.msg_tag ;
			bs[offset+17] = (byte)(msg_cont!=null?msg_cont.length:0) ;
			if(msg_cont!=null&&msg_cont.length>0)
				System.arraycopy(msg_cont, 0, bs, offset+18, msg_cont.length);
		}
		
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		this.aps_frm = new ApsFrame();
		this.aps_frm.unpack(bs, offset+3) ; //0-10
		this.hops = bs[offset+11] & 0xFF ;
		
		if(this.getEZSPVersion()<0x0e)
		{
			//bs[offset+12] = (byte)ZA_MAX_HOPS ;
			this.msg_tag = bs[offset+13] & 0xFF ;
			int mlen = bs[offset+14] & 0xFF ;
			if(msg_cont!=null&&msg_cont.length>0)
			{
				this.msg_cont = new byte[mlen] ;
				System.arraycopy(bs, offset+15, msg_cont, 0, msg_cont.length);
			}
		}
		else
		{
			this.alias = bs[offset+14] & 0xFF ;
			this.nwk_seq = bs[offset+15] & 0xFF  ;
			this.msg_tag = bs[offset+16] & 0xFF ;
			int mlen = bs[offset+17] & 0xFF ;
			if(mlen>0)
			{
				this.msg_cont = new byte[mlen] ;
				System.arraycopy(bs, offset+18, msg_cont, 0, msg_cont.length);
			}
		}
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("hops",this.hops).put("broadcast_addr",this.broadcast_addr)
				.put("aps_frm", this.aps_frm.toJO()).put("alias", this.alias).put("nwk_seq", this.nwk_seq)
				.put("msg_tag", this.msg_tag).put("msg_cont", Convert.byteArray2HexStr(this.msg_cont, "-"));
	}

}
