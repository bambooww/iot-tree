package org.iottree.driver.zigbee.zspec;

import java.io.File;

import org.iottree.core.DevDriver;
import org.iottree.core.conn.ConnPtStream;

public abstract class ZSDriver extends DevDriver
{
	public abstract ZSCoor getZSCoor() ;
	
	@Override
	public String getDriverMgrUrl()
	{
		return "/admin/ua/drv_zigbee/main.jsp";
	}
	

	public ConnPtStream getBindConnPtStream()
	{
		return (ConnPtStream)this.getBindedConnPt() ;
	}
	
	public boolean isBindConnPtReady()
	{
		ConnPtStream cpt = (ConnPtStream)this.getBindedConnPt() ;
		if(cpt==null)
			return false;
		return cpt.isConnReady() ;
	}

}
