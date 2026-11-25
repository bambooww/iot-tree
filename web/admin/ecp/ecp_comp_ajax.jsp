<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.ecp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out,"op"))
	return ;
String op = request.getParameter("op");
String libid = request.getParameter("libid");

ECPCompLib complib = null ;
if(Convert.isNotNullEmpty(libid))
{
	complib = ECPCompManager.getInstance().getLibById(libid) ;
	if(complib==null)
	{
		out.print("no comp lib found with id="+libid) ;
		return ;
	}
}
String comp_id = request.getParameter("compid") ;
ECPComp comp = null ;
if(Convert.isNotNullEmpty(comp_id))
{
	comp = complib.getCompById(comp_id);
	if(comp==null)
	{
		out.print("no comp found with id="+comp_id) ;
		return ;
	}
}

String comp_uid = request.getParameter("comp_uid") ;
if(Convert.isNotNullEmpty(comp_uid))
{
	comp = ECPCompManager.getInstance().getCompByUID(comp_uid) ;
	if(comp==null)
	{
		out.print("no comp found with uid="+comp_uid) ;
		return ;
	}
}

String name = request.getParameter("name") ;
String title = request.getParameter("title") ;
String desc = request.getParameter("desc") ;
String jarr_str = request.getParameter("jarr") ;
String jstr = request.getParameter("jstr") ;
JSONArray input_jarr = null ;
JSONObject input_jo = null ;
if(Convert.isNotNullEmpty(jarr_str))
	input_jarr = new JSONArray(jarr_str) ;
if(Convert.isNotNullEmpty(jstr))
	input_jo = new JSONObject(jstr) ;

JSONObject tmpjo = null ;
StringBuilder failedr = new StringBuilder() ;
try
{
switch(op)
{
case "add_complib":
case "edit_complib":
	if(!Convert.checkReqEmpty(request, out,"title"))
		return ;

		if(Convert.isNullOrEmpty(libid))
		{
			ECPCompLib plib = ECPCompManager.getInstance().addLibDir(title, desc);
			libid =  plib.getLibId();
		}
		else
		{
			complib.setLibBasic(title,desc) ;
		}
		out.print("succ="+libid) ;
	
	break;
case "del_complib":
	if(!Convert.checkReqEmpty(request, out,"libid"))
		return ;
	if(ECPCompManager.getInstance().delLib(libid))
		out.print("succ") ;
	else
		out.print("del partlib failed") ;
	break ;
case "comp_list_by_libid":
	if(!Convert.checkReqEmpty(request, out,"libid"))
		return ;
	JSONArray hd_jarr = new JSONArray() ;
	for(ECPComp.Head hd:complib.listCompHeads().values())
	{
		hd_jarr.put(hd.toJO()) ;
	}
	hd_jarr.write(out) ;
	return ;
case "comp_add_by_node":
	if(!Convert.checkReqEmpty(request, out,"libid","title","jstr"))
		return;
	comp = complib.addNewCompByNodeJO(title, desc, input_jo);
	if(comp==null)
		out.print("add failed") ;
	else
		out.print("succ="+comp.getId()) ;
	return ;
case "comp_new":
	if(!Convert.checkReqEmpty(request, out,"libid","title"))
		return;
	comp = complib.addNewComp(title, desc);
	if(comp==null)
		out.print("add failed") ;
	else
		out.print("succ="+comp.getId()) ;
	return ;
case "edit_comp":
	if(!Convert.checkReqEmpty(request, out,"libid","compid","title"))
		return ;
	comp.setBasic( title, desc);
	comp.save();
	out.print("succ="+comp_id) ;
	return ;
case "comp_load_by_uid":
	if(!Convert.checkReqEmpty(request, out,"comp_uid"))
		return ;
	comp.toJO().write(out) ;
	return ;
case "comp_save_by_uid":
	if(!Convert.checkReqEmpty(request, out,"comp_uid","jstr"))
		return ;
	comp.updateStructByJO(input_jo) ;
	out.print("succ") ;
	return ;
}
}
catch(Exception ee)
{
	out.print(ee.getMessage());
}
%>