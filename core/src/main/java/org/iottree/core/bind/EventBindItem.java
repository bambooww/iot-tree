package org.iottree.core.bind;

import org.iottree.core.UAHmi;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UAVal;
import org.iottree.core.cxt.UACodeItem;
import org.iottree.core.cxt.UAContext;

public class EventBindItem
{
	UAHmi hmi ;
	
	String eventName = null ;
	
	String serverJS = null ;
	
	String runName = null ;
	
	transient UACodeItem code = null ;
	
//	public EventBindItem()
//	{}
	
	public EventBindItem(UAHmi hmi ,String eventn,String serverjs,String runname)
	{
		this.hmi = hmi;
		
		this.eventName = eventn ;
		this.serverJS = serverjs ;
		this.runName = runname ;
	}
	
	public String getEventName()
	{
		return eventName ;
	}
	
	public String getServerJS()
	{
		return serverJS ;
	}
	
	public String getRunName()
	{
		return this.runName ;
	}
	
	public boolean RT_runEventJS(UANodeOCTagsCxt tagn,Object val,StringBuilder failedr)
	{
		UAContext cxt = tagn.RT_getContext() ;
		if(cxt==null)
		{
			failedr.append("no UAContext") ;
			return false ;
		}
		
		if(this.code==null)
		{
			this.code = new UACodeItem("", "{"+this.serverJS+"\r\n}") ;
			this.code.initItem(cxt) ;
		}
		
		if(!code.isValid())
		{
			failedr.append("code is invalid") ;
			return false ;
		}
		
		try
		{
			code.runCodeFunc(val) ;
			return true ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			failedr.append("run code func err:"+e.getMessage()) ;
			return false;
		}
	}
}
