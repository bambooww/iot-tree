package org.iottree.driver.zigbee.zspec.util;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.json.JSONObject;

public class BindingTableEntry
{
	/** The source IEEE address for the binding entry. */
    public String src_ieee ;//sourceEui64: Eui64;
    /** The source endpoint for the binding entry. */
    public int src_ep ; //sourceEndpoint: number;
    /** The identifier of the cluster on the source device that is bound to the destination device. */
    public  int clusterId ; //: ClusterId;
    /**
     * The addressing mode for the destination address. This field can take one of the non-reserved values from the following list:
     *   - 0x00 = reserved
     *   - 0x01 = 16-bit group address for DstAddr and DstEndpoint not present
     *   - 0x02 = reserved
     *   - 0x03 = 64-bit extended address for DstAddr and DstEndp present
     *   - 0x04 – 0xff = reserved
     */
    public int destAddrMode ; //: number;
    
    /** The destination address for the binding entry. 2-byte or 8-byte */
    public Integer dest_addr_nd = null ; //: NodeId | Eui64;
    public String dest_addr_ieee = null ;
    /**
     * This field shall be present only if the DstAddrMode field has a value of 0x03 and, if present,
     * shall be the destination endpoint for the binding entry.
     */
    public  Integer dest_ep = null ; //destEndpoint?: number;
    
    public JSONObject toJO()
   	{
   		return new JSONObject().put("src_ieee", this.src_ieee).putOpt("cluster_id", this.clusterId)
   				.put("destAddrMode", this.destAddrMode).put("src_ep", this.src_ep).putOpt("dest_addr_nd", dest_addr_nd)
   				.put("dest_addr_ieee", this.dest_addr_ieee).putOpt("dest_ep", this.dest_ep);
   	}
   	
   	public String toString()
   	{
   		return "src_ieee="+this.src_ieee+" cluster_id="+this.clusterId+" src_ep="+this.src_ep ;
   	}
       
       public static BindingTableEntry parseFrom(ZDOBuffer buf)
       {
    	   BindingTableEntry r = new BindingTableEntry() ;
    	   r.src_ieee = buf.readIEEEAddr() ;
           r.src_ep = buf.readUInt8();
           r. clusterId = buf.readUInt16();
           r. destAddrMode = buf.readUInt8();
           
           if(r.destAddrMode == 0x01)
        	   r.dest_addr_nd = buf.readUInt16();
           else if(r.destAddrMode == 0x03)
           {
        	   r.dest_addr_ieee = buf.readIEEEAddr() ;
        	   r.dest_ep = buf.readUInt8() ;
           }
           else
        	   return null ;
           
       		return r ;
       }
}
