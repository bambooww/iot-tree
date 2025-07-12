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
if(!Convert.checkReqEmpty(request, out, "prjid","cpid"))
	return;
	
String repid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
UAPrj prj = UAManager.getInstance().getPrjById(repid) ;
if(prj==null)
{
	out.print("no prj foudn") ;
	return ;
}

ConnProTcpServer cp = (ConnProTcpServer)ConnManager.getInstance().getConnProviderById(repid, cpid) ;
ConnPtTcpAccepted cpt = null ;
if(cp==null)
{
	out.print("no ConnProvider found") ;
	return ;
}

if(Convert.isNullOrEmpty(connid))
{
	cpt = new ConnPtTcpAccepted() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (ConnPtTcpAccepted)cp.getConnById(connid) ;
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
String sockconnid = cpt.getSockConnId() ;
String cp_tp = cp.getProviderType() ;

boolean en_at_ps = cpt.isEnabledAtPStation() ;
String en_at_ps_chked =  en_at_ps?"checked":"";
%>
<html>
<head>
<title>tcp server conn editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script>
dlg.resize_to(800,600);
</script>
</head>
<body>
<form class="layui-form"  onsubmit="return false;">
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
  <%
if(prj.isPrjPStationIns())
  {
  %>
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
	  <div class="layui-form-mid"><wbt:g>en_at_ps</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 200px;">
	    <input type="checkbox" id="en_at_ps" name="en_at_ps" <%=en_at_ps_chked%> lay-skin="switch"  lay-filter="en_at_ps" class="layui-input">
	  </div>
  </div>
  <%
  }
  %>
  <div class="layui-form-item">
    <label class="layui-form-label">Socket Conn Id:</label>
    <div class="layui-input-inline">
      <input type="text" id="sockconnid" name="sockconnid" value="<%=sockconnid%>"  lay-verify="required"  autocomplete="off" class="layui-input">
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
	  
	  $("#name").on("input",function(e){
		  setDirty();
		  });
	  $("#title").on("input",function(e){
		  setDirty();
		  });
	  $("#desc").on("input",function(e){
		  setDirty();
		  });
	  form.on('switch(enable)', function(obj){
		       setDirty();
		  });
	  form.on('switch(en_at_ps)', function(obj){
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
	let en_at_ps = $("#en_at_ps").prop("checked") ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var sockconnid = $('#sockconnid').val();
	if(sockconnid==null||sockconnid=='')
	{
		cb(false,'Please input sock conn id') ;
		return ;
	}
	
	cb(true,{id:conn_id,name:n,title:tt,desc:desc,enable:ben,en_at_ps:en_at_ps,sock_connid:sockconnid});
}

</script>
</html>