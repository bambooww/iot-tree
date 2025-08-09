package org.iottree.server;

import java.io.*;
import java.util.*;

import org.iottree.core.Config;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;


public class ServerCtrlHandler
{
	static ILogger log = LoggerManager.getLogger(ServerCtrlHandler.class) ;
	
	BufferedReader in = null ;
	public ServerCtrlHandler(InputStream cmdinput)
	{
		in = new BufferedReader(new InputStreamReader(cmdinput));
	}
	
	static long M = 1024*1024 ;
	
	public void handle() throws Exception
	{
		
		String inputLine;
		
		//printVer() ;//printVer to client
		
		System.out.print("iottree->");
		
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
					return ;//exit loop and client with be exit
				}
				else if ("exit_server".equals(cmds[0]))
				{
					System.exit(0);
				}
				else if ("?".equals(cmds[0])||"help".equalsIgnoreCase(cmds[0]))
				{
					System.out.println("exit - stop server and exit!") ;
					System.out.println("ver - show ver!") ;
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
						continue;
					if("start".equals(cmds[1]))
						Server.loadAndStartTomcat();
					else if("stop".equals(cmds[1]))
						Server.stopTomcat();
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
				System.out.print("iottree->");
			}
		}
		
		
	}
	
}
