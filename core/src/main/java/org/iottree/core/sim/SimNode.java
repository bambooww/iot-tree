package org.iottree.core.sim;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UANode;
import org.iottree.core.basic.JSObMap;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;

@data_class
public class SimNode extends JSObMap
{
	@data_val
	String id = null ;
	
	@data_val
	String name = null ;
	
	@data_val
	String title = null ;
	
	public SimNode()
	{
		this.id = CompressUUID.createNewId() ;
	}
	
	public Object JS_get(String  key)
	{
		switch(key)
		{
		case "_id":
			return this.id ;
		case "_name":
			return this.name ;
		case "_title":
			return this.title ;
		}
		//return this.getSubNodeByName(key) ;
		return null ;
	}
	
	public List<JsProp> JS_props()
	{
		List<JsProp> ss = super.JS_props() ;
		ss.add(new JsProp("_id",String.class,"Id","Sim Node Unique Id")) ;
		ss.add(new JsProp("_name",String.class,"Id","Sim Node Name")) ;
		ss.add(new JsProp("_title",String.class,"Id","Sim Node Title")) ;
//		List<UANode> subns = this.getSubNodes() ;
//		if(subns!=null)
//		{
//			for(UANode n:subns)
//				ss.add(n.getName()) ;
//		}
		return ss ;
	}
	

	public String getId()
	{
		return this.id ;
	}
	
	public SimNode withId(String id)
	{
		this.id = id ;
		return this ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public SimNode withName(String n) throws Exception
	{
		StringBuilder chkf = new StringBuilder() ;
		if(!Convert.checkVarName(n, true, chkf))
			throw new Exception(chkf.toString()) ;
		
		this.name = n ;
		return this ;
	}
	
	public String getTitle()
	{
		if(Convert.isNotNullEmpty(this.title))
			return this.title ;
		return this.name ;
	}
	
	public SimNode withTitle(String t)
	{
		this.title = t ;
		return this ;
	}
}
