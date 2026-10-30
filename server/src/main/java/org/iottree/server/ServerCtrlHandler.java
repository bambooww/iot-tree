package org.iottree.server;

import java.io.*;
import java.util.*;

import org.iottree.core.Config;
import org.iottree.core.util.IConsole;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;


public class ServerCtrlHandler
{
	static ILogger log = LoggerManager.getLogger(ServerCtrlHandler.class) ;
	
	static DefaultConsole defConsole = new DefaultConsole() ;
	
	static ArrayList<IConsole> consoles = new ArrayList<>() ;
	
	private static void reg_console(String classn)
	{
		try
		{
			Class<?> c = Class.forName(classn) ;
			IConsole cc = (IConsole)c.getConstructor().newInstance() ;
			consoles.add(cc) ;
		}
		catch(Exception e)
		{
			if(log.isWarnEnabled())
				log.warn("reg_console err:"+classn, e);
		}
	}
	
	static
	{
		consoles.add(defConsole) ;
		reg_console("org.iottree.driver.zigbee.zcl.ZCLConsole") ;
		reg_console("org.iottree.driver.zigbee.zstack.ZStackConsole") ;
		reg_console("org.iottree.driver.zigbee.ember.EmberConsole") ;
	}
	
	private static IConsole getConsole(String n)
	{
		for(IConsole c:consoles)
		{
			if(c.getConsoleName().equals(n))
				return c ;
		}
		return null ;
	}
	
	BufferedReader in = null ;
	public ServerCtrlHandler(InputStream cmdinput)
	{
		in = new BufferedReader(new InputStreamReader(cmdinput));
	}
	
	static IConsole cur_console = defConsole ;
	
	static void setConsoleByName(String n)
	{
		IConsole c = getConsole(n) ;
		if(c==null)
			return ;
		cur_console = c ;
	}
	
	public void handle() throws Exception
	{
		
		String inputLine;
		
		//printVer() ;//printVer to client
		
		System.out.print("iottree->");
		
		String[] last_cmds = null ;
		
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
				if("/".equals(cmds[0]))
				{
					cmds = last_cmds ;
					if(cmds==null||cmds.length==0)
						continue ;
				}
				int res = cur_console.onConsoleCmds(cmds) ;
				if(res<0) // exit
					break ;
				else if(res==0)
				{
					if(cur_console!=defConsole)
						cur_console = defConsole ; 
					continue ;
				}
				
				last_cmds = cmds ;
			}
			catch (Exception _e)
			{
				_e.printStackTrace();
			}
			finally
			{
				System.out.print("\r\n"+cur_console.getConsoleName()+"->");
			}
		}
		
		
	}
	
}

class DefaultConsole implements IConsole
{
	static long M = 1024*1024 ;
	
	@Override
	public String getConsoleName()
	{
		return "iottree";
	}
	
	public String getConsoleTitle()
	{
		return "Basic Console in IOTTree" ;
	}

	@Override
	public int onConsoleCmds(String[] cmds) throws Exception
	{
		if ("exit".equals(cmds[0])||"disconnect".equalsIgnoreCase(cmds[0]))
		{
			return -1 ;//exit loop and client with be exit
		}
		else if ("exit_server".equals(cmds[0]))
		{
			System.exit(0);
		}
		else if ("?".equals(cmds[0])||"help".equalsIgnoreCase(cmds[0]))
		{
			System.out.println("exit - stop server and exit!") ;
			System.out.println("ver - show ver!") ;
			System.out.println("cd xxx - set into registered console!") ;
			System.out.println("ls  - list registered console!") ;
			System.out.println("mem [gc]- check memory usage, or trigger gc") ;
		}
		else if("ls".equals(cmds[0]))
		{
			for(IConsole c:ServerCtrlHandler.consoles)
				System.out.println(c.getConsoleName()+" - "+c.getConsoleTitle()) ;
		}
		else if("cd".equals(cmds[0]))
		{
			if(cmds.length<2)
				return 1;
			ServerCtrlHandler.setConsoleByName(cmds[1]) ;
		}
		else if ("ver".equals(cmds[0]))
		{
			System.out.println("IOT-Tree Server,Version:"+Config.getVersion());
		}
		else if ("mem".equals(cmds[0]))
		{
			if(cmds.length>1)
			{
				if("gc".equals(cmds[1]))
				{
					System.gc();
					System.out.println("gc end") ;
				}
			}
			Runtime rt = Runtime.getRuntime() ;
			System.out.println("total mem="+rt.totalMemory()/M+"  free="+rt.freeMemory()/M+" used="+(rt.totalMemory()-rt.freeMemory())/M);
		}
		else if("tomcat".equals(cmds[0]))
		{
			if(cmds.length<=1)
				return 1;
			if("start".equals(cmds[1]))
				Server.loadAndStartTomcat();
			else if("stop".equals(cmds[1]))
				Server.stopTomcat();
		}
		else
		{
			System.out.println("unknow cmd , using ? or help !") ;
		}
		
		return 1 ;
	}
	
}
