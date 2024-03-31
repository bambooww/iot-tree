package org.iottree.driver.omron.hostlink;

import java.io.IOException;
import java.io.InputStream;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

/**
 * Hostlink msg format
 * 
 * 1) use ascii
 * 2) it can support cmode cmds and fins cmds
 * 
 * @author jason.zhu
 *
 */
public abstract class HLMsg
{
	public static ILogger log = LoggerManager.getLogger(HLMsg.class) ;
	
	public static ILogger log_w = LoggerManager.getLogger(HLMsg.class.getCanonicalName()+"_w") ;
	
	int plcUnit = 0 ; //0-30 BCD code
	
	public HLMsg()
	{}
	
	public HLMsg asPlcUnit(int plc_unit)
	{
		if(plc_unit<0||plc_unit>31)
			throw new IllegalArgumentException("plc unit must in 0-31") ;
		this.plcUnit = plc_unit ;
		return this ;
	}
	
//	public HLMsg asHeaderCode(String cmd)
//	{
//		cmd = cmd.toUpperCase() ;
//		switch(cmd)
//		{
//		case "RR":
//		case "RD":
//		case "WD":
//			break ;
//		default:
//			throw new IllegalArgumentException("unknown cmd="+cmd) ;
//		}
//		this.headerCode = cmd ;
//		return this;
//	}
	
	public abstract String getHeadCode() ;
	
	protected static String byte2hex(int b,boolean fix_len2)
	{
		String s = Integer.toHexString(b);
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
	
	
	
	public static int bcd2_to_byte(char h,char l)
	{
		return (h-'0')*10+(l-'0') ;
	}
	
	public static String calFCS(String str)
	{
		byte[] bs = str.getBytes() ;
		byte[] fcs16 = new byte[2] ;
		calFCS16(bs, fcs16) ;
		return new String(fcs16) ;
	}
	
	public static void calFCS16(byte[] bs,byte[] fcs16)
	{
		int chksum = 0 ;
		//int len = bs.length ;
		for(byte b:bs)
		{
			//byte b = bs[i] ;
			chksum ^=b ;
		}
		
		//chksum= chksum & 0xFF ;
		int h = (chksum >> 4) & 0xF ;
		int l = chksum & 0xF ;
		fcs16[0] = (byte)('0'+h) ;
		fcs16[1] = (byte)('0'+l) ;
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
