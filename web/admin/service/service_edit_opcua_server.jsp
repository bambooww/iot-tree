<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.service.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.driver.opc.opcua.server.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
OpcUAServer ser = (OpcUAServer)ServiceManager.getInstance().getService(OpcUAServer.NAME);
	HashMap<String,String> pms = ser.getConfPMS() ;
	boolean enable = ser.isEnable();//ser.isMqttEn();
	//boolean tcp_en = false;//ser.isTcpEn();
	
	String port =  "4840";// ser.getMqttPortStr();
	
	String chked_en = "" ;
	if(enable)
		chked_en = "checked=checked";
	//if(tcp_en)
	//	chked_tcp_en = "checked=checked";
	
	String user = "";// ser.getAuthUser() ;
	String psw =  "";// ser.getAuthPsw() ;
	String users =  "";// ser.getAuthUsers();
	
%>
<html>
<head>
<title>editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(600,400);
</script>
<style>
</style>
</head>
<body>
<form class="layui-form" action="">
 <div class="layui-form-item">
    <label class="layui-form-label">Enable:</label>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
	  <div class="layui-form-mid">Port:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="text" id="port" name="port" value="<%=port%>"  class="layui-input">
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">User:</label>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="user" name="user"  class="layui-input" value="<%=user%>">
	  </div>
	  <div class="layui-form-mid">Password:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="text" id="psw" name="psw" value="<%=psw%>"  class="layui-input">
	  </div>
  </div>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label">Users</label>
    <div class="layui-input-block">
      <textarea name="users" id="users" class="layui-textarea"><%=users %></textarea>
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
	var enable = $("#enable").prop("checked") ;
	
	var auth_user = $('#user').val();
	var auth_psw = $('#psw').val();
	var auth_users = $('#users').val();
	cb(true,{enable:enable,
		auth_user:auth_user,auth_psw:auth_psw,auth_users:auth_users});
}

</script>
</html>