package org.iottree.core;

import java.util.Random;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.store.tssdb.TSSAdapter4Test;
import org.iottree.core.store.tssdb.TSSTagParam;

import junit.framework.TestCase;

public class TSSTester extends TestCase
{
	public void test1() throws Exception
	{
		
		TSSAdapter4Test ratest = new TSSAdapter4Test() ;
		
		ratest.RT_start() ;
		
		
		Random rand = new Random();
		Long s_dt = null ,e_dt = null ;
		for(int i = 0 ; i < 100 ; i ++)
		{
			int val = rand.nextInt(100) ;
			Thread.sleep(Math.abs(val)+1); 
			//System.out.println("add tag value"+val) ;
			
			
			long ms = System.currentTimeMillis();
			if(i==0)
				s_dt = ms ;
			if(i==99)
				e_dt = ms ;

			ratest.addTagValue("aa.ss.ii", ms,true, val);
			ratest.addTagValue("kk.bb", ms,true, val%2==0);
			ratest.addTagValue("kk.mm.ff", ms,true, val/2.0);
		}
		
		
		System.out.println("add tag end ... from "+s_dt+" - "+e_dt) ;
		
		Thread.sleep(2000);
	}
}
