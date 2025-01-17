<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.*,
	org.iottree.core.alert.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.store.evt_alert.*,
	org.iottree.core.comp.*
	"%><%!
%><%
if(!Convert.checkReqEmpty(request, out,"op","uid"))
	return ;
String op = request.getParameter("op");
String uid = request.getParameter("uid") ;
EvtAlertTb tb = EvtAlertTb.getByUID(uid) ;

if(tb==null)
{
	out.print("no EvtAlertTb found") ;
	return ;
}

String title = request.getParameter("title") ;
String desc = request.getParameter("desc") ;

int pageidx = Convert.parseToInt32(request.getParameter("page"),1)-1;
int pagesize = Convert.parseToInt32(request.getParameter("limit"),10);

StringBuilder failedr = new StringBuilder() ;
switch(op)
{
case "list_items":
	int[] total_cc = new int[1] ;
	List<EvtAlertItem> ais = tb.listEvtAlertItems(pageidx, pagesize,total_cc,failedr);
	if(ais==null)
	{
		out.print(failedr) ;
		return ;
	}
%>
{"code":0,"msg":"","count":<%=total_cc[0]%>,"data":[<%
                                                    
boolean bfirst = true; 
for(EvtAlertItem ai:ais)
{
	if(bfirst)
		bfirst=false;
	else
		out.print(",");
	
	JSONObject tmpjo = ai.toJO() ;
	tmpjo.write(out) ;
}
%>]}
<%
	return ;
case "echart_tree":
	
default:
	return ;
}
%>