package org.iottree.driver.zigbee.zstack;

public class MsgNor extends Msg
{
	byte[] data = null ;
	
	@Override
	public String getTpName()
	{
		return "nor";
	}
	
	@Override
	public byte[] getData()
	{
		return this.data;
	}

	public MsgNor asData(byte[] bs)
	{
		this.data = bs ;
		return this ;
	}
	
	public static MsgNor parseFrom(byte[] bs,StringBuilder failedr)
	{
		MsgNor ret = new MsgNor() ;
		if(!ret.unpack(bs, failedr))
			return null ;
		return ret;
	}
	
	@Override
	protected boolean parseData(byte[] data, StringBuilder failedr)
	{
		this.data = data ;
		return true;
	}
	
	public static void main(String[] args) throws Exception
	{
		
	}

	
}
