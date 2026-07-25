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

.list
{
	position:relative;
	overflow:-y:auto;
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
    <label class="layui-form-label"><wbt:g>out,mode</wbt:g></label>
    <div class="layui-input-inline" style="width: 250px;">
    <select id="out_sty"  class="layui-input" lay-filter="out_sty" style="width:100px;border-right: 0px;">
<%
	for(NS_TagEvtTrigger.MsgOutSty outsty:NS_TagEvtTrigger.MsgOutSty.values())
	{
%>
<option value="<%=outsty.ordinal()%>"><%=outsty.getTitle() %></option>
<%
	}
%>
    </select>
    </div>
</div>
<div class="layui-form-item"  id="">
    <label class="layui-form-label"><wbt:g>tag,evt</wbt:g></label>
    <div class="layui-input-inline" style="width: 450px;">
 <div class="list">
<%
	for(UATag tag:prj.listTagsAll())
	{
		List<ValEvent> vas = tag.getValAlerts() ;
		if(vas==null||vas.size()<=0)
	continue ;
		String np = tag.getNodeCxtPathInPrj() ;
		String npt = tag.getNodeCxtPathTitleIn(prj) ;
%>
<div class="tag_item" id="np"><span style="font-weight: bold;">Tag:[<%=np%>] <%=npt%></span>  
<%
  	for(ValEvent va:vas)
  		{
  	String id = va.getUid() ;
  	String tt = Convert.plainToHtml(va.toTitleStr()) ;
  	String en_c = va.isEnable()?"green":"gray" ;
  	String en_t = va.isEnable()?"enabled":"disabled" ;
  %><div class="alert_item" title="<%=tt%>">
		<input type="checkbox" id="<%=id %>"  class="chk_alert" lay-ignore onclick="on_chk_alert('<%=id%>')" lay-skin="primary" />
		<span class="evt_nn" style="color:<%=en_c%>" title="<wbt:g><%=en_t%></wbt:g>"><%=tt %></span>
	<span class="tt"></span>
	
	</div>

<%
		}
%>
</div>
<%
	}
%>
 </div>
    </div>
</div>    
<script>

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
	let out_sty = get_input_val("out_sty",true,0) ;
	return {evt_ids:evt_ids,out_sty:out_sty} ;
}

function set_pm_jo(jo)
{
	let evt_ids = jo.evt_ids||[] ;
	for(let id of evt_ids)
	{
		$(document.getElementById(id)).prop("checked",true) ;
	}
	$("#out_sty").val(jo.out_sty||0) ;
}

function get_pm_size()
{
	return {w:700,h:450} ;
}

//on_init_pm_ok() ;
</script>