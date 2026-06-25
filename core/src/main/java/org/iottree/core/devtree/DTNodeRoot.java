package org.iottree.core.devtree;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * based on DTTree, use nodes that is already existed
 * @author jason.zhu
 *
 */
public class DTNodeRoot
{
	String rootId ;
	float x = 0 ;
	float y = 0 ;
	
	/**
	 * node grp or part ids
	 */
	ArrayList<String> subNodeIds = new ArrayList<>() ;
	
	DTTree owner ;
	
	private transient ArrayList<DTNode> subNodes = null ; 
	
	DTNodeRoot(DTTree owner)
	{
		this.owner = owner ;
	}
	
	DTNodeRoot(DTTree owner,float x,float y)
	{
		this(owner) ;
		this.rootId = CompressUUID.createNewId() ;
		this.x = x ;
		this.y = y ;
	}
	
	public DTTree getOwner()
	{
		return this.owner;
	}
	
	public String getRootId()
	{
		return this.rootId ;
	}
	
	public float getX()
	{
		return this.x ;
	}
	
	public float getY()
	{
		return this.y ;
	}
	
	public List<String> getSubNodeIds()
	{
		return this.subNodeIds ;
	}

	public synchronized ArrayList<DTNode> getSubNodes()
	{
		if(subNodes!= null)
			return subNodes ;
		ArrayList<DTNode> ret = new ArrayList<>() ;
		for(String nid:this.subNodeIds)
		{
			DTNode nd = this.owner.findNodeById(nid) ;
			if(nd==null)
				continue ;
			ret.add(nd) ;
		}
		return subNodes = ret ;
	}
	
	public JSONObject toJO(boolean b_show_detail)
	{
		return new JSONObject().put("x",x).put("y", y).putOpt("sub_nids", this.subNodeIds) ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		this.x = jo.optFloat("x",0) ;
		this.y = jo.optFloat("y",0) ;
		JSONArray jarr = jo.optJSONArray("sub_nids") ;
		if(jarr!=null)
		{
			int n = jarr.length() ; 
			for(int i = 0 ; i < n ; i ++)
			{
				String nid = jarr.optString(i) ;
				if(Convert.isNullOrEmpty(nid))
						continue ;
				this.subNodeIds.add(nid) ;
			}
		}
		return true ;
	}
}
