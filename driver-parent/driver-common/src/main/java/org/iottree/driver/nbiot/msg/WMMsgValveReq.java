package org.iottree.driver.nbiot.msg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class WMMsgValveReq extends WMMsg
{

	boolean bOpen = true ; //open valve or not
	
	public WMMsgValveReq()
	{
		final byte[] F = new byte[] {(byte)0x02,(byte)0x02} ; 
		this.setMsgFunc(F);
	}
	
//	public void setMeterAddr(byte[] addr)
//	{
//		super.setMeterAddr(addr);
//		this.meterAddr[0]= 0x10;
//		this.meterAddr[1]= 0x0;
//		this.meterAddr[2]= 0x0;
//	}
	
	public void setValveOpen(boolean b)
	{
		this.bOpen = b ;
	}
	
	@Override
	protected ArrayList<byte[]> parseMsgBody(InputStream inputs) throws IOException
	{
		return null;
	}

	@Override
	protected ArrayList<byte[]> getMsgBody()
	{
		ArrayList<byte[]> bbs = super.getMsgBody() ;
		if(bOpen)
			bbs.add(new byte[] {(byte)0x55}) ;
		else
			bbs.add(new byte[] {(byte)0xAA}) ;
		return bbs ;
	}
}
