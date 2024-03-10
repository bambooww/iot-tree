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

%><%
if(!Convert.checkReqEmpty(request, out,"op","prjid"))
	return ;

String op = request.getParameter("op");
String prjid = request.getParameter("prjid");
String jstr = request.getParameter("jstr") ;
String proid = request.getParameter("proid") ;
String id = request.getParameter("id") ;
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
RecManager rec_mgr = RecManager.getInstance(prj) ;

JSONObject jo = null;
if(Convert.isNotNullEmpty(jstr))
	jo = new JSONObject(jstr) ;

JSONArray jarr = null ;

StringBuilder failedr = new StringBuilder() ;
switch(op)
{
case "set_pro":
	if(!Convert.checkReqEmpty(request, out,"jstr"))
		return ;
	if(!rec_mgr.setRecProByJSON(jo, failedr))
	{
		out.print(failedr.toString()) ;
		return ;
	}
	out.print("succ") ;
	return ;
case "list_p":
	jarr = new JSONArray() ;
	for(RecPro rp:rec_mgr.getId2RecPro().values())
	{
		JSONObject tmpjo = rp.toListJO() ;
		jarr.put(tmpjo) ;
	}
	jarr.write(out) ;
	break ;
case "del_p":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	if(rec_mgr.delRecProById(id))
	{
		out.print("succ") ;
		return ;
	}
	out.print("delete processor failed");
	return ;
case "pro_fit_tagids":
	if(!Convert.checkReqEmpty(request, out,"op","proid"))
		return ;
	RecPro rp = rec_mgr.getRecProById(proid) ;
	if(rp==null || !(rp instanceof RecProL1))
	{
		out.print("no Processor L1 item") ;
		return ;
	}
	List<UATag> tmpts = ((RecProL1)rp).listFitTags() ;
	jarr = new JSONArray() ;
	if(tmpts!=null)
	{
		for(UATag tag:tmpts)
		{
			jarr.put(tag.getId()) ;
		}
	}
	jarr.write(out) ;
	return ;
case "set_p_sel_tagids":
	if(!Convert.checkReqEmpty(request, out, "prjid","id","idstr"))
		return ;
	String idstr = request.getParameter("idstr") ;
	try
	{
		List<String> tagids = Convert.splitStrWith(idstr, ",") ;
		rec_mgr.setRecProL1SelTagIds(id, tagids);//
		out.print("succ") ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage()) ;
	}
	break;
case "set_sor":
	break;
case "rt_data":
	JSONObject tmpjo = rec_mgr.RT_getInf();
	tmpjo.write(out) ;
	break;
}

%>