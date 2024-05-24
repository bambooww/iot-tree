package org.iottree.server;

import java.io.*;
import java.util.*;

import org.iottree.core.Config;
import org.iottree.core.UAManager;
import org.iottree.core.UAServer;
import org.iottree.core.sim.SimManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IServerBootComp;
import org.iottree.core.ws.WSHelper;
import org.w3c.dom.Element;

public class Server
{
	static ClassLoader dynCL = null;
	
	static ServerTomcat serverTomcat = null ; 
	
	static ArrayList<IServerBootComp> serverComps = new ArrayList<>() ;

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
						System.out.println("\r\nfile base="+Config.getConfigFileBase()) ;
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

		ClassLoader tbs_loader = Thread.currentThread().getContextClassLoader();

		//String jdkhome = System.getProperties().getProperty("java.home");

//		if (jdkhome == null || jdkhome.equals(""))
//		{
//			System.out.println("Error:No java.home found.....");
//			System.out.println("    You can start with -D option,like: -Djdk.home=c:/jdk ");
//			System.out.println("    or you can config it in [tomato.conf]");
//			System.out.println("    and try start server again!");
//			System.exit(0);
//			return;
//		}
//
//		if (Config.isDebug())
//			System.out.println("java.home=" + jdkhome);


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

		serverTomcat = new ServerTomcat();
		List<UAServer.WebItem> wis = serverTomcat.startTomcat(tbs_loader);

		//------ after tomcat start 
		// old service start here
		// -----

		for(String n:ServerBootCompMgr.getInstance().getAllServerBootNames())
			ServerBootCompMgr.getInstance().startBootComp(n);
		
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
		if(serverTomcat!=null)
		{
			try
			{
				serverTomcat.stopComp();
			}
			catch(Exception e)
			{
				
			}
		}
		
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

	public static void main(String[] args) throws Exception
	{
		boolean bservice = false;
		if (args.length > 0)
		{
			switch (args[0])
			{
			case "linux_nohup":
			case "service":
				bservice = true ;
				break;
			}

		}
		
		startServer(bservice);
		

		// System.exit(0);
	}
}
