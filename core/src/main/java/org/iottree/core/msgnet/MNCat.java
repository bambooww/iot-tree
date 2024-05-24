package org.iottree.core.msgnet;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAServer;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;

public class MNCat implements ILang
{
	/**
	 * name or appname
	 */
	private String name ;
	
	private String title = null;
	
	ArrayList<MNNode> nodes = new ArrayList<>() ;
	
	ArrayList<MNModule> modules = new ArrayList<>() ;
	
	UAServer.WebItem webItem = null ;
	
	public MNCat(String name)
	{
		this.name = name ;
	}
	
	public MNCat(String name,String title)
	{
		this.name = name ;
		this.title = title ;
	}
	
	public MNCat asWebItem(UAServer.WebItem wi)
	{
		this.webItem = wi ;
		return this ;
	}
	
	UAServer.WebItem getWebItem()
	{
		return this.webItem ;
	}
	
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		String t = Convert.isNotNullEmpty(this.title)?this.title:this.name ;
		return g_def(this.name,t) ;
	}
	
	public List<MNNode> getNodes()
	{
		return nodes ;
	}
	
	public MNNode getNodeByTP(String tp)
	{
		for(MNNode n:this.nodes)
		{
			if(tp.equals(n.getTP()))
				return n ;
		}
		return null ;
	}
	
	public List<MNModule> getModules()
	{
		return modules;
	}
	
	public MNModule getModuleByTP(String tp)
	{
		for(MNModule m:this.modules)
		{
			if(tp.equals(m.getTP()))
				return m ;
		}
		return null ;
	}
}
