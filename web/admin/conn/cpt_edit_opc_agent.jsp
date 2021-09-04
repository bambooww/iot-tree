<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","cpid"))
	return;
String prjid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
if(connid==null)
	connid="" ;
%>
<jsp:include page="./cpt_edit_tcp_server.jsp">
	<jsp:param name="prjid" value="<%=prjid %>"/>
	<jsp:param name="cpid" value="<%=cpid %>"/>
	<jsp:param name="connid" value="<%=connid %>"/>
</jsp:include>
