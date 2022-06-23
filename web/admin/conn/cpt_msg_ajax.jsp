<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.ext.opcda.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","cpid","cid","op"))
	return;
	String op = request.getParameter("op") ;
	String prjid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
String cid = request.getParameter("cid") ;
ConnProvider cp = ConnManager.getInstance().getConnProviderById(prjid, cpid);
if(cp==null)
{
	out.print("no provider found ");
	return ;
}
ConnPtMSG cpt = (ConnPtMSG)cp.getConnById(cid) ;
if(cpt==null)
{
	out.print("no connection found") ;
	return ;
}

String cptitle = cp.getTitle() ;

switch(op)
{
case "read_tmp_to_buf":
	File f = cpt.readMsgToTmpBuf() ;
	if(f==null)
	{
		out.print("read msg to tmp buf failed") ;
		return ;
	}
	Date dt = new Date( f.lastModified()) ;
	out.print("{dt:\""+Convert.toFullYMDHMS(dt)+"\",bfp:\"connpt_msg/"+f.getName()+"\"}") ;
	break ;
default:
	out.print("unknown op") ;
	break ;
}
%>
