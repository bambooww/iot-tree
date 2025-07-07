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
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	
%>
<html>
<head>
<title>com cp editor</title>
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
    <label class="layui-form-label">ID:</label>
    <div class="layui-input-inline">
      	    <select id="comid" lay-filter="comid">
      	    <option value="">---</option>
<%
for(ConnProCOM.COMItem cid:SlaveCPCom.listSysComs())
{
%><option value="<%=cid.name%>"><%=cid.getShowTitle() %></option>
<%
}
%>   </select>
    </div>
    <div class="layui-form-mid"><wbt:g>baud</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <select id="baud"  lay-filter="baud">
<%
for(int b:SlaveCPCom.BAUDS)
{
%><option value="<%=b%>"><%=b %></option>
<%
}
%>   </select>
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>data_bits</wbt:g>:</label>
    <div class="layui-input-inline">
      	    <select id="databits" lay-filter="databits">
<%
for(int dbit:SlaveCPCom.DATABITS)
{
%><option value="<%=dbit%>"><%=dbit %></option>
<%
}
%>   </select>
    </div>
    <div class="layui-form-mid"><wbt:g>parity</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <select id="parity" lay-filter="parity">
<%

for(int i = 0 ; i < SlaveCPCom.PARITY.length ; i ++)
{
	int pri = SlaveCPCom.PARITY[i] ;
	String tt =  "parity_"+SlaveCPCom.PARITY_NAME[i];//.toLowerCase() ;
%><option value="<%=pri%>"><wbt:g><%=tt %></wbt:g></option>
<%
}
%>   </select>
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>stop_bits</wbt:g>:</label>
    <div class="layui-input-inline">
      	    <select id="stopbits" lay-filter="stopbits">
      	    	
<%
for(int dbit:SlaveCPCom.STOPBITS)
{
%><option value="<%=dbit%>"><%=dbit %></option>
<%
}
%>   </select>
    </div>
    <div class="layui-form-mid"><wbt:g>flow_ctrl</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <select id="flowctl" lay-filter="flowctl">
<%
for(int i = 0 ; i < SlaveCPCom.FLOWCTL.length;i++)
{
	int fctl = ConnPtCOM.FLOWCTL[i] ;
	String tt = ConnPtCOM.FLOWCTL_TITLE[i] ;
%><option value="<%=fctl%>"><%=tt %></option>
<%
}
%>   </select>
	  </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var form = null;

var cp = dlg.get_opener_opt("cp") ;
if(!cp)
	cp= {id:dlg.create_new_tmp_id(),baud:9600,databits:8,stopbits:1,parity:0,flowctl:0,enable:true} ;
//console.log(cp) ;
layui.use('form', function(){
	  form = layui.form;

	  form.on('switch(enable)', function(obj){
		  });

	  $("#enable").prop("checked",cp.enable!=false);
	  $("#proto").val(cp.proto||"rtu") ;
	  $("#comid").val(cp.comid||"") ;
	  $("#baud").val(cp.baud||9600) ;
	  $("#parity").val(cp.parity||0) ;
	  $("#databits").val(cp.databits||8) ;
	  $("#stopbits").val(cp.stopbits||1) ;
	  $("#flowctl").val(cp.flowctl||0) ;
	  
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

var PARITY_T = [ "N", "O", "E" ];

function do_submit(cb)
{
	let ben = $("#enable").prop("checked") ;
	
	
	
	cp.proto = $("#proto").val()||"rtu";
	cp.comid = $("#comid").val() ;
	if(cp.comid<=0)
	{
		cb(false,"Please select com id") ;
		return ;
	}
	cp.enable = ben ;
	cp.baud = get_input_val("baud",9600,true) ;
	cp.databits = get_input_val("databits",8,true) ;
	cp.parity = get_input_val("parity",0,true) ;
	cp.databits = get_input_val("databits",8,true) ;
	cp.stopbits = get_input_val("stopbits",1,true) ;
	cp.flowctl = get_input_val("flowctl",0,true) ;
	cp.tt = `\${cp.comid} (\${cp.baud} \${PARITY_T[cp.parity]} \${cp.databits} \${cp.stopbits})`;
	cb(true,cp);
}

</script>
</html>