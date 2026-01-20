<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.json.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.router.*,
	org.iottree.core.dict.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.msgnet.*
	"%><%!

%><%
String op = request.getParameter("op") ;
StringBuilder failedr = new StringBuilder() ;
try
{
	switch(op)
	{
	case "res_caller_list":
		if(!Convert.checkReqEmpty(request, out,"res_cat"))
			return ;
		String res_cat = request.getParameter("res_cat") ;
		ResCat resc = MNManager.getResCat(res_cat) ;
		JSONArray res_ccs = new JSONArray() ;
		if(resc!=null)
		{
			List<ResCaller> callers = resc.listCallers() ;
			for(ResCaller caller:callers)
			{
				String caller_n = caller.getCallerName() ;
				String caller_t = caller.getCallerTitle() ;
				
				res_ccs.put(new JSONObject().put("name",caller_n).putOpt("title",caller_t)) ;
			}
		}
		res_ccs.write(out) ;
		return ;
	default:
		out.print("unknown op="+op) ;
		return ;
	}
}
catch(Exception e)
{
	//e.printStackTrace();
	out.print(e.getMessage()) ;
}
%>