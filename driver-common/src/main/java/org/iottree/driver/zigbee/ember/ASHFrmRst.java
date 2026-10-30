package org.iottree.driver.zigbee.ember;

public class ASHFrmRst extends ASHFrame
{
	public ASHFrmRst()
	{
		super(TP_RST) ;
	}
	
	@Override
	public int getDataFieldLen()
	{
		return 0;
	}

	@Override
	protected boolean packDataField(byte[] data, int offset,int dflen)
	{
		return true;
	}

	@Override
	protected boolean unpackDataField(byte[] data, int offset, int dflen,StringBuilder failedr)
	{
		return true;
	}
	
	public String toString()
	{
		return "RST";
	}
	
	@Override
	public String toShortString()
	{
		return "RST";
	}
}
