<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.util.xmldata.*"%><%!
%><%
	if(!Convert.checkReqEmpty(request, out,"tp","repid","id"))
		return ;
	String repid = request.getParameter("repid") ;
	String id = request.getParameter("id") ;
	String tp = request.getParameter("tp") ;

	UARep rep = UAManager.getInstance().getRepById(repid) ;
	if(rep==null)
	{
		out.print("no rep found") ;
		return ;
	}
	switch(tp)
	{
	case "hmi":
		UAHmi hmi = rep.findHmiById(id) ;
		if(hmi==null)
		{
			out.print("no hmi found") ;
			return ;
		}
		String ppath = hmi.getBelongTo().getNodePath() ;
		response.sendRedirect("/iottree"+ppath+"/_hmi/"+hmi.getName()) ;
		return ;
	case "list":
		//response.sendRedirect("/iottree"+ppath+"/_hmi/"+hmi.getName()) ;
	}
	
%>