package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPBuffer;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.EmberDeviceUpdate;
import org.iottree.driver.zigbee.ember.types.EmberJoinDecision;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

/**
 * The NCP uses the trust center behavior policy to decide whether to allow a new node to join 
 * the network (part of the trust center pre-join handler). The Host cannot change the current decision
 *  in this post-join callback, but it can change the policy for future decisions using the setPolicy command.
	This frame is a response to the callback command.
 * 
 * @author jason.zhu
 *
 */
public class TCPostJoinHandlerRspCB extends EZSPRsp
{
	int newNodeId ; //sl_802154_short_addr_t
	
	String newNodeIEEE ; //sl_802154_long_addr_t  EUI64
	
	/**
	 * The status of the node: Secure Join/Rejoin, Unsecure Join/Rejoin, Device left.
	 * sl_zigbee_device_update_t status
	 */
	EmberDeviceUpdate status ;
	
	/**
	 * sl_zigbee_join_decision_t policyDecision
	 * An sl_zigbee_join_decision_t reflecting the decision made.
	 */
	EmberJoinDecision policyDeci ;
	
	int parentOfNewNodeId ;//sl_802154_short_addr_t
	
	public TCPostJoinHandlerRspCB(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x0024 ;
	}

	@Override
	public String getFrmName()
	{
		return "trustCenterPostJoinHandler";
	}
	

	public int getNewNodeId()
	{
		return this.newNodeId ;
	}
	
	public String getNewNodeIEEE()
	{
		return this.newNodeIEEE ;
	}
	
	public EmberDeviceUpdate getStatus()
	{
		return this.status ;
	}
	
	public EmberJoinDecision getPolicyDecision()
	{
		return this.policyDeci ;
	}
	
	public int getParentOfNewNodeId()
	{
		return this.parentOfNewNodeId ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 14 ;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		EZSPBuffer buf = new EZSPBuffer(bs,offset,len) ;
		this.newNodeId = buf.readUInt16() ;
		this.newNodeIEEE = buf.readIEEEAddr() ;
		this.status = EmberDeviceUpdate.valOfInt(buf.readUInt8()) ;
		this.policyDeci = EmberJoinDecision.valOfInt(buf.readUInt8()) ;
		this.parentOfNewNodeId = buf.readUInt16() ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("new_nid",this.newNodeId).putOpt("new_ieee", this.newNodeIEEE)
				.putOpt("status", this.status).putOpt("policy_deci", this.policyDeci).put("new_pid", this.parentOfNewNodeId);
	}
}
