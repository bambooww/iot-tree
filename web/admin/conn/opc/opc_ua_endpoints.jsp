<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ taglib uri="wb_tag" prefix="w"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.eclipse.milo.opcua.stack.core.types.structured.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
	return;
String repid = request.getParameter("prjid") ;
String cptp = ConnProOPCUA.TP;//request.getParameter("cptp") ;
ConnProOPCUA cp = (ConnProOPCUA)ConnManager.getInstance().getOrCreateConnProviderSingle(repid, cptp);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();
String connid = request.getParameter("connid") ;

ConnPtOPCUA cpt = null ;
if(Convert.isNullOrEmpty(connid))
{
	cpt = new ConnPtOPCUA() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (ConnPtOPCUA)cp.getConnById(connid) ;
	if(cpt==null)
	{
		out.print("no connection found") ;
		return ;
	}
}

String name = cpt.getName() ;
String title= cpt.getTitle() ;
String chked = "" ;
if(cpt.isEnable())
	chked = "checked='checked'" ;
String desc = cpt.getDesc();
//String opc_appn = cpt.getOpcAppName();
//String opc_epuri  = cpt.getOpcEndPointURI();

String host = "" ;
String port = "" ;
String opc_proto = "" ;
String opc_path = "" ;
/*
String host = cpt.getOpcHost() ;
String port  = cpt.getOpcPortStr() ;
String opc_proto = cpt.getOpcProtocal();
String opc_path = cpt.getOpcPath();
*/

int opc_reqto = cpt.getOpcReqTimeout();
String opc_user = cpt.getOpcIdUser();
String opc_psw = cpt.getOpcIdPsw();
String cp_tp = cp.getProviderType() ;
long int_ms = cpt.getUpdateIntMs() ;
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style type="text/css">
.sitem {position: relative;border:1px solid #ccc;width:98%;}
.eitem {cursor:pointer;margin-left:10px;position:relative;}
.eitem:hover {background-color: #ccc;}
.seled {background-color: #ccc;}
</style>
<script>
dlg.resize_to(700,500);
</script>
</head>
<body>
  <div class="layui-form-item">
    <label class="layui-form-label">URI:</label>
    <div class="layui-input-inline" style="width:450px;">
      <input type="text" id="uri" name="uri" value=""  lay-verify="required" autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><button class="layui-btn layui-btn-sm" onclick="on_search()">Search</button></div>
  </div>
   
   <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div id="res" class="layui-input-inline" style="width:500px;height:300px;">
    </div>
  </div>
</body>
<script type="text/javascript">
var form = null;
var cur_ep = null ;
let u = dlg.get_opener_opt("input_url") ;
if(u)
	$("#uri").val(u) ;
layui.use('form', function(){
	  form = layui.form;
	  
	  $("#name").on("input",function(e){
		  setDirty();
		  });
	  
	  form.render(); 
	  
	  if(u)
		  on_search();
});

function up_endpoints(obs)
{
	let tmps = "" ;
	for(let ob of obs)
	{
		let app_n = ob.app_n ;
		tmps += `<div class="sitem"><span>\${app_n} - \${ob.app_uri}</span>` ;
		for(let ep of ob.endpoints)
		{
			tmps += `<div class="eitem" onclick="on_sel_ep(this)" 
				ep_u="\${ep.url}" ep_sm="\${ep.sm}" ep_sp_uri="\${ep.sp_uri}" ep_sp="\${ep.sp_name}">
				\${ep.url} \${ep.sp_name} \${ep.sm_name}</div>` ;
		}
		tmps += "</div>"
	}
	$("#res").html(tmps) ;
}

function on_sel_ep(ele)
{
	let ob = $(ele) ;
	$(".eitem").removeClass("seled") ;
	ob.addClass("seled") ;
	let url = ob.attr("ep_u") ;
	let sm = parseInt(ob.attr("ep_sm")) ;
	let sp_uri = ob.attr("ep_sp_uri") ;
	let ep_sp = ob.attr("ep_sp") ;
	cur_ep = {url:url,sm:sm,sp_uri:sp_uri,sp:ep_sp};
}

function on_search()
{
	let uri = $("#uri").val();
	if(!uri)
	{
		dlg.msg("请输入URI");return;
	}
	send_ajax("opc_ua_ajax.jsp",{op:"endpts_find",uri:uri},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("[")!=0)
		{
			dlg.msg(ret);return ;
		}
		let obs = null ;
		eval("obs="+ret) ;
		console.log(obs) ;
		up_endpoints(obs)
	});
}
	
function win_close()
{
	dlg.close(0);
}

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
}

function do_submit(cb)
{
	if(!cur_ep)
	{
		cb(false,"请选择端点") ;
		return ;
	}
	cb(true,cur_ep);
}

</script>
</html>