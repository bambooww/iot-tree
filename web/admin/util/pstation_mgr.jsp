<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.store.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.station.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%>
<%

%>
<html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(800,600);
</script>
<style>
</style>
</head>
<body>
<table>
	<tr>
		<td></td>
		<td></td>
	</tr>
</table>
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

</script>
</html>