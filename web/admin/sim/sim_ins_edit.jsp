<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.task.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.sim.*,
				org.iottree.driver.common.modbus.sim.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	String insid=request.getParameter("insid");
	SimInstance ins = null ;
	if(Convert.isNotNullEmpty(insid))
	{
		ins = SimManager.getInstance().getInstance(insid) ;
		if(ins==null)
		{
	out.print("no instance found") ;
	return ;
		}
	}
String name = "" ;
String title = "" ;
long int_ms = Task.DEFAULT_INT_MS ;

boolean benable = true;
if(ins!=null)
{
		name = ins.getName() ;
		title = ins.getTitle() ;
		
		benable = ins.isEnable() ;
}

if(insid==null)
	insid = "" ;
if(name==null)
	name = "" ;
if(title==null)
	title = "" ;

String chked_en = "" ;

if(benable)
	chked_en = "checked=checked";
%>
<html>
<head>
<title>instance editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(600,300);
</script>
</head>
<body>
<form class="layui-form" action="">
<div class="layui-form-item">
    <label class="layui-form-label">Name</label>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" name="name" id="name" value="<%=name%>" autocomplete="off" class="layui-input"/>
	  </div>
	  <div class="layui-form-mid">Enable:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	  <input type="checkbox" id="enable" name="enable" <%=chked_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Title</label>
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="text" name="title" id="title" value="<%=title%>" autocomplete="off" class="layui-input"/>
    </div>
    
  </div>
   
</form>
</body>
<script type="text/javascript">

layui.use('form', function(){
	  var form = layui.form;
	  form.render();
	});
	
function win_close()
{
	dlg.close(0);
}
function do_submit(cb)
{
	var n = document.getElementById('name').value;
	if(n==null||n=='')
	{
		cb(false,'<wbt:lang>pls_input</wbt:lang><wbt:lang>name</wbt:lang>') ;
		return ;
	}
	
	var t = document.getElementById('title').value;
	//var desc = document.getElementById('desc').value;
	//if(desc==null)
	//	desc =$("#desc").val(); ;
		var ben = $("#enable").prop("checked") ;
		/*
	var int_ms = $("#int_ms").val();
	var int_ms = parseInt(int_ms);
	if(int_ms==NaN||int_ms<=0)
	{
		cb(false,'Please input valid interval ms') ;
	}*/
	cb(true,{name:n,title:t,enable:ben})
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>