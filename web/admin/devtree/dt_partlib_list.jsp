<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

%><%
boolean bedit = "true".equalsIgnoreCase(request.getParameter("edit")) ;
boolean bsel = "true".equalsIgnoreCase(request.getParameter("sel")) ;
String tbh = "100%";
//if(!bedit)
//	tbh = "90%";

String libid = request.getParameter("libid") ;
if(libid==null)
	libid ="" ;

boolean bdlg = "true".equals(request.getParameter("dlg")) ;
%>
<html>
<head>
<jsp:include page="../head.jsp"></jsp:include>
</head>
<style>
table{border:0px solid skyblue;}
</style>
<script type="text/javascript">
var bdlg = <%=bdlg%>
dlg.resize_to(800,600) ;
</script>
<body>
<table style="width:100%;height:<%=tbh %>;border:0px solid red;">
	<tr >
		<td style="width:45%;height:100%"><iframe name="dev_left" src="dt_partlib_tps_list.jsp?sel=<%=bsel %>&edit=<%=bedit %>&libid=<%=libid %>" style="width:100%;height:100%;border:0"></iframe></td>
		<td style="width:55%;height:100%"><iframe name="dev_right" src="" style="width:100%;height:100%;border:0"></iframe></td>
	</tr>
</table>


</body>
<script type="text/javascript">
var sel_libid = '<%=libid%>' ;
var sel_parttp_id = "" ;
var sel_parttp_tt = tt;

var sel_partid = "" ;
var sel_part_tt = "" ;

function on_selected_parttp(libid,parttp_id,tt)
{
	let bchg = libid!=sel_libid || parttp_id!=sel_parttp_id;
	
	sel_libid = libid ;
	sel_parttp_id = parttp_id ;
	sel_parttp_tt = tt; 
	$("#selected_libcat_tt").html(tt) ;
	
	if(bchg)
	{
		sel_partid="" ;
		sel_part_tt=""
	}
		
}

function on_selected_part(part_id,tt)
{
	sel_partid = part_id ;
	sel_part_tt = t ;
	//$("#selected_dev_tt").html(tt) ;
}

function get_selected_part_tp(cb)
{
	if(!sel_parttp_id)
	{
		cb(false,"<wbt:g>pls,select,parttp</wbt:g>");return ;
	}
	uid = sel_libid+"."+sel_parttp_id ;
	let ret = {parttp_uid:uid,libid:sel_libid,parttp_id:sel_parttp_id,parttp_tt:sel_parttp_tt}; 
	cb(true,ret);
}

function get_selected_part(cb)
{
	if(!sel_parttp_id)
	{
		cb(false,"<wbt:g>pls,select,parttp</wbt:g>");return ;
	}
	if(!sel_partid)
	{
		cb(false,"<wbt:g>pls,select,part</wbt:g>");return ;
	}
	uid = sel_libid+"."+sel_parttp_id ;
	let ret = {parttp_uid:uid,libid:sel_libid,parttp_id:sel_parttp_id,parttp_tt:sel_parttp_tt,part_id:sel_partid,part_tt:sel_part_tt}; 
	cb(true,ret);
}
</script>
</html>