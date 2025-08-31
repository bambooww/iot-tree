<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %>
<%
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
    <style>
.sel_item
{
	width:80%;
	margin: 20px;
	margin-left:60px;
	align-content: center;
}
    </style>

<script>
dlg.resize_to(450,350);
</script>
</head>
<body>
<%
for(PageBlk pb:PageBlk.getAllTPs())
{
%>
	<div class="sel_item">
		<button class="layui-btn " style="width:80%" onclick="go_to('<%=pb.getTP() %>','<%=pb.getTPT() %>')"><%=pb.getTPT() %></button>
	</div>
<%
}
%>

<div class="sel_item">
		<button class="layui-btn layui-primary layui-btn-danger " style="width:80%" onclick="go_to('','')">删除设置内容</button>
	</div>
</body>
<script type="text/javascript">
function go_to(tp,tt)
{
	//document.location.href="quote_edit.jsp?tp="+tp ;
	dlg.close({tp:tp,tt:tt}) ;
}
</script>
</html>                                                                                                                                                                                                                            