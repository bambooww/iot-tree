<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.util.*,org.iottree.core.util.logger.*"%>
<%
boolean inctrl = "true".equalsIgnoreCase(request.getParameter("ctrl")) ;
if(!inctrl)
{
	LoggerManager.setupLoggerDefault() ;
	return ;
}

boolean btrace = "true".equalsIgnoreCase(request.getParameter("trace")) ;
String logids = request.getParameter("logids") ;
StringTokenizer st = new StringTokenizer(logids,"|") ;
HashSet<String> hs = new HashSet<String>() ;
while(st.hasMoreTokens())
	hs.add(st.nextToken()) ;

LoggerManager.setupLoggerInCtrl(hs,btrace) ;
hs = LoggerManager.getInCtrlEnableIds() ;
if(hs!=null)
{
	for(Iterator<String> ir=hs.iterator();ir.hasNext();)
	{
%><%=ir.next()%>,<%
	}
}
%>