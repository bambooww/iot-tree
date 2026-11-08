<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,org.json.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
					org.iottree.driver.zigbee.zstack.*,
	org.iottree.driver.zigbee.zspec.*,
	org.iottree.driver.zigbee.zspec.util.*,org.iottree.driver.zigbee.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
	String path = request.getParameter("path") ;
	StringBuilder failedr = new StringBuilder() ;
	ZSDriver zs_drv = ZigbeeManager.getZSDriverByPath(path, failedr) ;
	if(zs_drv==null)
	{
		out.print(failedr.toString()) ;
		return ;
	}
	ZSCoor z_coor = zs_drv.getZSCoor() ;
	List<ZSDevice> eds = z_coor.listDevices() ;
	if(eds==null)
		eds = Arrays.asList() ;
	ZSSettings settings = z_coor.getSettings() ;
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../../head.jsp">
<jsp:param value="true" name="simple"/>
</jsp:include>
<style>
</style>
</head>
<body>
 <div style="margin:1px;" class="layui-form" style="background-color:#fff;">
 
  <div class="layui-form-item" >
    <label class="layui-form-label">Channel:</label>
      <div class="layui-input-inline" style="width:80px">
    	<select id="channel"  lay-filter="channel">
<%
		for(int ch = 11 ; ch <= 26 ; ch ++)
		{
%><option value="<%=ch %>" ><%=ch %></option><%
		}
%>
    	</select>
    </div>
    <div class="layui-input-inline" style="width:50px">
    	<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="set_ch()">Set</button>
    </div>
 </div>
 
 
</div>
</body>
<script>
var form ;
var path = "<%=path%>" ;
var settings = <%=settings.toJO()%>

layui.use(['form'], function()
{
	form = layui.form;
	$("#channel").val(settings.channel) ;
	form.render() ;
});

function set_ch()
{
	let ch = parseInt($("#channel").val());
	send_ajax("drv_ins.ajax.jsp",{op:"coor_set_ch",path:path,ch:ch},(bsucc,ret)=>{
		if(!bsucc || ret!='succ')
		{
			dlg.msg(ret);return ;
		}
		dlg.msg("set channel ["+ch+"] ok") ;
	});
}

</script>
</html>