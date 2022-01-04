
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.cxt.*,
	org.iottree.core.ws.*,
	org.iottree.core.util.xmldata.*
"%><%
%><!DOCTYPE html>
<html class="">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width,minimum-scale=1,initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1">
    <title>IOT-Tree</title>
    <link href="/favicon.ico" rel="shortcut icon" type="image/x-icon">
    <script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js?v="></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
            <link href="./inc/common.css" rel="stylesheet" type="text/css">
        <link href="./inc/index.css" rel="stylesheet" type="text/css">
 <style>
 .btn_sh
 {
  //display:none;
  visibility: hidden;
 }
 
 .btn_sh_c:hover .btn_sh
 {
visibility: visible;
 }

 </style>
</head>
<body aria-hidden="false">
	<div class="iot-top-menu-wrap" >
		
			<div class="iot-logo" >
				<a style="width:300px"><img src="inc/logo1.png" width="40px" height="40px"/> IOT-Tree Server Document</a>
			</div>

			<div class="iot-top-nav navbar">
				<div class="navbar-header">
				  <button class="navbar-toggle pull-left">
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				  </button>
				</div>
				<nav role="navigation" class="collapse navbar-collapse bs-navbar-collapse">
				  <ul class="nav navbar-nav">
				      <li><a href="https://github.com/bambooww/iot-tree.git"  target="_blank" class=""><i class="icon icon-home"></i> Github</a></li>
					  <li><a href="javascript:set_lang('en')" ><i class="icon icon-home"></i> English</a></li>
					  <li><a href="javascript:set_lang('cn')" ><i class="icon icon-topic"></i> 中文</a></li>
				  </ul>
				</nav>
				
			</div>
			<div style="position: absolute;right:30px;">Version:<%=Config.getVersion() %></div>
	</div>
	<table style="width:100%;height:90%;">
		<tr>
			<td style="width:20%"><iframe id="nav" src="en/nav.md" style="width:100%;height:100%;overflow: auto"></iframe></td>
			<td style="width:80%"><iframe id="main" src="en/README.md" style="width:100%;height:100%;"></iframe></td>
		</tr>
	</table>

</body>
<script type="text/javascript">
function set_lang(lang)
{
	$("#nav").attr("src",lang+"/nav.md") ;
	var m = $("#main") ;
	var p = m.attr("src") ;
	var b_nochg=false;
	if(p==null||p=="")
		p = lang+"/README.md";
	else
	{
		if(p.indexOf(lang+"/")==0)
			b_nochg = true ;
		var k = p.indexOf('/') ;
		if(k>0)
			p = lang+"/"+p.substr(k+1) ; 
	}
	
	if(b_nochg)
		return;
	
	$("#main").attr("src",p) ;
}

function nav_to(p)
{
	$("#main").attr("src",p) ;
}
</script>
</html>
