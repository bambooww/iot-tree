package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.ember.EZSPReqRsp;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.VersionCmd;
import org.iottree.driver.zigbee.ember.ezsp.VersionRsp;
import org.json.JSONObject;

public class VersionRR extends EZSPReqRsp
{
	VersionCmd verCmd = null ;
	VersionRsp verRsp = null ;
	
	public VersionRR(EmberManager em,int desired_ver)
	{
		super(em) ;
		
		verCmd = new VersionCmd(em,desired_ver) ;
		this.asReq(verCmd, false, 5000) ;
	}
	
	@Override
	protected boolean RT_chkRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		if(m_recv instanceof VersionRsp)
		{
			this.verRsp = (VersionRsp)m_recv ;
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

	public static VersionRsp ezspGetVersion(EmberManager em,int desired_ver,StringBuilder failedr) throws Exception
	{
		VersionRR vrr = new VersionRR(em,desired_ver) ;
		if(!em.RT_synReqRsp(vrr, failedr))
			return null ;
		return vrr.verRsp ;
	}
}
