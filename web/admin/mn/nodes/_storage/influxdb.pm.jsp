<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
<div class="layui-form-item">
    <label class="layui-form-label">Using Source:</label>
    <div class="layui-input-inline" style="width: 400px;">
      <input type="checkbox" id="using_sor" name="using_sor"  lay-filter="using_sor"  autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item" id="is_sor">
    <label class="layui-form-label">Source Name:</label>
    <div class="layui-input-inline" style="width: 400px;">
      <select id="sor_name" name="sor_name" >
      <option value=""> --- </option>
<%
	for(SourceInfluxDB sor:StoreManager.listSourcesInfluxDB())
	{
%><option value="<%=sor.getName() %>"><%=sor.getTitle() %> - <%=sor.getName() %></option>
<%
	}
%>
      </select>
    </div>
  </div>
  
  <div id="not_sor" style="display:none">
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
  </div>
<script>


function on_after_pm_show(form)
{
	form.on("checkbox(using_sor)",function(obj){
		update_ui() ;
	}) ;
}

function update_ui()
{
	let using = $("#using_sor").prop("checked") ;
	if(using)
	{
		$("#is_sor").css("display","") ;
		$("#not_sor").css("display","none") ;
	}
	else
	{
		$("#is_sor").css("display","none") ;
		$("#not_sor").css("display","") ;
	}
}

function get_pm_jo()
{
	let using_sor = $("#using_sor").prop("checked") ;
	let sor_name = $("#sor_name").val() ;
	let url = $('#url').val();
	let token = $('#token').val();
	let org = $('#org').val();
	let bucket = $('#bucket').val();
	
	if(using_sor)
	{
		if(!sor_name)
			return '<w:g>pls,input</w:g> Source Name' ;
	}
	else
	{
		if(!url)
		{
			return '<w:g>pls,input</w:g> URL' ;
		}
		if(!token)
		{
			return '<w:g>pls,input</w:g> Token' ;
		}
		if(!org)
		{
			return '<w:g>pls,input</w:g> Org' ;
		}
		if(!bucket)
		{
			return '<w:g>pls,input</w:g> Bucket' ;
		}
	}
	
	
	let js_ob={} ;
	js_ob.using_sor = using_sor ;
	js_ob.sor_name = sor_name ;
	js_ob.url = url ;
	js_ob.token = token ;
	js_ob.org = org ;
	js_ob.bucket = bucket ;
	return js_ob ;
}

function set_pm_jo(jo)
{
	$("#using_sor").prop("checked",jo.using_sor||true) ;
	$("#sor_name").val(jo.sor_name||"");
	$('#url').val(jo.url||"");
	$('#token').val(jo.token||"");
	$("#org").val(jo.org||"") ;
	$("#bucket").val(jo.bucket||"");
	
	update_ui() ;
}

function get_pm_size()
{
	return {w:600,h:650} ;
}

//on_init_pm_ok() ;
</script>