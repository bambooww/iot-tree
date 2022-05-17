<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "prjid","op"))
	return ;

String op = request.getParameter("op");
String prjid = request.getParameter("prjid");
StoreManager stmgr= StoreManager.getInstance(prjid) ;
if(stmgr==null)
{
	out.print("no store manager found") ;
	return ;
}
String classid = request.getParameter("cid") ;
String nname = request.getParameter("name") ;

UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
switch(op)
{
case "set_store":
	String bind_for = request.getParameter("bind_for") ;
	boolean bind_m = "multi".equals(request.getParameter("bind_style")) ;
	boolean benable = !"false".equals(request.getParameter("enable")) ;
	try
	{
		stmgr.setStore(st, bsave)
		out.print("succ") ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "del_dc":
	if(!Convert.checkReqEmpty(request, out, "cid"))
		return ;
	if(pdc.delDataClass(classid))
		out.print("succ") ;
	else
		out.print("del error") ;	
	break;
case "dc_imp_txt":
	if(!Convert.checkReqEmpty(request, out, "txt"))
		return ;
	String txt = request.getParameter("txt") ;
	List<DataNode> newdns = pdc.impDataNodeByTxt(classid,txt) ;
	if(newdns.size()>0)
		out.print("succ") ;
	else
		out.print("no DataNode imported") ;
	return ;
case "export":
	if(!Convert.checkReqEmpty(request, out, "taskid"))
		return ;
	
	break;
case "edit_dn":
	if(!Convert.checkReqEmpty(request, out,"name"))
		return ;
case "add_dn":
	try
	{
		DataNode newdn = pdc.addOrUpdateDataNode(classid,name,title) ; 
		out.print("succ") ;
	}
	catch(Exception e)
	{
		e.printStackTrace();
		out.print(e.getMessage()) ;
	}
	break;
case "act_del":
	if(!Convert.checkReqEmpty(request, out, "taskid","actid"))
		return ;
	if(true)
		out.print("succ") ;
	else
		out.print("del error") ;	
	break;
case "list_html":
	for(DataNode tmpdn:dc.getRootNodes())
	{
%>
<tr id="<%=tmpdn.getName()%>">
	<td></td>
	<td><%=tmpdn.getName() %></td>
	<td><%=tmpdn.getTitle() %></td>
	<td><a href="javascript:add_or_edit_dn('<%=prjid %>','<%=dc.getClassId()%>','<%=tmpdn.getName()%>')"><i title="Edit Data Node" class="fa fa-pencil " aria-hidden="true"></i></a></td>
</tr>
<%
	}
	break ;
}
%>