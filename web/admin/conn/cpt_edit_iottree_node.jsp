<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
	org.iottree.core.conn.mqtt.*,
	org.iottree.core.node.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
	return;
String repid = request.getParameter("prjid") ;
String cptp = ConnProIOTTreeNode.TP;//request.getParameter("cptp") ;
ConnProIOTTreeNode cp = (ConnProIOTTreeNode)ConnManager.getInstance().getOrCreateConnProviderSingle(repid, cptp);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;

ConnPtIOTTreeNode cpt = null ;
if(Convert.isNullOrEmpty(connid))
{
	cpt = new ConnPtIOTTreeNode() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (ConnPtIOTTreeNode)cp.getConnById(connid) ;
	if(cpt==null)
	{
		out.print("no connection found") ;
		return ;
	}
}

String name = cpt.getName() ;
String title= cpt.getTitle() ;
String chked = "" ;
if(cpt.isEnable())
	chked = "checked='checked'" ;
String desc = cpt.getDesc();

PrjCallerMQTT pc = (PrjCallerMQTT)cpt.getCaller() ;
String share_prjid = pc.getSharePrjId();
MqttEndPoint mep = pc.getMqttEP();
//String opc_appn = cpt.getOpcAppName();
//String opc_epuri  = cpt.getOpcEndPointURI();
String host = mep.getMQTTHost();
String port  = mep.getMQTTPortStr() ;

int conn_to = mep.getMQTTConnTimeout();
String user = mep.getMQTTUser();
String psw = mep.getMQTTPsw();
int conn_int = mep.getMQTTKeepAliveInterval() ;
%>
<html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<style type="text/css">
.layui-form-label
{
width:100px;
}
</style>
<script>
dlg.resize_to(800,500);
</script>
</head>
<body>
<form class="layui-form"  onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>name</wbt:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  lay-verify="required" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>title</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid"><wbt:g>enable</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label">MQTT <wbt:g>host</wbt:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="mqtt_host" name="mqtt_host" value="<%=host%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>port</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="number" id="mqtt_port" name="mqtt_port" value="<%=port%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	 <div class="layui-form-mid"><wbt:g>conn,timeout</wbt:g>:</div>
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
    <label class="layui-form-label"><wbt:g>share,prj</wbt:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="share_prjid" name="share_prjid" value="<%=share_prjid%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>desc</wbt:g>:</label>
    <div class="layui-input-inline" style="width:490px">
      <textarea  id="desc"  name="desc"  required lay-verify="required" placeholder="" class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>
   
 </form>
</body>
<script type="text/javascript">
var form = null;
layui.use('form', function(){
	  form = layui.form;
	  
	  $("#name").on("input",function(e){
		  setDirty(true);
		  });
	  $("#title").on("input",function(e){
		  setDirty(true);
		  });
	  $("#desc").on("input",function(e){
		  setDirty(true);
		  });
	  $("#mqtt_host").on("input",function(e){
		  setDirty(true);
		  });
	  $("#mqtt_port").on("input",function(e){
		  setDirty(true);
		  });
	  $("#mqtt_conn_to").on("input",function(e){
		  setDirty(true);
		  });
	  $("#mqtt_user").on("input",function(e){
		  setDirty(true);
		  });
	  $("#mqtt_psw").on("input",function(e){
		  setDirty(true);
		  });
	  $("#share_prjid").on("input",function(e){
		  setDirty(true);
		  });

	  form.on('switch(enable)', function(obj){
		  setDirty(true);
		  });
		  
	  form.render(); 
});

var _tmpid = 0 ;

var prjid="<%=repid%>";
var bdirty=false;
var cp_id = "<%=cpid%>" ;
var cp_tp = "<%=cptp%>" ;
var conn_id = "<%=connid%>" ;

function isDirty()
{
	return bdirty;
}
function setDirty(b)
{
	if(!(b===false))
		b = true ;
	bdirty= b;
	dlg.btn_set_enable(1,b);
}

	
function win_close()
{
	dlg.close(0);
}

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
}

function do_submit(cb)
{
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'<wbt:g>pls,input,name</wbt:g>') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'<wbt:g>pls,input,title</wbt:g>') ;
		return ;
	}
	var ben = $("#enable").prop("checked") ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
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
	
	var share_prjid = $('#share_prjid').val();
	if(share_prjid==null||share_prjid=='')
	{
		cb(false,'<wbt:g>pls,input,shore,prj</wbt:g>') ;
		return ;
	}
	
	cb(true,{id:conn_id,name:n,title:tt,desc:desc,enable:ben,prjid:prjid,mqtt_host:host,mqtt_port:vp,
		mqtt_conn_to:conn_to,mqtt_user:mqtt_user,mqtt_psw:mqtt_psw,share_prjid:share_prjid});
}

</script>
</html>