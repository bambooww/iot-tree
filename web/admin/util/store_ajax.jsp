<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.json.*,
	org.iottree.core.store.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*
	"%><%!

%><%if(!Convert.checkReqEmpty(request, out, "prjid","op"))
	return ;

String op = request.getParameter("op");
String prjid = request.getParameter("prjid");
StoreManager stmgr= StoreManager.getInstance(prjid) ;
if(stmgr==null)
{
	out.print("no store manager found") ;
	return ;
}
String n = request.getParameter("n") ;
String classid = request.getParameter("cid") ;
String nname = request.getParameter("name") ;

UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}

switch(op)
{
case "set_store":
	String jstr = request.getParameter("jstr") ;
	try
	{
		JSONObject jo = new JSONObject(jstr) ;
		//String tmpid = jo.optString("id") ;
		//boolean badd = Convert.isNullOrEmpty(tmpid) ;
		SourceJDBC st = new SourceJDBC();
		DataTranserJSON.injectJSONToObj(st, jo) ;
		stmgr.setSource(st, true,false);
		out.print("succ") ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "list_stores":
	List<Source> sors = stmgr.listSources() ;
	JSONArray jarr = new JSONArray() ;
	for(Source sor:sors)
	{
		JSONObject jo = sor.toListJO() ;
		jarr.put(jo) ;
	}
	jarr.write(out) ;
	break ;
case "del_sor":
	if(!Convert.checkReqEmpty(request, out, "n"))
		return ;
	if(stmgr.delSource(n))
	{
		out.print("succ") ;
		return ;
	}
	out.print("delete source failed") ;
	break;
case "dc_imp_txt":
	if(!Convert.checkReqEmpty(request, out, "txt"))
		return ;
	
	return ;
case "export":
	if(!Convert.checkReqEmpty(request, out, "taskid"))
		return ;
	
	break;
case "edit_dn":
	if(!Convert.checkReqEmpty(request, out,"name"))
		return ;
	break ;
}%>