<%@ page contentType="text/json;charset=UTF-8" isELIgnored="false"%><%@ page import="java.util.*,
				java.io.*,
				java.net.*,
				java.util.*,
				org.iottree.core.*,
				org.iottree.core.util.*,
				java.net.*"%><%!
				
%><%
	if(!Convert.checkReqEmpty(request, out, "op"))
		return;
	
	String op = request.getParameter("op") ;
	String stationid = request.getParameter("stationid") ;
	String prjname = request.getParameter("prjname") ;
	UAPrj prj = null ;
	String prjid = null ;
	if(Convert.isNotNullEmpty(stationid) && Convert.isNotNullEmpty(prjname))
	{
		prj = UAManager.getInstance().getPrjByName(stationid+"_"+prjname) ;
		if(prj==null)
		{
			out.print("no prj found with name="+prjname) ;
			return ;
		}
		prjid = prj.getId() ;
	}
	
	switch(op)
	{
	case "prj_xml": //read prj xml
		if(Convert.isNullOrEmpty(stationid) ||Convert.isNullOrEmpty(prjname))
			return ;
			
		File prjf = UAManager.getPrjFile(prjid) ;
		String txt=  Convert.readFileTxt(prjf) ;
		out.print(txt) ;
		return ;
	default:
		out.print("unknown op="+op) ;
	}
%>