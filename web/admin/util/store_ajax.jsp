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

%><%if(!Convert.checkReqEmpty(request, out,"op"))
	return ;

String op = request.getParameter("op");
String prjid = request.getParameter("prjid");
String n = request.getParameter("n") ;
String classid = request.getParameter("cid") ;
String nname = request.getParameter("name") ;

StoreManager stmgr= null;

UAPrj prj = null;
if(Convert.isNotNullEmpty(prjid))
{
	prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	stmgr = StoreManager.getInstance(prjid) ;
	if(stmgr==null)
	{
		out.print("no store manager found") ;
		return ;
	}

}

switch(op)
{
case "set_sor":
	String jstr = request.getParameter("jstr") ;
	try
	{
		JSONObject jo = new JSONObject(jstr) ;
		//String tmpid = jo.optString("id") ;
		//boolean badd = Convert.isNullOrEmpty(tmpid) ;
		SourceJDBC st = new SourceJDBC();
		DataTranserJSON.injectJSONToObj(st, jo) ;
		StoreManager.setSource(st, true,false);
		out.print("succ") ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "list_sors":
	List<Source> sors = StoreManager.listSources() ;
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
	if(StoreManager.delSource(n))
	{
		out.print("succ") ;
		return ;
	}
	out.print("delete source failed") ;
	break;
case "test_sor":
	if(!Convert.checkReqEmpty(request, out, "n"))
		return ;
	Source sor = StoreManager.getSource(n) ;
	if(sor==null)
	{
		out.print("no source found with name="+n) ;
		return;
	}
	StringBuilder failedr = new StringBuilder() ;
	if(sor.checkConn(failedr))
	{
		out.print("Source connected successfully!") ;
		return ;
	}
	else
	{
		out.print(failedr.toString()) ;
	}
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