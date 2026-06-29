package org.iottree.core.devtree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

public class DTNodeGrp extends DTNode
{
	public static final String TP = "g" ;
	//LinkedHashMap<String,DTNodePart> nodeParts = new LinkedHashMap<>() ;
	
	LinkedHashMap<String,DTNode> subNodes = new LinkedHashMap<>() ;
	

	/**
	 * for load
	 * @param parent
	 */
	DTNodeGrp(DTNodeGrp parent)
	{
		super(parent) ;
	}
	
	/**
	 * for new
	 * @param parent
	 * @param title
	 * @param desc
	 */
	public DTNodeGrp(DTNodeGrp parent,String title,String desc)
	{
		super(parent,title,desc) ;
	}
	
	public DTNodeGrp getParentNode()
	{
		return (DTNodeGrp)this.parent ;
	}

	@Override
	protected String getNodeTp()
	{
		return TP;
	}

	public synchronized List<DTNode> getChildNodes()
	{
		ArrayList<DTNode> rets = new ArrayList<>() ;
		rets.addAll(this.subNodes.values()) ;
		//rets.addAll(this.nodeParts.values()) ;
		return rets;
		//return subNodes;
	}
	
	public synchronized DTNode getChildNodeById(String nodeid)
	{
//		DTNodeGrp nd = this.nodeGrps.get(nodeid) ;
//		if(nd!=null)
//			return nd ;
//		return this.nodeParts.get(nodeid) ;
		return this.subNodes.get(nodeid) ;
	}
	
	public synchronized int getChildNodeIdx(DTNode nd)
	{
		int idx = 0 ;
		for(DTNode n:this.subNodes.values())
		{
			if(n==nd)
				return idx ;
			idx ++ ;
		}
		return -1 ;
	}
	
	synchronized DTNodeGrp addChildGrp(String title,String desc,int idx)
	{
		DTNodeGrp grp = new DTNodeGrp(this,title,desc) ;
		//this.nodeGrps.put(grp.getNodeId(),grp) ;
		appendChild(grp,idx,null);
		
		return grp ;
	}
	
	synchronized DTNodePart addChildPart(DTDevPart dp,String title,String desc)
	{
		DTNodePart nd = new DTNodePart(this, title, desc) ;
		//this.nodeParts.put(nd.getNodeId(),nd) ;
		this.subNodes.put(nd.getNodeId(),nd) ;
		return nd ;
	}
	
	synchronized DTNode removeChild(String nodeid)
	{
		return this.subNodes.remove(nodeid) ;
//		if(ret!=null)
//			return ret ;
//		return this.nodeParts.remove(nodeid) ;
	}
	
	/**
	 * append or move other node to sub
	 * @param nd
	 * @param idx
	 * @param failedr
	 * @return
	 */
	public synchronized boolean appendChild(DTNode nd,int idx,StringBuilder failedr)
	{
		//judge
		if(nd instanceof DTNodeGrp)
		{
			if(nd==this || this.hasAncestor((DTNodeGrp)nd))
			{
				if(failedr!=null)
					failedr.append("cannot append self or ancestors") ;
				return false;
			}
		}
		
		int oldidx = this.getChildNodeIdx(nd) ;
		if(oldidx>=0)
		{//change order in same parent node
			if(idx<0)
				idx = this.subNodes.size() ;
			if(oldidx==idx)
				return true;
			if(idx>oldidx)
				idx-- ;
			this.subNodes.remove(nd.getNodeId()) ;
			List<DTNode> ns = this.getChildNodes() ;
			if(idx>=ns.size())
				ns.add(nd) ;
			else
				ns.add(idx, nd);
			this.subNodes.clear();
			for(DTNode n:ns)
				this.subNodes.put(n.getNodeId(),n) ;
			return true ;
		}
		
		DTNodeGrp oldpn = nd.getParentGrp() ;
		if(oldpn!=null&&oldpn!=this)
		{
			oldpn.removeChild(nd.getNodeId()) ;
		}
		nd.parent = this ;
		
		if(idx<0||idx>=this.subNodes.size())
		{
			this.subNodes.put(nd.getNodeId(),nd) ;
		}
		else
		{
			List<DTNode> ns = this.getChildNodes() ;
			ns.add(idx, nd) ;
			this.subNodes.clear();
			for(DTNode n:ns)
				this.subNodes.put(n.getNodeId(),n) ;
		}
		return true;
	}
	
	@Override
	public JSONObject renderToTree(DTTreeRenderCtrl tr_ctrl) //throws Exception
	{
		JSONObject jo = new JSONObject() ;
		
		jo.put("id",this.getNodeId());
		
		//jo.putOpt("n", this.getName()) ;
		jo.putOpt("t", this.getTitle()) ;
		jo.put("tp", ""+this.getNodeTp()) ;
		
		jo.put("path_tt", this.getPathTitle()) ;
		
		
		List<DTNode> subns = this.getChildNodes() ;
		
		String icon ;
		String css=getTreeNodeCss();
		if(subns!=null && subns.size()>0)
		{
			//jo.put("icon","fa fa-folder") ;
			jo.put("children",true) ;
			icon = "<span class='tn_icon ' style='"+css+"'>"+this.getNodeIcon()+"</span>  " ;
			
			if(tr_ctrl.checkRenderChild(this))
			{
				JSONArray jarr = new JSONArray() ;
				for(DTNode subn:subns)
				{
					JSONObject tmpjo = subn.renderToTree(tr_ctrl) ;//,p_node_id) ;
					if(tmpjo==null)
						continue ;
					jarr.put(tmpjo) ;
				}
				jo.put("children", jarr) ;
			}
		}
		else
		{
			//jo.put("icon","fa fa-circle") ;
			icon = "<span class='tn_icon ' style='"+css+"'>"+this.getNodeIcon()+"</span>  " ;
		}
		
		String tt = getTitle();
		//boolean b_top_c =checkInContainer(top_c) ;
		String txt_cc = "color:blue;"; //b_top_c?"color:blue;":"" ;
		String tip = "" ;
		jo.put("text","<span class='tn_cc' style='"+txt_cc+"' title='"+tip+"  "+this.getTreeNodeTips()+"'>"+icon+tt+"</span>");
		return jo ;
	}
	
	public JSONArray renderToTreeSub(DTTreeRenderCtrl tr_ctrl)
	{
		List<DTNode> subns = this.getChildNodes() ;
		JSONArray jarr = new JSONArray() ;
		if(subns!=null)
		{
			for(DTNode dn:subns)
			{
				JSONObject subjo = dn.renderToTree(tr_ctrl) ;//,p_node_id) ;
				if(subjo==null)
					continue ;
				jarr.put(subjo) ;
			}
		}
		return jarr ;
	}
	
	@Override
	public JSONObject toJO(boolean b_show_detail)
	{
		JSONObject ret = super.toJO(b_show_detail);
		
		JSONArray jarr = new JSONArray() ;
		for(DTNode np:this.subNodes.values())
		{
			jarr.put(np.toJO(b_show_detail)) ;
		}
		ret.put("subs", jarr) ;
		
//		jarr = new JSONArray() ;
//		for(DTNodeGrp ng:this.nodeGrps.values())
//		{
//			jarr.put(ng.toJO()) ;
//		}
//		ret.put("grps", jarr) ;
		
		return ret;
	}
	
	@Override
	public boolean fromJO(JSONObject jo)
	{
		if(!super.fromJO(jo))
			return false;
		
		JSONArray jarr = jo.optJSONArray("subs") ;
		if(jarr!=null)
		{
			int n = jarr.length() ; 
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				String tp = tmpjo.optString("_tp","");
				DTNode dtn = null ;
				switch(tp)
				{
				case DTNodeGrp.TP:
					dtn = new DTNodeGrp(this);
					break;
				case DTNodePart.TP:
					dtn = new DTNodePart(this);
					break;
				}
				if(dtn==null)
					continue ;
				if(dtn.fromJO(tmpjo))
					this.subNodes.put(dtn.getNodeId(),dtn) ;
			}
		}
//		jarr = jo.optJSONArray("grps") ;
//		if(jarr!=null)
//		{
//			int n = jarr.length() ; 
//			for(int i = 0 ; i < n ; i ++)
//			{
//				JSONObject tmpjo = jarr.getJSONObject(i) ;
//				DTNodeGrp np = new DTNodeGrp(this);
//				if(np.fromJO(tmpjo))
//					this.nodeGrps.put(np.getNodeId(),np) ;
//			}
//		}
		return true ;
	}
}
