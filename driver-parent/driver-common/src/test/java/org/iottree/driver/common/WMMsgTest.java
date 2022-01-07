package org.iottree.driver.common;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import org.iottree.core.util.Convert;
import org.iottree.driver.nbiot.msg.WMMsg;
import org.iottree.driver.nbiot.msg.WMMsgReceipt;
import org.iottree.driver.nbiot.msg.WMMsgReport;
import org.iottree.driver.nbiot.msg.WMMsgValveReq;
import org.iottree.driver.nbiot.msg.WMMsgValveResp;
import org.junit.Test;

public class WMMsgTest
{
	static byte[] bs1 = Convert.hexStr2ByteArray("A1 68 11 00 01 08 11 11 66 66 81 01 21 01 01 08 11 32 00 00 09 41 2D 96 72 95 96 72 95 00 18 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 94 96 72 95 FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 7F FF 01 66 34 36 30 30 34 39 30 39 31 31 30 39 38 36 33 38 36 32 35 39 32 30 35 30 30 37 39 30 39 37 38 39 38 36 30 34 37 30 31 39 32 30 38 31 35 31 34 38 36 33 11 FF A9 00 12 9A 16");
	
	static byte[] bs_t1 = Convert.hexStr2ByteArray("A1 68 11 00 01 08 11 11 66 66 81 10 21 01 01 00 00 56 94 96 72 95 2D FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 68 01 34 36 30 30 34 39 30 39 31 31 30 39 38 36 32 38 36 32 35 39 32 30 35 30 30 38 33 33 35 34 38 39 38 36 30 34 37 30 31 39 32 30 38 31 35 31 34 38 36 32 13 AB FF 0F 00 29 16");
	
	@Test
    public void testMsgParse() throws IOException
    {
    	    	
    	ByteArrayInputStream bais = new ByteArrayInputStream(bs1) ; 
    	
    	WMMsgReport msg = (WMMsgReport)WMMsg.parseMsg(bais) ;
    	assertTrue(msg!=null);
    	
    	
    	byte[] maddr = msg.getMeterAddr() ;
    	
    	assertTrue(msg.getCurMeterVal()==941) ;
    	String dtstr = Convert.toFullYMDHMS(msg.getMsgDTDate()) ;
    	System.out.println(dtstr) ;
    	//
    	WMMsgReceipt rpt = new WMMsgReceipt() ;
    	//rpt.setMeterAddr(new byte[] {1, 2,3,4,5,6,7,8});
    	rpt.setMeterAddr(maddr);
    	//rpt.set,new byte[] {1,1});
    	Date dt = Convert.toCalendar("2021-12-28 21:57:55").getTime() ;
    	rpt.setMsgDT(dt);
    	
    	rpt.setReceiptTp(WMMsgReceipt.TP_NOR);
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	rpt.writeOut(baos);
    	byte[] bs = baos.toByteArray() ;
    	String tmps = Convert.byteArray2HexStr(bs);
    	System.out.println(tmps) ;
    	assertTrue("A16811000108111166660101211228215755009A16".equals(tmps));
    	//A16801020304050607080101211228215755B616
    	
    }
	
	@Test
	public void test2() throws IOException
	{
		WMMsgValveReq req= new WMMsgValveReq() ;
		req.setMeterAddr(new byte[] {0x10, 00,0,0x12,0x34,0x56,0x78,(byte)0x90});
		System.out.println("valve req="+req.toWriteOutHexStr()) ;
		
		byte[] tmpbs = Convert.hexStr2ByteArray("A1 68 10 00 00 12 34 56 78 90 82 02 55 04 16");
		ByteArrayInputStream bais = new ByteArrayInputStream(tmpbs) ; 
		WMMsgValveResp msg = (WMMsgValveResp)WMMsg.parseMsg(bais) ;
		System.out.println("valve resp="+msg) ;
    	//assertTrue(msg!=null);
		
	}
}
