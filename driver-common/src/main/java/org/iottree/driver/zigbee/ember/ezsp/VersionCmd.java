package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

public class VersionCmd extends EZSPCmd
{
	public static final int EZSP_MIN_PROTOCOL_VERSION = 0x0d;
	
	public static final int EZSP_DESIRED_PROTOCOL_VERSION = 19 ;
	/** Latest EZSP protocol version */
	public static final  int EZSP_PROTOCOL_VERSION = 0x13;
	
	/**
	 * int8_t desiredProtocolVersion
		The EZSP version the Host wishes to use. To successfully set the version and allow other commands,
		 this must be same as EZSP_PROTOCOL_VERSION.
	 */
	int desired_proto_ver  = EZSP_DESIRED_PROTOCOL_VERSION;
	
	public VersionCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public VersionCmd(EmberManager emgr,int desired_ver)
	{
		super(emgr) ;
		this.desired_proto_ver = desired_ver ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0;
	}

	@Override
	public String getFrmName()
	{
		return "version";
	}

	public int getDesiredProtoVer()
	{
		return this.desired_proto_ver ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 1;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		bs[offset] = (byte)this.desired_proto_ver ;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.desired_proto_ver = bs[offset] & 0xFF ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("desired_proto_ver",this.desired_proto_ver) ;
	}
}
