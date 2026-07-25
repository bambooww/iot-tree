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
	org.iottree.core.msgnet.modules.*
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
RESTful_Req node = (RESTful_Req)net.getNodeById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

RESTful_M mmm = (RESTful_M)node.getOwnRelatedModule() ;
String url = node.getAccessPath() ;
%>
<style>
textarea {width:100%;height:100%;border:1px solid #ccc;}
.url_ppt {color:red;}
</style>

<div class="layui-form-item">
    <label class="layui-form-label">Api Name:</label>
    <div class="layui-input-inline" style="width:250px;">
    	<input type="text" id="api_n" class="layui-input" />
    </div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label">Input Type:</label>
    <div class="layui-input-inline" style="width:250px;">
    	<select id="in_tp">
<%
	for(RESTful_Req.InTP itp:RESTful_Req.InTP.values())
	{
		%><option value="<%=itp.name() %>"> <%=itp.name() %> </option>
		<%
	}
%>
   </select>
    </div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label">Request Sample:</label>
    <div class="layui-input-inline" style="width:550px;height:100px;">
    	<textarea id="sample" ></textarea>
    </div>
</div>
  
  <div class="layui-form-item">
    <label class="layui-form-label">Access Path:</label>
    <div class="layui-input-inline" style="width:550px;">
    	<div class="url_ppt"><br><span id="prefix"></span><%=url %></div>
    </div>
</div>
<script>
function on_after_pm_show(form)
{
	 
}
function get_pm_jo()
{
	let jo = {} ;
	let api_n = $("#api_n").val();
	let sample = $("#sample").val() ;
	return {api_n:api_n,sample:sample} ;
}

function set_pm_jo(jo)
{
	$("#api_n").val(jo.api_n||"") ;
	$("#in_tp").val(jo.in_tp||"json_object") ;
	$("#sample").val(jo.sample||"") ;
	let pre = location.protocol+"//"+location.host ;
	$("#prefix").html(pre)
}

function get_pm_size()
{
	return {w:600,h:550} ;
}
</script>