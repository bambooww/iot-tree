package org.iottree.core.comp;

import org.iottree.core.dict.DataNode;

/**
 * div component item,related js [IDIDivComp]
 * 
 * 
 * @author jason.zhu
 *
 */
public class DivCompItem
{
	String name = null ;
	
	DataNode dn = null ;
	
	DivCompCat belongTo = null ;
	
	public DivCompItem(DivCompCat cat,String n,DataNode dn)
	{
		belongTo = cat ;
		this.name = n ;
		this.dn = dn ;
	}
	
	public String getUniqueId()
	{
		return belongTo.getName()+"-"+this.name ;
	}
	
	public String getName()
	{
		return name ;
	}

	public String getTitle()
	{
		return this.dn.getNameBySysLan(this.name) ;
	}
	
	
}
