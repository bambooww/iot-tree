package org.iottree.core;

public interface IRoot
{
	public String getRootIdPrefix() ;
	
	public int getRootNextIdVal() ;
	
	public default String getRootNextId()
	{
		synchronized(this)
		{
			return getRootIdPrefix() + getRootNextIdVal();
		}
	}
}
