package org.iottree.driver.zigbee.zspec.util;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.json.JSONObject;

public class RoutingTableEntry
{
	/** 16-bit network address of this route */
	public int dst_addr ; //: NodeId;
    /**
     * Status of the route
     * 0x0=ACTIVE.
     * 0x1=DISCOVERY_UNDERWAY.
     * 0x2=DISCOVERY_FAILED.
     * 0x3=INACTIVE.
     * 0x4=VALIDATION_UNDERWAY
     * 0x5-0x7=RESERVED
     *
     * 3-bit
     */
    public RoutingTableStatus status ;
    /**
     * A flag indicating whether the device is a memory constrained concentrator
     *
     * 1-bit
     */
    public  int memoryConstrained ;//: number;
    /**
     * A flag indicating that the destination is a concentrator that issued a many-to-one request
     *
     * 1-bit
     */
    public int manyToOne ; //: number;
    /**
     * A flag indicating that a route record command frame should be sent to the destination prior to the next data packet.
     *
     * 1-bit
     */
    public int routeRecordRequired ; //: number;
    /** 2-bit */
    public  int reserved1 ; //: number;
    /** 16-bit network address of the next hop on the way to the destination. */
    public int nextHopAddress ; //: number;
    
    public JSONObject toJO()
	{
		return new JSONObject().put("dst_addr", this.dst_addr).putOpt("status", this.status)
				.put("nextHopAddress", this.nextHopAddress).put("memoryConstrained", this.memoryConstrained).put("manyToOne", manyToOne)
				.put("routeRecordRequired", this.routeRecordRequired);
	}
	
	public String toString()
	{
		return "dst_addr="+this.dst_addr+" status="+this.status+" nextHopAddress="+this.nextHopAddress ;
	}
    
    public static RoutingTableEntry parseFrom(ZDOBuffer buf)
    {
    	RoutingTableEntry r = new RoutingTableEntry() ;
    	r.dst_addr = buf.readUInt16();
        int statusByte = buf.readUInt8();
        r.nextHopAddress = buf.readUInt16();
        
        r.status = RoutingTableStatus.valOfInt(statusByte & 0x07);
        r.memoryConstrained = (statusByte & 0x08) >> 3;
        r.manyToOne = (statusByte & 0x10) >> 4;
        r.routeRecordRequired = (statusByte & 0x20) >> 5;
        r.reserved1= (statusByte & 0xc0) >> 6;
        
    	return r ;
    }
}
