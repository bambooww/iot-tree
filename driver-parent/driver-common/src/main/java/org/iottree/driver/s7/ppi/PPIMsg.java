package org.iottree.driver.s7.ppi;

import java.io.IOException;
import java.io.InputStream;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;

/**
 * 
 * @author jason.zhu
 */
public abstract class PPIMsg
{
	public static ILogger log = LoggerManager.getLogger(PPIMsg.class) ;
	
	public static ILogger log_w = LoggerManager.getLogger(PPIMsg.class.getCanonicalName()+"_w") ;
	/**
	 * one byte acknowledgement
	 */
	public static final short PK_ACK = 0xE5;
	
	public static final short SD_REQ_CONFIRM = 0x10;
	
	public static final short SD_REQ = 0x68;
	
	
	public static final short ED = 0x16;
	
	public PPIMsg()
	{}
	
	protected abstract short getStartD() ;
	
	protected short getEndD()
	{
		return ED ;
	}
	
	public abstract byte[] toBytes() ;
	

	protected static byte calChkSum(byte[] bs,int idx,int num)
	{
		int r = 0 ;
		for(int i = 0 ; i < num ; i ++)
			r += ((int)bs[idx+i]) & 0xff ;
		return (byte)(r & 0xFF) ;
	}
	
	private static void checkStreamLenTimeout(InputStream inputs,int len,long timeout) throws IOException
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
	
	/**
	 * read PPI Frame  0x68 le ler 0x68 [bs] fcs ed
	 * @param inputs
	 * @param timeout
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFromStream(InputStream inputs,long timeout) throws IOException
	{
		int st = 0 ;
		int le = 0 ;
		int c;
		
		byte[] ret = null ;
		//boolean bend = false; 
		long curt = System.currentTimeMillis() ;
		while(true)
		{
			if(st<4)
			{
				if(System.currentTimeMillis()-curt>timeout*2)
					throw new IOException("time out") ;
			}
			
			switch(st)
			{
			case 0://no start
				do
				{
					c = readCharTimeout(inputs,timeout);
					if(c==0xE5)
						return new byte[] {(byte)0xE5} ;
				}
				while(c!=0x68);
				st = 1 ;
				break ;
			case 1:
				le = readCharTimeout(inputs,timeout);
				st=2 ;
				break ;
			case 2:
				int ler = readCharTimeout(inputs,timeout);
				if(le!=ler)
				{
					st = 0 ;
					break;
				}
				st = 3 ;
				break ;
			case 3:
				c = readCharTimeout(inputs,timeout);
				if(c!=0x68)
				{
					st = 0 ;
					break ;
				}
				st = 4;
				break ;
			case 4://
				ret = new byte[le+6] ;
				ret[0] = ret[3] = 0x68;
				ret[1] = ret[2] = (byte)le ;
				checkStreamLenTimeout(inputs,le+2,timeout) ;
				inputs.read(ret, 4, le+2) ;
				if(ret[le+4]!=calChkSum(ret,4,le))
					return null ;
				if(ret[le+5]!=0x16)
					return null ;
				return ret ;
			}//end of switch
		}
	}
	
	
	/**
	 * 
	 * @param addr   may be VB100  MB12  Q0.1  I1.1
	 * @param num
	 * @return
	 */
	public static PPIMsgReq createReqReadByAddr(boolean bwrite,short da,short sa,String addr,ValTP vtp)
	{
		if(Convert.isNullOrEmpty(addr))
			return null ;
		
		StringBuilder failedr = new StringBuilder() ;
		PPIAddr ppiaddr = PPIAddr.parsePPIAddr(addr,vtp,failedr) ;
		if(ppiaddr==null)
			return null ;
		
		PPIMsgReqR ret  = new PPIMsgReqR() ;
		ret.sa = sa;
		ret.da = da ;
		ret.memTp = ppiaddr.getMemTp() ;
		ret.offsetBytes = ppiaddr.getOffsetBytes();
		ret.inBit = ppiaddr.getInBits();
		return ret ;
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
