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
.sopt {border:0px solid;height:25px;}
.sopt input {margin-left:120px;margin-top:6px;}
.sitem {position: relative;border:1px solid #ccc;width:98%;overflow:hidden;}
.sitem .hd {font-weight:bold;white-space: nowrap;}
.eitem {cursor:pointer;margin-left:10px;position:relative;border:1px solid;border-color:green;border-radius: 3px;margin-top:2px;}
.eitem:hover {background-color: #ccc;}
.seled {background-color: #ccc;}
</style>
<script>
dlg.resize_to(700,550);
</script>
</head>
<body>
	<div class="sopt">
    <input type="radio" id="discovery" name="search_tp" value="dis" checked="checked" onclick="chg_search_tp()"/> Discovery
      <input type="radio" id="manual" name="search_tp" value="man" onclick="chg_search_tp()"/> Manual
  </div>
  
  <div class="layui-form-item" id="cont_dis">
    <label class="layui-form-label">Host/IP:</label>
    <div class="layui-input-inline" style="width:350px;">
      <input type="text" id="host" name="host" value="localhost"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Port:</div>
    <div class="layui-input-inline" style="width:100px;">
      <input type="number" id="port" name="port" value="4840"  class="layui-input">
    </div>
    <div class="layui-input-inline" style="width:40px;"><button class="layui-btn" onclick="on_search_dis()"><i class="fa fa-search"></i></button></div>
  </div>
  
  <div class="layui-form-item" id="cont_man" style="display:none;">
    <label class="layui-form-label">URI:</label>
    <div class="layui-input-inline" style="width:500px;">
      <input type="text" id="uri" name="uri" value=""  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-input-inline" style="width:40px;"><button class="layui-btn" onclick="on_search_man()"><i class="fa fa-search"></i></button></div>
  </div>
   
   <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div id="res" class="layui-input-inline" style="width:550px;height:350px;overflow-y: auto;">
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
	  {
		  $('input[name="search_tp"][value="man"]').prop('checked', true);
		  chg_search_tp();
		  search_by_uri(u);
	  }
	  else
	  {
		  search_by_uri("opc.tcp://localhost:4840") ;
	  }
});

function chg_search_tp()
{
	let stp = $('input[name="search_tp"]:checked').val();
	if(stp=='man')
	{
		$("#cont_man").css("display","");
		$("#cont_dis").css("display","none");
	}
	else
	{
		$("#cont_man").css("display","none");
		$("#cont_dis").css("display","");
	}
}

function up_endpoints(obs)
{
	let tmps = "" ;
	for(let ob of obs)
	{
		let app_n = ob.app_n ;
		tmps += `<div class="sitem"><span class="hd">\${app_n} - \${ob.app_uri}</span>` ;
		for(let ep of ob.endpoints)
		{
			let ico = ep.sm_none?"<i class='fa fa-lock-open' style='color:#ee3124'></i>":"<i class='fa fa-lock' style='color:#1272b2'></i>";
			tmps += `<div class="eitem" onclick="on_sel_ep(this)" 
				ep_u="\${ep.url}" ep_sm="\${ep.sm}" ep_sp_uri="\${ep.sp_uri}" ep_sp="\${ep.sp_name}">
				\${ico} \${ep.url} \${ep.sp_name} \${ep.sm_name}</div>` ;
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

function on_search_dis()
{
	let host = $("#host").val();
	let port = parseInt($("#port").val());
	if(!host)
	{
		dlg.msg("<w:g>pls,input</w:g> Host");return;
	}
	if(isNaN(port) || port<=0)
	{
		dlg.msg("<w:g>pls,input,valid</w:g> Port");return;
	}
	let uri = `opc.tcp://\${host}:\${port}`;
	search_by_uri(uri);
}

function on_search_man()
{
	let uri = $("#uri").val();
	if(!uri)
	{
		dlg.msg("<w:g>pls,input</w:g>URI");return;
	}
	search_by_uri(uri);
}

function search_by_uri(uri)
{
	dlg.loading(true) ;
	send_ajax("opc_ua_ajax.jsp",{op:"endpts_find",uri:uri},(bsucc,ret)=>{
		dlg.loading(false) ;
		if(!bsucc || ret.indexOf("[")!=0)
		{
			//dlg.msg("<span style='color:red'><w:g>search,failed</w:g></span>:"+ret);
			$("#res").html("<span style='color:red'><w:g>search,failed</w:g></span>:"+ret) ;
			return ;
		}
		let obs = null ;
		eval("obs="+ret) ;
		//console.log(obs) ;
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