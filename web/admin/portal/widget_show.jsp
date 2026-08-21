<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,org.iottree.portal.*,
	java.io.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%>
<%
if(!Convert.checkReqEmpty(request, out, "prjid","ins_uid"))
	return ;
String prjid = request.getParameter("prjid") ;
String ins_uid = request.getParameter("ins_uid") ;
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found");return ;
}
Widget wid = Widget.getWidgetByInsUID(prj, ins_uid) ;
if(wid==null)
{
	out.print("no widget found");return;
}

%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script type="text/javascript" src="/_iottree/widgets/<%=wid.getTPJsPath() %>"></script>
<script>
dlg.resize_to(450,350);

</script>

</head>
<body>
<div id="ccc" style="width:100%;border:1px solid blue;"></div>
</body>
<script type="text/javascript">
var wid = new <%=wid.getTPJsClz()%>("ccc",{}) ;
</script>
</html>                                                                                                                                                                                                                            