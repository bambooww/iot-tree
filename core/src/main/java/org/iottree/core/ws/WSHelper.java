package org.iottree.core.ws;

public class WSHelper
{
	public static void onSysClose()
	{
		WSRoot.stopTimer(false);
		WSServer.stopTimer(false);
	}
}
