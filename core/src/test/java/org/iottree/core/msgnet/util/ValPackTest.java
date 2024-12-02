package org.iottree.core.msgnet.util;

import org.json.JSONArray;
import org.json.JSONObject;

import junit.framework.TestCase;

public class ValPackTest extends TestCase
{
	public void testValPack() throws Exception
	{
		ValPack tm = new ValPack(32,false) ;
		byte[] bs = tm.pkOut() ;
		tm = ValPack.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"int32|false") ;
		assertEquals(tm.getPayload(),32) ;
		
		tm = new ValPack(88888888888l,false) ;
		bs = tm.pkOut() ;
		tm = ValPack.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"int64|false") ;
		assertEquals(tm.getPayload(),88888888888l) ;
		
		tm = new ValPack(888888.88888f,false) ;
		bs = tm.pkOut() ;
		tm = ValPack.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"float|false") ;
		assertEquals(tm.getPayload(),888888.88888f) ;
		
		tm = new ValPack(888888.88888d,false) ;
		bs = tm.pkOut() ;
		tm = ValPack.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"double|false") ;
		assertEquals(tm.getPayload(),888888.88888d) ;
		
		tm = new ValPack(true,false) ;
		bs = tm.pkOut() ;
		tm = ValPack.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"bool|false") ;
		assertEquals(tm.getPayload(),true) ;
		
		JSONObject jo = new JSONObject() ;
		jo.put("aa", "zfd频器状态监ddf 111111111111111111111111111111111111111111111111111") ;
		
		tm = new ValPack(jo,false) ;
		bs = tm.pkOut() ;
		tm = ValPack.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"jo|false") ;
		assertEquals(tm.getPayload().toString(),"{\"aa\":\"zfd频器状态监ddf 111111111111111111111111111111111111111111111111111\"}") ;
		
		JSONArray jarr =new JSONArray() ;
		jarr.put(jo) ;
		tm = new ValPack(jarr,false) ;
		bs = tm.pkOut() ;
		System.out.println("pk len="+bs.length);
		tm = ValPack.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"jarr|false") ;
		assertEquals(tm.getPayload().toString(),"[{\"aa\":\"zfd频器状态监ddf 111111111111111111111111111111111111111111111111111\"}]") ;
		System.out.println(tm.toHeadStr()) ;
		
		tm = new ValPack(jarr,true) ;
		bs = tm.pkOut() ;
		System.out.println("pk len="+bs.length);
		tm = ValPack.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"jarr|true") ;
		assertEquals(tm.getPayload().toString(),"[{\"aa\":\"zfd频器状态监ddf 111111111111111111111111111111111111111111111111111\"}]") ;
		System.out.println(tm.toHeadStr()) ;
		
		byte[] databs = jarr.toString().getBytes("UTF-8") ;
		tm = new ValPack(databs,true) ;
		bs = tm.pkOut() ;
		System.out.println("pk len="+bs.length);
		tm = ValPack.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"bs|true") ;
		byte[] retbs = (byte[])tm.getPayload() ;
		assertEquals(new String(retbs,"utf-8"),"[{\"aa\":\"zfd频器状态监ddf 111111111111111111111111111111111111111111111111111\"}]") ;
		System.out.println(tm.toHeadStr()) ;
		
	}
}
