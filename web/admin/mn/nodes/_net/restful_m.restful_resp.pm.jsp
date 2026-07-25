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
RESTful_Resp node = (RESTful_Resp)net.getNodeById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

RESTful_M mmm = (RESTful_M)node.getOwnRelatedModule() ;
//String url = node.getAccessPath() ;
List<String> noresp_api_ns = mmm.getNoRespApiNames();
String api_n = node.getApiName() ;
if(Convert.isNotNullEmpty(api_n))
{
	if(!noresp_api_ns.contains(api_n))
		noresp_api_ns.add(0, api_n) ;
}
%>
<style>
textarea {width:100%;height:100%;border:1px solid #ccc;}
.url_ppt {color:red;}
</style>

<div class="layui-form-item">
    <label class="layui-form-label">Api Name:</label>
    <div class="layui-input-inline" style="width:250px;">
    	<select id="api_n">
<option value=""> --- </option>
<%
	for(String n:noresp_api_ns)
	{
		%><option value="<%=n %>"> <%=n %> </option>
		<%
	}
%>
   </select>
    </div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label">Output Type:</label>
    <div class="layui-input-inline" style="width:250px;">
    	<select id="out_tp">
<%
	for(RESTful_Resp.OutTP itp:RESTful_Resp.OutTP.values())
	{
		%><option value="<%=itp.name() %>"> <%=itp.name() %> </option>
		<%
	}
%>
   </select>
    </div>
</div>

<div class="layui-form-item">
    <label class="layui-form-label">Response Sample:</label>
    <div class="layui-input-inline" style="width:550px;height:100px;">
    	<textarea id="sample" style="width:100%;height:100%;"></textarea>
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
	let out_tp = $("#out_tp").val() ;
	let sample = $("#sample").val() ;
	
	return {api_n:api_n,out_tp:out_tp,sample:sample} ;
}

function set_pm_jo(jo)
{
	$("#api_n").val(jo.api_n||"") ;
	$("#out_tp").val(jo.out_tp||"json");
	$("#sample").val(jo.sample||"") ;
	let pre = location.protocol+"//"+location.host ;
	
	$("#prefix").html(pre)
}

function get_pm_size()
{
	return {w:600,h:550} ;
}
</script>