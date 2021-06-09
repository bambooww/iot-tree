<%@ page contentType="text/json;charset=UTF-8" isELIgnored="false"%><%@ page import="java.util.*,
				java.io.*,
				java.net.*,
				java.util.*,
				org.iottree.core.*,
				org.iottree.core.util.*,
				java.net.*"%><%!
				
%><%if(!Convert.checkReqEmpty(request, out, "id"))
		return;
	String id = request.getParameter("id");
	UAPrj rep = UAManager.getInstance().getPrjById(id);
	if(rep==null)
	{
		out.print("no repository found!");
		return;
	}
	
%>{"run":<%=rep.RT_isRunning()%>,
	"cps":
<%
	ConnManager.getInstance().renderRTJson(id, out) ;
%>,"chs":
<%
	UAManager.getInstance().renderRTJson(id, out) ;
%>
}
