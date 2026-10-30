package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

/**
 * Allows the Host to change the policies used by the NCP to make fast decisions.
 * 
 * @author jason.zhu
 */
public class SetPolicyCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0055 ;
	public static final String FRAME_NAME = "setPolicy" ;
	/**
	 * sl_zigbee_ezsp_policy_id_t	uint8_t	Identifies a policy
	 * 
		sl_zigbee_ezsp_policy_id_t policyId  Identifies which policy to modify.
	 */
	int policyId ;
	
	/**
	 * sl_zigbee_ezsp_decision_id_t	uint8_t	Identifies a policy decision.
	 * sl_zigbee_ezsp_decision_id_t decisionId The new decision for the specified policy.
	 */
	int decisionId ;
	
	public SetPolicyCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public SetPolicyCmd(EmberManager emgr,int policy_id,int decision_id)
	{
		super(emgr) ;
		this.policyId = policy_id ;
		this.decisionId = decision_id;
	}
	
	@Override
	public int getFrmID()
	{
		return FRAME_ID;
	}

	@Override
	public String getFrmName()
	{
		return FRAME_NAME ;
	}

	
	public int getPolicyId()
	{
		return this.policyId ;
	}
	
	public int getDecisionId()
	{
		return this.decisionId ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 2;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		bs[offset] = (byte)this.policyId ;
		bs[offset+1] = (byte)this.decisionId ;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<2)
			return false;
		this.policyId = bs[offset] & 0xFF ;
		this.decisionId = bs[offset+1] & 0xFF ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("policy_id",this.policyId).put("decision_id", this.decisionId) ;
	}
}
