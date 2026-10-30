package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.Msg;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.json.JSONObject;

public class ReqRsp_CMD<T extends Msg,R extends MsgRecv> extends MsgReqRsp
{
	R respRSP = null ;
	Class<R> respC ;
	
	public ReqRsp_CMD(T req,Class<R> rc)
	{
		super();
		respC = rc ;
		this.asReq(req, false, 5000) ;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!respC.isInstance(m_recv))
		{
			failedr.append("not "+respC.getName()+" response") ;
			return false;
		}
		respRSP = (R)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		return false;
	}
	
	public R getRsp()
	{
		return this.respRSP ;
	}
	

	@Override
	protected JSONObject toRetJO()
	{
		JSONObject ret = new JSONObject();
		if(respRSP!=null)
		{
			
		}
		return ret ;
	}

}
