package org.iottree.driver.zigbee.zspec.zdo;

import java.util.HashMap;
import java.util.List;

import org.iottree.driver.zigbee.zspec.ZDOBuffer;
import org.iottree.driver.zigbee.zspec.ZDOClusterId;
import org.iottree.driver.zigbee.zspec.ZDOResp;
import org.iottree.driver.zigbee.zspec.ZDOStatus;
import org.iottree.driver.zigbee.zspec.zdo_def.Tlv;
import org.iottree.driver.zigbee.zspec.zdo_def.TlvLocal;
import org.iottree.driver.zigbee.zspec.zdo_def.TlvTag;
import org.json.JSONObject;

public class StartKeyNegotiationResp extends ZDOResp
{
	public ZDOStatus status ;
	
	public List<TlvTag> tlvTags = null ;
	
	public StartKeyNegotiationResp()
	{
		
	}
	
	@Override
	public ZDOClusterId getZDOClusterId()
	{
		return ZDOClusterId.START_KEY_NEGOTIATION_RESPONSE;
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
        	this.tlvTags = buf.readTLVs(loc_rd_map, false) ;
        }
	}
	
	private static Tlv.LocalTLVReader tlv_read_00 = new Tlv.LocalTLVReader() {

		@Override
		public TlvLocal read_tlv_local(ZDOBuffer buf, int len)
		{
			return TlvLocal.readCurve25519PublicPointTLV(buf, len);
		}};
		

	private static HashMap<Integer,Tlv.LocalTLVReader> loc_rd_map = new HashMap<>() ;
	static
	{
		loc_rd_map.put(0x00,tlv_read_00);
	}

	@Override
	public ZDOStatus getStatus()
	{
		return status;
	}
	

	public JSONObject toJO()
	{
		JSONObject r = new JSONObject().put("clusterid", this.getClusterId()).put("cluster_n",this.getClusterName())
				.putOpt("status", this.status).putOpt("tlv_tags", this.tlvTags) ;
		return r ;
	}
	
	@Override
	public String toString()
	{
		String r = super.toString() ;
		r += "status="+this.status+" tlv_tags="+this.tlvTags ;
			
		return r ;
	}
}
