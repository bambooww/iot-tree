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
	org.iottree.core.msgnet.nodes.*,
	org.iottree.core.msgnet.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id"))
		return ;
	
	String container_id = request.getParameter("container_id") ;
	MNManager mnm = MNManager.getInstanceByContainerId(container_id) ;
	
UAPrj prj =  UAManager.getInstance().getPrjById(container_id) ;
if(prj==null)
{
	out.print("no project node found");
	return ;
}

%>
<style>

table{border-collapse:collapse;}
body,td{font-size:12px;cursor:default;}
thead td {font-size:14px;font-weight: bold;}
tbody td {border:1px solid #cccccc}

tr:hover {background-color: #666666;}

.list
{
	position:relative;
	overflow:-y:auto;
	width:100%;
}
.tag_item
{
	position: relative;
}


.alert_item
{
	position:relative;
	margin-bottom:3px;
	top:1px;
	height:25px;
	font-size:15px;
	margin-left:30px;
	right:0px;
	
}

.alert_item .chk_alert
{
	position: absolute;
	left:10px;
	top:6px;
}

.alert_item .tt
{
	position:absolute;
	left:20px;right:20px;
	text-overflow:ellipsis;
	overflow:hidden;
	white-space:nowrap;
}

.alert_item .en
{
position:absolute;
	top:3px;
	left:2px;
}

.alert_item .evt_nn
{
	position: absolute;
	left:40px;
	top:3px;
	border:1px solid #dddddd;
	right:2px;
}
</style>
<div class="layui-form-item"  id="">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline" style="width: 250px;">
    	no output when no triggered events:<input type="checkbox" id="no_out_no_evt" lay-skin="primary" />
    </div>
</div>
<div class="layui-form-item"  id="">
    <label class="layui-form-label"><wbt:g>tag,evt</wbt:g></label>
    <div class="layui-input-inline" style="width: 750px;">
 <div class="list">
 	<table style="width:100%;">
 		<thead>
 			<tr>
 			<td><input id="chk_all" type="checkbox" lay-ignore onclick="chk_all_or_null()" lay-skin="primary" /></td>
 			<td>Tag Path</td>
 			<td>Title</td>
 			<td >Alert/Event</td>
 			</tr>
 		</thead>
 		<tbody>
<%
	for(UATag tag:prj.listTagsAll())
	{
		List<ValEvent> vas = tag.getValAlerts() ;
		if(vas==null||vas.size()<=0)
	continue ;
		String np = tag.getNodeCxtPathInPrj() ;
		String npt = tag.getTitle() ;
		for(ValEvent va:vas)
		{
	String id = va.getUid() ;
	String tt = Convert.plainToHtml(va.toTitleStr()) ;
	String en_c = va.isEnable()?"green":"gray" ;
	String en_t = va.isEnable()?"enabled":"disabled" ;
%>
 		<tr onclick="on_chk_alert('<%=id%>')" >
 		   <td><input type="checkbox" id="<%=id %>"  class="chk_alert" lay-ignore lay-skin="primary" /></td>
 			<td><%=np %></td>
 			<td><%=npt %></td>
 			<td style="color:<%=en_c%>" title="<wbt:g><%=en_t%></wbt:g>"><%=tt %></td>
 		</tr>
<%
		}
	}
%>
		</tbody>
 	</table>

 </div>
    </div>
</div>    
<script>

function chk_all_or_null()
{
	let chked = $("#chk_all").prop("checked") ;
	$(".chk_alert").prop("checked",chked) ;
}

function on_chk_alert(id)
{
	let ele = $(document.getElementById(id)) ;
	let chked = ele.prop("checked");
	ele.prop("checked",!chked) ;
}

function on_after_pm_show(form)
{
	
}

function update_ui()
{
	
}

function get_chk_vals(name)
{
	let ret=[] ;
	$("input[name="+name+"]").each( function () {
	       let ob = $(this) ;
	       if(ob.prop("checked"))
	    	   ret.push(ob.val()) ;
	   });
	return ret ;
}

function get_pm_jo()
{
	let evt_ids = [] ;
	$(".chk_alert").each(function(){
		let id = $(this).attr("id") ;
		if($(this).prop("checked"))
			evt_ids.push(id) ;
	});
	let no_out_no_evt = $("#no_out_no_evt").prop("checked");
	return {evt_ids:evt_ids,no_out_no_evt:no_out_no_evt} ;
}

function set_pm_jo(jo)
{
	$("#no_out_no_evt").prop("checked",jo.no_out_no_evt||false) ;
	let evt_ids = jo.evt_ids||[] ;
	for(let id of evt_ids)
	{
		$(document.getElementById(id)).prop("checked",true) ;
	}
	//$("#out_sty").val(jo.out_sty||0) ;
}

function get_pm_size()
{
	return {w:900,h:550} ;
}

//on_init_pm_ok() ;
</script>