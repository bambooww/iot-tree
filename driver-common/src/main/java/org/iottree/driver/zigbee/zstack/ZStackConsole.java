package org.iottree.driver.zigbee.zstack;

import org.iottree.core.DevDriver;
import org.iottree.core.UACh;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IConsole;
import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLClusterManager;
import org.json.JSONObject;

public class ZStackConsole implements IConsole
{
	UACh cur_ch = null ;
	ZStackDriver cur_drv = null ;
	
	@Override
	public String getConsoleName()
	{
		if(cur_ch==null)
			return "zstack";
		else
			return "zstack-"+cur_ch.getNodePath();
	}
	
	public String getConsoleTitle()
	{
		return "ZigBee zstack functions or tester" ;
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
			System.out.println("set_ch [prjn.ch_name] - set current channel with zstack driver") ;
			System.out.println("trigger_msg_recv xx-xx-xx - set current channel with zstack driver") ;
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
		else if("set_ch".equals(cmds[0]))
		{
			if(cmds.length<2)
			{
				this.cur_ch = null ;
				cur_drv = null ;
				return 1;
			}
			UANode nd = UAManager.getInstance().findNodeByPath(cmds[1]) ;
			if(nd==null || !(nd instanceof UACh))
			{
				System.out.println("no UACh node found with path="+cmds[1]) ;
				return 1 ;
			}
			UACh ch = (UACh)nd ;
			DevDriver dd = ch.getDriver() ;
			if(dd==null||!(dd instanceof ZStackDriver))
			{
				System.out.println("no ZStackDriver found with in UACh="+cmds[1]) ;
				return 1 ;
			}
			cur_ch = ch ;
			cur_drv = (ZStackDriver)dd ;
			return 1 ;
		}
		else if("trigger_msg_recv".equals(cmds[0]))
		{
			if(cur_ch==null||cur_drv==null)
			{
				System.out.println("using: set_ch prjn.ch_name first") ;
				return 1;
			}
			if(cmds.length<2)
			{
				System.out.println("using: trigger_msg_recv xx-xx-xx-xx") ;
				return 1;
			}
			StringBuilder failedr = new StringBuilder() ;
			if(cur_drv.getZStackManager().RT_triggerMsgRecv(cmds[1], failedr))
				System.out.println("trigger ok") ;
			else
				System.out.println("trigger failed:"+failedr) ;
		}
		else if("coor".equals(cmds[0]))
		{
			if(cur_drv==null)
			{
				System.out.println("using: set_ch prjn.ch_name first") ;
				return 1;
			}
			
			JSONObject coor_jo = cur_drv.getZStackManager().getCoor().toJO() ;
			Convert.printJSON(coor_jo);
		}
		else if("end_device".equals(cmds[0]))
		{
			if(cmds.length<2)
			{
				System.out.println("using: end_device xx-xx-xx-xx") ;
				return 1;
			}
		}
		else if ("ver".equals(cmds[0]))
		{
			
		}
		
		return 1;
	}

}
