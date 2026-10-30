package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.EzspExtValueId;
import org.json.JSONObject;

/**
 * Reads a value from the NCP but passes an extra argument specific to the value being retrieved.
 * 
 * @author jason.zhu
 */
public class GetExtValueCmd extends EZSPCmd
{
	/**
	 * sl_zigbee_ezsp_extended_value_id_t	uint8_t
	 * 
		sl_zigbee_ezsp_extended_value_id_t valueId  Identifies which extended value ID to read.
	 */
	EzspExtValueId extValueId ;
	
	/**
	 * uint32_t characteristics
	 * Identifies which characteristics of the extended value ID to read. 
	 * These are specific to the value being read.
	 */
	long characteristics ;
	
	public GetExtValueCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public GetExtValueCmd(EmberManager emgr,EzspExtValueId ext_val_id,long character_id)
	{
		super(emgr) ;
		this.extValueId = ext_val_id ;
		this.characteristics = character_id ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x0003;
	}

	@Override
	public String getFrmName()
	{
		return "getExtendedValue";
	}

	public EzspExtValueId getValueId()
	{
		return this.extValueId ;
	}
	
	public long getCharacteristics()
	{
		return this.characteristics ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 5;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		bs[offset] = (byte)this.extValueId.getIntVal() ;
		DataUtil.setUInt32(this.characteristics, bs, offset+1);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.extValueId = EzspExtValueId.valOfInt(bs[offset] & 0xFF) ;
		this.characteristics = DataUtil.getUInt32(bs, offset+1) ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().putOpt("ext_val_id",this.extValueId).put("character", this.characteristics) ;
	}

}
