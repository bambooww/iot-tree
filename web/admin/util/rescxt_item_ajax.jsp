<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.util.xmldata.*"%><%!
%><%
if(!Convert.checkReqEmpty(request, out, "res_lib_id","op"))
	return ;
String res_lib_id = request.getParameter("res_lib_id") ;
String res_id = request.getParameter("res_id") ;
String op = request.getParameter("op") ;
String n = request.getParameter("n") ;
ResDir rdir = ResManager.getInstance().getResDir(res_lib_id, res_id); //

//ResLib reslib = ResManager.getInstance().getResLibByLibId(res_lib_id) ;
if(rdir==null)
{
	out.print("no ResDir found") ;
	return ;
}

	
	switch(op)
	{
	case "add":
		break;
	case "del":
		if(!Convert.checkReqEmpty(request, out, "n"))
			return ; 
		if(rdir.delResItem(n))
			out.print("succ") ;
		else
			out.print("delete failed") ;
		break ;
	}
	
%>