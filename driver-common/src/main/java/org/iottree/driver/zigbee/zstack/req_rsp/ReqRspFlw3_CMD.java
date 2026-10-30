package org.iottree.driver.zigbee.zstack.req_rsp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.driver.zigbee.zstack.Msg;
import org.iottree.driver.zigbee.zstack.MsgRecv;
import org.iottree.driver.zigbee.zstack.MsgReqRsp;
import org.json.JSONObject;

public class ReqRspFlw3_CMD<T extends Msg,R extends MsgRecv,F1 extends MsgRecv,F2 extends MsgRecv,F3 extends MsgRecv> extends MsgReqRsp
{
	R respRSP = null ;
	Class<R> respC ;
	
	Class<F1> flwC1 =null ;
	Class<F2> flwC2 =null ;
	Class<F3> flwC3 =null ;
	
	F1 flw1 = null ;
	F2 flw2 = null ;
	F3 flw3 = null ;
	
	int flw_n = 3 ;
	
	int cur_flw_idx = 0 ;
	
	public ReqRspFlw3_CMD(T req,Class<R> rc,Class<F1> flw_c1,Class<F2> flw_c2,Class<F3> flw_c3)
	{
		super();
		
		if(flw_c1==null || flw_c2==null || flw_c3==null)
			throw new IllegalArgumentException("flw_cx cannot null") ;
		respC = rc ;
		flwC1 = flw_c1 ;
		flwC2 = flw_c2 ;
		flwC3 = flw_c3 ;
		
		this.asReq(req, true, 5000) ;
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

	@SuppressWarnings("unchecked")
	@Override
	protected boolean RT_chkFollowMsg(MsgRecv m_recv, StringBuilder failedr)
	{
		switch(cur_flw_idx)
		{
		case 0:
			if(flwC1.isInstance(m_recv))
			{
				flw1 = (F1)m_recv;
				cur_flw_idx ++ ;
			}
			return cur_flw_idx==this.flw_n;
		case 1:
			if(flwC2.isInstance(m_recv))
			{
				flw2 = (F2)m_recv;
				cur_flw_idx ++ ;
			}
			return cur_flw_idx==this.flw_n;
		case 2:
			if(flwC3.isInstance(m_recv))
			{
				flw3 = (F3)m_recv;
				cur_flw_idx ++ ;
			}
			return cur_flw_idx==this.flw_n;
		}
		return false;
	}
	
	public R getRsp()
	{
		return this.respRSP ;
	}
	
	public F1 getFlw1()
	{
		return this.flw1 ;
	}
	
	public F2 getFlw2()
	{
		return this.flw2 ;
	}
	
	public F3 getFlw3()
	{
		return this.flw3 ;
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
