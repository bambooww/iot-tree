package org.iottree.core.store.record;

import java.util.List;

import org.json.JSONObject;

/**
 * 配合Pro进行数据存储或读取的支持
 * 
 * 可以认为，每个Saver对应一个数据源中的一个或多个表
 * 
 * 可以支持多个标签
 * 
 * @author jason.zhu
 *
 */
public abstract class RecSaver
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
	
	
	
	public abstract boolean RT_init(StringBuilder failedr) ;
	
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
