<%@page import="org.iottree.core.util.web.WebRes"%><%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
if(!Convert.checkReqEmpty(request, out, "id"))
	return;

String id = request.getParameter("id") ;
UAPrj p = UAManager.getInstance().getPrjById(id);
if(p==null)
{
	out.print("no prj found") ;
	return;
}

UAManager.getInstance().exportPrj(response,id) ;

/*
File fout =UAManager.getInstance().exportPrjToTmp(id) ;
try(FileInputStream fis = new FileInputStream(fout);)
{
	WebRes.renderFile(response, "prj_"+p.getName()+".zip", fis) ;
}

fout.delete();
*/
%>