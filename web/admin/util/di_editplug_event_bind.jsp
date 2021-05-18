<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.basic.*,
				 org.iottree.core.util.xmldata.*"%>
<html>
<head>
<title>Edit</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(500,550);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-block">
      <input type="text" id="name" name="name" required  lay-verify="required" placeholder="Pls input name" readonly="readonly" class="layui-input">
    </div>
  </div>
  
  <div class="layui-form-item">
    <label class="layui-form-label">Client JS:</label>
    <div class="layui-input-block">
      <textarea id="clientjs" name="clientjs" placeholder="" class="layui-textarea" rows="8"></textarea>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Server JS:</label>
    <div class="layui-input-block">
      <textarea id="serverjs" name="serverjs" placeholder="" class="layui-textarea" rows="8"></textarea>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">

var ow = dlg.get_opener_w() ;
var plugpm = ow.editor_plugcb_pm;
if(plugpm!=null)
{
	$("#name").val(plugpm.name) ;
	var eventb = plugpm.val ;
	if(eventb!=null)
	{
		$("#clientjs").val(eventb.getClientJS()) ;
		$("#serverjs").val(eventb.getServerJS()) ;
	}
		
}

layui.use('form', function(){
	  var form = layui.form;
});
	
function win_close()
{
	dlg.close(0);
}


function editplug_get(cb)
{
	var n = $('#name').val();
	var cjs = $('#clientjs').val();
	var sjs = $('#serverjs').val();
	if(n==null||n=='')
	{
		cb(false,'please input name') ;
		return ;
	}
	return {n:n,clientjs:cjs,serverjs:sjs};
}

</script>
</html>