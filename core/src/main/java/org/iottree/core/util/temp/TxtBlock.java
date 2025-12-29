package org.iottree.core.util.temp;

import java.util.*;

/**
 *
 * @author Jason Zhu
 */
public class TxtBlock
{
	String name = null ;
	HashMap<String,String> props = new HashMap<String,String>() ;
	
	public TxtBlock(String n)
	{
		int p = n.indexOf(' ');
		if(p>0)
		{
			name = n.substring(0,p);
			String tmps = n.substring(p+1).trim();
			
			p = 0 ;
			while(tmps!=null)
			{
				int i = tmps.indexOf('=',p);
				if(i<0)
				{
					props.put(tmps,"");
					return ;
				}
				
				String pn = tmps.substring(p,i).trim();
				tmps = tmps.substring(i+1).trim() ;
				char c = tmps.charAt(0);
				if(c!='\''&&c!='\"')
					throw new RuntimeException("page block must like [#xx xx=\"xx\"#]");
				
				int j = tmps.indexOf(c,1);
				if(j<=0)
					throw new RuntimeException("page block must like [#xx xx=\"xx\"#]");
				
				String pv = tmps.substring(1,j);
				props.put(pn, pv);
				tmps = tmps.substring(j+1).trim() ;
				if(tmps.equals(""))
					break;
			}
		}
		else
		{
			name = n ;
			//props = new HashMap<String,String>() ;
		}
	}
	
	TxtBlock(String n,HashMap<String,String> p)
	{
		name = n ;
		props = p;
		if(props==null)
			props = new HashMap<String,String>() ;
	}
	
	public String getBlockName()
	{
		return name ;
	}
	
	public HashMap<String,String> getProps()
	{
		return props ;
	}
	
	public String getPropVal(String pn)
	{
		return props.get(pn) ;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[#").append(name);
		for(Map.Entry<String, String> kv:props.entrySet())
		{
			sb.append(" ").append(kv.getKey()).append("=\"").append(kv.getValue()).append("\"");
		}
		sb.append("#]");
		return sb.toString();
	}
}
