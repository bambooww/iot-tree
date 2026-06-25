package org.iottree.core.devtree;

import org.iottree.core.util.CompressUUID;

/**
 * 
 * @author jason.zhu
 *
 */
public class DTDevPart
{
	String id ; //unique id
	
	String title ;// device part title
	
	String desc ;
	
	String code ;
	
	DTDevPart()
	{
		
	}
	
	/**
	 * create new
	 * @param t
	 * @param d
	 */
	DTDevPart(String t,String d)
	{
		this.id = CompressUUID.createNewId() ;
		this.title = t ;
		this.desc = d ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public String getDesc()
	{
		return this.desc ;
	}
}
