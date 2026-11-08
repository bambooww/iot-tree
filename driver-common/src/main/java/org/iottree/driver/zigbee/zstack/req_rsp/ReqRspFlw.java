package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.Msg;
import org.iottree.driver.zigbee.zstack.Msg.CmdTp;
import org.iottree.driver.zigbee.zstack.MsgData;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.json.JSONObject;

public class ReqRspFlw extends MsgReqRsp
{
	MsgRecv respRSP = null ;
	MsgRecv flwRSP = null ;
	
	
	private CmdTp respCmdTp = null ;
	
	private Msg.SubSys respSubSys = null ;
	
	private int respCmd1Id = -1 ;
	
	public ReqRspFlw(MsgData req,boolean has_flw)
	{
		super();
		
		this.asReq(req, has_flw, 5000) ;
	}
	
	public ReqRspFlw asRspFilter(Msg.CmdTp resp_comtp,Msg.SubSys sub_sys,int cmd1id)
	{
		this.respCmdTp = resp_comtp ;
		this.respSubSys = sub_sys ;
		this.respCmd1Id = cmd1id ;
		return this ;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(this.respCmdTp!=null)
		{
			if(this.respCmdTp!=m_recv.getCmdTp())
				return false;
		}
		if(this.respSubSys!=null)
		{
			if(this.respSubSys!=m_recv.getSubSys())
				return false;
		}
		if(this.respCmd1Id>=0)
		{
			if(this.respCmd1Id!=m_recv.getCmd1())
				return false;
		}
		
		respRSP = m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		return false;
	}
	
	public MsgRecv getRsp()
	{
		return this.respRSP ;
	}
	
	public MsgRecv getFlw()
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
