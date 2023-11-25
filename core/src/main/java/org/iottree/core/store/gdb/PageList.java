package org.iottree.core.store.gdb;

import java.util.List;

public class PageList<T>
{
	List<T> listObjs = null ;
	
	int totalCount = -1 ;
	
	int pageIdx = -1 ;
	
	int pageSize = -1 ;
	
	public PageList(List<T> objs,int pageidx,int pagesize,int totalc)
	{
		this.listObjs = objs;
		this.pageIdx = pageidx ;
		this.pageSize = pagesize ;
		this.totalCount = totalc ;
	}
	
	public List<T> getListObjs()
	{
		return this.listObjs ;
	}
	
	public int getPageIdx()
	{
		return this.pageIdx ;
	}
	
	public int getPageSize()
	{
		return this.pageSize ;
	}
	
	public int getTotalCount()
	{
		return this.totalCount ;
	}
}
