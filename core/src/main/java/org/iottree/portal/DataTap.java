package org.iottree.portal;

import java.util.List;

import org.json.JSONObject;

/**
 * 数据抽头
 * @author zzj
 */
public abstract class DataTap
{

	public DataTap()
	{
	}

	
	public abstract JSONObject RT_getData();
	
	public abstract List<DataItem> getDataItems() ;
	
}
