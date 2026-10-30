package org.iottree.driver.zigbee.zstack;

import java.io.IOException;

import org.iottree.core.util.Convert;
import org.junit.Test;

public class ZStackMsgTest
{
	private static byte[] hex2bs(String hex)
	{
		hex = hex.replaceAll("-", "") ;
		return Convert.hexStr2ByteArray(hex) ;
	}
	
	@Test
    public void testMsgTest() throws IOException
    {
		StringBuilder failedr = new StringBuilder() ;
		byte[] bs = hex2bs("FE-00-21-01-20") ;
		MsgNor m = MsgNor.parseFrom(bs,failedr) ;
		System.out.print(m+"  failed="+failedr);
    }
}
