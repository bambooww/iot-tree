package org.iottree.core.store.record;

import java.util.List;

import org.json.JSONObject;

/**
 * 依附于Rec Saver
 * 
 * @author jason.zhu
 *
 */
public abstract class RecShower
{
	String name ;
	
	String title ; 
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public abstract String getTp();
	
	public abstract boolean canShowMultiTags() ;
	
	/**
	 * 
	 * @param <T>
	 * @return
	 */
	public abstract <T extends RecSaver> List<Class<T>> supportedSaver() ; 
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("n", this.name) ;
		jo.put("t", this.title) ;
		jo.put("tp", this.getTp()) ;
		return jo ;
	}
	
	public static RecSaver fromJO(JSONObject jo)
	{
		String tp = jo.getString("tp") ;
		switch(tp)
		{
		
		}
		
		return null ;
	}
}
