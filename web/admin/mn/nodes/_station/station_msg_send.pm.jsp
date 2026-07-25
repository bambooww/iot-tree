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
	org.iottree.core.msgnet.store.*,
	org.iottree.core.station.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
	String container_id = request.getParameter("container_id");
	String netid = request.getParameter("netid") ;
	String itemid = request.getParameter("itemid") ;
	MNManager mnm = MNManager.getInstanceByContainerId(container_id) ;
	MNNet net = mnm.getNetById(netid) ;
	if(net==null)
	{
		out.print("no net found") ;
		return ;
	}
	IMNContainer mnc = net.getBelongTo().getBelongTo() ;
	if(mnc==null || !(mnc instanceof UAPrj))
	{
		out.print("no in prj") ;
		return ;
	}
	UAPrj prj = (UAPrj)mnc ;
	String prj_path = "/"+prj.getName() ;
	MNBase item =net.getItemById(itemid) ;
	if(item==null || !(item instanceof StationMsgSend_NE))
	{
		out.print("no item found") ;
		return ;
	}
	
	StationMsgSend_NE ms_node= (StationMsgSend_NE)item ;
	
%>

 <div class="layui-form-item">
    <label class="layui-form-label">Topic</label>
    <div class="layui-input-inline" style="width: 200px;">
      <input type="text" id="topic" name="topic" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid"> Pack Zip:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="checkbox"  id="zip" name="zip" class="layui-input" lay-skin="primary" />
    </div>
 </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Failed Save</label>
    <div class="layui-input-inline" style="width: 50px;">
      <input type="checkbox"  id="failed_save" name="failed_save" class="layui-input" lay-skin="primary" />
    </div>
    <div class="layui-form-mid"> Name:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="text" id="save_name" name="save_name" value=""  autocomplete="off"  class="layui-input" >
    </div>
    <div class="layui-form-mid"> Max Num:</div>
    <div class="layui-input-inline" style="width: 100px;">
      <input type="number" id="save_maxn" name="save_maxn" value=""  autocomplete="off"  class="layui-input" >
    </div>
 </div>
<script>
var prj_path = "<%=prj_path%>";
var container_id="<%=container_id%>";
var netid="<%=netid%>";

function on_after_pm_show(form)
{
	
}


function get_pm_jo()
{
	let topic = $("#topic").val() ;
	let save_name = $("#save_name").val() ;
	let save_maxn = get_input_val("save_maxn",-1,true) ;
	let zip = $("#zip").prop("checked") ;
	let failed_save = $("#failed_save").prop("checked") ;
	return {topic:topic,save_name:save_name,save_maxn:save_maxn,zip:zip,failed_save:failed_save} ;
}

function set_pm_jo(jo)
{
	
	$("#topic").val(jo.topic||"") ;
	$("#save_name").val(jo.save_name||"") ;
	$("#save_maxn").val(jo.save_maxn||"") ;
	$("#zip").prop("checked",jo.zip||false) ;
	$("#failed_save").prop("checked",jo.failed_save||false) ;
}

function get_pm_size()
{
	return {w:600,h:450} ;
}

//on_init_pm_ok() ;
</script>