<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ taglib uri="wb_tag" prefix="w"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.eclipse.milo.opcua.stack.core.types.structured.*,
				org.eclipse.milo.opcua.stack.core.security.*,
				org.eclipse.milo.opcua.stack.core.types.enumerated.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
	return;
String prjid = request.getParameter("prjid") ;
String cptp = ConnProOPCUA.TP;//request.getParameter("cptp") ;
ConnProOPCUA cp = (ConnProOPCUA)ConnManager.getInstance().getOrCreateConnProviderSingle(prjid, cptp);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
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
//String host = cpt.getOpcHost() ;
//String port  = cpt.getOpcPortStr() ;
//String opc_proto = cpt.getOpcProtocal();
//String opc_path = cpt.getOpcPath();
String opc_epu =  cpt.getOpcEndPointURI() ;
String opc_sp = cpt.getOpcSP().name();
String opc_auth_tp = cpt.getOpcAuthTP().name();
MessageSecurityMode sm = cpt.getOpcSM() ;
int opc_sec_m = 1 ;
if(sm!=null)
	opc_sec_m = sm.getValue() ;
int opc_reqto = cpt.getOpcReqTimeout();
String opc_user = cpt.getOpcIdUser();
String opc_psw = cpt.getOpcIdPsw();
String cp_tp = cp.getProviderType() ;
long int_ms = cpt.getUpdateIntMs() ;
%>
<html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<style type="text/css">

.layui-form-label {width: 120px;}
</style>
<script>
dlg.resize_to(800,600);
</script>
</head>
<body>
<form class="layui-form"  onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label"><w:g>name</w:g>:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid"><w:g>title</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid"><w:g>enable</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label" title="Endpoint URL"><w:g>endpoint</w:g> URL:</label>
    <div class="layui-input-inline" style="width:500px">
      <input type="text" id="opc_epu" name="opc_epu" value="<%=opc_epu%>"  style="width:500px" class="layui-input">
    </div>
    <div class="layui-input-inline" style="width:50px">
    <button class="layui-input" onclick="edit_epu()">...</button>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label" title="Security Mode"><w:g>security,mode</w:g>:</label>
    <div class="layui-input-inline" style="width:200px">
    <select id="opc_sec_m" lay-filter="opc_sec_m">
<%
	for(MessageSecurityMode sp:MessageSecurityMode.values())
{
%>
<option value="<%=sp.getValue()%>" > <w:g><%=sp.name().toLowerCase()%></w:g></option>
<%
}
%>   </select>

    </div>
    <div class="layui-form-mid" title="Security Policy"><w:g>security,policy</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	     
       <select id="opc_sp" lay-filter="opc_sp">
<%
for(SecurityPolicy sp:SecurityPolicy.values())
{
%>
<option value="<%=sp.name()%>" ><%=sp.name() %></option>
<%
}
%>   </select>
	  </div>
  </div>
   <div class="layui-form-item">
    <div class="layui-form-label" title="OPC Client Request Timeout"><w:g>request,timeout</w:g>:</div>
	  <div class="layui-input-inline" style="width: 100px;">
	    
	    <input type="number" id="opc_req_to" name="opc_req_to" value="<%=opc_reqto%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid">(<w:g>ms</w:g>) &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<w:g>update,intv</w:g></div>
	  <div class="layui-input-inline" style="width: 100px;">
	    <input type="number" id="int_ms" name="int_ms" value="<%=int_ms%>"  class="layui-input">
	  </div>
	  <div class="layui-form-mid">(<w:g>ms</w:g>) </div>
  </div>
  <%--
  <div class="layui-form-item">
    <label class="layui-form-label">Opc App Name:</label>
    <div class="layui-input-inline">
      <input type="text" id="host" name="host" value="<%=opc_appn%>"  class="layui-input">
    </div>
    
  </div>
  
  <div class="layui-form-item">
    <label class="layui-form-label">Opc URI:</label>
    <div class="layui-input-inline">
      <input type="text" id="opc_endpoint_uri" name="opc_endpoint_uri" value="<%=opc_epuri%>"  lay-verify="required" autocomplete="off" class="layui-input">
    </div>
<div class="layui-form-mid">Opt Request Timeout:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    
	    <input type="text" id="opc_req_to" name="opc_req_to" value="<%=opc_reqto%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	 
  </div>
  
   --%>

  
  <div class="layui-form-item">
  	<label class="layui-form-label" title=""><w:g>auth,type</w:g>:</label>
    <div class="layui-input-inline" style="width:140px;">
    	<select id="auth_tp" lay-filter="auth_tp">
<%
	for(ConnPtOPCUA.AuthTP authtp:ConnPtOPCUA.AuthTP.values())
	{
%><option value="<%=authtp.name()%>"><w:g><%=authtp.name()%></w:g></option><%
	}
%>
    	</select>
    </div>
    <div id="user_psw_c" style="display:none">
    <label class="layui-form-mid" title="OPC UserName"><w:g>user</w:g>:</label>
    <div class="layui-input-inline" style="width:160px;">
      <input type="text" id="opc_user" name="opc_user" value="<%=opc_user%>"  class="layui-input">
    </div>
    <div class="layui-form-mid" title=""><w:g>psw</w:g>:</div>
	  <div class="layui-input-inline" style="width:160px;">
	    <input type="text" id="opc_psw" name="opc_psw" value="<%=opc_psw%>"  autocomplete="off" class="layui-input">
	  </div>
	  </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label"><w:g>description</w:g>:</label>
    <div class="layui-input-inline" style="width:550px;">
      <textarea  id="desc"  name="desc"   placeholder="" class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>
   
 </form>
</body>
<script type="text/javascript">
var prjid = "<%=prjid%>" ;
var form = null;
var opc_sp = "<%=opc_sp%>" ;
var opc_sec_m = <%=opc_sec_m%> ;
var opc_auth_tp = "<%=opc_auth_tp%>" ;

layui.use('form', function(){
	  form = layui.form;
	  
	  $("#name").on("input",function(e){
		  setDirty();
		  });
	  $("#title").on("input",function(e){
		  setDirty();
		  });
	  $("#desc").on("input",function(e){
		  setDirty();
		  });
	  $("#opc_epu").on("input",function(e){
		  setDirty();
		  });
	  $("#opc_req_to").on("input",function(e){
		  setDirty();
		  });
	  $("#opc_user").on("input",function(e){
		  setDirty();
		  });
	  $("#opc_psw").on("input",function(e){
		  setDirty();
		  });
	  
	  
	  form.on('switch(enable)', function(obj){
		       setDirty();
		  });
	  form.on('select(opc_sp)', function(obj){
		       setDirty();
		  });
	  form.on('select(opc_sec_m)', function(obj){
		       setDirty();
		  });
	  form.on('select(auth_tp)', function(obj){
		       setDirty();
		  update_by_auth_tp();
		  });
	  
	  $("#opc_sp").val(opc_sp) ;
	  $("#opc_sec_m").val(opc_sec_m) ;
	  $("#auth_tp").val(opc_auth_tp) ;
	  update_by_auth_tp();
	  form.render(); 
});

function update_by_auth_tp()
{
	let bshow = $("#auth_tp").val()!='anony';
	$("#user_psw_c").css("display",bshow?"":"none") ;
}

var _tmpid = 0 ;

var bdirty=false;
var cp_id = "<%=cpid%>" ;
var cp_tp = "<%=cp_tp%>" ;
var conn_id = "<%=connid%>" ;

function isDirty()
{
	return bdirty;
}
function setDirty(b)
{
	if(!(b===false))
		b = true ;
	bdirty= b;
	dlg.btn_set_enable(1,b);
}

function edit_epu()
{
	event.preventDefault();
	let u = $("#opc_epu").val()||"" ;
	dlg.open("./opc/opc_ua_endpoints.jsp?prjid="+prjid,
			{title:"UA Server Browser",w:'500px',h:'400px',input_url:u},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 //console.log(ret) ;
						 $("#opc_epu").val(ret.url);
						 $("#opc_sec_m").val(ret.sm);
						 $("#opc_sp").val(ret.sp);
						 form.render(); 
						 dlg.close();
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
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
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'Please input name') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'Please input title') ;
		return ;
	}
	var ben = $("#enable").prop("checked") ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var opc_epu = $('#opc_epu').val();
	if(opc_epu==null||opc_epu=='')
	{
		cb(false,'Please input Endpoint URI') ;
		return ;
	}
	
	var opc_reqto = $('#opc_req_to').val();
	if(opc_reqto==null||opc_reqto=='')
	{
		cb(false,'Please input Opc Request timeout') ;
		return ;
	}
	opc_reqto = parseInt(opc_reqto);
	if(opc_reqto==NaN||opc_reqto<0)
	{
		cb(false,'Please input valid Opc Request timeout') ;
	}
	
	let auth_tp = $("#auth_tp").val();
	
	var opc_user = $('#opc_user').val();
	if(opc_user==null||opc_user=='')
	{
		//cb(false,'Please input Opc Id User') ;
		opc_user="";
		//return ;
	}
	
	var opc_psw = $('#opc_psw').val();
	if(opc_psw==null||opc_psw=='')
	{
		//cb(false,'Please input Opc Id password') ;
		//return ;
		opc_psw="";
	}
	
	var int_ms = $('#int_ms').val();
	if(int_ms==null||int_ms=='')
	{
		cb(false,'Please input Update interval') ;
		return ;
	}
	int_ms = parseInt(int_ms);
	if(int_ms==NaN||int_ms<0)
	{
		cb(false,'Please input valid Update interval') ;
	}
	
	var opc_sp = $("#opc_sp").val() ;
	var opc_sec_m = parseInt($("#opc_sec_m").val()) ;
	
	cb(true,{id:conn_id,name:n,title:tt,desc:desc,enable:ben,
		opc_epu:opc_epu,opc_sp:opc_sp,opc_sec_m:opc_sec_m,
		opc_req_to:opc_reqto,auth_tp:auth_tp,opc_user:opc_user,opc_psw:opc_psw,int_ms:int_ms});
}

</script>
</html>