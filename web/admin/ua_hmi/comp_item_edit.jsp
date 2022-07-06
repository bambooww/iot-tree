<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.comp.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
String libid =request.getParameter("libid") ;
String catid =request.getParameter("catid") ;
String compid =request.getParameter("compid") ;
String title = "" ;
if(Convert.isNotNullEmpty(libid) && Convert.isNotNullEmpty(catid) && Convert.isNotNullEmpty(compid))
{
	CompItem ci = CompManager.getInstance().getCompItemById(libid, catid,compid) ;
	if(ci!=null)
		title = ci.getTitle() ;
}
%>
<html>
<head>
<title>Item Editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(400,200);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Title:</label>
    <div class="layui-input-block">
      <input type="text" id="title" name="title" value="<%=title %>" class="layui-input">
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
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'please input title') ;
		return ;
	}
	cb(true,{title:tt});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>