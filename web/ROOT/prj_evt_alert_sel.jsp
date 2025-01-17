<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.store.evt_alert.*,
	org.iottree.core.comp.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
		return ;
	String prjid = request.getParameter("prjid") ;
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	MNManager m = MNManager.getInstance(prj) ;
	if(m==null)
	{
		out.print("no mnm found") ;
		return ;
	}
	List<EvtAlertTb> tbs = m.listEvtAlertTb() ;
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="./head.jsp"></jsp:include>
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
for(EvtAlertTb tb:tbs)
{
	String uid = tb.getUID() ;
	String tt = tb.getTitle() +"["+tb.getTableName()+"]";
%>
	<div class="sel_item">
		<button class="layui-btn " style="width:80%" onclick="go_to('<%=uid %>')"><%=tt %></button>
	</div>
<%
}
%>
<script>
var prjid = "<%=prjid%>" ;
function go_to(uid)
{
	document.location.href="/prj_evt_alert_rec.jsp?prjid="+prjid+"&uid="+uid ;
	//dlg.close({tp:tp,tt:tt}) ;
}
</script>
</body>
</html>
