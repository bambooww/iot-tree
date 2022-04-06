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
	"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","cpid"))
	return;
String repid = request.getParameter("prjid") ;
String cpid =  request.getParameter("cpid") ;
String cptp = ConnProHTTP.TP;//request.getParameter("cptp") ;
ConnProHTTP cp = (ConnProHTTP)ConnManager.getInstance().getConnProviderById(repid, cpid);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String connid = request.getParameter("connid") ;

ConnPtHTTP cpt = null ;
if(Convert.isNullOrEmpty(connid))
{
	cpt = new ConnPtHTTP() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (ConnPtHTTP)cp.getConnById(connid) ;
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
String url = cpt.getUrl();
String method = cpt.getMethod();
long int_ms = cpt.getIntervalMS();
String cp_tp = cp.getProviderType() ;

ConnPt.DataTp sor_tp = cpt.getSorTp();
String init_js = cpt.getInitJS() ;
String trans_js = cpt.getTransJS();

String encod = cpt.getEncod() ;
if(Convert.isNullOrEmpty(encod))
	encod = "UTF-8";
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
dlg.resize_to(600,500);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value="<%=name%>"  lay-verify="required" autocomplete="off" class="layui-input">
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
    <label class="layui-form-label">Url:</label>
    <div class="layui-input-block">
      <input type="text" id="url" name="url" value="<%=url %>" class="layui-input">
    </div>
    
  </div>

<div class="layui-form-item">
    <label class="layui-form-label">Method</label>
    <div class="layui-input-inline">
     <select id="method" lay-filter="method">
			<option value="GET" <%=("GET".equals(method)?"selected=selected":"") %>>GET</option>
			<option value="POST" <%=("POST".equals(method)?"selected=selected":"") %> >POST</option>
		 </select>
    </div>
	 <div class="layui-form-mid">Update Interval</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="number" id="int_ms" name="int_ms" value="<%=int_ms%>"  class="layui-input">
	  </div>
  </div>
  
   
  <div class="layui-form-item">
    <label class="layui-form-label">Message Source Type</label>
    <div class="layui-input-inline" style="width:70px">
    	<select id="sor_tp" lay-filter="sor_tp" >
<%
	for(ConnPt.DataTp stp:ConnPt.DataTp.values())
{
%><option value="<%=stp.toString()%>"><%=stp.getTitle() %></option>
<%
}
%>
    	</select>
    </div>
    <label class="layui-form-label">Encoding</label>
    <div class="layui-input-inline" style="width:100px">
    <select id="encod" lay-filter="encod" >
<%
for(String chartset:java.nio.charset.Charset.availableCharsets().keySet())
{
%><option value="<%=chartset%>"><%=chartset %></option><%
}
%>
		
		
    </select>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Initial JS:</label>
    <div class="layui-input-inline" style="width:600px">
      <textarea  id="init_js"  name="init_js"  style="height:60px;width:100%;border-color: #e6e6e6"><%=init_js%></textarea>
    </div>
    <button onclick="edit_js_init()" class="layui-btn layui-btn-<%=(true?"normal":"primary") %> layui-border-blue layui-btn-sm">...</button>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Transfer JS:</label>
    <div class="layui-input-inline" style="width:600px">
    ($topic,$msg)=>{
      <textarea  id="trans_js"  name="trans_js"  class="layui-textarea" style="height:150px"><%=trans_js%></textarea>
      }
    </div>
    <button onclick="edit_js_trans()" class="layui-btn layui-btn-<%=(true?"normal":"primary") %> layui-border-blue layui-btn-sm">...</button>
    <%--
    <label class="layui-form-label">Device JS:</label>
    <div class="layui-input-inline">
      <textarea  id="devs_js"  name="devs_js"  required class="layui-textarea" rows="2"><%=""%></textarea>
    </div>
     --%>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-inline" style="width:600px">
      <textarea  id="desc"  name="desc"  style="height:30px;width:100%;border-color: #e6e6e6"><%=desc%></textarea>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var form = null;
var sor_tp = "<%=sor_tp%>";
var encod = "<%=encod%>";
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
	  $("#url").on("input",function(e){
		  setDirty();
		  });
	 
	  $("#int_ms").on("input",function(e){
		  setDirty();
		  });
	  
	  
	  form.on('switch(enable)', function(obj){
		       setDirty();
		  });
	  form.on('select(method)', function(obj){
		       setDirty();
		  });
	  $("#init_js").on("input",function(e){
		  setDirty();
		  });
	  
	  $("#trans_js").on("input",function(e){
		  setDirty();
		  });
	  form.on('select(sor_tp)', function(obj){
		       setDirty();
		  });
	  form.on('select(encod)', function(obj){
		       setDirty();
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

function isDirty()
{
	return bdirty;
}
function setDirty()
{
	bdirty= true;
	dlg.btn_set_enable(1,true);
}

	
function win_close()
{
	dlg.close(0);
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
	
	var url = $('#url').val();
	if(url==null||url=='')
	{
		cb(false,'Please input Url') ;
		return ;
	}
	
	var method = $('#method').val();
	if(method==null||method=='')
	{
		cb(false,'Please select Method') ;
		return ;
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
	
	var sor_tp = $('#sor_tp').val();
	if(sor_tp==null||sor_tp=='')
	{
		cb(false,'Please input sor_tp') ;
		return ;
	}
	var init_js = $('#init_js').val();
	var trans_js = $('#trans_js').val();
	var enc =  $("#encod").val() ;
	
	cb(true,{id:conn_id,name:n,title:tt,desc:desc,enable:ben,url:url,method:method,int_ms:int_ms,sor_tp:sor_tp,init_js:init_js,trans_js:trans_js,encod:enc});
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