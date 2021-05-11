<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.*,
	org.iottree.core.*,
	org.iottree.core.cxt.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*"%><%!
	
%><%
	if(!Convert.checkReqEmpty(request, out, "repid","id"))
	return;
String op = request.getParameter("op");
String repid=request.getParameter("repid");
String id = request.getParameter("id");
UARep rep = UAManager.getInstance().getRepById(repid) ;
if(rep==null)
{
	out.print("no rep found");
	return ;
}


UANode n = rep.findNodeById(id) ;
if(n==null)
{
	out.print("no node found") ;
	return ;
}
if(!(n instanceof UANodeOCTagsCxt))
{
	out.print("not node oc tags") ;
	return ;
}
UANodeOCTagsCxt cxt = (UANodeOCTagsCxt)n ;

String txt = request.getParameter("txt") ;
if(Convert.isNullOrEmpty(txt))
{
	out.print("no txt input");
	return ;
}

txt = URLDecoder.decode(txt,"UTF-8") ;

long st = System.currentTimeMillis() ;
//Object obj = dnd.runCodeForTest(txt);
UAContext cxtr = cxt.RT_getContext() ;
long et = System.currentTimeMillis() ;
Object obj = null;
try
{
	obj = cxtr.runCode(txt);
}
catch(Exception e)
{
	obj = "err:"+e.getMessage();
	e.printStackTrace();
}

//System.out.println(sb.toString()) ;
%>res=[<%=""+obj%>]
cost ms=<%=(et-st)%>