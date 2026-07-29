package org.iottree.driver.mitsubishi.fxnet;

import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.core.basic.PropGroup;

/**
 * Fx PLC In RS485,and may has device id support
 * 
 * format:
 * 05 
 * 
 * @author jason.zhu
 *
 */
public class FxNetDriver extends DevDriver
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
		return new FxNetDriver();
	}

	@Override
	public String getName()
	{
		return "mitsubishi_fxnet";
	}

	@Override
	public String getTitle()
	{
		return "Mitsubishi FX Net";
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return null;
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
	
	private static FxNetAddr FX_NET_ADDR = new FxNetAddr() ;

	@Override
	public DevAddr getSupportAddr()
	{
		return FX_NET_ADDR;
	}

	@Override
	protected void RT_onConnReady(ConnPt cp, UACh ch, UADev dev)
	{
		
	}

	@Override
	protected void RT_onConnInvalid(ConnPt cp, UACh ch, UADev dev)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean RT_runInLoop(UACh ch, UADev dev, StringBuilder failedr) throws Exception
	{
		
		return false;
	}

	@Override
	public boolean RT_writeVal(UACh ch,UADev dev,UATag tag, DevAddr da, Object v)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean RT_writeVals(UACh ch,UADev dev,UATag[] tags, DevAddr[] da, Object[] v)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
