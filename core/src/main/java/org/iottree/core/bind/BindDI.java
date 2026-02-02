package org.iottree.core.bind;

import java.util.List;

import org.iottree.core.UAHmi;

/**
 * a drawitem which has multi PropBindItems
 *  
 * @author jason.zhu
 *
 */
public class BindDI
{
	UAHmi hmi ;
	/**
	 * drawitem id
	 */
	String id = null ;
	
	List<PropBindItem> propBindItems = null ;
	
	List<EventBindItem> evtBindItems  = null ;
	
//	public BindDI()
//	{}
	
	public BindDI(UAHmi hmi,String id,List<PropBindItem> pbis,List<EventBindItem> evtbds)
	{
		this.hmi = hmi ;
		this.id = id ;
		this.propBindItems = pbis ;
		this.evtBindItems = evtbds ;
	}
	
	public UAHmi getHmi()
	{
		return this.hmi;
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
