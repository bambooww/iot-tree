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
    <label class="layui-form-label"><w:g>buf_len</w:g>:</label>
    <div class="layui-input-inline" style="width:150px;">
    	<input type="number" class="layui-input" id="buf_len"/>
    </div>
  </div>
  <%--
  <div class="layui-form-item">
    <label class="layui-form-label">To</label>
    <div class="layui-input-inline" style="width:250px;">
    	<input type="checkbox" class="layui-input" id="out_win"/>debug window
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline" style="width:250px;">
    	<input type="checkbox" class="layui-input" id="out_console"/>system console
    </div>
  </div>
   --%>
<script>

function get_pm_jo()
{
	let out_win = $("#out_win").prop("checked") ;
	let out_console = $("#out_console").prop("checked") ;
	let buf_len = get_input_val("buf_len",true,100) ;
	return {out_win:out_win,out_console:out_console,buf_len:buf_len} ;
}

function set_pm_jo(jo)
{
	$("#out_win").prop("checked",!(jo.out_win===false)) ;
	$("#out_console").prop("checked",jo.out_console) ;
	$("#buf_len").val(jo.buf_len||100) ;
}

function get_pm_size()
{
	return {w:700,h:450} ;
}

</script>