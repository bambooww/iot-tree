package org.iottree.core.devtree;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class DTNodeRoot extends DTNode
{

	int curId = 0 ;
	
	public DTNodeRoot()
	{
		super(null) ;
	}
	
	DTNodeRoot(String title,String desc)
	{
		super(null,title,desc) ;
	}
	
//	DTNodeRoot(DTNode parent, DTNode oth, boolean ignore_runtag, boolean b_newid, boolean b_deep)
//	{
//		super(parent, oth, ignore_runtag, b_newid, b_deep);
//	}
	
	public abstract String getRootId() ;
	
	@Override
	public final String getNodeId()
	{
		return this.getRootId();
	}
	
	String getNextId(String node_tp)
	{
		return node_tp+ this.getNextIdNum();
	}
	

	private int getNextIdNum()
	{
		curId ++ ;
		return curId ;
	}
	
	@Override
	public JSONObject toJO(boolean b_show_detail)
	{
		JSONObject ret = super.toJO(b_show_detail);
		
		ret.put("__curid", curId) ;
		
		return ret;
	}
	
	@Override
	public boolean fromJO(JSONObject jo)
	{
		if(!super.fromJO(jo))
			return false;
		
		this.curId = jo.optInt("__curid", 0) ;
		
		return true ;
	}
}
