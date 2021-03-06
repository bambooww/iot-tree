<%@ page contentType="text/json;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
				java.net.*"%>
<%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
	String mid_ext = request.getParameter("mid_ext") ;
	String tag_ext = request.getParameter("tag_ext") ;
	//String op = request.getParameter("op");
	String node_path = request.getParameter("path");
	String tp = request.getParameter("tp") ;
	if(tp==null)
		tp = "" ;
	UANode n = UAUtil.findNodeByPath(node_path) ;
	if(n==null)
	{
		out.print("no node found") ;
	}
	
	
	//UANodeOCTags ntags = (UANodeOCTags)n ;
	if(n instanceof UANodeOCTagsCxt)
	{
		UANodeOCTagsCxt ntcxt = (UANodeOCTagsCxt)n ;
		switch(tp)
		{
		case "tags_list":
			ntcxt.CXT_renderTagsJson(out, false, -1) ;
			break ;
		default:
			if(Convert.isNotNullEmpty(mid_ext)||Convert.isNotNullEmpty(tag_ext))
			{
				UANodeFilter.JSON_renderMidNodesWithTagsByExtName(out,ntcxt,mid_ext,tag_ext) ;
			}
			else
			{
				ntcxt.CXT_renderJson(out) ;
			}
			break ;
		}
		return ;
	}
	
	if(n instanceof UATag)
	{
		UATag tag = (UATag)n;
		tag.CXT_renderTagJson(out) ;
		return ;
	}
	
	out.print("unkown cxt") ;
%>