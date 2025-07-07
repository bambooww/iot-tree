package org.iottree.driver.omron.fins;

import java.util.List;

import org.iottree.core.ConnPt;
import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UADev;
import org.iottree.core.conn.ConnPtStream;
import org.iottree.core.conn.ConnPtTcpClient;

public class FinsDriverEthTCP extends FinsDriver
{
	
	@Override
	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
	{
		return false;
	}
	
	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtTcpClient.class;
	}

	@Override
	public DevDriver copyMe()
	{
		return new FinsDriverEthTCP();
	}

	@Override
	public String getName()
	{
		return "omron_fins_tcp";
	}

	@Override
	public String getTitle()
	{
		return "Omron FINS Ethernet TCP";
	}

	

	protected int handshakePlcIpLastV = -1 ;
	
	@Override
	protected boolean onJustOnConn(ConnPtStream cp,UACh ch) throws Exception
	{
		List<UADev> devs = ch.getDevs() ;
		if(devs==null)
			return false;
		UADev dev = devs.get(0) ;
		ConnPtTcpClient cp_tcpc = (ConnPtTcpClient)cp ;
		int sa1 = dev.getOrDefaultPropValueInt(FinsDriver.PG_FINS_NET, "sa1", 0) ;
		byte[] bs = FinsMsg.Handshake_createReq((short)sa1) ;
		cp_tcpc.getOutputStream().write(bs);
		Short plc_iplastv = FinsMsg.Handshake_checkResp(cp_tcpc.getInputStream(), (short)sa1, getReadTimeout());
		if(plc_iplastv==null)
		{
			cp_tcpc.close();
			return false;
		}
		handshakePlcIpLastV = plc_iplastv ;
		return true ;
	}
}
