<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.json.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.sim.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.driver.common.modbus.sim.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*
	"%><%!

%><%if(!Convert.checkReqEmpty(request, out,"op"))
	return ;

String op = request.getParameter("op");
String insid = request.getParameter("insid");
String chtp = request.getParameter("chtp") ;
String chid = request.getParameter("chid");

String devid = request.getParameter("devid");

String name = request.getParameter("name") ;
String title = request.getParameter("title") ;
boolean ben = "true".equals(request.getParameter("enable"));
String desc = request.getParameter("desc") ;
SimInstance ins= null ;
if(Convert.isNotNullEmpty(insid))
	ins = SimManager.getInstance().getInstance(insid) ;

SimChannel sch = null ;
if(ins!=null && Convert.isNotNullEmpty(chid))
	sch = ins.getChannel(chid) ;

switch(op)
{
case "ins_add":
case "ins_edit":
	if(!Convert.checkReqEmpty(request, out, "name"))
		return ;
	try
	{
		if(ins==null)
			ins = new SimInstance() ;
		
		ins.withName(name).withTitle(title).withEnable(ben) ;
		SimManager.getInstance().setInstanceBasic(ins) ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break ;
case "ins_del":
	if(ins==null)
	{
		out.print("no instance found") ;
		return ;
	}
	if(SimManager.getInstance().delInstance(insid))
		out.print("succ") ;
	else
		out.print("del failed") ;
	break ;
case "ch_add":
case "ch_edit":
	if(!Convert.checkReqEmpty(request, out, "insid","chtp"))
		return ;
	
	sch = SimChannel.createNewInstance(chtp) ;
	try
	{
		XmlData tmpxd = XmlData.parseFromHttpRequest(request, "dx_");
		DataTranserXml.injectXmDataToObj(sch, tmpxd) ;
		
		//sch.withName(name).withEnable(ben).withTitle(title);//.withInitScript(initsc).withRunScript(runsc).withEndScript(endsc) ;
		
		if("ch_edit".equals(op))
		{
			if(!Convert.checkReqEmpty(request, out, "chid"))
				return ;
			sch.withId(chid) ;
		}
		
		ins.setChannelBasic(sch);
		out.print("succ") ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "ch_conn":
	if(!Convert.checkReqEmpty(request, out, "insid","chid","tp"))
		return ;
	String tp = request.getParameter("tp") ;
	SimCP conn = SimCP.createNewInstance(tp) ;
	if(conn==null)
	{
		out.print("no conn found with tp="+tp) ;
		return ;
	}
	try
	{
		XmlData tmpxd = XmlData.parseFromHttpRequest(request, "dx_");
		DataTranserXml.injectXmDataToObj(conn, tmpxd) ;
		sch.setConn(conn) ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break ;
case "ch_del":
	if(!Convert.checkReqEmpty(request, out, "insid","chid"))
		return ;
	if(ins==null)
	{
		out.print("no instance found") ;
		return ;
	}
	if(ins.delChannel(chid))
		out.print("succ") ;
	else
		out.print("del error") ;	
	break;
case "export":
	/*
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
	*/
	break;
case "dev_add":
case "dev_edit":
	if(sch==null)
	{
		out.print("no channel found") ;
		return ;
	}
	
	SimDev dev = sch.createNewDev() ;
	XmlData tmpxd = XmlData.parseFromHttpRequest(request, "dx_");
	DataTranserXml.injectXmDataToObj(dev, tmpxd) ;
	//ta.withName(name).withInitScript(c)
	try
	{
		if("dev_edit".equals(op))
		{
			if(!Convert.checkReqEmpty(request, out, "chid","devid"))
				return ;
			dev.withId(devid) ;
		}
		sch.setDevBasic(dev) ;
		//sch.save() ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	
	break;
case "dev_setup":
	if(sch==null)
	{
		out.print("no channel found") ;
		return ;
	}
	if(!Convert.checkReqEmpty(request, out, "chid","devid","jstr"))
		return ;
	
	dev = sch.createNewDev() ;
	String jstr = request.getParameter("jstr") ;
	//tmpxd = XmlData.parseFromHttpRequest(request, "dx_");
	//DataTranserXml.injectXmDataToObj(dev, tmpxd) ;
	
	//ta.withName(name).withInitScript(c)
	try
	{
		JSONObject jo = new JSONObject(jstr) ;
		DataTranserJSON.injectJSONToObj(dev, jo) ;
		dev.withId(devid) ;
		
		sch.setDevExt(dev) ;
		//sch.save() ;
		out.print("succ") ;
	}
	catch(Exception e)
	{
		e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break ;
case "act_del":
	/*
	if(!Convert.checkReqEmpty(request, out, "taskid","actid"))
		return ;
	if(TaskManager.getInstance().delTaskAction(prjid, taskid,actid))
		out.print("succ") ;
	else
		out.print("del error") ;	
	*/
	break;

case "list":
	/*
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
	*/
	break ;
case "ch_start":
case "ch_stop":
	if(!Convert.checkReqEmpty(request, out,"insid","chid"))
		return;
	if(sch==null)
	{
		out.print("no channel found") ;
		return ;
	}
	StringBuilder sb = new StringBuilder() ;
	if("ch_start".equals(op))
	{
		if(!sch.RT_start(sb))
		{
			out.print(sb.toString()) ;
			return ;
		}
	}
	else
		sch.RT_stop();
	out.print("succ") ;
	break;

}%>