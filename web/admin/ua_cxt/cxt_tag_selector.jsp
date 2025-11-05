<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
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
	
UANode n = null;
	n = UAUtil.findNodeByPath(path);//rep.findNodeById(id) ;
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
UANodeOCTags r_ntags = (UANodeOCTags)n ;
List<UANodeOCTags> ntags_list = r_ntags.listSelfAndSubTagsNode();
//List<UATag> tags = r_ntags.listTagsAll() ;

String parent_p = r_ntags.getNodePathName() ;
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
	dlg.resize_to(800,600) ;
</script>
<style type="text/css">
.row_ntags {background-color: #03a8bf;}
</style>
</head>
<body marginwidth="0" marginheight="0" margin="0">
<%

%>
<span id="updt"></span><span id="log_inf"></span>
<input  id="search_txt"/> 
<select id="search_unit">
	<option value=""> --- </option>
<%
	for(ValUnit vu:ValUnit.values())
	{
		String vun = vu.name() ;
%><option value="<%=vun%>"><%=vu.getUnit()%> <%=vu.getTitle() %></option><%
	}
%>
</select> <button>Search</button>

<div style="float:left;overflow: auto;height: 90%;width:99%">

<table width='100%' border='1' height0="100%">
 <tr height0='20'>
  <th width='2%'></th>
  <th width='20%'><wbt:g>tag</wbt:g></th>
  <th width='20%'><wbt:g>addr</wbt:g></th>
  <th width='30%'><wbt:g>title</wbt:g></th>
  <th width='6%'><wbt:g>val,type</wbt:g></th>
  <th width='6%'><wbt:g>unit</wbt:g></th>
  <th width='5%'><wbt:g>write</wbt:g></th>
 </tr>
<%
for(UANodeOCTags ntags:ntags_list)
{
	String subpath = ntags.getNodeCxtPathIn(r_ntags) ;
%>
<tr class="row_ntags">
  <td colspan="8">&nbsp;<%=subpath%>
  <button id="btn_<%=subpath %>" subpath="<%=subpath %>"  onclick="sel_all_or_not(this,'<%=subpath %>',false)">All children</button>
  <button id="btn_<%=subpath %>" subpath="<%=subpath %>"  onclick="sel_all_or_not(this,'<%=subpath %>',true)">All offspring</button> 
  </td>
  </tr>
<%
	List<UATag> tags = ntags.listTags() ;
	for(UATag tg : tags)
	{
		if(w_only && !tg.isCanWrite())
			continue ;
		String tagid = tg.getId() ;
		String pathn = tg.getNodeCxtPathIn(r_ntags) ;
		String patht =  tg.getNodeCxtPathTitleIn(r_ntags) ;
		//pathn = pathn.substring(parent_p.length()) ;
		String chked = "" ;
		String addr = tg.getAddress() ;
%>
 <tr id="row_<%=pathn %>" tagid="<%=tagid %>" onmouseover="mouseover(this)" onmouseout="mouseout(this)" onclick="clk_sel(this)">
  <td><input type="checkbox" class="chk" id="chk_<%=tagid %>" tagid="<%=tagid %>" path="<%=pathn %>" patht="<%=patht %>"  sub="<%=subpath %>" <%=chked %>/></td>
  <td><%=pathn %></td>
  <td><%=addr %></td>
  <td><%=patht %></td>
  <td><%=tg.getValTp() %></td>
  <td><%=tg.getUnit() %></td>
  <td><%=tg.isCanWrite()?"✔":"" %></td>
  </tr>
<%
	}
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
	
var selected_tagpaths = dlg.get_opener_opt("sel_tagpaths") ;
if(!selected_tagpaths)
	selected_tagpaths=[] ;

function init()
{
	$(".chk").each(function(){
		let tagid = $(this).attr('tagid') ;
		let tagp = $(this).attr('path') ;
		if(selected_tagids.indexOf(tagid)>=0)
			$(this).prop("checked",true) ;
		else if(selected_tagpaths.indexOf(tagp)>=0)
			$(this).prop("checked",true) ;
	});
}

init();

function sel_all_or_not(btnele,subpath,b_offspring)
{
	let bsel_all = !($(btnele).attr("seled")||false) ;
	$(btnele).attr("seled",bsel_all) ;
	if(!b_offspring)
	{
		$(".chk").each(function(){
			let ob = $(this) ;
			if(ob.attr("sub")==subpath)
				ob.prop("checked",bsel_all) ;
		}) ;
		return ;
	}
	//
	$(".chk").each(function(){
		let ob = $(this) ;
		let sub = ob.attr("sub") ;
		if(sub==subpath || sub.indexOf(subpath+".")==0 ||subpath==".")
			ob.prop("checked",bsel_all) ;
	}) ;
}

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

function get_selected_tags()
{
	let ret=[] ;
	$(".chk").each(function(){
		if($(this).prop("checked"))
		{
			let tagid = $(this).attr('tagid') ;
			let tagp = $(this).attr('path') ;
			let patht= $(this).attr('patht') ;
			ret.push({tagid:tagid,tagp:tagp,tagt:patht}) ;
		}
	});
	return ret ;
}

//function get_sel_tagpaths()

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