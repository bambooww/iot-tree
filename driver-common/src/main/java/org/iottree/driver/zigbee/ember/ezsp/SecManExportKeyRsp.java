package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.ember.EZSPBuffer;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.SecManContext;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

public class SecManExportKeyRsp extends EZSPRsp
{
	/**
	 * sl_status_t status  An sl_status_t value indicating success or the reason for failure.
	 */
	SLStatus sl_status ;
	
	/**
	 * sl_zigbee_sec_man_key_t key  Data to store the exported key in.
	 */
	String sec_man_key ; //uint[16]
	
	SecManContext context = null ;
	
	public SecManExportKeyRsp(EmberManager emgr)
	{
		super(emgr) ;
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
	

	public SLStatus getStatus()
	{
		return this.sl_status ;
	}
	
	public String getSecManKey()
	{
		return this.sec_man_key ;
	}
	
	
	public SLStatus getSLStatus()
	{
		return this.sl_status ;
	}
	
	public SecManContext getContext()
	{
		return this.context ;
	}
	
	@Override
	protected int getParamsLen()
	{
		int ver = this.getEZSPVersion() ;
		if(ver<0x0e)
			return 17 ;
		else
			return 20+SecManContext.STRUCT_LEN ;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<1)
			return false;
		int wlen = UTIL_writeStatus(this.owner.getEZSPVersion(), this.sl_status.getIntVal(), bs, offset) ;
		System.arraycopy(this.sec_man_key, 0, bs, offset+wlen, 16);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		int ver = this.getEZSPVersion() ;
		EZSPBuffer buf = new EZSPBuffer(bs,offset,len) ;
		if(ver<0x0e)
		{
			this.sec_man_key = buf.readSecManKey();//new byte[16] ;
			this.sl_status = buf.readStatus(ver) ;
			return true;
		}
		

		this.sl_status = buf.readStatus(ver) ;
		this.sec_man_key = buf.readSecManKey();//new byte[16] ;
		context = buf.readSecManContext();//
//		SecManContext.initContext() ;
//		context.unpack(bs, 20) ;
		return true;
		
//		if (this.version < 0x0e) {
//            const key = this.buffalo.readSecManKey();
//            const status = this.buffalo.readUInt32();
//
//            return [status, key];
//        }
//
//        const status = this.buffalo.readUInt32();
//        const key = this.buffalo.readSecManKey();
//        context = this.buffalo.readSecManContext();
//
//        return [status, key];
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO().put("status",this.sl_status)
				.putOpt("sec_man_key", this.sec_man_key);
		return jo ;
	}

}
