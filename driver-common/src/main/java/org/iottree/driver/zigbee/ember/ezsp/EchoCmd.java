package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

public class EchoCmd extends EZSPCmd
{
	byte[] data;
	
	public EchoCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public EchoCmd(EmberManager emgr,byte[] data)
	{
		super(emgr) ;
		this.data = data ;
	}

	@Override
	public int getFrmID()
	{
		return 0x0081;
	}

	@Override
	public String getFrmName()
	{
		return "echo";
	}

	@Override
	protected int getParamsLen()
	{
		return 1;
	}
	

	public byte[] getData()
	{
		return this.data ;
	}


	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		int dlen = this.data!=null?this.data.length:0 ;
		bs[offset] = (byte)dlen ;
		if(dlen>0)
			System.arraycopy(this.data, 0, bs, offset+1, dlen);
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		int dlen = bs[offset] & 0xFF ;
		if(dlen>0)
		{
			this.data = new byte[dlen] ;
			System.arraycopy(bs, offset+1, this.data, 0, dlen);
		}
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("data", Convert.byteArray2HexStr(this.data, "-")) ;
	}
}
