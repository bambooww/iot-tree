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
	org.iottree.ext.av.tts.*,
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
VibeVoiceTTS node = (VibeVoiceTTS)net.getNodeById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

String host = node.getHost() ;
int port = node.getPort() ;
float cfg = node.getCfg() ;
int steps = node.getSteps() ;
String voice = node.getVoice() ;
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
    <label class="layui-form-label">Vibe Voice Host</label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" id="host" class="layui-input" value="<%=host%>"/>
    </div>
    <div class="layui-form-mid">Port</div>
    <div class="layui-input-inline" style="width:100px;">
      <input type="number" id="port" class="layui-input" value="<%=port%>"/>
    </div>
  </div>
<div class="layui-form-item">
    <label class="layui-form-label">Speaker</label>
    <div class="layui-input-inline" style="width:350px;">
      <select id="voice">
      	<option></option>
      </select>
    </div>
    <div class="layui-form-mid"><button onclick="update_config()"><i class="fa fa-refresh"></i></button></div>
</div>
<div class="layui-form-item">
    <label class="layui-form-label">CFG</label>
    <div class="layui-input-inline" style="overflow: auto;width:100px;">
      <input type="number" id="cfg" class="layui-input"  min="1.3" max="3.0" step="0.1" value="<%=cfg%>"/>
    </div>
    <div class="layui-form-mid">Inference Steps</div>
    <div class="layui-input-inline" style="width:100px;">
      <input type="number" id="steps" class="layui-input" min="5" max="20" step="1"  value="<%=steps%>"/>
    </div>
</div>
 
<script>
var init_voice=  "<%=voice%>" ;

function on_after_pm_show(form)
{
	 
}

function update_config(init_v)
{
	let host = $("#host").val() ;
	let port = $("#port").val() ;
	let oldv = $("#voice").val()||init_v ;
	if(!host || !port)
		return ;
	let u = PM_URL_BASE+"/util/tts_vibe_voice_ajax.jsp" ;
	//console.log(u) ;
	send_ajax(u,{op:"list_voices",host:host,port:port},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("{")!=0)
		{
			dlg.msg(ret);return;
		}
		let ob = null;
		eval("ob="+ret) ;
		
		//console.log(ob) ;
		let ss = "" ;
		for(let v of ob.voices||[])
		{
			ss += `<option value="\${v}">\${v}</option>`;
		}
		//console.log(ss);
		$("#voice").html(ss) ;
		if(oldv)
			$("#voice").val(oldv) ;
		form.render();
		//console.log(oldv) ;
	});
}
//console.log(init_voice);
update_config(init_voice);

function get_pm_jo()
{
	let host = $("#host").val();
	let port = get_input_val("port",3000,true) ;
	let cfg =  $("#cfg").val();
	let steps =  $("#steps").val();
	let voice =  $("#voice").val();
	//let batch_w_buflen = get_input_val('batch_w_buflen',true,10);
	
	return {host:host,port:port,cfg:cfg,steps:steps,voice:voice} ;
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