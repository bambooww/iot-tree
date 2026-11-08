package org.iottree.driver.zigbee.zspec.zdo;

import java.util.ArrayList;
import java.util.List;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;
import org.json.JSONObject;

public class NwkEnhancedUpdateResp extends ZDOResp
{
	public ZDOStatus status ;
	
	public long scanned_chs;
	
	public int total_trans ;
	
	public int total_fails ;
	
	public int entry_c ;
	
	public List<Integer> entry_list = new ArrayList<>() ;
	
	public NwkEnhancedUpdateResp()
	{
		
	}
	
	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return ZDOClusterId.NWK_ENHANCED_UPDATE_RESPONSE;
	}

	@Override
	public void packTo(ZDOBuffer buf)
	{
		throw new RuntimeException("no impl") ;
	}

	@Override
	protected void unpackBuf(ZDOBuffer buf)
	{
		ZDOStatus status = ZDOStatus.valOfInt(buf.readUInt8());
        if (status == ZDOStatus.SUCCESS) {
        	this.scanned_chs = buf.readUInt32();
            this.total_trans = buf.readUInt16();
            this.total_fails = buf.readUInt16();
            // [0x00-0xFF]
            this.entry_c = buf.readUInt8();
            this.entry_list = buf.readListUInt8(entry_c);
        }
	}

	@Override
	public ZDOStatus getStatus()
	{
		return status;
	}
	

	public JSONObject toJO()
	{
		JSONObject r = new JSONObject().put("clusterid", this.getClusterId()).put("cluster_n",this.getClusterName())
				.putOpt("status", this.status).put("scanned_chs", this.scanned_chs)
				.put("total_trans", this.total_trans).put("total_fails",this.total_fails).put("entry_c",this.entry_c);
		
		r.putOpt("entry_list", this.entry_list) ;
		return r ;
	}
	
	@Override
	public String toString()
	{
		String r = super.toString() ;
		r += "status="+this.status+" scanned_chs="+this.scanned_chs ;
			
		return r ;
	}

}
