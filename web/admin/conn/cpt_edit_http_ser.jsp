<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.ext.opcda.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
	return;
String repid = request.getParameter("prjid") ;
//String cpid =  request.getParameter("cpid") ;
String cptp = ConnProHTTPSer.TP;//request.getParameter("cptp") ;
ConnProHTTPSer cp = (ConnProHTTPSer)ConnManager.getInstance().getOrCreateConnProviderSingle(repid, cptp);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid= cp.getId() ;
UAPrj prj = UAManager.getInstance().getPrjById(repid) ;
String prjn = prj.getName() ;
int  web_port = Config.getWebapps().getPort();
String connid = request.getParameter("connid") ;
String cid = connid ;
if(cid==null)
	cid = "" ;
ConnPtHTTPSer cpt = null ;
if(Convert.isNullOrEmpty(connid))
{
	cpt = new ConnPtHTTPSer() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (ConnPtHTTPSer)cp.getConnById(connid) ;
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

String cp_tp = cp.getProviderType() ;

ConnPt.DataTp sor_tp = cpt.getSorTp();
//String init_js = cpt.getInitJS() ;
//String trans_js = cpt.getTransJS();

String encod = cpt.getEncod() ;
if(Convert.isNullOrEmpty(encod))
	encod = "UTF-8";
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(800,750);

</script>
<style type="text/css">
.layui-form-item
{
	margin-bottom:5px;
}

.layui-input, .layui-select, .layui-textarea {
    height: 28px;
}
.layui-form-label {

    padding: 9px 15px;
    line-height: 15px;
}
.layui-form-mid {
    padding: 9px 0!important;
    line-height: 10px;
}
.layui-form-switch {

    margin-top: 2px;
}

</style>
</head>
<body>
<form class="layui-form"  onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  lay-verify="required" autocomplete="off" class="layui-input" onchange="on_up_prompt()">
    </div>
    <div class="layui-form-mid">Title:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value="<%=title%>"  lay-verify="required" autocomplete="off" class="layui-input">
	  </div>
	  <div class="layui-form-mid">Enable:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
   
  <div class="layui-form-item">
    <label class="layui-form-label">Recv Bind</label>
    <div class="layui-input-inline" style="width:650px;">
  <iframe id="if_msg" src="cpt_edit_msg.jsp?prjid=<%=repid%>&cpid=<%=cpid%>&connid=<%=cid%>" style="width:100%;height:310px;border:1px solid;border-color:#e6e6e6"></iframe>
  </div>
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-inline" style="width:600px">
      <textarea  id="desc"  name="desc"  style="height:30px;width:100%;border-color: #e6e6e6"><%=desc%></textarea>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Post URL:</label>
    <div class="layui-input-inline" style="width:600px">
      <span id="prompt" style="color:red"></span>
    </div>
  </div>
  
 </form>
</body>
<script type="text/javascript">
var form = null;
var sor_tp = "<%=sor_tp%>";
var encod = "<%=encod%>";
var web_port = <%=web_port%>;
var prjn = "<%=prjn%>" ;

layui.use('form', function(){
	  form = layui.form;
	  
	  $("#name").on("input",function(e){
		  setDirty(true);
		  });
	  $("#title").on("input",function(e){
		  setDirty(true);
		  });
	  $("#desc").on("input",function(e){
		  setDirty(true);
		  });
	  
	  form.on('switch(enable)', function(obj){
		  setDirty(true);
		  });
	  form.on('select(method)', function(obj){
		  setDirty(true);
		  });
	  $("#init_js").on("input",function(e){
		  setDirty(true);
		  });
	  
	  $("#trans_js").on("input",function(e){
		  setDirty(true);
		  });
	  form.on('select(sor_tp)', function(obj){
		  setDirty(true);
		  });
	  form.on('select(encod)', function(obj){
		  setDirty(true);
		  });

	  $("#sor_tp").val(sor_tp) ;
	  $("#encod").val(encod) ;
	  form.render(); 
});

var _tmpid = 0 ;

var bdirty=false;
var cp_id = "<%=cpid%>" ;
var cp_tp = "<%=cp_tp%>" ;
var conn_id = "<%=connid%>" ;

function on_up_prompt()
{
	let n = $("#name").val() ;
	if(!n)
	{
		$("#prompt").html("please input name") ;
		return ;
	}
	
	$("#prompt").html("http://host:"+web_port+"/"+prjn+"/_conn_httpser/"+n) ;
}

on_up_prompt();

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

function get_url()
{
	return $("#url").val();
}

	
function win_close()
{
	dlg.close(0);
}

function on_find_url_encod(enc)
{
	alert(enc) ;
}


function edit_js_trans()
{
	edit_js('trans_js','Transfer JS','$topic,$msg','trans_sample') ;
}

function edit_js_init()
{
	edit_js('init_js','Initial JS','','') ;
}

function edit_js(taid,tt,funcp,sample_id)
{
	event.preventDefault();
	dlg.open("../ua_cxt/cxt_script.jsp?opener_txt_id="+taid+"&sample_txt_id="+sample_id+"&func_params="+funcp,
			{title:tt},['Ok','Cancel'],
			[
				function(dlgw)
				{
					var jstxt = dlgw.get_edited_js() ;
					 if(jstxt==null)
						 jstxt='' ;
					 $("#"+taid).val(jstxt) ;
					 setDirty();
					 dlg.close() ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
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
		tt = n;
	}
	var ben = $("#enable").prop("checked") ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var msgob = null ;
	$("#if_msg")[0].contentWindow.do_submit((bok,ret)=>{
		if(!bok)
		{
			cb(false,ret) ;
			return;
		}
		msgob = ret ;
	})

	var oball = Object.assign({id:conn_id,name:n,title:tt,desc:desc,enable:ben},msgob);
	cb(true,oball) ;
}

function str2lns(str)
{
	var arr = str.split('\n');
	var res = [];
	arr.forEach(function (item)
	{
		var ln = item.replace(/(^\s*)|(\s*$)/g, "").replace(/\s+/g, " ")
		if(ln=='')
			return ;
	    res.push(ln);
	})

	return res ;
}
</script>
</html>