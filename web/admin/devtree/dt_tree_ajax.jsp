<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.devtree.*
	"%><%!
 static class CopyItem
 {
		String treeid ;
		String tree_nid ;
		
		public CopyItem(String treeid,String tree_nid)
		{
			this.treeid = treeid ;
			this.tree_nid = tree_nid ;
		}
		
		public DTNode getNode()
		{
			DTTree tree = DTTreeManager.getInstance().getTreeById(treeid) ;
			if(tree==null)
				return null ;
			return tree.findNodeById(tree_nid) ;
		}
 }
	
	static CopyItem copiedItem = null ;
%><%
if(!Convert.checkReqEmpty(request, out,"op"))
	return ;
String op = request.getParameter("op");
String treeid = request.getParameter("treeid");
String tree_nid = request.getParameter("tree_nid");

String tree_nids_str = request.getParameter("tree_nids");
String runblk_uid = request.getParameter("runblk_uid");

DTTree tree = null ;
if(Convert.isNotNullEmpty(treeid))
{
	tree = DTTreeManager.getInstance().getTreeById(treeid) ;
	if(tree==null)
	{
		out.print("no tree found with id="+treeid) ;
		return ;
	}
}

int idx = Convert.parseToInt32(request.getParameter("idx"), -1) ;

//  0 - sub node , 1 - sibling  2 - sibling head 
int add_sty = Convert.parseToInt32(request.getParameter("sty"), 0) ;
List<String> tree_nids = Convert.splitStrWith(tree_nids_str, ",|") ;
DTNode dn = null ;
if(Convert.isNotNullEmpty(tree_nid))
{
	dn = tree.findNodeById(tree_nid) ;
	if(dn==null)
	{
		out.print("no tree node found") ;
		return ;
	}
}

String pn_id = request.getParameter("pn_id");
DTNode pn_node = null ;
if(Convert.isNotNullEmpty(pn_id))
{
	DTNode tmpnd = tree.findNodeById(pn_id) ;
	if(tmpnd==null || !(tmpnd instanceof DTNode))
	{
		out.print("no pn node grp found") ;
		return ;
	}
	pn_node = (DTNode)tmpnd ;
}


String parttp_uid = request.getParameter("parttp_uid") ;
String part_id = request.getParameter("part_id") ;

String name = request.getParameter("name") ;
String title = request.getParameter("title") ;
String desc = request.getParameter("desc") ;
String jarr_str = request.getParameter("jarr") ;
String jstr = request.getParameter("jstr") ;
JSONArray input_jarr = null ;
JSONObject input_jo = null ;
if(Convert.isNotNullEmpty(jarr_str))
	input_jarr = new JSONArray(jarr_str) ;
if(Convert.isNotNullEmpty(jstr))
	input_jo = new JSONObject(jstr) ;

DTTreeRenderCtrl ctrl = new DTTreeRenderCtrl() ;

JSONObject tmpjo = null ;
StringBuilder failedr = new StringBuilder() ;
try
{
switch(op)
{
case "treen":
	if(!Convert.checkReqEmpty(request, out,"treeid"))
		return ;
	
	if(Convert.isNotNullEmpty(tree_nid))
	{
		if(dn instanceof DTNode)
		{
			JSONArray jarr = ((DTNode)dn).renderToTreeSub(ctrl) ;
			if(jarr==null)
				return ;
			jarr.write(out) ;
		}
	}
	else
	{
		JSONObject jo = tree.rendAsRootNode4JsTree(ctrl) ;
		if(jo==null)
			return ;
		jo.write(out) ;
	}
	
	return ;
case "load_tree":
	if(!Convert.checkReqEmpty(request, out,"treeid"))
		return ;
	tree.renderOut(out);
	return ;
case "add":
case "edit":
	if(!Convert.checkReqEmpty(request, out,"title"))
		return ;

		if(Convert.isNullOrEmpty(treeid))
		{
			DTTree ntree = DTTreeManager.getInstance().addTree(title, desc);
			treeid=  ntree.getTreeId();
		}
		else
		{
			tree.asBasic(title,desc) ;
			tree.save();
		}
		out.print("succ="+treeid) ;
	
	break;
case "del":
	if(!Convert.checkReqEmpty(request, out,"treeid"))
		return ;
	DTTreeManager.getInstance().delTreeById(treeid) ;
	out.print("succ") ;
	break ;
case "set_node_title":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid","title"))
		return ;
	if(tree.setNodeTitle(tree_nid, title,failedr)!=null)
		out.print("succ") ;
	else
		out.print(failedr) ;
	return;
case "add_sub_grp":
	if(!Convert.checkReqEmpty(request, out,"treeid"))
		return ;
	DTNode newn = null;
	DTTree.NodeAddWay way = DTTree.NodeAddWay.fromInt(add_sty) ;
		newn = tree.addNode(tree_nid,title, desc,way,failedr) ;
		if(newn!=null)
			out.print("succ="+newn.getNodeId()) ;
		else
			out.print(failedr) ;
	return ;
case "edit_node":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid","title"))
		return ;

	DTNode updn = tree.updateNode(tree_nid, title, desc) ;
	if(updn!=null)
		out.print("succ") ;
	else
		out.print("edit node failed") ;
	return ;
case "del_node":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid"))
		return ;

	if(tree.delNode(tree_nid,failedr)!=null)
		out.print("succ") ;
	else
		out.print(failedr) ;
	return ;
case "del_node_by_ids":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nids"))
		return ;

	if(tree.delNodeByIds(tree_nids).size()>0)
		out.print("succ") ;
	else
		out.print("del nodes failed") ;
	return ;
case "mv_node_to":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid","pn_id"))
		return ;
	boolean b_copy = "true".equals(request.getParameter("copy")) ;
	if(!b_copy && !dn.canBeMove(failedr))
	{
		out.print(failedr) ;
		return;
	}
	
	if(b_copy)
		dn = new DTNode(tree,null,dn,false,!dn.canBeMove(failedr),true,true) ;
	if(tree.setAppendChild(pn_id,dn, idx, failedr))
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	break;
case "copy_node":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid"))
		return ;
	copiedItem = new CopyItem(treeid,tree_nid) ;
	out.print("succ") ;
	break;
case "paste_node":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid"))
		return ;
	
	DTNode cpdn = null;
	if(copiedItem!=null)
		cpdn = copiedItem.getNode() ;
	if(cpdn==null)
	{
		out.print("no copied node found") ;
		return ;
	}
	cpdn = new DTNode(tree,null,cpdn,false,false,true,true) ;
	if(tree.setAppendChild(tree_nid,cpdn, idx, failedr))
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	break ;
case "set_node_by_part":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid","parttp_uid"))
		return ;
	boolean node_self = "true".equals(request.getParameter("node_self")) ;
	int num = Convert.parseToInt32(request.getParameter("num"), -1) ;
	DTNode retnd = tree.setPartToNode(tree_nid, node_self, parttp_uid, part_id,num, failedr) ;
	if(retnd==null)
	{
		out.print(failedr);return;
	}
	out.print("succ") ;
	break ;
case "set_static_data":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid","jstr"))
		return ;
	if(!tree.setStaticDataByJO(tree_nid, input_jo, failedr))
		out.print(failedr);
	else
		out.print("succ");
	break;
case "set_node_tags":
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid","jarr"))
		return ;
	if(!tree.setNodeTagsByJO(tree_nid, input_jarr, failedr))
		out.print(failedr);
	else
		out.print("succ");
	break ;
}
}
catch(Exception ee)
{
	out.print(ee.getMessage());
	ee.printStackTrace();
}
%>