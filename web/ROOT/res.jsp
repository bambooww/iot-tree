<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				 java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.util.xmldata.*"%><%!
%><%
	if(!Convert.checkReqEmpty(request, out, "res_node_id","name"))
	return ;
	
	String resname = request.getParameter("name") ;
	String resnodeid = request.getParameter("res_node_id") ;
	

	ResItem ri = ResManager.getInstance().findResItem(resnodeid, resname)  ;
	if(ri==null)
	{
		return ;
	}
	ri.renderOut(request, response);
	/*
	File rf = ri.getResFile();
	if(!rf.exists())
		return ;
	try(FileInputStream fis= new FileInputStream(rf))
	{
		WebRes.renderFile(response, ri.getFileName(), fis, true);
	}
	*/
	%>