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
	org.iottree.ext.ai.*,org.iottree.ext.ai.mn.*,org.iottree.ext.ai.dev.*,
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
DevCtrl_M node = (DevCtrl_M)net.getModuleById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

DevCtrl_M.Role role = node.getRole() ;
String sys_msg = node.getSystemMsg();
%>
<style>

.url_ppt {color:red;}
.msg
{
	height:85px;width:100%;
}
</style>

<div class="layui-form-item">
    <label class="layui-form-label">Role</label>
    <div class="layui-input-inline" style="width:150px;">
    	<select id="role">
<%
for(DevCtrl_M.Role r:DevCtrl_M.Role.values())
{
	String sel = (role==r)?"selected":"";
%><option value="<%=r.name()%>" <%=sel %>><%=r.getTitle() %></option>
<%
}
%>
		</select>
    </div>
<%--
    <div class="layui-form-mid">Port</div>
    <div class="layui-input-inline" style="width:100px;">
      <input type="number" id="ollama_port" class="layui-input" value="<%=""%>"/>
    </div>
     --%>
  </div>
<%--
  <div class="layui-form-item">
    <label class="layui-form-label">system message:</label>
    <div class="layui-input-inline" style="width:600px;">
      <textarea id="sys_msg" class="msg" style="height:120px;"><%=sys_msg %></textarea>
    </div>
    <div class="layui-form-mid"><button class="layui-btn layui-btn-sm layui-btn-primary">...</button></div>
</div>
   --%>
<%--
<div class="layui-form-item">
    <label class="layui-form-label">LLM Model</label>
    <div class="layui-input-inline" style="width:450px;">
      <select id="model_name">
      	<option></option>
      </select>
    </div>
    <div class="layui-form-mid"><button onclick="update_models()"><i class="fa fa-refresh"></i></button></div>
</div>
--%>

<script>

function on_after_pm_show(form)
{
	 
}

function get_pm_jo()
{
	let role = $("#role").val();
	
	return {role:role} ;
}

function set_pm_jo(jo)
{
	
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>