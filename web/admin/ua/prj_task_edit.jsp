<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.task.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	String prjid=request.getParameter("prjid");
	String id=request.getParameter("taskid");
	Task jst = null ;
	if(Convert.isNotNullEmpty(id))
	{
		jst = TaskManager.getInstance().getTask(prjid, id) ;
		if(jst==null)
		{
	out.print("no task found") ;
	return ;
		}
	}
String name = "" ;
long int_ms = Task.DEFAULT_INT_MS ;
String desc = "" ;

if(jst!=null)
{
		name = jst.getName() ;
		desc = jst.getDesc() ;
		int_ms = jst.getIntervalMS();
}

if(id==null)
	id = "" ;
if(name==null)
	name = "" ;

if(desc==null)
	desc = "" ;
%>
<html>
<head>
<title>task editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(600,300);
</script>
</head>
<body>
<form class="layui-form" action="">
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>name</wbt:lang></label>
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="text" name="name" id="name" value="<%=name%>" autocomplete="off" class="layui-input"/>
    </div>
    <label class="layui-form-mid">Interval MS</label>
    <div class="layui-input-inline">
      <input type="text" name="int_ms" id="int_ms" value="<%=  int_ms %>" autocomplete="off" class="layui-input"/>
    </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label">Description</label>
    <div class="layui-input-block" >
      <input type="text" name="desc" id="desc" value="<%=desc %>" autocomplete="off" class="layui-input"/>
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
	
	//var desc = document.getElementById('desc').value;
	//if(desc==null)
		desc =$("#desc").val(); ;
		
	var int_ms = $("#int_ms").val();
	var int_ms = parseInt(int_ms);
	if(int_ms==NaN||int_ms<=0)
	{
		cb(false,'Please input valid interval ms') ;
	}
	cb(true,{name:n,desc:desc,int_ms:int_ms})
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>