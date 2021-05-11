package org.iottree.core;

public class UAConnClient extends UAConn
{
	private boolean bRunning = false;
	
	public UAConnClient()
	{}
	
	public UAConnClient(String name,String title,String desc,String conntp)
	{
		super(name,title,desc,conntp);
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	public boolean isRunning()
	{
		return bRunning ;
	}
}
