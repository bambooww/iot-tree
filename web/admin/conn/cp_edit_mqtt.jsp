<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.conn.mqtt.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
	return;
String repid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
ConnProMQTT cp = null ;
if(Convert.isNullOrEmpty(cpid))
{
	cp = new ConnProMQTT() ;
	cpid = cp.getId() ;
}
else
{
	cp = (ConnProMQTT)ConnManager.getInstance().getConnProviderById(repid, cpid) ;
	if(cp==null)
	{
		out.print("no ConnProMQTT found") ;
		return ;
	}
}

String name = cp.getName() ;
String title= cp.getTitle() ;
String chked = "" ;
if(cp.isEnable())
	chked = "checked='checked'" ;
String desc = cp.getDesc();
String cp_tp = cp.getProviderType() ;
MqttEndPoint mep = cp.getMqttEP() ;
String host = mep.getMQTTHost();
String port  = mep.getMQTTPortStr() ;

int conn_to = mep.getMQTTConnTimeout();
String user = mep.getMQTTUser();
String psw = mep.getMQTTPsw();
int conn_int = mep.getMQTTKeepAliveInterval() ;
List<String> topics = mep.getMQTTTopics();
String topics_str = Convert.transListToMultiLineStr(topics) ;
if(topics_str==null)
	topics_str = "" ;
%>
<html>
<head>
<title>mqtt cp editor</title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  autocomplete="off"  class="layui-input">
    </div>
    <div class="layui-form-mid">Title:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid">Enable:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">MQTT Host:</label>
    <div class="layui-input-inline">
      <input type="text" id="mqtt_host" name="mqtt_host" value="<%=host%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Port:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="text" id="mqtt_port" name="mqtt_port" value="<%=port%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	 <div class="layui-form-mid">Conn Timeout:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    
	    <input type="text" id="mqtt_conn_to" name="mqtt_conn_to" value="<%=conn_to%>"  title="seconds" autocomplete="off" class="layui-input">
	  </div>
  </div>

  <div class="layui-form-item">
    <label class="layui-form-label">MQTT User:</label>
    <div class="layui-input-inline">
      <input type="text" id="mqtt_user" name="mqtt_user" value="<%=user%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Password:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="mqtt_psw" name="mqtt_psw" value="<%=psw%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
  </div>
  <%--
  <div class="layui-form-item">
    <label class="layui-form-label">Subscribe Topics</label>
    <div class="layui-input-block">
      <textarea  id="topics"  name="topics"  required lay-verify="required" placeholder="" class="layui-textarea" rows="2"><%=topics_str%></textarea>
    </div>
  </div>
   --%>
    <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-block">
      <textarea  id="desc"  name="desc"  required lay-verify="required" placeholder="" class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>
      
 </form>
</body>
<script type="text/javascript">
var form = null;
layui.use('form', function(){
	  form = layui.form;
	  
	  $("#name,#title,#desc,#local_ip,#local_port").on("input",function(e){
		  setDirty(true);
		  });
	  form.on('select(local_ip)', function(data){   
		    setDirty(true);
	 });
	  form.on('switch(enable)', function(obj){
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
	  
	  form.render(); 
});

var _tmpid = 0 ;

var bdirty=false;
var cp_id = "<%=cpid%>" ;
var cp_tp = "<%=cp_tp%>" ;


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
		cb(false,'Please input name') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'Please input title') ;
		return ;
	}
	var ben = $("#enable").prop("checked") ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	var host = $('#mqtt_host').val();
	if(host==null||host=='')
	{
		cb(false,'Please input host') ;
		return ;
	}
	var port = $('#mqtt_port').val();
	if(port==null||port=='')
	{
		cb(false,'Please input port') ;
		return ;
	}
	var vp = parseInt(port);
	if(vp==NaN||vp<0)
	{
		cb(false,'Please input valid port') ;
	}

	var conn_to = $('#mqtt_conn_to').val();
	if(conn_to==null||conn_to=='')
	{
		cb(false,'Please input connection timeout') ;
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
		mqtt_psw="";
	}
	
	//var topicsstr = $("#topics").val() ;
	var tps = [];//str2lns(topicsstr)
	
	cb(true,{id:cp_id,name:n,title:tt,desc:desc,enable:ben,mqtt_host:host,mqtt_port:vp,
		mqtt_conn_to:conn_to,mqtt_user:mqtt_user,mqtt_psw:mqtt_psw,mqtt_topics:tps});
}

function str2lns(str)
{
	var arr = str.split('\n');
	var res = [];
	arr.forEach(function (item)
	{
		var ln = item.replace(/(^\s*)|(\s*$)/g, "").replace(/\s+/g, " ")
		if(ln=='')
			return ;
	    res.push(ln);
	})

	return res ;
}
</script>
</html>