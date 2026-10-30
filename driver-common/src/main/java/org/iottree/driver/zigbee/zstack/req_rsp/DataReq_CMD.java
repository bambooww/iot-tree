package org.iottree.driver.zigbee.zstack.req_rsp;

import java.util.HashMap;

import org.iottree.driver.zigbee.zcl.ZCLFrame;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.ZStackManager;
import org.iottree.driver.zigbee.zstack.msg_af.DataConfirmAREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_af.DataRequestSREQ;
import org.iottree.driver.zigbee.zstack.msg_af.DataRequestSRSP;
import org.iottree.driver.zigbee.zstack.msg_af.IncomingMsgAREQ_CB;
import org.json.JSONObject;

public class DataReq_CMD extends MsgReqRsp
{
	DataRequestSRSP respRSP = null ;
	
	IncomingMsgAREQ_CB respFlwRSP = null ;
	
	DataConfirmAREQ_CB flwDataConfirm = null ;
	
	//protected byte[] respNVData = null ;

	public DataReq_CMD(ZStackManager zsm,int dst_addr,int dst_ep,
			int src_ep,int cluster_id,ZCLFrame zcl_frm)
	{
		super();
		int trans_id = zsm.get_next_trans_id() ;
		int radius = 30 ;
		zcl_frm.asSeqNum(trans_id) ;
		byte[] zcl_data = zcl_frm.packFrame() ;
		DataRequestSREQ req = new DataRequestSREQ(dst_addr,dst_ep,src_ep,
				cluster_id,trans_id,0x00,radius,zcl_data) ;
//		DataRequestExtSREQ req = new DataRequestExtSREQ(dst_addr,
//				dst_ep,dst_pan_id,
//				src_ep,cluster_id,trans_id,0x00,radius,zcl_data) ;
		this.asReq(req, true, 5000) ;
		
		//System.out.println("**="+zcl_frm) ;
		
		//System.out.println("$$="+req) ;
	}
	
	DataReq_CMD(DataRequestSREQ req)
	{
		super();
		this.asReq(req, true, 5000) ;
	}
	
	public static DataReq_CMD createClusterCmdReqRsp(ZStackManager zsm,
			int dst_addr,int endpoint,int cluster_id,int cmd_id,Object[] pm_vals,boolean dir_s2c)
	{
		int trans_id = zsm.get_next_trans_id() ;
		DataRequestSREQ req = DataRequestSREQ.createClusterCmdReq(dst_addr,endpoint,cluster_id,cmd_id,pm_vals,dir_s2c,trans_id) ;
		DataReq_CMD ret = new DataReq_CMD(req) ;
		return ret;
	}
	
	public static DataReq_CMD createClusterCmdReqRsp(ZStackManager zsm,
			int dst_addr,int endpoint,int cluster_id,int cmd_id,boolean dir_s2c)
	{
		return createClusterCmdReqRsp(zsm,dst_addr, endpoint, cluster_id, cmd_id,null,dir_s2c);
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		
		if(!(m_recv instanceof DataRequestSRSP))
		{
			failedr.append("not DataRequestSRSP response") ;
			return false;
		}
		respRSP = (DataRequestSRSP)m_recv;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		// System.out.println("$DataReq-Flw-MsgRecv$>>"+m_recv) ;
		
		if(m_recv instanceof DataConfirmAREQ_CB)
		{
			flwDataConfirm = (DataConfirmAREQ_CB)m_recv ;
			//return true ;
		}
		
		if(m_recv instanceof IncomingMsgAREQ_CB)
		{
//			failedr.append("not IncomingMsgAREQ_CB response") ;
//			return false;
			respFlwRSP = (IncomingMsgAREQ_CB)m_recv;
			return true;
		}
		//respFlwRSP = (IncomingMsgAREQ_CB)m_recv;
		return false ;
	}
	
	public boolean getStatus()
	{
		if(respRSP==null)
			return false ;
		return respRSP.getStatus() ;
	}
	
	public IncomingMsgAREQ_CB getIncomingMsg()
	{
		return this.respFlwRSP ;
	}

	@Override
	protected JSONObject toRetJO()
	{
		JSONObject ret = new JSONObject().put("status", this.getStatus());
		if(this.respFlwRSP!=null)
			ret.putOpt("flw_resp", respFlwRSP.toString());
		return ret ;
	}

}
