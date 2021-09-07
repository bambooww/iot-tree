package org.iottree.core.conn;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.iottree.core.UACh;
import org.iottree.core.basic.NameTitleVal;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONArray;
import org.json.JSONObject;


public class ConnProTcpServer extends ConnProvider
{
	transient static ILogger log = LoggerManager.getLogger(ConnProTcpServer.class);
	
	public static interface AcceptedSockHandler
    {
		public String getName() ;
		
		public String getTitle() ;
		
		public NameTitleVal[] getParamDefs() ;
		
		public XmlData chkAndCreateParams(HashMap<String,String> pn2strv,StringBuilder failedr) ;
		
		public void setParams(XmlData xd) ;
		
		/**
		 * 
		 * @param sock
		 * @return
		 */
    	public String checkSockConnId(Socket sock) throws Exception;
    	
    	
    	public int getRecvTimeout() ;
    	
    	public int getRecvEndTimeout() ;
    }
	
	
	public static class AcceptedSockItem
	{
		Socket sock = null ;
		
		String connId = null ;
		
		long acceptedDT = System.currentTimeMillis() ;
		
		ConnPtTcpAccepted assignedCPT = null ;
		
		public AcceptedSockItem(Socket sk,String connid,ConnPtTcpAccepted assignedcpt)
		{
			this.sock = sk ;
			this.connId = connid ;
			this.assignedCPT = assignedcpt ;
		}
		
		public Socket getSocket()
		{
			return sock ;
		}
		
		public String getConnId()
		{
			return connId ;
		}
		
		public long getAcceptedDT()
		{
			return acceptedDT ;
		}
		
		public ConnPtTcpAccepted getAssignedCPT()
		{
			return this.assignedCPT;
		}
		
		public boolean isTimeout(long to)
		{
			return System.currentTimeMillis()>this.acceptedDT+to ;
		}
		
		public boolean isFree()
		{
			return assignedCPT==null ;
		}
	}
	
	
	private final static String TP = "tcp_server" ; 
	
	private static List<AcceptedSockHandler> acceptedSockHs = null ; 
	
	public static AcceptedSockHandler getAcceptedSockHandler(String name)
	{
		for(AcceptedSockHandler ash: getAcceptedSockHandlers())
		{
			if(name.equals(ash.getName()))
				return ash ;
		}
		return null ;
	}
	
	private static AcceptedSockHandler createASHByName(String name)
	{
		AcceptedSockHandler ash = getAcceptedSockHandler(name) ;
		try
		{
			return (AcceptedSockHandler)ash.getClass().newInstance() ;
		}
		catch(Exception e)
		{
			return null ;
		}
	}
	
	public static List<AcceptedSockHandler> getAcceptedSockHandlers()
	{
		if(acceptedSockHs!=null)
			return acceptedSockHs ;
		
		ArrayList<AcceptedSockHandler> ss = new ArrayList<>() ;
		JSONObject jo = ConnProvider.getJSONConfigByTP(TP) ;
		JSONArray ashjos = jo.getJSONArray("accept_sock_handlers") ;
		int len = ashjos.length() ;
		for(int i = 0 ; i < len ; i ++)
		{
			String cn = ashjos.getString(i) ;
			AcceptedSockHandler ash = loadASH(cn) ;
			if(ash==null)
				continue ;
			ss.add(ash) ;
		}
		acceptedSockHs = ss ;
		return ss;
	}
	
	private static AcceptedSockHandler loadASH(String classn)
	{
		try
		{
			Class<?> c = Class.forName(classn) ;
			return (AcceptedSockHandler)c.newInstance() ;
		}
		catch(Exception e)
		{
			System.err.println(" load AcceptedSockHandler error:"+classn) ;
			return null ;
		}
	}
	
	String localIP = null;

	int localPort = 25000;
	
	String ashName = null ;
	
	XmlData ashParams = null ;
	
	/**
	 * unaccepted sock (not be assigned to connpt) keep time out
	 */
	long freeSockTO = 10000 ;
	
	transient private AcceptedSockHandler acceptedSockH = null ;

	transient private ServerSocket serverSock = null;

	transient private Thread acceptTh = null;

	/**
	 * all socket item
	 */
	transient private ArrayList<AcceptedSockItem> sockItems = new ArrayList<>() ;

	@Override
	public String getProviderType()
	{
		return TP;
	}
	
	public boolean isSingleProvider()
	{
		return false;
	}
	
	
	public String getLocalIP()
	{
		if(localIP==null)
			return "" ;
		return localIP ;
	}
	
	public int getLocalPort()
	{
		return localPort ;
	}
	
	public String getAshName()
	{
		if(this.ashName==null)
			return "" ;
		return ashName ;
	}
	
	public XmlData getAshParams()
	{
		return ashParams ;
	}
	
	public AcceptedSockHandler getASH()
	{
		return acceptedSockH ;
	}
	
	public String getStaticTxt()
	{
		return getLocalIP()+":"+this.localPort ;
	}

	public boolean fromXmlData(XmlData xd, StringBuilder errsb)
	{
		if (!super.fromXmlData(xd, errsb))
			return false;

		this.localIP = xd.getParamValueStr("local_ip") ;
		this.localPort = xd.getParamValueInt32("local_port", 25000) ;
		this.ashName = xd.getParamValueStr("ash_name") ;
		this.ashParams = xd.getSubDataSingle("ash_params") ;
		
		if(Convert.isNotNullEmpty(ashName))
		{
			acceptedSockH = createASHByName(this.ashName) ;
			//acceptedSockH.chkAndCreateParams(pn2strv)
			acceptedSockH.setParams(this.ashParams);
		}
		
		return true;
	}

	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		if(this.localIP!=null)
			xd.setParamValue("local_ip", this.localIP);
		xd.setParamValue("local_port", this.localPort);
		if(this.ashName!=null)
			xd.setParamValue("ash_name", this.ashName);
		if(this.ashParams!=null)
			xd.setSubDataSingle("ash_params", this.ashParams);

		return xd;
	}

	protected void injectByJson(JSONObject jo) throws Exception
	{
		String tmp_localip = jo.optString("local_ip") ;
		int tmp_localport = jo.optInt("local_port", 0) ;
		if(tmp_localport<=0)
			throw new Exception("invalid local port") ;
		
		String tmp_ashn = jo.optString("ash_name") ;
		if(Convert.isNullOrEmpty(tmp_ashn))
			throw new Exception("ash_name cannot be null ") ;
		JSONObject pms = jo.optJSONObject("ash_params") ;
		HashMap<String,String> pm_vs = new HashMap<>() ;
		if(pms!=null)
		{
			for(String pn:pms.keySet())
			{
				String pv = pms.getString(pn) ;
				pm_vs.put(pn, pv) ;
			}
		}
		
		AcceptedSockHandler ash = createASHByName(tmp_ashn) ;
		if(ash==null)
			throw new Exception("no ash found with name="+tmp_ashn) ;
		
		StringBuilder failedr = new StringBuilder() ;
		XmlData params = ash.chkAndCreateParams(pm_vs, failedr) ;
		if(params==null)
			throw new Exception(failedr.toString()) ;
		ash.setParams(params);
		
		//set super first
		super.injectByJson(jo);
		
		//super succ,then this self
		this.localIP = tmp_localip;
		this.localPort = tmp_localport;
		this.ashName = tmp_ashn ;
		this.ashParams = params ;
		this.acceptedSockH = ash ;
	}

	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtTcpAccepted.class;
	}

	@Override
	protected long connpRunInterval()
	{
		// TODO Auto-generated method stub
		return 500;
	}

	@Override
	protected void connpRunInLoop() throws Exception
	{//monitor accept socket
		chkSockItems() ;
	}
	
	public void start() throws Exception
	{
		AcceptedSockHandler ash = getASH() ;
		if(ash==null)
			throw new Exception("no accepted sock handler found") ;
		super.start();
		
		synchronized(this)
		{
			if(this.acceptTh!=null)
				return ;
			
			this.acceptTh = new Thread(acceptRunner);
			this.acceptTh.start();
		}
	}
	
	public void disconnAll() //throws IOException
	{
		for(ConnPt ci:this.listConns())
		{
			try
			{
				ConnPtTcpAccepted conn = (ConnPtTcpAccepted)ci ;
				conn.disconnect();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void stop()
	{
		super.stop();
		
		synchronized(this)
		{
			stopServer() ;
			
			Thread t = this.acceptTh ;
			if(t!=null)
				t.interrupt();
			this.acceptTh = null ;
			
			disconnAll();
		}
	}
	
	private void stopServer()
	{
		if (serverSock != null)
		{
			try
			{
				serverSock.close();
			} catch (Exception e)
			{
			}

			serverSock = null;
		}

		// server = null;
		acceptTh = null;
	}
	
	
	public ConnPtTcpAccepted getConnPtTcpBySockId(String sockconnid)
	{
		for(ConnPt cpt:this.listConns())
		{
			ConnPtTcpAccepted cpttcp = (ConnPtTcpAccepted)cpt ;
			if(sockconnid.equals(cpttcp.getSockConnId()))
				return cpttcp ;
		}
		return null ;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<AcceptedSockItem> getFreeSockItems()
	{
		ArrayList<AcceptedSockItem> rets = new ArrayList<>() ;
		for(AcceptedSockItem asi:this.sockItems)
		{
			if(asi.isFree())
			{
				rets.add(asi) ;
			}
		}
		return rets;
	}
	
	private transient long lastChkSockItemDT = -1 ;
	private transient int chkConnedCC = 0 ;
	
	private void chkSockItems()
	{
		if(System.currentTimeMillis()-lastChkSockItemDT<5000)
			return ;
		
		boolean bchk_conned = false;
		chkConnedCC ++ ;
		if(chkConnedCC>=10)
		{
			chkConnedCC = 0 ;
			bchk_conned = true;
		}
		
		try
		{
			ArrayList<AcceptedSockItem> toitems = new ArrayList<>() ;
			for(AcceptedSockItem asi:this.sockItems)
			{
				if(asi.isFree())
				{
					if(asi.isTimeout(this.freeSockTO))
						toitems.add(asi) ;
				}
				else
				{
					try
					{
						ConnPtTcpAccepted cpta = asi.getAssignedCPT();
						UACh ch = cpta.getJoinedCh() ;
						if(ch==null||bchk_conned)
						{
							try
							{
								asi.getSocket().sendUrgentData(0);
							}
							catch(IOException ioe)
							{
								cpta.close();
								toitems.add(asi);
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				//cpta.checkConn();
			}
			
			//close timeout and del
			for(AcceptedSockItem asi:toitems)
			{
				try
				{
					asi.sock.close();
				}
				catch(Exception ee)
				{}
			}
			if(toitems.size()>0)
			{
				synchronized(this)
				{
					this.sockItems.removeAll(toitems) ;
				}
			}
		}
		finally
		{
			lastChkSockItemDT = System.currentTimeMillis() ;
		}
	}
	
	private  void onSockConnIdAccepted(Socket sock,String connid)
	{
		//TODO check connid already existed or not 
		if(log.isDebugEnabled())
			System.out.println("onSockConnIdAccepted connid="+connid) ;
		ConnPtTcpAccepted cpt = getConnPtTcpBySockId(connid) ;
		AcceptedSockItem asi = new AcceptedSockItem(sock,connid,cpt);
		if(cpt!=null)
			cpt.setAcceptedSocket(sock) ;
		synchronized(this)
		{
			this.sockItems.add(asi) ;
		}
	}

	private class ASHThread extends Thread
	{
		Socket sock = null ;
		
		public ASHThread(Socket sock)
		{
			this.sock = sock ; 
		}
		
		public void run()
		{
			try
			{
				AcceptedSockHandler ash = getASH() ;
				String connid = ash.checkSockConnId(this.sock) ;
				if(Convert.isNullOrEmpty(connid))
					return ;
				onSockConnIdAccepted(sock,connid);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				if(log.isDebugEnabled())
					log.debug("ASH chk err", e);
			}
		}
	}
	
	

	private Runnable acceptRunner = new Runnable() {
		public void run()
		{
			try
			{
				// IPAddress.
				// IPAddress localAddr = InetAddress.getLocalHost().(localIP);
				// if(Convert.isNotNullEmpty(localIP))
				// serverSock = new ServerSocket(localPort,500,)
				// else
				serverSock = new ServerSocket(localPort, 100);

				System.out.println(
						"ConnProTcpServer started..<<<<<.,ready to recv client connection on port=" + localPort);

				while (acceptTh != null)
				{
					Socket client = serverSock.accept();
					new ASHThread(client).start();
				}
			} catch (Exception e)
			{
				System.out.println("ConnProTcpServer Stop Error with port=" + localPort);
				//e.printStackTrace();
				if (log.isDebugEnabled())
					log.debug("", e);
				// if (log.IsErrorEnabled)
				// log.error(e);
			} finally
			{
				// Stop listening for new clients.
				// close();

				if (log.isDebugEnabled())
					log.debug("Modbus Tcp Adapter on port=[" + localPort + "] Server stoped..");
				// serverThread = null ;

				stopServer() ;
			}
		}
	};

}
