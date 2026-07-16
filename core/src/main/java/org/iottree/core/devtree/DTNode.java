package org.iottree.core.devtree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsEnv;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.devtree.cxt.DevContext;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.IPack;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

public class DTNode extends JSObMap implements IPack, ILang
{
	public static class PartRef
	{
		String tpUID = null; // if this node is create by part tp or be set ref part
		
		String tpSubId = null;// null represent part ins root or it's part inner node
		
		String partId = null; //more detail spare part,only for tp root
		
		public PartRef(String tp_uid,String tp_sub_id,String part_id)
		{
			this.tpUID = tp_uid ;
			this.tpSubId = tp_sub_id ;
			this.partId = part_id ;
		}
		
		PartRef copyMe()
		{
			return new PartRef(this.tpUID,this.tpSubId,this.partId) ;
		}
		
		public boolean isPartRefMain()
		{
			return Convert.isNullOrEmpty(this.tpSubId) ;
		}
		
		public boolean isPartRefSub()
		{
			return Convert.isNotNullEmpty(this.tpSubId) ;
		}
		
		public String getPartTpUID()
		{
			return this.tpUID ;
		}
		
		public String getPartTpSubId()
		{
			return tpSubId ;
		}
		
		public String getPartId()
		{
			return this.partId ;
		}
		
		public DTDevPartTP getPartTp()
		{
			return DTDevPartManager.getInstance().getPartTPByUID(this.tpUID) ;
		}
		
		public String getPartTpTitle()
		{
			DTDevPartTP ptp = getPartTp() ;
			if(ptp==null)
				return null ;//error,may be deleted
			return ptp.getTitle() ;
		}
		
		public DTNode getPartTpSubNd()
		{
			DTDevPartTP ptp = DTDevPartManager.getInstance().getPartTPByUID(this.tpUID) ;
			if(ptp==null)
				return null ;
			return ptp.findOffspringNodeById(this.tpSubId) ;
		}
		
		public DTDevPart getPart()
		{
			if(Convert.isNullOrEmpty(this.partId))
				return null ;
			DTDevPartTP ptp = DTDevPartManager.getInstance().getPartTPByUID(this.tpUID) ;
			if(ptp==null)
				return null ;
			return ptp.getPartById(this.partId) ;
		}
		
		public JSONObject toJO(boolean b_detail)
		{
			JSONObject ret = new JSONObject().putOpt("tp_uid", this.tpUID).putOpt("tp_subid", this.tpSubId)
					.put("part_id", this.partId) ;
			if(b_detail)
			{
				DTDevPartTP ptp = getPartTp() ;
				if(ptp!=null)
				{
					ret.put("parttp_tt",ptp.getTitle()) ;
				}
			}
			return ret ;
		}
		
		public static PartRef fromJO(JSONObject jo)
		{
			String tp_uid = jo.optString("tp_uid") ;
			if(Convert.isNullOrEmpty(tp_uid))
				return null ;
			
			String tp_subid = jo.optString("tp_subid") ;
			String part_id = jo.optString("part_id") ;
			return new PartRef(tp_uid,tp_subid,part_id);
		}
	}
	
	public static final String TP = "g";

	String nodeId;

	String name = null;

	String title;

	String desc;

	DTStaticData staticData = null;
	/**
	 * binded tags
	 */
	LinkedHashMap<String, DTRunTag> path2runtag = new LinkedHashMap<>();

	LinkedHashMap<String, DTRunBlkIns> insname2runblk = new LinkedHashMap<>();

	LinkedHashMap<String, DTNode> subNodes = new LinkedHashMap<>();

	PartRef partRef = null ;

	DTNode parent;
	/**
	 * for load
	 * 
	 * @param parent
	 */
	DTNode(DTNode parent)
	{
		this.parent = parent;
	}

	/**
	 * for new
	 * 
	 * @param parent
	 * @param title
	 * @param desc
	 */
	public DTNode(DTNode parent, String title, String desc)
	{
		this.parent = parent;
		if(parent!=null)
			this.nodeId = parent.getNextIdFromRoot();
		if (Convert.isNullOrEmpty(title))
			title = "noname";
		this.title = title;
		this.desc = desc;
	}

	public DTNode(DTNodeRoot root,DTNode parent, DTNode oth, boolean ignore_runtag,boolean ignore_partref, boolean b_newid, boolean b_deep)
	{
		this.parent = parent;
		if (b_newid)
		{
			//if(parent==null)
			//	throw new IllegalArgumentException("no parent node in root found") ;
			this.nodeId = root.getNextId(this.getNodeTp());//parent.getNextIdFromRoot() ;
		}
		else
			this.nodeId = oth.nodeId;

		copyLocalFromOth(oth,ignore_runtag,ignore_partref);

		if (b_deep)
		{
			copySubDeepFromOth(root,oth,ignore_runtag,ignore_partref, b_newid, b_deep);
		}
	}
	
	protected void copyLocalFromOth(DTNode oth,boolean ignore_runtag,boolean ignore_partref)
	{
		this.name = oth.name;
		this.title = oth.title;
		this.desc = oth.desc;
		if (oth.staticData == null)
			this.staticData = null;
		else
			this.staticData = new DTStaticData(this, oth.staticData);
		
		if(!ignore_partref && oth.partRef!=null)
		{
			this.partRef = oth.partRef.copyMe() ;
		}
		
		if (!ignore_runtag)
		{
			for (DTRunTag rt : oth.path2runtag.values())
			{
				rt = new DTRunTag(this, rt);
				this.path2runtag.put(rt.tagPath, rt);
			}
		}
		for (DTRunBlkIns rt : oth.insname2runblk.values())
		{
			rt = new DTRunBlkIns(this, rt);
			this.insname2runblk.put(rt.insName, rt);
		}
	}
	
	protected void copySubDeepFromOth(DTNodeRoot root,DTNode oth,boolean ignore_runtag,boolean ignore_partref, boolean b_newid, boolean b_deep)
	{
		for (DTNode sub_n : oth.subNodes.values())
		{
			sub_n = new DTNode(root,this, sub_n, ignore_runtag,ignore_partref, b_newid, b_deep);
			this.subNodes.put(sub_n.getNodeId(), sub_n);
		}
	}
	
	DTNode addSubNodeByPartTP(DTDevPartTP part_tp,DTDevPart part,StringBuilder failedr)
	{
		if(this.isPartRefNode())
		{
			failedr.append("cannot add sub node to Ref Part Node") ;
			return null ;
		}
		DTNodeRoot root = this.getRoot() ;
		if(root==null)
		{
			failedr.append("no root found in this node") ;
			return null;
		}
		DTNode ret = new DTNode(parent) ;
		constructByPartTP(root,this,part_tp,part,ret,true,part_tp) ;
		if(!this.appendChild(ret, -1, failedr))
			return null;
		return ret;
	}
	
	boolean setThisNodeByPartTP(DTDevPartTP part_tp,DTDevPart part,StringBuilder failedr)
	{
		if(this.isPartRefNode())
		{
			failedr.append("this node is part ref node") ;
			return false;
		}
		
		DTNodeRoot root = this.getRoot() ;
		if(root==null)
		{
			failedr.append("no root found in this node") ;
			return false;
		}
		if(this.getParent()==null)
		{
			failedr.append("root or isolated node cannot be set part tp") ;
			return false;
		}
		if(this.subNodes!=null&&this.subNodes.size()>0)
		{
			failedr.append("not leaf node") ;
			return false;
		}
		
		String old_title =this.getTitle() ;
		constructByPartTP(root,this.getParent(),part_tp,part,this,false,part_tp) ;
		this.title = old_title;
		return true;
	}
	
//	private static DTNode addByPartTP(DTNodeRoot root,DTNode parent,DTDevPartTP part_tp,DTDevPart part)
//	{
//		DTNode ret = new DTNode(parent) ;
//		constructByPartTP(root,parent,part_tp,part,ret,true,part_tp) ;
//		return ret;
//	}
	
	private static void constructByPartTP(DTNodeRoot root,DTNode parent,DTDevPartTP part_tp,DTDevPart part,
			DTNode cur_nd,boolean cur_nd_newid,DTNode cur_part_tp_nd)
	{
		String part_tp_uid = part_tp.getPartTpUID() ;
		String part_tp_subid = null ;
		String part_id = null ;
		if(!(cur_part_tp_nd instanceof DTDevPartTP))
			part_tp_subid = cur_part_tp_nd.getNodeId() ;
		if(part!=null)
			part_id = part.getPartId() ;
		
		cur_nd.parent = parent;
		if(cur_nd_newid)
			cur_nd.nodeId = root.getNextId(cur_nd.getNodeTp());
		
		cur_nd.copyLocalFromOth(cur_part_tp_nd,true,true);//part ref ignore=true
		cur_nd.partRef = new PartRef(part_tp_uid, part_tp_subid, part_id) ;//

		for (DTNode subn : cur_part_tp_nd.subNodes.values())
		{
			DTNode subnew = new DTNode(parent) ;
			constructByPartTP(root,cur_nd,part_tp,part,subnew,true,subn) ;
			cur_nd.subNodes.put(subnew.getNodeId(), subnew);
		}
	}

	public DTNode getParentNode()
	{
		return this.parent;
	}
	
	public DTNodeRoot getRootNode()
	{
		if(this.parent==null)
		{
			if(this instanceof DTNodeRoot)
				return (DTNodeRoot)this ;
			else
				return null ;
		}
		return this.parent.getRootNode() ;
	}
	
	private String getNextIdFromRoot()
	{
		DTNodeRoot nr = this.getRootNode() ;
		if(nr==null)
			return null ;
		return nr.getNextId(this.getNodeTp()) ;
	}

	protected String getNodeTp()
	{
		return TP;
	}

	public String getNodeId()
	{
		return this.nodeId;
	}

	public String getName()
	{
		return this.name;
	}

	boolean setName(String n)
	{
		if (Convert.isNullOrEmpty(n))
		{
			this.name = n;
			return true;
		}
		if (n.equals(this.name))
			return true;
		DTNode p = this.getParent();
		if (p == null)
		{
			this.name = n;
			return true;
		}
		DTNode oldn = this.getChildNodeByName(n);
		if (oldn != null)
			return false;
		this.name = n;
		return true;
	}

	public String getTitle()
	{
		return this.title;
	}

	public boolean setTitle(String t,StringBuilder failedr)
	{
		if(this.isPartRefSub())
		{
			failedr.append("part ref sub node cannot be modified");
			return false ;
		}
		this.title = t;
		return true;
	}

	public String getDesc()
	{
		return this.desc;
	}

	public String getPathTitle()
	{
		DTNode pn = this.getParent();
		String t = this.getTitle();
		if (Convert.isNullOrEmpty(t))
			t = "";

		if (pn == null)
			return "/" + t;

		return pn.getPathTitle() + "/" + t;
	}

	public DTNode asBasic(String title, String desc)
	{
		this.title = title;
		this.desc = desc;
		return this;
	}

	public DTNode getParent()
	{
		return this.parent;
	}

	public DTNode getParentGrp()
	{
		return (DTNode) this.parent;
	}

	public boolean hasAncestor(DTNode grp)
	{
		DTNode p = getParentGrp();
		if (p == null)
			return false;
		if (p == grp)
			return true;
		return p.hasAncestor(grp);
	}
	
	public DTNodeRoot getRoot()
	{
		if (this.parent == null)
		{
			if (this instanceof DTNodeRoot)
				return (DTNodeRoot) this;
			return null;
		}
		return this.parent.getRoot();
	}
	
	public boolean isNodeInPartTP()
	{
		DTNodeRoot root = getRoot() ;
		if(root==null)
			return false;
		return root instanceof DTDevPartTP ;
	}

	public DTTree getTree()
	{
		if (this.parent == null)
		{
			if (this instanceof DTTree)
				return (DTTree) this;
			return null;
		}

		return this.parent.getTree();
	}
	
	public PartRef getPartRef()
	{
		return this.partRef ;
	}
	
	public boolean isPartRefNode()
	{
		return this.partRef!=null;
	}
	
	public boolean isPartRefMain()
	{
		if(this.partRef==null)
			return false;
		return this.partRef.isPartRefMain() ;
	}
	
	public boolean isPartRefSub()
	{
		if(this.partRef==null)
			return false;
		return this.partRef.isPartRefSub() ;
	}
	
	public boolean canBeMove(StringBuilder reson)
	{
		if(this.parent==null)
		{
			reson.append("no parent") ;
            return false;
		}
        if(this.isPartRefNode()&& this.isPartRefSub())
        {
        	reson.append("it's part ref sub node") ;
            return false;
        }
        return true;
	}

	void clearCache()
	{
		DTTree t = this.getTree();
		if (t == null)
			return;
		t.clearCache();
	}

	protected String getTreeNodeCss()
	{
		return "";
	}

	protected String getTreeNodeTips()
	{
		return "";
	}

	public String getNodeIcon()
	{
		if(this.isPartRefNode())
			return "\\uf1b2";
		return "\\uf5fd";
	}
	
	public String getNodeColor()
	{
		if(this.isPartRefNode())
		{
			if(this.isPartRefSub())
				return "#f0f0f0";
			return "#cce8ff";
		}
		return "#fef4ec";
	}

	public String getTNIcon()
	{
		if(this.isPartRefNode())
			return "<i class=\"fa-solid fa-cube\"></i>";
		return "<i class=\"fa-solid fa-layer-group\"></i>";
	}
	// children

	public synchronized List<DTNode> getChildNodes()
	{
		ArrayList<DTNode> rets = new ArrayList<>();
		rets.addAll(this.subNodes.values());
		return rets;
	}

	public synchronized DTNode getChildNodeById(String nodeid)
	{
		// DTNode nd = this.nodeGrps.get(nodeid) ;
		// if(nd!=null)
		// return nd ;
		// return this.nodeParts.get(nodeid) ;
		return this.subNodes.get(nodeid);
	}

	public synchronized DTNode getChildNodeByName(String name)
	{
		if (Convert.isNullOrEmpty(name))
			return null;
		for (DTNode n : this.subNodes.values())
		{
			if (name.equals(n.getName()))
				return n;
		}
		return null;
	}

	public synchronized int getChildNodeIdx(DTNode nd)
	{
		int idx = 0;
		for (DTNode n : this.subNodes.values())
		{
			if (n == nd)
				return idx;
			idx++;
		}
		return -1;
	}
	
	public DTNode findOffspringNodeById(String nodeid)
	{
		DTNode dn = this.subNodes.get(nodeid);
		if(dn!=null)
			return dn ;
		for(DTNode subdn:this.subNodes.values())
		{
			dn = subdn.findOffspringNodeById(nodeid) ;
			if(dn!=null)
				return dn ;
		}
		return null ;
	}

	synchronized DTNode addChild(String title, String desc, int idx,StringBuilder failedr)
	{
		if(this.isPartRefNode())
		{
			failedr.append("part ref node cannot add child") ;
			return null ;
		}
		DTNode grp = new DTNode(this, title, desc);
		// this.nodeGrps.put(grp.getNodeId(),grp) ;
		appendChild(grp, idx, null);

		return grp;
	}

	// synchronized DTNodePart addChildPart(DTDevPart dp,String title,String
	// desc)
	// {
	// DTNodePart nd = new DTNodePart(this, title, desc) ;
	// //this.nodeParts.put(nd.getNodeId(),nd) ;
	// this.subNodes.put(nd.getNodeId(),nd) ;
	// return nd ;
	// }

	synchronized DTNode removeChild(String nodeid,StringBuilder failedr)
	{
		DTNode subn = this.getChildNodeById(nodeid) ;
		if(subn==null)
		{
			failedr.append("no node found") ;
			return null;
		}
		if(subn.isPartRefSub())
		{
			failedr.append("part ref sub node cannot be deleted") ;
			return null ;
		}
		return this.subNodes.remove(nodeid);
		// if(ret!=null)
		// return ret ;
		// return this.nodeParts.remove(nodeid) ;
	}

	/**
	 * append or move other node to sub
	 * 
	 * @param nd
	 * @param idx
	 * @param failedr
	 * @return
	 */
	public synchronized boolean appendChild(DTNode nd, int idx, StringBuilder failedr)
	{
		if(this.isPartRefNode())
		{
			if (failedr != null)
				failedr.append("part ref node cannot append child");
			return false;
		}
		// judge
		if (nd == this || this.hasAncestor((DTNode) nd))
		{
			if (failedr != null)
				failedr.append("cannot append self or ancestors");
			return false;
		}

		String name = nd.getName();
		if (Convert.isNotNullEmpty(name))
		{
			DTNode oldn = this.getChildNodeByName(name);
			if (oldn != null)
			{
				if (failedr != null)
					failedr.append("append failed because node has child node with name=" + name);
				return false;
			}
		}

		int oldidx = this.getChildNodeIdx(nd);
		if (oldidx >= 0)
		{// change order in same parent node
			if (idx < 0)
				idx = this.subNodes.size();
			if (oldidx == idx)
				return true;
			if (idx > oldidx)
				idx--;
			this.subNodes.remove(nd.getNodeId());
			List<DTNode> ns = this.getChildNodes();
			if (idx >= ns.size())
				ns.add(nd);
			else
				ns.add(idx, nd);
			this.subNodes.clear();
			for (DTNode n : ns)
				this.subNodes.put(n.getNodeId(), n);
			return true;
		}

		DTNode oldpn = nd.getParent();
		if (oldpn != null && oldpn != this)
		{
			if(oldpn.removeChild(nd.getNodeId(),failedr)==null)
				return false;
		}
		nd.parent = this;

		if (idx < 0 || idx >= this.subNodes.size())
		{
			this.subNodes.put(nd.getNodeId(), nd);
		}
		else
		{
			List<DTNode> ns = this.getChildNodes();
			ns.add(idx, nd);
			this.subNodes.clear();
			for (DTNode n : ns)
				this.subNodes.put(n.getNodeId(), n);
		}
		return true;
	}

	public JSONObject renderToTree(DTTreeRenderCtrl tr_ctrl) // throws Exception
	{
		JSONObject jo = new JSONObject();

		jo.put("id", this.getNodeId());

		// jo.putOpt("n", this.getName()) ;
		jo.putOpt("t", this.getTitle());
		jo.put("tp", "" + this.getNodeTp());

		jo.put("path_tt", this.getPathTitle());

		List<DTNode> subns = this.getChildNodes();

		String icon;
		String css = getTreeNodeCss();
		if (subns != null && subns.size() > 0)
		{
			// jo.put("icon","fa fa-folder") ;
			jo.put("children", true);
			icon = "<span class='tn_icon ' style='" + css + "'>" + this.getTNIcon() + "</span>  ";

			if (tr_ctrl.checkRenderChild(this))
			{
				JSONArray jarr = new JSONArray();
				for (DTNode subn : subns)
				{
					JSONObject tmpjo = subn.renderToTree(tr_ctrl);// ,p_node_id)
																	// ;
					if (tmpjo == null)
						continue;
					jarr.put(tmpjo);
				}
				jo.put("children", jarr);
			}
		}
		else
		{
			// jo.put("icon","fa fa-circle") ;
			icon = "<span class='tn_icon ' style='" + css + "'>" + this.getTNIcon() + "</span>  ";
		}

		String tt = getTitle();
		// boolean b_top_c =checkInContainer(top_c) ;
		String txt_cc = "color:blue;"; // b_top_c?"color:blue;":"" ;
		String tip = "";
		jo.put("text", "<span class='tn_cc' style='" + txt_cc + "' title='" + tip + "  " + this.getTreeNodeTips() + "'>"
				+ icon + tt + "</span>");
		return jo;
	}

	public JSONArray renderToTreeSub(DTTreeRenderCtrl tr_ctrl)
	{
		List<DTNode> subns = this.getChildNodes();
		JSONArray jarr = new JSONArray();
		if (subns != null)
		{
			for (DTNode dn : subns)
			{
				JSONObject subjo = dn.renderToTree(tr_ctrl);// ,p_node_id) ;
				if (subjo == null)
					continue;
				jarr.put(subjo);
			}
		}
		return jarr;
	}

	
	// other data

	public DTStaticData getStaticData()
	{
		return this.staticData;
	}

	public LinkedHashMap<String, DTRunBlkIns> getRunBlkInssMap()
	{
		return this.insname2runblk;
	}

	public DTRunBlkIns getRunBlkIns(String ins_name)
	{
		return this.insname2runblk.get(ins_name);
	}

	public LinkedHashMap<String, DTRunTag> getRunTagsMap()
	{
		return this.path2runtag;
	}

	void setStaticData(DTStaticData sd)
	{
		this.staticData = sd;
		sd.owner = this;
	}

	boolean setRunTags(List<DTRunTag> rts)
	{
		LinkedHashMap<String, DTRunTag> m = new LinkedHashMap<>();
		if (rts != null)
		{
			for (DTRunTag rt : rts)
			{
				m.put(rt.getTagPath(), rt);
			}
		}
		if (this.path2runtag.size() <= 0 && m.size() <= 0)
			return false;
		this.path2runtag = m;
		return true;
	}

	DTRunBlkIns addRunBlkInsBasic(String runblk_uid, String ins_name, String ins_title, StringBuilder failedr)
	{
		if (!Convert.checkVarName(ins_name, true, failedr))
			return null;
		DTRunBlkIns old_ins = this.getRunBlkIns(ins_name);
		if (old_ins != null)
		{
			failedr.append("[" + ins_name + "] " + g("is_existed"));
			return null;
		}
		DTRunBlk rb = DTRunBlkManager.getInstance().getRunBlkByUID(runblk_uid);
		if (rb == null)
		{
			failedr.append("no runblk with id=" + runblk_uid);
			return null;
		}
		DTRunBlkIns ins = new DTRunBlkIns(this, rb, ins_name, ins_title);
		this.insname2runblk.put(ins.getInsName(), ins);
		return ins;
	}

	DTRunBlkIns setRunBlkInsBasic(String ins_name, String ins_title, StringBuilder failedr)
	{
		DTRunBlkIns old_ins = this.getRunBlkIns(ins_name);
		if (old_ins == null)
		{
			failedr.append("[" + ins_name + "] " + g("not_existed"));
			return null;
		}
		old_ins.insTitle = ins_title;
		return old_ins;
	}

	DTRunBlkIns delRunBlkIns(String name)
	{
		return this.insname2runblk.remove(name);
	}

	public List<DTRunProp> listUsingProps()
	{
		// LinkedHashMap<String,DevProp> n2p = listInheritedAndSelfProps() ;
		// if(n2p==null||n2p.size()<=0)
		// return Arrays.asList() ;
		ArrayList<DTRunProp> rets = new ArrayList<>();
		// rets.addAll(n2p.values()) ;
		return rets;
	}

	public DTRunProp getUsingProp(String prop_n)
	{
		// LinkedHashMap<String,DevProp> n2p = listInheritedAndSelfProps() ;
		// if(n2p==null)
		// return null ;
		// return n2p.get(prop_n) ;
		return null;
	}

	@Override
	public Object JS_get(String key)
	{
		Object ob = super.JS_get(key);
		if (ob != null)
			return ob;
		if (key.startsWith("_"))
		{
			switch (key)
			{
			case "_this":
				return this;
			case "_sys":
				return DevContext.sys;
			case "_debug":
				return DevContext.debug;
			case "_util":
				return DevContext.util;
			}

			// env
			JsEnv env = JsEnv.getInThLoc();
			if (env != null)
			{
				return env.JS_get(key);
			}
		}

		DTRunProp dp = this.getUsingProp(key);
		if (dp != null)
			return dp;

		return null;
	}

	@Override
	public List<JsProp> JS_props()
	{
		List<JsProp> ss = super.JS_props();

		List<DTRunProp> props = this.listUsingProps();
		if (props != null)
		{
			for (DTRunProp dp : props)
			{
				ss.add(new JsProp(dp.getName(), null, DTRunProp.class, true, dp.getTitle(), dp.getDesc()));
			}
		}

		return ss;
	}

	public DevContext JS_getContext()
	{
		return JS_getContext(false);
	}

	DevContext rtCxt = null;

	public synchronized DevContext JS_getContext(boolean b_reset)
	{
		if (!b_reset && rtCxt != null)
			return rtCxt;

		try
		{
			if (rtCxt != null)
				rtCxt.close();// 释放资源

			rtCxt = new DevContext(this);
			return rtCxt;
		}
		catch ( Exception e)
		{
			e.printStackTrace();
			return null;
		}

	}
	
	public JSONObject toJO(boolean b_show_detail)
	{
		JSONObject ret = new JSONObject().put("id", this.nodeId).putOpt("n", this.name).put("_tp", this.getNodeTp())
				.putOpt("t", this.title).putOpt("d", this.desc);
		
		if(this.partRef!=null)
			ret.put("part_ref", this.partRef.toJO(b_show_detail)) ;
		
		if (staticData != null)
		{
			JSONObject sdjo = staticData.toJO();
			ret.put("static", sdjo);
		}

		if (b_show_detail)
		{
			ret.putOpt("color", getNodeColor());
			ret.putOpt("icon", this.getNodeIcon());
			int tags_num = path2runtag != null ? path2runtag.size() : 0;
			ret.put("tags_num", tags_num);
			int runblk_num = insname2runblk != null ? insname2runblk.size() : 0;
			ret.put("runblks_num", runblk_num);
			ret.put("has_static", staticData != null);
			ret.put("has_part_ref", this.partRef != null);
			
			boolean prm = this.isPartRefMain() ;
			ret.put("part_ref_main", prm) ;
			if(prm)
				ret.putOpt("part_ref_tt", this.partRef.getPartTpTitle());
			ret.put("part_ref_sub", this.isPartRefSub()) ;
			if(isNodeInPartTP())
				ret.put("in_parttp", true) ;
		}

		if (path2runtag != null && this.path2runtag.size() > 0)
		{
			JSONArray jarr = new JSONArray();
			ret.put("run_tags", jarr);
			for (DTRunTag tag : this.path2runtag.values())
			{
				jarr.put(tag.toJO(b_show_detail));
			}
		}

		if (insname2runblk != null && this.insname2runblk.size() > 0)
		{
			JSONArray jarr = new JSONArray();
			ret.put("runblk_inss", jarr);
			for (DTRunBlkIns ins : this.insname2runblk.values())
			{
				jarr.put(ins.toJO(b_show_detail));
			}
		}

		JSONArray jarr = new JSONArray();
		for (DTNode np : this.subNodes.values())
		{
			jarr.put(np.toJO(b_show_detail));
		}
		ret.put("subs", jarr);

		// jarr = new JSONArray() ;
		// for(DTNode ng:this.nodeGrps.values())
		// {
		// jarr.put(ng.toJO()) ;
		// }
		// ret.put("grps", jarr) ;

		return ret;
	}

	public boolean fromJO(JSONObject jo)
	{

		this.nodeId = jo.optString("id");
		if (Convert.isNullOrEmpty(this.nodeId))
			return false;
		this.name = jo.optString("n");
		this.title = jo.optString("t");
		this.desc = jo.optString("d");
		
		JSONObject part_ref_jo = jo.optJSONObject("part_ref") ;
		if(part_ref_jo!=null)
		{
			this.partRef = PartRef.fromJO(part_ref_jo) ;
		}

		JSONObject staticjo = jo.optJSONObject("static");
		if (staticjo != null)
		{
			this.staticData = DTStaticData.fromJO(this, staticjo);
		}

		JSONArray jarr = jo.optJSONArray("run_tags");
		if (jarr != null)
		{
			int n = jarr.length();
			for (int i = 0; i < n; i++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i);
				DTRunTag rt = DTRunTag.formJO(this, tmpjo);
				if (rt == null)
					continue;
				this.path2runtag.put(rt.getTagPath(), rt);
			}
		}

		jarr = jo.optJSONArray("runblk_inss");
		if (jarr != null)
		{
			int n = jarr.length();
			for (int i = 0; i < n; i++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i);
				DTRunBlkIns rbi = DTRunBlkIns.fromJO(this, tmpjo);
				if (rbi == null)
					continue;
				this.insname2runblk.put(rbi.getInsName(), rbi);
			}
		}

		jarr = jo.optJSONArray("subs");
		if (jarr != null)
		{
			int n = jarr.length();
			for (int i = 0; i < n; i++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i);
				String tp = tmpjo.optString("_tp", "");
				DTNode dtn = null;
				switch (tp)
				{
				case DTNode.TP:
					dtn = new DTNode(this);
					break;
				// case DTNodePart.TP:
				// dtn = new DTNodePart(this);
				// break;
				}
				if (dtn == null)
					continue;
				if (dtn.fromJO(tmpjo))
					this.subNodes.put(dtn.getNodeId(), dtn);
			}
		}
		// jarr = jo.optJSONArray("grps") ;
		// if(jarr!=null)
		// {
		// int n = jarr.length() ;
		// for(int i = 0 ; i < n ; i ++)
		// {
		// JSONObject tmpjo = jarr.getJSONObject(i) ;
		// DTNode np = new DTNode(this);
		// if(np.fromJO(tmpjo))
		// this.nodeGrps.put(np.getNodeId(),np) ;
		// }
		// }
		return true;
	}


	@Override
	public JSONObject toJO()
	{
		return this.toJO(false);
	}
}
