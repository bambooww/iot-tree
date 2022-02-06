package org.iottree.core.sim;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

@data_class
public class SimConnTcp extends SimConn
{
	public static final int DEF_PORT = 12000 ;
	
	Socket socket = null ;
	
	@data_val(param_name = "server_ip")
	String serverIp = null ;
	
	@data_val(param_name = "server_port")
	int serverPort = DEF_PORT ;
	//SerialPort serialPort = null;
	
	public String getServerIp()
	{
		if(this.serverIp==null)
			return "" ;
		return this.serverIp ;
	}
	
	public int getServerPort()
	{
		return serverPort ;
	}
	
	public String getConnTitle()
	{
		return "Tcp-"+this.serverPort ;
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

	public void pulseConn() throws Exception
	{
		socket.sendUrgentData(0);
	}
}
