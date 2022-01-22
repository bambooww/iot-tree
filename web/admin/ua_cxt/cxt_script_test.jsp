<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.*,
	org.iottree.core.*,
	org.iottree.core.cxt.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*"%><%!
	
%><%
if(!Convert.checkReqEmpty(request, out, "path"))
	return;
String op = request.getParameter("op");
if(op==null)
	op="" ;
String path=request.getParameter("path");
/*
String id = request.getParameter("id");
UARep rep = UAManager.getInstance().getRepById(repid) ;
if(rep==null)
{
	out.print("no rep found");
	return ;
}
*/

UANode n = UAUtil.findNodeByPath(path);//.findNodeById(id) ;
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
UAPrj prj = (UAPrj)cxt.getTopNode() ;
String txt = request.getParameter("txt") ;
if(Convert.isNullOrEmpty(txt))
{
	out.print("no txt input");
	return ;
}

//txt = URLDecoder.decode(txt,"UTF-8") ;

long st = System.currentTimeMillis() ;
//Object obj = dnd.runCodeForTest(txt);
UAContext cxtr = null;
long et = System.currentTimeMillis() ;
switch(op)
{
case "task":
	if(!Convert.checkReqEmpty(request, out, "taskid"))
		return;
	String taskid = request.getParameter("taskid") ;
	Task task = TaskManager.getInstance().getTask(prj.getId(), taskid) ;
	if(task==null)
	{
		out.print("no task found") ;
		return ;
	}
	cxtr = task.getContext() ;
	String ret = cxtr.testScript(txt) ;
	out.print(ret) ;
	break ;
default:
	cxtr = cxt.RT_getContext() ;
	Object obj = null;
	try
	{
		//System.out.println("run js code") ;
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
<%
	break ;
}
%>