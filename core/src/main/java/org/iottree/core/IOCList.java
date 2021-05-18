package org.iottree.core;

import java.util.List;

import org.json.*;

public interface IOCList extends IOCBox
{
	/**
	 * export type ListHead = {n:string,t:string}[]
	 * @return
	 */
	public JSONArray OCList_getListHead() ;
	
	public List<Object> OCList_getItems();
}
