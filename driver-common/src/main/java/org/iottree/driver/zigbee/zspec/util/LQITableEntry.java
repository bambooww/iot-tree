package org.iottree.driver.zigbee.zspec.util;

import org.iottree.driver.zigbee.DataUtil;
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
	private byte[] data = null ;
	
	public LQITableEntry(byte[] dd)
	{
		if(dd==null || dd.length!=22)
			throw new IllegalArgumentException("data len!=22") ;
		this.data = dd ;
	}
	
	public String getPanId()
	{
		return DataUtil.transPanId(data, 0) ;
	}
	
	public String getIEEEAddr()
	{
		return DataUtil.transIEEEAddr(data, 8) ; //8-15
	}
	
	public int getNwkArr() //node id ,short addr
	{
		return DataUtil.getUInt16(data, 16) ; //16-17
	}
	
	public int getDeviceType()
	{
		return this.data[18] & 0xFF ;
	}
	
	public int getPermitJoining()
	{
		return this.data[19] & 0xFF ;
	}
	
	public int getDepth()
	{
		return this.data[20] & 0xFF ;
	}
	
	public int getLQI()
	{
		return this.data[21] & 0xFF ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().put("pan_id", this.getPanId()).putOpt("ieee_addr", this.getIEEEAddr())
				.put("nwk_addr", this.getNwkArr()).put("device_tp", this.getDeviceType()).put("permit_join", this.getPermitJoining())
				.put("depth", this.getDepth()).put("lqi", this.getLQI()) ;
	}
	
	public String toString()
	{
		return "PanId="+this.getPanId()+" IEEE="+this.getIEEEAddr()+" NwkAddr="+this.getNwkArr() ;
	}
}
