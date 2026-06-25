<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.devtree.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out,"op"))
	return ;
String op = request.getParameter("op");
String treeid = request.getParameter("treeid");
String tree_nid = request.getParameter("tree_nid");
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

//  0 - sub node , 1 - sibling  2 - sibling head 
int add_sty = Convert.parseToInt32(request.getParameter("sty"), 0) ;

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

String title = request.getParameter("title") ;
String desc = request.getParameter("desc") ;
DTTreeRenderCtrl ctrl = new DTTreeRenderCtrl() ;

try
{
switch(op)
{
case "treen":
	if(!Convert.checkReqEmpty(request, out,"treeid"))
		return ;
	
	if(Convert.isNotNullEmpty(tree_nid))
	{
		if(dn instanceof DTNodeGrp)
		{
			JSONArray jarr = ((DTNodeGrp)dn).renderToTreeSub(ctrl) ;
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
	if(tree.setNodeTitle(tree_nid, title)!=null)
		out.print("succ") ;
	else
		out.print("set title failed") ;
	return;
case "add_sub_grp":
	if(!Convert.checkReqEmpty(request, out,"treeid"))
		return ;
	DTNode newn = null;
	DTTree.NodeAddWay way = DTTree.NodeAddWay.fromInt(add_sty) ;
		newn = tree.addNodeGrp(tree_nid,title, desc,way) ;
		if(newn!=null)
			out.print("succ="+newn.getNodeId()) ;
		else
			out.print("add node failed") ;
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

	if(tree.delNode(tree_nid)!=null)
		out.print("succ") ;
	else
		out.print("del node failed") ;
	return ;
case "chg":
	break;

case "list":
	
	break ;
}
}
catch(Exception ee)
{
	out.print(ee.getMessage());
}
%>