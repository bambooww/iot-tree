package org.iottree.core.station;

public class PSCmdReboot extends PSCmd
{
	public static final String CMD = "reboot" ;
	
	@Override
	public String getCmd()
	{
		return CMD;
	}

	@Override
	public void RT_onRecvedInStationLocal(StationLocal sl) throws Exception
	{
		System.out.println("on recved reboot cmd,will reboot") ;
		System.exit(0);
	}
}
