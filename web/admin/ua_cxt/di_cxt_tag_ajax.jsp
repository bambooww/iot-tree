<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!
%><%
if(!Convert.checkReqEmpty(request, out, "path"))
	return;
boolean bind_tag_only = "true".equalsIgnoreCase(request.getParameter("bind_tag_only")) ;

String search_txt = request.getParameter("search_txt") ;
if(search_txt==null)
{
	search_txt ="";
}
session.setAttribute("_di_tag_sel_stxt", search_txt) ;

String val = request.getParameter("val") ;
if(val==null)
	val = "" ;
//String op = request.getParameter("op");
String path=request.getParameter("path");
String propv = "" ;
if(Convert.isNotNullEmpty(val))
{
	int k = val.lastIndexOf('.') ;
	if(k>0)
	{
		propv = val.substring(k+1) ;
		val = val.substring(0,k); 
	}
}

	
	//String repname = rep.getName() ;
	
UANode n = UAUtil.findNodeByPath(path);//rep.findNodeById(id) ;
if(n==null)
{
	out.print("no node found") ;
	return ;
}
if(n instanceof UAHmi)
	n = n.getParentNode() ;
if(!(n instanceof UANodeOCTags))
{
	out.print("not node oc tags") ;
	return ;
}
UANodeOCTags ntags = (UANodeOCTags)n ;
List<UATag> tags = ntags.listTagsAll() ;

String parent_p = ntags.getNodePathName() ;
if(Convert.isNotNullEmpty(parent_p))
	parent_p +="." ;
boolean bdlg = "true".equalsIgnoreCase(request.getParameter("dlg"));

	for(UATag tg : tags)
	{
		String tagid = tg.getId() ;
		String pathn = tg.getNodeCxtPathIn(ntags) ;
		String patht =  tg.getNodeCxtPathTitleIn(ntags) ;
		String addr0 = tg.getAddress() ;
		if(addr0==null)
			addr0="" ;
		String vt = tg.getValTp().toString() ;
		if(Convert.isNotNullEmpty(search_txt))
		{
			if(!pathn.contains(search_txt) && ! patht.contains(search_txt) && !addr0.contains(search_txt))
				continue ;
		}
		//pathn = pathn.substring(parent_p.length()) ;
		String chked = "" ;
		if(pathn.equals(val))
			chked = "checked='checked'" ;
		String addr = tg.getAddress() ;
%>
 <tr id="row_<%=pathn %>" height0='1' style0="height:5" onmouseover="mouseover(this)" onmouseout="mouseout(this)" 
 	onclick="clk_sel(this)" tagp="<%=pathn%>" tagt="<%=patht%>" tagid="<%=tagid%>" vt="<%=vt%>">
  <td><input type="checkbox" id="cb_<%=pathn %>"  <%=chked %>/></td>
  <td><%=pathn %></td>
  <td><%=addr %></td>
  <td><%=patht %></td>
  <td><%=tg.getValTp() %></td>
  </tr>
<%
	}
%>