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
String ricid = request.getParameter("ricid") ;
String roaid = request.getParameter("roaid") ;
String id = request.getParameter("id") ;
String nname = request.getParameter("name") ;

String fid = request.getParameter("fid") ;
String tid = request.getParameter("tid") ;
String js = request.getParameter("js") ;
boolean js_en = "true".equals(request.getParameter("en_js")) ;
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
RouterManager rmgr= RouterManager.getInstance(prj) ;
RouterInnCollator ric = null ;
if(Convert.isNotNullEmpty(ricid))
{
	ric = rmgr.getInnerCollatorById(ricid) ;
	if(ric==null)
	{
		out.print("no ric found with id="+ricid) ;
		return ;
	}
}
RouterOuterAdp roa = null ;
if(Convert.isNotNullEmpty(roaid))
{
	roa = rmgr.getOuterAdpById(roaid) ;
	if(roa==null)
	{
		out.print("no roa found with id="+roaid) ;
		return ;
	}
}

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
	
	if(rmgr.CONN_RIC_setConnJS(fid, tid, js_en,js)!=null)
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
	
	if(rmgr.CONN_ROA_setConnJS(fid, tid, js_en,js)!=null)
		out.print("succ") ;
	else
		out.print("set failed") ;
	return ;
case "ric_debug_trigger_data":
	if(!Convert.checkReqEmpty(request, out, "prjid","id"))
		return ;
	ric = rmgr.getInnerCollatorById(id) ;
	if(ric==null)
	{
		out.print("no ric found with id="+id) ;
		return ;
	}
	failedr = new StringBuilder() ;
	if(!ric.DEBUG_triggerOutData(failedr))
		out.print(failedr.toString()) ;
	else
		out.print("trigger ok") ;
	return ;
case "ric_debug_join_data":
	if(!Convert.checkReqEmpty(request, out, "id","name"))
		return ;
	boolean bout = "true".equals(request.getParameter("out")) ;
	ric = rmgr.getInnerCollatorById(id) ;
	if(ric==null)
	{
		out.print("no ric found with id="+id) ;
		return ;
	}
	RouterJoin jj = null;
	if(bout)
		jj = ric.getJoinOutByName(nname) ;
	else
		jj = ric.getJoinInByName(nname) ;
	if(jj==null)
	{
		out.print("no join found with name "+nname) ;
		return ;
	}
	long ldt = jj.RT_getLastDT() ;
	RouterObj ro = jj.RT_getLastData() ; 
	String ldd = "" ;
	if(ro!=null)
		ldd = ro.getTxt() ;
	JSONObject tmpjo = new JSONObject() ;
	tmpjo.put("dt",ldt) ;
	tmpjo.put("d",ldd) ;
	tmpjo.write(out) ;
	return ;
case "roa_debug_join_data":
	if(!Convert.checkReqEmpty(request, out, "id","name"))
		return ;
	bout = "true".equals(request.getParameter("out")) ;
	roa = rmgr.getOuterAdpById(id) ;
	if(roa==null)
	{
		out.print("no roa found with id="+id) ;
		return ;
	}
	jj = null;
	if(bout)
		jj = roa.getJoinOutByName(nname) ;
	else
		jj = roa.getJoinInByName(nname) ;
	if(jj==null)
	{
		out.print("no join found with name "+nname) ;
		return ;
	}
	ldt = jj.RT_getLastDT() ;
	ro = jj.RT_getLastData() ; 
	ldd = "" ;
	if(ro!=null)
		ldd = ro.getTxt() ;
	tmpjo = new JSONObject() ;
	tmpjo.put("dt",ldt) ;
	tmpjo.put("d",ldd) ;
	tmpjo.write(out) ;
	return ;
case "rt_inf": //monitor alert output
	jo = rmgr.RT_getRunInf() ;
	jo.write(out) ;
	break ;
case "debug_start_stop":
	if(ric!=null)
	{
		if(ric.RT_isRunning())
		{
			ric.RT_stop() ;
			out.print("succ=stop ok") ;
			return ;
		}
		else
		{
			ric.RT_start() ;
			out.print("succ=start ok") ;
			return ;
		}
	}
	
	if(roa!=null)
	{
		if(roa.RT_isRunning())
		{
			roa.RT_stop() ;
			out.print("succ=stop ok") ;
			return ;
		}
		else
		{
			roa.RT_start() ;
			out.print("succ=start ok") ;
			return ;
		}
	}
	out.print("no router node ") ;
	/*
	boolean bstart = "true".equals(request.getParameter("start")) ;
	if(bstart)
	{
		if(ric!=null)
			ric.RT_start() ;
		else if(roa!=null)
			roa.RT_start() ;
	}
	else
	{
		if(ric!=null)
			ric.RT_stop() ;
		else if(roa!=null)
			roa.RT_stop() ;
	}
	out.print("succ") ;
	*/
	return ;
}%>