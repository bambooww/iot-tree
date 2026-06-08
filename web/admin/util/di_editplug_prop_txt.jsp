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

%>
<html>
<head>
<title>Edit</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src='/_js/tinycolor/tinycolor-min.js'></script>
<script src="/_js/tinycolor/colorpicker.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/tinycolor/style.css" />
<script>
dlg.resize_to(520,550);
</script>
</head>
<body>
<textarea id="txt" style="width:520px;height:450px;border:2px solid;"></textarea>
</body>
<script type="text/javascript">

$(document).ready(function() {
    
});

var ow = dlg.get_opener_w() ;
var plugpm = null;
if(ow)
	plugpm = ow.editor_plugcb_pm;
if(plugpm!=null)
{
	$("#txt").html(plugpm.val||"") ;
}


layui.use('form', function(){
	  var form = layui.form;
});
	
function win_close()
{
	dlg.close(0);
}


function editplug_get()
{
	return {v:$("#txt").val()||""};
}

</script>
</html>