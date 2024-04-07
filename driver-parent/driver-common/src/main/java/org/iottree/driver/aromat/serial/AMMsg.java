package org.iottree.driver.aromat.serial;

import java.io.IOException;
import java.io.InputStream;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

public abstract class AMMsg
{
public static ILogger log = LoggerManager.getLogger(AMMsg.class) ;
	
	public static ILogger log_w = LoggerManager.getLogger(AMMsg.class.getCanonicalName()+"_w") ;
	
	char head = '%' ; // '<'
	
	int plcAddr = 0 ; //01-32  FF-is global
	
	public AMMsg()
	{}
	
	public AMMsg asHead(char head)
	{
		if(head!='%' && head!='<')
			throw new IllegalArgumentException("invalid head,it must be % or <") ;
		this.head = head ;
		return this ;
	}
	
	public AMMsg asPlcAddr(int plc_addr)
	{
		if(plc_addr<=0||plc_addr>32)
			throw new IllegalArgumentException("plc unit must in 1-32") ;
		this.plcAddr = plc_addr ;
		return this ;
	}
	
	
	
	protected static String byte2hex(int b,boolean fix_len2)
	{
		String s = Integer.toHexString(b).toUpperCase();
		if(fix_len2)
		{
			if (s.length() == 1)
				s = "0" + s;
		}
		return s ;
	}
	
	protected static short hex2byte(String hex)
	{
		return Short.parseShort(hex,16) ;
	}
	
	public static byte[] hex2bytes(String hex)
	{
		int len = hex.length() ;
		if(len%2!=0)
			throw new IllegalArgumentException("invalid hex str,length must be odd") ;
		byte[] bs = new byte[len/2] ;
		for(int i = 0 ; i < bs.length ; i ++)
		{
			int idx = i *2 ;
			bs[i] = (byte)Short.parseShort(hex.substring(idx,idx+2),16) ;
		}
		return bs ;
	}
	
	public static String byte_to_bcd2(int b)
	{
		return (""+b/10)+b%10 ;
	}
	
	public static String byte_to_bcd4(int b)
	{
		StringBuilder sb = new StringBuilder() ;
		sb.append(b/1000) ;
		b = b%1000 ;
		sb.append(b/100) ;
		b= b%100 ;
		sb.append(b/10) ;
		sb.append(b%10) ;
		return sb.toString() ;
	}
	
	public static int bcd2_to_byte(char h,char l)
	{
		return (h-'0')*10+(l-'0') ;
	}
	
	public static String calBCC(String str)
	{
		byte[] bs = str.getBytes() ;
		//byte[] fcs16 = new byte[2] ;
		return calBCC(bs) ;
		//return new String(fcs16) ;
	}
	
//	public static void calFCS16(byte[] bs,byte[] fcs16)
//	{
//		int chksum = 0 ;
//		//int len = bs.length ;
//		for(byte b:bs)
//		{
//			//byte b = bs[i] ;
//			chksum ^=b ;
//		}
//		
//		//chksum= chksum & 0xFF ;
//		int h = (chksum >> 4) & 0xF ;
//		int l = chksum & 0xF ;
//		fcs16[0] = (byte)('0'+h) ;
//		fcs16[1] = (byte)('0'+l) ;
//	}
	
	public static String calBCC(byte[] bs)
	{
		int chksum = 0 ;
		//int len = bs.length ;
		for(byte b:bs)
		{
			//byte b = bs[i] ;
			chksum ^=b ;
		}
		
		//chksum= chksum & 0xFF ;
//		int h = (chksum >> 4) & 0xF ;
//		int l = chksum & 0xF ;
//		fcs16[0] = (byte)('0'+h) ;
//		fcs16[1] = (byte)('0'+l) ;
		int v = chksum & 0xFF ;
		
		String r = Integer.toHexString(v).toUpperCase() ;
		if(r.length()==1)
			return "0"+r ;
		return r ;
	}
	

	public static void clearInputStream(InputStream inputs,long timeout) throws IOException
	{
		int lastav = inputs.available() ;
		long lastt = System.currentTimeMillis() ;
		long curt = lastt; 
		while((curt=System.currentTimeMillis())-lastt<timeout)
		{
			try
			{
			Thread.sleep(1);
			}
			catch(Exception e) {}
			
			int curav = inputs.available() ;
			if(curav!=lastav)
			{
				lastt = curt ;
				lastav = curav ;
				continue ;
			}
		}
		
		if(lastav>0)
			inputs.skip(lastav) ;
	}
}
