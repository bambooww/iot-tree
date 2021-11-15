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
	UAPrj prj = UAManager.getInstance().getPrjById(id);
	if(prj==null)
	{
		out.print("no project found!");
		return;
	}
	
	boolean bshare = prj.isShare() ;
	boolean bshare_r = prj.isShareRunning();
	int run_task_n = prj.getTaskRunningNum() ;
	
%>{"run":<%=prj.RT_isRunning()%>,"share":<%=bshare%>,"share_run":<%=bshare_r%>,"task_run_num":<%=run_task_n%>,
	"cps":
<%
	ConnManager.getInstance().renderRTJson(id, out) ;
%>,"chs":
<%
	UAManager.getInstance().renderRTJson(id, out) ;
%>
}
