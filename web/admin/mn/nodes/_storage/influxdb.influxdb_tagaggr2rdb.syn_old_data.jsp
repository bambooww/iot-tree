<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.core.msgnet.store.influxdb.*,
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
	String prjid = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	
	//IMNContainer
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	MNManager mnm= MNManager.getInstance(prj) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	InfluxDB_TagAggr2RDB item =(InfluxDB_TagAggr2RDB)net.getItemById(itemid) ;
	if(item==null)
	{
		out.print("no item found") ;
		return ;
	}
	InfluxDB_TagAggr2RDB.AggrDT aggr_dt = item.getAggrDT() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style>
.save_btn
{
	position: absolute;
	right:5px;
	top:5px;
	color:#27ba7d;
	
}

.in_title {position: absolute;left:2px;top:50px;border:1px solid #ccc;background-color: #003a36;color:#00ffe2;cursor: pointer;}
.in_title:hover ~ .child  {display: block;}
.layui-form-item .layui-form-checkbox[lay-skin=primary] {
    margin-top:0px;
}
.layui-form-item {
    margin-bottom: 3px;
    margin-top: 3px;
}
</style>
</head>

<body>
Node:<%=item.getTitle() %><br>
AggrDT=<%=aggr_dt.getTitle() %><br>

start <input type="datetime-local" id="st"/>
end <input type="datetime-local" id="et" />
<button onclick="do_syn()">Do Syn</button>
 </body>
<script>
dlg.resize_to(600,500) ;
var container_id="<%=prjid%>";
var netid="<%=netid%>";
var itemid="<%=itemid%>";
var prjid="<%=prjid%>";

function do_syn()
{
	var dstr = $('#st').val();
	var estr = $('#et').val();
	if (!dstr || !estr)
	{
		dlg.msg("please input start and end date");return;
	}
	let st = new Date(dstr).getTime();
	let et = new Date(estr).getTime();
	if(st>et)
	{
		dlg.msg("start must < end date");return;
	}
	dlg.get_opener_w().mn_fire_node_evt(itemid,"syn_in_period",{st:st,et:et}) ;
}
</script>
</html>