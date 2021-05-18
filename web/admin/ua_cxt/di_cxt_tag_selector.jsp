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
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%>	
<%
	if(!Convert.checkReqEmpty(request, out, "repid","id"))
		return;
	
	String val = request.getParameter("val") ;
	if(val==null)
		val = "" ;
	//String op = request.getParameter("op");
	String repid=request.getParameter("repid");
	String id = request.getParameter("id") ;
	UARep rep = UAManager.getInstance().getRepById(repid) ;
	if(rep==null)
	{
		out.print("no rep found");
		return ;
	}
	
	String repname = rep.getName() ;
	
	UANode n = rep.findNodeById(id) ;
	if(n==null)
	{
		out.print("no node found") ;
		return ;
	}
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
	%>
<html>
<head>
<title>context tags lister</title>
<style>
		table{border-collapse:collapse;}
		body,td{font-size:12px;cursor:default;}
</style>

<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/ajax.js" ></script>
<script src="/_js/dlg.js" ></script>
<script>
	
</script>
</head>
<body marginwidth="0" marginheight="0" margin="0">
<b>Context:<%=rep.getTitle() %>] / [<%=ntags.getNodePathName() %>] </b>
<%

%>
<span id="updt"></span><span id="log_inf"></span>
<div style="overflow: auto;height: 90%">
<table width='100%' border='1' height0="100%">
 <tr height0='20'>
  <td width='2%'></td>
  <td width='15%'>Path Name</td>
  <td width='15%'>Title</td>

  <td width='6%'>Value Type</td>
 </tr>
<%
	
	for(UATag tg : tags)
	{
		String pathn = tg.getMemberPathName();
		pathn = pathn.substring(parent_p.length()) ;
		String chked = "" ;
		if(pathn.equals(val))
			chked = "checked='checked'" ;
		
%>
 <tr id="row_<%=pathn %>" height0='1' style0="height:5" onmouseover="mouseover(this)" onmouseout="mouseout(this)" onclick="clk_sel(this)">
  <td><input type="checkbox" id="cb_<%=pathn %>"  <%=chked %>/></td>
  <td><%=pathn %></td>
  <td><%=tg.getMemberPathTitle() %></td>
  
  <td><%=tg.getValTp() %></td>
  </tr>
<%
	}
%>
</table>
</div>

</body>
<script>
var repid="<%=repid%>" ;
var repname = "<%=repname%>" ;
var id = "<%=id%>" ;
var rowbgcolor = '#ffffff';
var selVal = "<%=val%>" ;
function mouseover(sel)
{
 rowbgcolor = sel.style.backgroundColor;
 sel.style.backgroundColor='#dddddd';
}
function mouseout(sel)
{
 sel.style.backgroundColor=rowbgcolor;
}

function clk_sel(sel)
{
	var rowid = sel.id ;
	if(rowid==undefined||rowid==null)
		return ;
	var id = rowid.substring(4); 
	for(var inp of document.getElementsByTagName("input"))
	{
		inp.checked=false;
	}
	document.getElementById("cb_"+id).checked=true;
	selVal = id ;
}

function get_val()
{
	return selVal ;
}

function log(s)
{
	document.getElementById('log_inf').innerHTML = s ;
}
	
</script>
</html>