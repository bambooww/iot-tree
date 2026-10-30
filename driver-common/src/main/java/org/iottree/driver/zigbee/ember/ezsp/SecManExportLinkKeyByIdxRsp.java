package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.ember.EZSPBuffer;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;
import org.iottree.driver.zigbee.ember.types.SecManAPSKeyMeta;
import org.iottree.driver.zigbee.ember.types.SecManContext;
import org.iottree.driver.zigbee.ember.types.SecManKeyType;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

public class SecManExportLinkKeyByIdxRsp extends EZSPRsp
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
	
	SecManAPSKeyMeta keyData = null ;
	
	public SecManExportLinkKeyByIdxRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return EzspFrameID.EXPORT_LINK_KEY_BY_INDEX.getIntVal();
	}

	@Override
	public String getFrmName()
	{
		return "secManExportLinkKeyByIndex";
	}

	public SLStatus getStatus()
	{
		return this.sl_status ;
	}
	
	public String getSecManKey()
	{
		return this.sec_man_key ;
	}
	
	//=getNetworkKey()
	public String getSecManKeyStr()
	{
		return this.sec_man_key; //,0,16) ;
	}
	
	public SLStatus getSLStatus()
	{
		return this.sl_status ;
	}
	
	public SecManContext getContext()
	{
		return this.context ;
	}
	
	public SecManAPSKeyMeta getKeyData()
	{
		return this.keyData ;
	}
	
	@Override
	protected int getParamsLen()
	{
		int ver = this.getEZSPVersion() ;
		if(ver<0x0e)
			return 37 ;
		else
			return 50 ;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		int ver = this.getEZSPVersion() ;
		EZSPBuffer buf = new EZSPBuffer(bs,offset,len) ;
		if (ver < 0x0e) {
			this.context = SecManContext.initContext() ;
            context.coreKeyType = SecManKeyType.APP_LINK;
            //context.keyIndex = index; set outter
            context.eui64 = buf.readIEEEAddr(); //8
            this.sec_man_key = buf.readSecManKey(); //16
            this.keyData = buf.readSecManAPSKeyMetadata(); //12
            this.sl_status = buf.readStatus(ver) ;
            return true;
        }

		this.sl_status = buf.readStatus(ver) ; //4
        this.context = buf.readSecManContext(); //18
        this.sec_man_key = buf.readSecManKey(); //16
        this.keyData = buf.readSecManAPSKeyMetadata(); //12
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO().put("status",this.sl_status)
				.putOpt("sec_man_key", this.sec_man_key).putOpt("context", this.context.toJO())
				.put("key_data", this.keyData.toJO());
		return jo ;
	}
}
