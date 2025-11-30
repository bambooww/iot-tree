<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.alert.*,
				org.iottree.core.store.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return ;
	String prjid = request.getParameter("prjid") ;
UAPrj rep  = UAManager.getInstance().getPrjById(prjid) ;
if(rep==null)
{
	out.print("no prj found");
	return ;
}

AlertManager amgr = AlertManager.getInstancePrjN(rep.getName()) ;
AlertDef alert_def = amgr.getAlertDef() ;
int def_lvl = alert_def.getDefaultLvl() ;
AlertDef.Lvl[] lvls = alert_def.getLvls() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(600,500);
</script>
<style>
</style>
</head>
<body>

<table id="view_colorval"  class="layui-table" lay-filter="dl_list"  lay-size="sm" lay-even="true" style="width:95%" border="1">
   <thead style="background-color: #cccccc">
     <tr>
	  <td>Level</td>
	  <td>Default</td>
	  <td>Color</td>
	  <td>Sound</td>
	  <td>Blink</td>
	</tr>
  </thead>
		<tbody id="color_list">
<%
for(int i = 0 ; i < lvls.length ; i ++)
{
	int v = i+1 ;
	AlertDef.Lvl lvl = lvls[i] ;
	String blink_chk = lvl.isBlink()?"checked":"";
	String def_chk = (v==def_lvl)?"checked":"" ;
%>
			<tr id="lvl_tr_<%=v %>">
				<td>L<%=v %></td>
				<td><input type="radio" id="def_<%=v %>" name="def_lvl" value="<%=v%>" <%=def_chk %>/></td>
				<td><span id="color_s_<%=v %>" style="width: 30px; height: 30px; background-color: <%=lvl.getColor() %>; cursor: crosshair">&nbsp;&nbsp;&nbsp;&nbsp;</span>
				<input id="color_<%=v %>" value="<%=lvl.getColor() %>" onkeydown="on_color_chg(<%=v %>)" onchange="on_color_chg(<%=v %>)"/>
				</td>
				<td><input id="sound_<%=v %>" value="<%=lvl.getSound() %>" /></td>
				<td><input type="checkbox" id="blink_<%=v %>" <%=blink_chk %> /></td>
			</tr>
<%
}
%>
			
		</tbody>
		<tfoot>
    
  </tfoot>
</table>
<div style="position: relative;right: 30px;float: right;top:50px;">
  		<button id="btn_save" class="layui-btn layui-btn-primary" type="button" lay-filter="set_param" onclick="save_alert_def()">Save</button>
 </div>
</body>
<script type="text/javascript">

var prjid = "<%=prjid%>" ;
layui.use('form', function(){
	var form = layui.form;
	form.render();
});
	
function on_color_chg(lv)
{
	let c = $("#color_"+lv).val() ;
	$("#color_s_"+lv).css("background-color",c) ;
	
	$("#btn_save").addClass("layui-btn-warm")
}
	
function win_close()
{
	dlg.close(0);
}

function save_alert_def()
{
	let deflvl = parseInt($("input[name='def_lvl']:checked").val());
	let lvls=[] ;
	for(let lv = 1 ; lv<=5 ; lv ++)
	{
		let def = $("#def_"+lv).prop("checked") ;
		let c = $("#color_"+lv).val() ;
		if(!c)
		{
			dlg.msg("color cannot be empty!");return ;
		}
		let s = $("#sound_"+lv).val() ;
		let b =  $("#blink_"+lv).prop("checked") ;
		lvls.push({lvl:lv,color:c,sound:s,blink:b}) ;
	}
	let ob = {def_lvl:deflvl,lvls:lvls} ;
	let jstr = JSON.stringify(ob) ;
	send_ajax("prj_alert_ajax.jsp",{op:"alert_def_set",prjid:prjid,jstr:jstr},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("succ")!=0)
		{
			dlg.msg(ret);return ;
		}
		dlg.msg("set ok") ;
	}) ;
	$("#btn_save").removeClass("layui-btn-warm")
}


</script>
</html>