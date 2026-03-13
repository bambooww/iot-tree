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
	org.iottree.ext.av.*,
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
CameraCap_NM node = (CameraCap_NM)net.getNodeById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

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
  <label class="layui-form-label">Camera</label>
  <div class="layui-input-inline" style="width:550px;">
    <select id="camera_id" lay-filter="camera_id">
    	<option value=""> -- </option>
<%

	for(CameraCap_NM.CamItem cam:CameraCap_NM.listCameras())
	{
%><option value="<%=cam.index%>"><%=cam.getShowTitle() %></option><%
	}

%>
    </select>
  </div>
  
</div>

<div class="layui-form-item">
  <label class="layui-form-label"></label>
  <div class="layui-form-mid">Frame Size:</div>
  <div class="layui-input-inline" style="width:150px;">
      <input type="number" id="pm_w" class="layui-input" value=""/>
    </div>
    <div class="layui-form-mid">X</div>
    <div class="layui-input-inline" style="width:150px;">
      <input type="number" id="pm_h" class="layui-input" value=""/>
    </div>
</div>
<%--
<div class="layui-form-item">
  <label class="layui-form-label"></label>
  <div class="layui-input-inline" style="width:550px;">
    <select id="cam_pm" >
    	<option value=""> -- </option>
<%

	for(CameraCap_NM.CamItem cam:CameraCap_NM.listCameras())
	{
%><option value="<%=cam.index%>"><%=cam.getShowTitle() %></option><%
	}

%>
    </select>
  </div>
</div>--%>

<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-form-mid">Auto cancel after idle seconds</div>
    <div class="layui-input-inline" style="width:150px;">
      <input type="number" id="auto_cancel_after_sec" class="layui-input" value=""/>
    </div>
    <div class="layui-form-mid">Transfer to gray</div>
    <div class="layui-input-inline" style="width:150px;">
      <input type="checkbox" id="trans_gray" class="layui-input" />
    </div>
    
</div>

<script>

function on_after_pm_show(form)
{
	form.on('select(camera_id)', function(data){   
		on_cam_chg();
	 });
}

function get_pm_jo()
{
	let camera_id = $("#camera_id").val();
	let auto_cancel_after_sec = get_input_val("auto_cancel_after_sec",-1,true)
	let pm_w = get_input_val("pm_w",-1,true) ;
	let pm_h = get_input_val("pm_h",-1,true) ;
	let trans_gray = $("#trans_gray").prop("checked") ;
	return {camera_id:camera_id,auto_cancel_after_sec:auto_cancel_after_sec,pm_w:pm_w,pm_h:pm_h,trans_gray:trans_gray} ;
}

function set_pm_jo(jo)
{
	let camid = jo["camera_id"];
	if(camid==null||camid==undefined||camid<0)
		camid="" ;
	$("#camera_id").val(camid) ;
	$("#pm_w").val(jo.pm_w||-1);
	$("#pm_h").val(jo.pm_h||-1);
	$("#auto_cancel_after_sec").val(jo.auto_cancel_after_sec||-1) ;
	$("#trans_gray").prop("checked",jo.trans_gray||false) ; 
}

function on_cam_chg()
{
	let idx = $("#camera_id").val();
	if(idx<0) return ;
	dlg.loading(true) ;
	send_ajax("./nodes/util/camera_cap_ajax.jsp",{op:"cam_pm",idx:idx},(bsucc,ret)=>{
		dlg.loading(false) ;
		console.log(ret) ;
		dlg.msg(ret) ;
	})
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>