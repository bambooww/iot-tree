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
	org.iottree.core.msgnet.util.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid"><input type="checkbox" class="layui-input" id="b_delay"/>inject once after</div>
    <div class="layui-input-inline" style="width:50px;"><input type="number" class="layui-input" id="delay_ms" /></div>
    <div class="layui-input-mid">mills seconds, then</div>
    
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Repeat:</label>
    <div class="layui-input-inline">
		<select id="repeat_tp"  name="repeat_tp" class="layui-input" lay-filter="repect_tp">
<%
	for(RepeatTP rt:RepeatTP.values())
	{
%>
        <option value="<%=rt.getInt()%>"><%=rt.getTitle() %></option>
<%
	}
%>
      </select>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline">
      <input type="text" id="title" name="title" value=""  class="layui-input">
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
	return {w:700,h:650} ;
}

on_init_pm_ok() ;
</script>