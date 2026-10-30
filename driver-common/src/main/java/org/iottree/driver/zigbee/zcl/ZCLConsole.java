package org.iottree.driver.zigbee.zcl;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IConsole;

public class ZCLConsole implements IConsole
{

	@Override
	public String getConsoleName()
	{
		return "zcl";
	}
	
	public String getConsoleTitle()
	{
		return "ZigBee ZCL (Cluster Library) functions or tester" ;
	}

	@Override
	public int onConsoleCmds(String[] cmds) throws Exception
	{
		if ("exit".equals(cmds[0])||"disconnect".equalsIgnoreCase(cmds[0]))
		{
			return 0 ;//
		}
		else if ("?".equals(cmds[0])||"help".equalsIgnoreCase(cmds[0]))
		{
			System.out.println("cluster_id - check cluster by id!") ;
			System.out.println("cluster_name - check cluster by name!") ;
		}
		else if("cluster_reload".equals(cmds[0]))
		{
			ZCLClusterManager.loadClusters() ;
			System.out.println("reload end") ;
		}
		else if("cluster_id".equals(cmds[0]))
		{
			if(cmds.length<2)
			{
				System.out.print("no id input"); return 1 ;
			}
			int id = Integer.parseInt(cmds[1]) ;
			ZCLCluster c = ZCLClusterManager.getInstance().getCluserByID(id) ;
			if(c==null)
			{
				System.out.print("no cluster found with id="+id); return 1 ;
			}
			Convert.printJSON(c.toJO()) ;
		}
		else if("cluster_name".equals(cmds[0]))
		{
			if(cmds.length<2)
			{
				System.out.print("no name input"); return 1 ;
			}
			ZCLCluster c = ZCLClusterManager.getInstance().getClusterByName(cmds[1]) ;
			if(c==null)
			{
				System.out.print("no cluster found with name="+cmds[1]); return 1 ;
			}
			Convert.printJSON(c.toJO()) ;
		}	
		else if ("ver".equals(cmds[0]))
		{
			
		}
		
		return 1;
	}

}
