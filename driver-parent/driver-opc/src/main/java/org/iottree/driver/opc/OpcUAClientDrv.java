package org.iottree.driver.opc;

import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UADev;
import org.iottree.core.basic.PropGroup;

public class OpcUAClientDrv extends DevDriver
{

	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DevDriver copyMe()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportDevFinder()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForCh()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DevAddr getSupportAddr()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void RT_onConnReady(ConnPt cp)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean RT_runInLoop(StringBuilder failedr) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean RT_writeVal(UADev dev, DevAddr da, Object v)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean RT_writeVals(UADev dev, DevAddr[] da, Object[] v)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
