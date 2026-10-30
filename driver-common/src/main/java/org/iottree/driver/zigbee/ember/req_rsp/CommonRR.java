package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EZSPReqRsp;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.Common_Cmd;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;
import org.json.JSONObject;

public class CommonRR<R extends EZSPRsp> extends EZSPReqRsp
{
	EZSPCmd spCmd = null;
	//Class<? extends EZSPRsp> comRspC;
	Class<R> comRspC;
	
	R retRsp = null ;

	public CommonRR(EmberManager em, EzspFrameID frm_id, byte[] cmd_bs, Class<R> rsp_c)
	{
		super(em);
		spCmd = new Common_Cmd(em, frm_id, cmd_bs);
		this.comRspC = rsp_c;
		this.asReq(spCmd, false, 5000);
	}

	public CommonRR(EmberManager em, EZSPCmd cmd, Class<R> rsp_c)
	{
		super(em);
		spCmd = cmd;
		this.comRspC = rsp_c;
		this.asReq(spCmd, false, 5000);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean RT_chkRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		if (this.comRspC.isInstance(m_recv))
		{
			retRsp = (R)m_recv ;
			return true;
		}
		return false;
	}

	@Override
	protected boolean RT_chkFlwRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		return false;
	}
	
	public R getReturnRsp()
	{
		return this.retRsp ;
	}

	@Override
	protected JSONObject toRetJO()
	{
		return null;
	}
}
