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

.left  {position:absolute;left:0px;top:0px;width:200px;bottom:0px;overflow: hidden;border:0px solid red;}
.right {position:absolute;left:201px;top:0px;right:0px;bottom:0px;z-index:10000;}
</style>
</head>
<body  style="overflow: hidden;">
<div class="left">
	<iframe src="./left.jsp?path=<%=path %>" style="width:100%;height:100%;border:0px;"></iframe>
</div>
<div class="right">
<iframe id="if_right"  style="width:99%;height:100%;border:1px solid #ccc;" src=""></iframe>
</div>
<script>
dlg.dlg_top=true;
var form ;
var table ;
var table_cur_page = 1 ;

layui.use(['table','form'], function()
{
	form = layui.form;
	form.render();
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