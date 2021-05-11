package org.iottree.server;

import java.io.*;
import java.util.*;

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


				if ("start".equals(cmds[0]))
				{
					
				}
				else if ("stop".equals(cmds[0]))
				{
					
				}
				else if ("exit".equals(cmds[0])||"disconnect".equalsIgnoreCase(cmds[0]))
				{
					return ;//exit loop and client with be exit
				}
				else if ("exit_server".equals(cmds[0]))
				{
					System.exit(0);
				}
				
				else if("start_server_comp".equals(cmds[0]))
				{
					ServerBootCompMgr.getInstance().startBootComp(cmds[1]);
				}
				else if("stop_server_comp".equals(cmds[0]))
				{
					ServerBootCompMgr.getInstance().stopBootComp(cmds[1]);
				}
				else if ("startins".equals(cmds[0]))
				{
					log.println("start ins!");
					try
					{

					}
					catch (Exception ee1)
					{
						ee1.printStackTrace();
					}
				}
				else if ("checkprocess".equals(cmds[0]))
				{

				}
				
				else if ("?".equals(cmds[0])||"help".equalsIgnoreCase(cmds[0]))
				{
					log.println("db_access - show current db access runtime info!") ;
					log.println("online_user - show online user info!") ;
					log.println("ls_task - show auto task running info!") ;
					log.println("gdb_list - list gdb config files!") ;
					log.println("gdb_install - intall or init db table by gdb config files!") ;
					log.println("db_connpool - show database connection pool info!") ;
					log.println("dd_list - show system dictionary info!") ;
					log.println("ver - show ver!") ;
				}
				else if ("ver".equals(cmds[0]))
				{
					printVer();
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
	
	private static void printVer()
	{
		log.println("");
		log.println("   *************************************************");
		log.println("   *       IOT Tree Server                         *");
		log.println("   *                                               *");
		log.println("   *  Version : 1.0                                *");
		log.println("   *************************************************");
		log.println("");
	}
}
