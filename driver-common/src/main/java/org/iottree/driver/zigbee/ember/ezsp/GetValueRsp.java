package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

public class GetValueRsp extends EZSPRsp
{
	/**
	 * sl_status_t	uint32_t	See sl_status.h for an enumerated list.
	 sl_status_t status
	SL_STATUS_OK if the value was read successfully, SL_STATUS_ZIGBEE_EZSP_ERROR otherwise. Errors could be SL_ZIGBEE_EZSP_ERROR_INVALID_ID if the NCP does not recognize valueId, SL_ZIGBEE_EZSP_ERROR_INVALID_VALUE if the length of the returned value exceeds the size of local storage allocated to receive it.
	 */
	SLStatus status ;
	
	/**
	 * uint8_t valueLength

Both a command and response parameter. On command, the maximum size in bytes of local storage allocated to receive the returned value. On response, the actual length in bytes of the returned value.
	 */
	int value_len ;
	
	/**
	 * uint8_t[] valuev The value.
	 */
	byte[] values ;
	
	public GetValueRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x00AA;
	}

	@Override
	public String getFrmName()
	{
		return "getValue";
	}

	@Override
	protected int getParamsLen()
	{
		return 2+this.value_len;
	}

	
	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		int wlen = UTIL_writeStatus(this.owner.getEZSPVersion(), this.status.getIntVal(), bs, offset) ;
		//DataUtil.setUInt32(this.status, bs, offset);
		bs[offset+wlen] = (byte)this.value_len ;
		System.arraycopy(this.values, 0, bs, offset+wlen+1, this.value_len);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
		{
			if(failedr!=null)
				failedr.append("unpack params len<0");
			return false;
		}
		SLStatus[] rets = new SLStatus[1] ;
		int rlen = UTIL_readStatus(this.owner.getEZSPVersion(), bs, offset,rets) ;
		this.status = rets[0]; //DataUtil.getUInt32(bs, offset) ;//0-3
		if(this.status!=SLStatus.OK)
			return true;
		this.value_len = bs[offset+rlen] & 0xFF ;
		this.values = new byte[this.value_len] ;
		System.arraycopy(bs, offset+1+rlen, this.values, 0, this.value_len);
		return true;
	}
	
	public byte[] getValues()
	{
		return this.values ;
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject r = super.toJO().put("status",this.status).put("value_len", this.value_len) ;
		if(this.values!=null)
			r.put("values", Convert.byteArray2HexStr(this.values, "-")) ;
		return r ;
	}
}
