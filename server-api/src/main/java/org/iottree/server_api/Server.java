package org.iottree.server_api;

import java.io.*;
import java.util.*;

import org.iottree.core.Config;
import org.iottree.core.UAManager;
import org.iottree.core.UAServer;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IServerBootComp;
import org.iottree.core.util.ServerCtrlHandler;
import org.iottree.ext.ExtManager;
import org.iottree.pro.ProMain;
import org.w3c.dom.Element;

public class Server
{
	static
	{
		Locale.setDefault(Locale.US); 
		System.setProperty("polyglot.engine.WarnInterpreterOnly", "false");
	}
	
	protected static ExtManager ext_mgr = new ExtManager() ;//make graalvm known
	
	protected static ProMain proMain = new ProMain() ;//make graalvm known
	
	static ClassLoader dynCL = null;
	
	static ArrayList<IServerBootComp> serverComps = new ArrayList<>() ;
	
	//static ServerQuarkus serverQuarkus = null ;
	
	static ServerUndertow serverUndertow = new ServerUndertow() ;

	private static void printBanner()
	{
		try
		{
			try (InputStream inputs = Server.class.getResourceAsStream("banner.txt");)
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(inputs));
				String ln;
				do
				{
					ln = br.readLine();
					if (ln != null)
						System.out.print("\r\n" + ln);
					else
					{
						System.out.print("   version " + Config.getVersion());
						String prodn = Config.getProductName() ;
						if(Convert.isNotNullEmpty(prodn))
							System.out.print("\r\nproduct="+prodn) ;
						System.out.print("\r\nfile base="+Config.getConfigFileBase()) ;
						System.out.print("\r\nData Dir Base=" + Config.getDataDirBase());
						System.out.print("\r\ndata_dyn_dir=" + Config.getDataDynDirBase());
						System.out.print("\r\nLib Dir Base=" + Config.getLibDirBase());
						
						System.out.print("\r\n\r\n");
						break;
					}
				} while (ln != null);

			}
		}
		catch ( Exception ee)
		{
		}
	}
	
	
	static synchronized void loadAndStart() throws Exception
	{

		//serverQuarkus = new ServerQuarkus() ;
		
		ClassLoader tbs_loader = Thread.currentThread().getContextClassLoader();

		Element sysele = Config.getConfElement("system");
		if (sysele != null)
		{
			Element[] scs = Convert.getSubChildElement(sysele, "server_comp");
			if (scs != null)
			{
				for (Element sc : scs)
				{
					String serv_comp_cn = sc.getAttribute("class");
					if (Convert.isNullOrEmpty(serv_comp_cn))
						continue;

					try
					{
						System.out.println("find server comp:" + serv_comp_cn);
						ServerBootCompMgr.registerServerBoolComp(serv_comp_cn) ;
						
//						Class<?> c = Class.forName(serv_comp_cn) ;
//						IServerBootComp comp = (IServerBootComp)c.newInstance(); 
//						serverComps.add(comp) ;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		serverUndertow.start(7070);
		
		
	}

	static void startServer(boolean bservice) throws Exception
	{
		printBanner();

		if (Config.isDebug())
			System.out.println("user.dir=" + System.getProperties().getProperty("user.dir"));

		// System.out.println("java.class.path=" +
		// System.getProperties().getProperty("java.class.path"));

		Config.loadConf();

		// ClassLoader tbs_loader = null;
		//
		// if (tbs_loader != null)
		// {
		// Thread.currentThread().setContextClassLoader(tbs_loader);
		// SecurityClassLoad.securityClassLoad(tbs_loader);
		// } else
		// {
		// tbs_loader = Thread.currentThread().getContextClassLoader();
		// }

		List<UAServer.WebItem> wis = null;
		
		loadAndStart();

		//------ after tomcat start 
		// old service start here
		// -----

		for(String n:ServerBootCompMgr.getInstance().getAllServerBootNames())
			ServerBootCompMgr.getInstance().startBootComp(n);
		
		UAManager.getInstance() ;
		
		UAServer.onServerStarted(wis);
		
		if(bservice)
		{
			new Thread(Server::runFileMon,"").start();
		}
		else
		{
			consoleRunner.run();
		}
		// new Thread(consoleRunner).start();
	}

	static void stopServer()
	{
		serverUndertow.stop();
		
		UAServer.beforeServerStop();

		try
		{
			ServerBootCompMgr.getInstance().stopAllBootComp();
			
			Thread.sleep(5000);
		}
		catch ( Exception ee)
		{
		}

		//
		//stopTomcat() ;
	}
	
	

	static Runnable consoleRunner = new Runnable() {
		public void run()
		{
			try
			{
				ServerCtrlHandler sch = new ServerCtrlHandler(System.in);
				sch.handle();
				stopServer();
			}
			catch ( Exception e)
			{
				e.printStackTrace();
			}
		}
	};

	private static void runFileMon()// throws IOException
	{
		try
		{
			File wf = new File("./iottree_running.flag");
			wf.createNewFile();
			File stopwf = new File("./iottree_stopped.flag");
			if (stopwf.exists())
				stopwf.delete();
	
			while (wf.exists())
			{
				try
				{
					Thread.sleep(3000);
				}
				catch ( Exception ex)
				{
				}
			}
	
			// exit
			stopServer();
			stopwf.createNewFile();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	static {
	    System.setProperty("file.encoding", "UTF-8");
	    try {
	        System.setOut(new PrintStream(System.out, true, "UTF-8"));
	    } catch (UnsupportedEncodingException ignored) {}
	}

	public static void main(String[] args) throws Exception
	{
		boolean bservice = false;
		boolean blinux_nohup = false;
		if (args.length > 0)
		{
			switch (args[0])
			{
			case "linux_nohup":
				blinux_nohup = true;
				bservice = true ;
				break ;
			case "service":
				bservice = true ;
				break;
			}

		}
		
		startServer(bservice);
		
		if(blinux_nohup)
		{
			while(true)
			{
				Thread.sleep(60000);
			}
		}
		// System.exit(0);
	}
}
