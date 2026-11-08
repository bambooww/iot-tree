package org.iottree.driver.zigbee.zspec.util;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.json.JSONObject;

/**
 * [extended PAN ID:8] [EUI64:8] [node ID:2]
     *             [device type, RX on when idle, relationship:1]
     *             [permit joining:1] [depth:1] [LQI:1]
     *             
     * The device-type byte has the following fields:
     *
     *      Name          Mask        Values
     *
     *   device type      0x03     0x00 coordinator
     *                             0x01 router
     *                             0x02 end device
     *                             0x03 unknown
     *
     *   rx mode          0x0C     0x00 off when idle
     *                             0x04 on when idle
     *                             0x08 unknown
     *
     *   relationship     0x70     0x00 parent
     *                             0x10 child
     *                             0x20 sibling
     *                             0x30 other
     *                             0x40 previous child
     *   reserved         0x10
     *
     * The permit-joining byte has the following fields
     *
     *      Name          Mask        Values
     *
     *   permit joining   0x03     0x00 not accepting join requests
     *                             0x01 accepting join requests
     *                             0x02 unknown
     *   reserved         0xFC
     *   
 * 
 * @author jason.zhu
 */
public class LQITableEntry
{
	public String ext_pan_id ;
	
	public String ieee_addr ;
	
	public int nwk_addr ;
	
	public int device_tp ;
	
	public int permit_join ;
	
	public int depth ;
	
	public int lqi ;
	
	public LQITableEntry()
	{}
	
	public LQITableEntry(byte[] data)
	{
		if(data==null || data.length!=22)
			throw new IllegalArgumentException("data len!=22") ;
		ext_pan_id = DataUtil.transPanId(data, 0) ; ;
		ieee_addr = DataUtil.transIEEEAddr(data, 8) ; ;
		nwk_addr = DataUtil.getUInt16(data, 16) ;  ;
		device_tp = data[18] & 0xFF;
		permit_join = data[19] & 0xFF  ;
		depth = data[20] & 0xFF ;
		lqi = data[21] & 0xFF ;
	}
	
	
	public JSONObject toJO()
	{
		return new JSONObject().put("ext_pan_id", this.ext_pan_id).putOpt("ieee_addr", this.ieee_addr)
				.put("nwk_addr", this.nwk_addr).put("device_tp", this.device_tp).put("permit_join", permit_join)
				.put("depth", this.depth).put("lqi", this.lqi) ;
	}
	
	public String toString()
	{
		return "ExtPanId="+this.ext_pan_id+" IEEE="+this.ieee_addr+" NwkAddr="+this.nwk_addr ;
	}
	
	public static LQITableEntry parseFrom(ZDOBuffer buf)
	{
		LQITableEntry r = new LQITableEntry() ;
		r.ext_pan_id = buf.readExtendPanId() ;
		r.ieee_addr = buf.readIEEEAddr();
        r.nwk_addr = buf.readUInt16();
        r.device_tp = buf.readUInt8();
        r.permit_join = buf.readUInt8();
        r.depth = buf.readUInt8();
        r.lqi = buf.readUInt8();
        return r ;
	}
}
