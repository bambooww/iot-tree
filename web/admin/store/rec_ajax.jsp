<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.json.*,
	org.iottree.core.store.*,
	org.iottree.core.store.record.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*
	"%><%!

%><%if(!Convert.checkReqEmpty(request, out,"op","prjid"))
	return ;

String op = request.getParameter("op");
String prjid = request.getParameter("prjid");

UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
RecManager rec_mgr = RecManager.getInstance(prj) ;

switch(op)
{
case "set_sor":
	break;
case "rt_data":
	JSONObject tmpjo = rec_mgr.getTSSAdapterPrj().RT_getInfo().toJO() ;
	tmpjo.write(out) ;
	break;
}

%>