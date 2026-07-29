package org.iottree.driver.mitsubishi.fxnet;

import java.io.IOException;
import java.io.InputStream;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

public abstract class FxNetMsg
{
	public static ILogger log = LoggerManager.getLogger(FxNetMsg.class) ;
	
	public static ILogger log_w = LoggerManager.getLogger(FxNetMsg.class.getCanonicalName()+"_w") ;
	/**
	 * one byte acknowledgement
	 */
	public static final byte ENQ = 0x05;
	
	public static final byte STX = 0x02;
	
	public static final byte ETX = 0x03;
	
	public static final byte ACK = 0x06;
	
	public static final byte NCK  =0x15;
	
	/**
	 * PLC Addr (0xFF)   e.g 1-15
	 */
	short stationCode = 1 ;
	
	/**
	 * PC Addr 0xFF
	 */
	short pcCode = 0xFF ;
	
	byte msgWait = '0' ;
	
	byte[] startAddrBS5 = null ;
	
	public FxNetMsg()
	{}
	
	//private abstract  
	
	public FxNetMsg asStationCode(short station_c)
	{
		this.stationCode = station_c ;
		return this ;
	}
	
	public FxNetMsg asPCCode(short c)
	{
		this.pcCode = c ;
		return this ;
	}
	
	public FxNetMsg asStartAddrBS5(byte[] bs)
	{
		if(bs==null||bs.length!=5)
			throw new IllegalArgumentException("invalid bytes 5 length addr") ;
		this.startAddrBS5 = bs ;
		return this ;
	}
	/**
	 * e.g  X 0 0 0 0  , S 0 0 0 0 ,  T N 1 2 3
	 * @return
	 */
	public byte[] getStartAddrBS5()
	{
		return this.startAddrBS5 ;
	}
	
	public abstract byte[] toBytes() ;
	

	protected static final int calCRC(byte[] bs,int offset, int len) //, byte[] CRC)
    {
		int r = 0 ;
		for(int i = 0;i<len ; i ++)
		{
			r += (bs[i+offset] & 0xFF) ;
		}
		return r & 0xFF ;
    }
	
	protected final static byte toAsciiHexByte(int f)
	{
		if(f<10)
			return (byte)('0'+f) ;
		else
			return (byte)('A'+f-10) ;
	}
	
	protected final static int fromAsciiHexByte(byte b)
	{
		if(b<'A')
			return b-'0' ;
		else
			return b-'A'+10 ;
	}
	/**
	 * 
	 * @param v
	 * @param bs
	 */
	public final static void toAsciiHexBytes(int v,byte[] bs,int offset,int byte_n)
	{
		for(int i = 0 ; i < byte_n ; i ++)
		{
			int tmpv = ( v>>(4*(byte_n-i-1)) ) & 0x0F ;
			bs[offset+i] = (byte)(tmpv<10?'0'+tmpv:'A'+tmpv-10); //toAsciiHexByte(tmpv) ;
		}
	}
	
	public final static int fromAsciiHexBytes(byte[] bs,int offset,int byte_n)
	{
		int r = 0 ;
		for(int i = 0 ; i < byte_n ; i ++)
		{
			byte b = bs[offset+i];
			int bv = b<'A'?b-'0':b-'A'+10; //fromAsciiHexByte(bs[offset+i]) ;
			r += bv ;
			if(byte_n-i-1>0)
				r = r << 4 ;
		}
		return r ;
	}
	
	protected static void checkStreamLenTimeout(InputStream inputs,int len,long timeout) throws IOException
	{
		long lastt = System.currentTimeMillis() ;
		int lastlen = inputs.available() ;
		long curt ;
		while((curt=System.currentTimeMillis())-lastt<timeout)
		{
			int curlen = inputs.available();
			if(curlen>=len)
				return  ;
			
			if(curlen>lastlen)
			{
				lastlen = curlen ;
				lastt = curt;
				continue ;
			}
			
			try
			{
				Thread.sleep(1);
			}
			catch(Exception ee) {}
			
			continue ;
		}
		throw new IOException("time out") ;
	}
	
	public static int readCharTimeout(InputStream inputs,long timeout) throws IOException
	{
		long curt = System.currentTimeMillis() ;
		
		while(System.currentTimeMillis()-curt<timeout)
		{
			if(inputs.available()<1)
			{
				try
				{
					Thread.sleep(1);
				}
				catch(Exception ee) {}
				
				continue ;
			}
			
			return inputs.read() ;
		}
		
		throw new IOException("time out "+timeout+"ms") ;
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
