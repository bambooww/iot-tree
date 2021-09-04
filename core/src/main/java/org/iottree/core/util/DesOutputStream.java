package org.iottree.core.util;

import java.io.*;

public class DesOutputStream  extends OutputStream
{
	static final int DEF_BUF_LEN=15 ;
	byte[] key = null ;
	DesJava desJ = null ;
	
	OutputStream innerOS = null ;
	
	byte[] buf = new byte[DEF_BUF_LEN];
	
	int bufLen = 0 ;
	
	public DesOutputStream(byte[] key,OutputStream os) throws Exception
	{
		setKey( key);
		
		innerOS = os ;
	}
	
	public DesOutputStream(String key,OutputStream os) throws Exception
	{
		setKey( key.getBytes());
		innerOS = os ;
	}
	
	public OutputStream getInnerOutputStream()
	{
		return innerOS;
	}
	
	public void setKey(byte[] key) throws Exception
	{
		this.key = key ;
		desJ = new DesJava(key) ;
	}
	
	@Override
	synchronized public void write(int c) throws IOException
	{
		buf[bufLen] = (byte)c ;
		bufLen ++ ;
		if(bufLen<DEF_BUF_LEN)
		{
			return ;
		}
		
		this.flush() ;
	}
	
	public void flush() throws IOException
	{
		if(bufLen==0)
			return ;
		
		try
		{
			byte[] dd = desJ.encrypt(buf,0, bufLen) ;
			
			innerOS.write(dd.length) ;//write len
			innerOS.write(dd) ;
			innerOS.flush() ;
			//System.out.println("data out len="+bufLen+" des out len="+dd.length) ;
			bufLen=0;
		}
		catch(Exception ee)
		{
			throw new IOException("encrypt err:"+ee.getMessage());
		}
    }
	
	@Override
	public void close() throws IOException
	{
		innerOS.close() ;
	}

}
