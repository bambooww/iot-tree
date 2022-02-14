<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*
	"%><%!

%><%if(!Convert.checkReqEmpty(request, out, "prjid","op"))
	return ;

String op = request.getParameter("op");
String prjid = request.getParameter("prjid");

UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}

String taskid = request.getParameter("taskid") ;
String actid =  request.getParameter("actid") ;
String name = request.getParameter("name") ;
String title = request.getParameter("title") ;
boolean ben = "true".equals(request.getParameter("enable"));
String desc = request.getParameter("desc") ;
long int_ms = Convert.parseToInt64(request.getParameter("int_ms"), Task.DEFAULT_INT_MS) ;


switch(op)
{
case "add":
case "edit":
	Task jst = new Task(prj.getId()) ;
	try
	{
		jst.withName(name).withEnable(ben).withTitle(title).withIntervalMS(int_ms).withDesc(desc);//.withInitScript(initsc).withRunScript(runsc).withEndScript(endsc) ;
		
		if("edit".equals(op))
		{
			if(!Convert.checkReqEmpty(request, out, "taskid"))
				return ;
			jst.withId(taskid) ;
		}
		
		TaskManager.getInstance().setTask(prjid, jst);
		out.print("succ") ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "del":
	if(!Convert.checkReqEmpty(request, out, "taskid"))
		return ;
	if(TaskManager.getInstance().delTask(prjid, taskid))
		out.print("succ") ;
	else
		out.print("del error") ;	
	break;
case "export":
	if(!Convert.checkReqEmpty(request, out, "taskid"))
		return ;
	Task expt = TaskManager.getInstance().getTask(prjid, taskid);
	if(expt==null)
	{
		out.print("no task found") ;
		return ;
	}
	File ft = TaskManager.getInstance().findTaskFile(prjid, taskid) ;
	if(ft==null)
	{
		out.print("no task file found") ;
		return ;
	}
	
	try(FileInputStream fis = new FileInputStream(ft);)
	{
		WebRes.renderFile(response, "task_"+expt.getName()+".xml", fis) ;
	}
	break;
case "act_add":
case "act_edit":
	TaskAction ta = new TaskAction() ;
	XmlData tmpxd = XmlData.parseFromHttpRequest(request, "dx_");
	DataTranserXml.injectXmDataToObj(ta, tmpxd) ;
	//ta.withName(name).withInitScript(c)
	try
	{
		if("act_edit".equals(op))
		{
			if(!Convert.checkReqEmpty(request, out, "taskid","actid"))
				return ;
			ta.withId(actid) ;
		}
		
		TaskManager.getInstance().setTaskActionBasic(prjid,taskid, ta);
		out.print("succ") ;
	}
	catch(Exception e)
	{
		e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "act_del":
	if(!Convert.checkReqEmpty(request, out, "taskid","actid"))
		return ;
	if(TaskManager.getInstance().delTaskAction(prjid, taskid,actid))
		out.print("succ") ;
	else
		out.print("del error") ;	
	break;
case "act_js_read":
	if(!Convert.checkReqEmpty(request, out, "taskid","actid","jstp"))
		return ;
	String jstp = request.getParameter("jstp") ;
	ta = TaskManager.getInstance(). getTaskAction(prjid,taskid,actid) ;
	if(ta==null)
	{
		out.print("no Task Action found") ;
		return ;
	}
	switch(jstp)
	{
	case "init":
		out.print("succ=");
		out.print(ta.getInitScript()) ;
		break;
	case "run":
		out.print("succ=");
		out.print(ta.getRunScript()) ;
		break;
	case "end":
		out.print("succ=");
		out.print(ta.getEndScript()) ;
		break;
	default:
		out.print("unknown jstp") ;
		break ;
	}
	break ;
case "act_js_write":
	if(!Convert.checkReqEmpty(request, out, "taskid","actid","jstp"))
		return ;
	jstp = request.getParameter("jstp") ;
	String jstxt = request.getParameter("jstxt") ;
	if(jstxt==null)
		jstxt= "" ;
	ta = TaskManager.getInstance(). getTaskAction(prjid,taskid,actid) ;
	if(ta==null)
	{
		out.print("no Task Action found") ;
		return ;
	}
	switch(jstp)
	{
	case "init":
		ta.withInitScript(jstxt) ;
		break;
	case "run":
		ta.withRunScript(jstxt) ;
		break;
	case "end":
		ta.withEndScript(jstxt) ;
		break;
	default:
		out.print("unknown jstp") ;
		return ;
	}
	ta.getTask().save();
	out.print("succ");
	break ;
case "list":
	List<Task> jts = TaskManager.getInstance().getTasks(prjid);
	out.print("[") ;
	boolean bfirst = true; 
	for(Task jt:jts)
	{
		if(bfirst)
			bfirst=false;
		else
			out.print(",");
		out.print("{\"id\":\""+jt.getId()+"\",\"n\":\""+jt.getName() +"\",\"t\":\""+jt.getDesc() +"\"}");
	}
	out.print("]") ;
	break ;
case "start":
case "stop":
	if(!Convert.checkReqEmpty(request, out,"taskid"))
		return;
	Task t = TaskManager.getInstance().getTask(prjid, taskid) ;
	if(t==null)
	{
		out.print("no task found") ;
		return ;
	}
	if("start".equals(op))
		t.RT_start();
	else
		t.RT_stop();
	out.print("ok") ;
	break;
case "list_tb":
	jts = TaskManager.getInstance().getTasks(prjid);%>{"code":0,"msg":"","count":<%=jts.size()%>,

"data":
	[
<%bfirst=true;
for(Task jt:jts)
{
	if(bfirst)bfirst=false;
	else out.print(",") ;%>
	{"id":"<%=jt.getId() %>","n":"<%=jt.getName() %>","t":"<%=jt.getDesc() %>"}
<%
}
if(true)
{
if(!bfirst)
	out.print(",") ;
%>{"id":"","n":"","t":""}
<%
}
%>
	]
}
<%
	break;
}
%>