package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.msg_sys.VersionSREQ;
import org.iottree.driver.zigbee.zstack.msg_sys.VersionSRSP;
import org.json.JSONObject;

public class Version_CMD extends MsgReqRsp
{
	VersionSRSP respRSP = null ;
	
	public Version_CMD()
	{
		super();
		VersionSREQ req = new VersionSREQ() ;
		this.asReq(req, false, 5000) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof VersionSRSP))
		{
			failedr.append("not VersionSRSP response") ;
			return false;
		}
		respRSP = (VersionSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		return false;
	}
	
	public VersionSRSP getVersionRsp()
	{
		return this.respRSP ;
	}

	@Override
	protected JSONObject toRetJO()
	{
		JSONObject ret = new JSONObject();
		if(respRSP!=null)
		{
			ret.put("trans_rev",respRSP.getTransportRev()) ;
			ret.put("product_id",respRSP.getProductId()) ;
			ret.put("majorrel",respRSP.getMajorRel()) ;
			ret.put("minorrel",respRSP.getMinorRel()) ;
		}
		return ret ;
	}


}
