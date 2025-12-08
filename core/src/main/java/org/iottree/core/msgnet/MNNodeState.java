package org.iottree.core.msgnet;

public abstract class MNNodeState extends MNNodeMid
{
	@Override
	public final int getOutNum()
	{
		return 3;
	}
	

	@Override
	public boolean getShowOutTitleDefault()
	{
		return true;
	}
	
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case 0:
			return "in";
		case 1:
			return "run";
		case 2:
			return "out";
		default:
			return null ;
		}
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		switch(idx)
		{
		case 0:
			return "green";
		case 1:
			return "blue";
		case 2:
			return "red";
		default:
			return null ;
		}
	}

	public abstract boolean RT_isStateActive() ;
	
	public abstract boolean RT_isStateRunning() ;
}
