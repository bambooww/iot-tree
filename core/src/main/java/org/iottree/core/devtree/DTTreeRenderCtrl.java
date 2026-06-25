package org.iottree.core.devtree;

public class DTTreeRenderCtrl
{
	public boolean checkShowSub(DTNode nd)
	{
		return true ;
	}
	
	public boolean checkShowNodeTP(String nodetp)
	{
		return true ;
	}
	
	public boolean checkRenderChild(DTNode nd)
	{
		return false;
	}
	
	public boolean checkIgnoreNode(DTNode nd)
	{
		return false;
	}
}
