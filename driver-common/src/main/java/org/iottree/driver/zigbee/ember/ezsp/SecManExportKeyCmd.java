package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.SecManContext;
import org.json.JSONObject;

/**
 * Exports a key from security manager based on passed context.
 * 
 * @author jason.zhu
 */
public class SecManExportKeyCmd extends EZSPCmd
{
	/**
	 * Metadata to identify the requested key.
	 */
	SecManContext context ;
	
	public SecManExportKeyCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public SecManExportKeyCmd(EmberManager emgr,SecManContext cxt)
	{
		super(emgr) ;
		this.context = cxt ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x0114;
	}

	@Override
	public String getFrmName()
	{
		return "secManExportKey";
	}

	
	public SecManContext getContext()
	{
		return this.context ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return SecManContext.STRUCT_LEN;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		if(context!=null)
			context.pack(bs, offset) ;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.context = new SecManContext();
		return this.context.unpack(bs, offset) ;
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO();
		if(this.context!=null)
			jo.put("context",this.context.toJO()) ;
		return jo ;
	}

}
