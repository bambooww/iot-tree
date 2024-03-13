<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	java.text.*,
	org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.tssdb.*,
	org.iottree.core.store.record.*,
	org.iottree.core.alert.*,
	org.iottree.core.store.gdb.*,
	org.iottree.core.comp.*
	"%><%!
static final int PAGE_SIZE = 40 ;
%><%
if(!Convert.checkReqEmpty(request, out, "prjid","tag","proid"))
	return ;
long page_last_dt = Convert.parseToInt64(request.getParameter("page_last_dt"), -1) ;
if(page_last_dt<=0)
	page_last_dt = Long.MAX_VALUE ;
String prjid = request.getParameter("prjid");
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}

RecManager recm = RecManager.getInstance(prjid) ;
TSSAdapterPrj tssadp = recm.getTSSAdapterPrj() ;

String tagpath = request.getParameter("tag") ;
String proid = request.getParameter("proid") ;
RecPro rp = recm.getRecProById(proid) ;
if(rp==null || !(rp instanceof RecProL1DValue))
{
	out.print("no RecProL1DValue found") ;
	return ;
}
RecProL1DValue prodv = (RecProL1DValue)rp ;
List<RecProL1DValue.RowOb> rowobs = prodv.readRowsForPage(tagpath, page_last_dt, true, PAGE_SIZE) ;
int rowc = rowobs.size() ;
boolean has_next =rowc>=PAGE_SIZE ;

RecProL1DValue.ByWay way = prodv.getWay() ;
SimpleDateFormat dfmt = way.getDTFormat() ;

StringBuilder jarrsb = new StringBuilder() ;
jarrsb.append("[") ;
boolean bfirst = true;
for(RecProL1DValue.RowOb ob:rowobs)
{
	long dt = ob.dt ;
	Date ddt = new Date(dt) ;
	Number val = ob.getVal();
	
	page_last_dt = dt ;
	if(bfirst) bfirst=false;
	else jarrsb.append(",") ;
	
	String dstr = dfmt.format(ddt) ;
	jarrsb.append("[\"").append(dstr).append("\",").append(val).append("]") ;
	
	
%>

<%--
 <th>From</th>
      <th>To</th>
      <th>Valid</th>
      <th>Value</th>
 --%>
<tr id="ai_row_<%=dt%>" class="file_row" onclick="on_row_clk('<%=dt%>')" title="">
    <td title=""><%=dstr %></td>
    <td><%=val %></td>
</tr>
<%
}
jarrsb.append("]") ;
%>
<script>
	page_has_next=<%=has_next%> ;
	tmp_ds = <%=jarrsb%>;
	page_last_dt = <%=page_last_dt%>
	show_chart(tmp_ds);
</script>
