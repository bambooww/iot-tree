package org.iottree.driver.omron.fins;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.iottree.core.basic.ByteOrder;
import org.iottree.core.util.IBSOutput;
import org.iottree.core.util.xmldata.DataUtil;

public abstract class FinsMsg
{
	protected static final byte[] FINS = new byte[] {'F','I','N','S'} ;
	
	public FinsMsg()
	{
	}
	
//	/**
//	 * fins tcp will has head
//	 * @return
//	 */
//	public abstract boolean isTcp() ;
	
	// -- util func
	
	protected static final byte[] int2bytes(int i)
	{
		return DataUtil.intToBytes(i,ByteOrder.LittleEndian) ;
	}
	
	protected static final int bytes2int(byte[] bs,int offset)
	{
		return DataUtil.bytesToInt(bs,offset, ByteOrder.LittleEndian);
	}
	
	protected static final void int2byte3(int i,byte[] bytes,int offset)
	{
		bytes[offset+3] = (byte) (i & 0xFF);
		i = i >>> 8;
		bytes[offset+2] = (byte) (i & 0xFF);
		i = i >>> 8;
		bytes[offset+1] = (byte) (i & 0xFF);
		//i = i >>> 8;
		//bytes[offset] = (byte) (i & 0xFF);
		
	}
	
	protected static final byte[] short2bytes(short i)
	{
		return DataUtil.shortToBytes(i) ;
	}
	
	protected static final void short2bytes(short i,byte[] bs,int offset)
	{
		DataUtil.shortToBytes(i,bs,offset) ;
	}
	
	protected static final short bytes2short(byte[] bs,int offset)
	{
		return DataUtil.bytesToShort(bs, offset, ByteOrder.LittleEndian) ;
	}
	
	//----  Handshake support
	
	static byte[] Handshake_createReq(short client_pc_last_ip)
	{
		byte[] rets = new byte[20] ;
		rets[0]='F' ;
		rets[1]='I' ;
		rets[2]='N' ;
		rets[3]='S' ;
		for(int i = 4; i< 20 ; i ++)
			rets[i]=0 ;
		rets[7]=0x0C;
		rets[19] = (byte)client_pc_last_ip ;
		return rets ;
	}
	
	/**
	 * failed may time out IOException.or return null ;
	 */
	static Short Handshake_checkResp(InputStream inputs,short client_pc_last_ip,long timeout) throws IOException
	{
		checkStreamLenTimeout(inputs, 24, timeout);
		
		byte[] bs = new byte[24] ;
		inputs.read(bs) ;
		
		if(bs[0]!='F' || bs[1]!='I' || bs[2] != 'N' || bs[3]!='S')
			return null ;
		if(bs[7]!=0x10)
			return null ;
		if( (0xFF & bs[19])!=client_pc_last_ip)
			return null ;
		return (short)(0xFF & bs[23]) ;
	}
	
	//
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
		throw new IOException("time out with "+timeout) ;
	}

	public static int readByteTimeout(InputStream inputs,long timeout) throws IOException
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
	
	
	public static void readBytesTimeout(InputStream inputs,long timeout,byte[] buf,int offset,int len) throws IOException
	{
		for(int i = 0 ; i < len ; i ++)
		{
			byte b = (byte)readByteTimeout(inputs, timeout) ;
			buf[offset+i] = b ;
		}
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
