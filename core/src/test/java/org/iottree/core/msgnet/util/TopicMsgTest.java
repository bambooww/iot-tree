package org.iottree.core.msgnet.util;

import static java.lang.System.out;

import org.json.JSONArray;
import org.json.JSONObject;

import junit.framework.TestCase;

public class TopicMsgTest extends TestCase
{
	
	
	public void testBasic() throws Exception
	{
		TopicMsg tm = new TopicMsg("top1.a",32,false) ;
		byte[] bs = tm.pkOut() ;
		tm = TopicMsg.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"top1.a|int32|false") ;
		assertEquals(tm.getPayload(),32) ;
		
		tm = new TopicMsg("top1.a",88888888888l,false) ;
		bs = tm.pkOut() ;
		tm = TopicMsg.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"top1.a|int64|false") ;
		assertEquals(tm.getPayload(),88888888888l) ;
		
		tm = new TopicMsg("top1.a",888888.88888f,false) ;
		bs = tm.pkOut() ;
		tm = TopicMsg.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"top1.a|float|false") ;
		assertEquals(tm.getPayload(),888888.88888f) ;
		
		tm = new TopicMsg("top1.a",888888.88888d,false) ;
		bs = tm.pkOut() ;
		tm = TopicMsg.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"top1.a|double|false") ;
		assertEquals(tm.getPayload(),888888.88888d) ;
		
		tm = new TopicMsg("top1.a",true,false) ;
		bs = tm.pkOut() ;
		tm = TopicMsg.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"top1.a|bool|false") ;
		assertEquals(tm.getPayload(),true) ;
		
		JSONObject jo = new JSONObject() ;
		jo.put("aa", "zfd频器状态监ddf") ;
		
		tm = new TopicMsg("top1.a",jo,false) ;
		bs = tm.pkOut() ;
		tm = TopicMsg.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"top1.a|jo|false") ;
		assertEquals(tm.getPayload().toString(),"{\"aa\":\"zfd频器状态监ddf\"}") ;
		
		JSONArray jarr =new JSONArray() ;
		jarr.put(jo) ;
		tm = new TopicMsg("top1.a",jarr,false) ;
		bs = tm.pkOut() ;
		tm = TopicMsg.parseFrom(bs) ;
		assertNotNull(tm);
		assertEquals(tm.toHeadStr(),"top1.a|jarr|false") ;
		assertEquals(tm.getPayload().toString(),"[{\"aa\":\"zfd频器状态监ddf\"}]") ;
		System.out.println(tm.toHeadStr()) ;
	}
}
