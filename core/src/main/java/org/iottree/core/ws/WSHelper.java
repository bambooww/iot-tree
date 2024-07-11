package org.iottree.core.ws;

import org.iottree.core.station.PlatformWSServer;

public class WSHelper
{
	public static void onSysClose()
	{
		WSRoot.stopTimer(false);
		WSServer.stopTimer(false);
		PlatformWSServer.stopTimer(false);
	}
}
