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
	if(!Convert.checkReqEmpty(request, out, "prjid","cpid","msgid"))
	return;
	String prjid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
String cid = request.getParameter("cid") ;
String msgid = request.getParameter("msgid") ;
ConnProvider cp = ConnManager.getInstance().getConnProviderById(prjid, cpid);
if(cp==null)
{
	out.print("no provider found ");
	return ;
}
ConnPt cpt = null ;
if(Convert.isNotNullEmpty(cid))
{
	cpt = cp.getConnById(cid) ;
	if(cpt==null)
	{
		out.print("no connection found") ;
		return ;
	}
}
String cptitle = cp.getTitle() ;

ConnMsg msg = null;
if(cpt!=null)
	msg = cpt.getConnMsgById(msgid) ;
else
	msg = cp.getConnMsgById(msgid) ;

if(msg==null)
{
	out.print("no msg with id "+msgid+" found") ;
	return;
}

String dlgu = msg.getDlgUrl();
if(dlgu==null)
	dlgu="" ;
String dlgt = msg.getDlgTitle() ;
if(dlgt==null)
	dlgt= "" ;
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
  <div class="layui-card-header"><%=msg.getTitle() %></div>
  <div class="layui-card-body">
 <%=msg.getDesc() %>
  </div>
</div>
<%
if(Convert.isNotNullEmpty(dlgu))
{
%>
<button onclick="show_d()">Show Detail</button>
<%
}
%>
</body>
<script type="text/javascript">
var form = null;
var dlgu = "<%=dlgu%>" ;
var dlgt = "<%=dlgt%>" ;
layui.use('form', function(){
	  form = layui.form;
	
	  form.render(); 
});

function show_d()
{
	if(!dlgu)
		return ;
	
	dlg.open(dlgu,{title:dlgt,w:'450px',h:'500px'},
			['Close'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
}
</script>
</html>