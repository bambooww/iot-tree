<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
String id=request.getParameter("id");
String name = null ;
String title = "" ;
String desc = "" ;
if(Convert.isNotNullEmpty(id))
{
	UARep dc = UAManager.getInstance().getRepById(id) ;
	if(dc!=null)
	{
		name = dc.getName();
		title = dc.getTitle() ;
		desc = dc.getDesc() ;
	}
}

if(id==null)
	id = "" ;
if(name==null)
	name = "" ;
if(title==null)
	title = "" ;
if(desc==null)
	desc = "" ;
%>
<html>
<head>
<title>rep editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<form class="layui-form" action="">
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>name</wbt:lang></label>
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="text" name="name" id="name" value="<%=name%>" placeholder="<wbt:lang>pls_input</wbt:lang><wbt:lang>name</wbt:lang>" autocomplete="off" class="layui-input"/>
    </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>title</wbt:lang></label>
    <div class="layui-input-block">
      <input type="text" name="title" id="title" value="<%=title %>" placeholder="<wbt:lang>pls_input</wbt:lang><wbt:lang>title</wbt:lang>" autocomplete="off" class="layui-input"/>
    </div>
  </div>
   <div class="layui-form-item layui-form-text">
    <label class="layui-form-label"><wbt:lang>description</wbt:lang></label>
    <div class="layui-input-block">
      <textarea name="desc" id="desc" placeholder="<wbt:lang>pls_input</wbt:lang><wbt:lang>description</wbt:lang>" class="layui-textarea"><%=desc %></textarea>
    </div>
  </div>
</form>
</body>
<script type="text/javascript">
function win_close()
{
	dlg.close(0);
}
function do_submit(cb)
{
	var n = document.getElementById('name').value;
	if(n==null||n=='')
	{
		cb(false,'<wbt:lang>pls_input</wbt:lang><wbt:lang>name</wbt:lang>') ;
		return ;
	}
	var tt = document.getElementById('title').value;
	if(tt==null||tt=='')
	{
		cb(false,'<wbt:lang>pls_input</wbt:lang><wbt:lang>title</wbt:lang>') ;
		return ;
	}
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	//var dbname=document.getElementById('db_name').value;
	var pms = "name="+n+'&title='+tt+"&desc="+desc ;
	send_ajax('rep_edit_do_ajax.jsp',pms,function(bsucc,ret)
	{
		if(!bsucc || ret.indexOf('succ')<0)
		{
			cb(false,ret) ;
			return ;
		}
		cb(true,ret);
	},false);
	//document.getElementById('form1').submit() ;
}

</script>
</html>