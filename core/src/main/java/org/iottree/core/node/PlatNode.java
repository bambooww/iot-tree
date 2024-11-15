package org.iottree.core.node;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * local node config inf,node id and title is assigned by platform
 * @author jason.zhu
 *
 */
public class PlatNode
{
//	String nodeId ;
//	
//	String nodeTitle ;
	
	boolean openForPlat = true ;
	
	ArrayList<String> openPrjNames = null;
	
	ArrayList<String> limitPlatIPs = null ;
	
	private transient JSONObject relatedJO = null ;
	
	private PlatNode()
	{
		//this.nodeId = nodeid ;
	}
	
//	public String getId()
//	{
//		return this.nodeId ;
//	}
//	
//	public String getTitle()
//	{
//		return this.nodeTitle ;
//	}
	
	public boolean isOpenForPlat()
	{
		return this.openForPlat ;
	}
	
	public List<String> getOpenPrjNames()
	{
		return this.openPrjNames ;
	}
	
	public void setOpenPrjNames(ArrayList<String> ss)
	{
		this.openPrjNames = ss ;
	}
	
	public List<String> getLimitPlatIPs()
	{
		return this.limitPlatIPs ;
	}
	
	public JSONObject toJO()
	{
		return relatedJO;
	}
	
	public static PlatNode fromJO(JSONObject jo)
	{
		//String node_id = jo.optString("node_id") ;
		//if(Convert.isNullOrEmpty(node_id))
		//	return null ;
		PlatNode pn = new PlatNode() ;
		pn.relatedJO = jo ;
		//pn.nodeTitle = jo.optString("node_tt") ;
		pn.openForPlat = jo.optBoolean("open_for_plat", true) ;
		pn.openPrjNames = Convert.optJOStrList(jo, "open_prjs") ;
		pn.limitPlatIPs = Convert.optJOStrList(jo, "limit_plat_ip") ;
		return pn ;
	}
}
