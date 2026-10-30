package org.iottree.driver.zigbee.zstack;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public abstract class MsgRecv extends MsgData
{
	public MsgRecv()
	{
		super() ;
	}
	
	public MsgRecv(int cmd0,int cmd1)
	{
		super(cmd0,cmd1) ;
	}
	
	public MsgRecv(byte cmd0_type,byte cmd0_sub,int cmd1)
	{
		super(cmd0_type, cmd0_sub, cmd1);
	}
	
	public abstract JSONObject toRecvJO();
	
	public boolean fromRecvJO(JSONObject jo)
	{
		String data_hex = jo.optString("data_hex") ;
		if(Convert.isNullOrEmpty(data_hex))
			return false;
		byte[] bs = Convert.hexStr2ByteArray(data_hex) ;
		if(bs==null||bs.length<this.getDataLenMin())
			return false;
		this.data = bs ;
		return true ;
	}
	
	@Override
	public final String toString()
	{
		return super.toString() +transJO2PropStr(toRecvJO());
	}
}
