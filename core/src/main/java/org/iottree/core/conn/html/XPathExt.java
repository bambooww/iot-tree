package org.iottree.core.conn.html;

import java.util.List;


public class XPathExt
{

}

class XPathAttr extends XPathExt
{
	String attrName= null ;
	
	public XPathAttr(String pn)
	{
		this.attrName = pn ;
	}
	
	public String getAttrName()
	{
		return attrName ;
	}
}

