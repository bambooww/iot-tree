package org.iottree.driver.zigbee.zstack.req_rsp;

import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVReadExtSREQ;
import org.iottree.driver.zigbee.zstack.msg_sys.OsalNVReadExtSRSP;

public abstract class ReadNV extends MsgReqRsp
{
	OsalNVReadExtSRSP respNVReadExt = null ;
	
	protected byte[] respNVData = null ;

	public ReadNV(int nv_itemid,int offset)
	{
		super();
		OsalNVReadExtSREQ req = new OsalNVReadExtSREQ(nv_itemid,offset) ;
		this.asReq(req, false, 1000) ;
	}
	
	@Override
	protected boolean RT_chkSRspMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		if(!(m_recv instanceof OsalNVReadExtSRSP))
		{
			failedr.append("not OsalNVReadExtSRSP response") ;
			return false;
		}
		respNVReadExt = (OsalNVReadExtSRSP)m_recv;
		respNVData = respNVReadExt.getNVValue() ;
		return true ;
	}

	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		return false;
	}
	
	public byte[] getRespNVData()
	{
		return this.respNVData ;
	}
}
