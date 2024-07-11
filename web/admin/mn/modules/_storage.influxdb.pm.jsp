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
    <label class="layui-form-label">URL:</label>
    <div class="layui-input-inline" style="width: 400px;">
      <input type="text" id="url" name="url" value=""  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
  </div>

  <div class="layui-form-item">
    <label class="layui-form-label">Token:</label>
    <div class="layui-input-inline" style="width: 400px;">
      <input type="text" id="token" name="token" value=""  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    
  </div>
   
   <div class="layui-form-item">
    <label class="layui-form-label">Org:</label>
    <div class="layui-input-inline" style="width: 200px;">
      <input type="text" id="org" name="org" value=""  lay-verify="required"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Bucket:</div>
	  <div class="layui-input-inline" style="width: 200px;">
	    <input type="text" id="bucket" name="bucket" value=""  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
  </div>
<script>


function on_after_pm_show(form)
{
	//update_ui();
}


function get_pm_jo()
{
	let url = $('#url').val();
	if(!url)
	{
		return '<w:g>pls,input</w:g> URL' ;
	}
	
	let token = $('#token').val();
	if(!token)
	{
		return '<w:g>pls,input</w:g> Token' ;
	}
	let org = $('#org').val();
	if(!org)
	{
		return '<w:g>pls,input</w:g> Org' ;
	}
	let bucket = $('#bucket').val();
	if(!bucket)
	{
		return '<w:g>pls,input</w:g> Bucket' ;
	}
	let js_ob={} ;
	js_ob.url = url ;
	js_ob.token = token ;
	js_ob.org = org ;
	js_ob.bucket = bucket ;
	return js_ob ;
}

function set_pm_jo(jo)
{
	$('#url').val(jo.url||"");
	$('#token').val(jo.token||"");
	$("#org").val(jo.org||"") ;
	$("#bucket").val(jo.bucket||"");
}

function get_pm_size()
{
	return {w:600,h:350} ;
}

//on_init_pm_ok() ;
</script>