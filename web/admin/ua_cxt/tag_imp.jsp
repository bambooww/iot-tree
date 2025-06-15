<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.json.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

%><%
	
%><html>
<head>
<title>Tag Importer </title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>

<style type="text/css">

</style>
<script>
dlg.resize_to(850,600);
</script>

</head>
<body>

<textarea id="txt" style="width:800px;height:470px;margin-left:25px;" placeholder="[tagname] [value type] [address] [title] or [...]"></textarea>
</body>
<script type="text/javascript">
function do_submit(cb)
{
	let txt = $("#txt").val() ;
	if(!txt)
	{
		cb(false,"no txt input") ;
		return;
	}
	cb(true,txt) ;
}
</script>
</html>