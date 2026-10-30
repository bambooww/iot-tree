package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * Writes a value to the NCP.
 * 
 * @author jason.zhu
 */
public class SetValueCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x00AB ;
	public static final String FRAME_NAME = "setValue" ;
	
	/**
	 * sl_zigbee_ezsp_value_id_t	uint8_t	Identifies a value.
	 * 
		sl_zigbee_ezsp_value_id_t valueId Identifies which value to read.
	 */
	int valueId ;
	
	/**
	 * uint8_t valueLength The length of the value parameter in bytes.
	 */
	byte[] values ;
	
	public SetValueCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public SetValueCmd(EmberManager emgr,int value_id,byte[] vs)
	{
		super(emgr) ;
		this.valueId = value_id ;
		this.values = vs;
		//this.valueLen = value.length ;
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

	
	public int getValueId()
	{
		return this.valueId ;
	}
	
	public byte[] getValues()
	{
		return this.values ;
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
		bs[offset] = (byte)this.valueId ;
		int vlen = 0 ;
		if(this.values!=null)
			vlen = this.values.length ;
		bs[offset+1] = (byte)vlen ;
		if(vlen>0)
			System.arraycopy(this.values, 0, bs, offset+2, vlen);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		this.valueId = bs[offset] & 0xFF ;
		int vlen = bs[offset+1] & 0xFF ;
		if(vlen>0)
		{
			this.values = new byte[vlen] ;
			System.arraycopy(bs, offset+2, this.values, 0, vlen);
		}
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("value_id",this.valueId).putOpt("values", Convert.byteArray2HexStr(this.values, "-")) ;
	}

}
