package org.iottree.core;

import java.time.Instant;
import java.util.Random;

import org.iottree.core.store.record.RecProL1DValue;
import org.iottree.core.store.ttsr.RecAdapter4Test;
import org.iottree.core.util.Convert;

import junit.framework.TestCase;

public class RecTester extends TestCase
{
	public void test1() throws Exception
	{
		RecProL1DValue dv = new RecProL1DValue() ;
		String tmps = dv.getTpDesc() ;
		System.out.println(tmps) ;
	}
}
