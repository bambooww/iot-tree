<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				 java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.util.xmldata.*"%><%!
%><%
	String resid = request.getParameter("resid") ;
	if(Convert.isNullOrEmpty(resid))
	{
		return ;
	}
	ResItem ri = ResCxtManager.getInstance().getResItemById(resid) ;
	if(ri==null)
	{
		return ;
	}
	File rf = ri.getResFile();
	if(!rf.exists())
		return ;
	try(FileInputStream fis= new FileInputStream(rf))
	{
		WebRes.renderFile(response, ri.getFileName(), fis, true);
	}
%>