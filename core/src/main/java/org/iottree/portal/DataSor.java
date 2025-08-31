package org.iottree.portal;

import java.util.LinkedHashMap;

/**
 * 数据源——代表了一个数据集合。如果
 * 
 * @author zzj
 *
 */
public abstract class DataSor
{
	
	/**
	 * 
	 */
	LinkedHashMap<String,DataTap> name2tap = new LinkedHashMap<>() ;
	
	
	
	public DataSor()
	{
	}

}
