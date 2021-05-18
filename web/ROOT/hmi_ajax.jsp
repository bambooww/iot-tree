<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
	if(!Convert.checkReqEmpty(request, out, "tp"))
		return;
	String tp = request.getParameter("tp");
	
	UAHmi hmi = null ;
	switch(tp)
	{
	case "rep":
		if(!Convert.checkReqEmpty(request, out, "rootid","id"))
			return;
		String rootid = request.getParameter("rootid") ;
		String id=request.getParameter("id");
		
		UARep rep = UAManager.getInstance().getRepById(rootid) ;
		if(rep==null)
		{
			out.print("no rep found");
			return ;
		}
		hmi = rep.findHmiById(id) ;
		break ;
	case "devdef":
		if(!Convert.checkReqEmpty(request, out, "rootid","id"))
			return;
		rootid = request.getParameter("rootid") ;
		id=request.getParameter("id");
		DevDef dd = DevManager.getInstance().getDevDefById(rootid);
		if(dd==null)
		{
			out.print("no rep found");
			return ;
		}
		hmi = (UAHmi)dd.findNodeById(id) ;
		break ;
	case "sub":
		if(!Convert.checkReqEmpty(request, out, "hmi_path","sub_id"))
			return;
		String hmipath = request.getParameter("hmi_path") ;
		String subid = request.getParameter("sub_id") ;
		hmi = UAUtil.findHmiByPath(hmipath) ;
		if(hmi==null)
		{
			out.print("no path hmi found") ;
			return ;
		}
		if(subid.equals(hmi.getId()))
		{
			out.print("illegal hmi_path and sub_id") ;
			return ;
		}
		UANode branchn = hmi.getRefBranchNode() ;
		UANode subn = hmi.getParentNode().findNodeById(subid) ;
		if(subn==null&&branchn!=null)
			subn = branchn.getParentNode().findNodeById(subid) ;
		if(subn==null || !(subn instanceof UAHmi))
		{
			out.print("no sub hmi found") ;
			return ;
		}
		hmi = (UAHmi)subn ;
		break ;
	default:
		return ;
	}

	if(hmi==null)
	{
		out.print("no hmi found") ;
		return ;
	}
	UANode refn = hmi.getRefBranchNode() ;
	String refid = null ;
	if(refn!=null)
	{
		hmi = (UAHmi)refn ;
	}
	String np = hmi.getNodePath() ;
	String txt = hmi.loadHmiUITxt() ;
	out.print(np+"\r\n") ;
	out.print(txt);
%>


