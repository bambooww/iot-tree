package org.iottree.core.store;

import org.iottree.core.UATag;

/**
 * 
 * @author jason.zhu
 *
 */
public class StoreHandlerInd extends StoreHandler
{
	public static final String TP = "ind" ;
	
	@Override
	public String getTp()
	{
		return TP;
	}

	@Override
	public String getTpTitle()
	{
		return "Tag Data as Indicator Handler";
	}

	@Override
	public boolean checkFilterFit(UATag tag)
	{
		return false;
	}
	
	@Override
	protected void RT_runInLoop()
	{
		// add self code here
		//
		super.RT_runInLoop();
	}
}
