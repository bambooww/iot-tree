package org.iottree.driver.bacnet;

import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.basic.PropGroup;

/**
 * Building Automation and Control Networks
 * 
 *  Yabe （Yet Another Bacnet Explorer）can provide simulator
 * @author jason.zhu
 *
 */
public class BACNetDrv extends DevDriver
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
		return new BACNetDrv();
	}

	@Override
	public String getName()
	{
		return "bacnet";
	}

	@Override
	public String getTitle()
	{
		return "Building Automation and Control Networks";
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
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
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
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
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean RT_runInLoop(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean RT_writeVal(UACh ch, UADev dev, UATag tag, DevAddr da, Object v)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean RT_writeVals(UACh ch, UADev dev, UATag[] tags, DevAddr[] da, Object[] v)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
