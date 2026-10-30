package org.iottree.driver.zigbee;

import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.UANode;
import org.iottree.core.UAUtil;
import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.zspec.ZSDriver;

public class ZigbeeManager
{
	public static ZSDriver getZSDriverByPath(String path,StringBuilder failedr)
	{
		UANode node = UAUtil.findNodeByPath(path) ;
		if(node==null)
		{
			failedr.append("no node found") ;
			return null;
		}
		
		DevDriver dd = null ;
		if(node instanceof UACh)
		{
			UACh ch  = (UACh)node ;
			dd = ch.getDriver() ;
		}
		else if(node instanceof UADev)
		{
			UADev dev = (UADev)node ;
			dd = dev.getRelatedDrv() ;
		}
		
		if(dd==null||Convert.isNullOrEmpty(dd.getDriverMgrUrl()))
		{
			failedr.append("no driver or no driver url found") ;
			return null;
		}
		
		if(!(dd instanceof ZSDriver))
		{
			failedr.append("not ZSDriver") ;
			return null;
		}
		return  (ZSDriver)dd ;
	}
}
