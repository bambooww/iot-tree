package org.iottree.driver.zigbee.zcl.util;

import org.iottree.driver.zigbee.DataUtil;
import org.json.JSONObject;

public class AttrStatus
{
	int status ; // 1byte
	
	int dir ; // 1byte
	
	int attr_id ; //2bytes
	
	public int getStatus()
	{
		return this.status ;
	}
	
	public int getDirection()
	{
		return this.dir ;
	}
	
	public boolean isDirReported()
	{
		return this.dir==0x00 ;
	}
	
	public boolean isDirReceived()
	{
		return this.dir==0x01 ;
	}
	
	public int getAttrId()
	{
		return this.attr_id ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().put("status",this.status).put("dir", this.dir).put("attr_id",this.attr_id) ;
	}
	
	public void packTo(byte[] data,int offset)
	{
		if(data.length-offset<4)
			throw new IllegalArgumentException("data offset len < 4") ;
		data[offset] = (byte)status ;
		data[offset+1] = (byte)dir ;
		DataUtil.setUInt16(this.attr_id, data, offset+2); //2-3
	}
	
	public int parseFrom(byte[] data,int offset)
	{
		if(data.length-offset<4)
			return -1 ;
		this.status = data[offset] & 0xFF ;
		this.dir = data[offset+1] & 0xFF ;
		this.attr_id = DataUtil.getUInt16(data, offset+2) ; // 2-3
		return 4 ;
	}
}
