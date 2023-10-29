package org.iottree.core.cxt;

public class JsSubOb
{
	JsSub sub = null ;
	
	Object subVal = null ;
	
	public JsSubOb(JsSub jss,Object val)
	{
		this.sub = jss ;
		this.subVal = val ;
	}
	
	public JsSub getJsSub()
	{
		return this.sub ;
	}
	
	public Object getSubVal()
	{
		return this.subVal ;
	}
}
