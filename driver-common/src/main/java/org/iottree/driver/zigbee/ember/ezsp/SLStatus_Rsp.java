package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.util.ICopyable;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

public class SLStatus_Rsp extends EZSPRsp implements ICopyable<SLStatus_Rsp>
{
	int frm_id ;
	
	String frm_name ;
	/**
	 * sl_status_t status  An sl_status_t value indicating success or the reason for failure.
	 */
	SLStatus sl_status ;
	
	public SLStatus_Rsp(EmberManager emgr,int frm_id,String frm_name)
	{
		super(emgr) ;
		this.frm_id = frm_id ;
		this.frm_name = frm_name ;
	}
	
	@Override
	public int getFrmID()
	{
		return this.frm_id ;
	}

	@Override
	public String getFrmName()
	{
		return frm_name;
	}
	

	@Override
	public SLStatus_Rsp copyMe(EmberManager emgr)
	{
		return new SLStatus_Rsp(emgr,this.frm_id,this.frm_name);
	}


	public SLStatus getSLStatus()
	{
		return this.sl_status ;
	}
	
	
	@Override
	protected int getParamsLen()
	{
		if (this.getEZSPVersion() < 0x0e)
			return 1 ;
		else
			return 4 ;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<1)
			return false;
		if(this.sl_status==null)
			return false;
		UTIL_writeStatus(this.owner.getEZSPVersion(), this.sl_status.getIntVal(), bs, offset) ;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		SLStatus[] rets= new SLStatus[1] ;
		UTIL_readStatus(this.owner.getEZSPVersion(), bs, offset, rets) ;
		this.sl_status = rets[0] ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("sl_status",this.sl_status);
	}

}
