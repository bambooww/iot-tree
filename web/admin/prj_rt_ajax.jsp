<%@ page contentType="text/json;charset=UTF-8" isELIgnored="false"%><%@ page import="java.util.*,
				java.io.*,org.json.*,
				java.net.*,
				java.util.*,
				org.iottree.core.*,
				org.iottree.core.util.*,
				java.net.*"%><%!
				
%><%if(!Convert.checkReqEmpty(request, out, "op"))
		return;
	String op = request.getParameter("op") ;
	String id = request.getParameter("id");
	UAPrj prj = null;
	if(Convert.isNotNullEmpty(id))
	{
		prj = UAManager.getInstance().getPrjById(id);
		if(prj==null)
		{
			out.print("no project found!");
			return;
		}
	}
	switch(op)
	{
	case "prj_rt":
		if(!Convert.checkReqEmpty(request, out, "id"))
			return;
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
<%
		return ;
	case "prjs_rt_all":
		JSONArray tmpjarr = new JSONArray() ;
		for(UAPrj p:UAManager.getInstance().listPrjs())
		{
			JSONObject jo = new JSONObject().put("id",p.getId()).put("n",p.getName())
					.put("run",p.RT_isRunning()) ;
			tmpjarr.put(jo) ;
		}
		tmpjarr.write(out) ;
		return ;
	default:
		break ;
	}
%>unknown op