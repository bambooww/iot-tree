<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
	List<DevDriver> dds = DevManager.getInstance().getDrivers() ;
%>
<html>
<head>
<title>drv selector</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<link rel="stylesheet" type="text/css" href="/_res/icon/css/font-awesome.css">
<link rel="stylesheet" type="text/css" href="/_res/icon/css/font-awesome-ie7.min.css">
<script>
dlg.resize_to(400,600);
</script>
</head>
<style>
tr:hover
{
	background-color: grey;
}
</style>
<body>
Selected:<span id="seled"></span>
<table class="layui-table" lay-size="sm">
  <colgroup>
    <col width="100" />
    <col />
  </colgroup>
  <tbody>
<%
for(DevDriver dd:dds)
{
%>
<tr onclick="sel_drv('<%=dd.getName()%>','<%=dd.getTitle() %>')">
      <td><%=dd.getName()%></td>
      <td><%=dd.getTitle() %></td>
</tr>
<%
}
%>
  </tbody>
</table>
<%--
<form class="layui-form" action="">

  <div class="layui-form-item">
    <label class="layui-form-label">Driver</label>
    <div class0="layui-input-block" class="layui-input-inline">
      
      <select name="drv" id="drv"  multiple="multiple">
<%
for(DevDriver dd:dds)
{
%>
			<option value="<%=dd.getName()%>"  tt="<%=dd.getTitle() %>"><%=dd.getTitle() %></option>
<%
}
%>
		</select>

    </div>
    
  </div>
  
</form>
 --%>
</body>
<script type="text/javascript">
layui.use('form', function(){
	  var form = layui.form;
	  form.render();
	});
	
var seled_drv=null ;
	
function win_close()
{
	dlg.close(0);
}

function sel_drv(n,t)
{
	seled_drv={name:n,title:t} ;
	$("#seled").html(t);
}

function do_submit(cb)
{
	/*
	var n = $('#drv').val();
	if(n==null||n=='')
	{
		cb(false,'请选择一个驱动') ;
		return ;
	}
	var tt = $("#drv").find("option:selected").text();
	cb(true,{name:n,title:tt});
	*/
	if(seled_drv==null)
	{
		cb(false,'请选择一个驱动') ;
		return ;
	}
	cb(true,seled_drv);
}

</script>
</html>