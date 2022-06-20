package org.iottree.core.conn.html;

import java.util.List;

public class XPathFunc extends XPathExt
{
	String funcName =null ;
	
	List<String> funcParams = null ;
	
	private transient XPFunc xpF = null ;
	
	public XPathFunc(String fn,List<String> params)
	{
		this.funcName = fn ;
		this.funcParams = params ;
	}
	
	public String getFuncName()
	{
		return funcName ;
	}
	
	public List<String> getFuncParams()
	{
		return funcParams ;
	}
	
	public boolean init()
	{
		switch(funcName)
		{
		case "substr":
		case "substring":
			xpF = new XPFuncSubstr() ;
			break ;
		case "seg":
			xpF = new XPFuncSeg() ;
			break ;
		case "split":
		}
		
		if(xpF==null)
			return false;
		if(!xpF.setupParams(funcParams))
			return false;
		return true;
	}
	
	public String runFunc(String input)
	{
		if(xpF==null)
		{
			init();
			if(xpF==null)
				throw new RuntimeException("func is not inited") ;
		}
		return xpF.runFunc(input) ;
	}
}


