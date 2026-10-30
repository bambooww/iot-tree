package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * Read attribute data on NCP endpoints.
 * 
 * @author jason.zhu
 */
public class AttrReadCmd extends EZSPCmd
{
	/**
	 * sl_zigbee_ezsp_value_id_t	uint8_t	Identifies a value.
	 * 
		sl_zigbee_ezsp_value_id_t valueId Identifies which value to read.
	 */
	int endpoint ;
	
	int cluster ;
	
	int attr_id ;
	
	int mask ;
	
	int manufacturer_code ;
	
	public AttrReadCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public AttrReadCmd(EmberManager emgr,int endpoint,int cluster,int attr_id,int mask,int m_code)
	{
		super(emgr) ;
		this.endpoint = endpoint ;
		this.cluster = cluster ;
		this.attr_id = attr_id;
		this.mask = mask;
		this.manufacturer_code =m_code;
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

	public int getEndpoint()
	{
		return this.endpoint ;
	}
	
	public int getCluster()
	{
		return this.cluster ;
	}
	
	public int getAttrID()
	{
		return this.attr_id ;
	}
	
	public int getMask()
	{
		return this.mask ;
	}
	
	public int getManufacturerCode()
	{
		return this.manufacturer_code ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 8;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		bs[offset] = (byte)this.endpoint ;
		DataUtil.setUInt16(this.cluster, bs, offset+1); //1-2
		DataUtil.setUInt16(this.attr_id, bs, offset+3); //3-4
		bs[offset+5] = (byte)this.mask ;
		DataUtil.setUInt16(this.manufacturer_code, bs, offset+6); //6-7
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.endpoint = bs[offset] & 0xFF ;
		this.cluster = DataUtil.getUInt16(bs, offset+1) ; //1-2
		this.attr_id = DataUtil.getUInt16(bs, offset+3) ; //3-4
		this.mask = bs[offset+5] & 0xFF ;
		this.manufacturer_code = DataUtil.getUInt16(bs, offset+6) ; //6-7
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("epid",this.endpoint).put("clusterid", this.cluster).put("attr_id", this.attr_id)
				.put("mask", this.mask).put("fac_code", this.manufacturer_code);
	}
}
