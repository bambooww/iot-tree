<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.*,
	org.iottree.core.alert.*,
	org.iottree.core.store.gdb.*,
	org.iottree.core.comp.*
	"%><%!
static final int PAGE_SIZE = 40 ;
%><%
if(!Convert.checkReqEmpty(request, out, "prjid","hid","oid"))
	return ;
int pageidx = Convert.parseToInt32(request.getParameter("pageidx"), 0) ;
String prjid = request.getParameter("prjid");
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
String hid= request.getParameter("hid");
String oid= request.getParameter("oid");
StoreManager stom = StoreManager.getInstance(prjid) ;
StoreHandler storeh = stom.getHandlerById(hid) ;
if(storeh==null)
{
	out.print("no handler found") ;
	return ;
}
StoreOutTbHis storeo = (StoreOutTbHis)storeh.getOutById(oid) ;
if(storeo==null)
{
	out.print("no outer found") ;
	return ;
}
StringBuilder failedr = new StringBuilder() ;
if(!storeo.checkOrInitOk(failedr))
{
	//out.print("the outer is not enable or init failed!") ;
	out.print(failedr) ;
	return ;
}

String start_dt = request.getParameter("start_dt");
Date startdt = null ;
if(Convert.isNotNullEmpty(start_dt))
	startdt = Convert.toCalendar(start_dt).getTime() ;
String end_dt = request.getParameter("end_dt") ;
Date enddt = null ;
if(Convert.isNotNullEmpty(end_dt))
	enddt =  Convert.toCalendar(end_dt).getTime() ;

String tagpath = request.getParameter("tag") ;
String validstr = request.getParameter("valid") ;
Boolean bvalid = null ;
if(Convert.isNotNullEmpty(validstr))
	bvalid = "true".equalsIgnoreCase(validstr) ;

DataTable dt = storeo.selectRecords(tagpath, startdt, enddt, bvalid, pageidx, PAGE_SIZE);
int cc = dt.getTotalCount() ;
boolean has_next = dt.getRowNum()>=PAGE_SIZE ;

StringBuilder jarrsb = new StringBuilder() ;
jarrsb.append("[") ;
boolean bfirst = true;
for(DataRow dr:dt.getRows())
{
	String autoid = dr.getValueStr(storeo.getColAutoId(), "") ;
	Date updt = dr.getValueDate(storeo.getColUpDT(), null) ;
	Date chgdt = dr.getValueDate(storeo.getColChgDT(), null) ;
	String tagp = dr.getValueStr(storeo.getColTag(), "") ;
	String valtp = dr.getValueStr(storeo.getColValTp(), "") ;
	boolean valid = dr.getValueBool(storeo.getColValid(), true) ;
	Object val = storeo.getValInRow(dr) ;
	int alert_num = dr.getValueInt32(storeo.getColAlertNum(), 0) ;
	String alert_ppt = dr.getValueStr(storeo.getColAlertInf(), "") ;
	String alert_str="" ;
	if(alert_num>0)
		alert_str= "["+alert_num+"] "+alert_ppt ;
	
	if(bfirst) bfirst=false;
	else jarrsb.append(",") ;
	
	if(chgdt.equals(updt))
	{
		if(valid)
			jarrsb.append("[\"").append(Convert.toFullYMDHMS(updt)).append("\",").append(val).append("]") ;
		else
			jarrsb.append("[\"").append(Convert.toFullYMDHMS(updt)).append("\",\"\"]") ;
	}
	else
	{
		if(valid)
			jarrsb.append("[\"").append(Convert.toFullYMDHMS(chgdt)).append("\",").append(val).append("],")
				.append("[\"").append(Convert.toFullYMDHMS(updt)).append("\",").append(val).append("]");
		else
			jarrsb.append("[\"").append(Convert.toFullYMDHMS(chgdt)).append("\",\"\"],")
				.append("[\"").append(Convert.toFullYMDHMS(updt)).append("\",\"\"]");
	}
%>

<%--
 <th>Tag</th>
      <th>Update Time</th>
      <th>Change Time</th>
      <th>Valid</th>
      <th>Type</th>
      <th>Value</th>
      <th>Alert</th>
 --%>
<tr id="ai_row_<%=autoid%>" class="file_row" onclick="on_row_clk('<%=autoid%>')" title="<%=tagp%> <%=valtp%>">
    <td><%=Convert.toFullYMDHMS(updt) %></td>
    <td><%=Convert.toFullYMDHMS(chgdt)  %></td>
    <td><%=(valid?"√":"×") %></td>
    <td><%=val %></td>
    <td><%=Convert.plainToHtml(alert_str) %></td>
</tr>
<%
}
jarrsb.append("]") ;
%>
<script>
	page_has_next=<%=has_next%> ;
	page_idx=<%=pageidx%> ;
	page_total=<%=cc%>;
	tmp_ds = <%=jarrsb%>;
	
	show_chart(tmp_ds);
</script>
