<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
	String path = request.getParameter("path") ;
	UANode node = UAUtil.findNodeByPath(path) ;
	if(node==null)
	{
		out.print("no node found") ;
		return ;
	}
	
	DevDriver dd = null ;
	if(node instanceof UACh)
	{
		UACh ch  = (UACh)node ;
		dd = ch.getDriver() ;
	}
	else if(node instanceof UADev)
	{
		UADev dev = (UADev)node ;
		dd = dev.getRelatedDrv() ;
	}
	
	if(dd==null||!dd.supportDriverUrl())
	{
		out.print("no driver or no driver url found") ;
		return ;
	}
	
	
%><html>
<head>
<title>ember driver</title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style type="text/css">
table {width:100%;}
td {border:1px solid #ccc;}
</style>
<script>
dlg.resize_to(650,500);
</script>
</head>
<body>
<table >
	<tr>
		<td>
		
		<button onclick="send_req('ping','')">SYS_PING</button><br>
	<button onclick="send_req('version','')">SYS_VERSION</button><br>
	<button onclick="send_req('nv_length','')">SYS_OSAL_NV_LENGTH 0x82</button><br>
	<button onclick="send_req('mgmt_lqi','')">MgmtLQISREQ</button><br>
	Register ClusterId<input type="text" id="cluster_id" /> <button onclick="reg_cluster_id()">reg cluster id</button><br>
	<button onclick="send_req('get_device_info','')">UTIL_GET_DEVICE_INFO</button><br>
	<button onclick="send_req('startup_from_app','')">ZDO_STARTUP_FROM_APP</button><br>
	
		</td>
		
		<td>
		<button onclick="zstack_init()" style="background-color:#aaa;">zstack_init</button><br>
		NVItemId<input type="text" id="nv_item_id" style="width:100px;"/>
		<button onclick="req_rsp_nv_read()">nv_read</button><br>
		<button onclick="req_rsp('nwk_key','')">getnwk key</button><br>
		<button onclick="req_rsp('get_len_nv','')">get_len_nv</button><br>
		<button onclick="req_rsp('read_ext_pan_id','')">read_ext_pan_id</button><br>
		<button onclick="req_rsp('read_pan_id','')">read_pan_id</button><br>
		<button onclick="req_rsp('read_ieee_addr','')">read_ieee_addr</button><br>
		</td>
		
		
		<td>
		
		<a href="javascript:open_cache()" >show cache</a><br>
		<button onclick="get_coor_info()">coor_info</button><br>
		</td>
	</tr>
</table>
	

	<textarea style="width:90%;height:100px" id="return_txt"></textarea>
	
	<table style="width:100%;">
		<tr>
			<td style="width:30%;"><textarea style="width:99%;height:600px" id="input_txt"></textarea></td>
			<td><button onclick="pack_ana()">分析</button><br>
			<button onclick="print_jo()">Print JO</button>
			</td>
			<td style="width:65%;"><div id="output_div" style="width:99%;height:600px;overflow-y:auto;border:1px solid;"></div></td>
		</tr>
	</table>
	
</body>
<script type="text/javascript">

var path="<%=path%>";
var form = null;
layui.use('form', function(){
	  form = layui.form;
	  form.render();
	  
});

function open_cache()
{
	window.open("./drv_ins.cache.jsp?path="+path) ;
}

function pack_ana()
{
	let inputtxt = $("#input_txt").val() ;
	if(!inputtxt) return ;
	//console.log(inputtxt) ;
	send_ajax("drv_ins.ajax.jsp",{op:"pack_ana",path:path,input_txt:inputtxt},(bsucc,ret)=>{
		//dlg.msg(ret) ;
		$("#output_div").html(ret) ;
	})
}

function print_jo()
{
	let inputtxt = $("#input_txt").val() ;
	if(!inputtxt) return ;
	//console.log(inputtxt) ;
	send_ajax("drv_ins.ajax.jsp",{op:"print_jo",path:path,input_txt:inputtxt},(bsucc,ret)=>{
		//dlg.msg(ret) ;
		$("#output_div").html("<pre>"+ret+"</pre>") ;
	})
}

function strToInt(str) {
	  const s = String(str).trim();
	  return parseInt(s, /^[+-]?0[xX]/.test(s) ? 16 : 10);
	}

function reg_cluster_id()
{
	let cid = $("#cluster_id").val() ;
	if(!cid) {dlg.msg("input cluster id");return;}
	cid = strToInt(cid) ;
	if(isNaN(cid)) {dlg.msg("invalid cluster id");return;}
	send_req('reg_cluster_id',{cluster_id:cid})
}

function send_req(req,pm)
{
	if(!pm) pm="" ;
	else pm = JSON.stringify(pm);
	send_ajax("drv_ins.ajax.jsp",{op:"req",path:path,req:req,pm:pm},(bsucc,ret)=>{
		//dlg.msg(ret) ;
		$("#return_txt").val(ret) ;
	})
}

function req_rsp(req,pm)
{
	if(!pm) pm="" ;
	else pm = JSON.stringify(pm);
	send_ajax("drv_ins.ajax.jsp",{op:"req_rsp",path:path,req:req,pm:pm},(bsucc,ret)=>{
		//dlg.msg(ret) ;
		$("#return_txt").val(ret) ;
	})
}

function get_coor_info()
{
	send_ajax("drv_ins.ajax.jsp",{op:"coor_info",path:path},(bsucc,ret)=>{
		$("#output_div").html("<pre>"+ret+"</pre>") ;
	})
}

function zstack_init()
{
	send_ajax("drv_ins.ajax.jsp",{op:"zstack_init",path:path},(bsucc,ret)=>{
		//dlg.msg(ret) ;
		$("#return_txt").val(ret) ;
	})
}

function req_rsp_nv_read()
{
	let nv_item_id = $("#nv_item_id").val() ;
	if(!nv_item_id) {dlg.msg("no nv item id input");return ;}
	nv_item_id = toNumber(nv_item_id) ;
	if(isNaN(nv_item_id))
	{
		dlg.msg("invalid nv item id");return ;
	}
	pm = {nv_item_id:nv_item_id};
	send_ajax("drv_ins.ajax.jsp",{op:"req_rsp",path:path,req:"nv_read",pm:JSON.stringify(pm)},(bsucc,ret)=>{
		console.log(ret) ;
		$("#return_txt").html(ret) ;
	})
}

function toNumber(str) {
	  if (typeof str !== 'string') return Number(str);

	  const s = str.trim();
	  if (s === '') return NaN;

	  // 支持：0x10、0XFF、+0x10、-0x10
	  if (/^[+-]?0x[0-9a-f]+$/i.test(s)) {
	    const sign = s.startsWith('-') ? -1 : 1;
	    const hex = s.replace(/^[+-]?0x/i, '');
	    return sign * parseInt(hex, 16);
	  }

	  // 其他情况交给 Number，支持十进制、浮点数、科学计数法等
	  return Number(s);
	}
</script>
</html>