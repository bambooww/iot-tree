<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.ext.opcda.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
	return;
String repid = request.getParameter("prjid") ;
String cptp = OpcDAConnPro.TP;//request.getParameter("cptp") ;
OpcDAConnPro cp = (OpcDAConnPro)ConnManager.getInstance().getOrCreateConnProviderSingle(repid, cptp);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;

OpcDAConnPt cpt = null ;
if(Convert.isNullOrEmpty(connid))
{
	cpt = new OpcDAConnPt() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (OpcDAConnPt)cp.getConnById(connid) ;
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
//String opc_appn = cpt.getOpcAppName();
//String opc_epuri  = cpt.getOpcEndPointURI();
String host = cpt.getOpcHost() ;
if(Convert.isNullOrEmpty(host))
	host = "localhost";
String progid = cpt.getProgId() ;
long int_ms = cpt.getUpdateIntMs() ;
String cp_tp = cp.getProviderType() ;
String chk_pros = cpt.getChkProcessName() ;
boolean chk_pros_reboot = cpt.isChkProcessReboot() ;
long chk_pros_reboot_after = cpt.getChkProcessRebootAfter() ;
%>
<html>
<head>
<title></title>
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
<form class="layui-form" action="">
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
    <label class="layui-form-label">Opc Host:</label>
    <div class="layui-input-inline">
      <input type="text" id="opc_host" name="opc_host" value="<%=host %>"  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Program Id:</div>
	  <div class="layui-input-inline" style="width: 300px;">
<%
    StringBuilder failedr = new StringBuilder() ;
	String[] progids = OpcClientBrowser.getOpcServers("localhost", failedr);
	if(progids==null)
	{
%><b><%=failedr %></b><%
	}
	else
	{
%>
	    <select id="prog_id" lay-filter="comid">
<%
for(String cid:progids)
{
	String pseled = "" ;
	if(cid.equals(progid))
	{
		pseled = "selected=selected" ;
	}
%><option value="<%=cid%>" <%=pseled %>><%=cid %></option>
<%
}
%>   </select>
<%
	}
%>
	  </div>
	 
  </div>

<div class="layui-form-item">
    <label class="layui-form-label"></label>
	 <div class="layui-form-mid">Update Interval</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="number" id="int_ms" name="int_ms" value="<%=int_ms%>"  class="layui-input">
	  </div>
	  
  </div>
  
  <div class="layui-form-item">
    <label class="layui-form-label">Check Process</label>
	  <div class="layui-input-inline" style="width: 250px;">
	    <input type="text" id="chk_pros" name="chk_pros" value="<%=chk_pros %>"  autocomplete="off" class="layui-input" title="<wbt:g>dbclk,select</wbt:g>" ondblclick="sel_pro()">
	  </div>
	  <div class="layui-form-mid">Failed Reboot</div>
	  <div class="layui-input-inline" style="width: 50px;">
	    <input type="checkbox" id="chk_pros_reboot" name="chk_pros_reboot"  lay-skin="switch"  lay-filter="chk_pros_reboot"  <%=chk_pros_reboot?"checked":"" %>  autocomplete="off" class="layui-input">
	   </div>
	  <div class="layui-form-mid">After(MS)</div>
	  <div class="layui-input-inline" style="width: 100px;">
	    <input type="number" id="chk_pros_reboot_after" name="chk_pros_reboot_after" value="<%=chk_pros_reboot_after %>"  autocomplete="off" class="layui-input">
	  </div>
  </div>
  
    <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>desc</wbt:g>:</label>
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
	  $("#opc_host").on("input",function(e){
		  setDirty();
		  });
	  $("#prog_id").on("input",function(e){
		  setDirty();
		  });
	  $("#int_ms").on("input",function(e){
		  setDirty();
		  });
	  
	  
	  form.on('switch(enable)', function(obj){
		       setDirty();
		  });
		  
	  form.render(); 
});

function sel_pro()
{
	
}

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
		tt = n ;
	}
	var ben = $("#enable").prop("checked") ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var opc_host = $('#opc_host').val();
	if(opc_host==null||opc_host=='')
	{
		cb(false,'Please input Opc Host') ;
		return ;
	}
	
	var prog_id = $('#prog_id').val();
	if(prog_id==null||prog_id=='')
	{
		cb(false,'Please select Program Id') ;
		return ;
	}
	
	var int_ms = $('#int_ms').val();
	if(int_ms==null||int_ms=='')
	{
		cb(false,'Please input Update interval') ;
		return ;
	}
	int_ms = parseInt(int_ms);
	if(int_ms==NaN||int_ms<0)
	{
		cb(false,'Please input valid Update interval') ;
	}
	
	let chk_pros = trim($("#chk_pros").val()) ;
	chk_pros_reboot = $("#chk_pros_reboot").prop("checked") ;
	chk_pros_reboot_after = get_input_val("chk_pros_reboot_after",600000,true)
	
	let pm ={id:conn_id,name:n,title:tt,desc:desc,enable:ben,opc_host:opc_host,prog_id:prog_id,int_ms:int_ms,
			chk_pros:chk_pros,chk_pros_reboot:chk_pros_reboot,chk_pros_reboot_after:chk_pros_reboot_after};
	
	cb(true,pm);
}

</script>
</html>