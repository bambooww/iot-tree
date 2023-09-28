<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
ConnPtOPCUA.MessageMode mm = cpt.getOpcMsgMode() ;
String opc_mm = "" ;
if(mm!=null)
	opc_mm = mm.name() ;
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
<script>
dlg.resize_to(800,600);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">Title:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid">Enable:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Endpoint URL:</label>
    <div class="layui-input-inline" style="width:200px">
      <input type="text" id="opc_epu" name="opc_epu" value="<%=opc_epu%>"  style="width:200px" class="layui-input">
    </div>
    <div class="layui-input-inline" style="width:50px">
    <button class="layui-input" onclick="edit_epu()">...</button>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Security Policy:</label>
    <div class="layui-input-inline" style="width:200px">
       <select id="opc_sp" lay-filter="opc_sp">
<%
for(ConnPtOPCUA.SecurityPolicy sp:ConnPtOPCUA.SecurityPolicy.values())
{
%>
<option value="<%=sp.name()%>" ><%=sp.getTitle() %></option>
<%
}
%>   </select>
    </div>
    <div class="layui-form-mid">Message Mode:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	     <select id="opc_mm" lay-filter="opc_mm">
	     <option value="" >--</option>
<%
for(ConnPtOPCUA.MessageMode sp:ConnPtOPCUA.MessageMode.values())
{
%>
<option value="<%=sp.name()%>" ><%=sp.getTitle() %></option>
<%
}
%>   </select>
	  </div>
  </div>
   <div class="layui-form-item">
    <div class="layui-form-label">Opc Request Timeout:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    
	    <input type="text" id="opc_req_to" name="opc_req_to" value="<%=opc_reqto%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid">Update Interval</div>
	  <div class="layui-input-inline" style="width: 100px;">
	    <input type="number" id="int_ms" name="int_ms" value="<%=int_ms%>"  class="layui-input">
	  </div>
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
    <label class="layui-form-label">Opc User:</label>
    <div class="layui-input-inline">
      <input type="text" id="opc_user" name="opc_user" value="<%=opc_user%>"  class="layui-input">
    </div>
    <div class="layui-form-mid">Opc Password:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="opc_psw" name="opc_psw" value="<%=opc_psw%>"  autocomplete="off" class="layui-input">
	  </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-block">
      <textarea  id="desc"  name="desc"   placeholder="" class="layui-textarea" rows="2"><%=desc%></textarea>
    </div>
  </div>
   
 </form>
</body>
<script type="text/javascript">
var form = null;
var opc_sp = "<%=opc_sp%>" ;
var opc_mm = "<%=opc_mm%>" ;
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
	  form.on('switch(opc_sp)', function(obj){
		       setDirty();
		  });
	  form.on('switch(opc_mm)', function(obj){
		       setDirty();
		  });
	  $("#opc_sp").val(opc_sp) ;
	  $("#opc_mm").val(opc_mm) ;
	  form.render(); 
});

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
	dlg.open("opc_ua_endpoints.jsp",
			{title:"UA Server Browser",w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
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
	var opc_mm = $("#opc_mm").val() ;
	
	cb(true,{id:conn_id,name:n,title:tt,desc:desc,enable:ben,
		opc_epu:opc_epu,opc_sp:opc_sp,opc_mm:opc_mm,
		opc_req_to:opc_reqto,opc_user:opc_user,opc_psw:opc_psw,int_ms:int_ms});
}

</script>
</html>