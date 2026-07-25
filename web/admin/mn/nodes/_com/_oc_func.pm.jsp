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
    <label class="layui-form-label">Function:</label>
    <div class="layui-input-inline" style="width:150px;">
    	<input type="text" class="layui-input" id="func"/>
    </div>
  </div>
<script>

function get_pm_jo()
{
	let func = $("#func").val() ;
	return {func:func} ;
}

function set_pm_jo(jo)
{
	$("#func").val(jo.func||"") ;
}

function get_pm_size()
{
	return {w:600,h:450} ;
}

</script>