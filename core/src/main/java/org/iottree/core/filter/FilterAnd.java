package org.iottree.core.filter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class FilterAnd<T> extends Filter<T>
{
	ArrayList<Filter<T>> innFilters = new ArrayList<>() ;
	
	public FilterAnd(List<Filter<T>> innfs)
	{
		if(innfs==null||innfs.size()<=0)
			throw new IllegalArgumentException("no inner filter input") ;
		this.innFilters.addAll(innfs) ;
	}
	
	@Override
	public boolean accept(T ob)
	{
		for(Filter<T> f:this.innFilters)
		{
			if(!f.accept(ob))
				return false;
		}
		return true;
	}
	
	@Override
	public String getTP()
	{
		return "and";
	}

	@Override
	public JSONObject toDefJO()
	{
		JSONObject jo = super.toDefJO() ;
		JSONArray jarr = new JSONArray() ;
		for(Filter<T> innt :this.innFilters)
		{
			jarr.put(innt.toDefJO()) ;
		}
		return jo;
	}
	
	
}
