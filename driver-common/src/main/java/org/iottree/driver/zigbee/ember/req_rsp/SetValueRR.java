package org.iottree.driver.zigbee.ember.req_rsp;

import org.iottree.driver.zigbee.ember.EZSPReqRsp;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.ezsp.SLStatus_Rsp;
import org.iottree.driver.zigbee.ember.ezsp.SetValueCmd;
import org.iottree.driver.zigbee.ember.types.EzspValueId;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

public class SetValueRR extends EZSPReqRsp
{
	SetValueCmd svCmd = null ;
	SLStatus_Rsp svRsp = null ;
	
	public SetValueRR(EmberManager em,int value_id,byte[] vs)
	{
		super(em) ;
		
		svCmd = new SetValueCmd(em,value_id,vs) ;
		this.asReq(svCmd, false, 5000) ;
	}
	
	@Override
	protected boolean RT_chkRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		if(m_recv instanceof SLStatus_Rsp)
		{
			this.svRsp = (SLStatus_Rsp)m_recv ;
			return true ;
		}
		return false;
	}

	@Override
	protected boolean RT_chkFlwRsp(EZSPRsp m_recv, StringBuilder failedr)
	{
		return false;
	}
	
	public boolean isSLStatusOk()
	{
		if(this.svRsp==null)
			return false ;
		return this.svRsp.getSLStatus().isOk() ;
	}
	
	public SLStatus getSLStatus()
	{
		if(this.svRsp==null)
			return null ;
		return this.svRsp.getSLStatus();
	}

	@Override
	protected JSONObject toRetJO()
	{
		return null;
	}

	
	public static SLStatus setValue(EmberManager em,int value_id,byte[] vs) throws Exception
	{
		SetValueRR rr = new SetValueRR(em,value_id,vs) ;
		if(!em.RT_synReqRsp(rr, null))
			return null ;
		return rr.getSLStatus();
	}
	
	/**
     * Wrapper for `ezspSetValue`.
	 * @throws Exception 
     * @returns
     */
    public static SLStatus startWritingStackTokens(EmberManager em) throws Exception
    {
    	byte[] vs = new byte[] {1} ;
        return setValue(em,EzspValueId.STACK_TOKEN_WRITING, vs);
    }

    /**
     * Wrapper for `ezspSetValue`.
     * @throws Exception 
     * @returns
     */
    public static SLStatus stopWritingStackTokens(EmberManager em) throws Exception
    {
    	byte[] vs = new byte[] {0} ;
    	return setValue(em,EzspValueId.STACK_TOKEN_WRITING, vs);
    }
	
}
