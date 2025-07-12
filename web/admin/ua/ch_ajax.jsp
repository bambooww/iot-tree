<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%!
	
	private static void addOrEditCh(HttpServletRequest request,JspWriter out) throws Exception
	{
		String reppath = request.getParameter("rep_path") ;
		String chpath = request.getParameter("ch_path") ;
		
		UAPrj rep = null ;
		UACh ch  = null;
		
		if(Convert.isNotNullEmpty(reppath))
		{
			rep  = (UAPrj)UAUtil.findNodeByPath(reppath) ;
			if(rep==null)
			{
				out.print("no rep node found");
				return ;
			}
		}
		else if(Convert.isNotNullEmpty(chpath))
		{
			ch  = (UACh)UAUtil.findNodeByPath(chpath) ;
			if(ch==null)
			{
				out.print("no ch node found");
				return ;
			}
		}
		else
		{
			out.print("no ch_path or rep_path input") ;
			return ;
		}
		//String chid = request.getParameter("chid");
		if(!Convert.checkReqEmpty(request, out, "name"))
			return;
		String drv = request.getParameter("drv");
		boolean drv_en_at_ps = "true".equals(request.getParameter("drv_en_at_ps")) ;
		String name=request.getParameter("name");
		String title = request.getParameter("title");
		String desc = request.getParameter("desc");
		
		try
		{
			if(ch==null)
				ch = rep.addCh(drv,drv_en_at_ps,name, title, desc,null) ;
			else
			{
				UAPrj rep0 = ch.getBelongTo() ;
				rep0.updateCh(ch,drv,drv_en_at_ps,name,title,desc);
			}
			out.print("succ="+ch.getId()) ;
			return ;
		}
		catch(Exception e)
		{
			out.print(e.getMessage());
			return ;
		}
		
	}
	
	private static void delCh(HttpServletRequest request,JspWriter out)throws Exception
	{
		if(!Convert.checkReqEmpty(request, out, "ch_path"))
			return;
		String chpath = request.getParameter("ch_path") ;
		UACh ch  = (UACh)UAUtil.findNodeByPath(chpath) ;
		if(ch==null)
		{
			out.print("no ch node found");
			return ;
		}
		
		boolean b = ch.delFromParent();
		if(!b)
		{
			out.print("del err") ;
			return ;
		}
		else
		{
			out.print("succ="+ch.getId()) ;
			return ;
		}
	}

	private static void startStopCh(HttpServletRequest request,JspWriter out)throws Exception
	{
		if(!Convert.checkReqEmpty(request, out, "ch_path"))
			return;
		String chpath = request.getParameter("ch_path") ;
		UACh ch  = (UACh)UAUtil.findNodeByPath(chpath) ;
		if(ch==null)
		{
			out.print("no ch node found");
			return ;
		}
	
		StringBuilder failedr = new StringBuilder() ;
		switch(ch.RT_getState())
		{
		case not_run:
			if(!ch.RT_startDriver(failedr))
			{
				out.print(failedr.toString()) ;
				return ;
			}
			else
			{
				out.print("succ") ;
			}
			return ;
		case running:
			ch.RT_stopDriver(false) ;
			out.print("succ") ;
			return ;
		case run_stopping:
			ch.RT_stopDriver(true) ;
			out.print("succ") ;
			return ;
		}
	}
	
	private static void findUpdateDevInCh(HttpServletRequest request,JspWriter out)throws Exception
	{
		if(!Convert.checkReqEmpty(request, out, "ch_path"))
			return;
		String chpath = request.getParameter("ch_path") ;
		UACh ch  = (UACh)UAUtil.findNodeByPath(chpath) ;
		if(ch==null)
		{
			out.print("no ch node found");
			return ;
		}
	
		StringBuilder failedr = new StringBuilder() ;
		DevDriver dd = ch.getDriver() ;
		if(dd==null)
		{
			out.print("no driver found") ;
			return ;
		}
		if(!dd.supportDevFinder())
		{
			out.print("ch driver not support device finding") ;
			return ;
		}
		if(!dd.updateFindedDevs(failedr))
		{
			out.print(failedr) ;
			return ;
		}
		out.print("succ") ;
	}

%><%
if(!Convert.checkReqEmpty(request, out, "op"))
	return;
String op = request.getParameter("op") ;
switch(op)
{
case "add":
case "edit":
	addOrEditCh(request, out);
	break ;
case "start_stop":
	startStopCh(request,out) ;
	break ;
case "find_update_dev":
	findUpdateDevInCh(request,out) ;
	break ;
case "del":
	delCh(request,out);
	break;
}
 %>