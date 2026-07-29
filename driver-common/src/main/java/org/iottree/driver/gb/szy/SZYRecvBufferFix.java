package org.iottree.driver.gb.szy;

public class SZYRecvBufferFix
{
	private transient int bufLen = 1024 ;
	private transient byte[] dataBuf = null;
	
	private transient int firstPos = 0;
	private transient int lastPos = 0;
	
	public SZYRecvBufferFix(int buflen)
	{
		this.bufLen = buflen ;
		dataBuf = new byte[bufLen] ;
	}
	
	public void addData(byte[] bs) throws Exception
	{
		if(bs==null||bs.length<=0)
			return ;
		
		int leftlen = getBufEmptyLen();
		if(leftlen<bs.length)
			throw new Exception("buffer empty space is not enough") ;
		
		synchronized(this)
		{
			if(lastPos>=firstPos)
			{
				int flowlen = bufLen-lastPos;
				if(bs.length<=flowlen)
				{
					System.arraycopy(bs, 0, dataBuf, lastPos, bs.length);
				}
				else
				{
					System.arraycopy(bs, 0, dataBuf, lastPos, flowlen);
					System.arraycopy(bs, flowlen, dataBuf, 0, bs.length-flowlen);
				}
				
			}
			else
			{
				System.arraycopy(bs, 0, dataBuf, lastPos, bs.length);
				//lastPos += bs.length ;
			}
			
			lastPos += bs.length ;
			if(lastPos>=bufLen)
				lastPos -= bufLen ;
		}
	}
	
	private int getBufEmptyLen()
	{//one byte is no used for lastpos
		return bufLen - getBufLen()-1 ;
	}
	
	public int getBufLen()
	{
		int r = lastPos - firstPos ;
		if(r>=0)
			return r ;
		return bufLen+r ;
	}
	
	private boolean readData(byte[] buf,int offset,int len,boolean remove_readed)
	{
		int buflen = getBufLen() ;
		if(buflen<len)
			return false;
		
		synchronized(this)
		{
			if(firstPos<lastPos)
			{
				System.arraycopy(dataBuf, firstPos, buf, offset, len);
			}
			else
			{
				int flowlen = bufLen - firstPos ;
				if(flowlen>=len)
				{
					System.arraycopy(dataBuf, firstPos, buf, offset, len);
				}
				else
				{
					System.arraycopy(dataBuf, firstPos, buf, offset, flowlen);
					System.arraycopy(dataBuf, 0, buf, offset+flowlen, len-flowlen);
				}
			}
			
			if(remove_readed)
			{
				firstPos += len ;
				if(firstPos>=bufLen)
					firstPos -= bufLen ;
			}
		}
		return true ;
	}
	
	public boolean readData(byte[] buf,int offset,int len)
	{
		return readData(buf,offset,len,true) ;
	}
	
	public synchronized boolean skipLen(int len)
	{
		int buflen = getBufLen() ;
		if(buflen<len)
			return false;
		firstPos += len ;
		if(firstPos>=bufLen)
			firstPos -= bufLen ;
		return true;
	}
	
	public synchronized int readNextChar()
	{
		if(firstPos==lastPos)
			return -1 ;
		int r = (dataBuf[firstPos]) & 0xFF;
		firstPos ++ ;
		if(firstPos>=bufLen)
			firstPos -= bufLen ;
		return r ;
	}
	
	public boolean peekData(byte[] buf,int offset,int len)
	{
		return readData(buf,offset,len,false) ;
	}
}
