package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.ApsFrame;
import org.iottree.driver.zigbee.ember.types.OutgoingMsgType;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

/**
 * A callback indicating the stack has completed sending a message.
 * 
 * @author jason.zhu
 */
public class MsgSentHandlerRspCB extends EZSPRsp
{
	SLStatus sl_status ;
	
	OutgoingMsgType out_msgtp ; //uint8_t
	
	/**
	 * The destination to which the message was sent, for direct unicasts, 
	 * or the address table or binding index for other unicasts. 
	 * The value is unspecified for multicasts and broadcasts.
	 */
	int idx_of_dest ;//uint16_t
	
	/**
	 * The APS frame for the message.
	 */
	ApsFrame aps_frame ;
	
	int msg_tag ; //
	
	byte[] msg_content;
	
	public MsgSentHandlerRspCB(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x003F ;
	}

	@Override
	public String getFrmName()
	{
		return "messageSentHandler";
	}

	public SLStatus getSLStatus()
	{
		return this.sl_status ;
	}
	
	
	@Override
	protected int getParamsLen()
	{
		return 1 ;
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
		if(len<1)
			return false;
		if(this.owner.getEZSPVersion()<0x0e)
		{
			this.out_msgtp = OutgoingMsgType.valOfInt(bs[offset] & 0xFF) ;
			this.idx_of_dest = DataUtil.getUInt16(bs, offset+1) ; //1-2
			this.aps_frame = new ApsFrame() ;
			int ulen = this.aps_frame.unpack(bs, offset+3) ;
			int sidx = offset+3+ulen ;
			this.msg_tag = bs[sidx] ;
			SLStatus[] rets= new SLStatus[1] ;
			int st_len = UTIL_readStatus(this.owner.getEZSPVersion(), bs, offset, rets) ;
			this.sl_status = rets[0] ;
			int mlen = bs[sidx+1+st_len] & 0xFF;
			if(mlen>0)
			{
				this.msg_content = new byte[mlen] ;
				System.arraycopy(bs, sidx+2+st_len, this.msg_content, 0, mlen);
			}
		}
		else
		{
			int v = (int) DataUtil.getUInt32(bs, offset); //0-3
			this.sl_status = SLStatus.valOfInt(v);
			this.out_msgtp = OutgoingMsgType.valOfInt(bs[offset+4] & 0xFF) ;
			this.idx_of_dest = DataUtil.getUInt16(bs, offset+5) ; //5-6
			this.aps_frame = new ApsFrame() ;
			int ulen = this.aps_frame.unpack(bs, offset+7) ;
			int sidx = offset+7+ulen ;
			this.msg_tag = bs[sidx] ;
			int mlen = bs[sidx+1] & 0xFF;
			if(mlen>0)
			{
				this.msg_content = new byte[mlen] ;
				System.arraycopy(bs, sidx+2, this.msg_content, 0, mlen);
			}
		}
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject r = super.toJO().put("sl_status",this.sl_status).put("out_msgtp", this.out_msgtp).put("idx_of_dest", this.idx_of_dest) ;
		if(this.aps_frame!=null)
			r.putOpt("aps_frame", this.aps_frame.toJO());
		if(this.msg_content!=null)
			r.put("msg_cont", Convert.byteArray2HexStr(this.msg_content, "-")) ;
		return r ;
	}

}
