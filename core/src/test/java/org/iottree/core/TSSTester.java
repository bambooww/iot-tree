package org.iottree.core;

import java.io.File;
import java.util.Random;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.store.tssdb.TSSAdapter4Test;
import org.iottree.core.store.tssdb.TSSTagParam;
import org.iottree.core.store.tssdb.TSSValPt;
import org.iottree.core.store.tssdb.TSSValSeg;
import org.iottree.core.store.tssdb.TSSValSegHit;
import org.iottree.core.store.tssdb.TSSValSegHitNext;
import org.iottree.core.store.tssdb.TSSValSegHitPrev;
import org.junit.Test;

import junit.framework.TestCase;

public class TSSTester extends TestCase
{
	TSSAdapter4Test adp = null;
			
	private synchronized TSSAdapter4Test getAdp() throws Exception
	{
		if(adp!=null)
			return adp ;
		
		adp = new TSSAdapter4Test() ;
		File dbf = adp.getDBFile() ;
		if(dbf.exists())
			dbf.delete() ;
		
		adp.RT_start() ;
		
		
		return adp ;
	}
	
	private void reinitData() throws Exception
	{
		TSSAdapter4Test ratest = getAdp() ;
		long basedt = 1709550000000l;// 2770701
		
		int gap_nano = 10 ; //0-999999
		
		Random rand = new Random();
		Long s_dt = null ,e_dt = null ;
		long ms = basedt ;
		
		long st = System.currentTimeMillis() ;
		for(int i = 0 ; i < 10000 ; i ++)
		{
			int val = i ;//rand.nextInt(100) ;
			int dx = i%3+1;//Math.abs(val)%10+1 ;

			ms +=dx;//System.currentTimeMillis();
			if(i==0)
				s_dt = ms ;
			
			//System.out.println("ms="+ms) ;
			ratest.addTagValue("aa.ss.ii", ms,true, val);
			ratest.addTagValue("kk.bb", ms,true, val%2==0);
			ratest.addTagValue("kk.mm.ff", ms,true, val/2.0);
			
			if(i==9999)
				e_dt = ms ;
		}
		
		System.out.println("add tag end ... from "+s_dt+" - "+e_dt +"  cost="+(System.currentTimeMillis()-st)) ;
		
		ms = basedt ;
		st = System.currentTimeMillis() ;
		for(int i = 0 ; i < 20000 ; i ++)
		{
			//int val = rand.nextInt(5) ;
			int val = i;//rand.nextInt(50) ;
			int dx = i%3+1; //int dx = Math.abs(val)%10+1 ;
			//Thread.sleep(1);//(0,gap_nano); 
			//System.out.println("add tag value"+val) ;
			
			ms +=dx; //long ms = basedt+dx; //System.currentTimeMillis();
			if(i==0)
				s_dt = ms ;
			
			//System.out.println("ms="+ms) ;
			ratest.addTagValue("aa.ss.ii2", ms,true, val);
			ratest.addTagValue("kk.bb2", ms,true, val%2==0);
			ratest.addTagValue("kk.mm.ff2", ms,true, val/2.0);
			
			if(i==19999)
				e_dt = ms ;
		}
		System.out.println("add tag2 end ... from "+s_dt+" - "+e_dt+"  cost="+(System.currentTimeMillis()-st)) ;
		
		while(ratest.getUnsavedSegsNum()>0)
		{
			System.out.println(" unsaved seg num="+ratest.getUnsavedSegsNum()) ;
			Thread.sleep(1000);
		}
		
		System.out.println(" unsaved seg num="+ratest.getUnsavedSegsNum()) ;
	}
	
	@Test
	public void testRead1() throws Exception
	{
		TSSAdapter4Test ratest = getAdp() ;
		
		TSSValPt<Short> vpt = ratest.<Short>readValPt("aa.ss.ii", 1709550000010l) ;
		short sv = vpt.getVal() ;
		assertEquals(sv, 4);
		
		vpt = ratest.<Short>readValPt("aa.ss.ii", 1709550019995l) ;
		assertEquals((short)vpt.getVal(), 9997);
		vpt = ratest.<Short>readValPt("aa.ss.ii", 1709550019996l) ;
		assertEquals((short)vpt.getVal(), 9997);
		
		TSSValSeg<Long> vs = ratest.readValSegAt("aa.ss.ii", 1709550019999l) ;
		assertNotNull(vs) ;
		//System.out.println("vpt=="+vpt) ;
	}
	
	@Test
	public void testRead2() throws Exception
	{
		TSSAdapter4Test ratest = getAdp() ;
		
		long st = System.currentTimeMillis() ;
		TSSValSegHitNext<Long> hitnext = ratest.<Long>readValSegAtAndNext("aa.ss.ii", 1709550000010l) ;
		long et = System.currentTimeMillis() ;
		System.out.println(" readValSegAtAndNext cost"+(et-st)) ;
		assertNotNull(hitnext);
		//System.out.println(hitnext.hitSeg) ;
		//System.out.println(hitnext.nextSeg) ;
		assertEquals(""+hitnext.hitSeg, "1709550000009,1,4,1709550000012");
		assertEquals(""+hitnext.nextSeg, "1709550000012,1,5,1709550000013");
		
		st = System.currentTimeMillis() ;
		hitnext = ratest.<Long>readValSegAtAndNext("aa.ss.ii", -1) ;
		et = System.currentTimeMillis() ;
		System.out.println(" readValSegAtAndNext cost"+(et-st)) ;
		assertNull(hitnext);
		
		st = System.currentTimeMillis() ;
		TSSValSegHitPrev<Long> hitprev = ratest.<Long>readValSegAtAndPrev("aa.ss.ii", 1709550019995l) ;
		et = System.currentTimeMillis() ;
		System.out.println(" readValSegAtAndNext cost"+(et-st)) ;
		assertNotNull(hitprev);
		//System.out.println(hitprev.hitSeg) ;
		//System.out.println(hitprev.prevSeg) ;
		assertEquals(""+hitprev.hitSeg, "1709550019995,1,9997,1709550019998");
		assertEquals(""+hitprev.prevSeg, "1709550019993,1,9996,1709550019995");
		
		st = System.currentTimeMillis() ;
		hitprev = ratest.<Long>readValSegAtAndPrev("aa.ss.ii", 1709550019996l) ;
		et = System.currentTimeMillis() ;
		System.out.println(" readValSegAtAndNext cost"+(et-st)) ;
		
		assertNotNull(hitprev);
		//System.out.println(hitprev.hitSeg) ;
		//System.out.println(hitprev.prevSeg) ;
		assertEquals(""+hitprev.hitSeg, "1709550019995,1,9997,1709550019998");
		assertEquals(""+hitprev.prevSeg, "1709550019993,1,9996,1709550019995");
		
		st = System.currentTimeMillis() ;
		hitnext = ratest.<Long>readValSegAtAndNext("aa.ss.ii", 1709550019999l) ;
		et = System.currentTimeMillis() ;
		System.out.println(" readValSegAtAndNext cost"+(et-st)) ;
		
		assertNotNull(hitnext);
		//System.out.println(hitnext.hitSeg) ;
		//System.out.println(hitnext.nextSeg) ;
		assertEquals(""+hitnext.hitSeg, "1709550019999,1,9999,1709550019999");
		assertEquals(""+hitnext.nextSeg, "null");
	}
	
	@Test
	public void testRead3() throws Exception
	{
		TSSAdapter4Test ratest = getAdp() ;
		
		long st = System.currentTimeMillis() ;
		TSSValSegHit<Long> hit = ratest.<Long>readValSegAt("aa.ss.ii", 1709550000010l,true,true) ;
		long et = System.currentTimeMillis() ;
		System.out.println(" readValSegAt cost"+(et-st)) ;
		assertNotNull(hit);
		//System.out.println(hit.prevSeg) ;
		//System.out.println(hit.hitSeg) ;
		//System.out.println(hit.nextSeg) ;
		assertEquals(""+hit.prevSeg, "1709550000007,1,3,1709550000009") ;
		assertEquals(""+hit.hitSeg, "1709550000009,1,4,1709550000012");
		assertEquals(""+hit.nextSeg, "1709550000012,1,5,1709550000013");
		
		st = System.currentTimeMillis() ;
		hit = ratest.<Long>readValSegAt("aa.ss.ii", 1709550000000l,true,true) ;
		et = System.currentTimeMillis() ;
		System.out.println(" readValSegAt cost"+(et-st)) ;
		assertNotNull(hit==null);
		
		st = System.currentTimeMillis() ;
		hit = ratest.<Long>readValSegAt("aa.ss.ii", 1709550000001l,true,true) ;
		et = System.currentTimeMillis() ;
		System.out.println(" readValSegAt cost"+(et-st)) ;
		assertNotNull(hit);
		//System.out.println(hit.prevSeg) ;
		//System.out.println(hit.hitSeg) ;
		//System.out.println(hit.nextSeg) ;
		assertEquals(""+hit.prevSeg, "null") ;
		assertEquals(""+hit.hitSeg, "1709550000001,1,0,1709550000003");
		assertEquals(""+hit.nextSeg, "1709550000003,1,1,1709550000006");
		
		st = System.currentTimeMillis() ;
		hit = ratest.<Long>readValSegAt("aa.ss.ii", 1709550019999l,true,true) ;
		et = System.currentTimeMillis() ;
		System.out.println(" readValSegAt cost"+(et-st)) ;
		assertNotNull(hit);
		System.out.println(hit.prevSeg) ;
		System.out.println(hit.hitSeg) ;
		System.out.println(hit.nextSeg) ;
		assertEquals(""+hit.prevSeg, "1709550019998,1,9998,1709550019999") ;
		assertEquals(""+hit.hitSeg, "1709550019999,1,9999,1709550019999");
		assertEquals(""+hit.nextSeg, "null");
		
		st = System.currentTimeMillis() ;
		hit = ratest.<Long>readValSegAt("aa.ss.ii", 1709550029999l,true,true) ;
		et = System.currentTimeMillis() ;
		System.out.println(" readValSegAt cost"+(et-st)) ;
		assertNotNull(hit==null);
	}
}
