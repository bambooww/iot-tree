package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * Sets the manufacturer code to the specified value. 
 * The manufacturer code is one of the fields of the node descriptor.
 * 
 * @author jason.zhu
 */
public class SetFacCodeCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0015 ;
	public static final String FRAME_NAME = "setManufacturerCode" ;
	/**
	 uint16_t code The manufacturer code for the local node.
	 */
	int code ;
	
	public SetFacCodeCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public SetFacCodeCmd(EmberManager emgr,int code)
	{
		super(emgr) ;
		this.code = code ;
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

	
	public int getCode()
	{
		return this.code;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 2;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		DataUtil.setUInt16(this.code, bs, offset);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		this.code = DataUtil.getUInt16(bs, offset) ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("code",this.code) ;
	}

}
