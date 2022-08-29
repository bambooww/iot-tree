package org.iottree.core.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.iottree.core.ConnProvider;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

/**
 * for tcp server accepted socket connection
 * @author jason.zhu
 *
 */
public class ConnPtTcpAccepted extends ConnPtStream
{
	public static String TP = "tcp_accepted";

	Socket sock = null;

	InputStream inputS = null;

	OutputStream outputS = null;
	
	/**
	 * accepted tcp socket connnection must send first data which has it's id to identify itself
	 * some accepted may has verification process.
	 * 
	 * different communication equipment may has different auth method.
	 * so it may has inner auth driver to support.
	 */
	String sockConnId = null ;

	public ConnPtTcpAccepted()
	{
	}

	public ConnPtTcpAccepted(ConnProvider cp, String name, String title, String desc)
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
		
		if(sockConnId!=null)
			xd.setParamValue("sock_connid", sockConnId);
		
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);
		this.sockConnId = xd.getParamValueStr("sock_connid");
		return r;
	}
	
	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);
		this.sockConnId = jo.getString("sock_connid") ;
	}
	
	public boolean setAcceptedSocket(Socket sock)// throws IOException
	{
		if(this.sock!=null)
		{//
			disconnect();
			this.fireConnInvalid();
		}
		
		try
		{
			this.sock = sock ;
			inputS = sock.getInputStream();
			outputS = sock.getOutputStream();
	
			this.fireConnReady();
			return true ;
		}
		catch (Exception ee)
		{
			disconnect();
			return false;
		}
	}

	public String getSockConnId()
	{
		if(sockConnId==null)
			return "" ;
		return sockConnId;
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
		return this.sockConnId;
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
	
	public void RT_checkConn()
	{}

	private long lastChk = -1;

	void checkConn()
	{
		if(System.currentTimeMillis()-lastChk<5000)
			return ;
		
		try
		{
			//connect();
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
		if(sock==null)
			return true ;
		return sock.isClosed();
	}

	@Override
	public boolean isConnReady()
	{
		return sock!=null;
	}
	
	public String getConnErrInfo()
	{
		if(sock==null)
			return "no connection" ;
		else
			return null ;
	}

	@Override
	public void close() throws IOException
	{
		disconnect();
	}

}
