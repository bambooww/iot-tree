package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.msg_sys.*;
import org.json.JSONObject;

public class GetLenNV extends MsgReqRsp
{
	OsalNVLengthSRSP respNVLen = null ;
	
	protected byte[] respNVData = null ;

	public GetLenNV(int nv_itemid)
	{
		super();
		OsalNVLengthSREQ req = new OsalNVLengthSREQ(nv_itemid) ;
		this.asReq(req, false, 200) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof OsalNVLengthSRSP))
		{
			failedr.append("not OsalNVReadExtSRSP response") ;
			return false;
		}
		respNVLen = (OsalNVLengthSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		return false;
	}
	
	public int getLen()
	{
		if(respNVLen==null)
			return -1 ;
		return respNVLen.getNVLength() ;
	}

	@Override
	protected JSONObject toRetJO()
	{
		return new JSONObject().put("nv_len", this.getLen());
	}
}
