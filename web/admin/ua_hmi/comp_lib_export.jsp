<%@page import="org.iottree.core.util.web.WebRes"%><%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
if(!Convert.checkReqEmpty(request, out, "catid"))
	return;
String catid = request.getParameter("catid") ;
CompManager compmgr = CompManager.getInstance() ;

String fn = catid+".zip";
File fout = new File(Config.getDataDirBase()+"/tmp/"+fn) ;
compmgr.exportCompCat(catid, fout);

try(FileInputStream fis = new FileInputStream(fout);)
{
	WebRes.renderFile(response, fn, fis) ;
}

//fout.delete();
%>