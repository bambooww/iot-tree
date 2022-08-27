package org.iottree.driver.common;

import java.io.IOException;

import org.iottree.driver.s7.eth.S7Addr;
import org.junit.Test;

public class S7Test
{
	@Test
    public void testAddr() throws Exception
    {
		String[] addrs = new String[] {"I0.1","Q0","IX0.1","DB200,W2","DB200,DBD0","DB200,DBD0.3"} ;
		for(String addr:addrs)
		{
			StringBuilder failedr = new StringBuilder() ;
			S7Addr apt = S7Addr.parseS7Addr(addr,null, failedr) ;
			if(apt==null)
			{
				System.out.println("parse "+addr+" failed:"+failedr) ;
			}
			else
			{
				System.out.println(addr+">>>>"+apt) ;
			}
			
		}
    }
}
