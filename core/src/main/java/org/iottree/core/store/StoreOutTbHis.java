package org.iottree.core.store;

import org.iottree.core.util.xmldata.data_val;

public class StoreOutTbHis extends StoreOut
{
	public static final String TP = "r_tb_his" ;
	
	@data_val(param_name = "sor_n")
	String sorName = null ;
	
	@data_val(param_name = "table")
	String tableName = null ;
	
	@Override
	public String getOutTp()
	{
		return TP;
	}

	@Override
	public String getOutTpTitle()
	{
		return "History Data Table";
	}

	public String getSorName()
	{
		return this.sorName ;
	}
	
	public String getTableName()
	{
		return this.tableName ;
	}
	
	public boolean checkValid(StringBuilder failedr)
	{
		return true;
	}
	
	public boolean initOut(StringBuilder failedr)
	{
		return true ;
	}
	
	@Override
	protected boolean RT_init(StringBuilder failedr)
	{
		return true;
	}
	
	@Override
	protected void RT_runInLoop()
	{
		
	}
}
