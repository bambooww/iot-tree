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
	org.iottree.ext.ai.*,org.iottree.ext.ai.mn.*,
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
VLLM_M node = (VLLM_M)net.getModuleById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

String host = node.getHost() ;
int port = node.getPort() ;
%>
<style>
.rule
{
	position: relative;
	width:98%;
	left:1%;
	border:0px solid;
	border-color: #dddddd;
	margin-top: 5px;
}

.rule .del
{
	position: absolute;
	right:2px;
	top:2px;z-index:10;
	width:20px;
	height:20px;
	color:#dddddd;
	border-color:#dddddd;
}
.rule .del:hover
{
	background-color: red;
	
}

.row
{
	position: relative;
	width:100%;
	height:55px;
}
.row .msg
{
	position:absolute;left:30px;top:10px;
	width:140px;height:36px;
	border:0px solid #dddddd;
	vertical-align: middle;
}
.row .act
{
	position:absolute;
	left:50px;top:10px;
}
.row .mid
{
	position:absolute;
	left:207px;top:20px;
}
.row .tar_pktp
{
	position:absolute;
	left:150px;top:10px;
	width:100px
}

.row .nor_sel
{
	position:absolute;
	left:150px;top:10px;
	width:200px
}

.row .tar_subn
{
	position:absolute;
	left:230px;top:10px;
	width:260px;
}

.row .tar_pktp .layui-edge
{
	right:80px;
}
.row .tar_pktp .layui-input
{
	padding-left: 20px;
	padding-right: 20px;
	text-align: right;
	border-right: 0px;
}
.url_ppt {color:red;}
</style>

<div class="layui-form-item">
    <label class="layui-form-label">vLLM Host</label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" id="host" class="layui-input" value="<%=host%>"/>
    </div>
    <div class="layui-form-mid">Port</div>
    <div class="layui-input-inline" style="width:100px;">
      <input type="number" id="port" class="layui-input" value="<%=port%>"/>
    </div>
  </div>
  
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
	let host = $("#host").val();
	let port = get_input_val("port",8000,true) ;
	//let model_name =  $("#model_name").val();
	
	return {host:host,port:port} ;
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