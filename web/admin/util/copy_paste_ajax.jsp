<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page import="java.util.*,
				java.io.*,
				java.util.*,
				java.net.*"%><%
	String op = request.getParameter("op");
	switch(op)
	{
	case "copy":
		String itemjson = request.getParameter("items_json") ;
		//System.out.println("copyed:"+itemjson);	
		if(itemjson!=null)
		{
			session.setAttribute("items_json", itemjson) ;
		}
		break;
	case "paste":
		String tmps = (String)session.getAttribute("items_json");
		if(tmps!=null)
			out.print(tmps) ;
		break;
	}
%>