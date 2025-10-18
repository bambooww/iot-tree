<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.service.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.driver.opc.opcua.server.*,
				java.security.cert.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
OpcUAService ser = (OpcUAService)ServiceManager.getInstance().getService(OpcUAService.NAME);
	HashMap<String,String> pms = ser.getConfPMS() ;
	boolean enable = ser.isEnable();//ser.isMqttEn();
	//boolean tcp_en = false;//ser.isTcpEn();
	String port =  ser.getTcpPortStr();//.getMqttPortStr();
	
	String chked_en = "" ;
	if(enable)
		chked_en = "checked=checked";
	//if(tcp_en)
	//	chked_tcp_en = "checked=checked";
	
	String user = "";// ser.getAuthUser() ;
	String psw =  "";// ser.getAuthPsw() ;
	String users = ser.getAuthUsers();
	
	String chked_sm_none = ser.isSecModeNone()?"checked":"" ;
	String chked_sm_sign = ser.isSecModeSign()?"checked":"" ;
	String chked_sm_sign_enc = ser.isSecModeSignEncrypt()?"checked":"" ;
	
	List<KeyStoreLoader.CertItem> trusted_certs = KeyStoreLoader.listTrustedCers() ;
	List<KeyStoreLoader.CertItem> rejected_certs = KeyStoreLoader.listRejectedCers() ;
%>
<html>
<head>
<title>editor</title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(700,720);
</script>
<style>
.cert {position: relative;width:99%;height:50px;border:1px solid;border-radius: 3px;}
.cert .subject {position: absolute;left:3px;top:1px;font-size:12px;font-weight: bold;}
.cert .issuer {position: absolute;left:7px;top:18px;font-size:12px;}
.cert .serial {position: absolute;right:23px;top:3px;font-size:12px;}
.cert .dt {position: absolute;left:7px;top:35px;font-size:12px;}
.cert .op {position: absolute;right:3px;top:25px;font-size:12px;}
.rejected {border-color: red;}
.trusted {border-color: green;}
</style>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>enable</wbt:g>:</label>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
	  <div class="layui-form-mid"><wbt:g>port</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="number" id="port" name="port" value="<%=port%>"  class="layui-input">
	  </div>
  </div>
  <%--

  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>user</wbt:g>:</label>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="user" name="user"  class="layui-input" value="<%=user%>">
	  </div>
	  <div class="layui-form-mid"><wbt:g>psw</wbt:g>:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	    <input type="text" id="psw" name="psw" value="<%=psw%>"  class="layui-input">
	  </div>
  </div>
   --%>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label"><wbt:g>users</wbt:g></label>
    <div class="layui-input-inline" style="width:200px">
      <textarea name="users" id="users" class="layui-textarea"><%=users %></textarea>
    </div>
    <div class="layui-form-mid"><wbt:g>prj</wbt:g></div>
    <div class="layui-input-inline" style="width:300px;height:100px;" >
    	<div id="prj_list" style="overflow-y:auto;width:100%;height:100%;border:1px solid #ccc;">
<%
List<UAPrj> prjs = UAManager.getInstance().listPrjs() ;
for(UAPrj prj:prjs)
{
	String chked = ser.hasPrjId(prj.getId())?"checked":"" ;
			
%><div class="prj"><input type="checkbox" class="chk_prj" id="prj_<%=prj.getId() %>" value="<%=prj.getId() %>" <%=chked %> lay-ignore/><%=prj.getTitle() %></div><%
}
%>
    	</div>
      
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>security,mode</wbt:g>:</label>
	  <div class="layui-input-inline" style="width: 350px;">
	    <input type="checkbox" id="sm_none" name="sm_none" <%=chked_sm_none%> lay-skin="primary"  lay-filter="sm_none" class="layui-input"> None&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	    <input type="checkbox" id="sm_sign" name="sm_sign" <%=chked_sm_sign%>  lay-skin="primary"   lay-filter="sm_sign" class="layui-input"> Sign&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	    <input type="checkbox" id="sm_sign_enc" name="sm_sign_enc" <%=chked_sm_sign_enc%>  lay-skin="primary"   lay-filter="sm_sign_enc" class="layui-input"> Sign &amp; Encrypt
	  </div>
	  
  </div>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label"><wbt:g>trusted,cer</wbt:g></label>
    <div class="layui-input-inline" style="width:450px;height:200px;border:1px solid #ccc;overflow-y:auto;" id="trusted_list">
<%--
for(KeyStoreLoader.CertItem certi:trusted_certs)
{
%>
<div class="cert trusted">
	<div class="subject">主题<%=certi.cert.getSubjectX500Principal() %></div>
	<div class="issuer">颁发者<%=certi.cert.getIssuerX500Principal() %></div>
	<div class="serial">序列号 <%=certi.cert.getSerialNumber().toString(16) %></div>
	<div class="dt"><%= Convert.toFullYMDHMS(certi.cert.getNotBefore()) %> - <%= Convert.toFullYMDHMS(certi.cert.getNotAfter())%></div>
	<div class="op">
		<button class="layui-btn layui-btn-xs layui-btn-danger" title="reject"><i class="fa fa-arrow-down"></i></button>
		<button class="layui-btn layui-btn-xs layui-btn-danger" title="delete"><i class="fa fa-times"></i></button>
	</div>
</div>
<%
}
--%>
    </div>
  </div>
<%--
    <div class="layui-input-inline" style="width:450px;border:0px solid #ccc;text-align: center;">
      <button class="layui-btn layui-btn-xs"><wbt:g>set,trusted</wbt:g></button><button  class="layui-btn layui-btn-xs layui-btn-danger"><wbt:g>set,rejected</wbt:g></button>
    </div>
 --%>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label"><wbt:g>rejected,cer</wbt:g></label>
    <div class="layui-input-inline" style="width:450px;height:130px;border:1px solid #ccc;overflow-y:auto;"  id="rejected_list">
<%--
for(KeyStoreLoader.CertItem certi:rejected_certs)
{
	String filen = certi.file.toFile().getName() ;
%>
<div class="cert trusted">
	<div class="subject">主题<%=certi.cert.getSubjectX500Principal() %></div>
	<div class="issuer">颁发者<%=certi.cert.getIssuerX500Principal() %></div>
	<div class="serial">序列号 <%=certi.cert.getSerialNumber().toString(16) %></div>
	<div class="dt"><%= Convert.toFullYMDHMS(certi.cert.getNotBefore()) %> - <%= Convert.toFullYMDHMS(certi.cert.getNotAfter())%></div>
	<div class="op"><button class="layui-btn layui-btn-xs" title="trust it" onclick="trust_cert('<%=filen%>')"><i class="fa fa-arrow-up"></i></button></div>
</div>
<%
}
--%>
    </div>
  </div>
</form>
</body>
<script type="text/javascript">

layui.use('form', function(){
	  var form = layui.form;
	  form.render();
	});
	
var cert_jo = null ;
	
function win_close()
{
	dlg.close(0);
}

function update_ui()
{
	let tmps="" ;
	if(cert_jo&&cert_jo.trusted)
	{
		for(let cert of cert_jo.trusted)
		{
			tmps += `<div class="cert trusted">
				<div class="subject">\${cert.subject}</div>
				<div class="issuer">\${cert.issuer}</div>
				<div class="serial">No: \${cert.serial}</div>
				<div class="dt">\${new Date(cert.st).format_local('yyyy-MM-dd hh:mm:ss')} - \${new Date(cert.et).format_local('yyyy-MM-dd hh:mm:ss')}</div>
				<div class="op">
				<button class="layui-btn layui-btn-xs layui-btn-danger" title="reject" onclick="reject_cert('\${cert.fn}')"><i class="fa fa-arrow-down"></i></button>
				<button class="layui-btn layui-btn-xs layui-btn-danger" title="delete" onclick="del_cert(true,'\${cert.fn}')"><i class="fa fa-times"></i></button>
			</div>
			</div>`
		}
		
	}
	
	$("#trusted_list").html(tmps) ;
	
	tmps="" ;
	if(cert_jo&&cert_jo.rejected)
	{
		for(let cert of cert_jo.rejected)
		{
			tmps += `<div class="cert rejected">
				<div class="subject">\${cert.subject}</div>
				<div class="issuer">\${cert.issuer}</div>
				<div class="serial">No: \${cert.serial}</div>
				<div class="dt">\${new Date(cert.st).format_local('yyyy-MM-dd hh:mm:ss')} - \${new Date(cert.et).format_local('yyyy-MM-dd hh:mm:ss')}</div>
				<div class="op">
					<button class="layui-btn layui-btn-xs" title="trust it" onclick="trust_cert('\${cert.fn}')"><i class="fa fa-arrow-up"></i></button>
					<button class="layui-btn layui-btn-xs layui-btn-danger" title="delete" onclick="del_cert(false,'\${cert.fn}')"><i class="fa fa-times"></i></button>
				</div>
			</div>`
		}
	}
	$("#rejected_list").html(tmps) ;
}

function update_cers()
{
	send_ajax("service_ajax.jsp","op=opcua_cer_list",function(bsucc,ret){
		if(!bsucc || ret.indexOf('{')!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		eval("cert_jo="+ret) ;
		update_ui() ;
	}) ;
}

function trust_cert(filen)
{
	dlg.confirm('trust this cert?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>confirm</wbt:g>"},function ()
		    {
					send_ajax("service_ajax.jsp","op=opcua_trust_cert&fn="+filen,function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg(ret) ;
			    			return ;
			    		}
			    		//
			    		update_cers();
						
			    	}) ;
				});
}

function reject_cert(filen)
{
	dlg.confirm('reject this cert?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>confirm</wbt:g>"},function ()
		    {
					send_ajax("service_ajax.jsp","op=opcua_reject_cert&fn="+filen,function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg(ret) ;
			    			return ;
			    		}
			    		//
			    		update_cers();
						
			    	}) ;
				});
}

function del_cert(b_trusted,filen)
{
	dlg.confirm('delete this cert?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>confirm</wbt:g>"},function ()
		    {
					send_ajax("service_ajax.jsp","op=opcua_del_cert&fn="+filen+"&trusted="+b_trusted,function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg(ret) ;
			    			return ;
			    		}
			    		//
			    		update_cers();
						
			    	}) ;
				});
}

update_cers();

function do_submit(cb)
{
	var enable = $("#enable").prop("checked") ;
	
	var auth_user = $('#user').val();
	var auth_psw = $('#psw').val();
	var auth_users = $('#users').val();
	let prjids = [] ;
	$(".chk_prj").each(function(){
		let ob =$(this) ;
		if(ob.prop("checked"))
			prjids.push(ob.val()) ;
	})
	
	let tcp_port = parseInt($("#port").val());
	if(isNaN(tcp_port))
	{
		cb(false,"invalid port") ;
		return ;
	}
	let sm_none = ""+$("#sm_none").prop("checked") ;
	let sm_sign = ""+$("#sm_sign").prop("checked") ;
	let sm_sign_enc = ""+$("#sm_sign_enc").prop("checked") ;
	
	cb(true,{enable:enable,
		auth_user:auth_user,auth_psw:auth_psw,auth_users:auth_users,
		sm_sign:sm_sign,sm_none:sm_none,sm_sign_enc:sm_sign_enc,
		tcp_port:tcp_port,prjs:prjids.join(",")});
}

</script>
</html>