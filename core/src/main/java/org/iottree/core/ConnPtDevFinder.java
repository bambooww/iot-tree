package org.iottree.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.conn.ConnPtBinder;

public abstract class ConnPtDevFinder extends ConnPtBinder implements IConnPtDevFinder
{
	public abstract LinkedHashMap<String,ConnDev> getFoundConnDevs() ;
	
	public final boolean addFoundConnDevToCh(ConnDev cd,StringBuilder failedr) throws Exception
	{
		IJoinedNode jn = this.getJoinedNode();
		if(jn==null || !(jn instanceof UACh))
		{
			failedr.append("no joined channel") ;
			return false;
		}
			
		UACh ch=  (UACh)jn ;//.getJoinedCh() ;
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
	
}
