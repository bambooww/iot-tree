package org.iottree.ext.conn;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;

public class DesktopEyeTest extends TestCase
{
	public void test1() throws Exception
	{
		DesktopEye de = new DesktopEye();
		de.capScreen(); 
	}
}
