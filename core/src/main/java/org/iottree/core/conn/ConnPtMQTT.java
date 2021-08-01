package org.iottree.core.conn;

import java.util.List;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;

public class ConnPtMQTT extends ConnPtMSG
{
	@Override
	public String getConnType()
	{
		return "mqtt";
	}
}
