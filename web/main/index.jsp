<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
"%><%
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Self Main Page Demo</title>
  <meta name="renderer" content="webkit">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="viewport" content="width=device-width, initial-scale=1">
<script src="/_js/jquery-1.12.0.min.js"></script>
<link href="./inc/1.css" rel="stylesheet" />
</head>
<style>

</style>
<body style="overflow: hidden;">
<div class="top_nav">
	<span class="nav_item " style="background-color: #2d363c;width:300px;">IOT-Tree Main Demo</span>
	<span class="nav_item" id="ni_iottree" onclick="nav_to('iottree')">Monitor1</span>
	<span class="nav_item" id="ni_u1"  onclick="nav_to('u1')">Monitor2</span>
	<span class="nav_item" id="ni_nav1"  onclick="nav_to('tb')">Tables</span>
	<span class="nav_item" id="ni_nav2"  onclick="nav_to('st')">Stat.</span>
</div>
 <div class="main" id="main">
</div>
<script>

function nav_to(p)
{
	$(".nav_item").removeClass("nav_sel") ;
	$("#ni_"+p).addClass("nav_sel") ;
	switch(p)
	{
	case "iottree":
		$("#main").html(`<iframe src="/watertank/iottree" style="width:100%;height:100%;overflow:hidden;border:0px;"></iframe>`) ;
		return ;
	case "u1":
		$("#main").html(`<iframe src="/watertank/u1"  style="width:100%;height:100%;overflow:hidden;border:0px;"></iframe>`) ;
		return ;
	case "tb":
		$("#main").html(`Your table data here:<br><table style="width:80%"><tr><td>1</td><td>2</td><td>3</td></tr></table>`) ;
		return ;
	case "st":
		$("#main").html(`Your stat data here`) ;
		return  ;
	default:
		alert('navigate to '+p) ;
		return ;
	}
}

nav_to('iottree');
</script>
</body>
</html>
