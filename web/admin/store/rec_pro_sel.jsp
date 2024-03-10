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
	boolean b_l1 = "true".equals(request.getParameter("l1")) ;
	boolean b_l2 = "true".equals(request.getParameter("l2")) ;
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
<%
	if(b_l1)
	{
		for(RecProL1 p:RecPro.listProL1())
		{
			String tp = p.getTp() ;
			String tt = p.getTpTitle() ;
			String dd = p.getTpDesc() ;
%>
	<div class="sel_item">
		<button class="layui-btn " style="width:80%" onclick="go_to('<%=tp %>','<%=tt%>')" title="<%=dd%>"><%=tt %></button>
	</div>
<%
		}
	}
%>

<script>
function go_to(tp,tt)
{
	//document.location.href="quote_edit.jsp?tp="+tp ;
	dlg.close({tp:tp,tt:tt}) ;
}
</script>
</body>
</html>
