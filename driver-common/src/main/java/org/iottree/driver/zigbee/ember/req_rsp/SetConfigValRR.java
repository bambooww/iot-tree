package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.ember.EZSPReqRsp;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.SetConfigValCmd;
import org.iottree.driver.zigbee.ember.ezsp.SetConfigValRsp;
import org.iottree.driver.zigbee.ember.types.EzspConfigId;
import org.iottree.driver.zigbee.ember.types.ZdoConfigFlags;
import org.json.JSONObject;

public class SetConfigValRR extends EZSPReqRsp
{
	SetConfigValCmd scvCmd = null;
	SetConfigValRsp scvRsp = null;

	public SetConfigValRR(EmberManager em, int conf_id, int v)
	{
		super(em);

		scvCmd = new SetConfigValCmd(em, conf_id, v);
		this.asReq(scvCmd, false, 5000);
	}

	@Override
	protected boolean RT_chkRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		if (m_recv instanceof SetConfigValRsp)
		{
			this.scvRsp = (SetConfigValRsp) m_recv;
			return true;
		}
		return false;
	}

	@Override
	protected boolean RT_chkFlwRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		return false;
	}

	@Override
	protected JSONObject toRetJO()
	{
		return null;
	}

}
