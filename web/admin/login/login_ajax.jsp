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
	String lan = request.getParameter("lan") ;
	switch(op)
	{
	case "login":
		if(LoginUtil.doLogin(request, user, psw,lan))
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
	case "chg_psw":
		if(!LoginUtil.checkAdminLogin(request))
		{
			out.print("no current login inf") ;
			return ;
		}
		String oldpsw = request.getParameter("oldpsw") ;
		String newpsw = request.getParameter("newpsw") ;
		StringBuilder failedr = new StringBuilder() ;
		if(LoginUtil.chgPsw("admin",oldpsw, newpsw, failedr))
			out.print("succ") ;
		else
			out.print(failedr.toString()) ;
		break ;
	case "set_lan":
		if(!Convert.checkReqEmpty(request,  out, "lan"))
			return ;
		//lan = request.getParameter("lan") ;
		Lan.setSysLang(lan) ;
		out.print("succ") ;
		break ;
	case "set_session_lan":
		if(!Convert.checkReqEmpty(request,  out, "lan"))
			return ;
		LoginUtil.SessionItem si = LoginUtil.getAdminLoginSession(session) ;
		if(si==null)
		{
			out.print("no login session") ;
			return ;
		}
		si.lan = lan ;
		out.print("succ") ;
		break ;
	}
	
%>