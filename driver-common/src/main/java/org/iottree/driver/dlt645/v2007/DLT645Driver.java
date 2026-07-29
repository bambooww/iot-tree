package org.iottree.driver.dlt645.v2007;

import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.util.ILang;

public class DLT645Driver extends DevDriver implements ILang
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
		return new DLT645Driver();
	}

	@Override
	public String getName()
	{
		return "dlt645_2007";
	}

	@Override
	public String getTitle()
	{
		return g("dlt645_2007");
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtStream.class;
	}

	@Override
	public boolean supportDevFinder()
	{
		return false;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForCh(UACh ch)
	{
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh(UADev d)
	{
		return null;
	}

	@Override
	public DevAddr getSupportAddr()
	{
		return null;
	}

	@Override
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev) throws Exception
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
