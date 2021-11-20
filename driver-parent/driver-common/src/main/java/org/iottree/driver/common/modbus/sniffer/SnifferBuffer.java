package org.iottree.driver.common.modbus.sniffer;

import java.util.LinkedList;

public class SnifferBuffer
{
	private transient LinkedList<byte[]> bsList = new LinkedList<>();
	
	private transient int firstPos = 0;
	
	public SnifferBuffer()
	{}
	
	public void addData(byte[] bs)
	{
		if(bs==null||bs.length<=0)
			return ;
		
		synchronized(this)
		{
			bsList.addLast(bs);
		}
	}
	
	public int getBufLen()
	{
		int r = 0 ;
		for(byte[] bs:bsList)
		{
			r += bs.length ;
		}
		return r - firstPos;
	}
	
	private boolean readData(byte[] buf,int offset,int len,boolean remove_readed)
	{
		int buflen = getBufLen() ;
		if(buflen<len)
			return false;
		
		int fpos = this.firstPos;
		int ln = bsList.size() ;
		int i ;
		for(i = 0 ; i < ln ; i ++)
		{
			if(len==0)
				break ;
			
			byte[] bs = bsList.get(i) ;
			int rlen = bs.length-fpos ;
			if(rlen>len)
			{
				if(buf!=null)
					System.arraycopy(bs, fpos, buf, offset, len);
				fpos += len ;
				break ;
			}
			
			//
			if(buf!=null)
				System.arraycopy(bs, fpos, buf, offset, rlen);
			fpos =0 ;
			offset += rlen ;
			len -= rlen ;
		}
		
		if(remove_readed)
		{
			synchronized(this)
			{
				for(int j = 0 ; j < i ; j ++)
				{
					bsList.removeFirst();
				}
				
				this.firstPos = fpos ;
			}
		}
		return true ;
	}
	
	public boolean readData(byte[] buf,int offset,int len)
	{
		return readData(buf,offset,len,true) ;
	}
	
	public boolean skipLen(int len)
	{
		return readData(null,0,len,true) ;
	}
	
	public int readNextChar()
	{
		if(bsList.size()<=0)
			return -1 ;
		
		byte[] bs = bsList.getFirst() ;
		byte b = bs[this.firstPos] ;
		synchronized(this)
		{
			this.firstPos ++ ;
			if(this.firstPos==bs.length)
			{
				bsList.removeFirst() ;
				this.firstPos = 0 ;
			}
		}
		return ((int)b)&0xFF ;
	}
	
	public boolean peekData(byte[] buf,int offset,int len)
	{
		return readData(buf,offset,len,false) ;
	}
}
