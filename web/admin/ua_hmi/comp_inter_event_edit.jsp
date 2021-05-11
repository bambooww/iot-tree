<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
String catid = request.getParameter("catid");
String id = request.getParameter("id");
CompManager cm = CompManager.getInstance() ;
CompCat cc = cm.getCatById(catid) ;
if(cc==null)
{
	out.print("cat not found") ;
	return ;
}
CompItem ci = cc.getItemById(id);
if(ci==null)
{
	out.print("no item found") ;
	return ;
}
String n = request.getParameter("n") ;
if(n==null)
	n = "" ;
%>
<html>
<head>
<title>Component Ctrl Edit</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(400,450);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-block">
      <input type="text" id="name" name="name" required  lay-verify="required" placeholder="Pls input name" autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Title:</label>
    <div class="layui-input-block">
      <input type="text" id="title" name="title" required  lay-verify="required" placeholder="" autocomplete="off" class="layui-input">
    </div>
  </div>
  
 </form>
</body>
<script type="text/javascript">

var ctrl_n = "<%=n%>" ;
if(ctrl_n!="")
{
	var ow = dlg.get_opener_w() ;
	var ctrlitem = ow.loadLayer.getCompInter().getInterEventByName(ctrl_n) ;
	if(ctrlitem!=null)
	{
		$("#name").val(ctrlitem.n) ;
		$("#title").val(ctrlitem.t) ;
	}
}
layui.use('form', function(){
	  var form = layui.form;
});
	
function win_close()
{
	dlg.close(0);
}


function do_submit(cb)
{
	var n = $('#name').val();
	var tt = $('#title').val();
	if(n==null||n=='')
	{
		cb(false,'please input name') ;
		return ;
	}
	cb(true,{n:n,t:tt});
}

</script>
</html>