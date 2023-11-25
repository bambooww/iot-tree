package org.iottree.core.store.gdb;

import java.util.*;

public class DataSet
{
	ArrayList<DataTable> tables = new ArrayList<DataTable>() ;
	
	public DataSet()
	{}
	
	public int getTableNum()
	{
		return tables.size() ;
	}
	
	public DataTable getTable(int idx)
	{
		if(idx<0||idx>=tables.size())
			return null ;
		
		return tables.get(idx);
	}
	
	public DataTable getTable(String tn)
	{
		for(DataTable dt:tables)
		{
			if(dt.getTableName().equalsIgnoreCase(tn))
				return dt;
		}
		
		return null ;
	}
	
	public void addTable(DataTable dt)
	{
		tables.add(dt);
	}
	
	public void clear()
	{
		tables.clear();
	}
}
