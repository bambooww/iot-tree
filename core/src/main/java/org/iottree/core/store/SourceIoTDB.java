package org.iottree.core.store;

import org.json.JSONObject;

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

	public boolean checkValid(StringBuilder failedr)
	{
		return true ;
	}
	
	public boolean checkConn(StringBuilder failedr)
	{
		throw new NotImplementedError();
	}

	@Override
	public String getExchgTP()
	{
		return null;
	}

	@Override
	protected JSONObject toExchgPmJO()
	{
		return null;
	}

	@Override
	protected boolean fromExchgPmJO(JSONObject pmjo)
	{
		return false;
	}
}
