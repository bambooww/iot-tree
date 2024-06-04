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
if(!Convert.checkReqEmpty(request, out, "path"))
	return;
boolean b_multi = "true".equals(request.getParameter("multi")) ;
//String op = request.getParameter("op");
String path=request.getParameter("path");

boolean w_only = "true".equalsIgnoreCase(request.getParameter("w_only")) ;
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
%><html>
<head>
<title>context tags lister</title>
<style>
		table{border-collapse:collapse;}
		body,td{font-size:12px;cursor:default;}
		body,th{font-size:13px;cursor:default;}
</style>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script>
	dlg.resize_to(700,500) ;
</script>
</head>
<body marginwidth="0" marginheight="0" margin="0">
<%

%>
<span id="updt"></span><span id="log_inf"></span>
<div style="float:left;overflow: auto;height: 90%;width:99%">
<table width='100%' border='1' height0="100%">
 <tr height0='20'>
  <th width='2%'></th>
  <th width='30%'><wbt:g>tag</wbt:g></th>
  <th width='40%'><wbt:g>title</wbt:g></th>

  <th width='6%'><wbt:g>val,type</wbt:g></th>
  <th width='5%'><wbt:g>write</wbt:g></th>
 </tr>
<%
	for(UATag tg : tags)
	{
		if(w_only && !tg.isCanWrite())
			continue ;
		String tagid = tg.getId() ;
		String pathn = tg.getNodeCxtPathIn(ntags) ;
		String patht =  tg.getNodeCxtPathTitleIn(ntags) ;
		//pathn = pathn.substring(parent_p.length()) ;
		String chked = "" ;
		
%>
 <tr id="row_<%=pathn %>" tagid="<%=tagid %>" onmouseover="mouseover(this)" onmouseout="mouseout(this)" onclick="clk_sel(this)">
  <td><input type="checkbox" class="chk" id="chk_<%=tagid %>" tagid="<%=tagid %>" path="<%=pathn %>"  <%=chked %>/></td>
  <td><%=pathn %></td>
  <td><%=patht %></td>
  
  <td><%=tg.getValTp() %></td>
  <td><%=tg.isCanWrite()?"✔":"" %></td>
  </tr>
<%
	}
%>
</table>
</div>
</body>
<script>
var path="<%=path%>" ;
var rowbgcolor = '#ffffff';
var b_multi = <%=b_multi%>;

var selected_tagids = dlg.get_opener_opt("sel_tagids") ;
if(!selected_tagids)
	selected_tagids=[] ;

function init()
{
	$(".chk").each(function(){
		let tagid = $(this).attr('tagid') ;
		if(selected_tagids.indexOf(tagid)>=0)
			$(this).prop("checked",true) ;
	});
}

init();

function get_selected_tagids()
{
	let ret=[] ;
	$(".chk").each(function(){
		if($(this).prop("checked"))
		{
			let tagid = $(this).attr('tagid') ;
			ret.push(tagid) ;
		}
	});
	return ret ;
}

function get_selected_tagpaths()
{
	let ret=[] ;
	$(".chk").each(function(){
		if($(this).prop("checked"))
		{
			let tagid = $(this).attr('path') ;
			ret.push(tagid) ;
		}
	});
	return ret ;
}

function get_selected_tagtxt()
{
	let ret="" ;
	$(".chk").each(function(){
		if($(this).prop("checked"))
		{
			let p = $(this).attr('path') ;
			ret += p +"\r\n" ;
		}
	});
	return ret ;
}

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
	let tagid = $(sel).attr("tagid") ;
	if(!tagid) return ;
	if(b_multi)
	{
		let ob = $("#chk_"+tagid) ;
		let chked = ob.prop("checked") ;
		ob.prop("checked",!chked) ;
	}
	else
	{
		$(".chk").each(function(){
				$(this).prop("checked",false) ;
		});
		$("#chk_"+tagid).prop("checked",true) ;
	}
}


function log(s)
{
	document.getElementById('log_inf').innerHTML = s ;
}
	
</script>
</html>