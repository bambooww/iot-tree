package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.Msg;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.json.JSONObject;

public class ReqRspFlw_CMD<T extends Msg,R extends MsgRecv,F extends MsgRecv> extends MsgReqRsp
{
	R respRSP = null ;
	F flwRSP = null ;
	Class<R> respC ;
	
	Class<F> flwC =null ;
	
	public ReqRspFlw_CMD(T req,Class<R> rc,Class<F> flw_c)
	{
		super();
		
		respC = rc ;
		flwC = flw_c ;
		this.asReq(req, flw_c!=null, 5000) ;
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
		if(flwC==null)
			return false;
		if(!flwC.isInstance(m_recv))
		{
			failedr.append("not "+flwC.getName()+" response") ;
			return false;
		}
		flwRSP = (F)m_recv;
		return false;
	}
	
	public R getRsp()
	{
		return this.respRSP ;
	}
	
	public F getFlw()
	{
		return this.flwRSP ;
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
