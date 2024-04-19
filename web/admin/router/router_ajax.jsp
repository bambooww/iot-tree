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
	org.iottree.core.comp.*
	"%><%!

%><%if(!Convert.checkReqEmpty(request, out, "prjid","op"))
	return ;

String op = request.getParameter("op");
String prjid = request.getParameter("prjid");
String id = request.getParameter("id") ;
String nname = request.getParameter("name") ;

String fid = request.getParameter("fid") ;
String tid = request.getParameter("tid") ;
String jstxt = request.getParameter("jstxt") ;
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
RouterManager rmgr= RouterManager.getInstance(prj) ;

switch(op)
{
case "list_full":
	JSONObject jo = rmgr.UTIL_toJOFull() ;
	jo.write(out) ;
	break ;
case "list_ric":
	JSONArray jarr = rmgr.UTIL_RIC_toJarr() ;
	jarr.write(out) ;
	break ;
case "list_ric_conns":
	jarr = rmgr.UTIL_CONN_RIC_toJarr() ;
	jarr.write(out) ;
	break ;
case "edit_ric":
case "add_ric":
	try
	{
		if(!Convert.checkReqEmpty(request, out,  "jstr"))
			return ;
		String jstr = request.getParameter("jstr") ;
		jo = new JSONObject(jstr) ;
		rmgr.setInnerCollatorByJSON(jo) ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "del_ric":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	if(rmgr.delInnerCollatorById(id))
	{
		out.print("succ") ;
	}
	else
	{
		out.print("delete out failedr") ;
	}
	break ;
case "list_roa":
	jarr = rmgr.UTIL_ROA_toJarr() ;
	jarr.write(out) ;
	break ;
case "list_roa_conns":
	jarr = rmgr.UTIL_CONN_ROA_toJarr() ;
	jarr.write(out) ;
	break ;
case "edit_roa":
case "add_roa":
	try
	{
		if(!Convert.checkReqEmpty(request, out,  "jstr"))
			return ;
		String jstr = request.getParameter("jstr") ;
		jo = new JSONObject(jstr) ;
		rmgr.setOuterAdpByJSON(jo) ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "del_roa":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	if(rmgr.delOuterAdpById(id))
	{
		out.print("succ") ;
	}
	else
	{
		out.print("delete out adapter failedr") ;
	}
	break ;
case "ric_set_conn":
	if(!Convert.checkReqEmpty(request, out,  "fid","tid"))
		return ;
	StringBuilder failedr = new StringBuilder() ;
	if(rmgr.CONN_RIC_setConn2ROA(fid, tid, failedr)==null)
	{
		out.print(failedr.toString()) ;
		return ;
	}
	out.print("succ") ;
	break ;
case "ric_set_trans_js":
	if(!Convert.checkReqEmpty(request, out,  "fid","tid"))
		return ;
	
	if(rmgr.CONN_RIC_setConnJS(fid, tid, jstxt)!=null)
		out.print("succ") ;
	else
		out.print("set failed") ;
	return ;
case "ric_del_conn":
	if(!Convert.checkReqEmpty(request, out,  "fid","tid"))
		return ;
	if(rmgr.CONN_RIC_delConn(fid, tid)==null)
	{
		out.print("no conn deleted") ;
		return ;
	}
	out.print("succ") ;
	break ;
case "roa_set_conn":
	if(!Convert.checkReqEmpty(request, out,  "fid","tid"))
		return ;
	failedr = new StringBuilder() ;
	if(rmgr.CONN_ROA_setConn2RIC(fid, tid, failedr)==null)
	{
		out.print(failedr.toString()) ;
		return ;
	}
	out.print("succ") ;
	break ;
case "roa_set_trans_js":
	if(!Convert.checkReqEmpty(request, out,  "fid","tid"))
		return ;
	
	if(rmgr.CONN_ROA_setConnJS(fid, tid, jstxt)!=null)
		out.print("succ") ;
	else
		out.print("set failed") ;
	return ;
case "out_mon": //monitor alert output
	break ;
}%>