<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.ext.opcda.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","cpid"))
	return;
	String prjid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
ConnProvider cp = ConnManager.getInstance().getConnProviderById(prjid, cpid);
if(cp==null)
{
	out.print("no provider found ");
	return ;
}

String cptitle = cp.getTitle() ;
String pmsg = cp.getPromptMsg() ;
%>
<html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script>
dlg.resize_to(400,300);
</script>
</head>
<body>
<div class="layui-card">
  <div class="layui-card-header"><%=cptitle %></div>
  <div class="layui-card-body">
 <%=pmsg %>
  </div>
</div>
</body>
<script type="text/javascript">
var form = null;
layui.use('form', function(){
	  form = layui.form;
	
	  form.render(); 
});

</script>
</html>