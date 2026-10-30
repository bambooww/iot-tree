package org.iottree.driver.zigbee.zspec;

import java.util.List;

import org.iottree.core.DevAddr;
import org.iottree.core.UADev;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.iottree.driver.zigbee.zcl.ZCLAttr;
import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.iottree.driver.zigbee.zcl.ZCLClusterManager;
import org.iottree.driver.zigbee.zcl.ZCLCmd;
import org.iottree.driver.zigbee.zcl.ZCLParam;

public class ZSAddr extends DevAddr
{
	int ep_id = -1 ;
	
	int cluster_id = -1 ;
	
	int attr_id = -1 ;
	
	int cmd_id = -1 ;
	
	int cmd_pm_idx = -1 ;
	
	private transient ZCLCluster clusterDef = null ;
	private transient ZCLAttr attrDef = null ;
	private transient ZCLCmd cmdDef = null ;
	private transient ZCLParam cmdPmDef = null ;
	
	public int getEndPointID()
	{
		return this.ep_id ;
	}
	
	public int getClusterID()
	{
		return this.cluster_id ;
	}
	
	public int getAttrID()
	{
		return this.attr_id ;
	}
	
	public int getCmdID()
	{
		return this.cmd_id ;
	}
	
	public int getCmdPmIdx()
	{
		return this.cmd_pm_idx ;
	}
	
	public boolean isAddrCmd()
	{
		return this.cmd_id>=0 && this.cmd_pm_idx<0 ;
	}
	
	public boolean isAddrCmdPm()
	{
		return this.cmd_id>=0 && this.cmd_pm_idx>=0 ;
	}
	
//	public String getAddrCmdPmKey()
//	{
//		return ep_id+"_"+cluster_id+"_"+cmd_id+"_"+cmd_pm_idx ;
//	}
	
	public boolean isAddrAttr()
	{
		return this.attr_id>=0 ;
	}
	
	public boolean isValid()
	{
		if(ep_id<0) return false;
		if(cluster_id<0) return false;
		if(attr_id<0 && cmd_id<0) return false;
		return true ;
	}
	
	public static ZSAddr createCmdPmAddr(ZSAddr cmd_addr,int pm_idx)
	{
		ZSAddr r = new ZSAddr() ;
		r.ep_id = cmd_addr.ep_id ;
		r.cluster_id = cmd_addr.cluster_id ;
		r.cmd_id = cmd_addr.cmd_id ;
		r.cmd_pm_idx = pm_idx ;
		return r ;
	}
	
	public ZCLCluster getClusterDef()
	{
		if(clusterDef!=null)
			return clusterDef ;
		return clusterDef = ZCLClusterManager.getInstance().getCluserByID(this.cluster_id) ;
	}
	
	public ZCLAttr getAttrDef()
	{
		if(attrDef!=null)
			return attrDef ;
		ZCLCluster c = getClusterDef() ;
		if(c==null)
			return null ;
		return attrDef = c.getAttributeById(this.attr_id) ;
	}
	

	public ZCLCmd getCmdDef()
	{
		if(cmdDef!=null)
			return cmdDef ;
		ZCLCluster c = getClusterDef() ;
		if(c==null)
			return null ;
		return cmdDef = c.getCmdById(this.cmd_id) ;
	}
	
	public ZCLParam getCmdPmDef()
	{
		if(this.cmd_pm_idx<0)
			return null ;
		if(this.cmdPmDef!=null)
			return this.cmdPmDef ;
		ZCLCmd cmddef = getCmdDef() ;
		if(cmddef==null)
			return null ;
		return this.cmdPmDef = cmddef.getParamByIdx(this.cmd_pm_idx) ;
	}
	
	public static ZSAddr parseAddr(String str,StringBuilder failedr)
	{
		List<String> ss = Convert.splitStrWith(str, ";") ;
		if(ss==null||ss.size()<3)
		{
			failedr.append("invalid addr - "+str) ;
			return null ;
		}
		ZSAddr addr = new ZSAddr() ;
		for(String s:ss)
		{
			int k = s.indexOf(':') ;
			if(k<=0)
				continue ;
			String pn = s.substring(0,k) ;
			String pv = s.substring(k+1) ;
			if(Convert.isNullOrEmpty(pv))
				continue ;
			try
			{
				int v = Integer.parseInt(pv) ;
				switch(pn)
				{
				case "ep":
					addr.ep_id = v ;
					break ;
				case "cluster":
					addr.cluster_id = v ;
					break ;
				case "attr":
					addr.attr_id = v ;
					break ;
				case "cmd":
					addr.cmd_id = v ;
					break ;
				case "pm":
					addr.cmd_pm_idx = v ;
					break ;
				}
			}
			catch(Exception ee)
			{
				failedr.append("invalid addr - "+str+" "+ee.getMessage()) ;
			}
		}
		
		if(!addr.isValid())
		{
			failedr.append("invalid addr - "+str) ;
			return null ;
		}
		addr.addr=addr.getAddrStr() ;
		return addr;
	}
	
	@Override
	public DevAddr parseAddr(UADev dev, String str, ValTP vtp, StringBuilder failedr)
	{
		return parseAddr(str, failedr) ;
	}
	
	private transient String _addrStr = null ;
	
	public String getAddrStr()
	{
		if(_addrStr!=null)
			return _addrStr;
		
		String r = "ep:"+this.ep_id+";cluster:"+this.cluster_id;
		
		if(cmd_id>=0)
		{
			if(this.cmd_pm_idx<0)
				return _addrStr = r+";cmd:"+this.cmd_id ;
			else
				return _addrStr = r+";cmd:"+this.cmd_id+";pm:"+this.cmd_pm_idx ;
		}
		
		if(attr_id>=0)
			return _addrStr = r+";attr:"+this.attr_id ;
		
		return _addrStr = r;
	}
	
	@Override
	public String toString()
	{
		return getAddrStr() ;
	}
	
	
	@Override
	public boolean isSupportGuessAddr()
	{
		return false;
	}

	@Override
	public DevAddr guessAddr(UADev dev, String str, ValTP vtp)
	{
		return null;
	}

	@Override
	public List<String> listAddrHelpers()
	{
		return null;
	}
	
	private static ValTP[] VTP_BOOL = new ValTP[] {ValTP.vt_bool} ;

	@Override
	public ValTP[] getSupportValTPs()
	{
		if(this.isAddrAttr())
		{
			ZCLAttr attr = getAttrDef();
			if(attr==null)
				return null ;
			UAVal.ValTP vtp = attr.getUAValTP() ;
			if(vtp==null)
				return null ;
			return new ValTP[] {vtp};
		}
		if(this.isAddrCmd())
		{
			return VTP_BOOL;
		}
		if(this.isAddrCmdPm())
		{
			ZCLParam pmdef = this.getCmdPmDef() ;
			if(pmdef==null)
				return null ;
			UAVal.ValTP vtp = pmdef.getUAValTP() ;
			if(vtp==null)
				return null ;
			return new ValTP[] {vtp};
		}
		return null ;
	}

	@Override
	public boolean canRead()
	{
		return false;
	}

	@Override
	public boolean canWrite()
	{
		if(this.isAddrCmd() || this.isAddrCmdPm())
			return true ;
		ZCLAttr attr = getAttrDef();
		if(attr==null)
			return false ;
		return attr.isWrite();
	}

	@Override
	public String toCheckAdjStr()
	{
		return null;
	}

}
