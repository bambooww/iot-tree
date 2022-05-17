package org.iottree.core;

import org.iottree.core.util.Convert;

/**
 * if a UADev is device set. then it will represent multi devices related it
 * every device name UADevSetItem.
 * 
 * @author jason.zhu
 *
 */
public class UADevSetItem
{
	String id = null ;
	
	//String name = null ;
	
	
	
	String title = null ;
	
	private transient UADev devSet = null ;
	
	public UADevSetItem(UADev devset,String id)
	{
		if(devset==null||!devset.isDevSet())
			throw new IllegalArgumentException("UADev must has be set") ;
		
		if(!checkId(id))
			throw new IllegalArgumentException("id must has char in a-z A-Z _ 0-9") ;
		
	}
	
	
	private static boolean checkId(String n)
	{
		int s = n.length();
		for (int i = 1; i < s; i++)
		{
			char c = n.charAt(i);
			boolean bc = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_';
			if (!bc)
			{
				return false;
			}
		}
		return true ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public String getName()
	{
		return devSet.getName()+"_"+this.id;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	
}
