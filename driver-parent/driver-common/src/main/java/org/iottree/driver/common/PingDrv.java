package org.iottree.driver.common;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevAddrFix;
import org.iottree.core.DevDriver;
import org.iottree.core.UAUtil;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropItem.PValTP;

public class PingDrv extends DevDriver
{

	@Override
	public DevDriver copyMe()
	{
		return new PingDrv();
	}

	@Override
	public String getName()
	{
		return "ping";
	}

	@Override
	public String getTitle()
	{
		return "Ping";
	}

	@Override
	public List<PropGroup> getPropGroupsForCh()
	{
		return null;
	}

	@Override
	public List<PropGroup> getPropGroupsForDevInCh()
	{
		ArrayList<PropGroup> rets = new ArrayList<>() ;
		PropGroup pg = new PropGroup("ping","Ping");
		pg.addPropItem(new PropItem("timeout","Check Timeout","Check Timeout(ms),too small may check error",PValTP.vt_int,false,null,null,3000));
		pg.addPropItem(new PropItem("chk_inter","Check Interval","Check Interval(ms)",PValTP.vt_int,false,null,null,10000));
		
		rets.add(pg);
		return rets;
	}
	

	@Override
	public List<PropGroup> getPropGroupsForDevDef()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * null will has no addr
	 * inner may provider fix tags
	 */
	@Override
	public DevAddr getSupportAddr()
	{
		return new DevAddrFix();
	}
	
	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		switch(groupn)
		{
		case "ping":
			if("timeout".contentEquals(itemn))
			{
				if(!UAUtil.chkPropValInt(strv,1000l,null,true,failedr))
					return false;
			}
			if("chk_inter".contentEquals(itemn))
			{
				if(!UAUtil.chkPropValInt(strv,100l,null,true,failedr))
					return false;
			}
		}
		return true ;
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
	

}
