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
	org.iottree.core.sim.*,
	org.iottree.core.util.xmldata.*
	"%>
<%@ taglib uri="wb_tag" prefix="wbt"%>	
<%
if(!Convert.checkReqEmpty(request, out, "insid"))
	return;

String insid = request.getParameter("insid") ;
SimInstance ins = SimManager.getInstance().getInstance(insid) ;
if(ins==null)
{
	out.print("no instance found");
	return ;
}
String jstp = request.getParameter("jstp") ;
if(jstp==null)
	jstp = "" ;
String tt = "" ;
switch(jstp)
{
case "init":
	tt = "Init script,run once when starting" ;
	break ;
case "run":
	tt = "Run script,run in loop" ;
	break ;
case "end":
	tt = "End script,run once when stopping" ;
	break ;
default:
	out.print("unknown jstp="+jstp) ;
	return ;
}
String opener_txt_id = request.getParameter("opener_txt_id") ;
if(opener_txt_id==null)
	opener_txt_id = "" ;
//UANode n = rep.findNodeById(id) ;


boolean bdlg = "true".equalsIgnoreCase(request.getParameter("dlg"));
%>
<html>
<head>
<title>context script</title>
<style>
		table{border-collapse:collapse;}
		body,td{font-size:12px;cursor:default;}
</style>
<jsp:include page="../head.jsp"></jsp:include>
<script>
	dlg.resize_to(600,500) ;
</script>
</head>
<body marginwidth="0" marginheight="0" margin="0">
<b > <%=tt %> </b>
<input type='button' value='Test Run' onclick="run_script_test('')" class="layui-btn layui-btn-sm layui-border-blue" />

<table border='1' style="height:90%;width:100%">
 <tr height="75%">
  <td colspan="2">
   <textarea id='script_test' rows="6" style="overflow: scroll;width:100%;height:100%"></textarea>
  </td>
 </tr>
  <tr height="20%">

  <td  colspan="2">
  script test result
   <textarea id='script_res' rows="6" style="overflow: scroll;width:100%;height:100%"></textarea>
  </td>
 </tr>
</table>
<div id='opc_info'>
</div>
</body>
<script>

var opener_txt_id = "<%=opener_txt_id%>" ;
var insid="<%=insid%>";

function log(s)
{
	document.getElementById('log_inf').innerHTML = s ;
}

function init()
{
	if(opener_txt_id!='')
	{
		var ow = dlg.get_opener_w();
		var txtob = ow.document.getElementById(opener_txt_id) ;
		if(txtob!=null)
		{
			$("#script_test").val(txtob.value) ;
		}
	}
	
}

init() ;

function run_script_test(fn)
{
	var scode = document.getElementById('script_test').value ;
	if(scode==null||scode==''||trim(scode)=='')
		return ;
	var pm = {op:"ins_js_test",insid:insid,txt:scode} ;
	send_ajax('sim_ajax.jsp',pm,function(bsucc,ret)
		{
			document.getElementById('script_res').value = ret ;
		},false) ;
}

function get_edited_js()
{
	return $("#script_test").val() ;
}
</script>
</html>