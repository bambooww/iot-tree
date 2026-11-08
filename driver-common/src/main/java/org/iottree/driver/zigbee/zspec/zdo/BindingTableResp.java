package org.iottree.driver.zigbee.zspec.zdo;

import java.util.ArrayList;
import java.util.List;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;
import org.iottree.driver.zigbee.zspec.util.BindingTableEntry;
import org.json.JSONArray;
import org.json.JSONObject;

public class BindingTableResp extends ZDOResp
{
	public ZDOStatus status ;
	
	public int binding_tb_entries ;
	
	public int start_idx ;
	
	public int entry_c ;
	
	public List<BindingTableEntry> entry_list = new ArrayList<>() ;
	
	public BindingTableResp()
	{
		
	}
	
	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return ZDOClusterId.BINDING_TABLE_RESPONSE;
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
        	this.binding_tb_entries = buf.readUInt8();
            this.start_idx = buf.readUInt8();
            this.entry_c = buf.readUInt8() ;
            for(int i = 0 ; i < entry_c ; i ++)
            {
            	BindingTableEntry te = BindingTableEntry.parseFrom(buf) ;
            	this.entry_list.add(te) ;
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
				.put("status_ok", this.getStatus()).put("binding_tb_entries", this.binding_tb_entries)
				.put("start_idx", this.start_idx);
		JSONArray lqi_table = new JSONArray() ;
		r.put("lqi_table", lqi_table) ;
		for(BindingTableEntry te:this.entry_list)
		{
			lqi_table.put(te.toJO()) ;
		}
		return r ;
	}
	
	@Override
	public String toString()
	{
		String r = super.toString() ;
		r += "binding_tb_entries="+this.binding_tb_entries+" idx="+this.start_idx ;
			
		for(BindingTableEntry te:this.entry_list)
			r += "\r\n    "+te.toString() ;
		return r ;
	}
}
