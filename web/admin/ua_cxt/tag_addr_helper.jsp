<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	org.json.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="lan"%><%!
	
%><%
if(!Convert.checkReqEmpty(request, out, "path"))
	return;
String path = request.getParameter("path") ;
UANode n = UAUtil.findNodeByPath(path);
if(n==null)
{
	out.print("no tag node with path="+path) ;
	return ;
}

if(n instanceof UAHmi || n instanceof UATag)
{
	n = n.getParentNode();
	path = n.getNodePath() ;
}
UANodeOCTags tn = (UANodeOCTags)n;
if(tn==null)
{
	out.print("no node with path="+path) ;
	return ;
}


//UATag tag = (UATag)n ;
UADev dev = tn.getBelongToDev() ;
if(dev==null)
{
	out.print("no belong to Device found") ;
	return ;
}
DevDriver dd = dev.getRelatedDrv() ;
DevDriver.Model  dm = dev.getDrvDevModel() ;
if(dm==null)
{
	out.print("no Device Driver Model found") ;
	return ;
}
List<DevAddr.IAddrDef> defs = dm.getAddrDefs() ;
if(defs==null)
{
	out.print("no Address Definition found") ;
	return ;
}
%> 
<html>
<head>
<title>Tag Address Helper </title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
</head>
<body>
 <blockquote class="layui-elem-quote ">
<%=dd.getTitle()%> <%=dm.getTitle() %> (<%=dm.getName() %>)<lan:g>addr</lan:g>
</blockquote>
<table class="layui-table">
  <colgroup>
    <col width="150">
    <col width="150">
    <col>
  </colgroup>
  <thead>
    <tr>
      <th width="30%"><lan:g>type</lan:g></th>
      <th width="25%"><lan:g>range</lan:g></th>
      <th width="15%"><lan:g>data,type</lan:g></th>
      <th width="10%"><lan:g>access</lan:g></th>
      <th width="20%"><lan:g>sample</lan:g></th>
    </tr> 
  </thead>
  <tbody>
  <%
  	for(DevAddr.IAddrDef def:defs)
    {
  	  String deft = def.getDefTypeForDoc() ;
  	  List<DevAddr.IAddrDefSeg> segs = def.getSegsForDoc() ;
  	  if(segs==null||segs.size()<=0)
  		  continue ;
  	  int s = segs.size() ;
  	DevAddr.IAddrDefSeg  seg = segs.get(0) ;
  %>
    <tr>
      <td rowspan="<%=s%>"><%=deft %></td>
      <td><%=seg.getRangeStr() %></td>
      <td><%=seg.getValTPsStr() %></td>
      <td><%=seg.getReadWriteStr() %></td>
      <td><%=seg.getSample() %></td>
    </tr>
<%
	for(int i = 1 ; i < s ; i ++)
	{
		seg = segs.get(i) ;
%>
    <tr>
      <td><%=seg.getRangeStr() %></td>
      <td><%=seg.getValTPsStr() %></td>
      <td><%=seg.getReadWriteStr() %></td>
      <td><%=seg.getSample() %></td>
    </tr>
<%
	}
  }
%>
  </tbody>
</table>
</body>
</html>