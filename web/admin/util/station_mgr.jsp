<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.json.*,
	org.iottree.core.station.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*
	"%><%!

%><%
StationLocal sl = StationLocal.getInstance() ;
if(sl==null)
{
	out.print("Not station instance") ;
	return ;
}
String host = sl.getPlatfromHost() ;
int port = sl.getPlatfromPort() ;
String id = sl.getStationId() ;
boolean brun = sl.isRunning() ;
boolean bconn = sl.isConnReady() ;
%>ID=<%=id%> Platform=<%=host%>:<%=port%><br>
running=<%=brun%>  conn=<%=bconn%><br>
conn state=<%=sl.getConnState()%>