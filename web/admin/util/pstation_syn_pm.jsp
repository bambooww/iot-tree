<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.cxt.*,
	org.iottree.core.ws.*,
	org.iottree.core.sim.*,
	org.iottree.pro.*,
	org.iottree.core.station.*,
	org.iottree.core.util.xmldata.*
"%><%@ taglib uri="wb_tag" prefix="w"%><%

if(!Convert.checkReqEmpty(request,  out, "id"))
	return ;
String id = request.getParameter("id") ;
List<UAPrj> prjs = UAManager.getInstance().listPrjs();
String using_lan = Lan.getUsingLang() ;

PlatInsManager platf = PlatInsManager.getInstance() ;
PStation pstation = platf.getStationById(id) ;
if(pstation==null)
{
	out.print("no pstation found") ;
	return ;
}
%><!DOCTYPE html>
<html class="">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width,minimum-scale=1,initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1">
    <title>IOT-Tree</title>
    <link href="/favicon.ico" rel="shortcut icon" type="image/x-icon">
<jsp:include page="../head.jsp"></jsp:include>
 <style>
.hd {font-weight: bold;background-color: #f0f0f0;}
.prj_item
{
	position:relative;
	margin:20px;
	border:1px solid blue;
	border-radius: 5px;
}
.set_pm_btn {position: absolute;right:10px;height:30px;top:10px;}
 </style>
 <script type="text/javascript">
 dlg.resize_to(800,600);
 </script>
</head>
<body aria-hidden="false">
<div class="hd"><w:g>remote_station_pm_set</w:g></div>
<%

	PStation ps = pstation ;
	String clientip = ps.RT_getClientIP() ;
	String conndt = Convert.toFullYMDHMS(new Date(ps.RT_getClientOpenDT())) ;
	List<PStation.PrjST> prjsts = ps.RT_getPrjSTs() ;
%>
	<div class="station">
	<%=ps.getTitle() %> [<%=ps.getId() %>] 
	
	<br> &lt;- <%=clientip %> [<%=conndt %>]
	<br>
<%
	if(prjsts!=null)
	{
		for(PStation.PrjST prjst:prjsts)
		{
			String chked = prjst.isAutoStart()?"checked":"" ;
			String syn_chked = prjst.isDataSynEnable()?"checked":"" ;
			long syn_intv= prjst.getDataSynIntvMs() ;
			
			String failed_keep_chked = prjst.isFailedKeep()?"checked":"" ;
			long keep_max_len = prjst.getKeepMaxLen() ;
			long last_recv_dt = prjst.getLastRecvDT() ;
			String lastrdt = "" ;
			if(last_recv_dt>0)
				lastrdt = Convert.toFullYMDHMS(new Date(last_recv_dt)) ;
%>
	<div class="prj_item">
	<%=prjst.getPrjName() %>  running=<%=prjst.isRunning() %> auto start=<%=prjst.isAutoStart() %>
	  -&gt;[<%=lastrdt%>]
	<br>
		
	 	
<%
	String p_station_prj = ps.getId()+"_"+prjst.getPrjName() ;
	UAPrj p_prj = UAManager.getInstance().getPrjByName(p_station_prj) ;
	if(p_prj!=null)
	{
%>
		
<%
	}
%>
	 <input type="checkbox" id="autostart_<%=ps.getId() %>_<%=prjst.getPrjName() %>" <%=chked %>/><w:g>prj_auto_start</w:g> <br>
	 <input type="checkbox" id="syn_en_<%=ps.getId() %>_<%=prjst.getPrjName() %>" <%=syn_chked %>/><w:g>en_data_syn</w:g>
	 &nbsp;&nbsp;&nbsp;<w:g>intv</w:g> <input type="number" id="syn_intv_<%=ps.getId() %>_<%=prjst.getPrjName() %>"  value="<%=syn_intv %>" style="width:65px"/><w:g>ms</w:g><br>
	 <input type="checkbox" id="failed_keep_<%=ps.getId() %>_<%=prjst.getPrjName() %>"  <%=failed_keep_chked %> /><w:g>save_bf_sending</w:g> 
	 <w:g>max_len</w:g> <input type="number" id="keep_max_len_<%=ps.getId() %>_<%=prjst.getPrjName() %>"  value="<%=keep_max_len %>" style="width:65px"/>
	 <button class="set_pm_btn" title="set param" onclick="station_prj_set_pm('<%=ps.getId() %>','<%=prjst.getPrjName() %>')"><w:g>set_pm</w:g></button>
	 
	 </div>
<%
		}
	}
%>

</div>
	<%

%>



</body>
<script type="text/javascript">

var module2files = {} ;

function station_prj_set_pm(stationid,prjname)
{
	let bautostart = $(`#autostart_\${stationid}_\${prjname}`).prop("checked") ;
	let data_syn_en = $(`#syn_en_\${stationid}_\${prjname}`).prop("checked") ;
	let data_syn_intv = $(`#syn_intv_\${stationid}_\${prjname}`).val() ;
	let bfailed_keep = $(`#failed_keep_\${stationid}_\${prjname}`).prop("checked") ;
	let keep_max_len  = $(`#keep_max_len_\${stationid}_\${prjname}`).val() ;
	send_ajax("pstation_remote_ajax.jsp",
		{op:"station_prj_pm",stationid:stationid,prj:prjname,auto_start:bautostart,data_syn_en:data_syn_en,data_syn_intv:data_syn_intv,
			failed_keep:bfailed_keep,keep_max_len:keep_max_len},
		(bsucc,ret)=>{
		dlg.msg(ret) ;
	});
}


</script>
</html>
