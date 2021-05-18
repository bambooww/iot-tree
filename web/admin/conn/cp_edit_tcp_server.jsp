<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "repid"))
	return;
String repid = request.getParameter("repid") ;
String cpid = request.getParameter("cpid") ;
ConnProTcpServer cp = null ;
if(Convert.isNullOrEmpty(cpid))
{
	cp = new ConnProTcpServer() ;
	cpid = cp.getId() ;
}
else
{
	cp = (ConnProTcpServer)ConnManager.getInstance().getConnProviderById(repid, cpid) ;
	if(cp==null)
	{
		out.print("no ConnProvider found") ;
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
String local_ip = cp.getLocalIP() ;
int local_port = cp.getLocalPort() ;
String ashn = cp.getAshName() ;
List<ConnProTcpServer.AcceptedSockHandler> ashs = ConnProTcpServer.getAcceptedSockHandlers() ;
%>
<html>
<head>
<title>tcp client cp editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<script type="text/javascript" src="/_js/ajax.js"></script>
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  class="layui-input">
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
    <label class="layui-form-label">Local Port:</label>
    <div class="layui-input-inline">
      <input type="text" id="local_port" name="local_port" value="<%=local_port%>"  class="layui-input">
    </div>
    <div class="layui-form-mid">Local IP:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <select id="local_ip"  lay-filter="local_ip" >
	    	<option value="">--</option>
<%
	for(NetUtil.Adapter adp:NetUtil.listAdapters())
	{
%>
	<option value="<%=adp.getIp4()%>"><%=adp.getIp4() %></option>
<%
	}
%>	  
<!-- 
	    <input type="text" id="local_ip" name="local_ip" value="<%=local_ip%>"  class="layui-input">
	     -->
	    </select>
	  </div>

  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Conn Identity:</label>
    <div class="layui-input-inline">
    	<select id="ash" onchange="sel_ash()"  lay-filter="ash" >
<%
for(ConnProTcpServer.AcceptedSockHandler ash:ashs)
{
%>
    		<option value="<%=ash.getName()%>"><%=ash.getTitle() %></option>
<%
}
%>
    	</select>
    </div>
    
  </div>

   <div class="layui-form-item">
    <label class="layui-form-label">Identier Params:</label>
    <div class="layui-input-block" id="cont">
      
    </div>
  </div>
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
		  setDirty();
		  });
	  form.on('select(local_ip)', function(data){   
		    setDirty();
	 });
	  form.on('switch(enable)', function(obj){
		       setDirty();
		  });
	  form.on('select(ash)', function(data){   
		    sel_ash();
		    setDirty();
	 });
	  
	  $("#local_ip").val("<%=local_ip%>") ;
	  $("#ash").val("<%=ashn%>") ;
	  
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
function setDirty()
{
	bdirty= true;
	dlg.btn_set_enable(1,true);
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
	
	var ashn = $("#ash").val() ;

	var ashpm={};
	$( "[id^='param_']" ).each(function(index,item){
		var id = $(this).attr("id").substring(6) ;
		var v = $(this).val() ;
		ashpm[id]=v ;
		
	}) ;
	
	var local_port = $("#local_port").val() ;
	if(local_port==null||local_port=="")
	{
		cb(false,"Please input local port") ;
		return ;
	}
	var lpt =parseInt(local_port);
	if(lpt==NaN||lpt<=0||lpt>65535)
	{
		cb(false,"invalid local port") ;
		return ;
	}
	var local_ip = $("#local_ip").val() ;
	
	var ret = {id:cp_id,name:n,title:tt,desc:desc,enable:ben,tp:cp_tp,ash_name:ashn,ash_params:ashpm}
	ret.local_port = local_port;
	ret.local_ip = local_ip ;
	console.log(ret) ;
	cb(true,ret);
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}
<%--
var ash2paramdef={
<%
boolean tmpbf=  true ;
for(ConnProTcpServer.AcceptedSockHandler ash: ashs)
{
	if(tmpbf)tmpbf=false;
	else out.write(",") ;
	out.write(ash.getName()+":") ;
	NameTitleVal.writeToJsonStr(ash.getParamDefs(),out) ;
}
%>
}
--%>

function sel_ash()
{
	var ashn = $("#ash").val() ;
	if(ashn==null||ashn==undefined)
		return ;
	send_ajax("cp_edit_tcp_server_ash.jsp",{ashn:ashn},(bsucc,ret)=>{
		$("#cont").html(ret) ;
		//form.render();
		
		$( "[id^='param_']" ).each(function(index,item){
			var id = $(this).attr("id") ;
			var v = $(this).val() ;
			console.log(id,v) ;
			
		}) ;
		
		$( "[id^='param_']" ).on("change",function(){
			var v = $(this).val() ;
			console.log(v) ;
			setDirty()
		}) ;
		
		$( "[id^='param_']" ).bind('input propertychange', function(){
			var v = $(this).val() ;
			console.log("textarea "+v) ;
			setDirty()
		});  
	});
}

sel_ash();
</script>
</html>