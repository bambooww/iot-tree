<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.json.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.router.*,
	org.iottree.core.dict.*,
	org.iottree.core.util.xmldata.*,
	org.iottree.core.util.web.*,
	org.iottree.core.msgnet.*
	"%><%!

%><%if(!Convert.checkReqEmpty(request, out,"container_id","op"))
	return ;

String op = request.getParameter("op");
String container_id = request.getParameter("container_id");
String taglist_cat = request.getParameter("taglist_cat") ;

MNManager mnm= MNManager.getInstanceByContainerId(container_id) ;
if(mnm==null)
{
	out.print("no MsgNet Manager with container_id="+container_id) ;
	return ;
}
IMNContainer cont = mnm.getBelongTo() ;
if(!(cont instanceof IMNContTagListMapper))
{
	out.print("Container is not impl IMNContTagListMapper") ;
	return ;
}

IMNContTagListMapper taglist_m = (IMNContTagListMapper)cont ;

StringBuilder failedr = new StringBuilder() ;
switch(op)
{
case "cat_tag_list":
	if(!Convert.checkReqEmpty(request, out, "taglist_cat"))
		return ;
	List<NameTitle> nts = taglist_m.getMNContTagList(taglist_cat) ;
	JSONArray jarr = new JSONArray() ;
	for(NameTitle nt:nts)
	{
		jarr.put(nt.toJO()) ;
	}
	jarr.write(out) ;
	return ;
default:
	out.print("unknown op="+op) ;
	return ;
}%>