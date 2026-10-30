package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.ember.EZSPReqRsp;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.SLStatus_Rsp;
import org.iottree.driver.zigbee.ember.ezsp.SetPolicyCmd;
import org.iottree.driver.zigbee.ember.types.EmberJoinDecision;
import org.iottree.driver.zigbee.ember.types.EzspDecisionBitmask;
import org.iottree.driver.zigbee.ember.types.EzspDecisionId;
import org.iottree.driver.zigbee.ember.types.EzspPolicyId;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

public class SetPolicyRR extends EZSPReqRsp
{
	SetPolicyCmd spCmd = null;
	SLStatus_Rsp slstRsp = null;

	public SetPolicyRR(EmberManager em,int policy_id,int decision_id)
	{
		super(em);

		spCmd = new SetPolicyCmd(em, policy_id,decision_id);
		this.asReq(spCmd, false, 5000);
	}

	@Override
	protected boolean RT_chkRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		if (m_recv instanceof SLStatus_Rsp)
		{
			this.slstRsp = (SLStatus_Rsp) m_recv;
			return true;
		}
		return false;
	}

	@Override
	protected boolean RT_chkFlwRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		return false;
	}
	
	public boolean isSLStatusOk()
	{
		if(this.slstRsp==null)
			return false ;
		return this.slstRsp.getSLStatus().isOk() ;
	}

	@Override
	protected JSONObject toRetJO()
	{
		return null;
	}

	public static boolean ezspSetPolicyVal(EmberManager em, EzspPolicyId pid, int decision_id, StringBuilder failedr)
			throws Exception
	{
		SetPolicyRR gvrr = new SetPolicyRR(em, pid.getIntVal(), decision_id);
		if (!em.RT_synReqRsp(gvrr, failedr))
			return false;
		return gvrr.slstRsp.getSLStatus().isOk();
	}
	
	public static SLStatus setPolicyVal(EmberManager em, EzspPolicyId pid, int decision_id)
			throws Exception
	{
		SetPolicyRR gvrr = new SetPolicyRR(em, pid.getIntVal(), decision_id);
		if (!em.RT_synReqRsp(gvrr, null))
			return null;
		return gvrr.slstRsp.getSLStatus();
	}

	static class ItemVal
	{
		EzspPolicyId item_id;

		int val;

		ItemVal(EzspPolicyId itemid, int val)
		{
			this.item_id = itemid;
			this.val = val;
		}
	}

//	private static ItemVal[] POLICY_IDS_PRE_V8 = new ItemVal[] {
//			new ItemVal(EzspPolicyId.APP_KEY_REQUEST_POLICY, EzspDecisionId.DENY_APP_KEY_REQUESTS.getIntVal()),
//			new ItemVal(EzspPolicyId.TC_KEY_REQUEST_POLICY, EzspDecisionId.ALLOW_TC_KEY_REQUESTS.getIntVal())
//			
//	};
//
//	/**
//	 * Can only decrease "NCP Memory Allocation" configs at runtime from V9 on.
//	 * 
//	 * @see https://www.silabs.com/documents/public/release-notes/emberznet-release-notes-7.0.1.0.pdf
//	 */
//	private static ItemVal[] POLICY_IDS_CURRENT = new ItemVal[] {
//			new ItemVal(EzspPolicyId.APP_KEY_REQUEST_POLICY, EzspDecisionId.DENY_APP_KEY_REQUESTS.getIntVal()),
//			new ItemVal(EzspPolicyId.TC_KEY_REQUEST_POLICY, EzspDecisionId.ALLOW_TC_KEY_REQUESTS.getIntVal()),
//			new ItemVal(EzspPolicyId.TRUST_CENTER_POLICY, EzspDecisionBitmask.ALLOW_UNSECURED_REJOINS.getIntVal() | EzspDecisionBitmask.ALLOW_JOINS.getIntVal())
//	};
//
//	public static int RT_updatePolices(EmberManager em)
//	{
//		ItemVal[] configs = em.getEZSPVersion() < 8 ? POLICY_IDS_PRE_V8 : POLICY_IDS_CURRENT;
//		int r = 0 ;
//		for (ItemVal cv : configs)
//		{
//			StringBuilder failedr = new StringBuilder();
//			try
//			{
//				if(ezspSetPolicyVal(em, cv.item_id, cv.val, failedr))
//					r ++ ;
//			}
//			catch ( Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//		return r ;
//	}


	   /**
	    * Set the trust center policy bitmask using decision.
	    * @param decision
	 * @throws Exception 
	    * @returns
	    */
	   public static SLStatus setJoinPolicy(EmberManager em,EmberJoinDecision decision) throws Exception
	   {
	       int policy = EzspDecisionBitmask.DEFAULT_CONFIGURATION.getIntVal();

	       switch (decision)
	       {
	           case USE_PRECONFIGURED_KEY: {
	               policy = EzspDecisionBitmask.ALLOW_JOINS.getIntVal() | EzspDecisionBitmask.ALLOW_UNSECURED_REJOINS.getIntVal();
	               break;
	           }
	           case ALLOW_REJOINS_ONLY: {
	               policy = EzspDecisionBitmask.ALLOW_UNSECURED_REJOINS.getIntVal();
	               break;
	           }
	           /*case EmberJoinDecision.SEND_KEY_IN_THE_CLEAR: {
	               policy = EzspDecisionBitmask.ALLOW_JOINS | EzspDecisionBitmask.ALLOW_UNSECURED_REJOINS | EzspDecisionBitmask.SEND_KEY_IN_CLEAR;
	               break;
	           }*/
	       }

	       return setPolicyVal(em,EzspPolicyId.TRUST_CENTER_POLICY, policy);
	   }
}
