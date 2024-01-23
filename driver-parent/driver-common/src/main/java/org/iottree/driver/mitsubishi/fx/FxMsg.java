package org.iottree.driver.mitsubishi.fx;

import java.io.IOException;
import java.io.InputStream;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

/**
 * PLC inner address defintion
 * 
 * D:   PLC-Address*2+1000H;   数据寄存器D bit16
    T:   PLC-Address+00C0H; //timer  bit16
    C:   PLC-Address*2+01C0H; //count  bit16
    S:   PLC-Address*3;  状态继电器
    M:   PLC-Address*2+0100H;   辅助继电器
    Y:   PLC-Address+00A0H; out
    X:   PLC-Address+0080H; input(只能读不能写，输入寄存器必须由外部信号驱动)
    
         PLC-Address元件是指最低位开始后的第N个元件的位置。
         
 * @author jason.zhu
 * 
 *   》02 30 30 30 38 30 30 35 03 36 30
 * 《 02 30 30 30 30 30 30 30 30 30 30 03 45 33
 *
 */
public abstract class FxMsg
{
	public static ILogger log = LoggerManager.getLogger(FxMsg.class) ;
	
	public static ILogger log_w = LoggerManager.getLogger(FxMsg.class.getCanonicalName()+"_w") ;
	/**
	 * one byte acknowledgement
	 */
	public static final byte ENQ = 0x05;
	
	public static final byte STX = 0x02;
	
	public static final byte ETX = 0x03;
	
	public static final byte ACK = 0x06;
	
	public static final byte NCK  =0x15;
	
	public static final byte CMD_BR = 0x30 ;
	public static final byte CMD_BW = 0x31 ;
	public static final byte CMD_FORCE_ON = 0x37 ;
	public static final byte CMD_FORCE_OFF = 0x38 ;
	
	//int stationCode = 1 ;
	
	//int pcCode = 1 ;
	
	//String cmd = "BR" ;
	
	public FxMsg()
	{}
	
	//private abstract  
	
//	public FxMsg asStationCode(int station_c)
//	{
//		this.stationCode = station_c ;
//		return this ;
//	}
//	
//	public FxMsg asPCCode(int c)
//	{
//		this.pcCode = c ;
//		return this ;
//	}
	
	
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
	
	
	
	
//	/**
//	 * 
//	 * @param addr   may be VB100  MB12  Q0.1  I1.1
//	 * @param num
//	 * @return
//	 */
//	public static PPIMsgReq createReqReadByAddr(boolean bwrite,short da,short sa,String addr,ValTP vtp)
//	{
//		if(Convert.isNullOrEmpty(addr))
//			return null ;
//		
//		StringBuilder failedr = new StringBuilder() ;
//		PPIAddr ppiaddr = PPIAddr.parsePPIAddr(addr,vtp,failedr) ;
//		if(ppiaddr==null)
//			return null ;
//		
//		PPIMsgReqR ret  = new PPIMsgReqR() ;
//		ret.sa = sa;
//		ret.da = da ;
//		ret.memTp = ppiaddr.getMemTp() ;
//		ret.offsetBytes = ppiaddr.getOffsetBytes();
//		ret.inBit = ppiaddr.getInBits();
//		return ret ;
//	}
	
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
