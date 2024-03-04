package org.iottree.core;

import java.time.Instant;
import java.util.Random;

import org.iottree.core.store.ttsr.RecAdapter4Test;
import org.iottree.core.util.Convert;

import junit.framework.TestCase;

public class RecTester extends TestCase
{
	public void test1() throws Exception
	{
		long ms = System.currentTimeMillis() ;
		
		RecAdapter4Test ratest = new RecAdapter4Test() ;
		
		ratest.RT_start() ;
		
		
		Random rand = new Random();
		for(int i = 0 ; i < 100 ; i ++)
		{
			int val = rand.nextInt(10) ;
			Thread.sleep(100); 
			System.out.println("add tag value"+val) ;
			ratest.addTagValue("xx.bb.cc", System.currentTimeMillis(), val);
		}
		System.out.println("add tag end ...") ;
		
		Thread.sleep(10000);
	}
}
