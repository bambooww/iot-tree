<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.ai.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.ai.*,org.iottree.ext.ai.edge.*,
	org.iottree.core.msgnet.nodes.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	if(!Convert.checkReqEmpty(request, out, "container_id","netid","itemid"))
		return ;
	
	String container_id = request.getParameter("container_id") ;
UAPrj prj = UAManager.getInstance().getPrjById(container_id) ;
if(prj==null)
{
	out.print("no project node found");
	return ;
}
MNManager mnm = MNManager.getInstance(prj) ;
String netid = request.getParameter("netid") ;
MNNet net = mnm.getNetById(netid) ;
if(net==null)
{
	out.print("no net found") ;
	return ;
}
String itemid = request.getParameter("itemid") ;
NM_AIRole node = (NM_AIRole)net.getNodeById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

%>
<style>
.tb_cameras {font-size:12px;width:100%;}
.tb_cameras td {border:1px solid ;}
.tb_cameras thead {font-weight:bold;}
.url_ppt {color:red;}
</style>

<div class="layui-form-item">
    <label class="layui-form-label">Role Type</label>
    <div class="layui-input-inline" style="width:150px;">
      <select id="ai_role_tp">
<%
for(AIRole air:AIRoleManager.ALL_ROLES)
{
%><option value="<%=air.getRoleTP()%>"><%=air.getRoleTPT() %></option><%
}
%>
      	<option></option>
      </select>
    </div>
    
    <div class="layui-form-mid">&nbsp;&nbsp;&nbsp;&nbsp;<input type="checkbox" id="all_devs" class="layui-input" value="" lay-skin="primary"/>
    All Devices 
    </div>
    <div class="layui-input-inline" style="width:100px;">
      
    </div>
    
  </div>
<div class="layui-form-item">
    <label class="layui-form-label">Related Devices</label>
    <div class="layui-input-inline" style="width:450px;">
<%
for(UADev dev: AIRoleManager.getInstance(prj).listAIDevAll())
{
	String dev_id = dev.getId() ;
	String dev_t = dev.getUseTitle() ;
%><input type="checkbox" name="related_dev" value="<%=dev_id%>"  lay-skin="primary"/><%=dev_t %>
<%
}
%>
    </div>
    
  </div>
<script>

function get_pm_jo()
{
	let ai_role_tp = $("#ai_role_tp").val();
	let all_devs = $("#all_devs").prop("checked") ;
	let dev_ids = $('input[name="related_dev"]:checked').map(function () {
	    return this.value;
	}).get();
	return {ai_role_tp:ai_role_tp,all_devs:all_devs,dev_ids:dev_ids} ;
}

function set_pm_jo(jo)
{
	$("#ai_role_tp").val(jo.ai_role_tp);
	$("#all_devs").prop("checked",jo.all_devs) ;
	let dev_ids = jo.dev_ids||[] ;
	$('input[name="related_dev"]').prop('checked', false);
	$('input[name="related_dev"]').each(function () {
	    if (dev_ids.indexOf(this.value) !== -1) {
	        $(this).prop('checked', true);
	    }
	});
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>