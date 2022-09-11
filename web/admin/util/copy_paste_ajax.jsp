<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page import="java.util.*,
				java.io.*,
				java.util.*,
				java.net.*,
				org.iottree.core.util.*
				"%><%!
	public static String getKeyByName(String name)
	{
				return "_common_cp_"+name ;	
	}
				
	public static class CommonItem
	{
		String name;
		
		String txt ;
		
		public CommonItem(String name,String txt)
		{
			this.name = name;
			this.txt = txt ;
		}
		
		public String getKey()
		{
			return getKeyByName(this.name);
		}
		
		public String getName()
		{
			return this.name ;
		}
		
		public String getTxt()
		{
			return this.txt ;
		}
	}

%><%
if(!Convert.checkReqEmpty(request, out, "op"))
	return ;
String n = request.getParameter("n") ;
String t = request.getParameter("t") ;

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
	case "common_copy":
		if(!Convert.checkReqEmpty(request, out, "n","t"))
			return ;
		
		CommonItem ci = new CommonItem(n,t) ;
		session.setAttribute(ci.getKey(), ci) ;
		out.print("succ");
		break;
	case "common_paste":
		if(!Convert.checkReqEmpty(request, out, "n"))
			return ;
		ci = (CommonItem)session.getAttribute(getKeyByName(n));
		if(ci==null)
		{
			out.print("none");
			return ;
		}
		out.print("succ="+ci.getTxt()) ;
		break;
		
	}
%>