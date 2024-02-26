<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.alert.*,
	org.iottree.core.store.gdb.*,
	org.iottree.core.comp.*
	"%><%!
static final int PAGE_SIZE = 40 ;
%><%
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return ;

String prjid = request.getParameter("prjid");
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}

String sor_name = request.getParameter("sor") ;
String start_dt = request.getParameter("start_dt");
Date startdt = null ;
if(Convert.isNotNullEmpty(start_dt))
	startdt = Convert.toCalendar(start_dt).getTime() ;
String end_dt = request.getParameter("end_dt") ;
Date enddt = null ;
if(Convert.isNotNullEmpty(end_dt))
	enddt =  Convert.toCalendar(end_dt).getTime() ;
String hander = request.getParameter("handler") ;
int pageidx = Convert.parseToInt32(request.getParameter("pageidx"), 0) ;

AlertManager amgr = AlertManager.getInstance(prjid) ;
DataTable dt = amgr.HIS_selectRecord(sor_name, startdt, enddt, hander, pageidx, PAGE_SIZE);
int cc = dt.getTotalCount() ;
boolean has_next = dt.getRowNum()>=PAGE_SIZE ;
for(DataRow dr:dt.getRows())
{
	Date triggerdt = dr.getValueDate("TriggerDT", null) ;
	Date releasedt = dr.getValueDate("ReleaseDT", null) ;
	String handler = dr.getValueStr("Handler", "") ;
	String tag = dr.getValueStr("Tag", "") ;
	String type = dr.getValueStr("Type", "") ;
	String id = dr.getValueStr("AutoId", "") ;
	String val = dr.getValueStr("Value", "") ;
	int lvl = dr.getValueInt32("Level", 0) ;
	String prompt= dr.getValueStr("Prompt", "") ;
%>
<%--
<th>Time</th>
      <th>Handler</th>
      <th>Tag</th>
      <th>Type</th>
      <th>Value</th>
      <th>Level</th>
      <th>Prompt</th>
 --%>
<tr id="ai_row_<%=id%>" class="file_row" onclick="on_row_clk('<%=id%>')">
    <td><%=Convert.toFullYMDHMS(triggerdt) %></td>
    <td><%=Convert.toFullYMDHMS(releasedt)  %></td>
    <td><%=handler %></td>
    <td><%=tag%></td>
    <td><%=Convert.plainToHtml(type) %></td>
    <td><%=val %></td>
    <td><%=lvl %></td>
    <td><%=Convert.plainToHtml(prompt) %></td>
</tr>
<%
}
%>
<script>
	page_has_next=<%=has_next%> ;
	page_idx=<%=pageidx%> ;
	page_total=<%=cc%>;
</script>
