package org.iottree.core.sim;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

@data_class
public class SimCPCom extends SimCP
{
	@data_val(param_name = "com_id")
	String comId = null;
	
	public String getConnTitle()
	{
		return "COM-" ;
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

	public List<SimConn> getConns()
	{
		return null ;
	}


	@Override
	public boolean RT_init(StringBuilder failedr)
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void RT_runInLoop() throws Exception
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void RT_stop()
	{
		// TODO Auto-generated method stub
		
	}
}
