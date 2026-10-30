<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.store.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.ai.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%>
<%
	String n = request.getParameter("n") ;

if(n==null)
	n="" ;
String t="" ;
String api_h = "" ;
String api_k="" ;
String chat_u = "" ;
String modellist_u = "";

AIApiKey ps = null ;
if(Convert.isNotNullEmpty(n))
{
	ps = AIApiKey.getApiKey(n) ;
	if(ps==null)
	{
		out.print("no api key found") ;
		return ;
	}
	t = ps.getTitle() ;
	api_h = ps.getApiHost() ;
	api_k = ps.getApiKey() ;
	chat_u = ps.getChatUrl() ;
	modellist_u = ps.getModelListUrl() ;
}
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(700,450);
</script>
<style>
.layui-form-label {width:100px;}
</style>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>name</w:g>:</label>
    <div class="layui-input-inline" style="width: 150px;">
      <input type="text" id="n" name="n" value="<%=n%>"  autocomplete="off"  class="layui-input" <%=(ps!=null?"readonly":"") %>>
    </div>
	  <div class="layui-form-mid"><w:g>title</w:g>:</div>
	  <div class="layui-input-inline" style="width: 250px;">
	    <input type="text" id="t" name="t" value="<%=t%>"  autocomplete="off" class="layui-input">
	  </div>
 </div>
 
 <div class="layui-form-item">
<div class="layui-form-label"><w:g>host</w:g>:</div>
	  <div class="layui-input-inline" style="width: 250px;">
	    <select id="api_h" name="api_h"  lay-filter="api_h">
	    	<option value=""> --- </option>
	    	<option value="api.openai.com">api.openai.com</option>
	    	<option value="api.deepseek.com">api.deepseek.com</option>
	    </select>
	  </div>
</div>

  <div class="layui-form-item">
<div class="layui-form-label">Chat URL:</div>
	  <div class="layui-input-inline" style="width: 500px;">
	    <input type="text" id="chat_u" name="chat_u" value="<%=modellist_u%>"  autocomplete="off" class="layui-input">
	  </div>
</div>
  <div class="layui-form-item">
<div class="layui-form-label"><w:g>model,list</w:g> URL:</div>
	  <div class="layui-input-inline" style="width: 500px;">
	    <input type="text" id="modellist_u" name="modellist_u" value="<%=modellist_u%>"  autocomplete="off" class="layui-input">
	  </div>
</div>
  

  <div class="layui-form-item">
<div class="layui-form-label">Key:</div>
	  <div class="layui-input-inline" style="width: 250px;">
	    <input type="text" id="api_k" name="api_k" value="<%=api_k%>"  autocomplete="off" class="layui-input">
	  </div>
</div>
 </form>
</body>
<script type="text/javascript">
var n = "<%=n%>" ;

layui.use('form', function(){
	  var form = layui.form;
	  $("#api_h").val("<%=api_h%>");
	  form.on('select(api_h)', function(obj){
		  let api_h =$("#api_h").val() ;
		  if(api_h)
		  {
			  $("#chat_u").val("https://"+api_h+"/chat/completions");
			  $("#modellist_u").val("https://"+api_h+ "/v1/models");
		  }
	  });
	  form.render();
});

function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	let n = $('#n').val();
	if(!n)
	{
		cb(false,'<w:g>pls,input,name</w:g>') ;
		return ;
	}
	let t = $('#t').val();
	if(t==null||t=='')
	{
		t = "";
	}
	let api_h = $('#api_h').val();
	let api_k = $('#api_k').val();
	let chat_u = $('#chat_u').val();
	let modellist_u = $('#modellist_u').val();
	
	cb(true,{n:n,t:t,api_h:api_h,chat_u:chat_u,modellist_u:modellist_u,api_k:api_k});
}

</script>
</html>