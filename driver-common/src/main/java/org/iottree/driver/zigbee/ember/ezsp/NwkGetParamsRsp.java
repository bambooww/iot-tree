package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.iottree.driver.zigbee.ember.types.EmberNodeType;
import org.iottree.driver.zigbee.ember.types.NwkParams;
import org.json.JSONObject;

/**
 * Returns the current network parameters.
 * 
 * @author jason.zhu
 */
public class NwkGetParamsRsp extends EZSPRsp
{
	/**
	 * sl_status_t status  An sl_status_t value indicating success or the reason for failure.
	 */
	SLStatus sl_status ;
	
	/**
	 * sl_zigbee_node_type_t 	uint8_t	The type of the node.
	 * An sl_zigbee_node_type_t value indicating the current node type.
	 */
	int node_type ;
	
	NwkParams params ;
	
	public NwkGetParamsRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return NwkGetParamsCmd.FRAME_ID ;
	}

	@Override
	public String getFrmName()
	{
		return NwkGetParamsCmd.FRAME_NAME ;
	}
	

	public SLStatus getStatus()
	{
		return this.sl_status ;
	}
	
	public int getNodeType()
	{
		return this.node_type ;
	}
	
	public EmberNodeType getEmberNodeType()
	{
		return EmberNodeType.valOfInt(this.node_type) ;
	}
	
	public NwkParams getNwkParams()
	{
		return this.params ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 2+NwkParams.STRUCT_LEN ;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<1)
			return false;
		int wlen = UTIL_writeStatus(this.owner.getEZSPVersion(), this.sl_status.getIntVal(), bs, offset) ;
		bs[offset+wlen] = (byte)this.node_type;
		if(params!=null)
			this.params.pack(bs, offset+wlen+1) ;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<20)
			return false;
		SLStatus[] rets= new SLStatus[1] ;
		int rlen = UTIL_readStatus(this.owner.getEZSPVersion(), bs, offset, rets) ;
		this.sl_status = rets[0] ;
		this.node_type = bs[offset+rlen] ;
		this.params = new NwkParams() ;
		return this.params.unpack(bs, offset+rlen+1) ;
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO().put("status",this.sl_status).put("node_type", this.node_type);
		if(this.params!=null)
			jo.put("nwk_params", this.params.toJO()) ;
		return jo ;
	}

}
