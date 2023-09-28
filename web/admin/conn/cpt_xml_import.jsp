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
String repid = request.getParameter("prjid") ;
String cpid =  request.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnProvider cp = ConnManager.getInstance().getConnProviderById(repid, cpid);
if(cp==null)
{
	out.print("no conn provider found with ");
	return ;
}
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(800,600);
</script>
</head>
<body>
<div class="layui-form-item">
    <label class="layui-form-label">&nbsp;</label>
    <div class="layui-input-inline" style="width:500px">
     <span style="color:green"><%=cp.getTitle() %> </span><br>
     <b>You can paste xml from clipboard, and import to create same node.</b>
    </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline" style="width:700px">
      <textarea  id=xml_str  name="xml_str"  style="height:30px;width:100%;height:400px;border-color: #e6e6e6;overflow:scroll;"></textarea>
    </div>
  </div>
</body>
<script type="text/javascript">
function get_xml_str()
{
	return $("#xml_str").val() ;
}
</script>
</html>