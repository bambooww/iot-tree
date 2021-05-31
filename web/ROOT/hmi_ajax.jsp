<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%	
	String tp = request.getParameter("tp");
	if(tp==null)
		tp= "" ;
	UAHmi hmi = null ;
	UANode branchn =null;
	switch(tp)
	{
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
		branchn = hmi.getRefBranchNode() ;
		UANode subn = hmi.getParentNode().findNodeById(subid) ;
		if(subn==null&&branchn!=null)
			subn = branchn.getParentNode().findNodeById(subid) ;
		if(subn==null || !(subn instanceof UAHmi))
		{
			out.print("no sub hmi found") ;
			return ;
		}
		hmi = (UAHmi)subn ;
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
		break ;
	case "rt":
		if(!Convert.checkReqEmpty(request, out, "path"))
			return;
		String path=request.getParameter("path");
		UANode tmpn = UAUtil.findNodeByPath(path);
		if(tmpn==null)
		{
			out.print("no node found") ;
			return ;
		}
		UANodeOCTagsCxt ntags = null ;
		if(tmpn instanceof UAHmi)
		{
			ntags = ((UAHmi)tmpn).getBelongTo() ;
		}
		else if(tmpn instanceof UANodeOCTagsCxt)
		{
			ntags = (UANodeOCTagsCxt)tmpn ;
		}
		else
		{
			return ;
		}
		
		ntags.CXT_renderJson(out);
		break ;
	default:
		if(!Convert.checkReqEmpty(request, out, "path"))
			return;
		path=request.getParameter("path");
		hmi = (UAHmi)UAUtil.findNodeByPath(path);//.findHmiById(hmiid) ;
		if(hmi==null)
		{
			out.print("no hmi found") ;
			return ;
		}
		branchn = hmi.getRefBranchNode();
		if(branchn!=null&&branchn instanceof UAHmi)
			hmi = (UAHmi)branchn ;
		txt = hmi.loadHmiUITxt() ;
		out.print(txt);
		break ;
	}
	

	
%>