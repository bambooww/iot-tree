package org.iottree.driver.zigbee.ember.types;

import org.iottree.driver.zigbee.DataUtil;
import org.json.JSONObject;

public class ApsFrame
{
    public int profileId; //uint16_t
    public int clusterId; //uint16_t
    public int src_ep ; //uint8_t
    public int dst_ep; //uint8_t
    
    //ApsOption bitmask
    public int options ; //uint16
    public int groupId; //uint16
    public int sequence; //uint8
    
    // radius
    public Integer radius = null ;

    public int pack(byte[] bs,int offset)
	{
		DataUtil.setUInt16(this.profileId, bs, offset);//0-1
		DataUtil.setUInt16(this.clusterId, bs, offset+2); //2-3
		bs[offset+4] = (byte)src_ep;
		bs[offset+5] = (byte)dst_ep;
		DataUtil.setUInt16(this.options,bs, offset+6) ; //6-7
		DataUtil.setUInt16(this.groupId,bs, offset+8) ; //8-9
		bs[offset+10] =(byte)this.sequence ;
		return 11 ;
	}
	
	public int unpack(byte[] bs,int offset)
	{
		this.profileId = DataUtil.getUInt16(bs, offset);//0-1
		this.clusterId = DataUtil.getUInt16(bs, offset+2); //2-3
		this.src_ep = bs[offset+4] & 0xFF;
		this.dst_ep = bs[offset+5] & 0xFF;
		this.options = DataUtil.getUInt16(bs, offset+6) ; //6-7
		this.groupId = DataUtil.getUInt16(bs, offset+8) ; //8-9
		this.sequence = bs[offset+10] & 0xFF ;
		return 11 ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().putOpt("profile_id", profileId)
				.put("cluster_id", this.clusterId)
				.put("src_ep", this.src_ep).put("dst_ep", this.dst_ep)
				.put("options", this.options).putOpt("group_id", this.groupId)
				.put("seq", this.sequence) ;
	}
	
	public static final int STRUCT_LEN = 11 ;
}
