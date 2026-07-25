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
	org.iottree.core.msgnet.nodes.*,
	org.iottree.core.msgnet.modules.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
<style>
textarea {width:100%;height:100px;}
</style>
<div class="layui-form-item">
    <label class="layui-form-label">Max Row Num:</label>
    <div class="layui-input-inline" style="width: 80px;">
      <input type="number" id="max_row" class="layui-input" />
    </div>
 </div>
 <div class="layui-form-item">
    <label class="layui-form-label">SQL Temp:</label>
    <div class="layui-input-inline" style="width: 580px;">
      <textarea id="pm_sql"></textarea>
    </div>
 </div>
 <div class="layui-form-item">
    <label class="layui-form-label">In Sample:</label>
    <div class="layui-input-inline" style="width: 580px;">
      <textarea id="in_sample" placeholder="{xxxxx}"></textarea>
    </div>
 </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Out Sample:</label>
    <div class="layui-input-inline" style="width: 580px;">
      <textarea id="out_sample" placeholder="[xxxx]"></textarea>
    </div>
 </div>
 
<script>


function on_after_pm_show(form)
{
	 
}


function get_pm_jo()
{
	let jo = {} ;
	let max_row = get_input_val("max_row",100,true);
	let pm_sql = $("#pm_sql").val();
	let in_sample = $("#in_sample").val();
	let out_sample = $("#out_sample").val();
	
	return {max_row:max_row,pm_sql:pm_sql,in_sample:in_sample,out_sample:out_sample} ;
}

function set_pm_jo(jo)
{
	$("#max_row").val(jo.max_row||100) ;
	$("#pm_sql").val(jo.pm_sql||"") ;
	$("#in_sample").val(jo.in_sample||"") ;
	$("#out_sample").val(jo.out_sample||"") ;
	
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>