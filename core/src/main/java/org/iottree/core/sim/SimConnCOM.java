package org.iottree.core.sim;

import java.io.InputStream;
import java.io.OutputStream;

import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

@data_class
public class SimConnCOM extends SimConn
{
	@data_val(param_name = "com_id")
	String comId = null;
	
	public String getConnTitle()
	{
		return "COM-" ;
	}
	
	@Override
	public InputStream getConnInputStream()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getConnOutputStream()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toConfigStr()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean fromConfig(JSONObject jo)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startConn()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopConn()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isConn()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pulseConn() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

}
