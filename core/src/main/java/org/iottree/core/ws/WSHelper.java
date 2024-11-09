package org.iottree.core.ws;

import org.iottree.core.station.PlatInsWSServer;

public class WSHelper
{
	public static void onSysClose()
	{
		WSRoot.stopTimer(false);
		WSServer.stopTimer(false);
		PlatInsWSServer.stopTimer(false);
	}
}
