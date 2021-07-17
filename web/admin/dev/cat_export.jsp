<%@page import="org.iottree.core.util.web.WebRes"%><%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
if(!Convert.checkReqEmpty(request, out, "drvn","catn"))
	return;
String drvn = request.getParameter("drvn") ;
String catn = request.getParameter("catn") ;


File fout =DevManager.getInstance().exportDevCatToTmp(drvn, catn) ;
if(fout==null)
	return ;
try(FileInputStream fis = new FileInputStream(fout);)
{
	WebRes.renderFile(response, fout.getName(), fis) ;
}

fout.delete();
%>