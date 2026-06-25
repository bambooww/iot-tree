<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
org.iottree.core.*,org.json.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,org.iottree.core.drv.*,
	java.net.*,
	java.util.*"%>
<%
if(!Convert.checkReqEmpty(request, out, "op","nodepath"))
	return;
String op=request.getParameter("op");
if(!Convert.checkReqEmpty(request, out, "nodepath"))
	return ;
String nodep = request.getParameter("nodepath") ;
UANode node = UAManager.getInstance().findNodeByPath(nodep) ;
if(node==null || !(node instanceof UACh))
{
	out.print("no UACh found with path="+nodep) ;
	return ;
}

UACh ch = (UACh)node ;
LocPrjMapDriver drv = (LocPrjMapDriver)ch.getDriver() ;
String jarr = request.getParameter("jarr");
JSONArray input_jarr = null ;
if(Convert.isNotNullEmpty(jarr))
{
	input_jarr = new JSONArray(jarr) ;
}
StringBuilder failedr = new StringBuilder() ;
if("prj_items_detail".equals(op))
{
	if(!Convert.checkReqEmpty(request, out, "jarr"))
		return;
	LinkedHashMap<String,LocPrjMapDriver.PrjItem> id2pis = drv.parseFromJArr(input_jarr) ;
	JSONArray outjarr = new JSONArray() ;
	for(LocPrjMapDriver.PrjItem pi:id2pis.values())
	{
		outjarr.put(pi.toJODetail()) ;
	}
	outjarr.write(out) ;
	return ;
}
else if("stop".equals(op))
{
	
}
%>unknown op