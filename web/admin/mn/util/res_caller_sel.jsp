<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.*,
	org.iottree.core.*,org.iottree.core.msgnet.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%>	
<%

String uid = request.getParameter("uid") ;
List<ResCat> rescats = MNManager.listRegisteredResCats() ;
%><html>
<head>
<title></title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style>
		table{border-collapse:collapse;}
		body,td{font-size:12px;cursor:default;}
.left {position: absolute;left:0px;top:0px;width:40%;bottom:0px;border:1px solid;overflow-y:auto}
.right  {position: absolute;right:0px;top:0px;width:60%;bottom:0px;;overflow-y:auto}
.cat {margin:2px;width:95%;height:20px;border:1px solid #ccc;border-radius: 3px;}
.cat .t {font-weight:bold;}
.cat:hover {background-color: grey;}
.caller {margin:2px;width:95%;height:20px;border:1px solid #ccc;border-radius: 3px;}
.caller .t {font-weight:bold;}
.caller:hover {background-color: grey;}
.sel {background-color: grey;}
</style>
<script>
	dlg.resize_to(800,600) ;
</script>
</head>
<body marginwidth="0" marginheight="0" margin="0">
<div class="left">
<%
for(ResCat cat:rescats)
{
	String n = cat.getName() ;
	String t = cat.getTitle() ;
%><div class="cat" onclick="sel_cat('<%=n%>',this)">
	<span class="t"><%=t %></span>[<span class="n"><%=n %></span>]
</div><%
}
%>
</div>
<div id="right" class="right">
</div>
</body>
<script>
var cur_res_cat = "" ;
var cur_callers = [] ;

var cur_caller = "" ;
var cur_caller_t = "" ;
function update_list()
{
	let ss = "" ;
	for(let c of cur_callers)
	{
		ss += `<div class="caller" onclick="sel_caller('\${c.name}','\${c.title}',this)">
			<span class="t">\${c.title}</span>[<span class="n">\${c.name}</span>]
			</div>`;
	}
	$("#right").html(ss) ;
	cur_caller="" ;
	cur_caller_t = "" ;
}

function sel_caller(name,title,div)
{
	cur_caller = name ;
	$(".caller").removeClass('sel') ;
	$(div).addClass("sel") ;
	
	cur_caller = name ;
	cur_caller_t = title ;
}



function sel_cat(n,div)
{
	$(".cat").removeClass('sel') ;
	$(div).addClass("sel") ;
	
	send_ajax("res_caller_ajax.jsp",{op:"res_caller_list",res_cat:n},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("[")!=0)
		{
			dlg.msg(ret);return;
		}
		let res = null ;
		eval("res="+ret) ;
		cur_res_cat = n ;
		cur_callers = res ;
		update_list() ;
	}) ;
}

function get_selected()
{
	if(!cur_res_cat || !cur_caller)
	{
		return "<wbt:g>pls,select</wbt:g> Caller"
	}
	return {uid:cur_res_cat+"."+cur_caller,title:cur_caller_t};
}

function log(s)
{
	document.getElementById('log_inf').innerHTML = s ;
}
	
</script>
</html>