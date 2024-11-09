<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.json.*,
	org.iottree.core.station.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out,"op"))
	return ;

String op = request.getParameter("op");
String id = request.getParameter("id") ;
String tt = request.getParameter("tt") ;
String key = request.getParameter("key") ;

switch(op)
{
case "set_pstation":
	try
	{
		if(!Convert.checkReqEmpty(request, out,"id"))
			return ;

		PlatInsManager.getInstance().setStation(id, tt, key) ; 
		
		out.print("succ") ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "list_pstation":
	List<PStation> pss = PlatInsManager.getInstance().listStations() ;
	JSONArray jarr = new JSONArray() ;
	for(PStation ps:pss)
	{
		JSONObject jo = ps.toJO() ;
		jarr.put(jo) ;
	}
	jarr.write(out) ;
	break ;
case "del_pstation":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	if( PlatInsManager.getInstance().delStation(id)!=null)
	{
		out.print("succ") ;
		return ;
	}
	out.print("delete pstation failed") ;
	break;
default:
	out.print("unknown op="+op) ;
	return ;
}

%>