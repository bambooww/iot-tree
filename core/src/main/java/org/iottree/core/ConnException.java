package org.iottree.core;

import java.io.IOException;

/**
 * ConnException is used by driver to catch.
 * Driver must catch this Exception,to do some connection error handler(it will not stop driver loop).
 * ConnProvider and ConnPt must controller connpt exception that may be recover later.
 * 
 * 
 * @author jason.zhu
 *
 */
public class ConnException extends IOException
{
	public ConnException()
	{
		super();
	}
	
	public ConnException(String msg)
	{
		super(msg) ;
	}
	
	public ConnException(Throwable t)
	{
		super(t) ;
	}
	
	public ConnException(String msg,Throwable t)
	{
		super(msg,t) ;
	}
}
