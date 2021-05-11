package org.iottree.core.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.iottree.core.ConnProvider;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

public class ConnPtTcpClient extends ConnPtStream
{
	public static String TP = "tcp_client";

	String host = null;

	int port = -1;

	Socket sock = null;

	InputStream inputS = null;

	OutputStream outputS = null;

	public ConnPtTcpClient()
	{
	}

	public ConnPtTcpClient(ConnProvider cp, String name, String title, String desc)
	{
		super(cp, name, title, desc);
	}
	
	public String getConnType()
	{
		return "tcp_client" ;
	}

	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		xd.setParamValue("host", host);
		xd.setParamValue("port", port);
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);
		this.host = xd.getParamValueStr("host");
		this.port = xd.getParamValueInt32("port", 8081);
		return r;
	}
	
	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);
		
		
		this.host = jo.getString("host") ;
		this.port = jo.getInt("port") ;
	}

	public String getHost()
	{
		if(host==null)
			return "" ;
		return host;
	}

	public int getPort()
	{
		return port;
	}
	
	public String getPortStr()
	{
		if(port<=0)
			return "" ;
		return ""+port;
	}

	@Override
	protected InputStream getInputStreamInner()
	{
		return inputS;
	}

	@Override
	protected OutputStream getOutputStreamInner()
	{
		return outputS;
	}

	public String getStaticTxt()
	{
		return this.host + ":" + this.port;
	}
	

	private synchronized boolean connect()
	{
		if (sock != null)
		{
			if (sock.isClosed())
			{
				try
				{
					disconnect();
				} catch (Exception e)
				{
				}
			}
			
			try
			{
				sock.sendUrgentData(0xFF);
			}
			catch(Exception e)
			{
				disconnect() ;
			}
			return true;
		}

		try
		{
			
			sock = new Socket(host, port);
			sock.setSoTimeout(10000);
			sock.setTcpNoDelay(true);
			inputS = sock.getInputStream();
			outputS = sock.getOutputStream();

			this.fireConnReady();

			return true;
		} catch (Exception ee)
		{
			//System.out.println("conn to "+this.getStaticTxt()+" err") ;
			disconnect();
			return false;
		}
	}

	synchronized void disconnect() //throws IOException
	{
		if (sock == null)
			return;

		try
		{
			try
			{
				if (inputS != null)
					inputS.close();
			} catch (Exception e)
			{
			}

			try
			{
				if (outputS != null)
					outputS.close();
			} catch (Exception e)
			{
			}

			try
			{
				if (sock != null)
					sock.close();
			} catch (Exception e)
			{
			}
		} finally
		{
			inputS = null;
			outputS = null;
			sock = null;
		}
	}

	private long lastChk = -1;

	void checkConn()
	{
		if(System.currentTimeMillis()-lastChk<5000)
			return ;
		
		try
		{
			connect();
		}
		finally
		{
			lastChk = System.currentTimeMillis() ;
		}
	}

	public String getDynTxt()
	{
		return "";
	}

	@Override
	public boolean isClosed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConnReady()
	{
		return sock!=null;
	}

	@Override
	public void close() throws IOException
	{
		disconnect();
	}

}
