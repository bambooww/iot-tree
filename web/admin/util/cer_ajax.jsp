<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,org.iottree.core.util.cer.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*,
	org.iottree.core.util.logger.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "op"))
	return ;
String op = request.getParameter("op") ;
String cat = request.getParameter("cat") ;
String item_id =  request.getParameter("item_id") ;
String title =  request.getParameter("title") ;
String org =  request.getParameter("org") ;
String org_depart =  request.getParameter("org_depart") ;

CerCat cer_cat = null;
if(Convert.isNotNullEmpty(cat))
{
	cer_cat = CerManager.getInstance().getCat(cat) ;
	if(cer_cat==null)
	{
		out.print("no cat found") ;
		return ;
	}
}

try
{
	switch(op)
	{
	case "add_item":
		if(!Convert.checkReqEmpty(request, out, "cat","title","org"))
			return ;
		CerItem new_ci = cer_cat.addCerItem(title, org, org_depart) ;
		if(new_ci!=null)
			out.print("succ") ;
		else
			out.print("failed") ;
		return ;
	case "del_item":
		if(!Convert.checkReqEmpty(request, out, "cat","item_id"))
			return ;
		CerItem old_ci = cer_cat.delCerItem(item_id) ;
		if(old_ci==null)
		{
			out.print("del failed") ;
			return ;
		}
		out.print("succ") ;
		return ;
	}
}
catch(Exception ee)
{
	ee.printStackTrace();
	out.print(ee.getMessage()) ;
}
%>