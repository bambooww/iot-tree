package org.iottree.driver.common;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.iottree.driver.s7.ppi.PPIMsgReqR;
import org.iottree.driver.s7.ppi.PPIMsgReqW;
import org.iottree.driver.s7.ppi.PPIMemTp;
import org.iottree.driver.s7.ppi.PPIMemValTp;
import org.iottree.driver.s7.ppi.PPIMsgReqConfirm;
import org.junit.Test;

public class PPIMsgTest
{
	@Test
    public void testR() throws IOException
    {
		PPIMsgReqR mreq = new PPIMsgReqR();
		mreq.withAddr("Q0",ValTP.vt_byte).withSorAddr((short)0)
			.withDestAddr((short)2)
			;
		byte[] bs = mreq.toBytes() ;
		String tmps = Convert.byteArray2HexStr(bs," ") ;
		System.out.println("read Q0="+tmps) ;
    	assertTrue("68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10 02 00 01 00 00 82 00 00 00 65 16".equals(tmps));
    	
    	mreq = new PPIMsgReqR();
		mreq.withAddr("Q0.1",ValTP.vt_bool).withSorAddr((short)0)
			.withDestAddr((short)2)
			;
		bs = mreq.toBytes() ;
		tmps = Convert.byteArray2HexStr(bs," ") ;
		System.out.println("read Q0.1="+tmps) ;
		
    	mreq = new PPIMsgReqR();
		mreq.withAddrByte(PPIMemTp.V, 100,-1,3).withSorAddr((short)0)
			.withDestAddr((short)2);
			//.withAddr("VB100",ValTP.vt_uint16);
		bs = mreq.toBytes() ;
		tmps = Convert.byteArray2HexStr(bs," ") ;
		System.out.println(tmps) ;
    	assertTrue("68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10 02 00 03 00 01 84 00 03 20 8D 16".equals(tmps));
    	
    	PPIMsgReqConfirm reqc = new PPIMsgReqConfirm() ;
    	reqc.withSorAddr((short)0)
			.withDestAddr((short)2);
    	bs = reqc.toBytes() ;
    	
    	tmps = Convert.byteArray2HexStr(bs," ") ;
		System.out.println(tmps) ;
    	assertTrue("10 02 00 5C 5E 16".equals(tmps));
    	
    }
	
	@Test
	public void testW() throws IOException
    {
		PPIMsgReqW mreq = new PPIMsgReqW();
		mreq.withAddr("Q0",ValTP.vt_byte).withWriteVal(PPIMemValTp.B,0xFF).withSorAddr((short)0)
			.withDestAddr((short)2)
			;
			
		
		byte[] bs = mreq.toBytes() ;
		String tmps = Convert.byteArray2HexStr(bs," ") ;
		System.out.println("Q0=0xFF "+tmps) ;
    	assertTrue("68 20 20 68 02 00 7C 32 01 00 00 00 00 00 0E 00 05 05 01 12 0A 10 02 00 01 00 00 82 00 00 00 00 04 00 08 FF 86 16".equals(tmps));
    	
    	mreq = new PPIMsgReqW();
		mreq.withAddr("Q0",ValTP.vt_byte).withWriteVal(PPIMemValTp.B,0x00).withSorAddr((short)0)
			.withDestAddr((short)2)
			;
		bs = mreq.toBytes() ;
		tmps = Convert.byteArray2HexStr(bs," ") ;
		System.out.println("Q0=0x00 "+tmps) ;
		
		mreq = new PPIMsgReqW();
		mreq.withAddr("Q0.1",ValTP.vt_bool).withWriteVal(PPIMemValTp.BIT,0x01).withSorAddr((short)0)
			.withDestAddr((short)2)
			;
		bs = mreq.toBytes() ;
		tmps = Convert.byteArray2HexStr(bs," ") ;
		System.out.println("Q0.1=0x01 "+tmps) ;
		
		mreq = new PPIMsgReqW();
		mreq.withAddr("Q0.1",ValTP.vt_bool).withWriteVal(PPIMemValTp.B,0x00).withSorAddr((short)0)
			.withDestAddr((short)2)
			;
		bs = mreq.toBytes() ;
		tmps = Convert.byteArray2HexStr(bs," ") ;
		System.out.println("Q0.1=0x00 "+tmps) ;
		
    	mreq = new PPIMsgReqW();
		mreq.withAddr("VB100",ValTP.vt_byte).withWriteVal(PPIMemValTp.B,0x12).withSorAddr((short)0)
			.withDestAddr((short)2)
			;
			
		
		bs = mreq.toBytes() ;
		tmps = Convert.byteArray2HexStr(bs," ") ;
		System.out.println(tmps) ;
    	assertTrue("68 20 20 68 02 00 7C 32 01 00 00 00 00 00 0E 00 05 05 01 12 0A 10 02 00 01 00 01 84 00 03 20 00 04 00 08 12 BF 16".equals(tmps));
    	
    	PPIMsgReqConfirm reqc = new PPIMsgReqConfirm() ;
    	reqc.withSorAddr((short)0)
			.withDestAddr((short)2);
    	bs = reqc.toBytes() ;
    	
    	tmps = Convert.byteArray2HexStr(bs," ") ;
		System.out.println(tmps) ;
    	assertTrue("10 02 00 5C 5E 16".equals(tmps));
    	
    }
}
