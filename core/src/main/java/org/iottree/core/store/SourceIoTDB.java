package org.iottree.core.store;

import kotlin.NotImplementedError;

public class SourceIoTDB extends Source
{

	@Override
	public String getSorTp()
	{
		return "iotdb";
	}

	@Override
	public String getSorTpTitle()
	{
		return "IoTDB";
	}

	public boolean checkConn(StringBuilder failedr)
	{
		throw new NotImplementedError();
	}
}
