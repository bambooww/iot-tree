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
	String hmipath = null ;
	switch(tp)
	{
	case "subp":
		if(!Convert.checkReqEmpty(request, out, "hmi_path","sub_path"))
			return;
		hmipath = request.getParameter("hmi_path") ;
		hmi =  UAUtil.findHmiByPath(hmipath) ;
		if(hmi==null)
		{
			out.print("no path hmi found") ;
			return ;
		}
		String subpath = request.getParameter("sub_path") ;
		String fp = hmi.getParentNode().getNodePath()+"/"+subpath ;
		UAHmi sub_hmi = UAUtil.findHmiByPath(fp) ;
		if(sub_hmi==null)
		{
			out.print("no sub hmi found") ;
			return ;
		}
		
		
		UAHmi rb_hmi = (UAHmi)sub_hmi.getRefBranchNode();
		String rb_path = "" ;
		if(rb_hmi!=null)
		{
			rb_path = rb_hmi.getNodePath() ;
			sub_hmi = rb_hmi ;
		}
		
		String txt = sub_hmi.loadHmiUITxt() ;
		//System.out.println("{\"hmipath\":\""+np+"\",\"refpath\":\""+refpath_cxt+"\"}\r\n") ;
		out.print("{\"path\":\""+fp+"\",\"rb_path\":\""+rb_path+"\"}\r\n") ;
		out.print(txt);
		break ;
	case "sub":
		if(!Convert.checkReqEmpty(request, out, "hmi_path","sub_id"))
			return;
		hmipath = request.getParameter("hmi_path") ;
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
		
		UANode bn = hmi.getRefBranchNode();
		UANode subn = null;
		String refpath_cxt = null;
		if(bn!=null)
		{
			refpath_cxt = hmi.getParentNode().getNodePath();
			subn = bn.getParentNode().findNodeById(subid) ;
		}
		else
		{
			//branchn = hmi.getRefBranchNode() ;
			subn = hmi.getParentNode().findNodeById(subid) ;
			refpath_cxt = subn.getParentNode().getNodePath();
		}
		//if(subn==null&&branchn!=null)
		//	subn = branchn.getParentNode().findNodeById(subid) ;
		if(subn==null || !(subn instanceof UAHmi))
		{
			out.print("no sub hmi found") ;
			return ;
		}
		hmi = (UAHmi)subn ;
		branchn = hmi.getRefBranchNode() ;
		String refid = null ;
		UAHmi branchhmi = null ;
		if(branchn!=null)
		{
			hmi = (UAHmi)branchn ;
		}
		String np = hmi.getNodePath() ;
		txt = hmi.loadHmiUITxt() ;
		//System.out.println("{\"hmipath\":\""+np+"\",\"refpath\":\""+refpath_cxt+"\"}\r\n") ;
		out.print("{\"hmipath\":\""+np+"\",\"refpath\":\""+refpath_cxt+"\"}\r\n") ;
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