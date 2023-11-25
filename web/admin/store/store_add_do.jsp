<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
	if(!Convert.checkReqEmpty(request, out, "repid","tp","name"))
	return;
String repid = request.getParameter("repid") ;
String name=request.getParameter("name");
String tp=request.getParameter("tp");
String title = request.getParameter("title");
String desc = request.getParameter("desc");
float x = Convert.parseToFloat(request.getParameter("x"), 0.0f);
float y = Convert.parseToFloat(request.getParameter("y"), 0.0f);

StringBuilder errsb = new StringBuilder() ;
UAManager uam = UAManager.getInstance();
UAPrj dc = uam.getPrjById(repid) ;
if(dc==null)
{
	out.print("no rep found with id="+repid) ;
	return ;
}
HashMap<String,Object> uips = new HashMap<>() ;
uips.put("x",x);
uips.put("y",y);
Source ch = null;
try
{
	ch = dc.addStore(tp,name, title, desc,uips) ;
}
catch(Exception e)
{
	out.print(e.getMessage());
	return ;
}
if(ch==null)
{
	out.print(errsb.toString()) ;
	return ;
}
else
{
	out.print("succ="+ch.getId()) ;
	return ;
}
%>