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
	
	String syslan = Lan.getSysLang() ;
	if(syslan==null)
		syslan="" ;
%><!DOCTYPE html>
<html>
<head>
<title>IOT Tree Server</title>
<jsp:include page="../head.jsp"></jsp:include>
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
  <button onclick="do_accept()" class="layui-btn"><wbt:lang>accept</wbt:lang></button>
  <button class="layui-btn layui-btn-primary layui-border"><wbt:lang>decline</wbt:lang></button>
<%
}
%>
</div>
</body>
<script type="text/javascript">

var sysln = "<%=syslan%>" ;

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

function set_sys_lan()
{
	dlg.open("lan_sel.jsp",{title:"Select Language"},
				['Cancel'],
				[
					function(dlgw)
					{
						dlg.close();
					}
				],(ret)=>{
					if(!ret)
						return ;
					ret.op="set_lan" ;
					send_ajax("login_ajax.jsp",ret,(bsucc,rr)=>{
						if(!bsucc || rr!='succ')
						{
							dlg.msg(rr);
							return ;
						}
						document.location.href=document.location.href ;
					}) ;
				});
}

if(!sysln)
{
	set_sys_lan();
}
</script>
</html>
