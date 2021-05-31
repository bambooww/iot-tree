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
	if(!Convert.checkReqEmpty(request, out, "repid","cpid"))
	return;
String repid = request.getParameter("repid") ;
String cpid = request.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnProTcpClient cp = (ConnProTcpClient)ConnManager.getInstance().getConnProviderById(repid, cpid) ;
ConnPtTcpClient cpt = null ;
if(cp==null)
{
	out.print("no ConnProvider found") ;
	return ;
}

if(Convert.isNullOrEmpty(connid))
{
	cpt = new ConnPtTcpClient() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (ConnPtTcpClient)cp.getConnById(connid) ;
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
String host = cpt.getHost() ;
String port  = cpt.getPortStr() ;
int connto = cpt.getConnTimeout();
String cp_tp = cp.getProviderType() ;

%>
<html>
<head>
<title>tcp client conn editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  lay-verify="required" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Title:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid">Enable:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Host:</label>
    <div class="layui-input-inline">
      <input type="text" id="host" name="host" value="<%=host%>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Port:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="port" name="port" value="<%=port%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	 <div class="layui-form-mid">Connect timeout</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="conn_to" name="conn_to" value="<%=connto%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-block">
      <textarea  id="desc"  name="desc"  required lay-verify="required" placeholder="" class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label">Properties:</label>
    <div class="layui-input-block">
        
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
	  $("#conn_to").on("input",function(e){
		  setDirty();
		  });
	  form.on('switch(enable)', function(obj){
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
	
	var host = $('#host').val();
	if(host==null||host=='')
	{
		cb(false,'Please input host') ;
		return ;
	}
	var port = $('#port').val();
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
	var connto = $("#conn_to").val() ;
	if(connto==NaN)
	{
		cb(false,'Please input valid connect timeout') ;
	}
	
	cb(true,{id:conn_id,name:n,title:tt,desc:desc,enable:ben,host:host,port:vp,conn_to:connto});
}

</script>
</html>