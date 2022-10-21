package org.iottree.core;

import org.iottree.core.UAVal.ValTP;

import junit.framework.TestCase;

public class FilterTest  extends TestCase
{
	public void test1() throws Exception
	{
		long base_dt = System.currentTimeMillis() - 10000;
		UATag tag = new UATag("n1", "n1", "", "", ValTP.vt_float, 2) ;
		UAValList uvl = new UAValList(20) ;
		UAVal uv = uvl.filterValByAntiInterference(tag) ;
		assertEquals(uv,null) ;
		uvl.addVal(UAVal.createByStrVal(ValTP.vt_float, "12.3", base_dt, base_dt));
		uv = uvl.filterValByAntiInterference(tag) ;
		assertEquals(uv,null) ;
		uvl.addVal(UAVal.createByStrVal(ValTP.vt_float, "12.0", base_dt+10, base_dt+10));
		uv = uvl.filterValByAntiInterference(tag) ;
		assertEquals(uv,null) ;
		
		uvl.addVal(UAVal.createByStrVal(ValTP.vt_float, "12.5", base_dt+20, base_dt+20));
		uv = uvl.filterValByAntiInterference(tag) ;
		System.out.println(uv.getObjVal());
		assertEquals(uv.isValid(),true) ;
		assertEquals(uv.getObjVal(),12.3f) ;
		
		uvl.addVal(new UAVal(false,null, base_dt+30, base_dt+30));
		uv = uvl.filterValByAntiInterference(tag) ;
		System.out.println(uv.getObjVal());
		assertEquals(uv.isValid(),true) ;
		assertEquals(uv.getObjVal(),12.266666f) ;
		
		uvl.addVal(new UAVal(false,null, base_dt+30, base_dt+30));
		uv = uvl.filterValByAntiInterference(tag) ;
		System.out.println(uv.getObjVal());
		assertEquals(uv.isValid(),true) ;
		assertEquals(uv.getObjVal(),12.266666f) ;
		
		uvl.addVal(new UAVal(false,null, base_dt+30, base_dt+30));
		uv = uvl.filterValByAntiInterference(tag) ;
		System.out.println(uv.getObjVal());
		assertEquals(uv.isValid(),false) ;
		assertEquals(uv.getObjVal(),null) ;
		
		uvl.addVal(UAVal.createByStrVal(ValTP.vt_float, "10.5", base_dt+40, base_dt+40));
		uv = uvl.filterValByAntiInterference(tag) ;
		System.out.println(uv.getObjVal());
		assertEquals(uv.isValid(),true) ;
		assertEquals(uv.getObjVal(),11.825f) ;
		
		uvl.addVal(UAVal.createByStrVal(ValTP.vt_float, "9.6", base_dt+40, base_dt+40));
		uv = uvl.filterValByAntiInterference(tag) ;
		System.out.println(uv.getObjVal());
		assertEquals(uv.isValid(),true) ;
		assertEquals(uv.getObjVal(),11.38f) ;
	}
}
