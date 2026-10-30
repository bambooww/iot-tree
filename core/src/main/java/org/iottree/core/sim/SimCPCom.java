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
		return null;
	}

	@Override
	public boolean fromConfig(JSONObject jo)
	{
		return false;
	}
	
	public int getConnsNum()
	{
		return 0;
	}

	public List<SimConn> getConns()
	{
		return null ;
	}


	@Override
	public boolean RT_init(StringBuilder failedr)
	{
		return false;
	}


	@Override
	public void RT_runInLoop() throws Exception
	{
		
	}


	@Override
	public void RT_stop()
	{
		
	}
}
