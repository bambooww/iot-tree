package org.iottree.core.bind;

import java.util.List;

/**
 * a drawitem which has multi PropBindItems
 *  
 * @author jason.zhu
 *
 */
public class BindDI
{
	/**
	 * drawitem id
	 */
	String id = null ;
	
	List<PropBindItem> propBindItems = null ;
	
	List<EventBindItem> evtBindItems  = null ;
	
	public BindDI()
	{}
	
	public BindDI(String id,List<PropBindItem> pbis,List<EventBindItem> evtbds)
	{
		this.id = id ;
		this.propBindItems = pbis ;
		this.evtBindItems = evtbds ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public List<PropBindItem> getPropBindItems()
	{
		return propBindItems;
	}
	
	public List<EventBindItem> getEventBindItems()
	{
		return this.evtBindItems ;
	}
	
	public EventBindItem getEventBindItem(String eventn)
	{
		if(evtBindItems==null)
			return null ;
		for(EventBindItem item:evtBindItems)
		{
			if(item.getEventName().equals(eventn))
				return item ;
		}
		return null ;
	}
}
