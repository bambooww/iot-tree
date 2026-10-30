package org.iottree.driver.zigbee.zstack.req_rsp;

import java.util.Arrays;
import java.util.List;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.msg_af.RegisterSREQ;
import org.iottree.driver.zigbee.zstack.msg_af.RegisterSRSP;
import org.json.JSONObject;

/**
 * 本机注册endpoint
 * @author jason.zhu
 *
 */
public class RegEndpoint_CMD extends MsgReqRsp
{
	RegisterSRSP respRSP = null ;
	
	protected byte[] respNVData = null ;

	public RegEndpoint_CMD(int endpoint,int profile_id,int device_id)
	{
		super();
		List<Integer> in_cluster_ids = Arrays.asList(0x0000) ;
		List<Integer> out_cluster_ids = Arrays.asList(0x0000) ;
		RegisterSREQ req = new RegisterSREQ(endpoint,profile_id,device_id,0x00,
				0x00,in_cluster_ids,out_cluster_ids);
		this.asReq(req, false, 5000) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof RegisterSRSP))
		{
			failedr.append("not RegisterSRSP response") ;
			return false;
		}
		respRSP = (RegisterSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		return false;
	}
	
	public boolean getStatus()
	{
		if(respRSP==null)
			return false ;
		return respRSP.getStatus() ;
	}
	
	@Override
	protected JSONObject toRetJO()
	{
		JSONObject ret = new JSONObject().put("status", this.getStatus());
		return ret ;
	}

}
