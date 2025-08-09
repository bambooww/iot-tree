<%@ page contentType="text/html;charset=UTF-8"%><%@page import = "java.io.PrintStream,java.util.* , java.io.* , java.net.*,org.iottree.core.* " %><%!
static long M = 1024*1024 ;
%><%
  StringBuffer u = request.getRequestURL() ;
  URL tmpu = new URL(u.toString()) ;
  if("true".equals(request.getParameter("gc")))
	{
			System.gc();
			out.println("gc end  ") ;
	}
	Runtime rt = Runtime.getRuntime() ;
	out.println("total mem="+rt.totalMemory()/M+"  free="+rt.freeMemory()/M+" used="+(rt.totalMemory()-rt.freeMemory())/M+"<br>");
%>