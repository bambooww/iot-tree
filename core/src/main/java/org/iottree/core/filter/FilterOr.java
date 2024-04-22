package org.iottree.core.filter;

import java.util.ArrayList;
import java.util.List;


public class FilterOr<T> extends Filter<T>
{
	ArrayList<Filter<T>> innFilters = new ArrayList<>() ;
	
	public FilterOr(List<Filter<T>> innfs)
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
			if(f.accept(ob))
				return true;
		}
		return false;
	}

	@Override
	public String getTP()
	{
		return "or";
	}
}
