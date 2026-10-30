package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPReqRsp;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.GetExtValueCmd;
import org.iottree.driver.zigbee.ember.ezsp.GetExtValueRsp;
import org.iottree.driver.zigbee.ember.types.EzspEndpointFlag;
import org.iottree.driver.zigbee.ember.types.EzspExtValueId;
import org.json.JSONObject;

public class GetExtValueRR extends EZSPReqRsp
{
	GetExtValueCmd gvCmd = null ;
	GetExtValueRsp gvRsp = null ;
	
	public GetExtValueRR(EmberManager em,EzspExtValueId ext_val_id,long character_id)
	{
		super(em) ;
		
		gvCmd = new GetExtValueCmd(em,ext_val_id,character_id) ;
		this.asReq(gvCmd, false, 5000) ;
	}
	
	@Override
	protected boolean RT_chkRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		if(m_recv instanceof GetExtValueRsp)
		{
			this.gvRsp = (GetExtValueRsp)m_recv ;
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

	
	public static byte[] getExtValue(EmberManager em,EzspExtValueId ext_val_id,long character_id) throws Exception
	{
		GetExtValueRR gvrr = new GetExtValueRR(em,ext_val_id,character_id);
		if(!em.RT_synReqRsp(gvrr, null))
			return null ;
		if(!gvrr.gvRsp.isSLStatusOk())
			return null ;
		return gvrr.gvRsp.getValues() ;
	}
	
	/**
	 * Function for manipulating the endpoints flags on the NCP.
     * Wrapper for ezspGetExtendedValue.
	 * @param em
	 * @param endpoint
	 * @return
	 * @throws Exception
	 */
	public static EzspEndpointFlag getEndpointFlags(EmberManager em,int endpoint) throws Exception
	{
		byte[] vs = getExtValue(em,EzspExtValueId.ENDPOINT_FLAGS, endpoint); 
		if(vs==null || vs.length<2)
			return null ;
		return EzspEndpointFlag.valOfInt(DataUtil.getUInt16(vs, 0)) ;
	}
}
