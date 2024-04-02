package org.iottree.driver.omron.hostlink;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.iottree.core.util.BSOutputBuf;
import org.iottree.driver.common.modbus.ModbusCmd;
import org.iottree.driver.omron.fins.FinsCmdReqR;
import org.iottree.driver.omron.fins.FinsEndCode;
import org.iottree.driver.omron.fins.FinsMode;
import org.iottree.driver.omron.hostlink.HLMsg;
import org.iottree.driver.omron.hostlink.fins.HLFinsReqMemR;
import org.iottree.driver.omron.hostlink.fins.HLFinsReqMemW;
import org.iottree.driver.omron.hostlink.fins.HLFinsRespMemR;
import org.iottree.driver.omron.hostlink.fins.HLFinsRespOnlyEnd;
import org.junit.Test;

public class FinsCmdTest
{
	@Test
    public void testHLFinsMsgMemR() throws Exception
    {
		FinsMode fm = FinsMode.getMode_CS_CJ1() ;
		HLFinsReqMemR crr = new HLFinsReqMemR(fm) ;
		crr.asReqR("CIO",true, 0,0, 5).asFinsHeaderSerial() ;
		
		//StringBuilder sb = new StringBuilder() ;
		String str = crr.packToStr();
		
		assertTrue("@00FA000000000010130000000000571*\r".equals(str)) ;
		//System.out.println(sb) ;
		
		String resp_txt = "@00FA0040000000010100000001000101" ;//43*" ;
		HLMsgReq req  = crr ;
		HLFinsRespMemR resp = (HLFinsRespMemR)req.parseFromTxt(resp_txt) ;
		//System.out.println(resp.getBitStr()) ;
		assertTrue("01011".equals(resp.getBitStr())) ;
		
		crr.asReqR("CIO",false, 0,0, 3).asFinsHeaderSerial() ;
		//sb = new StringBuilder() ;
		str = crr.packToStr();
		//System.out.println(sb) ;
		assertTrue("@00FA0000000000101B0000000000306*\r".equals(str)) ;
		//System.out.println(sb) ;
		
		resp_txt = "@00FA004000000001010000001A00000000" ;//33*
		resp = (HLFinsRespMemR)req.parseFromTxt(resp_txt) ;
		//System.out.println(resp.getWordStr()) ;
		assertTrue("26,0,0".equals(resp.getWordStr())) ;
    }
	
	@Test
	public void testHLFinsMsgMemWCIOBits() throws Exception
    {
		FinsMode fm = FinsMode.getMode_CS_CJ1() ;
		HLFinsReqMemW crr = new HLFinsReqMemW(fm) ;
		crr.asReqWBit("CIO", 100,5, 5, Arrays.asList(true,true,false,false,true)).asFinsHeaderSerial() ;
		//crr.asReqR("CIO",true, 0, 5).asFinsHeaderSerial() ;
		
		//StringBuilder sb = new StringBuilder() ;
		String str = crr.packToStr();
		
		assertTrue("@00FA0000000000102300064050005010100000174*\r".equals(str)) ;
		//System.out.println(str) ;
		
    }
	
	@Test
	public void testHLFinsMsgMemR_D() throws Exception
    {
		FinsMode fm = FinsMode.getMode_CS_CJ1() ;
		HLFinsReqMemR crr = new HLFinsReqMemR(fm) ;
		crr.asReqR("D",false, 100,0, 4).asFinsHeaderSerial(0x0A,0,0) ;
		//crr.asReqR("CIO",true, 0, 5).asFinsHeaderSerial() ;
		
		//StringBuilder sb = new StringBuilder() ;
		String str = crr.packToStr();
		                  
		assertTrue("@00FA0000A000001018200640000040A*\r".equals(str)) ;
		//System.out.println(str) ;
		
    }
	
	@Test
	public void testHLFinsMsgMemW() throws Exception
    {
		FinsMode fm = FinsMode.getMode_CS_CJ1() ;
		HLFinsReqMemW crr = new HLFinsReqMemW(fm) ;
		crr.asReqWBit("CIO", 0,0, 5, Arrays.asList(true,false,false,true,true)).asFinsHeaderSerial() ;
		//crr.asReqR("CIO",true, 0, 5).asFinsHeaderSerial() ;
		
		//StringBuilder sb = new StringBuilder() ;
		String str = crr.packToStr();
		
		assertTrue("@00FA0000000000102300000000005010000010173*\r".equals(str)) ;
		//System.out.println(str) ;
		
		String resp_txt = "@00FA004000000001020000" ;//40*"
		HLMsgReq req  = crr ;
		HLFinsRespOnlyEnd resp = (HLFinsRespOnlyEnd)req.parseFromTxt(resp_txt) ;
		String endcode = resp.getHLEndCode() ;
		FinsEndCode fins_ec = resp.getFinsEndCode() ;
		//System.out.println(endcode) ;
		assertTrue("00".equals(endcode)) ;
		assertTrue(fins_ec.isNormal()) ;
		
		crr = new HLFinsReqMemW(fm) ;
		crr.asReqWWord("CIO", 0, 5, Arrays.asList((short)0,(short)1,(short)2,(short)3,(short)4)).asFinsHeaderSerial() ;
		str = crr.packToStr();
		assertTrue("@00FA0000000000102B000000000050000000100020003000407*\r".equals(str)) ;
		//System.out.println(str) ;
		resp_txt = "@00FA004000000001020000" ;//40*"
		req  = crr ;
		resp = (HLFinsRespOnlyEnd)req.parseFromTxt(resp_txt) ;
		endcode = resp.getHLEndCode() ;
		fins_ec = resp.getFinsEndCode() ;
		//System.out.println(endcode) ;
		assertTrue("00".equals(endcode)) ;
		assertTrue(fins_ec.isNormal()) ;
    }
}
