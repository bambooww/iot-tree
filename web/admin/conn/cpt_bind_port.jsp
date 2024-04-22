<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.eclipse.milo.opcua.stack.core.types.structured.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*,
				org.eclipse.milo.opcua.sdk.client.nodes.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%!
	
	%><%
	if(!Convert.checkReqEmpty(request, out,"op", "prjid","cptp"))
		return;
	String op = request.getParameter("op") ;
	String prjid = request.getParameter("prjid") ;

String cptp = request.getParameter("cptp") ; //reConnProOPCUA.TP;//
ConnProvider cp = ConnManager.getInstance().getOrCreateConnProviderSingle(prjid, cptp);
if(cp==null)
{
	String cpid = request.getParameter("cpid") ;
	if(Convert.isNotNullEmpty(cpid))
	{
		cp =  ConnManager.getInstance().getConnProviderById(prjid, cpid);
	}
}
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnPtBinder cpt = null ;


if(Convert.isNullOrEmpty(connid))
{
	out.print("no connid input") ;
	return;
}

cpt = (ConnPtBinder)cp.getConnById(connid) ;
if(cpt==null)
{
	out.print("no connection found") ;
	return ;
}

String txt = "" ;
if("export".equals(op))
{
	txt = cpt.exportBindMap();
}
%>
<html>
<head>
<title>import or export</title>
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
<%
if("import".equals(op))
{
%>
  <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline">
      
    </div>
    <div class="layui-form-mid"><w:g>ign_not_exist</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="ignore_null" name="ignore_null" checked='checked' lay-skin="switch"  lay-filter="ignore_null" class="layui-input">
	  </div>
	<div class="layui-form-mid"><w:g>auto_c_tag</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="auto_create_tag" name="auto_create_tag" checked='checked' lay-skin="switch"  lay-filter="auto_create_tag" class="layui-input">
	  </div>
  </div>
<%
}
else
{
%>
<div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline">
      
    </div>
    <div class="layui-form-mid"></div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <button type="button" class="layui-btn"><w:g>copy</w:g></button>
	  </div>

  </div>
<%
}
%>
    <div class="layui-form-item">
    <label class="layui-form-label">Text:</label>
    <div class="layui-input-block" style="width:80%">
      <textarea  id="txt"  name="txt"  class="layui-textarea" rows="20"><%=txt%></textarea>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var prjid="<%=prjid%>";
var cpid = "<%=cpid%>";
var cptp = "<%=cptp%>";
var connid = "<%=connid%>";

var form = null;
layui.use('form', function(){
	  form = layui.form;
	  form.on("select(ignore_null)",function(obj){
		  setDirty();
		  });
	  form.on("select(auto_create_tag)",function(obj){
		  setDirty();
		  });
	  $("#txt").on("input",function(e){
		  setDirty();
		  });
	  form.render(); 
});

var _tmpid = 0 ;

var bdirty=false;


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

function get_val(id,title,cb,bnum)
{
	var v = $('#'+id).val();
	if(v==null||v=='')
	{
		cb(false,'<w:g>pls,input</w:g> '+title) ;
		throw "no "+title+" input" ;
	}
	if(bnum)
	{
		v = parseInt(v);
		if(v==NaN)
		{
			cb(false,'<w:g>pls,input,valid</w:g> '+title) ;
			throw "invalid "+title+" input" ;
		}
	}
	
	return v ;
}

function do_submit(cb)
{
	
	var ignore_null = $("#ignore_null").prop("checked") ;
	var auto_create_tag = $("#auto_create_tag").prop("checked") ;
	var txt = document.getElementById('txt').value;
	txt = trim(txt) ;
	if(txt==null||txt=='')
	{
		dlg.msg("<w:g>pls,input,txt</w:g>") ;
		return ;
	}

	cb(true,{ignore_null:ignore_null,auto_create_tag:auto_create_tag,txt:txt});
}

</script>
</html>