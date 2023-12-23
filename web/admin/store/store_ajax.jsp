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
String hid = request.getParameter("hid") ;
String id = request.getParameter("id") ;
//String nname = request.getParameter("name") ;

StoreManager stmgr= null;
StoreHandler shandler = null ;
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

	if(Convert.isNotNullEmpty(hid))
		shandler = stmgr.getHandlerById(hid) ;
}

switch(op)
{
case "set_sor":
	String jstr = request.getParameter("jstr") ;
	try
	{
		JSONObject jo = new JSONObject(jstr) ;
		StoreManager.setSourceByJO(jo) ; 
		//SourceJDBC st = new SourceJDBC();
		//DataTranserJSON.injectJSONToObj(st, jo) ;
		//StoreManager.setSource(st, true,false);
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
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	if(StoreManager.delSourceById(id))
	{
		out.print("succ") ;
		return ;
	}
	out.print("delete source failed") ;
	break;
case "test_sor":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	Source sor = StoreManager.getSourceById(id) ;
	if(sor==null)
	{
		out.print("no source found with id="+id) ;
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
case "list_h":
	List<StoreHandler> shs = stmgr.listHandlers() ;
	jarr = new JSONArray() ;
	for(StoreHandler sh:shs)
	{
		JSONObject jo = sh.toJO() ;
		jarr.put(jo) ;
	}
	jarr.write(out) ;
	break ;
case "set_h":
	if(!Convert.checkReqEmpty(request, out, "prjid","jstr"))
		return ;
	jstr = request.getParameter("jstr") ;
	try
	{
		JSONObject jo = new JSONObject(jstr) ;
		stmgr.setHandlerByJSON(jo);//
		out.print("succ") ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break;
case "set_h_sel_tagids":
	if(!Convert.checkReqEmpty(request, out, "prjid","hid","idstr"))
		return ;
	String idstr = request.getParameter("idstr") ;
	try
	{
		List<String> tagids = Convert.splitStrWith(idstr, ",") ;
		stmgr.setHandlerSelTagIds(hid, tagids);//
		out.print("succ") ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break;
case "del_h":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	if(stmgr.delHandlerById(id))
	{
		out.print("succ") ;
		return ;
	}
	out.print("delete handler failed") ;
	break ;
case "set_o":
	if(!Convert.checkReqEmpty(request, out, "prjid","jstr"))
		return ;
	jstr = request.getParameter("jstr") ;
	try
	{
		JSONObject jo = new JSONObject(jstr) ;
		stmgr.setHandlerOutByJSON(jo);//
		out.print("succ") ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break;
case "init_o":
	if(!Convert.checkReqEmpty(request, out, "prjid","hid","id"))
		return ;
	StoreOut sout = shandler.getOutById(id);
	if(sout==null)
	{
		out.print("no out found") ;
		return ;
	}
	failedr = new StringBuilder() ;
	if(sout.initOut(failedr))
		out.print("succ") ;
	else
		out.print(failedr.toString()) ;
	break ;
case "del_o":
	if(!Convert.checkReqEmpty(request, out, "prjid","hid","id"))
		return ;
	
	if(stmgr.delHandlerOutById(hid,id)!=null)
	{
		out.print("succ") ;
		return ;
	}
	out.print("delete handler failed") ;
	break ;
case "rt_data":
	JSONObject tmpjo = stmgr.RT_toJO() ;
	tmpjo.write(out) ;
	break;
}

%>