<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.driver.common.modbus.*,
				org.iottree.pro.modbuss.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	
%>
<html>
<head>
<title></title>
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
		if(pts==ModbusCmd.Protocol.tcp)
			continue ;
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
    <label class="layui-form-label">Host:</label>
    <div class="layui-input-inline">
      	 <input type="text" id="server_host" name="server_host" value=""  class="layui-input">
    </div>
    <div class="layui-form-mid"><wbt:g>port</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="number" id="server_port" name="server_port" value=""  class="layui-input">
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid">Send When Connected</div>
    <div class="layui-input-inline" style="width:30px;">
      	 <input type="checkbox" id="send_id" name="send_id" value=""  class="layui-input" lay-skin="primary">
    </div>
    <div class="layui-form-mid">Conn Id:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="conn_id" name="conn_id" value=""  class="layui-input">
	  </div>
	  <div class="layui-form-mid">Hex</div>
	  <div class="layui-input-inline" style="width:30px;">
      	 <input type="checkbox" id="conn_id_hex" name="conn_id_hex" value=""  class="layui-input" lay-skin="primary">
     </div>
	  
  </div>
  
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid">Conn Not Used Timeouit (MS)</div>
    <div class="layui-input-inline" style="width:130px;">
      	 <input type="number" id="conn_nouse_to" name="conn_nouse_to" value=""  class="layui-input">
    </div>
    
  </div>
  
 </form>
</body>
<script type="text/javascript">
var form = null;

var cp = dlg.get_opener_opt("cp") ;
if(!cp)
	cp= {id:dlg.create_new_tmp_id(),server_host:"",server_port:"",send_id:true,conn_id:"",conn_id_hex:false,conn_nouse_to:60000,enable:true} ;
//console.log(cp) ;
layui.use('form', function(){
	  form = layui.form;

	  form.on('switch(enable)', function(obj){
		  });

	  $("#enable").prop("checked",cp.enable!=false);
	  $("#proto").val(cp.proto||"rtu") ;
	  $("#server_host").val(cp.server_host||"") ;
	  $("#server_port").val(cp.server_port) ;
	  $("#send_id").prop("checked",cp.send_id);
	  $("#conn_id_hex").prop("checked",cp.conn_id_hex) ;
	  $("#conn_nouse_to").val(cp.conn_nouse_to||60000) ;
	  $("#conn_id").val(cp.conn_id||"") ;
	  
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

	cp.proto = $("#proto").val()||"rtu";
	
	cp.enable = ben ;
	cp.server_host = $("#server_host").val() ;
	if(!cp.server_host)
	{
		cb(false,"Please select input host") ;
		return ;
	}
	cp.server_port = get_input_val("server_port",-1,true) ;
	if(cp.server_port<=0)
	{
		cb(false,"Please input valid port (1-65535)") ;
		return ;
	}
	cp.send_id = $("#send_id").prop("checked") ;
	cp.conn_id_hex = $("#conn_id_hex").prop("checked") ;
	cp.conn_id = $("#conn_id").val();
	cp.conn_nouse_to = get_input_val("conn_nouse_to",60000,true) ;
	if(cp.send_id==true && !cp.conn_id)
	{
		cb(false,"") ;
		return;
	}
	cp.tt = `\${cp.server_host}:\${cp.server_port}`;
	cb(true,cp);
}

</script>
</html>