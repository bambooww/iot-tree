<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%>
<%
	if(!Convert.checkReqEmpty(request, out, "repid","id"))
	return;
String repid = request.getParameter("repid") ;
String id=request.getParameter("id");

UAManager uam = UAManager.getInstance();
UAPrj dc = uam.getPrjById(repid) ;
if(dc==null)
{
	out.print("no rep found with id="+repid) ;
	return ;
}

UANode n = dc.findNodeById(id) ;
if(n==null)
{
	out.print("no node found") ;
	return ;
}
if(!(n instanceof UATag))
{
	out.print("not tag node found") ;
	return ;
}

UATag tag = (UATag)n ;
String curv_str ="" ;

UAVal uav = tag.RT_getVal() ;
if(uav!=null)
{
	Object objv = uav.getObjVal() ;
	if(objv!=null)
		curv_str = objv.toString() ;
}
%>
<html>
<head>
<title>Tag Writer</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(600,400);
</script>
</head>
<body>
<form class="layui-form" action="">
<div class="layui-form-item">
    <label class="layui-form-label">Item Id:</label>
    <div class="layui-input-block">
      <%=tag.getNodePathName() %>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Current Value:</label>
    <div class="layui-input-block">
      <%=curv_str %>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Write Value:</label>
    <div class="layui-input-block">
      <input type="text" id="w_val" name="w_val" required  lay-verify="required" placeholder="Pls input write value" autocomplete="off" class="layui-input">
    </div>
  </div>
   
 </form>
</body>
<script type="text/javascript">

layui.use('form', function(){
	  var form = layui.form;
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
	var n = $('#w_val').val();
	if(n==null||n=='')
	{
		cb(false,'请输入值') ;
		return ;
	}
	
	cb(true,{strv:n});
}

</script>
</html>