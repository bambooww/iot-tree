package org.iottree.core;

import java.io.File;
import java.util.Random;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.store.tssdb.TSSAdapter4Test;
import org.iottree.core.store.tssdb.TSSTagParam;

import junit.framework.TestCase;

public class TSSTester extends TestCase
{
	TSSAdapter4Test adp = null;
			
	private synchronized TSSAdapter4Test getAdp()
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
	
	public void test1() throws Exception
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
			
			//if(i%10==0)
			//Thread.sleep(1);//(0,gap_nano); 
			//System.out.println("add tag value"+val) ;
			
			
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
		
		Thread.sleep(10000);
		System.out.println(" unsaved seg num="+ratest.getUnsavedSegsNum()) ;
	}
	
	public void testRead1() throws Exception
	{
		TSSAdapter4Test ratest = getAdp() ;
		
		
	}
}
