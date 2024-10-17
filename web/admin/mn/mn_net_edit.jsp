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
	if(!Convert.checkReqEmpty(request, out, "container_id"))
		return ;
	String container_id = request.getParameter("container_id");
	//String prjid = request.getParameter("prjid");
	MNManager mnm= MNManager.getInstanceByContainerId(container_id) ;
if(mnm==null)
{
	out.print("no MsgNet Manager with container_id="+container_id) ;
	return ;
}
	
	String name="" ;
	String title = "" ;
	String desc="" ;
	
	//MNManager mnm = MNManager.getInstance(prj) ;
	String id = request.getParameter("netid") ;
	boolean benable = true ;
	if(Convert.isNotNullEmpty(id))
	{
		MNNet net = mnm.getNetById(id) ;
		if(net==null)
		{
			out.print("no net found with id="+id) ;
			return ;
		}
		name  = net.getName() ;
		title =net.getTitle() ;
		desc = net.getDesc() ;
		benable = net.isEnable() ;
		if(title==null)
			title ="" ;
		if(desc==null)
			desc = "" ;
	}
	
	String ben_chked = benable?"checked":"" ;
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
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>name</wbt:lang></label>
    <div class="layui-input-inline" style="width:200px;">
      <input type="text" name="name" id="name" value="<%=name %>"  class="layui-input"/>
    </div>
    
     <div class="layui-input-inline" style="width:30px;">
      <input type="checkbox" class="layui-input" lay-skin="primary" id="enable"  <%=ben_chked %> />
    </div>
    <div class="layui-form-mid" style="width:50px;">Enable</div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>title</wbt:lang></label>
    <div class="layui-input-inline" style="width:60%;">
      <input type="text" name="title" id="title" value="<%=title %>"  class="layui-input"/>
    </div>
  </div>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label"><wbt:lang>description</wbt:lang></label>
    <div class="layui-input-inline" style="width:60%;">
      <textarea name="desc" id="desc" class="layui-textarea"><%=desc %></textarea>
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
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	let benable = $("#enable").prop("checked") ;
	cb(true,{name:n,title:tt,desc:desc,enable:benable});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>