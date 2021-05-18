package org.iottree.core;

import static java.lang.System.*;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.MemSeg8;
import org.iottree.core.basic.MemTable;

import junit.framework.TestCase;

public class MemTableTester extends TestCase
{
	MemTable<MemSeg8> mt = new MemTable<>(8,1000) ;
	
	private void eqAndPrintln(String exp,String act)
	{
		out.println(act);
		assertEquals(exp,act) ;
	}
	
	public void testBasic()
	{
		//this.assertEquals(expected, actual);
		mt.setValBool(0, 0, true);
		mt.setValBool(0, 1, true);
		mt.setValBool(0, 2, true);
		assertEquals(7,mt.getValInt32(ValTP.vt_byte, 0)) ;
		eqAndPrintln("[0,1]",mt.toSegsStr());
		
		mt.setValNumber(ValTP.vt_int32, 1, 100);		
		assertEquals(7,mt.getValInt32(ValTP.vt_byte, 0)) ;
		assertEquals(100,mt.getValInt32(ValTP.vt_int32, 1)) ;
		eqAndPrintln("[0,5]",mt.toSegsStr());
		
		mt.setValNumber(ValTP.vt_int16, 7, 100);		
		assertEquals(7,mt.getValInt32(ValTP.vt_byte, 0)) ;
		assertEquals(100,mt.getValInt32(ValTP.vt_int16, 7)) ;
		eqAndPrintln("[0,5][7,2]",mt.toSegsStr());
		
		mt.setValNumber(ValTP.vt_int16, 5, 12345);		
		assertEquals(7,mt.getValInt32(ValTP.vt_byte, 0)) ;
		assertEquals(12345,mt.getValInt32(ValTP.vt_int16, 5)) ;
		eqAndPrintln("[0,9]",mt.toSegsStr());
	}
	
	public void testGetVal()
	{
		mt.getValNumber(ValTP.vt_byte, 0) ;
	}
}
