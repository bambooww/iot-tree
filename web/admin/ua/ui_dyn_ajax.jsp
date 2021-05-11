<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.json.*,
	org.iottree.core.*"%><%
	if(!Convert.checkReqEmpty(request, out, "id"))
		return;
	String repid=request.getParameter("id");

	UARep rep = UAManager.getInstance().getRepById(repid) ;
	if(rep==null)
	{
		out.print("no rep found");
		return ;
	}

	JSONObject jobj = rep.toOCDynJSON(-1);
	out.print(jobj.toString(2));
%>


