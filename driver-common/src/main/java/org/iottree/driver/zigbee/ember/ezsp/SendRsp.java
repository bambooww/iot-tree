package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;
import org.iottree.driver.zigbee.ember.util.ICopyable;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

public class SendRsp extends EZSPRsp implements ICopyable<SendRsp>
{
	EzspFrameID sendFrmId ;
	
	SLStatus status ;
	
	int seq ;
	
	public SendRsp(EmberManager emgr,EzspFrameID frmid)
	{
		super(emgr) ;
		this.sendFrmId = frmid ;
	}
	

	@Override
	public SendRsp copyMe(EmberManager emgr)
	{
		return new SendRsp(emgr,this.sendFrmId);
	}

	
	@Override
	public int getFrmID()
	{
		return sendFrmId.getIntVal(); //SendMulticastCmd.FRAME_ID;
	}

	@Override
	public String getFrmName()
	{
		return sendFrmId.name();//SendMulticastCmd.FRAME_NAME ;
	}
	

	public SLStatus getStatus()
	{
		return this.status ;
	}
	
	public int getSeq()
	{
		return this.seq ;
	}
	
	@Override
	protected int getParamsLen()
	{
		if (this.getEZSPVersion() < 0x0e)
			return 1+1 ;
		else
			return 4+1 ;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		SLStatus[] rets= new SLStatus[1] ;
		int rlen = UTIL_readStatus(this.owner.getEZSPVersion(), bs, offset, rets) ;
		this.status = rets[0] ;
		this.seq = bs[offset+rlen] & 0xFF;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("status",this.status).put("seq", this.seq);
	}

}
