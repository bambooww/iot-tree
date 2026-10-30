package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPReqRsp;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.GetValueCmd;
import org.iottree.driver.zigbee.ember.ezsp.GetValueRsp;
import org.iottree.driver.zigbee.ember.types.EzspValueId;
import org.iottree.driver.zigbee.ember.types.RejoinReason;
import org.json.JSONObject;

public class GetValueRR extends EZSPReqRsp
{
	GetValueCmd gvCmd = null ;
	GetValueRsp gvRsp = null ;
	
	public GetValueRR(EmberManager em,int value_id)
	{
		super(em) ;
		
		gvCmd = new GetValueCmd(em,value_id) ;
		this.asReq(gvCmd, false, 5000) ;
	}
	
	@Override
	protected boolean RT_chkRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		if(m_recv instanceof GetValueRsp)
		{
			this.gvRsp = (GetValueRsp)m_recv ;
			return true ;
		}
		return false;
	}

	@Override
	protected boolean RT_chkFlwRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		return false;
	}

	@Override
	protected JSONObject toRetJO()
	{
		return null;
	}

	
	public static String ezspGetVersion(EmberManager em,StringBuilder failedr) throws Exception
	{
		GetValueRR gvrr = new GetValueRR(em,EzspValueId.VERSION_INFO) ;
		if(!em.RT_synReqRsp(gvrr, failedr))
			return null ;
		/*
		 build: outValue[0] + (outValue[1] << 8),
                major: outValue[2],
                minor: outValue[3],
                patch: outValue[4],
                special: outValue[5],
                type: outValue[6],
		 */
		byte[] bs = gvrr.gvRsp.getValues() ;
		if(bs==null||bs.length<7)
			return null;
		return (bs[2]&0xFF)+"."+(bs[3]&0xFF)+"."+(bs[4]&0xFF);//DataUtil.getUInt16(bs, 0)+""
	}
	
	public static RejoinReason ezspGetLastRejoinReason(EmberManager em,StringBuilder failedr) throws Exception
	{
		GetValueRR gvrr = new GetValueRR(em,EzspValueId.LAST_REJOIN_REASON) ;
		if(!em.RT_synReqRsp(gvrr, failedr))
			return null ;
		byte[] bs = gvrr.gvRsp.getValues() ;
		if(bs==null||bs.length<1)
			return null;
		return RejoinReason.valOfInt(bs[0]&0xFF) ;
	}
	
	public static Integer ezspGetExtendedSecurityBitmask(EmberManager em,StringBuilder failedr) throws Exception
	{
		GetValueRR gvrr = new GetValueRR(em,EzspValueId.EXTENDED_SECURITY_BITMASK) ;
		if(!em.RT_synReqRsp(gvrr, failedr))
			return null ;
		byte[] bs = gvrr.gvRsp.getValues() ;
		if(bs==null||bs.length<2)
			return null;
		return DataUtil.getUInt16(bs, 0) ;
	}
}
