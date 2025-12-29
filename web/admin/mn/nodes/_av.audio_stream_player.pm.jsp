<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,javax.sound.sampled.*,
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
AudioStreamPlayer node = (AudioStreamPlayer)net.getNodeById(itemid) ;
if(node==null)
{
	out.print("no node found") ;
	return ;
}

float sample_rate = node.getSampleRate() ;
int sample_sizeinbit = node.getSampleSizeInBits() ;
int audio_ch = node.getAudioChannels() ;
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
    <label class="layui-form-label">Audio Format</label>
    <div class="layui-input-inline" style="width:350px;">
      <select id="sample_rate">
      	
<option value="8000">8000</option>
<option value="11025">11025</option>
<option value="22050">22050</option>
<option value="24000">24000</option>
<option value="44100">44100</option>
<option value="48000">48000</option>
<option value="88200">88200</option>
<option value="96000">96000</option>
<option value="176400">176400</option>
<option value="192000">192000</option>

      </select>
    </div>
</div>
<div class="layui-form-item">
    <label class="layui-form-label">Sample Size In Bit</label>
    <div class="layui-input-inline" style="width:100px;">
    <select id="sample_sizeinbit">
      	
<option value="8">8</option>
<option value="16">16</option>
<option value="24">24</option>
<option value="32">32</option>

      </select>
      
    </div>
    <div class="layui-form-mid">Audio Channels</div>
    <div class="layui-input-inline" style="width:100px;">
      <input type="number" id="audio_ch" class="layui-input" min="1" max="10" step="1"  />
    </div>
</div>
 
<script>

function on_after_pm_show(form)
{
	 
}


function get_pm_jo()
{
	let sample_rate = get_input_val("sample_rate",24000,true) ;
	let sample_sizeinbit = get_input_val("sample_sizeinbit",16,true) ;
	let audio_ch = get_input_val("audio_ch",1,true) ;
	
	return {sample_rate:sample_rate,sample_sizeinbit:sample_sizeinbit,audio_ch:audio_ch} ;
}

function set_pm_jo(jo)
{
	$("#sample_rate").val(jo.sample_rate||24000) ;
	$("#sample_sizeinbit").val(jo.sample_sizeinbit||16) ;
	$("#audio_ch").val(jo.audio_ch||1) ;
}

function get_pm_size()
{
	return {w:600,h:550} ;
}

//on_init_pm_ok() ;
</script>