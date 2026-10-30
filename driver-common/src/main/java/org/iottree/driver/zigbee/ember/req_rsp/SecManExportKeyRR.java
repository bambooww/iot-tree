package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.ember.EZSPReqRsp;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.SecManExportKeyCmd;
import org.iottree.driver.zigbee.ember.ezsp.SecManExportKeyRsp;
import org.iottree.driver.zigbee.ember.types.SecManContext;
import org.json.JSONObject;

public class SecManExportKeyRR extends EZSPReqRsp
{
	SecManExportKeyCmd gvCmd = null ;
	SecManExportKeyRsp gvRsp = null ;
	
	public SecManExportKeyRR(EmberManager em,SecManContext cxt)
	{
		super(em) ;
		
		gvCmd = new SecManExportKeyCmd(em,cxt);
		this.asReq(gvCmd, false, 5000) ;
	}
	
	@Override
	protected boolean RT_chkRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		if(m_recv instanceof SecManExportKeyRsp)
		{
			this.gvRsp = (SecManExportKeyRsp)m_recv ;
			return true ;
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

	public SecManExportKeyRsp getExportKeyRsp()
	{
		return this.gvRsp ;
	}
	
}
