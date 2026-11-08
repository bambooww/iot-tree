package org.iottree.driver.zigbee.zspec.zdo;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;
import org.json.JSONObject;

public class NwkUnsolicEnhancedUpdateResp extends ZDOResp
{
	public ZDOStatus status ;
	
	public long ch_in_use;
	
	public int mac_tx_ucast_total ;
	
	public int mac_tx_ucast_fails ;
	
	public int mac_tx_ucast_retries ;
	
	public int time_period ;
	
	public NwkUnsolicEnhancedUpdateResp()
	{
		
	}
	
	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return ZDOClusterId.NWK_UNSOLICITED_ENHANCED_UPDATE_RESPONSE;
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
        	this.ch_in_use = buf.readUInt32();
            this.mac_tx_ucast_total = buf.readUInt16();
            this.mac_tx_ucast_fails = buf.readUInt16();
            this.mac_tx_ucast_retries = buf.readUInt16();
            this.time_period = buf.readUInt8();
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
				.putOpt("status", this.status).put("ch_in_use", this.ch_in_use)
				.put("mac_tx_ucast_total", this.mac_tx_ucast_total).put("mac_tx_ucast_fails",this.mac_tx_ucast_fails).put("mac_tx_ucast_retries",this.mac_tx_ucast_retries);
		
		r.putOpt("time_period", this.time_period) ;
		return r ;
	}
	
	@Override
	public String toString()
	{
		String r = super.toString() ;
		r += "status="+this.status+" ch_in_use="+this.ch_in_use ;
			
		return r ;
	}
}
