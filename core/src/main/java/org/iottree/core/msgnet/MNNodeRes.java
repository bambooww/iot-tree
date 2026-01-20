package org.iottree.core.msgnet;

import org.json.JSONObject;

import java.util.List;

import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.util.*;

/**
 * Resource Node
 * 
 * 资源型节点，只为前置节点提供对应的资源支持，如DBConn DBTable etc
 * 
 * @author jason.zhu
 *
 */
public  abstract class MNNodeRes extends MNNodeMid
{
	
	@Override
	protected final RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		return null;
	}

	
	@Override
	public int getOutNum()
	{
		return 0 ;
	}
	
	
//	@Override
//	public JSONObject toJO()
//	{
//		JSONObject jo = super.toJO();
//		jo.putOpt("caller_uid", this.callerUID) ;
//		return jo ;
//	}
//	
//	@Override
//	public boolean fromJO(JSONObject jo)
//	{
//		boolean b =  super.fromJO(jo);
//		if(!b) return false;
//		this.callerUID = jo.optString("caller_uid") ;
//		return true ;
//	}
//	
//	@Override
//	protected boolean fromJOBasic(JSONObject jo, StringBuilder failedr)
//	{
//		boolean b = super.fromJOBasic(jo, failedr);
//		if(!b) return false;
//		this.callerUID = jo.optString("caller_uid") ;
//		return true ;
//	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = super.toListJO() ;
		jo.put("node_res",true) ;
		return jo ;
	}
	
	
	
}
