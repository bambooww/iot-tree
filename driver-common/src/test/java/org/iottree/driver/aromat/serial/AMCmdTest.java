package org.iottree.driver.aromat.serial;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AMCmdTest
{
	@Test
	public void test1() throws Exception
	{
		AMMsgReqRCS req = new AMMsgReqRCS() ;
		req.asPlcAddr(1);
		req.asContactCode(AMMsgReqRC.CONTACT_CODE_X, 7) ;
		
		String str = req.packToStr();
		System.out.println(str) ;
		
		//assertTrue("@00FA000000000010130000000000571*\r".equals(str)) ;
	}
}
