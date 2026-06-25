package org.iottree.core.devtree;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * tree instance,and this is root NodeGroup too.
 * 
 * @author jason.zhu
 *
 */
public class DTTree extends DTNodeGrp implements Comparable<DTTree>
{
	public static enum NodeAddWay
	{
		sub(0),
		sibling(1),
		sibling_ahead(2);
		
		private final int val ;
		
		NodeAddWay(int v)
		{
			val = v ;
		}
		
		public int getInt()
		{
			return val ;
		}
		
		public static NodeAddWay fromInt(int i)
		{
			switch(i)
			{
			case 0:
				return sub;
			case 1:
				return sibling;
			case 2:
				return sibling_ahead;
			default:
				return null ;
			}
		}
	}
	
	String treeId ;
	
	int curId = 0 ;
	
	long updateDT = -1 ;
	
	float x = 0 ;
	float y = 0 ;
	
	ArrayList<DTNodeRoot> roots = new ArrayList<>() ;
	
	private transient HashMap<String,DTNode> cachedId2Node = null;//new HashMap<>() ;
	
	public DTTree()
	{
		super(null) ;
	}
	
	DTTree(String title,String desc)
	{
		super(null,title,desc) ;
		treeId = CompressUUID.createNewId() ;
		updateDT = System.currentTimeMillis() ;
	}
	
	@Override
	protected String getNodeTp() 
	{
		return "t";
	}
	
	int getNextId()
	{
		curId ++ ;
		return curId ;
	}
	
	public long getUpdateDT()
	{
		return this.updateDT ;
	}
	
	@Override
	public String getNodeId()
	{
		return this.treeId ;
	}
	
	public String getTreeId()
	{
		return this.treeId ;
	}
	
	private synchronized HashMap<String,DTNode> getCachedId2Node()
	{
		if(cachedId2Node!=null)
			return cachedId2Node;
		
		HashMap<String,DTNode> id2n = new HashMap<>() ;
		constructId2Node(this,id2n) ;
		return cachedId2Node = id2n ;
	}
	
	private static void constructId2Node(DTNodeGrp cur_n,HashMap<String,DTNode> id2n)
	{
		List<DTNode> ns = cur_n.getChildNodes() ;
		if(ns==null || ns.size()<=0)
			return ;
		for(DTNode n:ns)
		{
			id2n.put(n.getNodeId(),n) ;
			if(n instanceof DTNodeGrp)
				constructId2Node((DTNodeGrp)n,id2n) ;
		}
	}
	
	
	synchronized void clearCache()
	{
		cachedId2Node = null ;
		roots = null ;
	}
	
	
	public DTNode findNodeById(String tree_nid)
	{
		if(this.treeId.equals(tree_nid))
			return this ;
		return getCachedId2Node().get(tree_nid) ;
	}
	
	public DTNode setNodeTitle(String tree_nid,String t) throws Exception
	{
		DTNode nd = this.findNodeById(tree_nid);
		if (nd == null)
			return null;
		nd.setTitle(t);
		save();
		clearCache();
		return nd ;
	}
	
	public DTNodeGrp addNodeGrp(String ref_tree_nid, String t, String d,NodeAddWay way) throws Exception
	{
		DTNodeGrp pn = null;
		DTNodeGrp newnd = null;
		int idx = -1 ;
		if (Convert.isNotNullEmpty(ref_tree_nid))
		{
			DTNode pn0 = this.findNodeById(ref_tree_nid);
			if (pn0 == null)
				return null;
			if(!(pn0 instanceof DTNodeGrp))
				throw new Exception("pnode is not DTNodeGrp") ;
			pn = (DTNodeGrp)pn0 ;
			if(way==null)
				way = NodeAddWay.sub;
			
			switch(way)
			{
			case sibling:
				pn = (DTNodeGrp)pn0.getParent();
				if(pn==null)
					return null;
				idx = pn.getChildNodeIdx(pn0) +1;
				break ;
			case sibling_ahead:
				pn = (DTNodeGrp)pn0.getParent();
				if(pn==null)
					return null;
				idx = pn.getChildNodeIdx(pn0) -1;
				if(idx<0)
					idx = 0 ;
				break ;
			}
		}
		else
		{// add to root
			pn = this;
		}
		newnd = pn.addChildGrp(t, d,idx);
		save();
		clearCache();

		return newnd;
	}
	
	public DTNodePart addNodePart(DTDevPart dp,String p_tree_nid, String t, String d) throws Exception
	{
		DTNodeGrp pn = null;
		if (Convert.isNotNullEmpty(p_tree_nid))
		{
			DTNode pn0 = this.findNodeById(p_tree_nid);
			if (pn0 == null)
				return null;
			if(!(pn0 instanceof DTNodeGrp))
				throw new Exception("pnode is not DTNodeGrp") ;
			pn = (DTNodeGrp)pn0 ;
		}
		else
		{
			pn = this;
		}

		DTNodePart newnd = pn.addChildPart(dp,t, d);
		
		save();
		clearCache();

		return newnd;
	}

	public DTNode updateNode(String tree_nid, String t, String d) throws Exception
	{
		DTNode nd = this.findNodeById(tree_nid);
		if (nd == null)
			return null;
		
		nd.asBasic(t, d);
		save();
		clearCache();

		return nd;
	}

	public DTNode delNode(String tree_nid) throws Exception
	{
		DTNode nd = this.findNodeById(tree_nid);
		if (nd == null)
			return null;
		
		DTNodeGrp pn =(DTNodeGrp)nd.getParent();
		if (pn == null)
		{
			return null;
		}
		else
		{
			if (pn.removeChild(tree_nid)==null)
				return null;
		}
		save();
		clearCache();
		return nd;
	}
	
	public JSONObject rendAsRootNode4JsTree(DTTreeRenderCtrl tr_ctrl) //throws Exception
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id","_");// this.getId()) ;
		jo.put("nc", 0) ;
		jo.put("tp", "device") ;
		jo.put("root", true) ;
		jo.put("tn_subid", "") ;
		
		String icon =  "<i class=\"fa fa-sitemap\" /></i>";
		
		jo.put("icon", "fa fa-sitemap") ;
		String txt = "<span style='font-weight: bold;'>"+this.getTitle()+"</span>" ;
		
		jo.put("text",icon+txt) ;
		jo.put("state", new JSONObject().put("opened", true)) ;
		JSONArray jarr = renderSubNode4JsTree(tr_ctrl) ;
		if(jarr==null)
			jarr = new JSONArray() ;
		jo.put("children", jarr) ;
		return jo ;
	}
	
	public JSONArray renderSubNode4JsTree(DTTreeRenderCtrl tr_ctrl) //throws Exception
	{
		List<DTNode> subns = this.getChildNodes() ;
		JSONArray jarr = new JSONArray() ;
		
		if(subns!=null)
		{
			for(DTNode dn:subns)
			{
				if(tr_ctrl.checkIgnoreNode(dn))
					continue ;
				//需要展示的树节点严格按照层次组合唯一
				JSONObject jo = dn.renderToTree(tr_ctrl);//,null) ;
				if(jo==null)
					continue ;
				jarr.put(jo) ;
			}
		}
		return jarr ;
	}
	
	@Override
	public JSONObject toJO(boolean b_show_detail)
	{
		JSONObject ret = super.toJO(b_show_detail);
		
		ret.put("treeid", this.treeId) ;
		JSONArray jarr = new JSONArray() ;
		if(this.roots!=null)
		{
			for(DTNodeRoot r:this.roots)
			{
				jarr.put(r.toJO(b_show_detail)) ;
			}
		}
		ret.put("roots", jarr) ;
		ret.put("__curid", curId) ;
		ret.put("x", this.x);
		ret.put("y", this.y);
		
		return ret;
	}
	
	
	@Override
	public boolean fromJO(JSONObject jo)
	{
		if(!super.fromJO(jo))
			return false;
		
		this.treeId = jo.optString("treeid") ;
		if(Convert.isNullOrEmpty(this.treeId))
			return false;
		this.nodeId = this.treeId;
		this.curId = jo.optInt("__curid", 0) ;
		this.x = jo.optFloat("x",0.0f);
		this.y = jo.optFloat("y",0.0f);
		JSONArray jarr = jo.optJSONArray("roots") ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.optJSONObject(i) ;
				if(tmpjo==null)
					continue ;
				DTNodeRoot r = new DTNodeRoot(this) ;
				if(r.fromJO(tmpjo))
					this.roots.add(r);
			}
		}
		return true ;
	}

	@Override
	public int compareTo(DTTree o)
	{
		if(o.updateDT>this.updateDT)
			return -1 ;
		else if(o.updateDT<this.updateDT)
			return 1 ;
		else
			return 0 ;
	}
	
	public void save() throws IOException
	{
		DTTreeManager.getInstance().saveTree(this);
	}
	
	public boolean renderOut(Writer out) throws IOException
	{
		JSONObject jo = this.toJO(true) ;
		
		// fit for UI
		jo.putOpt("_tp", "__tree") ;
		jo.putOpt("tpt", "Tree") ;
		jo.putOpt("color", "#FFD700") ;
		jo.putOpt("icon", "\\uf126-90") ;
		jo.putOpt("runner",true) ;
		jo.putOpt("pm_ready",true) ;
		jo.putOpt("pm_err","") ;
				
		jo.write(out) ;
		return true ;
	}
}
