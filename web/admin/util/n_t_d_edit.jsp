<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(450,350);
</script>
</head>
<body>
<form class="layui-form" action="">
	<div class="layui-form-item">
    <label class="layui-form-label">名称:</label>
    <div class="layui-input-inline" style="width:200px;">
      <input type="text" id="name" name="name" value=""  autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>title</wbt:lang>:</label>
    <div class="layui-input-inline"  style="width:300px;">
      <input type="text" id="title" name="title" value=""  autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item" id="cont_desc">
    <label class="layui-form-label">描述:</label>
    <div class="layui-input-inline"  style="width:300px;">
      <input type="text" id="desc" name="desc" value=""  autocomplete="off" class="layui-input">
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var input = dlg.get_opener_opt("input") ;
var b_name_need=false;
var b_title_need=false;
if(input)
{
	$("#name").val(input.name||"");
	$("#title").val(input.title||"");
	$("#desc").val(input.desc||"");
	if(input.no_desc==true)
		$("#cont_desc").css("display","none") ;
	b_name_need =input.name_need||false;
	b_title_need =input.b_title_need||false;
}
var form ;
layui.use('form', function(){
	  form = layui.form;
	  form.render() ;
});
	
function win_close()
{
	dlg.close(0);
}

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
}

function do_submit(cb)
{
	let name = $('#name').val();
	if(!name && b_name_need)
	{
		cb(false,"请输入名称") ;return ;
	}
	let tt = $('#title').val();
	if(!tt && b_title_need)
	{
		cb(false,"请输入标题") ;return ;
	}
	let desc = $('#desc').val();
	//let num = get_input_val("num",0,true) ; ,num:num
	cb(true,{title:tt,name:name,desc:desc});
}

</script>
</html>                                                                                                                                                                                                                            