<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.util.web.*,
	org.iottree.core.util.xmldata.*
"%><%
	String user = request.getParameter("user") ;
	String psw = request.getParameter("psw") ;
	String op =  request.getParameter("op") ;
	switch(op)
	{
	case "login":
		if(LoginUtil.doLogin(request, user, psw))
		{
			out.print("succ") ;
		}
		else
		{
			out.print("failed") ;
		}
		break ;
	case "logout":
		LoginUtil.doLogout(request) ;
		out.print("ok") ;
		break ;
	}
	
%>