package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

public class GetEui64Rsp extends EZSPRsp
{
	/**
	 * uint8_t[8] 
	 */
	byte[] ieee_addr ;
	
	public GetEui64Rsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x0026;
	}

	@Override
	public String getFrmName()
	{
		return "getEui64";
	}

	@Override
	protected int getParamsLen()
	{
		return 8;
	}

	public String getIEEEAddr()
	{
		return DataUtil.transIEEEAddr(this.ieee_addr, 0) ;
	}
	
	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<8)
			return false;
		System.arraycopy(this.ieee_addr, 0, bs, offset, 8);
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
		this.ieee_addr = new byte[8] ;
		System.arraycopy(bs, offset, this.ieee_addr, 0, 8);
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject r = super.toJO().put("ieee_addr",DataUtil.transIEEEAddr(this.ieee_addr, 0)) ;
		return r ;
	}

}
