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
String libid = request.getParameter("libid");

DTDevPartLib partlib = null ;
if(Convert.isNotNullEmpty(libid))
{
	partlib = DTDevPartManager.getInstance().getLibById(libid) ;
	if(partlib==null)
	{
		out.print("no part lib found with id="+libid) ;
		return ;
	}
}
String parttpid = request.getParameter("parttpid") ;
DTDevPartTP parttp = null ;
if(Convert.isNotNullEmpty(parttpid))
{
	parttp = partlib.getPartTP(parttpid) ;
	if(parttp==null)
	{
		out.print("no parttp found with id="+parttpid) ;
		return ;
	}
}
String name = request.getParameter("name") ;
String title = request.getParameter("title") ;
String desc = request.getParameter("desc") ;
String treeid = request.getParameter("treeid") ;
String tree_nid = request.getParameter("tree_nid") ;
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

case "load_tree":
	//if(!Convert.checkReqEmpty(request, out,"treeid"))
	//	return ;
	//tree.renderOut(out);
	return ;
case "add_partlib":
case "edit_partlib":
	if(!Convert.checkReqEmpty(request, out,"title"))
		return ;

		if(Convert.isNullOrEmpty(libid))
		{
			DTDevPartLib plib = DTDevPartManager.getInstance().addLib(title, desc);
			libid =  plib.getLibId();
		}
		else
		{
			partlib.asBasic(title,desc) ;
			partlib.save();
		}
		out.print("succ="+libid) ;
	
	break;
case "del_partlib":
	if(!Convert.checkReqEmpty(request, out,"libid"))
		return ;
	if(DTDevPartManager.getInstance().delLib(libid))
		out.print("succ") ;
	else
		out.print("del partlib failed") ;
	break ;
case "add_parttp":
case "edit_parttp":
	if(!Convert.checkReqEmpty(request, out,"libid","title"))
		return ;
	
	if(partlib.setPartTP(parttpid, title, desc, failedr)!=null)
		out.print("succ") ;
	else
		out.print(failedr) ;
	break ;
case "add_parttp_by_node":
	if(!Convert.checkReqEmpty(request, out,"libid","treeid","tree_nid"))
		return ;
	
	DTDevPartTP newtp = partlib.addPartTPByTreeNode(treeid, tree_nid,title, failedr) ;
	if(newtp!=null)
		out.print("succ="+newtp.getPartTpId()) ;
	else
		out.print(failedr) ;
	break ;
}
}
catch(Exception ee)
{
	out.print(ee.getMessage());
}
%>