package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.zcl.cluster_cmd.GreenPowerCmd;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.ZStackManager;
import org.iottree.driver.zigbee.zstack.msg_af.DataConfirmAREQ_CB;
import org.iottree.driver.zigbee.zstack.msg_af.DataRequestExtSREQ;
import org.iottree.driver.zigbee.zstack.msg_af.DataRequestExtSRSP;
import org.iottree.driver.zigbee.zstack.msg_af.DataRequestSREQ;
import org.iottree.driver.zigbee.zstack.msg_af.DataRequestSRSP;
import org.iottree.driver.zigbee.zstack.types.AddressMode;
import org.json.JSONObject;

public class DataReqExtConfirm_CMD extends MsgReqRsp
{
	DataRequestExtSRSP respRSP = null ;
	
	
	DataConfirmAREQ_CB flwCB = null ;

	DataReqExtConfirm_CMD(DataRequestExtSREQ req)
	{
		super();
		this.asReq(req, true, 10000) ;
	}
	
	public static DataReqExtConfirm_CMD createClusterCmdReqRsp(ZStackManager zsm,
			int dst_addr,int endpoint,int cluster_id,int cmd_id,boolean dir_s2c)
	{
		int trans_id = zsm.get_next_trans_id() ;
		DataRequestExtSREQ req = DataRequestExtSREQ.createClusterCmdReq(dst_addr,endpoint,cluster_id,cmd_id,dir_s2c,trans_id) ;
		DataReqExtConfirm_CMD ret = new DataReqExtConfirm_CMD(req) ;
		return ret;
	}
	
	public static DataReqExtConfirm_CMD createClusterCmdReqRsp(ZStackManager zsm,
			AddressMode addr_mode,Object dst_addr,
			int dst_endpoint,int dst_pan_id,
			int src_endpoint,int cluster_id,
			int options,int radius,byte[] last_data)
	{
		int trans_id = zsm.get_next_trans_id() ;
		
		byte[] dst_bs = null ;
		if(dst_addr instanceof Integer)
		{
			int addr = (Integer)dst_addr ;
			dst_bs = new byte[8] ;
			DataUtil.setUInt16(addr, dst_bs, 0);
		}
		else if(dst_addr instanceof String)
		{
			String hexaddr = (String)dst_addr ;
			dst_bs = new byte[8] ;
			DataUtil.transStr2IEEEAddr(hexaddr, dst_bs, 0) ;
		}
		else
			throw new IllegalArgumentException("invalid dst_addr="+dst_addr) ;
		
		DataRequestExtSREQ req = new DataRequestExtSREQ( addr_mode.val, dst_bs,
				dst_endpoint, dst_pan_id,
				src_endpoint, cluster_id, trans_id, options, radius, last_data) ;
		
		DataReqExtConfirm_CMD ret = new DataReqExtConfirm_CMD(req) ;
		return ret;
	}
	
	public static DataReqExtConfirm_CMD createGreenPowerPermitOrNot(ZStackManager zsm,boolean permit_or_not)
	{//Green Power Proxy Commissioning Mode 开启广播。
		//dst_addr = 0xFFFD  endpoint=242
		//green powner cluster_id=33  cmd_id=2
		int trans_id = zsm.get_next_trans_id() ;
		GreenPowerCmd gpc = GreenPowerCmd.createGPPermitJoin(permit_or_not);
		DataRequestExtSREQ req = DataRequestExtSREQ.createClusterGreenPowerCmd(0xFFFD,gpc, trans_id) ;
		DataReqExtConfirm_CMD ret = new DataReqExtConfirm_CMD(req) ;
		return ret;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof DataRequestExtSRSP))
		{
			failedr.append("not DataRequestExtSRSP response") ;
			return false;
		}
		respRSP = (DataRequestExtSRSP)m_recv;
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
