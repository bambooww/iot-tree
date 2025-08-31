package org.iottree.portal;

import org.iottree.core.UAVal;

/**
 * 数据项定义
 * @author zzj
 *
 */
public class DataItem
{
	String name ;
	
	String title ;
	
	UAVal.ValTP valTP = UAVal.ValTP.vt_str ;
	
	
	public DataItem()
	{
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public UAVal.ValTP getValTP()
	{
		return this.valTP ;
	}

}
