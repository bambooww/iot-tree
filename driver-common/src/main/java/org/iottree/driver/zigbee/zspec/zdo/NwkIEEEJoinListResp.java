package org.iottree.driver.zigbee.zspec.zdo;

import java.util.ArrayList;
import java.util.List;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;
import org.json.JSONObject;

public class NwkIEEEJoinListResp  extends ZDOResp
{
	public ZDOStatus status ;
	
	public int update_id;
	
	public int join_policy ;
	
	public int list_total ;
	
	public int start_idx =-1;
	
	public List<String> ieee_list = new ArrayList<>() ; //ieee
	
	public NwkIEEEJoinListResp()
	{
		
	}
	
	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return ZDOClusterId.NWK_IEEE_JOINING_LIST_RESPONSE;
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
        	this.update_id = buf.readUInt8();
            this.join_policy = buf.readUInt8();
            // [0x00-0xFF]
            this.list_total = buf.readUInt8();
            
            if (this.list_total > 0) {
                this.start_idx = buf.readUInt8();
                int entryCount = buf.readUInt8();
                for (int i = 0; i < entryCount; i++) {
                    String ieee = buf.readIEEEAddr();
                    ieee_list.add(ieee);
                }
            }
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
				.putOpt("status", this.status).put("join_policy", this.join_policy)
				.put("list_total", this.list_total).put("start_idx",this.start_idx)
				.putOpt("ieee_list", this.ieee_list);
		
		return r ;
	}
	
	@Override
	public String toString()
	{
		String r = super.toString() ;
		r += "status="+this.status+" list_total="+this.list_total ;
		for(String te:this.ieee_list)
			r += "\r\n    "+te.toString() ;
		return r ;
	}

}
