package org.iottree.core.msgnet;

public abstract class MNNodeState extends MNNodeMid
{
	
	@Override
	public boolean getShowOutTitleDefault()
	{
		return true;
	}
	
	

	public abstract boolean RT_isStateActive() ;
	
	public abstract boolean RT_isStateRunning() ;
}
