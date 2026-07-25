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
	org.iottree.core.msgnet.nodes.*,
	org.iottree.core.msgnet.util.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>

<div class="layui-form-item">
    <label class="layui-form-label"><select id="method"  class="layui-input" lay-filter="method" style="width:80px;border-right: 0px;">
<%
	for(NM_HttpClient.Method m:NM_HttpClient.Method.values())
	{
%><option value="<%=m.name()%>" ><%=m.name() %></option>
<%
	}
%>
    </select></label>
    <div class="layui-form-mid" style="width:30px;">
    Url
    </div>
    <div class="layui-input-inline" style="width:80px;">
    <select id="url_sty"  class="layui-input" lay-filter="url_sty" style="width:80px;border-right: 0px;">
<%
	for(MNCxtValSty vs:MNCxtValSty.FOR_STR_LIST)
	{
%>
        <option value="<%=vs.name()%>"><%=vs.getTitle() %>.</option>
<%
	}
%>
    </select>
    </div>
    <div class="layui-input-inline" style="width:350px;">
    <input type="text" id="url_subn" class="layui-input" style="border-left: 0px;left:2px;"/>
    </div>
</div>
 <div class="layui-form-item">
    <label class="layui-form-label">Heads:</label>
    <div class="layui-input-inline" style="width:550px;height:60px;">
    	<textarea id="head_txt" style="width:100%;height:100%;border:1px solid #ccc;"></textarea>
    </div>
</div>
 <div class="layui-form-item" id="body_c">
    <label class="layui-form-label">Body:</label>
    <div class="layui-input-inline" style="width:550px;height:250px;">
    	<textarea id="post_body" style="width:100%;height:100%;border:1px solid #ccc;"></textarea>
    </div>
</div>
 <div class="layui-form-item">
    <label class="layui-form-label">Response:</label>
    <div class="layui-input-inline" style="width:150px;">
    	<select id=resp_fmt  class="layui-input" lay-filter="resp_fmt" style="width:80px;border-right: 0px;">
<%
	for(NM_HttpClient.RespFmt rf:NM_HttpClient.RespFmt.values())
	{
%><option value="<%=rf.name()%>"><%=rf.name() %></option>
<%
	}
%>
    </select>
    </div>
</div>
<script>

function on_after_pm_show(form)
{
	//form.render();
	form.on('select(method)', function(data){   
		update_method()
	 });
	
	update_method()
}

function update_method()
{
	let m = $("#method").val();
	console.log(m) ;
	let dis = ("POST"==m?"block":"none");
	$("#body_c").css("display",dis);
}

function get_pm_jo()
{
	let method = $("#method").val();
	let url_sty = $("#url_sty").val() ;
	let url_subn = $("#url_subn").val() ;
	let head_txt = $("#head_txt").val();
	let resp_fmt = $("#resp_fmt").val() ;
	let post_body = $("#post_body").val();
	return {method:method,url_sty:url_sty,url_subn:url_subn,resp_fmt:resp_fmt,head_txt:head_txt,post_body:post_body} ;
}

function set_pm_jo(jo)
{
	if(jo.method)
		$("#method").val(jo.method||"POST") ;
	if(jo.url_sty)
		$("#url_sty").val(jo.url_sty) ;
	if(jo.url_subn)
		$("#url_subn").val(jo.url_subn) ;
	if(jo.resp_fmt)
		$("#resp_fmt").val(jo.resp_fmt) ;
	$("#head_txt").val(jo.head_txt||"") ;
	$("#post_body").val(jo.post_body||"") ;
}

function get_pm_size()
{
	return {w:700,h:550} ;
}

//on_init_pm_ok() ;
</script>