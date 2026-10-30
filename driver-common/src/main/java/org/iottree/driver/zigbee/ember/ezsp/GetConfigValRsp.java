package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataBuffer;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPBuffer;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

public class GetConfigValRsp extends EZSPRsp
{
	/**
	 * sl_status_t uint32_t  See sl_status.h for an enumerated list.
	 */
	SLStatus status ;
	
	/**
	 * uint16_t value The configuration value.
	 */
	int config_v;
	
	public GetConfigValRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
//	public GetConfigValRsp(int status,int config_v)
//	{
//		this.status = status ;
//		this.config_v = config_v ;
//	}
	
	@Override
	public int getFrmID()
	{
		return 0x52;
	}

	@Override
	public String getFrmName()
	{
		return "getConfigurationValue";
	}

	public SLStatus getStatus()
	{
		return this.status ;
	}
	
	public int getConfigV()
	{
		return this.config_v ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 6;
	}
	
	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
//		DataUtil.setUInt32(this.status, bs, offset); //0-3
//		DataUtil.setUInt16(this.config_v, bs, offset+4); //4-5
		return true;
	}
	
	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<6)
			return false;
		EZSPBuffer dbuf = new EZSPBuffer(bs,offset,len) ;
		this.status = dbuf.readStatus(this.owner.getEZSPVersion()) ;
		this.config_v = dbuf.readUInt16() ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("status",this.status).put("config_v", this.config_v);
	}
}
