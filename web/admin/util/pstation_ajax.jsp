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

StringBuilder failedr = new StringBuilder() ;
switch(op)
{
case "add_pstation":
case "set_pstation":
	try
	{
		if(!Convert.checkReqEmpty(request, out,"id"))
			return ;
		if(!Convert.checkVarName(id,"Station Id", false, failedr))
		{
			out.print(failedr) ;
			return;
		}
		if("add_pstation".equals(op))
		{
			PStation oldps = PlatInsManager.getInstance().getStationById(id) ;
			if(oldps!=null)
			{
				out.print("Station Id ["+id+"] is already existed") ;
				return ;
			}
		}
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
case "st":
	JSONObject st_jo = new JSONObject() ;
	st_jo.put("local",StationLocal.getInstance().RT_toJO());
	pss = PlatInsManager.getInstance().listStations() ;
	JSONArray ps_jarr = new JSONArray() ;
	for(PStation ps:pss)
	{
		JSONObject jo = ps.RT_toStatusJO();
		ps_jarr.put(jo) ;
	}
	st_jo.put("pstations",ps_jarr) ;
	st_jo.write(out) ;
	return ;
default:
	out.print("unknown op="+op) ;
	return ;
}

%>