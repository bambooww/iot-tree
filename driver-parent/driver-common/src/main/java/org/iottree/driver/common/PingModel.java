package org.iottree.driver.common;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.DevAddr;
import org.iottree.core.DevItem;
import org.iottree.core.DevModel;
import org.iottree.core.UADev;
import org.iottree.core.*;
import org.iottree.core.util.NetUtil;

public class PingModel extends DevModel
{
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
	public DevModel copyMe()
	{
		return new PingModel();
	}


	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		switch(groupn)
		{
		case "dev":
			if("devid".contentEquals(itemn))
			{//check host or ip
				strv = strv.trim();
				if(Convert.isNullOrEmpty(strv))
				{
					failedr.append("ID must be host address or ip,it's cannot be empty") ;
					return false;
				}
				return true;
			}
			break ;
		}
		return true;
	}
	
	protected boolean CONF_supportFixItems()
	{
		return true;
	}
	
	static ArrayList<DevItem> fixItems = new ArrayList<>() ;
	static DevItem itemStatus = null ;
	static
	{
		itemStatus = new DevItem("status","Host Status","Host Status,true is active","status",UAVal.ValTP.vt_bool,false,2000);
		fixItems.add(itemStatus) ;
	}
	
	protected List<DevItem> CONF_getFixItems()
	{
		return fixItems ;
	}
	
	private int timeOut = 3000 ;
	private long chkInterMS = 10000 ;
	

	protected boolean RT_setupModel(List<DevAddr> addrs,StringBuilder failedr)
	{
		if(!super.RT_setupModel(addrs, failedr))
			return false;
		
		UADev d = this.getBelongToDev();
		UATag t = d.getTagByName("status") ;
		if(t==null)
		{
			try
			{
				d.addTag(itemStatus);
			}
			catch(Exception ee)
			{
				failedr.append(ee.getMessage()) ;
				return false;
			}
		}
		
		timeOut = (int)this.getPropValInt("ping", "timeout", 3000) ;
		if(timeOut<=0)
			timeOut = 3000 ;
		chkInterMS = this.getPropValInt("ping", "chk_inter", 10000) ;
		if(chkInterMS<=0)
			chkInterMS = 10000 ;
		return true;
	}
	
	
	@Override
	protected boolean RT_initModel(StringBuilder failedr)
	{
		return true;
	}
	
	long lastPingDT = -1 ;
	
	@Override
	protected void RT_runInLoop()
	{
		if(System.currentTimeMillis()-lastPingDT<chkInterMS)
			return ;
		try
		{
			String devid = this.getDevId() ;
			boolean bv = NetUtil.ping(devid, timeOut);
			//System.out.println("ping "+devid+" "+bv);
			for(DevAddr da:this.RT_listModelAddrs())
			{
				if("status".contentEquals(da.getAddr()))
					da.RT_setVal(bv);
			}
		}
		catch(Exception e)
		{
			for(DevAddr da:this.RT_listModelAddrs())
			{
				if("status".contentEquals(da.getAddr()))
					da.RT_setVal(null);
			}
		}
		finally
		{
			lastPingDT = System.currentTimeMillis() ;
		}
	}

	@Override
	protected void RT_endModel()
	{
		
	}

	@Override
	public boolean RT_writeVal(DevAddr da, Object v)
	{
		return false;
	}

}
