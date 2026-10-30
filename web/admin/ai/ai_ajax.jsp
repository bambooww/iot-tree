<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.ai.*,
	org.json.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out,"op"))
	return ;

String op = request.getParameter("op");
String id = request.getParameter("id") ;
String n = request.getParameter("n") ;
String t = request.getParameter("t") ;
String jstr = request.getParameter("jstr") ;
JSONObject input_jo  = null ;
if(Convert.isNotNullEmpty(jstr))
	input_jo = new JSONObject(jstr) ;

StringBuilder failedr = new StringBuilder() ;
switch(op)
{
case "add_apikey":
case "set_apikey":
	try
	{
		if(!Convert.checkReqEmpty(request, out,"jstr"))
			return ;
		AIApiKey newak = AIApiKey.setApkKey(input_jo, "add_apikey".equals(op), failedr) ;
		if(newak==null)
			out.print(failedr.toString()) ;
		else
			out.print("succ") ;
		return ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "list_apikey":
	JSONArray jarr = new JSONArray() ;
	for(AIApiKey aak:AIApiKey.listAll().values())
	{
		JSONObject jo = aak.toJO() ;
		jarr.put(jo) ;
	}
	jarr.write(out) ;
	break ;
case "del_apikey":
	if(!Convert.checkReqEmpty(request, out, "n"))
		return ;
	
	if(AIApiKey.delApkKey(n))
	{
		out.print("succ") ;
		return ;
	}
	out.print("delete api key failed") ;
	break;

default:
	out.print("unknown op="+op) ;
	return ;
}

%>