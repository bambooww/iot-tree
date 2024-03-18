<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.record.*,
	org.iottree.core.comp.*
	"%><%!

%><%
%><%@ taglib uri="wb_tag" prefix="wbt"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp"></jsp:include>
    <style>
.sel_item
{
	width:80%;
	margin: 20px;
	margin-left:60px;
	align-content: center;
}
    </style>
    <script type="text/javascript">
    dlg.resize_to(350,400);
    </script>
</head>
<body>

	<div class="sel_item">
		<button class="layui-btn " style="width:80%" onclick="go_to('en')" >English</button>
	</div>
	
	<div class="sel_item">
		<button class="layui-btn " style="width:80%" onclick="go_to('cn')" >中文</button>
	</div>

<script>
function go_to(ln)
{
	//document.location.href="quote_edit.jsp?tp="+tp ;
	dlg.close({lan:ln}) ;
}
</script>
</body>
</html>
