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
	if(!Convert.checkReqEmpty(request, out, "prjid"))
		return ;

	String prjid = request.getParameter("prjid");
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	
	String name="" ;
	String title = "" ;
	String desc="" ;
	
	MNManager mnm = MNManager.getInstance(prj) ;
	String id = request.getParameter("netid") ;
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
		if(title==null)
			title ="" ;
		if(desc==null)
			desc = "" ;
	}
	
%>
<html>
<head>
<title>net editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
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
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="text" name="name" id="name" value="<%=name %>"  class="layui-input"/>
    </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>title</wbt:lang></label>
    <div class="layui-input-block">
      <input type="text" name="title" id="title" value="<%=title %>"  class="layui-input"/>
    </div>
  </div>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label"><wbt:lang>description</wbt:lang></label>
    <div class="layui-input-block">
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
	cb(true,{name:n,title:tt,desc:desc});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>