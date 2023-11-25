package org.iottree.core.store.gdb;

public class DataOut
{
	int totalCount = -1 ;
	int pageSize = 20 ;
	//int pageNum = -1 ;
	int pageCur = -1 ;
	
	public int getTotalCount()
	{
		return totalCount ;
	}
	
	public int getPageSize()
	{
		return pageSize ;
	}
	
	public int getPageCur()
	{
		return pageCur ;
	}
	
	public int getPageNum()
	{
		if(totalCount<=0)
			return 0 ;
		
		return totalCount / pageSize + (totalCount%pageSize)>0?1:0 ;
	}
}
