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
CompManager compmgr = CompManager.getInstance() ;
CompLib cc = compmgr.getCompLibById(libid);
if(cc==null)
{
	out.print("no cat found") ;
	return;
}
String fn = libid+".zip";
File fout = new File(Config.getDataDirBase()+"/tmp/"+fn) ;
compmgr.exportCompLibTo(libid, fout);

try(FileInputStream fis = new FileInputStream(fout);)
{
	WebRes.renderFile(response, cc.getTitle()+".zip", fis) ;
}

//fout.delete();
%>