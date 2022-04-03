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
	if(!Convert.checkReqEmpty(request, out, "prjid","cpid","msgid"))
	return;
	String prjid = request.getParameter("prjid") ;
String cpid = request.getParameter("cpid") ;
String cid = request.getParameter("cid") ;
String msgid = request.getParameter("msgid") ;
ConnProvider cp = ConnManager.getInstance().getConnProviderById(prjid, cpid);
if(cp==null)
{
	out.print("no provider found ");
	return ;
}
ConnPt cpt = null ;
if(Convert.isNotNullEmpty(cid))
{
	cpt = cp.getConnById(cid) ;
	if(cpt==null)
	{
		out.print("no connection found") ;
		return ;
	}
}
String cptitle = cp.getTitle() ;

ConnMsg msg = null;
if(cpt!=null)
	msg = cpt.getConnMsgById(msgid) ;
else
	msg = cp.getConnMsgById(msgid) ;

if(msg==null)
{
	out.print("no msg with id "+msgid+" found") ;
	return;
}


%><%=msg.toFullJsonStr()%>
