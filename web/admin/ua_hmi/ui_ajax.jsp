<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.ui.*,
	org.json.*,
	org.iottree.core.store.*,
	org.iottree.core.store.record.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out,"op","prjid"))
	return ;

String op = request.getParameter("op");
String prjid = request.getParameter("prjid");
String tagids = request.getParameter("tagids");
List<String> tagidlist = Convert.splitStrWith(tagids, ",") ;
String tagid = null ;
if(tagidlist!=null&&tagidlist.size()>0)
	tagid = tagidlist.get(0) ;
String jstr = request.getParameter("jstr") ;
String proid = request.getParameter("proid") ;
String id = request.getParameter("id") ;
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}

UIManager ui_mgr = UIManager.getInstance(prj) ;
RecManager rec_mgr = ui_mgr.getRecMgr() ;
JSONObject jo = null;
if(Convert.isNotNullEmpty(jstr))
	jo = new JSONObject(jstr) ;


JSONArray jarr = null ;

StringBuilder failedr = new StringBuilder() ;
switch(op)
{
case "list_temps":
	if(Convert.isNullOrEmpty(tagid))
		return ;
	List<IUITemp> uits = ui_mgr.listFitTempsByTagId(tagid) ;
	jarr = new JSONArray() ;
	if(uits!=null)
	{
		for(IUITemp uit:uits)
		{
			JSONObject tmpjo = uit.toJO() ;
			jarr.put(tmpjo) ;
		}
	}
	jarr.write(out) ;
	return ;
case "set_item":
	if(!Convert.checkReqEmpty(request, out,"jstr"))
		return ;
	if(!ui_mgr.setItemByJSON(jo, failedr))
	{
		out.print(failedr.toString()) ;
		return ;
	}
	out.print("succ") ;
	return ;
case "list_items":
	jarr = new JSONArray() ;
	for(UIItem rp:ui_mgr.getId2Items().values())
	{
		JSONObject tmpjo = rp.toListJO() ;
		jarr.put(tmpjo) ;
	}
	jarr.write(out) ;
	break ;
case "del_item":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	if(ui_mgr.delItemById(id))
	{
		out.print("succ") ;
		return ;
	}
	out.print("delete UI Item failed");
	return ;

}

%>