package org.iottree.core;

public enum DevState
{
	st_not_setup(0),
	st_setup_ok(1),
	st_running(2);
	
	private final int val ;
	
	DevState(int v)
	{
		val = v ;
	}
	
	public int getInt()
	{
		return val ;
	}
	
	public String getStr()
	{
		return this.toString().substring(3);
	}
	
}
