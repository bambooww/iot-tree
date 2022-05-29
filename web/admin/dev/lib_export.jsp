<%@page import="org.iottree.core.util.web.WebRes"%><%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
if(!Convert.checkReqEmpty(request, out, "libid"))
	return;
String libid = request.getParameter("libid") ;

File fout =DevManager.getInstance().exportDevLibToTmp(libid) ;
if(fout==null)
	return ;
try(FileInputStream fis = new FileInputStream(fout);)
{
	WebRes.renderFile(response, fout.getName(), fis) ;
}

fout.delete();
%>