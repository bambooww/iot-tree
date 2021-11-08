<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.service.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	ServiceActiveMQ ser = (ServiceActiveMQ)ServiceManager.getInstance().getService(ServiceActiveMQ.NAME);
	HashMap<String,String> pms = ser.getConfPMS() ;
	boolean mqtt_en = ser.isMqttEn();
	boolean tcp_en = ser.isTcpEn();
	
	String mqtt_port = ser.getMqttPortStr();
	String tcp_port = ser.getTcpPortStr() ;

	String chked_mqtt_en = "" ;
	String chked_tcp_en = "" ;
	if(mqtt_en)
		chked_mqtt_en = "checked=checked";
	if(tcp_en)
		chked_tcp_en = "checked=checked";
	
	String user = ser.getAuthUser() ;
	String psw = ser.getAuthPsw() ;
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
    <label class="layui-form-label">Mqtt Enable:</label>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="mqtt_en" name="enable" <%=chked_mqtt_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
	  <div class="layui-form-mid">Port:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="text" id="mqtt_port" name="mqtt_port" value="<%=mqtt_port%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Tcp Enable:</label>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="tcp_en" name="tcp_en" <%=chked_tcp_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
	  <div class="layui-form-mid">Port:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="text" id="tcp_port" name="tcp_port" value="<%=tcp_port%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">User:</label>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="user" name="user"  class="layui-input" value="<%=user%>">
	  </div>
	  <div class="layui-form-mid">Password:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="password" id="psw" name="psw" value="<%=psw%>"  class="layui-input">
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
	var mqtt_en = $("#mqtt_en").prop("checked") ;
	var tcp_en =  $("#tcp_en").prop("checked") ;
	

	var mqtt_port = $('#mqtt_port').val();
	if(mqtt_en && (mqtt_port==null||mqtt_port==''))
	{
		cb(false,'Please input mqtt port') ;
		return ;
	}
	var mqtt_port = parseInt(mqtt_port);
	if(mqtt_port==NaN||mqtt_port<=0)
	{
		cb(false,'Please input valid mqtt port') ;
	}
	var tcp_port = $('#tcp_port').val();
	if(tcp_en && (tcp_port==null||tcp_port==''))
	{
		cb(false,'Please input mqtt port') ;
		return ;
	}
	var tcp_port = parseInt(tcp_port);
	if(tcp_port==NaN||tcp_port<=0)
	{
		cb(false,'Please input valid tcp port') ;
	}
	var auth_user = $('#user').val();
	var auth_psw = $('#psw').val();
	var enable = mqtt_en||tcp_en ;
	cb(true,{enable:enable,mqtt_en:mqtt_en,mqtt_port:mqtt_port,tcp_en:tcp_en,tcp_port:tcp_port,
		auth_user:auth_user,auth_psw:auth_psw});
}

</script>
</html>