<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
if(!Convert.checkReqEmpty(request, out, "prjid","cptp"))
	return;
String prjid = request.getParameter("prjid") ;
String cptp = request.getParameter("cptp") ;//ConnProOPCUA.TP;//
ConnProvider cp = ConnManager.getInstance().getOrCreateConnProviderSingle(prjid, cptp);
if(cp==null)
{
	String cpid = request.getParameter("cpid") ;
	if(Convert.isNotNullEmpty(cpid))
	{
		cp =  ConnManager.getInstance().getConnProviderById(prjid, cpid);
	}
}
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnPtIOTTreeNode cpt = null ;


if(Convert.isNullOrEmpty(connid))
{
	out.print("no connid input") ;
	return;
}

cpt = (ConnPtIOTTreeNode)cp.getConnById(connid) ;
if(cpt==null)
{
	out.print("no connection found") ;
	return ;
}

List<String> topics = cpt.getMsgTopics() ;
boolean bconn_ok = cpt.isConnReady() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp"></jsp:include>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
}

.ppt
{
	width:96%;
	margin: 10px;
	margin-left:2%;
	align-content: center;
	border:1px solid;
	height:80px;
}

.sel_item
{
	width:80%;
	margin: 20px;
	margin-left:60px;
	align-content: center;
}
</style>
<script>
dlg.resize_to(550,300);
</script>
</head>
<body>
<div class="ppt">
	<div style="font-size: 18px;color:green;"><wbt:g>syn_tt</wbt:g></div>
<%
if(!bconn_ok)
{
%><div style="font-size: 15px;color:red;"><wbt:g>syn_no_conn_pt</wbt:g></div><%
}
else
{
%><div style="font-size: 15px;color:blue;"><wbt:g>pls_start_syn_btn</wbt:g>.<span style="color:red;"><wbt:g>syn_overw_pt</wbt:g></span>!</div><%
}
String btn_disb = bconn_ok?"":"layui-btn-disabled" ;

%>
</div>
<div class="sel_item">
		<button style="width:80%"  class="layui-btn  <%=btn_disb %>" title="" onclick="do_syn()"><i id="ico_syn" class="fa fa-refresh" aria-hidden="true"></i><wbt:g>start_syn</wbt:g></button>
</div>
 <div class="sel_item">
	<span id="st_inf" style="font-size: 15px;"></span>
</div>

</body>
<script type="text/javascript">
var prjid="<%=prjid%>";
var cpid = "<%=cpid%>";
var cptp = "<%=cptp%>";
var connid = "<%=connid%>";

var bconn_ok = <%=bconn_ok%> ;

var up_intv = null ;

var b_syn_ok=false;

layui.use(['form', 'laydate', 'util'], function(){
	  var form = layui.form;
	 
});


function do_syn()
{
	if(!bconn_ok)
	{
		dlg.msg("<wbt:g>syn_no_conn_pt</wbt:g>") ;
		return ;
	}
	
	if(up_intv)
	{
		dlg.msg("<wbt:g>syn_is_running</wbt:g>") ;
		return ;
	}
	
	var pm={op:"syn_tree",prjid:prjid,cptp:cptp,cpid:cpid,connid:connid} ;
	send_ajax("cpt_iottree_node_ajax.jsp",pm,function(bsucc,ret){
		if(!bsucc||ret!='ok')
		{
			dlg.msg(ret) ;
			return ;
		}
		start_syn_lis();
	});
}



function start_syn_lis()
{
	b_syn_ok=false;
	$("#ico_syn").addClass("fa-spin");
	$("#st_inf").html("<wbt:g>syn_is_running</wbt:g>...").css("color","blue") ;
	up_intv = setInterval(update_st,1000) ;
}

function update_st()
{
	var pm={op:"syn_st",prjid:prjid,cptp:cptp,cpid:cpid,connid:connid} ;
	send_ajax("cpt_iottree_node_ajax.jsp",pm,function(bsucc,ret){
		if(!bsucc||ret.indexOf("{")!=0)
		{
			$("#st_inf").html(ret) ;
			return ;
		}
		let ob = null ;
		eval("ob="+ret) ;
		show_st(ob);
	});
}
	
function show_st(ob)
{
	//console.log(ob)
	if(ob.in_syn)
	{
		return ;
	}
	
	//show
	if(ob.syn_ok)
	{
		$("#st_inf").html("<wbt:g>syn_ok</wbt:g>").css("color","green") ;
		b_syn_ok=true ;
	}
	else
	{
		if(ob.resp_to)
			$("#st_inf").html("<wbt:g>syn_timeout</wbt:g>") ;
		else
			$("#st_inf").html(ob.syn_err) ;
		$("#st_inf").css("color","red") ;
	}
	$("#ico_syn").removeClass("fa-spin");
	
	if(up_intv)
	{
		clearInterval(up_intv);
		up_intv = null ;
	}
}

function is_syn_ok()
{
	return b_syn_ok ;
}

</script>
</html>