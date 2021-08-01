package org.iottree.core.conn;

import org.iottree.core.ConnPt;

/**
 * based on msg connections,
 * 
 * @author jason.zhu
 *
 */
public abstract class ConnPtMSG  extends ConnPt
{
	@Override
	public String getStaticTxt()
	{
		return null;
	}

	@Override
	public boolean isConnReady()
	{
		
		return false;
	}
	
	
}
