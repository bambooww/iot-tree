package org.iottree.driver.mitsubishi.fx;

import java.io.IOException;
import java.io.InputStream;

public class FxMsgRespR extends FxMsg
{
	int byteNum ;
	//boolean[] regVals = null ;
	byte[] byteBuf = null ;
	
	boolean readOk=false;
	String errInf = null ;
	
	public FxMsgRespR(int bytenum)
	{
		this.byteNum = bytenum ;
		//regVals = new boolean[regnum] ;
	}
	
	@Override
	public byte[] toBytes()
	{
		
		return null;
	}
	
	/**
	 * read PPI Frame  0x68 le ler 0x68 [bs] fcs ed
	 * @param inputs
	 * @param timeout
	 * @return
	 * @throws IOException
	 */
	public boolean readFromStream(InputStream inputs,long timeout) throws IOException
	{
		int st = 0 ;
		int c;
		
		int len = 3+this.byteNum*2 ;
		byte[] ret = new byte[len] ;
		
		//long curt = System.currentTimeMillis() ;
		while(true)
		{
			switch(st)
			{
			case 0://no start
				do
				{
					c = readCharTimeout(inputs,timeout);
					if(c==NCK)
					{
						readOk=false;
						errInf = "recv NAK";
						return false;
					}
				}
				while(c!=STX);
				st = 1 ;
				break ;
			case 1:
				checkStreamLenTimeout(inputs,len,timeout) ;
				inputs.read(ret) ;
				
				int retcrc = fromAsciiHexBytes(ret,len-2,2) ;
				if(ret[len-3]!=ETX)
					throw new IOException("no ETX found") ;
				int crc = calCRC(ret,0,len-2) ;
				if(retcrc!=crc)
					throw new IOException("check crc error") ;
				
				this.byteBuf = new byte[this.byteNum] ;
				
				for(int i = 0 ; i < this.byteNum ; i ++)
				{
					int bt = fromAsciiHexBytes(ret,i*2,2) ;
					this.byteBuf[i] = (byte)bt ;
				}
				this.readOk = true ;
				return true ;
			}//end of switch
		}
	}
	
	public byte[] getRetData()
	{
		return byteBuf;
	}
}
