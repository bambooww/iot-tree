package org.iottree.driver.nbiot.msg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class WMMsgValveResp extends WMMsg
{
	boolean bOpen = true ; //open valve or not
	
	public WMMsgValveResp()
	{
		final byte[] F = new byte[] {(byte)0x82,(byte)0x02} ; 
		this.setMsgFunc(F);
	}
	
	public boolean isValveOpen()
	{
		return bOpen;
	}
	
	@Override
	protected ArrayList<byte[]> parseMsgBody(InputStream inputs) throws IOException
	{
		if(inputs.available()<1)
			return null;
		byte[] bs = new byte[1] ;
		inputs.read(bs) ;
		ArrayList<byte[]> bbs = new ArrayList<>() ;
		bbs.add(bs) ;
		
		int v = bs[0] & 0xFF ;
		if(v==0x55)
			this.bOpen = true ;
		else if(v==0xAA)
			this.bOpen = false;
		else
			throw new IOException("invalid valve status value");
		return bbs;
	}

	public String toString()
	{
		return super.toString()+" valve_open="+this.bOpen ;
	}
}
