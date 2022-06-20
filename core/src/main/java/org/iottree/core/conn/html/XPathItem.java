package org.iottree.core.conn.html;

public class XPathItem
{
	String name = null ;
	
	int idx =  1 ;
	
	public String getName()
	{
		return name ;
	}
	
	public int getIdx()
	{
		return idx ;
	}
	
	public static XPathItem parseStr(String str,StringBuilder failedr)
	{
		if(str.endsWith("]"))
		{
			int k = str.lastIndexOf("[") ;
			if(k<=0)
			{
				failedr.append("invalid xpath item :"+str) ;
				return null ;
			}
			
			XPathItem r = new XPathItem() ;
			r.name = str.substring(0,k) ;
			r.idx = Integer.parseInt(str.substring(k+1,str.length()-1)) ;
			return r;
		}
		
		XPathItem r = new XPathItem() ;
		r.name=  str ;
		return r ;
	}
}
