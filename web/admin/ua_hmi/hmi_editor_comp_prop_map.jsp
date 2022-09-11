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
				 org.iottree.core.util.xmldata.*"%><%
		//boolean bind_tag_only = "true".equalsIgnoreCase(request.getParameter("bind_tag_only")) ;
%>
<html>
<head>
<title>Edit</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script>
dlg.resize_to(500,500);
</script>
</head>
<body>

</body>
<script type="text/javascript">

var form = null ;

var bind_val = "false";

layui.use('form', function(){
	  form = layui.form;
	  
	  form.on('radio(bexp)', function (data) {
		　
		　　var value = data.value;
			sel_exp_or_not(value);
		});
	  
	  form.render() ;
});

var ow = dlg.get_opener_w() ;
var layer = ow.loadLayer;
var editor= ow.editor;
var plugpm = ow.editor_plugcb_pm;

	
function win_close()
{
	dlg.close(0);
}

</script>
</html>