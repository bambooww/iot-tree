package org.iottree.driver.zigbee.zstack.req_rsp;

import java.util.HashMap;
import java.util.List;

import org.iottree.driver.zigbee.zcl.util.AttrReportConfig;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.ZStackManager;
import org.iottree.driver.zigbee.zstack.msg_af.DataConfirmAREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_af.DataRequestSREQ;
import org.iottree.driver.zigbee.zstack.msg_af.DataRequestSRSP;
import org.json.JSONObject;

public class DataReqConfirm_CMD extends MsgReqRsp
{
	DataRequestSRSP respRSP = null ;
	
	
	DataConfirmAREQ_CB flwCB = null ;
	
//	protected byte[] respNVData = null ;

//	public DataReq_CMD(ZStackManager zsm,int dst_addr,int dst_ep,
//			int src_ep,int cluster_id,ZCLFrame zcl_frm)
//	{
//		super();
//		int trans_id = zsm.get_next_trans_id() ;
//		int radius = 30 ;
//		zcl_frm.asSeqNum(trans_id) ;
//		byte[] zcl_data = zcl_frm.packFrame() ;
//		DataRequestSREQ req = new DataRequestSREQ(dst_addr,dst_ep,src_ep,
//				cluster_id,trans_id,0x00,radius,zcl_data) ;
////		DataRequestExtSREQ req = new DataRequestExtSREQ(dst_addr,
////				dst_ep,dst_pan_id,
////				src_ep,cluster_id,trans_id,0x00,radius,zcl_data) ;
//		this.asReq(req, true, 5000) ;
//		
//		System.out.println("**="+zcl_frm) ;
//		
//		System.out.println("$$="+req) ;
//	}
	
	DataReqConfirm_CMD(DataRequestSREQ req)
	{
		super();
		this.asReq(req, true, 5000) ;
	}
	
	/**
	 * 创建cluster command触发请求
	 * @param zsm
	 * @param dst_addr
	 * @param endpoint
	 * @param cluster_id
	 * @param cmd_id
	 * @param pm_vals
	 * @param dir_s2c
	 * @return
	 */
	public static DataReqConfirm_CMD createClusterCmdReqRsp(ZStackManager zsm,
			int dst_addr,int endpoint,int cluster_id,int cmd_id,Object[] pm_vals,boolean dir_s2c)
	{
		int trans_id = zsm.get_next_trans_id() ;
		DataRequestSREQ req = DataRequestSREQ.createClusterCmdReq(dst_addr,endpoint,cluster_id,cmd_id,pm_vals,dir_s2c,trans_id) ;
		DataReqConfirm_CMD ret = new DataReqConfirm_CMD(req) ;
		return ret;
	}
	
	/**
	 * 创建attribute report 配置信息——Configure Reporting Command Frame Format
	 *  主要是 report 最小 最大report间隔，值变化阈值，
	 *  
	 * @param zsm
	 * @param dst_addr
	 * @param endpoint
	 * @param cluster_id
	 * @param dir_s2c
	 * @param attr_reports
	 * @return
	 */
	public static DataReqConfirm_CMD createConfigAttrReport(ZStackManager zsm,
			int dst_addr,int endpoint,int cluster_id,boolean dir_s2c,List<AttrReportConfig> attr_reports)
	{
		int trans_id = zsm.get_next_trans_id() ;
		DataRequestSREQ req =  DataRequestSREQ.createConfigAttrReportReq(dst_addr,endpoint,cluster_id, 
				dir_s2c, trans_id,attr_reports) ;
		DataReqConfirm_CMD ret = new DataReqConfirm_CMD(req) ;
		return ret;
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
		if(m_recv instanceof DataConfirmAREQ_CB)
		{
			flwCB = (DataConfirmAREQ_CB)m_recv ;
			return true ;
		}
		
		return false ;
	}
	
	public boolean getStatus()
	{
		if(respRSP==null)
			return false ;
		return respRSP.getStatus() ;
	}
	
	public DataConfirmAREQ_CB getFlwMsg()
	{
		return this.flwCB ;
	}
	
	public boolean getConfirmStatus()
	{
		if(this.flwCB==null)
			return false;
		return this.flwCB.getStatus() ;
	}

	@Override
	protected JSONObject toRetJO()
	{
		JSONObject ret = new JSONObject().put("status", this.getStatus());
		if(this.flwCB!=null)
			ret.putOpt("flw_resp", flwCB.toString());
		return ret ;
	}

}
