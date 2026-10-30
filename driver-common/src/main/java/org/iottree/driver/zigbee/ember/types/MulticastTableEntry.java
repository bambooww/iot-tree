package org.iottree.driver.zigbee.ember.types;

import org.iottree.driver.zigbee.DataUtil;
import org.json.JSONObject;

public class MulticastTableEntry
{
	/** The multicast group ID. */
    public int multicastId;
    /** The endpoint that is a member, or 0 if this entry is not in use (the ZDO is not a member of any multicast groups). uint8_t */
    public int endpoint;
    /** The network index of the network the entry is related to. uint8_t */
    public int networkIndex;
    
    public MulticastTableEntry()
    {}
    
    public MulticastTableEntry(int multicast_id,int ep,int nwk_idx)
    {
    	this.multicastId = multicast_id ;
    	this.endpoint = ep ;
    	this.networkIndex = nwk_idx ;
    }
    
    public int pack(byte[] bs,int offset)
	{
		DataUtil.setUInt16(this.multicastId,bs, offset) ; //0-1
		bs[offset+2] = (byte)this.endpoint ; //
		bs[offset+3] = (byte)this.networkIndex ; //
		return 4 ;
	}
	
	public int unpack(byte[] bs,int offset)
	{
		this.multicastId = DataUtil.getUInt16(bs, offset) ; //0-1
		this.endpoint = bs[offset+2] & 0xFF; //
		this.networkIndex = bs[offset+3] & 0xFF; //
		return 4 ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().put("multicast_id", this.multicastId)
				.put("endpoint", this.endpoint).put("nwk_idx", this.networkIndex) ;
	}
	
	public String toString()
	{
		return "multicast_id"+this.multicastId+";endpoint:"+this.endpoint+";nwk_idx:"+this.networkIndex ;
	}
	
	public static final int STRUCT_LEN = 4 ;
}
