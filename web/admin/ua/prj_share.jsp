<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.node.*,
				org.iottree.core.conn.mqtt.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
		return ;
	
	String prjid = request.getParameter("prjid") ;

UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no project node found");
	return ;
}

PrjShareMQTT ps = (PrjShareMQTT)PrjShareManager.getInstance().getSharer(prjid) ;

boolean benable = true;
boolean bwrite=true;
String host = "";
String port  = "1883";

int conn_to = 30;
String user = "";
String psw = "";
int conn_int = 60;

long push_int = 10000 ;

String chked_en = "" ;
String chked_w = "" ;
if(ps!=null)
{
	benable = ps.isEnable();
	bwrite = ps.isWritable();
	push_int = ps.getPushInterval() ;
	
	MqttEndPoint cpt = ps.getMqttEP();
	host = cpt.getMQTTHost();
	port  = cpt.getMQTTPortStr() ;
	
	conn_to = cpt.getMQTTConnTimeout();
	user = cpt.getMQTTUser();
	psw = cpt.getMQTTPsw();
	conn_int = cpt.getMQTTKeepAliveInterval() ;
}

if(benable)
	chked_en = "checked=checked";
if(bwrite)
	chked_w = "checked=checked";
%>
<html>
<head>
<title>share project</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(750,400);
</script>
<style>
</style>
</head>
<body>
<form class="layui-form" action="">
<div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>enable</wbt:g>:</label>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
	  <div class="layui-form-mid"><wbt:g>writable</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="checkbox" id="writable" name="writable" <%=chked_w%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  <%--
<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-block">
      <input type="radio" name="share_tp" value="" title="Not share">
      <input type="radio" name="share_tp" value="mqtt" title="MQTT">
      <input type="radio" name="share_tp" value="rt_conn" title="rt conn">
    </div>
    --%>
    <div id="edit_mqtt">
     <div class="layui-form-item">
    <label class="layui-form-label">MQTT <wbt:g>host</wbt:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="mqtt_host" name="mqtt_host" value="<%=host%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>port</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="number" id="mqtt_port" name="mqtt_port" value="<%=port%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	 <div class="layui-form-mid"><wbt:g>conn,timeout</wbt:g>(<wbt:g>second</wbt:g>):</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    
	    <input type="number" id="mqtt_conn_to" name="mqtt_conn_to" value="<%=conn_to%>"  title="seconds" autocomplete="off" class="layui-input">
	  </div>
  </div>

  <div class="layui-form-item">
    <label class="layui-form-label">MQTT <wbt:g>user</wbt:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="mqtt_user" name="mqtt_user" value="<%=user%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>psw</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="mqtt_psw" name="mqtt_psw" value="<%=psw%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>push,intv</wbt:g>(<wbt:g>ms</wbt:g>):</label>
    <div class="layui-input-inline">
      <input type="number" id="push_int" name="push_int" value="<%=push_int%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline">
    </div>
    
    
  </div>
  
    </div>

</form>
</body>
<script type="text/javascript">

var prjid="<%=prjid%>";

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
	var ben = $("#enable").prop("checked") ;
	var bw =  $("#writable").prop("checked") ;
	
	var host = $('#mqtt_host').val();
	if(host==null||host=='')
	{
		cb(false,'<wbt:g>pls,input,host</wbt:g>') ;
		return ;
	}
	var port = $('#mqtt_port').val();
	if(port==null||port=='')
	{
		cb(false,'<wbt:g>pls,input,port</wbt:g>') ;
		return ;
	}
	var vp = parseInt(port);
	if(vp==NaN||vp<0)
	{
		cb(false,'<wbt:g>pls,input,valid,port</wbt:g>') ;
	}
	
	var conn_to = $('#mqtt_conn_to').val();
	if(conn_to==null||conn_to=='')
	{
		cb(false,'<wbt:g>pls,input,conn,timeout</wbt:g>') ;
		return ;
	}
	conn_to = parseInt(conn_to);
	if(conn_to==NaN||conn_to<0)
	{
		cb(false,'Please input valid connection timeout') ;
	}
	
	var mqtt_user = $('#mqtt_user').val();
	if(mqtt_user==null||mqtt_user=='')
	{
		//cb(false,'Please input Opc Id User') ;
		mqtt_user="";
		//return ;
	}
	
	var mqtt_psw = $('#mqtt_psw').val();
	if(mqtt_psw==null||mqtt_psw=='')
	{
		//cb(false,'Please input Opc Id password') ;
		//return ;
		mqtt_psw="";
	}
	
	var push_int = $("#push_int").val() ;
	if(push_int==null||push_int=='')
		push_int="" ;
	
	
	var pm = {op:"share",id:prjid,enable:ben,"dx_/mqtt_host:string":host
			,"dx_/mqtt_port:int32":vp,
			"dx_/mqtt_conn_to:int32":conn_to,"dx_/mqtt_user:string":mqtt_user,"dx_/mqtt_psw:string":mqtt_psw,
			"push_int":push_int,"w":bw}
	send_ajax("prj_ajax.jsp",pm,function(bsucc,ret){
		if(bsucc&&"ok"==ret)
		{
			cb(true,null) ;
			return ;
		}
		cb(false,ret) ;
		
	});
}

</script>
</html>