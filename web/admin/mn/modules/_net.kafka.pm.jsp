<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
<div id="edit_mqtt">
     <div class="layui-form-item">
    <label class="layui-form-label">Broker <w:g>host</w:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="host" name="host" value=""  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><w:g>port</w:g>:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="number" id="port" name="port" value=""  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	 <div class="layui-form-mid"><w:g>send,timeout</w:g>(<w:g>second</w:g>):</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    
	    <input type="number" id="send_to" name="send_to" value=""  title="seconds" autocomplete="off" class="layui-input">
	  </div>
  </div>
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>sec_proto</w:g>:</label>
    <div class="layui-input-inline">
      <select id="sec_proto" lay-filter="sec_proto">
<%
	for(Kafka_M.SecurityProto sp:Kafka_M.SecurityProto.values())
	{
		//String sel = (sp==sec_proto?"selected":"") ;
%><option value="<%=sp.id%>" ><%=sp.name %></option><%
	}
%>
      </select>
    </div>
    <div class="layui-form-mid sasl_mech"><w:g>sasl_mech</w:g>:</div>
	  <div class="layui-input-inline sasl_mech" style="width: 200px;">
	          <select id="sec_sasl_mech">
<%
	for(Kafka_M.SaslMech sp:Kafka_M.SaslMech.values())
	{
		//String sel = (sp==sec_sasl_mech?"selected":"") ;
%><option value="<%=sp.id%>" ><%=sp.name %></option><%
	}
%>
      </select>
	  </div>
  </div>
  <div class="layui-form-item user_psw">
    <label class="layui-form-label"><w:g>user</w:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="user" name="user" value=""  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><w:g>psw</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="psw" name="psw" value=""  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
  </div>

<script>

function get_pm_jo()
{
	let bdelay = $("#b_delay").prop("checked") ;
	let delay_ms = get_input_val("delay_ms",-1,true) ;
	if(bdelay && delay_ms<=0)
	{
		return "<w:g>pls,input,delay</w:g>" ;
	}
	
	let repeat_tp = get_input_val("repeat_tp",0,true);
	return {delay_ms:delay_ms,repeat_tp:repeat_tp} ;
}

function set_pm_jo(jo)
{
	let delay_ms = jo.delay_ms ;
	if(delay_ms>0)
	{
		$("#b_delay").prop("checked",true) ;
		$("#delay_ms").val(delay_ms) ;
	}
	else
	{
		$("#b_delay").prop("checked",false) ;
		$("#delay_ms").val(100) ;
	}
		
	$("#repeat_tp").val(jo.repeat_tp||0) ;
}

function get_pm_size()
{
	return {w:700,h:350} ;
}

on_init_pm_ok() ;
</script>