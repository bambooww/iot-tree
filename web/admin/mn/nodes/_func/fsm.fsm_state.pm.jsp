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
<style>

.seg .del
{
	right:2px;
	top:2px;z-index:10;
	width:20px;
	height:20px;
	color:#dddddd;
	border-color:#dddddd;
}
.seg .del:hover
{
	background-color: red;
	
}
</style>
 	
 <div class="layui-form-item">
    <label class="layui-form-label">Start:</label>
<div class="layui-form-mid">
    <input type="checkbox" class="layui-input" id="start" lay-skin="primary" lay-filter="start" />
    </div>
    <div class="layui-input-inline" style="width:350px;">
    	
    </div>
    <div class="layui-form-mid">
    
    </div>
     <div class="layui-form-mid">&nbsp;</div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label">Active Run Interval:</label>
<div class="layui-form-mid">
    <input type="number" class="layui-input" id="act_run_intv" lay-skin="primary" value=""/>
    </div>
    <div class="layui-input-inline" style="width:350px;">
    	
    </div>
    <div class="layui-form-mid">
    
    </div>
     <div class="layui-form-mid">&nbsp;</div>
</div>

<script>


function on_after_pm_show(form)
{
	form.on('checkbox(start)', function (data) {
		//update_bt();
	});

	//update_bt();
}

function get_pm_jo()
{
	let b_start = $("#start").prop("checked") ;
	let act_run_intv = get_input_val("act_run_intv",1000,true);
	
	return {start:b_start,act_run_intv:act_run_intv} ;
}

function set_pm_jo(jo)
{
	$("#start").prop("checked",jo.start||false) ;
	$("#act_run_intv").val(jo.act_run_intv||1000);
	
}

function get_pm_size()
{
	return {w:700,h:450} ;
}

//on_init_pm_ok() ;
</script>