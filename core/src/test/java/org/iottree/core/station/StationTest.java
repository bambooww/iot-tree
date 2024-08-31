package org.iottree.core.station;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.station.StationLocSaver.Item;
import org.iottree.core.util.IdCreator;

import junit.framework.TestCase;

public class StationTest extends TestCase
{
	public void testSaver() throws Exception
	{
		int put_num =1011 ;
		int max_num =  501 ;
		StationLocSaver.forTest = true ;
		
		StationLocSaver sls = StationLocSaver.getSaver("prj1") ;
		sls.clear();
		
		ArrayList<Item> items = new ArrayList<>() ;
		for(int i = 0 ; i < put_num ; i ++)
		{
			String k = IdCreator.newSeqId() ;
			String msg = "msgggg "+i ;
			
			items.add(new Item(k, msg.getBytes())) ;
		}
		long st = System.currentTimeMillis() ;
		sls.putBatch(items, max_num) ;
		long et = System.currentTimeMillis() ;
		System.out.println("cost "+(et-st)) ;
		
		long saven = sls.getSavedNum() ;
		assertTrue(saven==max_num) ;
		
		//Item lastitem = sls.getLastItem() ;
		//System.out.println("last key="+lastitem.getKey()) ;
		
		List<Item> first10 = sls.getFirstItems(10) ;
		assertTrue(first10.size()==10) ;
		
		sls.deleteBatchByItems(first10) ;
		assertTrue((max_num-10)==sls.getSavedNum()) ;
		assertTrue((max_num-10)==sls.countNum()) ;
		
		List<Item> last10 = sls.getLastItems(10) ;
		assertTrue(last10.size()==10) ;
		
		sls.deleteBatchByItems(last10) ;
		assertTrue((max_num-20)==sls.getSavedNum()) ;
		assertTrue((max_num-20)==sls.countNum()) ;
		
		for(Item item:first10)
		{
			Item dbitem = sls.getItemByKey(item.key) ;
			assertTrue(dbitem==null) ;
		}
		
		Item lasti = sls.getLastItem() ;
		assertTrue("msgggg 1000".equals(new String(lasti.msg))) ;
	}
}
