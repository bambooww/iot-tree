<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
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
if(!Convert.checkReqEmpty(request, out, "prjid","tag"))
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

String start_dt = request.getParameter("start_dt");
Date startdt = null ;
long from_dt = -1 ;
if(Convert.isNotNullEmpty(start_dt))
	startdt = Convert.toCalendar(start_dt).getTime() ;
String end_dt = request.getParameter("end_dt") ;
Date enddt = null ;
long to_dt = Long.MAX_VALUE ;
if(Convert.isNotNullEmpty(end_dt))
{
	enddt =  Convert.toCalendar(end_dt).getTime() ;
	to_dt = enddt.getTime() ;
}

String tagpath = request.getParameter("tag") ;
String validstr = request.getParameter("valid") ;
Boolean bvalid = null ;
if(Convert.isNotNullEmpty(validstr))
	bvalid = "true".equalsIgnoreCase(validstr) ;

TSSTagSegs<?> tss = tssadp.getTagSegs(tagpath) ;
if(tss==null)
{
	out.print("no Tag Segs found") ;
	return ;
}
// desc order out
List<TSSValSeg<?>> vss = tss.readValSegsNoT(from_dt, page_last_dt,true,PAGE_SIZE) ;
int rowc = vss.size() ;
boolean has_next =rowc>=PAGE_SIZE ;

StringBuilder jarrsb = new StringBuilder() ;
jarrsb.append("[") ;
boolean bfirst = true;
for(TSSValSeg<?> vs:vss)
{
	long sdt = vs.getStartDT() ;
	long edt = vs.getEndDT()-1 ;
	String id = "s_"+sdt ;
	
	Date fdt = new Date(sdt) ;
	Date todt= new Date(edt) ;
	boolean valid = vs.isValid() ;
	Object val = vs.getVal();
	
	page_last_dt = sdt ;
	if(bfirst) bfirst=false;
	else jarrsb.append(",") ;
	
	/*
	if(sdt==edt)
	{
		if(valid)
			jarrsb.append("[").append(edt).append(",").append(val).append("]") ;
		else
			jarrsb.append("[").append(sdt).append(",\"\"]") ;
	}
	else
	{
		if(valid)
			jarrsb.append("[").append(edt).append(val).append("],")
				.append("[").append(sdt).append(",").append(val).append("]");
		else
			jarrsb.append("[").append(sdt).append(",null],")
				.append("[").append(sdt).append(",\"\"]");
	}
	*/
	
	if(sdt==edt)
	{
		if(valid)
			jarrsb.append("[\"").append(Convert.toXmlValDateStr(fdt)).append("\",").append(val).append("]") ;
		else
			jarrsb.append("[\"").append(Convert.toXmlValDateStr(fdt)).append("\",\"\"]") ;
	}
	else
	{
		if(valid)
			jarrsb.append("[\"").append(Convert.toXmlValDateStr(todt)).append("\",").append(val).append("],")
				.append("[\"").append(Convert.toXmlValDateStr(fdt)).append("\",").append(val).append("]");
		else
			jarrsb.append("[\"").append(Convert.toXmlValDateStr(todt)).append("\",\"\"],")
			//.append("[\"").append(Convert.toXmlValDateStr(new Date(edt-1000))).append("\",\"\"],")
			//.append("[\"").append(Convert.toXmlValDateStr(new Date(sdt+1000))).append("\",\"\"],")
				.append("[\"").append(Convert.toXmlValDateStr(fdt)).append("\",\"\"]");
	}
	
%>

<%--
 <th>From</th>
      <th>To</th>
      <th>Valid</th>
      <th>Value</th>
 --%>
<tr id="ai_row_<%=sdt%>" class="file_row" onclick="on_row_clk('<%=sdt%>')" title="">
    <td title="<%=Convert.toXmlValDateStr(fdt) %>"><%=Convert.toHMS(fdt) %></td>
    <td title="<%=Convert.toXmlValDateStr(todt)  %>"><%=Convert.toHMS(todt)  %></td>
    <td><%=(edt-sdt)/1000  %></td>
    <td><%=(valid?"√":"×") %></td>
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
