<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id","name"))
		return ;
	String container_id = request.getParameter("container_id");
	//String prjid = request.getParameter("prjid");
	MNManager mnm= MNManager.getInstanceByContainerId(container_id) ;
if(mnm==null)
{
	out.print("no MsgNet Manager with container_id="+container_id) ;
	return ;
}
	
	String name = request.getParameter("name") ;
	MNNet oldnet = mnm.getNetByName(name) ;
	String title = "" ;
	if(oldnet!=null)
		title = oldnet.getTitle() ;
%>
<html>
<head>
<title>net editor</title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(600,400);
</script>
<style>
</style>
</head>
<body>
<form class="layui-form" action="">
<%
if(oldnet!=null)
{
%><span style="color:red"><%=oldnet.getTitle()%> [<%=oldnet.getName() %>] is existed.</span>
<%
}
%>
<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline" style="width:200px;">
		<input type="radio" name="replace_or_not" value="true"/>Replace <input type="radio" name="replace_or_not" value="false" checked="checked"/>Create New One
    </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>name</wbt:lang></label>
    <div class="layui-input-inline" style="width:200px;">
      <input type="text" name="name" id="name" value="<%=name %>"  class="layui-input"/>
    </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>title</wbt:lang></label>
    <div class="layui-input-inline" style="width:60%;">
      <input type="text" name="title" id="title" value="<%=title %>"  class="layui-input"/>
    </div>
  </div>
  
</form>
</body>
<script type="text/javascript">

layui.use('form', function(){
	  var form = layui.form;
	  form.render();
	});
	
function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'<wbt:g>pls,input,name</wbt:g>') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt = n;
	}
	let rep = $('input[name="replace_or_not"]:checked').val()=='true';
	cb(true,{name:n,title:tt,replace:rep});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>