package org.iottree.driver.zigbee.zstack;

public abstract class MsgNoData extends Msg
{
	public MsgNoData()
	{
		super();
	}
	
	public MsgNoData(int cmd0,int cmd1)
	{
		super(cmd0,cmd1) ;
	}
	
	public MsgNoData(byte cmd0_type,byte cmd0_sub,int cmd1)
	{
		super(cmd0_type, cmd0_sub, cmd1);
	}
	
	@Override
	public byte[] getData()
	{
		return null;
	}

	@Override
	protected boolean parseData(byte[] data, StringBuilder failedr)
	{
		return true;
	}

}
