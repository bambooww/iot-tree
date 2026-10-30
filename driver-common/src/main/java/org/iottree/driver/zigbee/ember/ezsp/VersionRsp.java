package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

public class VersionRsp extends EZSPRsp
{
	/**
	 * The EZSP version the NCP is using.
	 */
	int proto_ver ;
	
	/**
	 * uint8_t stackType The type of stack running on the NCP (2).
	 */
	int stack_tp;
	
	/**
	 * uint16_t stackVersion  The version number of the stack.
	 */
	int stack_ver ;
	
	public VersionRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public VersionRsp(EmberManager emgr,int proto_ver,int stack_tp,int stack_ver)
	{
		super(emgr);
		this.proto_ver = proto_ver ;
		this.stack_tp = stack_tp ;
		this.stack_ver = stack_ver ;
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

	public int getProtoVer()
	{
		return this.proto_ver ;
	}
	
	public int getStackTP()
	{
		return this.stack_tp ;
	}
	
	public int getStackVer()
	{
		return this.stack_ver ;
	}
	
	/**
	 * 
 15      12   11       8   7        4   3        0
+----------+----------+----------+----------+
| major    | minor    | patch    | build    |
+----------+----------+----------+----------+
	 * @return
	 */
	public String getStackVerT()
	{
		int major = (stack_ver >> 12) & 0xF;
		int minor = (stack_ver >> 8) & 0xF;
		int patch = (stack_ver >> 4) & 0xF;
		int build = stack_ver & 0xF;
		return major+"."+minor+"."+patch+"."+build ;
	}

	@Override
	protected int getParamsLen()
	{
		return 4;
	}
	
	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<3)
			return false;
		bs[offset] = (byte)this.proto_ver ;
		bs[offset+1] = (byte)this.stack_tp ;
		DataUtil.setUInt16(this.stack_ver, bs, offset+2); //2-3
		return true;
	}
	
	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<4)
			return false;
		this.proto_ver = bs[offset] & 0xFF ;
		this.stack_tp = bs[offset+1] & 0xFF ;
		this.stack_ver = DataUtil.getUInt16(bs, offset+2) ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("proto_ver",this.proto_ver).put("stack_tp", this.stack_tp)
				.put("stack_ver", this.getStackVerT()) ;
	}

}
