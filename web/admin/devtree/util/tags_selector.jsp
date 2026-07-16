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
boolean b_multi = "true".equals(request.getParameter("multi")) ;
String prjn = request.getParameter("prjn") ;
String search_txt = request.getParameter("search_txt") ;
if(search_txt==null)
	search_txt = "" ;
List<UAPrj> prjs = UAManager.getInstance().listPrjs() ;
if(prjs==null||prjs.size()<=0)
{
	out.print("no local project") ;return ;
}
UAPrj prj = null ;
String path=null;
if(Convert.isNullOrEmpty(prjn))
{
	prj = prjs.get(0) ;
	prjn = prj.getName() ;
}
else
{
	prj = UAManager.getInstance().getPrjByName(prjn) ;
	if(prj==null)
	{
		out.print("no prj with name found ["+prjn+"]") ;return ;
	}
}

path = "/"+prj.getName() ;

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
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
	dlg.resize_to(800,600) ;
</script>
<style type="text/css">
.row_ntags {background-color: #ccc;}
table {table-layout: fixed;width:100%;border:1px solid #ccc;}
td {border:1px solid #ccc;}
.tagc {padding-top:1px;padding-bottom: 1px;text-align: right;}
.tagb {height:100%;font-size:16px;}
.tc {white-space: nowrap;overflow: hidden;text-overflow: ellipsis;width: 100%;}
.topbb {height:30px;width:100%;}
.topbb td {border:0px;}
.topbb button,input,select {border:1px solid #ccc;}
</style>
</head>
<body marginwidth="0" marginheight="0" margin="0">
<table class="topbb" style="border:0px;">
 <tr><td>
<select id="prjs" onchange="on_prj_chg()" style="height:28px;width:100%">
<%
	for(UAPrj p:prjs)
	{
		String sel = prjn.equals(p.getName())?"selected":"" ;
%><option value="<%=p.getName()%>" <%=sel %>><%=p.getTitle() %></option><%
	}
%>
	</select>
	</td><td width="120px">
<input  id="search_txt" style="height:28px;width:100%;" value="<%=search_txt %>"  onkeydown="on_search_key(event)"/>
 </td>
  <td width="28px"> 
<button style="height:28px;width:27px;" onclick="do_search()" ><i class="fa fa-search"></i></button>
  </td>
  <td width="31px"> 
<button style="height:28px;width:27px;" onclick="do_search(true)" ><i class="fa fa-eraser"></i></button>
  </td>
  </tr>
</table>
<div style="float:left;overflow-x:hidden;width:99%;position:absolute;top:30px;bottom:0px;">
<table height0="100%">
 <tr height0='20'>
  <th width='5%'></th>
  <th width='50%'><wbt:g>tag</wbt:g></th>
  <th width='12%'><wbt:g>addr</wbt:g></th>
  <th width='10%'><wbt:g>type</wbt:g></th>
  <th width='8%'><wbt:g>unit</wbt:g></th>
  <th width='5%'>w</th>
  <th width='10%'>&nbsp;</th>
 </tr>
<%
for(UANodeOCTags ntags:ntags_list)
{
	String subpath = ntags.getNodeCxtPathIn(r_ntags) ;
	List<UATag> tags = ntags.listTags() ;
	if(Convert.isNotNullEmpty(search_txt))
	{
		ArrayList<UATag> ttts = new ArrayList<>() ;
		for(UATag tg : tags)
		{
			if(w_only && !tg.isCanWrite())
				continue ;
			//String t = tg.getTitle() ;
			String fullp = tg.getNodePathCxt();
			String patht =  Convert.plainToHtml(tg.getNodeCxtPathTitleIn(r_ntags)) ;
			String addr = tg.getAddress() ;
				boolean b_fit=false;
				if(fullp.indexOf(search_txt)>=0)
					b_fit = true ;
				if(!b_fit && patht.indexOf(search_txt)>=0)
					b_fit = true ;
				if(!b_fit && Convert.isNotNullEmpty(addr) && addr.indexOf(search_txt)>=0)
					b_fit = true ;
				if(!b_fit)
					continue ;
				ttts.add(tg) ;
		}
		tags = ttts ;
	}
	if(tags.size()<=0)
		continue ;
%>
<tr class="row_ntags">
  <td colspan="8">&nbsp;<%=subpath%>
  <%--
  <button id="btn_<%=subpath %>" subpath="<%=subpath %>"  onclick="sel_all_or_not(this,'<%=subpath %>',false)">All children</button>
  <button id="btn_<%=subpath %>" subpath="<%=subpath %>"  onclick="sel_all_or_not(this,'<%=subpath %>',true)">All offspring</button>
   --%> 
  </td>
  </tr>
<%
	for(UATag tg : tags)
	{
		if(w_only && !tg.isCanWrite())
			continue ;
		String tagid = tg.getId() ;
		String t = tg.getTitle() ;
		String pathn = tg.getNodeCxtPathIn(r_ntags) ;
		String patht =  Convert.plainToHtml(tg.getNodeCxtPathTitleIn(r_ntags)) ;
		String fullp = tg.getNodePathCxt();
		//String fullt = tg.getNodePathTitle() ;
		String vt = tg.getValTp().getStr() ;
		//pathn = pathn.substring(parent_p.length()) ;
		
		String addr = tg.getAddress() ;
%>
 <tr id="row_<%=pathn %>" tagid="<%=tagid %>" tagp="<%=fullp %>" tagt="<%=patht %>" tagvt="<%=vt %>" onmouseover="mouseover(this)" onmouseout="mouseout(this)" onclick="set_to_right(this)" >
  <td style="vertical-align: top;">
  	<%--<input type="checkbox" class="chk" id="chk_<%=tagid %>" tagid="<%=tagid %>" path="<%=pathn %>" patht="<%=patht %>"  sub="<%=subpath %>" vt="<%=vt %>" <%=chked %>/>
  	 --%>
  	</td>
  <td><div class="tc" title="<%=patht %>"><%=pathn %><br><%=patht %></div></td>
  <td><div class="tc"><%=addr %></div></td>
  <td><%=tg.getValTp().getStr() %></td>
  <td><%=tg.getUnit() %></td>
  <td><%=tg.isCanWrite()?"✔":"" %></td>
  <td class="tagc"></td>
  </tr>
<%
	}
}
%>
</table>
</div>
</body>
<script>
var prjn = "<%=prjn%>";
var search_txt = "<%=Convert.plainToJsStr(search_txt)%>";
var path="<%=path%>" ;
var rowbgcolor = '#ffffff';
var b_multi = <%=b_multi%>;

$("#search_txt").focus();

var selected_tagids = dlg.get_opener_opt("sel_tagids") ;
if(!selected_tagids)
	selected_tagids=[] ;
	
var selected_tagpaths = dlg.get_opener_opt("sel_tagpaths") ;
if(!selected_tagpaths)
	selected_tagpaths=[] ;

function do_search(b_clear)
{
	let stxt ="" ;
	if(b_clear)
		$("#search_txt").val("");
	else
		stxt = $("#search_txt").val()||"";
	location.href="tags_selector.jsp?prjn="+prjn+"&multi="+b_multi+"&search_txt="+stxt ;
}

function on_search_key(evt)
{
	if(evt.keyCode==13)
		do_search();
}

function on_prj_chg()
{
	prjn = $("#prjs").val();
	location.href="tags_selector.jsp?prjn="+prjn+"&multi="+b_multi ;
}

function get_prj_name()
{
	return prjn ;
}
	
function set_to_right(ele)
{
	let ob = $(ele) ;
	let fullp=ob.attr("tagp");
	let vt = ob.attr("tagvt");
	let patht = ob.attr("tagt");
	if(parent && parent.on_tag_set_to_node)
		parent.on_tag_set_to_node({tagp:fullp,tagvt:vt,tagt:patht});
}

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
		fire_tags_chged()
		return ;
	}
	//
	$(".chk").each(function(){
		let ob = $(this) ;
		let sub = ob.attr("sub") ;
		if(sub==subpath || sub.indexOf(subpath+".")==0 ||subpath==".")
			ob.prop("checked",bsel_all) ;
	}) ;
	fire_tags_chged()
}

function fire_tags_chged()
{
	let tags = get_selected_tags();
	
	if(parent && parent.on_tags_chged)
		parent.on_tags_chged(tags);
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
			let vt = $(this).attr('vt') ;
			ret.push({tagid:tagid,tagp:tagp,tagt:patht,vt:vt}) ;
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
	fire_tags_chged()
}


function log(s)
{
	document.getElementById('log_inf').innerHTML = s ;
}
	
</script>
</html>