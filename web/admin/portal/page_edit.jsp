<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %>
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
    <div class="layui-input-inline"  style="width:300px;">
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
    <label class="layui-form-label">模板:</label>
    <div class="layui-input-inline"  style="width:300px;">
      <select id="templet_uid" lay-filter="templet_uid">
<%
for(Templet temp:PortalManager.getInstance().listTempletsAll())
{
%><option value="<%=temp.getUID() %>"><%=temp.getCat().getTitle()%>.<%=temp.getTitle() %></option><%
}
%>
      </select>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var input = dlg.get_opener_opt("input") ;
var page_id = "" ;
if(input)
{
	page_id = input.id||"";
	$("#name").val(input.name||"");
	$("#title").val(input.title||"");
	
	if(input.templet_uid)
		$("#templet_uid").val(input.templet_uid)
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
	//if(!name && b_name_need)
	//{
	//	cb(false,"请输入名称") ;return ;
	//}
	let tt = $('#title').val();
	if(!tt)
	{
		cb(false,"请输入标题") ;return ;
	}
	let templet_uid = $("#templet_uid").val() ;
	let desc = $('#desc').val();
	//let num = get_input_val("num",0,true) ; ,num:num
	cb(true,{title:tt,name:name,desc:desc,templet_uid:templet_uid});
}

</script>
</html>                                                                                                                                                                                                                            