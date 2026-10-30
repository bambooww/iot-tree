<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.driver.zigbee.*,
				org.iottree.driver.zigbee.ember.*,
				org.iottree.driver.zigbee.zcl.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
	String path = request.getParameter("path") ;
	StringBuilder failedr = new StringBuilder() ;
	DevDriver dd = ZigbeeManager.getZSDriverByPath(path, failedr) ;
	if(dd==null)
	{
		out.print(failedr.toString()) ;
		return ;
	}
	
	UACh ch = dd.getBelongToCh() ;
	EmberDriver edd = (EmberDriver)dd ;
	EmberManager emgr = edd.getManager() ;
	
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
EMgr Run=<%=emgr.RT_isRunning() %>
<table >
	<tr>
		<td>
		<button onclick="ember_send('reset','')">SYS_RESET</button><br>
	<button onclick="ember_send('version','')">SYS_VERSION</button><br>
	<input type="number" id="value_id" style="width:100px;"/><button onclick="ember_send('get_value','')">Get Value</button><br>
	<input type="number" id="conf_id" style="width:60px;"/><input type="number" id="conf_val" style="width:60px;"/><button onclick="ember_send('set_config_val','')">Set Config Val</button><br>
		</td>
		
		<td>
		<button onclick="ember_syn('version','')">EZSP_VERSION</button><br>
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
			<td>
			<button onclick="pack_ember()">Ember分析</button><br>
			<button onclick="pack_ezsp(false)">EZSP分析</button><br>
			<button onclick="pack_ezsp(true)">EZSP Legacy分析</button><br>
			<button onclick="print_jo()">Print JO</button>
			</td>
			<td style="width:65%;"><div id="output_div" style="width:99%;height:600px;overflow:auto;border:1px solid;"></div></td>
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


function pack_ember()
{
	let inputtxt = $("#input_txt").val() ;
	if(!inputtxt) return ;
	//console.log(inputtxt) ;
	send_ajax("ember.ajax.jsp",{op:"pack_ember",path:path,input_txt:inputtxt},(bsucc,ret)=>{
		//dlg.msg(ret) ;
		$("#output_div").html(ret) ;
	})
}

function pack_ezsp(b_legacy)
{
	let inputtxt = $("#input_txt").val() ;
	if(!inputtxt) return ;
	//console.log(inputtxt) ;
	send_ajax("ember.ajax.jsp",{op:"pack_ezsp",path:path,legacy:b_legacy,input_txt:inputtxt},(bsucc,ret)=>{
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
function get_val_num(id)
{
	let vstr = $("#"+id).val() ;
	return strToInt(vstr);
}

function reg_cluster_id()
{
	let cid = $("#cluster_id").val() ;
	if(!cid) {dlg.msg("input cluster id");return;}
	cid = strToInt(cid) ;
	if(isNaN(cid)) {dlg.msg("invalid cluster id");return;}
	send_req('reg_cluster_id',{cluster_id:cid})
}

function ember_send(req,pm)
{
	switch(req)
	{
	case "get_value":
		let vstr = $("#value_id").val() ;
		//console.log(vstr) ;
		if(vstr==="") {dlg.msg("no value_id input");return;}
		let vid = parseInt(vstr) ;
		if(isNaN(vid)) {dlg.msg("无效value_id"+vstr);return;}
		pm = {value_id:vid};
		break ;
	case "set_config_val":
		let conf_id = get_val_num("conf_id") ;
		let conf_val = get_val_num("conf_val") ;
		if(isNaN(conf_id) || isNaN(conf_val)) {dlg.msg("无效conf_id/conf_val");return;}
		pm = {conf_id:conf_id,conf_val:conf_val} ;
		break ;
	}
	if(!pm) pm="" ;
	else pm = JSON.stringify(pm);
	send_ajax("ember.ajax.jsp",{op:"ember_send",path:path,req:req,pm:pm},(bsucc,ret)=>{
		//dlg.msg(ret) ;
		$("#return_txt").val(ret) ;
	})
}

function ember_syn(req,pm)
{
	switch(req)
	{
	case "get_value":
		break ;
	}
	if(!pm) pm="" ;
	else pm = JSON.stringify(pm);
	send_ajax("ember.ajax.jsp",{op:"ember_syn",path:path,req:req,pm:pm},(bsucc,ret)=>{
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