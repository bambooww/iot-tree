package org.iottree.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ConnPtDevFinder extends ConnPt
{
	public abstract LinkedHashMap<String,ConnDev> getFoundConnDevs() ;
	
	public final boolean addFoundConnDevToCh(ConnDev cd,StringBuilder failedr) throws Exception
	{
		UACh ch= this.getJoinedCh() ;
		if(ch==null)
		{
			failedr.append("no joined channel") ;
			return false;
		}
		boolean b = addConnDevToCh(ch,cd, failedr) ;
		if(b)
		{
			LinkedHashMap<String,ConnDev> n2d = getFoundConnDevs() ;
			if(n2d!=null)
				n2d.remove(cd.getName()) ;
		}
		return b ;
	}
	
	private transient ConnMsg newDevMsg = null;//new ConnMsg() ;
	private transient List<ConnMsg> newDevMsgs = null;
	
	@Override
	public List<ConnMsg> getConnMsgs()
	{
		LinkedHashMap<String,ConnDev> n2d = getFoundConnDevs() ;
		if(n2d==null||n2d.size()<=0)
			return null ;
		
		if(newDevMsg==null)
		{
			ConnProvider cp = this.getConnProvider() ;
			UAPrj prj = cp.getBelongTo() ;
			newDevMsg = new ConnMsg().asTitle("New Device Found")
					.asIconColor("green").asIcon("fa-solid fa-calendar-plus fa-lg fa-beat-fade")
					.asDlg("conn/cpt_new_devs.jsp?prjid="+prj.getId()+"&cpid="+cp.getId()+"&cid="+this.getId(), "New Devices Found");
			
			newDevMsgs = Arrays.asList(newDevMsg) ;
		}
		String desc = "" ;
		for(ConnDev cd:n2d.values())
			desc += cd.getTitle()+"["+cd.getName()+"]\r\n" ;
		newDevMsg.asDesc(desc);
		
		return newDevMsgs;
	}
	
	private static boolean addConnDevToCh(UACh ch,ConnDev cd,StringBuilder failedr) throws Exception
	{
		UADev dev = ch.getDevByName(cd.getName()) ;
		if(dev!=null)
		{
			failedr.append("device is existed") ;
			return false;
		}
		
		UADev ndev = ch.addDev(cd.getName(), cd.getTitle(), "", null) ;
		
		for(ConnDev.Data d:cd.getDatas())
		{
			String vtstr = d.getValTp() ;
			UAVal.ValTP vt = UAVal.getValTp(vtstr) ;
			if(vt==null)
				continue ;
			UATag nt = ndev.addTagWithGroupByPath(d.getPath(),vt,false) ;
		}
		ndev.save();
		return true ;
	}
}
