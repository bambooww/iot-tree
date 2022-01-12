package org.iottree.server;

import java.io.*;
import java.util.*;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.security.SecurityClassLoad;
import org.apache.catalina.startup.Tomcat;
import org.iottree.core.Config;
import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.service.ServiceManager;
import org.iottree.core.util.Convert;
import org.w3c.dom.Element;

public class Server
{
	static ClassLoader dynCL = null;
	
	static ServerTomcat serverTomcat = null ; 

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

		String jdkhome = System.getProperties().getProperty("java.home");

		if (jdkhome == null || jdkhome.equals(""))
		{
			System.out.println("Error:No java.home found.....");
			System.out.println("    You can start with -D option,like: -Djdk.home=c:/jdk1.5 ");
			System.out.println("    or you can config it in [tomato.conf]");
			System.out.println("    and try start server again!");
			System.exit(0);
			return;
		}

		if (Config.isDebug())
			System.out.println("java.home=" + jdkhome);

		System.getProperties().setProperty("java.util.logging.manager", "org.apache.juli.ClassLoaderLogManager");
		System.getProperties().setProperty("java.util.logging.config.file", "./tomcat/conf/logging.properties");
		System.getProperties().setProperty("java.endorsed.dirs", "./tomcat/common/endorsed");
		System.getProperties().setProperty("catalina.base", "./tomcat");
		System.getProperties().setProperty("catalina.home", "./tomcat");
		System.getProperties().setProperty("tomcat.util.scan.StandardJarScanFilter.jarsToSkip", "*");
		System.getProperties().setProperty("java.io.tmpdir", "./tomcat/temp");

		System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");

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

					// System.out.println("find server comp:" + serv_comp_cn);

				}
			}
		}

		serverTomcat = new ServerTomcat();
		serverTomcat.startTomcat(tbs_loader);

		ServiceManager.getInstance();
		// System.out.println(" all web comp loaded,fire event");
		// runFileMon();
		UAManager.getInstance().start();

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
		
		UAManager.getInstance().stop();
		try
		{
			ServerBootCompMgr.getInstance().stopAllBootComp();
		}
		catch ( Exception ee)
		{
		}

		//

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
