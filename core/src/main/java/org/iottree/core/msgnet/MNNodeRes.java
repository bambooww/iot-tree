package org.iottree.core.msgnet;

import org.json.JSONObject;

/**
 * Resource Node
 * 
 * 资源型节点，只为前置节点提供对应的资源支持，如DBConn DBTable etc
 * 
 * @author jason.zhu
 *
 */
public  abstract class MNNodeRes extends MNNodeEnd
{

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		return null;
	}

	
	public JSONObject toListJO()
	{
		JSONObject jo = super.toListJO() ;
		jo.put("node_res",true) ;
		return jo ;
	}
}
