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
					log.println("exit - stop server and exit!") ;
					log.println("ver - show ver!") ;
				}
				else if ("ver".equals(cmds[0]))
				{
					log.println("IOT-Tree Server,Version:"+Config.getVersion());
				}
				else
				{
					log.println("unknow cmd , using ? or help !") ;
				}
			}
			catch (Exception _e)
			{
				_e.printStackTrace();
			}
			finally
			{
				log.print("iottree->");
			}
		}
		
		
	}
	
}
