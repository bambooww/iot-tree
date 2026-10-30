package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPBuffer;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.EzspFrameID;
import org.iottree.driver.zigbee.ember.types.SecManNwkKeyInf;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

public class GetNwkKeyInfRsp extends EZSPRsp
{
	SLStatus status ;
	/**
	 * 
	 */
	SecManNwkKeyInf nwk_key_inf ;
	
	public GetNwkKeyInfRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return EzspFrameID.GET_NETWORK_KEY_INFO.getIntVal();
	}

	@Override
	public String getFrmName()
	{
		return "secManGetNetworkKeyInfo";
	}

	@Override
	protected int getParamsLen()
	{
		return SecManNwkKeyInf.STRUCT_LEN+1;
	}

	public SLStatus getStatus()
	{
		return this.status ;
	}
	
	public SecManNwkKeyInf getNwkKeyInf()
	{
		return this.nwk_key_inf ;
	}
	
	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<8)
		{
			if(failedr!=null)
				failedr.append("unpack params len<8");
			return false;
		}
		EZSPBuffer buf = new EZSPBuffer(bs,offset,len) ;
		this.status = buf.readStatus(this.getEZSPVersion()) ;
		this.nwk_key_inf = buf.readSecManNwkKeyInf() ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		JSONObject r = super.toJO().putOpt("status",this.status)
				.putOpt("nwk_key_inf",nwk_key_inf!=null?nwk_key_inf.toJO():null) ;
		return r ;
	}
}
