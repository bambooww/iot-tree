package org.iottree.driver.common;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.iottree.core.util.Convert;
import org.iottree.driver.mitsubishi.fx.FxMsg;
import org.iottree.driver.mitsubishi.fx.FxMsgReqR;
import org.junit.Test;

public class FxMsgTest
{
	@Test
    public void testAsciiHex() throws IOException
    {
		byte[] bs2 = new byte[6] ;
		FxMsg.toAsciiHexBytes(5, bs2, 0, 2);
		FxMsg.toAsciiHexBytes(0x40, bs2, 2, 4);
		String tmps = new String(bs2) ;
		//System.out.println(tmps) ;
		assertTrue("050040".equals(tmps));
		assertTrue(FxMsg.fromAsciiHexBytes(bs2, 0, 2)==5) ;
		assertTrue(FxMsg.fromAsciiHexBytes(bs2, 2, 4)==0x40) ;
		
		FxMsg.toAsciiHexBytes(0x15, bs2, 0, 2);
		FxMsg.toAsciiHexBytes(0x2140, bs2, 2, 4);
		tmps = new String(bs2) ;
		//System.out.println(tmps) ;
		assertTrue("152140".equals(tmps));
		assertTrue(FxMsg.fromAsciiHexBytes(bs2, 0, 2)==0x15) ;
		assertTrue(FxMsg.fromAsciiHexBytes(bs2, 2, 4)==0x2140) ;
		
		FxMsg.toAsciiHexBytes(0x1, bs2, 0, 2);
		FxMsg.toAsciiHexBytes(0x0000, bs2, 2, 4);
		tmps = new String(bs2) ;
		System.out.println(tmps) ;
		assertTrue("010000".equals(tmps));
		assertTrue(FxMsg.fromAsciiHexBytes(bs2, 0, 2)==0x1) ;
		assertTrue(FxMsg.fromAsciiHexBytes(bs2, 2, 4)==0x0) ;
    }
	
	@Test
	public void test1()
	{
		FxMsgReqR fmBR = new FxMsgReqR() ;
		//fmBR.asAddrTp('X');//.asStationCode(5).asPCCode(0xff);
		fmBR.asStartAddr(0x80,0).asByteNum(5);
		
		String tmps = new String(fmBR.toBytes()) ;
		System.out.println(tmps) ;
		
		//fmBR.asAddrTp('X');//.asStationCode(1).asPCCode(0xff);
		fmBR.asStartAddr(0x80,0).asByteNum(5);
		
		byte[] bs = fmBR.toBytes() ;
		tmps = new String(bs) ;
		System.out.println(tmps) ;
		tmps = Convert.byteArray2HexStr(bs," ") ;
		System.out.println(tmps) ;
	}
}
