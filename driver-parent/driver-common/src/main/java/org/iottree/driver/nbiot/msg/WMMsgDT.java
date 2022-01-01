package org.iottree.driver.nbiot.msg;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.iottree.core.util.Convert;

public abstract class WMMsgDT extends WMMsg
{
	/**
	 * 当前时间，6字节
	 */
	byte[] msgDT = null ;
	
	public byte[] getMsgDT()
	{
		return msgDT ;
	}
	
	public void setMsgDT(byte[] msgdt)
	{
		if(msgdt.length!=6)
		{
			throw new IllegalArgumentException("invalid dt info") ;
		}
		this.msgDT = msgdt ;
	}
	
	public void setMsgDT(Date dt)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int y = cal.get(Calendar.YEAR) -2000;
		int m =cal.get(Calendar.MONTH)+1;
		int d = cal.get(Calendar.DAY_OF_MONTH);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.SECOND);
		
		byte[] bs = new byte[6] ;
		bs[0] = int2bcd(y) ;
		bs[1] = int2bcd(m) ;
		bs[2] = int2bcd(d) ;
		bs[3] = int2bcd(h) ;
		bs[4] = int2bcd(min) ;
		bs[5] = int2bcd(s) ;
		
		msgDT = bs ;
	}
	
	public Date getMsgDTDate()
	{
		if(msgDT==null)
			return null ;
		
		int y = 2000+bcd2int(msgDT[0]);
		int m = bcd2int(msgDT[1]);
		int d = bcd2int(msgDT[2]);
		int h = bcd2int(msgDT[3]);
		int min = bcd2int(msgDT[4]);
		int s = bcd2int(msgDT[5]);
		
		Calendar cal = Calendar.getInstance() ;
		cal.set(Calendar.YEAR, y);
		cal.set(Calendar.MONTH, m-1);
		cal.set(Calendar.DAY_OF_MONTH, d);
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, s);
		
		return cal.getTime() ;
	}
	
//	protected int checkSum(byte[] body)
//	{
//		int r = super.checkSum(body) ;
//		
//		for(int i = 0 ; i < 6 ; i ++)
//			r += (msgDT[i] & 0xFF) ;
//		
//		return r ;
//	}
	
	protected ArrayList<byte[]> getMsgBody()
	{
		ArrayList<byte[]> bbs = super.getMsgBody() ;
		bbs.add(this.msgDT) ;
		return bbs ;
		
	}
	
	protected ArrayList<byte[]> parseMsgBody(InputStream inputs) throws IOException
	{
		if(inputs.available()<6)
			return null;
		this.msgDT = new byte[6] ;
		inputs.read(this.msgDT) ;
		ArrayList<byte[]> bbs = new ArrayList<>() ;
		bbs.add(this.msgDT) ;
		return bbs;
	}
	
	
	public String toString()
	{
		String ret=super.toString() ;
		
		ret += " dt="+Convert.toFullYMDHMS(this.getMsgDTDate());
		
		return ret ;
	}
}
