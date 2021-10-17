package org.iottree.core.util;

public class VTest
{//volatile
	private  boolean flag = true ;
	
	private void refresh()
	{
		flag=false;
		System.out.println("flat - false") ;
	}
	
	private Runnable refreshRun = new Runnable() {

		@Override
		public void run()
		{
			refresh() ;
		}} ;
	
	public void load() //throws InterruptedException
	{
		int i = 0 ;
		while(flag)
		{
			i ++ ;
			try
			{
			Thread.sleep(1);
			}
			catch(Exception e)
			{}
		}
		System.out.print("out loop");
	}
	
	private Runnable loadRun = new Runnable() {

		@Override
		public void run()
		{
			load() ;
		}} ;
		
		
	private static void test1() throws Exception
	{
		VTest test = new VTest() ;
		new Thread(test.refreshRun,"thread A").start();
		
		//Thread.sleep(2000);
		new Thread(test.loadRun,"thread B").start();
	}
	
	public static void test2() throws Exception
	{
		VTest test = new VTest() ;
		new Thread(test::load,"thread A").start();
		
		//Thread.sleep(2000);
		new Thread(test::refresh,"thread B").start();
		//Thread.sleep(1000);
	}
	
	public static void main(String[] args) throws Exception
	{
		test2();
	}
}
