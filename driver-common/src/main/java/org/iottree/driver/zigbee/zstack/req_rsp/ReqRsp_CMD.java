package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.Msg;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.util.ICopyable;
import org.json.JSONObject;

public class ReqRsp_CMD<T extends Msg,R extends MsgRecv> extends MsgReqRsp
{
	R respRSP = null ;
	Class<R> respC = null;
	int[] cmd01 = null;
	
	public ReqRsp_CMD(T req,Class<R> rc)
	{
		this(req,rc,null) ;
	}
	
	public ReqRsp_CMD(T req,Class<R> rc,int[] cmd01)
	{
		super();
		respC = rc ;
		this.cmd01 = cmd01 ;
		this.asReq(req, false, 10000) ;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(cmd01!=null)
		{
			boolean b = m_recv.getCmd0()==cmd01[0] && m_recv.getCmd1()==cmd01[1] ;
			if(b)
			{
				respRSP = (R)m_recv;
				return true ;
			}
		}
		else if(respC!=null)
		{
			if(!respC.isInstance(m_recv))
			{
				failedr.append("not "+respC.getName()+" response") ;
				return false;
			}
			respRSP = (R)m_recv;
		}
		
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
