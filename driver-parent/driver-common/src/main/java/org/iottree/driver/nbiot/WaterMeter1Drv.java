package org.iottree.driver.nbiot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.iottree.core.Config;
import org.iottree.core.ConnPt;
import org.iottree.core.DevAddr;
import org.iottree.core.DevDriver;
import org.iottree.core.UADev;
import org.iottree.core.basic.NameTitleVal;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.conn.ConnPtTcpAccepted;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.driver.nbiot.msg.WMMsg;
import org.iottree.driver.nbiot.msg.WMMsgReport;
import org.iottree.driver.nbiot.msg.WMMsgValveReq;

public class WaterMeter1Drv //extends DevDriver
{
	public WaterMeter1Drv()
	{}
	

//	@Override
//	public boolean checkPropValue(String groupn, String itemn, String strv, StringBuilder failedr)
//	{
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public DevDriver copyMe()
//	{
//		return new WaterMeter1Drv();
//	}
//
//	@Override
//	public String getName()
//	{
//		return "nb_watermeter1";
//	}
//
//	@Override
//	public String getTitle()
//	{
//		return "NB Water Meter1";
//	}
//
//	@Override
//	public Class<? extends ConnPt> supportConnPtClass()
//	{
//		return null;
//	}
//
//	@Override
//	public boolean supportDevFinder()
//	{
//		return false;
//	}
//
//	@Override
//	public List<PropGroup> getPropGroupsForDevDef()
//	{
//		return null;
//	}
//
//	@Override
//	public List<PropGroup> getPropGroupsForCh()
//	{
//		return null;
//	}
//
//	@Override
//	public List<PropGroup> getPropGroupsForDevInCh()
//	{
//		return null;
//	}
//
//	@Override
//	public DevAddr getSupportAddr()
//	{
//		return null;
//	}
//
//	@Override
//	protected void RT_onConnReady(ConnPt cp)
//	{
//		
//	}
//
//	@Override
//	protected void RT_onConnInvalid(ConnPt cp)
//	{
//		
//	}
//
//	@Override
//	protected boolean RT_runInLoop(StringBuilder failedr) throws Exception
//	{
//		return false;
//	}
//
//	@Override
//	public boolean RT_writeVal(UADev dev, DevAddr da, Object v)
//	{
//		return false;
//	}
//
//	@Override
//	public boolean RT_writeVals(UADev dev, DevAddr[] da, Object[] v)
//	{
//		return false;
//	}
	
	
	transient static ILogger log = LoggerManager.getLogger(WaterMeter1Drv.class);
	
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
		
		long acceptedDT = System.currentTimeMillis() ;
		
		ConnPtTcpAccepted assignedCPT = null ;
		
		public AcceptedSockItem(Socket sk,ConnPtTcpAccepted assignedcpt)
		{
			this.sock = sk ;
			this.assignedCPT = assignedcpt ;
		}
		
		public Socket getSocket()
		{
			return sock ;
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
	
	
	String localIP = null;

	int localPort = 25000;
	
	String ashName = null ;
	
	XmlData ashParams = null ;
	
	/**
	 * unaccepted sock (not be assigned to connpt) keep time out
	 */
	long freeSockTO = 300000 ;
	
	transient private AcceptedSockHandler acceptedSockH = null ;

	transient private ServerSocket serverSock = null;

	transient private Thread acceptTh = null;

	/**
	 * all socket item
	 */
	transient private ArrayList<AcceptedSockItem> sockItems = new ArrayList<>() ;

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

	public void start() throws Exception
	{
		
		synchronized(this)
		{
			if(this.acceptTh!=null)
				return ;
			
			this.acceptTh = new Thread(acceptRunner,"iottree-nbiot-watermeter1-tcpserver");
			this.acceptTh.start();
		}
	}
	
	public void disconnAll() //throws IOException
	{
		for(WaterMeterConnAccepted ci:WaterMeterConnAccepted.listConns())
		{
			try
			{
				ci.disconnect();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void stop()
	{
		
		
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
	
	
	int rep_st = 0 ;
	
	IOnReport onReport = new IOnReport() {

		@Override
		public List<WMMsg> onMsgReport(WMMsgReport report)
		{
			if(rep_st==0)
			{
				rep_st = 1 ;
				return null ;
			}
			
			ArrayList<WMMsg> rets = new ArrayList<>();
			if(rep_st==1)
			{
				WMMsgValveReq m  = new WMMsgValveReq();
				m.setMeterAddr(report.getMeterAddr());
				m.setValveOpen(false);
				rets.add(m);
				rep_st = 2 ;
			}
			else if(rep_st==2)
			{
				WMMsgValveReq m  = new WMMsgValveReq();
				m.setMeterAddr(report.getMeterAddr());
				m.setValveOpen(true);
				rets.add(m);
				rep_st = 0 ;
			}
			return rets;
		}} ;

	private class ASHThread extends Thread
	{
		Socket sock = null ;
		
		public ASHThread(Socket sock)
		{
			this.sock = sock ; 
		}
		
		public void run()
		{
			WaterMeterConnAccepted connapt = null ;
			
			try
			{
				connapt = new WaterMeterConnAccepted() ;
				connapt.setAcceptedSocket(this.sock) ;
				connapt.setOnReport(onReport);
				connapt.runInTh(freeSockTO) ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				if(log.isDebugEnabled())
					log.debug("ASHThread", e);
			}
			finally
			{
				if(connapt!=null)
				{
					connapt.disconnect();
				}
			}
		}
	}
	
	

	private Runnable acceptRunner = new Runnable() {
		public void run()
		{
			try
			{
				serverSock = new ServerSocket(localPort, 100);

				System.out.println(
						"Water Meter1Drv started..<<<<<.,ready to recv client connection on port=" + localPort);

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

	
	public static void main(String[] args) throws Exception
	{
		WaterMeter1Drv wmd = new WaterMeter1Drv() ;
		if(args.length>0)
			wmd.localPort = Convert.parseToInt32(args[0], 1901) ;
		else
			wmd.localPort = 1901 ;
		
		wmd.start();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
		String inputLine;
		
		//printVer() ;//printVer to client
		
		System.out.print("wm->");
		
		while ((inputLine = in.readLine()) != null)
		{
			try
			{//System.out.println("read line="+inputLine) ;
				StringTokenizer st = new StringTokenizer(inputLine, " ", false);
				String cmds[] = new String[st.countTokens()];
				for (int i = 0; i < cmds.length; i++)
				{
					cmds[i] = st.nextToken();
				}
				if (cmds.length == 0)
					continue;

				if ("exit".equals(cmds[0])||"disconnect".equalsIgnoreCase(cmds[0]))
				{
					wmd.stop();
					System.exit(0);
				}
				else if ("?".equals(cmds[0])||"help".equalsIgnoreCase(cmds[0]))
				{
					System.out.println("exit - stop and exit!") ;
					System.out.println("ver - show ver!") ;
				}
				else if ("ver".equals(cmds[0]))
				{
					System.out.println("Version:"+Config.getVersion());
				}
				else
				{
					System.out.println("unknow cmd , using ? or help !") ;
				}
			}
			catch (Exception _e)
			{
				_e.printStackTrace();
			}
			finally
			{
				System.out.print("wm->");
			}
		}
		
	}
}
