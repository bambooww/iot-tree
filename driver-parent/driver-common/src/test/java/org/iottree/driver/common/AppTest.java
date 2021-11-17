package org.iottree.driver.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.iottree.driver.common.modbus.sniffer.SnifferBuffer;
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
    }
    
}
