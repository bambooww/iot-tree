package org.iottree.driver.zigbee.zspec.zdo;

import java.util.ArrayList;
import java.util.List;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOCluster;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;
import org.iottree.driver.zigbee.zspec.util.LQITableEntry;
import org.json.JSONArray;
import org.json.JSONObject;

public class LQITableRsp extends ZDOResp
{
	ZDOStatus status ;
	
	int neighborTbEntries ;
	
	int start_idx ;
	
	List<LQITableEntry> tbEntries ;
	/**
     * Response: [transaction sequence number: 1]
     * 			 [status:1]
     *           [neighbor table entries:1] [start index:1]
     *           [entry count:1] [entry:22]*
     *   [entry] = [extended PAN ID:8] [EUI64:8] [node ID:2]
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
     */
	public LQITableRsp()
	{
		
	}
	
	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return ZDOClusterId.LQI_TABLE_RESPONSE;
	}

	@Override
	public void packTo(ZDOBuffer buf)
	{
		
	}

	@Override
	protected void unpackBuf(ZDOBuffer buf)
	{
		
	}

	@Override
	public ZDOStatus getStatus()
	{
		return status;
	}
	

	public int getNeighborTableEntries()
	{
		return this.neighborTbEntries;// this.data[1] & 0xFF ;
	}
	
	public int getStartIdx()
	{
		return this.start_idx ;// this.data[2] & 0xFF ;
	}
	
//	public int getEntryCount()
//	{
//		return  this.data[3] & 0xFF ;
//	}
	
	public List<LQITableEntry> listEntries()
	{
		//int c = getEntryCount() ;
		ArrayList<LQITableEntry> rets = new ArrayList<>() ;
//		for(int i = 0 ; i < c ; i ++)
//		{
//			byte[] bs = new byte[22] ;
//			System.arraycopy(this.data, 4+22*i, bs, 0, 22);
//			rets.add(new LQITableEntry(bs)) ;
//		}
		return rets ;
	}
	
	public JSONObject toJO()
	{
		JSONObject r = new JSONObject().put("clusterid", this.getClusterId()).put("cluster_n",this.getClusterName())
				.put("status_ok", this.getStatus()).put("neighbor_tb_entries", getNeighborTableEntries())
				.put("start_idx", this.getStartIdx());
		JSONArray lqi_table = new JSONArray() ;
		r.put("lqi_table", lqi_table) ;
		for(LQITableEntry te:this.listEntries())
		{
			lqi_table.put(te.toJO()) ;
		}
		return r ;
	}
	
	@Override
	public String toString()
	{
		String r = super.toString() ;
		r += "NeighborTableEntries="+this.getNeighborTableEntries()+" idx="+this.getStartIdx() ;
			
		for(LQITableEntry te:this.listEntries())
			r += "\r\n    "+te.toString() ;
		return r ;
	}

}
