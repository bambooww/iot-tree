<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.util.web.*,
	org.iottree.core.util.xmldata.*
"%>
<%@ taglib uri="wb_tag" prefix="wbt"%><%
	File licf = new File("./license.txt") ;
	String lictxt = "" ;
	boolean lic_ok=false;
	if(!licf.exists())
	{
		lictxt = "Deleting the license terms is not allowed" ;
	}
	else
	{
		lictxt = Convert.readFileTxt(licf, "utf-8") ;
		lic_ok = true;
	}
%><!DOCTYPE html>
<html>
<head>
<title>IOT Tree Server</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
</head>
<style>

</style>
<body>
  <div id="header" style="white-space:nowrap;top:0;width:100%;height:70px;background-color: #dfdfdf">
  	<img src="../inc/logo3.png"/>  <span style="font-size:large;">IOT-Tree Server     License Terms</span>
  </div>
  <pre id="term_txt" style="margin:0 auto;margin-top:10px;width:90%;height:500px;text-align:left; border:1px solid #F00;overflow:scroll">
  <%=lictxt %>
</pre>
<div style="text-align: center">
<%
if(lic_ok)
{
%>
  <button onclick="do_accept()">Accept</button>
  <button>Decline</button>
<%
}
%>
</div>
</body>
<script type="text/javascript">

function do_accept()
{
	send_ajax("license_terms_ajax.jsp",{},function(bsucc,ret){
		if(!bsucc&&ret!="ok")
		{
			dlg.msg(ret) ;
			return ;
		}
		document.location.href="login.jsp";
	});
}

function fit()
{
	var h = $(window).height()-140;
	$("#term_txt").css("height",h+"px");
}

$(window).resize(function(){
	fit() ;
});

fit();
</script>
</html>
