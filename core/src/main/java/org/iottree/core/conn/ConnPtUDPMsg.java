package org.iottree.core.conn;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.iottree.core.ConnProvider;
import org.iottree.core.DevDriver;
import org.iottree.core.DevDriverMsgOnly;
import org.iottree.core.UACh;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

public class ConnPtUDPMsg extends ConnPtMsg
{
	public static final String TP = "udp_msg";
	
	static ILogger log = LoggerManager.getLogger(ConnPtUDPMsg.class) ;
	
	int recvLocPort = 9001 ;
	
	String recvLocIP = null ;
	
	String host = null ;
	
	int port = 9002 ;
	
	private transient Thread recvTh = null ;
	
	private DatagramSocket socket = null;// new DatagramSocket(PORT)
	//DatagramSocket dgSock = null ;
	
	private InetAddress hostAddr = null ;
	
	public ConnPtUDPMsg()
	{}
	
	public ConnPtUDPMsg(ConnProvider cp, String name, String title, String desc)
	{
		super(cp, name, title, desc);
	}
	
	@Override
	public String getConnType()
	{
		return TP;
	}

	@Override
	public String getStaticTxt()
	{
		return this.host+":"+this.port;
	}
	
	public String getHost()
	{
		if(this.host==null)
			return "" ;
		
		return this.host ;
	}
	
	public int getPort()
	{
		return this.port ;
	}
	
	public int getRecvLocPort()
	{
		return this.recvLocPort;
	}
	
	public String getRecvLocIP()
	{
		if(this.recvLocIP==null)
			return "" ;
		
		return this.recvLocIP ;
	}
	
	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		xd.setParamValue("host", host);
		xd.setParamValue("port", port);
		xd.setParamValue("loc_ip", this.recvLocIP);
		xd.setParamValue("loc_port", this.recvLocPort);
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);
		this.host = xd.getParamValueStr("host");
		this.port = xd.getParamValueInt32("port", 9002);
		this.recvLocIP = xd.getParamValueStr("loc_ip");
		this.recvLocPort = xd.getParamValueInt32("loc_port", 9001);
		return r;
	}

	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);

		this.host = jo.getString("host");
		this.port = jo.optInt("port",9002);
		this.recvLocIP = jo.optString("loc_ip");
		this.recvLocPort = jo.optInt("loc_port",9001);
		if(this.recvLocPort<=0)
			this.recvLocPort = 9001 ;
	}
	
	

	@Override
	public boolean isConnReady()
	{
		return socket!=null;
	}
	
	//private long lastChk = -1 ;
	
	public void RT_checkConn()
	{
		
	}
	
	private Runnable recvRunner = new Runnable() {

		@Override
		public void run()
		{
			RT_runRecv() ;
		}} ;
		
	private transient InetAddress lastRecvRemoteAddr = null ;
	
	private transient int lastRecvRemotePort = -1 ;
	
	private void RT_runRecv()
	{
		try
		{
			byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
			while(recvTh!=null)
			{
				socket.receive(packet);
				lastRecvRemoteAddr = packet.getAddress() ;
				lastRecvRemotePort = packet.getPort() ;
				
				onRecvData(packet.getData(),packet.getLength());
			}
		}
		catch(Exception ee)
		{
			if(log.isDebugEnabled())
				log.debug(ee);
		}
		finally
		{
			recvTh = null ;
			disconnect() ;
		}
	}
	
	private void onRecvData(byte[] data,int len)
	{
		byte[] msg = new byte[len] ;
		System.arraycopy(data, 0, msg, 0, len);
		this.RT_onMsgRecved(null,msg);
		
		DevDriverMsgOnly drv = getDriverMsgOnly() ;
		if(drv==null)
			return ;
		drv.RT_onConnMsgIn(msg);
	}
	
	@Override
	public boolean RT_supportSendMsgOut()
	{
		return true;
	}
	
	@Override
	public boolean RT_sendMsgOut(String topic,byte[] msg,StringBuilder failedr) throws Exception
	{
		if(this.socket==null)
		{
			failedr.append("no udp socket") ;
			return false;
		}
		
		InetAddress tar_addr = hostAddr;
		int tar_port = this.port;
		if(tar_addr==null)
		{
			tar_addr = this.lastRecvRemoteAddr ;
			tar_port = this.lastRecvRemotePort ;
		}
		if(tar_addr==null||tar_port<=0)
		{
			failedr.append("no remote host port set or found") ;
			return false;
		}
		DatagramPacket pk = new DatagramPacket(msg, msg.length, tar_addr, tar_port);
        socket.send(pk);
		return true;
	}
	
	public synchronized boolean RT_start()
	{
		if(recvTh!=null)
		{
			return true ;
		}
		
		try
		{
			if(Convert.isNotNullEmpty(this.host))
				hostAddr = InetAddress.getByName(host) ;
			
			if(Convert.isNotNullEmpty(this.recvLocIP))
			{
				InetAddress locaddr= InetAddress.getByName(recvLocIP);
				socket = new DatagramSocket(this.recvLocPort, locaddr) ;
			}
			else
			{
				socket = new DatagramSocket(this.recvLocPort) ;
			}
			//start recv
			recvTh = new Thread(recvRunner) ;
			recvTh.start();
			return true ;
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
				log.debug("listen udp local failed",e);
			return false;
		}
		finally
		{
			//lastChk = System.currentTimeMillis() ;
		}
	}
	
	public synchronized void RT_stop()
	{
		if(recvTh==null)
			return ;
		recvTh.interrupt();
		disconnect();
		recvTh = null ;
	}
	
	public boolean RT_isRunning()
	{
		return recvTh!=null;
	}

	@Override
	public String getConnErrInfo()
	{
		if (socket == null)
			return "local udp bind err";
		else
			return null;
	}
	
	@Override
	public String RT_getConnRunInfo()
	{
		if(this.lastRecvRemoteAddr!=null)
			return "Last Recv "+this.lastRecvRemoteAddr.toString()+":"+this.lastRecvRemotePort ;
		return "" ;
	}
	
	synchronized void disconnect() // throws IOException
	{
		if (socket == null)
			return;

		try
		{
			socket.close();
		}
		catch ( Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			socket = null;
		}
	}
}
