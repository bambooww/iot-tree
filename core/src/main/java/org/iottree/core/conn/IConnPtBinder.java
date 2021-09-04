package org.iottree.core.conn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface IConnPtBinder
{
	public HashMap<String,String> bindParams = new HashMap<>() ;
	
	public ArrayList<String> bindList = new ArrayList<>() ;
	/**
	 * overrider may use param to setup some special param when connections
	 * this param may send to some special client like agent
	 * @return
	 */
	public default HashMap<String,String> getBindParam()
	{
		return bindParams ;
	}
	
	public default void setBindParam(HashMap<String,String> pms)
	{
		bindParams.putAll(pms) ;
	}
	
	public default void setBindList(List<String> bindids)
	{
		bindList.addAll(bindids);
	}
	
	public default List<String> getBindList()
	{
		return bindList;
	}
}
