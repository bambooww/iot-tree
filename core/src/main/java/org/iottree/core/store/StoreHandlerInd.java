package org.iottree.core.store;

import java.util.Arrays;
import java.util.List;

import org.iottree.core.UATag;

/**
 * for Tags which has indicator properties
 * e.g   flow speed
 *         flow accumlation
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
	
	private final static List<StoreOut> supportedOuts = Arrays.asList() ; 
	
	@Override
	public List<StoreOut> getSupportedOuts()
	{
		return supportedOuts;
	}
	
	@Override
	protected void RT_runInLoop()
	{
		// add self code here
		//
		super.RT_runInLoop();
	}
}
