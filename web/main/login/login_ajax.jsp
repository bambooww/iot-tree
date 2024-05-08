<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page import="java.util.*,
				java.io.*,
				java.net.*,
				java.util.*,
				org.iottree.core.*,
				org.iottree.core.util.*,
				java.net.*"%><%
	String op = request.getParameter("op") ;
	String user = request.getParameter("user") ;
	String psw = request.getParameter("psw") ;
	if(op==null)
		op="" ;
	switch(op)
	{
	case "login":
		if("user1".equals(user) && "123456".equals(psw))
		{
			session.setAttribute("token", "user1_1111111") ;
			Cookie ck = new Cookie("token","user1_1111111") ;
			ck.setPath("/") ;
			response.addCookie(ck) ;
			out.print("ok") ;
		}
		else if("admin".equals(user) && "123456".equals(psw))
		{
			session.setAttribute("token", "admin_1111111") ;
			Cookie ck = new Cookie("token","user1_1111111") ;
			ck.setPath("/") ;
			response.addCookie(ck) ;
			out.print("ok") ;
		}
		else
		{
			out.print("login failed") ;
		}
		return ;
	case "logout":
		out.print("ok") ;
		return ;
	default:
		out.print("unknown op") ;
		return ;
	}
%>