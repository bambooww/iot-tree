package org.iottree.core.basic;

import java.util.*;

import org.iottree.core.dict.DataNode;
import org.iottree.core.util.Lan;
import org.iottree.core.util.xmldata.*;

/**
 * a prop group contanier one or more PropItem
 * it can be used for configuration.
 * 
 * 1)for every node ,it has prop group
 * 2)for related node ,some prop group may came from others
 * @author jason.zhu
 */
@data_class
public class PropGroup
{
	@data_val
	String name = null ;
	
//	@data_val
//	String title = null ;
	
	@data_obj(obj_c=PropItem.class)
	List<PropItem> props = new ArrayList<>() ;
	
	DataNode lanDN = null ;
	
//	public PropGroup()
//	{}
	
	public PropGroup(String n,Lan prop_lan) //String t)
	{
		this.name = n ;
//		this.title = t ;
		//propLan = prop_lan ;
		lanDN = prop_lan.gn("pg_"+n) ;
		if(lanDN==null)
			throw new IllegalArgumentException("no pg_"+n+" found in prop_lang") ;
	}
	
	//private transient 
	public void addPropItem(PropItem pi)
	{
		props.add(pi) ;
	}
	
	public List<PropItem> getPropItems()
	{
		return props ;
	}
	
	public PropItem getPropItem(String name)
	{
		if(props==null)
			return null ;
		for(PropItem pi:props)
		{
			if(pi.getName().contentEquals(name))
				return pi ;
		}
		return null ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		return lanDN.getNameByLang(Lan.getUsingLang());//.g(this.name) ;
		//return title ;
	}
}
