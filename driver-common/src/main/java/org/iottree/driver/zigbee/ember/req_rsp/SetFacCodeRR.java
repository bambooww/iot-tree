package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.ember.EZSPReqRsp;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.SLStatus_Rsp;
import org.iottree.driver.zigbee.ember.ezsp.SetFacCodeCmd;
import org.json.JSONObject;

public class SetFacCodeRR extends EZSPReqRsp
{
	SetFacCodeCmd spCmd = null;
	SLStatus_Rsp slstRsp = null;

	public SetFacCodeRR(EmberManager em,int code)
	{
		super(em);

		spCmd = new SetFacCodeCmd(em, code);
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

	public static boolean setFacCode(EmberManager em, int code)
			throws Exception
	{
		SetFacCodeRR gvrr = new SetFacCodeRR(em, code);
		if (!em.RT_synReqRsp(gvrr, null))
			return false;
		return gvrr.slstRsp.getSLStatus().isOk();
	}
}
