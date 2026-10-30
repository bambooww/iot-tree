package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

public class AttrReadRsp extends EZSPRsp
{
	/**
	 * sl_zigbee_af_status_t	uint8_t	A ZCL status
		An sl_zigbee_af_status_t value indicating success or the reason for failure, 
		handled by the EZSP layer as a uint8_t. 255 indicates an EZSP-specific error.
	 */
	int af_status ;
	
	int attr_data_tp ;
	
	int read_len ;
	
	byte[] attr_data ;
	
	
	public AttrReadRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x0108;
	}

	@Override
	public String getFrmName()
	{
		return "readAttribute";
	}

	public int getAFStatus()
	{
		return this.af_status ;
	}
	
	
	public int getAttrDataType()
	{
		return this.attr_data_tp ;
	}
	
	public int getAttrDataLen()
	{
		return this.read_len ;
	}
	
	public byte[] getAttrData()
	{
		return this.attr_data ;
	}
	
	@Override
	protected int getParamsLen()
	{
		if(this.attr_data==null)
			return 3 ;
		return 3+this.attr_data.length;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<3)
			return false;
		bs[offset] = (byte)this.af_status ;
		bs[offset+1] = (byte)this.attr_data_tp ;
		bs[offset+2] = (byte)this.read_len ;
		if(this.read_len>0)
			System.arraycopy(this.attr_data, 0, bs, offset+3, this.read_len);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.af_status = bs[offset] & 0xFF ;
		this.attr_data_tp = bs[offset+1] & 0xFF ;
		this.read_len = bs[offset+2] & 0xFF ;
		if(this.read_len>0)
		{
			this.attr_data = new byte[this.read_len] ;
			System.arraycopy(bs, offset+3, this.attr_data, 0, this.read_len);
		}
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("af_status",this.af_status).put("attr_data_tp", this.attr_data_tp).put("read_len", this.read_len)
				.putOpt("attr_data", Convert.byteArray2HexStr(this.attr_data, "-"));
	}
}
