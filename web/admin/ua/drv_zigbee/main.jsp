<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	org.iottree.driver.zigbee.*,
	org.iottree.driver.zigbee.zspec.*,
	org.iottree.driver.zigbee.zstack.*,
	org.iottree.driver.zigbee.zstack.msg_sys.*,
	org.iottree.driver.zigbee.zstack.msg_zdo.*,
	org.iottree.driver.zigbee.zstack.msg_util.*,
	org.iottree.driver.zigbee.zstack.req_rsp.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
	String path = request.getParameter("path") ;
	StringBuilder failedr = new StringBuilder() ;
	ZSDriver dd = ZigbeeManager.getZSDriverByPath(path, failedr) ;
	if(dd==null)
	{
		out.print(failedr.toString()) ;
		return ;
	}
	
	String nav_t = path ;
	String dd_t = dd.getTitle() ;
	ZSCoor z_coor = dd.getZSCoor();
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../../head.jsp">
<jsp:param value="true" name="simple"/>
</jsp:include>
    <style>
.layui-form-label{
    width: 120px;
}
.layui-input-block {
    margin-left: 140px;
    min-height: 36px;
    width:240px;
}
.layui-table-view
{
	margin-top: 1px;
}
  .layui-table-cell {
    height: auto;
    line-height: 18px;
}

.top_mgr {
position:absolute;right:200px;
top:0px;height:30px;width:140px;
overflow:hidden;
margin:5px;
border:1px solid #cecece;
white-space: nowrap;
}
.top_mgr .item {
	margin: 2px;width:25px;height:25px;
	font-size: 13px;
}

.d_btn
{
margin: 2px;width:18px;height:18px;
	font-size: 12px;
}
.left  {position:absolute;left:0px;top:0px;width:200px;bottom:0px;overflow: hidden;}
.right {position:absolute;left:200px;top:0px;right:0px;bottom:0px;}
#stat_chart {width:100%;height:300px;top:40px;}
</style>
</head>
<body  style="overflow: hidden;">
<div class="left">
	<iframe src="./left.jsp?path=<%=path %>" style="width:100%;height:100%;border:0px;"></iframe>
</div>
<div class="right">
<iframe id="if_right"  style="width:100%;height:100%;border:1px solid #ccc;" src=""></iframe>
</div>
<script type="text/html" id="row_toolbar">
</script>

<script>
dlg.dlg_top=true;
var form ;
var table ;
var table_cur_page = 1 ;

layui.use(['table','form'], function()
{
	form = layui.form;
});


function set_cur_item(itemid,tt,u)
{
	$("#if_right").attr("src",u) ;
}

function on_permit_join_tick()
{
	let w = $("#if_right")[0].contentWindow ;
	if(!w) return ;
	if(w.on_permit_join_tick)
		w.on_permit_join_tick();
}

</script>
</body>
</html>