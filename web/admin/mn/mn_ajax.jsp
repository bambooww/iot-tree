<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.json.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.router.*,
	org.iottree.core.dict.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.msgnet.*
	"%><%!

%><%if(!Convert.checkReqEmpty(request, out,"prjid","op"))
	return ;

String op = request.getParameter("op");
String prjid = request.getParameter("prjid");
String netid = request.getParameter("netid") ;
String nodeid = request.getParameter("nodeid") ;
String moduleid= request.getParameter("moduleid") ;
String name = request.getParameter("name") ;
String title = request.getParameter("title") ;
String desc = request.getParameter("desc") ;
String fulltp = request.getParameter("fulltp") ;

String tp = request.getParameter("tp") ;
float x = Convert.parseToFloat(request.getParameter("x"),0) ;
float y = Convert.parseToFloat(request.getParameter("y"),0) ;

UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}

MNManager mnm= MNManager.getInstance(prj) ;
MNNet net = null;
MNNode node=  null ;
MNModule module = null ;
if(Convert.isNotNullEmpty(netid))
{
	net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	if(Convert.isNotNullEmpty(nodeid))
	{
		
		node = net.getNodeById(nodeid) ;
		if(node==null)
		{
			out.print("no node found with id="+nodeid) ;
			return ;
		}
		
	}
	
	if(Convert.isNotNullEmpty(moduleid))
	{
		module = net.getModuleById(moduleid) ;
		if(module==null)
		{
			out.print("no module found with id="+moduleid) ;
			return ;
		}
	}
}

String jstr = request.getParameter("jstr") ;
JSONObject in_jo = null ;
if(Convert.isNotNullEmpty(jstr))
	in_jo = new JSONObject(jstr) ;

StringBuilder failedr = new StringBuilder() ;
switch(op)
{
case "add_edit_net":
	if(!Convert.checkReqEmpty(request, out, "name"))
		return ;
	try
	{
		if(Convert.isNullOrEmpty(netid))
			mnm.createNewNet(name, title, desc) ;
		else
			mnm.updateNet(netid, name, title, desc);
		out.print("succ") ;
	}
	catch(Exception e)
	{
		e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	return ;
case "del_net":
	if(!Convert.checkReqEmpty(request, out, "netid"))
		return ;
	try
	{
		mnm.delNet(netid);
		out.print("succ") ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	return ;
case "load_net":
	if(!Convert.checkReqEmpty(request, out, "netid"))
		return ;
	net.renderOut(out);
	return ;
case "net_save_basic": //只保存布局信息
	if(!Convert.checkReqEmpty(request, out, "netid","jstr"))
		return ;
	
	if(!net.updateBasicByJO(in_jo, failedr))
		out.print(failedr.toString()) ;
	else
		out.print("succ") ;
	return ;
case "node_add_up":
	if(!Convert.checkReqEmpty(request, out,"netid", "tp","x","y"))
		return ;
	MNNode rnn = net.createNewNodeByFullTP(tp, x, y,moduleid) ;
	if(rnn==null)
	{
		out.print("create new node error") ;
		return ;
	}
	out.print("succ") ;
	return ;
case "module_add_up":
	if(!Convert.checkReqEmpty(request, out,"netid", "tp","x","y"))
		return ;
	MNModule mnn = net.createNewModuleByFullTP(tp, x, y) ;
	if(mnn==null)
	{
		out.print("create new module error") ;
		return ;
	}
	out.print("succ") ;
	return ;
case "detail_set":
	if(!Convert.checkReqEmpty(request, out,"netid", "jstr"))
		return ;
	if(Convert.isNotNullEmpty(nodeid))
		net.setDetailJO(nodeid,in_jo,true,true) ;
	else if(Convert.isNotNullEmpty(moduleid))
		net.setDetailJO(nodeid,in_jo,false,true) ;
		
	out.print("succ") ;
	return ;
case "module_detail_set":
	if(!Convert.checkReqEmpty(request, out,"netid", "moduleid","jstr"))
		return ;
	net.setDetailJO(nodeid,in_jo,false,true) ;
	out.print("succ") ;
	return ;
case "module_list_nodes":
	if(!Convert.checkReqEmpty(request, out,"netid","moduleid"))
		return ;
	JSONArray jarr = new JSONArray() ;
	for(MNNode tmpn:module.listSupportedNodes())
	{
		jarr.put(tmpn.toJO()) ;
	}
	jarr.write(out) ;
	return ;
case "conn_set":
	if(!Convert.checkReqEmpty(request, out,"netid", "out_id","to_nid"))
		return ;
	String out_id = request.getParameter("out_id") ;
	String to_nid = request.getParameter("to_nid") ;
	
	MNConn conn = net.addConn(out_id, to_nid,failedr) ;
	if(conn!=null)
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	return ;
case "del_by_ids":
	if(!Convert.checkReqEmpty(request, out,"netid", "ids"))
		return ;
	String ids = request.getParameter("ids") ;
	List<String> idlist = Convert.splitStrWith(ids, ",") ;
	int r = net.delItemsByIds(idlist) ;
	if(r>0)
		out.print("succ") ;
	else
		out.print("no_item_del") ;
	return ;
case "node_start_trigger":
	if(!Convert.checkReqEmpty(request, out,"netid", "nodeid"))
		return ;

	boolean b = net.RT_triggerNodeStart(nodeid, failedr);
	if(b)
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	return;
default:
	out.print("unknown op") ;
	return ;
}%>