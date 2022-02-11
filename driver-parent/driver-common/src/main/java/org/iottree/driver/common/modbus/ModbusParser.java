package org.iottree.driver.common.modbus;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class ModbusParser
{
	

	protected static void readFill(InputStream ins,byte[] bs,int offset,int len) throws IOException
	{
		int rlen = 0 ;
		int r = 0 ;
		while((r=ins.read(bs, rlen+offset, len-rlen))>=0)
		{
			rlen += r ;
			if(rlen==len)
				return ;
		}
		
		if(r<0)
			throw new IOException("end of stream") ;
	}
}
