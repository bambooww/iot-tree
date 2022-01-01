package org.iottree.driver.nbiot.msg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.iottree.core.util.Convert;

public abstract class WMMsg
{
	/**
	 * 表地址8字节
	 */
	byte[] meterAddr = null ;
	
	/**
	 * 功能码两字节
	 */
	byte[] func = null ;
	
	
	private transient long parseReadTO = 1000 ;
	
	
	public WMMsg()
	{}
	
	
	public void setParseReadTimeout(long toms)
	{
		this.parseReadTO = toms ;
	}
	
	public void setMeterAddr(byte[] addr)
	{
		if(addr.length!=8)
		{
			throw new IllegalArgumentException("invalid addr info") ;
		}
		this.meterAddr = addr ;
	}
	
	public void setMsgFunc(byte[] func)
	{
		if(func.length!=2)
		{
			throw new IllegalArgumentException("invalid func info") ;
		}
		this.func = func ;
	}
	
	public byte[] getMeterAddr()
	{
		return this.meterAddr ;
	}
	
	public byte[] getFuncCode()
	{
		return this.func ;
	}
	
	
	protected static final int checkSum(byte[] addr,byte[] func,List<byte[]> body_bs)
	{
		int r = 0x68,i ;
		
		for(i = 0 ; i < 8 ; i ++)
			r += (addr[i] & 0xFF) ;
		
		for(i = 0 ; i < 2 ; i ++)
			r += (func[i] & 0xFF) ;
		
		if(body_bs!=null)
		{
			for(byte[] bs:body_bs)
			{
				for(i = 0 ; i < bs.length ; i ++)
					r += (bs[i] & 0xFF) ;
			}
		}
		
		return r ;
	}
	
	private void writeOutInner(OutputStream outputs) throws IOException
	{
		final byte[] bs01 = new byte[] {(byte)0xA1,(byte)0x68} ;
		ArrayList<byte[]> bss = getMsgBody() ;
		int chksum = checkSum(meterAddr,func,bss) & 0xFF;
		outputs.write(bs01);
		outputs.write(meterAddr);
		outputs.write(func);
		//outputs.write(msgDT);
		if(bss!=null)
		{
			for(byte[] bs:bss)
				outputs.write(bs);
		}
		
		outputs.write(chksum);
		outputs.write(0x16);
	}
	
	public byte[] toWriteOutBytes() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		writeOutInner(baos);
    	return baos.toByteArray() ;
	}
	
	public String toWriteOutHexStr() throws IOException
	{
		byte[] bs = toWriteOutBytes() ;
    	return Convert.byteArray2HexStr(bs);
	}
	
	
	
	public final void writeOut(OutputStream outputs) throws IOException
	{
		byte[] bs = toWriteOutBytes() ;
		outputs.write(bs);
	}
	

	final int bcd2int(byte b)
	{
		String s = Integer.toHexString(((int)b) & 0xFF);
		return Integer.parseInt(s) ;
	}
	
	final byte int2bcd(int v)
	{
		return Byte.parseByte(""+v, 16) ;
	}
	
	final long bcd2long(byte[] bs,int offset,int len)
	{
		String tmps = "" ;
		for(int i = 0 ; i < len ; i ++)
		{
			int vh = ((bs[i+offset] >>4) & 0x0F) ;
			int vl = (bs[i+offset] & 0x0F) ;
			tmps += vh ;
			tmps += vl ;
		}
		return Long.parseLong(tmps) ;
	}
	
	protected static final byte[] readLenTimeout(InputStream inputs,int rlen,long to_ms) throws IOException
	{
		byte[] ret = new byte[rlen] ;
		
		long st = System.currentTimeMillis() ;
		while(inputs.available()<rlen)
		{
			try
			{
			Thread.sleep(1);
			}
			catch(Exception e) {}
			
			if(System.currentTimeMillis()-st>=to_ms)
				break ;
		}
		
		int len = inputs.available();
		if(len>=rlen)
		{
			inputs.read(ret) ;
			return ret ;
		}
		
		throw new IOException("time out") ;
	}
	
	protected final byte[] readLenTimeout(InputStream inputs,int rlen) throws IOException
	{
		return readLenTimeout(inputs,rlen,parseReadTO) ;
	}
	
	protected ArrayList<byte[]> getMsgBody()
	{
		return new ArrayList<byte[]>() ;
	}
	
	
	
	
	protected abstract ArrayList<byte[]> parseMsgBody(InputStream inputs) throws IOException;
	
	
	public static WMMsg parseMsg(InputStream inputs) throws IOException
	{
		
		byte[] addr=  null;
		byte[] func = null ;
		int st = 0 ;
		ArrayList<byte[]> body_bs = null ;
		WMMsg msg = null ;
		do
		{
			
			switch(st)
			{
			case 0:
				if(inputs.available()<12)
					return null ;
				int c = inputs.read() ;
				if(c!=0xA1)
				{
					if(inputs.available()<12)
						return null;
					else
						continue ;
				}
				st = 1 ;
				break ;
			case 1:
				c = inputs.read() ;
				if(c!=0x68)
				{
					st = 0 ;
					return null ;
				}
				st = 2 ;
				break ;
			case 2://read addr
				addr = new byte[8] ;
				inputs.read(addr) ;
				st = 3 ;
				break ;
			case 3:// read func
				func = new byte[2] ;
				inputs.read(func) ;

				int f1 = (((int)func[0])&0xFF) ;
				int f2 = (((int)func[1])&0xFF) ;
				if(f1 ==0x81 && 
						(f2==0x01 || f2==0x10))
				{//report
					msg = new WMMsgReport() ;
					msg.setMeterAddr(addr);
					msg.setMsgFunc(func);
				}
				else
				{
					return null ;
				}
				
				st = 4 ;//
				break ;
			case 4://read body
				body_bs =msg.parseMsgBody(inputs) ; 
				if(body_bs==null)
					return null ;
				
				st = 5 ;
				break ;
			case 5://end
				if(inputs.available()<2)
					return null;
				byte[] endbs= new byte[2] ;
				inputs.read(endbs) ;
				int chksum = checkSum(addr,func,body_bs) ;
				if( (((int)endbs[0])&0xFF)==(chksum & 0xFF) && endbs[1]==0x16)
					return msg ;
				return null ;
			}
			
		}
		while(true) ;
	}
	
	
	public String toString()
	{
		String ret="addr="+Convert.byteArray2HexStr(this.meterAddr) ;
		ret += " func="+Convert.byteArray2HexStr(this.func) ;
		
		return ret ;
	}
	
	
}
