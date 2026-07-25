<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.driver.common.modbus.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	
%>
<html>
<head>
<title>cp editor</title>
<jsp:include page="../../../head.jsp"></jsp:include>
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Protocal:</label>
    <div class="layui-input-inline" style="width: 150px;">
	    <select id="proto"  lay-filter="proto" >
<%
	for(ModbusCmd.Protocol pts:ModbusCmd.Protocol.values())
	{
%>
	<option value="<%=pts.name()%>"><%=pts.name()%></option>
<%
	}
%>
	    </select>
	  </div>
    <div class="layui-form-mid">Enable:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" checked  lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Local Port:</label>
    <div class="layui-input-inline">
      <input type="number" id="server_port" name="server_port" value=""  class="layui-input">
    </div>
    <div class="layui-form-mid">Local IP:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <select id="server_ip"  lay-filter="server_ip" >
	    	<option value="">--</option>
<%
	for(NetUtil.Adapter adp:NetUtil.listAdapters())
	{
%>
	<option value="<%=adp.getIp4()%>"><%=adp.getIp4() %></option>
<%
	}
%>
	    </select>
	  </div>

  </div>
  
      
 </form>
</body>
<script type="text/javascript">
var form = null;

var cp = dlg.get_opener_opt("cp") ;
if(!cp)
	cp= {id:dlg.create_new_tmp_id(),server_port:12000} ;
//console.log(cp) ;
layui.use('form', function(){
	  form = layui.form;

	  form.on('select(server_ip)', function(data){   
	 });
	  form.on('switch(enable)', function(obj){
		  });
	  
	  $("#enable").prop("checked",cp.enable!=false);
	  $("#proto").val(cp.proto||"rtu") ;
	  $("#server_port").val(cp.server_port) ;
	  $("#server_ip").val(cp.server_ip||"") ;
	  
	  form.render(); 
});

	
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
	let ben = $("#enable").prop("checked") ;
	
	var local_port = get_input_val("server_port",-1,true) ;
	if(local_port<=0)
	{
		cb(false,"Please input local port") ;
		return ;
	}
	cp.enable = ben ;
	cp.proto = $("#proto").val()||"rtu";
	cp.server_ip = $("#server_ip").val() ;
	cp.server_port = local_port ;
	cp.tt = (cp.server_ip||"")+":"+cp.server_port;
	cb(true,cp);
}

</script>
</html>