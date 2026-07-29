package org.iottree.driver.common.modbus;


import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.net.*;

import org.w3c.dom.Element;


public class ModbusConnServer
//implements Runnable,IServerBootComp,IAppWebConfigLoadedListener
{
//	static ILogger log = LoggerManager.getLogger("MConnServer");
//
//    static int port = 2401 ;
//
//    ServerSocket server = null;
//    Thread serverThread = null,serverScheTh = null ;
//    boolean bRun = false;
//    
//    ThreadPoolExecutor  pool = null ;
//    
//    static int MAX_CLIENT_NUM = 100 ;
//    int CHECK_CLIENT_INTERVAL = 30000 ;//����ն˵�ʱ����
//
//    //IMCmdHandler handler = null;
//    public MConnServer(int port)//, IMCmdHandler h
//    {
//        this.port = port;
//        //handler = h;
//        
//        try
//    	{
//    		loadConfig() ;
//    	}
//    	catch(Exception ee)
//    	{
//    		ee.printStackTrace() ;
//    	}
//    }
//    
//    public MConnServer()//IMCmdHandler h
//    {
//    	this(port);
//    }
//    
//
//    public void onAppWebConfigLoaded(AppWebConfig awc, ClassLoader comp_cl)
//	{
////    	/�������ݴ������ӿ�
//        Element mconnele = awc.getConfElement("mconn");
//        if(mconnele==null)
//        	return ;
//        
//        for(Element lisele:Convert.getSubChildElement(mconnele, "data_handler"))
//        {
//        	String cn = lisele.getAttribute("classname") ;
//        	if(Convert.isNullOrEmpty(cn))
//        		continue ;
//
//        	try
//			{
//        		Class cc = comp_cl.loadClass(cn);
//            	if(cc==null)
//            		continue ;
//        		IMCmdDataCompHandler h = (IMCmdDataCompHandler)cc.newInstance() ;
//        		MCmdServerForClient.dataCompHandlers.add(h) ;
//        		System.out.println("  MConnServer    succ load data handler="+cn) ;
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//				System.out.println("  MConnServer  failed load data handler="+cn) ;
//			}
//        }
//        //data_listener
//	}
//    
//    
//    
//    /**
//     * װ��������Ϣ
//     * 1���統ǰ�����ƶ��ն˵�����--�����������ж��̳߳صĸ���
//     * 2��
//     */
//    private static void loadConfig() throws Exception
//    {
//    	String fp = AppConfig.getDataDirBase()+"/mconn/conf.xml";
//    	File f = new File(fp) ;
//    	if(!f.exists())
//    		return ;
//    	
//    	XmlData xd = XmlData.readFromFile(f) ;
//    	MAX_CLIENT_NUM = xd.getParamValueInt32("max_client_num", 100);
//    	port = xd.getParamValueInt32("port", MCmd.DEFAULT_PORT);
//    }
//    
//    public static void saveConfig(int max_client_n,int port) throws Exception
//    {
//    	XmlData xd = new XmlData() ;
//    	
//    	//MAX_CLIENT_NUM = xd.getParamValueInt32("max_client_num", 100);
//    	xd.setParamValue("max_client_num", max_client_n);
//    	xd.setParamValue("port", port);
//    	
//    	String fp = AppConfig.getDataDirBase()+"/mconn/conf.xml";
//    	File f = new File(fp) ;
//    	if(!f.getParentFile().exists())
//    		f.getParentFile().mkdirs() ;
//    	
//    	XmlData.writeToFile(xd, f);
//    }
//    
//    public static int getConfigPort()
//    {
//    	return port ;
//    }
//    
//    public static int getConfMaxClientN()
//    {
//    	return MAX_CLIENT_NUM ;
//    }
//    
//    /**
//     * �õ���ǰ�ͻ���������
//     * @return
//     */
//    public int getClientConnCount()
//    {
//    	return MCmdServerForClient.getClientConnCount();
//    }
//    
//    public ModbusConnInfo[] getClientConnInfos()
//    {
//    	MCmdServerForClient[] sfc =  MCmdServerForClient.getAllClients() ;
//    	if(sfc==null)
//    		return null ;
//    	
//    	ModbusConnInfo[] rets = new ModbusConnInfo[sfc.length];
//    	for(int i = 0 ; i < sfc.length ; i ++)
//    	{
//    		rets[i] = sfc[i].getClientInfo() ;
//    	}
//    	
//    	return rets ;
//    }
//    
//    Runnable scheRT = new Runnable()
//    {
//
//		public void run()
//		{
//			try
//			{
//				while (bRun)
//	            {
//					try
//					{
//						Thread.sleep(CHECK_CLIENT_INTERVAL);//�ȴ�һ��ʱ����
//						//����һ���µ�����
//						
//						List<MCmdServerForClient> cs = MCmdServerForClient.getAllClientsList();
//						
//						for(MCmdServerForClient fc:cs)
//						{
//							Future<Boolean> fold = fc.future ;
//							if(fold!=null&&!fold.isDone() && !fold.isCancelled())
//							{
//								continue ;
//							}
//							
//							Future<Boolean> f = pool.submit((Callable)fc) ;
//							fc.future = f ;
//						}
//					}
//					catch(Exception ee)
//					{
//						//ee.printStackTrace() ;
//						//System.out.println(ee.getMessage()) ;
//						if(log.isErrorEnabled())
//							log.error(ee) ;
//						
//					}
//	            }
//			}
//			finally
//			{
//				serverScheTh = null ;
//			}
//		}
//    	
//    };
//    
//    synchronized public void start()
//    {
//        if(serverThread!=null)
//                return ;
//
//        pool = (ThreadPoolExecutor )Executors.newFixedThreadPool(MAX_CLIENT_NUM/3);
//        
//        bRun = true ;
//        serverThread = new Thread(this,"m_conn_server") ;
//        serverThread.start() ;
//        
//        serverScheTh = new Thread(scheRT,"m_conn_sche") ;
//        serverScheTh.start() ;
//        
//        
//    }
//
//    synchronized public void stop()
//    {
//    	if (server != null)
//        {
//        	try
//        	{
//        		server.close();
//        	}
//        	catch(Exception e)
//        	{}
//        	
//        	server = null ;
//        }
//        
//        pool.shutdownNow() ;
//        
//    	Thread st = serverThread;
//    	Thread t = serverScheTh;
//    	
//    	if(st!=null)
//    	{
//    		st.interrupt() ;
//    	}
//    	if(t!=null)
//    	{
//    		t.interrupt() ;
//    	}
//    	
//        bRun = false;
//        serverScheTh = null ;
//        serverThread = null ;
//    }
//
//    public void run()
//    {
//        try
//        {
//            //IPAddress.
//            //IPAddress localAddr = IPAddress.Parse("127.0.0.1");
//            server = new ServerSocket(port,100);
//            //server..Start();
//            // Enter the listening loop.
//            System.out.println("MCmd Server started..<<<<<.,ready to recv client connection on port="+port);
//            while (bRun)
//            {
//                Socket client = server.accept() ;
//
//                MCmdServerForClient sfc = new MCmdServerForClient(client);
//                sfc.Start();
//            }
//        }
//        catch (Exception e)
//        {
//        	e.printStackTrace();
//            //if (log.IsErrorEnabled)
//                log.error(e);
//        }
//        finally
//        {
//            // Stop listening for new clients.
//        	close();
//
//        	System.out.println("MCmd Server stoped..") ;
//            serverThread = null ;
//            //server = null;
//            bRun = false;
//        }
//    }
//
//
////    MsgCmd DoCmd(MsgCmd mc)
////    {
////        return handler.OnCmd(mc);
////    }
//
//    
//    public void close()
//    {
//        stop() ;
//    }
//    
//    
//    public static void main(String[] args) throws Exception
//    {
//    	
//    	String inputLine;
//		BufferedReader in = new BufferedReader(
//			new InputStreamReader(
//			System.in));
//		
//		String sDevId = null ;
//		
//		MConnServer mcs = null ;
//		if(args.length>=1)
//		{
//			mcs = new MConnServer(Integer.parseInt(args[0])) ;
//		}
//		else
//		{
//			mcs = new MConnServer() ;
//		}
//		
//		MCmdServerForClient sfc = null ;
//		mcs.start() ;
//		
//		System.out.print(">") ;
//
//		while ( (inputLine = in.readLine()) != null)
//		{
//			try
//			{
//				if(sDevId!=null&&!sDevId.equals(""))
//				{
//					sfc = MCmdServerForClient.getClientById(sDevId) ;
//					//if(sfc==null)
//				}
//				StringTokenizer st = new StringTokenizer(inputLine, " ", false);
//				String cmds[] = new String[st.countTokens()];
//				for (int i = 0; i < cmds.length; i++)
//				{
//					cmds[i] = st.nextToken();
//				}
//				if(cmds.length<=0)
//					continue ;
//				
//				if ("send".equals(cmds[0]))
//				{
//					if(sfc==null)
//					{
//						System.out.println("please select client ") ;
//						continue ;
//					}
//					
//					if(cmds.length<=1)
//					{
//						System.out.println("using: send cmd param") ;
//						continue ;
//					}
//					
//					MCmd retmc = null ;
//					if(cmds.length==2)
//						retmc = sfc.sendManualCmd(cmds[1], null,false);
//					else
//						retmc = sfc.sendManualCmd(cmds[1], cmds[2],false);
//					
//					if(retmc==null)
//						System.out.println("may cmd in running with client!") ;
//					else
//						System.out.println(retmc) ;
//				}
//				else if("pool".equals(cmds[0]))
//				{
//					System.out.println("que size="+mcs.pool.getQueue().size()+" of "+mcs.pool.getMaximumPoolSize()) ;
//				}
//				else if("sendtest".equals(cmds[0]))
//				{
//					if(sfc==null)
//					{
//						System.out.println("please select client ") ;
//						continue ;
//					}
//					
////					ArrayList<MCmd> mcss =new ArrayList<MCmd>() ;
////					for(int i=0;i<1000;i++)
////					{
////						mcss.add(new MCmd("test_cmd", "adfasdfasdfsadf7667655"));
////						
////					}
////					
////					sfc.setCmdsToBeSent(mcss);
//				}
//				else if("select".equals(cmds[0]))
//				{
//					if(cmds.length<2)
//					{
//						System.out.println("select devid") ;
//						continue ;
//					}
//					MCmdServerForClient fc = MCmdServerForClient.getClientById(cmds[1]) ;
//					if(fc==null)
//					{
//						System.out.println("no find devid="+cmds[1]) ;
//						continue ;
//					}
//					
//					//sfc = fc ;
//					sDevId = cmds[1] ;
//				}
//				else if ("list".equals(cmds[0]))
//				{
//					for(MCmdServerForClient fc:MCmdServerForClient.getAllClients())
//					{
//						System.out.println(fc.getClientInfo());
//					}
//				}
//				else if("exit".equals(cmds[0]))
//				{
//					break;
//				}
//			}
//			catch (Exception _e)
//			{
//				_e.printStackTrace();
//			}
//			finally
//			{
//				if(sDevId!=null)
//					System.out.print(sDevId) ;
//				System.out.print(">") ;
//			}
//		}
//		
//		System.exit(0);
//    }
//
//	public String getBootCompName()
//	{
//		return "mconn_server";
//	}
//
//	public void startComp() throws Exception
//	{
//		start() ;
//	}
//
//	public void stopComp() throws Exception
//	{
//		stop();
//	}
//
//	public boolean isRunning() throws Exception
//	{
//		return bRun;
//	}

	
}
