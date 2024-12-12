<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.json.*,
	org.iottree.core.*,
	org.iottree.core.conn.*,
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
String cptp = ConnProUDP.TP;
ConnProUDP cp = (ConnProUDP)ConnManager.getInstance().getOrCreateConnProviderSingle(repid, cptp);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
//ConnProTcpClient cp = (ConnProTcpClient)ConnManager.getInstance().getConnProviderById(repid, cpid) ;
ConnPtUDPMsg cpt = null ;
if(Convert.isNullOrEmpty(connid))
{
	cpt = new ConnPtUDPMsg() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (ConnPtUDPMsg)cp.getConnById(connid) ;
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
int connto = -1;//cpt.getConnTimeout();
String cp_tp = cp.getProviderType() ;

String host = cpt.getHost() ;
int port = cpt.getPort() ;

String loc_ip = cpt.getRecvLocIP() ;
int loc_port = cpt.getRecvLocPort() ;

%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style type="text/css">
.remote_item
{
	border:1px solid #03a8d8;
	min-height:30px;
	margin:3px;
	min-width: 50px;
}
</style>
<script>
dlg.resize_to(800,500);
</script>
</head>
<body>
<form class="layui-form" onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>name</wbt:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>title</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"   class="layui-input">
	  </div>
	  <div class="layui-form-mid"><wbt:g>enable</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  
  <div class="layui-form-item" >
    <label class="layui-form-label">Local:</label>
    
   	<div class="layui-form-mid" >Listen on Port:</div>
	  <div class="layui-input-inline" style="width: 80px;">
	    <input type="number" id="loc_port" name="loc_port" value="<%=loc_port %>" class="layui-input">
	  </div>
    	<div class="layui-form-mid" >Local IP:</div>
	    <div class="layui-input-inline" >
	      <select id="loc_ip"  lay-filter="loc_ip" >
	    	<option value="">--</option>
<%
	for(NetUtil.Adapter adp:NetUtil.listAdapters())
	{
		String chk = adp.getIp4().equals(loc_ip)?"checked":"" ;
			
%>
	<option value="<%=adp.getIp4()%>" <%=chk %>><%=adp.getIp4()%></option>
<%
	}
%>	  
	    </select>
	    </div>
	    
	  </div>
  
  <div class="layui-form-item" >
    <label class="layui-form-label">Remote:</label>
    	<div class="layui-form-mid" ><wbt:g>host</wbt:g>:</div>
	    <div class="layui-input-inline" >
	      <input type="text" id="host" name="host" value="<%=host %>"  class="layui-input">
	    </div>
	    <div class="layui-form-mid" ><wbt:g>port</wbt:g>:</div>
		  <div class="layui-input-inline" style="width: 80px;">
		    <input type="number" id="port" name="port" value="<%=port %>" class="layui-input">
		  </div>
    </div>
     
   
    <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>desc</wbt:g>:</label>
    <div class="layui-input-inline" style="width:80%">
      <textarea  id="desc"  name="desc"  class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>
  
 </form>
</body>
<script type="text/javascript">

var form = null;
layui.use('form', function(){
	  form = layui.form;
	  
	  $("#name").on("input",function(e){
		  setDirty();
		  });
	  $("#title").on("input",function(e){
		  setDirty();
		  });
	  $("#desc").on("input",function(e){
		  setDirty();
		  });
	 
	  form.render(); 
});


var _tmpid = 0 ;

var bdirty=false;
var cp_id = "<%=cpid%>" ;
var cp_tp = "<%=cp_tp%>" ;
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
		//cb(false,'Please input title') ;
		//return ;
		tt = n;
	}
	var ben = $("#enable").prop("checked") ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	let host = $("#host").val() ;
	let port = get_input_val("port",-1,true) ;
	let loc_ip = $("#loc_ip").val() ;
	let loc_port = get_input_val("loc_port",-1,true) ;
	cb(true,{id:conn_id,name:n,title:tt,desc:desc,enable:ben,
		host:host,port:port,loc_ip:loc_ip,loc_port:loc_port});
}

</script>
</html>