package org.iottree.core;

import org.iottree.core.util.IdIId;

public interface IRoot
{
	public String getRootIdPrefix() ;
	
	public int getRootNextIdVal() ;
	
	public default IdIId getRootNextId()
	{
		synchronized(this)
		{
			int iid = getRootNextIdVal() ;
			return new IdIId(getRootIdPrefix() +iid,iid) ;
		}
	}
}
