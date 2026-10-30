package org.iottree.core;

public class UAConnServer extends UAConn
{
	public UAConnServer()
	{}
	
	public UAConnServer(String name,String title,String desc,String conntp)
	{
		super(name,title,desc,conntp);
	}
	@Override
	public void start()
	{
		
	}

	@Override
	public void stop()
	{
		
	}

	@Override
	public boolean isRunning()
	{
		return false;
	}

}
