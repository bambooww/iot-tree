<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.json.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.alert.*,
	org.iottree.core.dict.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "prjid","op"))
	return ;

String op = request.getParameter("op");
String prjid = request.getParameter("prjid");
String id = request.getParameter("id") ;
String nname = request.getParameter("name") ;

UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
AlertManager amgr= AlertManager.getInstance(prjid) ;

switch(op)
{
case "list_h":
	JSONArray jarr = new JSONArray() ;
	for(AlertHandler ao:amgr.getHandlers().values())
	{
		JSONObject tmpjo = ao.toJO() ;
		jarr.put(tmpjo) ;
	}
	jarr.write(out) ;
	break ;
case "edit_h":
case "add_h":
	try
	{
		if(!Convert.checkReqEmpty(request, out,  "jstr"))
			return ;
		String jstr = request.getParameter("jstr") ;
		JSONObject jo = new JSONObject(jstr) ;
		amgr.setHandlerByJSON(jo) ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "del_h":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	if(amgr.delHandlerById(id))
	{
		out.print("succ") ;
	}
	else
	{
		out.print("delete out failedr") ;
	}
	break ;
case "save_h_ids":
	try
	{
		if(!Convert.checkReqEmpty(request, out,  "jstr"))
			return ;
		String jstr = request.getParameter("jstr") ;
		
		jarr = new JSONArray(jstr) ;
		amgr.setHandlerInOutIds(jarr) ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break ;
case "list_o":
	jarr = new JSONArray() ;
	for(AlertOut ao:amgr.getOuts().values())
	{
		JSONObject tmpjo = ao.toJO() ;
		jarr.put(tmpjo) ;
	}
	jarr.write(out) ;
	break ;
case "edit_o":
case "add_o":
	
	try
	{
		if(!Convert.checkReqEmpty(request, out,  "jstr"))
			return ;
		String jstr = request.getParameter("jstr") ;
		JSONObject jo = new JSONObject(jstr) ;
		amgr.setOutByJSON(jo) ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "del_o":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	if(amgr.delOutById(id))
	{
		out.print("succ") ;
	}
	else
	{
		out.print("delete out failedr") ;
	}
	break ;
case "out_mon": //monitor alert output
	break ;
}
%>