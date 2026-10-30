package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPBuffer;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.ApsFrame;
import org.iottree.driver.zigbee.ember.types.IncomeMsgTp;
import org.iottree.driver.zigbee.ember.types.OutgoingMsgType;
import org.iottree.driver.zigbee.ember.types.RxPacketInfo;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

/**
 * A callback indicating a message has been received. This frame is a response to the callback command.
 * 
 * @author jason.zhu
 */
public class MsgIncomeHandlerCB extends EZSPRsp
{
	public static final int FRAME_ID = 0x0045 ;
	public static final String FRAME_NAME = "incomingMessageHandler" ;
	
	/**
	 * The type of the incoming message. One of the following: 
	 * SL_ZIGBEE_INCOMING_UNICAST, 
	 * SL_ZIGBEE_INCOMING_UNICAST_REPLY, 
	 * SL_ZIGBEE_INCOMING_MULTICAST, 
	 * SL_ZIGBEE_INCOMING_MULTICAST_LOOPBACK, 
	 * SL_ZIGBEE_INCOMING_BROADCAST, 
	 * SL_ZIGBEE_INCOMING_BROADCAST_LOOPBACK
	 * uint8_t
	 */
	IncomeMsgTp in_msgtp ; //uint8_t
	
	/**
	 * The APS frame for the message.
	 */
	ApsFrame aps_frame ;
	
	RxPacketInfo rx_pack_inf ;
	
	byte[] msg_pld;
	
	public MsgIncomeHandlerCB(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return FRAME_ID ;
	}

	@Override
	public String getFrmName()
	{
		return FRAME_NAME;
	}
	
	public IncomeMsgTp getIncomeMsgTp()
	{
		return this.in_msgtp ;
	}
	
	public ApsFrame getApsFrame()
	{
		return this.aps_frame ;
	}
	
	public RxPacketInfo getRxPacketInfo()
	{
		return this.rx_pack_inf ;
	}
	
	public byte[] getMsgPayload()
	{
		return this.msg_pld ;
	}

	@Override
	protected int getParamsLen()
	{
		if (this.getEZSPVersion() < 0x0e)
		{
			return 1 + ApsFrame.STRUCT_LEN +6 +1 + (msg_pld!=null?msg_pld.length:0) ;
		}
		else
		{
			return 1 + ApsFrame.STRUCT_LEN +18 +1 + (msg_pld!=null?msg_pld.length:0) ;
		}
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		return true;
	}

	/*
	 if (this.version < 0x0e) {
                    const type: EmberOutgoingMessageType = this.callbackBuffalo.readUInt8();
                    const indexOrDestination = this.callbackBuffalo.readUInt16();
                    const apsFrame: EmberApsFrame = this.callbackBuffalo.readEmberApsFrame();
                    const messageTag = this.callbackBuffalo.readUInt8();
                    const status = this.callbackBuffalo.readStatus(this.version);
                    // EzspPolicyId.MESSAGE_CONTENTS_IN_CALLBACK_POLICY set to messageTag only, so skip parsing entirely
                    // const messageContents = this.callbackBuffalo.readPayload();

                } else {
                    const status = this.callbackBuffalo.readUInt32();
                    const type: EmberOutgoingMessageType = this.callbackBuffalo.readUInt8();
                    const indexOrDestination = this.callbackBuffalo.readUInt16();
                    const apsFrame: EmberApsFrame = this.callbackBuffalo.readEmberApsFrame();
                    const messageTag = this.callbackBuffalo.readUInt16();
                    // EzspPolicyId.MESSAGE_CONTENTS_IN_CALLBACK_POLICY set to messageTag only, so skip parsing entirely
                    // const messageContents = this.callbackBuffalo.readPayload();

                }
	 */
	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		EZSPBuffer databuf = new EZSPBuffer(bs,offset,len) ;
		if (this.getEZSPVersion() < 0x0e)
		{//1 + 
			this.in_msgtp = IncomeMsgTp.valOfInt(databuf.readUInt8());
			this.aps_frame = databuf.readEmberApsFrame();
			this.rx_pack_inf = new RxPacketInfo();
			this.rx_pack_inf.lastHopLqi = databuf.readUInt8();
			this.rx_pack_inf.lastHopRssi = databuf.readInt8();
			this.rx_pack_inf.senderShortAddr = databuf.readUInt16();
			this.rx_pack_inf.bindingIdx = databuf.readUInt8();
			this.rx_pack_inf.addrIdx = databuf.readUInt8();

			this.msg_pld = databuf.readPayload();
			//this.ezspIncomeMsgHandle(type, apsFrame, packetInfo, msg_pld);
		}
		else
		{
			this.in_msgtp = IncomeMsgTp.valOfInt(databuf.readUInt8());
			this.aps_frame = databuf.readEmberApsFrame();
			this.rx_pack_inf = databuf.readRxPacketInfo(); //len=18
			this.msg_pld = databuf.readPayload();
			//this.ezspIncomeMsgHandle(type, apsFrame, packetInfo, msg_pld);
		}
		
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject r = super.toJO().put("in_msgtp", this.in_msgtp);
		if(rx_pack_inf!=null)
			r.putOpt("rx_pack_inf",this.rx_pack_inf.toJO()) ;
		if(this.aps_frame!=null)
			r.putOpt("aps_frame", this.aps_frame.toJO());
		if(this.msg_pld!=null)
			r.put("msg_cont", Convert.byteArray2HexStr(this.msg_pld, "-")) ;
		return r ;
	}

}
