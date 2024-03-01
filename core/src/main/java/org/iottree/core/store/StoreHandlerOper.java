package org.iottree.core.store;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UATag;

/**
 * for operator issue command to system log
 * 
 * @author jason.zhu
 *
 */
public class StoreHandlerOper  extends StoreHandler
{
	public static final String TP = "op" ;
	
	@Override
	public String getTp()
	{
		return TP;
	}

	@Override
	public String getTpTitle()
	{
		return "User Operation logger";
	}

	@Override
	public boolean checkFilterFit(UATag tag)
	{
		return false;
	}

	@Override
	public List<StoreOut> getSupportedOuts()
	{
		return null;
	}
	
}
