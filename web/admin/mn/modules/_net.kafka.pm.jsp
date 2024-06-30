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

function on_after_pm_show(form)
{
	form.on('select(sec_proto)', function(obj){
		  update_fm();
	  });
	
	update_fm();
}


function update_fm()
{
	let v = parseInt($("#sec_proto").val());
	switch(v)
	{
	case 2: //SASL_PLAINTEXT(2
		$(".sasl_mech").css("display","block") ;
		$(".user_psw").css("display","block") ;
		break;
	default:
		$(".sasl_mech").css("display","none") ;
		$(".user_psw").css("display","none") ;
		break ;
	}
	form.render();
}

function get_pm_jo()
{
	var host = $('#host').val();
	if(host==null||host=='')
	{
		return '<w:g>pls,input,host</w:g>';
	}
	var port = $('#port').val();
	if(port==null||port=='')
	{
		return '<w:g>pls,input,port</w:g>' ;
	}
	var vp = parseInt(port);
	if(vp==NaN||vp<0)
	{
		return '<w:g>pls,input,valid,port</w:g>' ;
	}
	
	let send_to = get_input_val('send_to',1000,true) ;
	let sec_proto= get_input_val('sec_proto',0,true) ;
	let sec_sasl_mech= get_input_val('sec_sasl_mech',0,true) ;
	var user = $('#user').val();
	if(user==null||user=='')
	{
		user="";
	}
	
	var psw = $('#psw').val();
	if(psw==null||psw=='')
	{
		psw="";
	}
	
	let js_ob={};
	js_ob.host = host ;
	js_ob.port = port ;
	js_ob.sec_proto = sec_proto ;
	js_ob.sec_sasl_mech = sec_sasl_mech;
	js_ob.send_to=send_to;
	js_ob.user = user ;
	js_ob.psw = psw ;
	return js_ob ;
}

function set_pm_jo(jo)
{
	$('#host').val(jo.host||"");
	$('#port').val(jo.port||9092);
	$("#sec_proto").val(jo.sec_proto||0);
	$("#send_to").val(jo.send_to||1000);
	$("#sec_sasl_mech").val(jo.sec_sasl_mech||0);
	$("#user").val(jo.user||"");
	$("#psw").val(jo.psw||"");
}

function get_pm_size()
{
	return {w:700,h:350} ;
}

//on_init_pm_ok() ;
</script>