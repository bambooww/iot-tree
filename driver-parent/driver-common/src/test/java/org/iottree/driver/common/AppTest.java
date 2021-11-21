package org.iottree.driver.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.iottree.core.util.Convert;
import org.iottree.driver.common.modbus.sniffer.SnifferBuffer;
import org.iottree.driver.common.modbus.sniffer.SnifferBufferFix;
import org.iottree.driver.common.modbus.sniffer.SnifferRTUCh;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
    
    @Test
    public void testSnifferBuffer()
    {
    	byte[] buf = new byte[10] ;
    	for(int i = 0 ; i < buf.length ; i ++)
    		buf[i] = 0 ;
    	SnifferBuffer snb = new SnifferBuffer() ;
    	assertTrue(snb.getBufLen()==0);
    	
    	assertFalse(snb.readData(buf, 0, 1));
    	
    	snb.addData("123".getBytes());
    	snb.addData("456".getBytes());
    	assertTrue(snb.getBufLen()==6);
    	
    	assertTrue(snb.peekData(buf, 0, 2)) ;
    	assertTrue(new String(buf,0,2).equals("12"));
    	
    	assertTrue(snb.peekData(buf, 0, 5)) ;
    	assertTrue(new String(buf,0,5).equals("12345"));
    	
    	assertTrue(snb.readData(buf, 0, 2)) ;
    	assertTrue(snb.getBufLen()==4);
    	assertTrue(snb.readData(buf, 2, 2)) ;
    	assertTrue(snb.getBufLen()==2);
    	assertTrue(snb.readData(buf, 4, 2)) ;
    	
    	assertFalse(snb.readData(buf, 4, 2)) ;
    	
    	assertTrue(new String(buf,0,6).equals("123456"));
    	assertTrue(snb.getBufLen()==0);
    	
    	snb.addData("12".getBytes());
    	snb.addData("3".getBytes());
    	snb.addData("456".getBytes());
    	
    	assertTrue('1'==snb.readNextChar());
    	
    	assertTrue(5==snb.getBufLen());
    	
    	assertTrue('2'==snb.readNextChar());
    	assertTrue('3'==snb.readNextChar());
    	assertTrue('4'==snb.readNextChar());
    	assertTrue('5'==snb.readNextChar());
    	assertTrue(1==snb.getBufLen());
    	assertTrue('6'==snb.readNextChar());
    	
    	assertTrue(0==snb.getBufLen());
    	
    	assertTrue(-1==snb.readNextChar());
    	
    	snb.addData("12".getBytes());
    	snb.addData("3".getBytes());
    	snb.addData("456".getBytes());
    	
    	snb.skipLen(4) ;
    	assertTrue(2==snb.getBufLen());
    	assertTrue(snb.readData(buf, 0, 2)) ;
    	assertTrue(new String(buf,0,2).equals("56"));
    }
    
    
    @Test
    public void testSnifferBufferFix() throws Exception
    {
    	byte[] buf = new byte[10] ;
    	for(int i = 0 ; i < buf.length ; i ++)
    		buf[i] = 0 ;
    	SnifferBufferFix snb = new SnifferBufferFix(10) ;
    	assertTrue(snb.getBufLen()==0);
    	
    	assertFalse(snb.readData(buf, 0, 1));
    	
    	snb.addData("123".getBytes());
    	snb.addData("456".getBytes());
    	assertTrue(snb.getBufLen()==6);
    	
    	assertTrue(snb.peekData(buf, 0, 2)) ;
    	assertTrue(new String(buf,0,2).equals("12"));
    	
    	assertTrue(snb.peekData(buf, 0, 5)) ;
    	assertTrue(new String(buf,0,5).equals("12345"));
    	
    	assertTrue(snb.readData(buf, 0, 2)) ;
    	assertTrue(snb.getBufLen()==4);
    	assertTrue(snb.readData(buf, 2, 2)) ;
    	assertTrue(snb.getBufLen()==2);
    	assertTrue(snb.readData(buf, 4, 2)) ;
    	
    	assertFalse(snb.readData(buf, 4, 2)) ;
    	
    	assertTrue(new String(buf,0,6).equals("123456"));
    	assertTrue(snb.getBufLen()==0);
    	
    	snb.addData("12".getBytes());
    	snb.addData("3".getBytes());
    	snb.addData("456".getBytes());
    	
    	assertTrue('1'==snb.readNextChar());
    	
    	assertTrue(5==snb.getBufLen());
    	
    	assertTrue('2'==snb.readNextChar());
    	assertTrue('3'==snb.readNextChar());
    	assertTrue('4'==snb.readNextChar());
    	assertTrue('5'==snb.readNextChar());
    	assertTrue(1==snb.getBufLen());
    	assertTrue('6'==snb.readNextChar());
    	
    	assertTrue(0==snb.getBufLen());
    	
    	assertTrue(-1==snb.readNextChar());
    	
    	snb.addData("12".getBytes());
    	snb.addData("3".getBytes());
    	snb.addData("456".getBytes());
    	
    	snb.skipLen(4) ;
    	assertTrue(2==snb.getBufLen());
    	assertTrue(snb.readData(buf, 0, 2)) ;
    	assertTrue(new String(buf,0,2).equals("56"));
    }
    
    @Test
    public void testSnifferCh()
    {
    	byte[] bs1 = Convert.hexStr2ByteArray("01 04 10 10 00 0A 75 08 01 04 14 42 47 33 33 3F 47 EF 9E 41 78 F5 C3 3F 80 00 00 00 03 94 5D 86 5F 02 04 10 10 00 0A 75 3B");
    	byte[] bs2 = Convert.hexStr2ByteArray("02 04 10 10 00 0A 75 3B 02 04 14 42 5A 00 00 3F 5B E7 6D 41 88 3D 71 3F 80 00 00 00 00 C3 0C D7 2D");
    	SnifferRTUCh sch = new SnifferRTUCh() ;
    	sch.onSniffedData(bs1, null);
    	assertTrue(sch.getSnifferCmd("1_4_4112_10")!=null);
    	sch.onSniffedData(bs2,null);
    	assertTrue(sch.getSnifferCmd("2_4_4112_10")!=null);
    }
}
