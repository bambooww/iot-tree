package org.iottree.core.devtree;

import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class DTNode
{
	String nodeId ;
	
	String title ;
	
	String desc ;
	
	DTNode parent ;
	/**
	 * for load
	 */
	DTNode(DTNode parent)
	{
		this.parent = parent ;
	}
	
	/**
	 * for create new
	 * @param title
	 * @param desc
	 */
	public DTNode(DTNode parent,String title,String desc)
	{
		this.parent = parent ;
		this.nodeId = this.getNodeTp()+this.getTree().getNextId() ;
		if(Convert.isNullOrEmpty(title))
			title = "noname" ;
		this.title = title ;
		this.desc = desc ;
	}
	
	public String getNodeId()
	{
		return this.nodeId ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public void setTitle(String t)
	{
		this.title = t ;
	}
	
	public String getDesc()
	{
		return this.desc ;
	}
	
	public String getPathTitle()
	{
		DTNode pn = this.getParent() ;
		String t = this.getTitle() ;
		if(Convert.isNullOrEmpty(t))
			t = "" ;
		
		if(pn==null)
			return "/"+t ;

		return pn.getPathTitle()+"/"+t ;
	}
	
	public DTNode asBasic(String title,String desc)
	{
		this.title = title ;
		this.desc = desc ;
		return this ;
	}
	
	public DTNode getParent()
	{
		return this.parent ;
	}
	
	public DTTree getTree()
	{
		if(this.parent==null)
		{
			if(this instanceof DTTree)
				return (DTTree)this ;
			return null ;
		}
		
		return this.parent.getTree() ;
	}
	
	void clearCache()
	{
		DTTree t = this.getTree() ;
		if(t==null)
			return ;
		t.clearCache(); 
	}
	
	protected abstract String getNodeTp() ; 
	
	//public abstract List<DTNode> getChildNodes() ;
	
	//public abstract DTNode getChildNodeById(String id) ;
	
	//abstract DTNode removeChild(String nodeid);
	
	abstract public JSONObject renderToTree(DTTreeRenderCtrl tr_ctrl) ; 
	
	protected String getTreeNodeCss()
	{
		return "" ;
	}
	
	protected String getTreeNodeTips()
	{
		return "" ;
	}
	
	public String getNodeIcon()
	{
		return "<i class=\"fa-solid fa-layer-group\"></i>" ;
	}
	
	public JSONObject toJO(boolean b_show_detail)
	{
		JSONObject ret = new JSONObject().put("id", this.nodeId)
				.put("_tp", this.getNodeTp())
				.putOpt("t", this.title).putOpt("d",this.desc) ;
		if(b_show_detail)
			ret.putOpt("color", "#fef4ec").putOpt("icon", this.getNodeIcon()) ;
		return ret ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.nodeId = jo.optString("id") ;
		if(Convert.isNullOrEmpty(this.nodeId))
			return false;
		this.title = jo.optString("t") ;
		this.desc = jo.optString("d") ;
		return true ;
	}
}
