<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

	 %><%
	String lang = "en" ;
%>
<div class="layui-form-item" id="param2_c">
    <label class="layui-form-label param_title" id="param2_t"><wbt:g>data_num</wbt:g></label>
	 <div class="layui-input-inline" style="width: 250px;" id="param2_d">
	    <input type="number" id="data_num" name="data_num" value="20" class="layui-input" placeholder=""/> 
	  </div>
  </div>
  
   <div class="layui-form-item" id="param1_c">
    <label class="layui-form-label" id="param1_t"><wbt:g>ref_val</wbt:g></label>
	 <div class="layui-input-inline" style="width: 250px;" id="param1_d">
	    <input type="number" id="ref_val" name="ref_val" value="0.5" class="layui-input"/> 
	  </div>
  </div>


  
  <div class="layui-form-item" id="param3_c">
    <label class="layui-form-label param_title" id="param3_t"><wbt:g>trigger_tp</wbt:g></label>
	 <div class="layui-input-inline" style="width: 250px;" id="param3_d">
	 	<select id="trigger_tp">
<%
	for(ValEventTp.GradientTP gtp:ValEventTp.GradientTP.values())
	{
%><option value="<%=gtp.getInt()%>"><%=gtp.getTitle() %></option>
<%
	}
%>
	 	</select>
	  </div>
  </div>
  
  <div class="layui-form-item" >
    <label class="layui-form-label param_title" "><wbt:g>prompt</wbt:g></label>
	 <div class="layui-input-inline" style="width: 250px;" >
	    <input type="text" id="prompt" name="prompt" value="" class="layui-input"/> 
	  </div>
  </div>
  <div id="tp_ppt" class="prompt">
  </div>
<script type="text/javascript">

function get_param_jo()
{
	let data_num = get_input_val("data_num",20,true) ;
	let ref_val = get_input_val("ref_val",1.0,true) ;
	let trigger_tp = get_input_val("trigger_tp",0,true) ;
	let prompt = $("#prompt").val() ;
	let ret={data_num:data_num,ref_val:ref_val,trigger_tp:trigger_tp,prompt:prompt} ;
	//console.log("get_param_jo",ret) ;
	return ret;
}

function get_param_tt()
{
	return $('#trigger_tp option:selected').text() +"-"+get_input_val("ref_val",1.0,true) ;
}

function set_param_jo(jo)
{//console.log("set_param_jo",jo) ;
	if(!jo) jo={};
	
	$("#data_num").val(jo.data_num||10) ;
	$("#ref_val").val(jo.ref_val||1.0);
	$("#trigger_tp").val(jo.trigger_tp||0) ;
	$("#prompt").val(jo.prompt||"") ;
}

</script>