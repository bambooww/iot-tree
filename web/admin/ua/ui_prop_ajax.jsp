<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page import="java.util.*,
				java.io.*,
				java.util.*,
				org.json.*,
				org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				java.net.*"%><%!
	
%><%
/*
	if(!Convert.checkReqEmpty(request, out, "repid","id","op"))
		return ;
    String op = request.getParameter("op");
	String repid = request.getParameter("repid") ;
	String id = request.getParameter("id") ;
	UAManager uam = UAManager.getInstance();
	UARep dc = uam.getRepById(repid) ;
	if(dc==null)
	{
		out.print("no rep found with id="+repid) ;
		return ;
	}
	UANode n = dc.findNodeById(id);
	if(n==null)
	{
		out.print("no node with id="+id) ;
		return ;
	}
	*/
	if(!Convert.checkReqEmpty(request, out, "path","op"))
		return ;
	//boolean bdev = "true".equals(request.getParameter("bdev")) ;
	boolean bmgr ="true".equals(request.getParameter("mgr")) ;
	String path = request.getParameter("path") ;
	String op = request.getParameter("op");
	UANode n = UAUtil.findNodeByPath(path) ;
	if(n==null)
	{
		out.print("no node found");
		return ;
	}
	
	if("load".equalsIgnoreCase(op))
	{
		JSONObject jobj = n.toPropNodeValJSON() ;
		String txt = jobj.toString();
		out.print(txt) ;
		return ;
	}
	
	if("save".equalsIgnoreCase(op))
	{
		try
		{
			String txt = request.getParameter("txt") ;
			JSONObject jobj = new JSONObject(txt);
			n.fromPropNodeValJSON(jobj);
			((ISaver)n.getTopNode()).save();
			out.print("succ");
			return ;
		}
		catch(Exception e)
		{
			out.print(e.getMessage()) ;
			return ;
		}
	}
%>
err:unknow op