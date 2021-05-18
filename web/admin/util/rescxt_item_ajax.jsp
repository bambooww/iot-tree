<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.util.xmldata.*"%><%!
%><%
	if(!Convert.checkReqEmpty(request, out,"op","cxtid"))
		return ;
	String cxtid = request.getParameter("cxtid") ;
	String op = request.getParameter("op") ;

	ResCxt rc = ResCxtManager.getInstance().getResCxt(cxtid) ;
	if(rc==null)
	{
		out.print("no ResCxt found") ;
		return ;
	}
	
	switch(op)
	{
	case "add":
		
	case "del":
	
	}
	
%>