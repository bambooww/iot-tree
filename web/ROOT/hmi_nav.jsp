<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				org.json.*,
				org.iottree.core.*,
				org.iottree.core.store.record.*,
				org.iottree.core.ui.*,
				org.iottree.core.res.*,
				org.iottree.core.alert.*,
				org.iottree.core.store.*,
				org.iottree.core.util.web.*,
				org.iottree.core.plugin.*,
	org.iottree.core.util.*,org.iottree.core.station.*,
	org.iottree.core.comp.*,
				java.net.*"%><%@ taglib uri="wb_tag" prefix="lan"%><%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
   boolean b_inplat = PlatformManager.isInPlatform() ;
   String user = request.getParameter("user") ;
   String tp = request.getParameter("tp") ;
   if(Convert.isNullOrEmpty(user))
	   user="" ;
   
   if(Convert.isNullOrEmpty(tp))
	   tp="nav1" ;
	//String op = request.getParameter("op");
	String path = request.getParameter("path");
	UANode uanode = UAUtil.findNodeByPath(path) ;
	if(uanode==null || !(uanode instanceof UAPrj))
	{
		out.print("no prj node found") ;
		return ;
	}
	
	UAPrj prj = (UAPrj)uanode ;
	String prjid = "" ;
	String prjname = "" ;
	prjid = prj.getId() ;
	prjname = prj.getName() ;
	
	PrjNavTree navtree = prj.getPrjNavTree(tp) ;
	if(navtree==null)
	{
		out.print("no nav tree set") ;
		return ;
	}
	
	int maxDeep = navtree.getMaxDeep() ;
	JSONObject treejo = navtree.toJO() ;
%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title><%=prj.getTitle() %></title>
 <jsp:include page="head.jsp">
 	<jsp:param value="true0" name="oc"/>
 </jsp:include>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
}

th
{
	border:1px solid;
}
.top {
	position: fixed;
	
	left: 0;
	top: 0;
	bottom: 0;
	z-index: 999;
	height: 45px;
	width:100%;
	text-align: left;
	margin:0px;
	padding:0px;
	overflow: hidden;
	background-color: #515658;
}


.main {
	position: absolute;
	left: 0;
	top: 45px;
	bottom: 0px;
	width: 100%;
	overflow: hidden
}

.nav1
{
	position: relative;
	width:100px;
	height:40px;
	float:left;font-size:medium;
	padding-top0:8px;
	display: flex;
	line-height:1.1;
	 justify-content: center;align-items: center;
	text-align:center;
	vertical-align:middle;
	border:1px solid #6eb0f7;
	color:#6eb0f7;
	margin:3px;
	cursor: pointer;
}

.nav1:hover
{
	background-color: #2893c9;
}

.nav2
{
	position: relative;
	width:100px;
	height:40px;
	float:left;font-size:medium;
	padding-top0:8px;
	display: flex;
	line-height:1.1;
	 justify-content: center;align-items: center;
	text-align:center;
	vertical-align:middle;
	border:1px solid #6eb0f7;
	color:#6eb0f7;
	margin:3px;
	cursor: pointer;
}

.nav2:hover
{
	background-color: #2893c9;
}

.sel
{
	background-color: #2893c9;
	color:yellow;
}
</style>
</head>
<script type="text/javascript">
dlg.dlg_top=true;

var b_platform = <%=b_inplat%>;
</script>
<body class="layout-body">
<div class="top" id="top_nav1">

</div>
<%
if(maxDeep>1)
{
%>
<div class="top" id="top_nav2" style="top:45px;">

</div>
<%
}
%>
<div class="main" style="top:<%=(45*maxDeep)%>px;">
	<iframe id="if_main" style="border:0px;width:100%;height:100%;overflow: hidden;"></iframe>
</div>
<script>

var firstu = "" ;
var prjid = "<%=prjid%>" ;
var max_deep = <%=maxDeep%>;
var treejo = <%=treejo%> ;

layui.use('form', function(){

});

var cur_nav1 = null ;
var cur_nav2 = null ;

function get_nav1(nav)
{
	for(let ni of treejo.navs)
	{
		if(nav==ni.n)
			return ni ;
	}
	return null ;
}

function get_nav2(nav1,nav2)
{
	let n1 = get_nav1(nav1) ;
	if(!n1) return null ;
	for(let ni of n1.navs)
	{
		if(nav2==ni.n)
			return ni ;
	}
	return null ;
}

function update_ui()
{
	$(".nav1").removeClass("sel") ;
	if(cur_nav1)
	{
		$("#nav1_"+cur_nav1).addClass("sel") ;
	}
	
	$(".nav2").removeClass("sel") ;
	if(cur_nav2)
	{
		$("#nav2_"+cur_nav2).addClass("sel") ;
	}
}

function show_nav1()
{
	let tmps = "" ;
	let firstn = "" ;
	for(let ni of treejo.navs)
	{
		tmps += `<div id="nav1_\${ni.n}" class="nav1" nav="\${ni.n}" lvl="1" nav_u="\${ni.u}" onclick="on_nav_clk(this)">\${ni.t}</div>` ;
		if(!firstn)
			firstn = ni.n ;
	}
	$("#top_nav1").html(tmps) ;
	
	on_nav_clk($("#nav1_"+firstn)[0]) ;
}

function show_nav2()
{
	if(max_deep<=1) return ;
	if(!cur_nav1) return ;
	let n1 = get_nav1(cur_nav1) ;
	if(!n1) return ;
	
	let tmps = "" ;
	let firstn = "" ;
	if(n1.navs)
	{
		for(let ni of n1.navs)
		{
			tmps += `<div id="nav2_\${ni.n}" class="nav2" nav="\${ni.n}" lvl="2" nav_u="\${ni.u}" onclick="on_nav_clk(this)">\${ni.t}</div>` ;
			if(!firstn)
				firstn = ni.n ;
		}
	}
	
	$("#top_nav2").html(tmps) ;
	
	if(firstn)
		on_nav_clk($("#nav2_"+firstn)[0]) ;
}

function on_nav_clk(ele)
{
	let ob = $(ele) ;
	let lvl = parseInt(ob.attr("lvl")) ;
	let nn = ob.attr("nav") ;
	let uu = ob.attr("nav_u") ;
	
	if(uu)
		$("#if_main").attr("src",uu);
	
	if(lvl==1)
	{
		cur_nav1 = nn ;
		show_nav2() ;
	}
	else if(lvl==2)
	{
		cur_nav2 = nn ;
	}

	update_ui();
}


show_nav1();
</script>
</body>
</html>