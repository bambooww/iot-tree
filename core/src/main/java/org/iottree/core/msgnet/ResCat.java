package org.iottree.core.msgnet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;

public class ResCat implements ILang
{
	private String name ;
	
	private String title = null;
	
	LinkedHashMap<String,ResCaller> name2caller = new LinkedHashMap<>() ;
	
	public ResCat(String name)
	{
		this.name = name ;
	}
	
	public ResCat(String name,String title)
	{
		this.name = name ;
		this.title = title ;
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
	
	public ResCaller getCallerByName(String name)
	{
		return this.name2caller.get(name) ;
	}
	
	public List<ResCaller> listCallers()
	{
		ArrayList<ResCaller> rets = new ArrayList<>(this.name2caller.size()) ;
		rets.addAll(name2caller.values()) ;
		return rets ;
	}
}
