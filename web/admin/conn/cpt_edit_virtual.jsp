<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "prjid"))
	return;
String repid = request.getParameter("prjid") ;
String cptp = ConnPtVirtual.TP;//request.getParameter("cptp") ;
ConnProVirtual cp = (ConnProVirtual)ConnManager.getInstance().getOrCreateConnProviderSingle(repid, cptp);
if(cp==null)
{
	out.print("no single provider found with "+cptp);
	return ;
}

String cpid = cp.getId();//.getParameter("cpid") ;
String connid = request.getParameter("connid") ;
ConnPtVirtual cpt = null ;


if(Convert.isNullOrEmpty(connid))
{
	cpt = new ConnPtVirtual() ;
	connid = cpt.getId() ;
}
else
{
	cpt = (ConnPtVirtual)cp.getConnById(connid) ;
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
String script = cpt.getIntervalScript();
String ow_script = cpt.getOnWriteScript() ;
String cp_tp = cp.getProviderType() ;
long run_int = cpt.getRunIntervalMS() ;
%>
<html>
<head>
<title>virtual conn editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script>
dlg.resize_to(750,780);
</script>
</head>
<body>
<form class="layui-form"  onsubmit="return false;">
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
    <label class="layui-form-label">Run Interval:</label>
    <div class="layui-input-inline">
      <input type="text" id="run_int" name="run_int" value="<%=run_int%>"   autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">MS</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    
	  </div>
	 <div class="layui-form-mid"></div>
	  <div class="layui-input-inline" style="width: 150px;">
	    
	  </div>
  </div>
  
    <div class="layui-form-item">
    <label class="layui-form-label">Run Script:</label>
    <div class="layui-input-block">
    ()=&gt;
      <textarea  id="script"  name="script"  lay-verify="required" placeholder="" class="layui-textarea" rows="10"><%=script%></textarea>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">On Write Script:</label>
    <div class="layui-input-block">
    ("$tag","$value")=&gt;
      <textarea  id="on_w_script"  name="on_w_script"  lay-verify="required" placeholder="" class="layui-textarea" rows="10"><%=ow_script%></textarea>
    </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label">Description:</label>
    <div class="layui-input-block">
      <input  id="desc"  name="desc"  lay-verify="required" placeholder="" class="layui-input" value="<%=Convert.plainToHtml(desc)%>" />
    </div>
  </div>
   
 </form>
</body>
<script type="text/javascript">
var form = null;
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
	  $("#script").on("input",function(e){
		  setDirty();
		  });
	  $("#on_w_script").on("input",function(e){
		  setDirty();
		  });
	  
	  $("#run_int").on("input",function(e){
		  setDirty();
		  });
	  form.on('switch(enable)', function(obj){
		       setDirty();
		  });
		  
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
	//if(tt==null||tt=='')
	//{
	//	cb(false,'Please input title') ;
	//	return ;
	//}
	var ben = $("#enable").prop("checked") ;
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var script = document.getElementById('script').value;
	if(script==null)
		script ='' ;
	var on_w_script = document.getElementById('on_w_script').value;
	if(on_w_script==null)
		on_w_script ='' ;
	
	var runint = get_input_val("run_int",500,true) ;
	cb(true,{id:conn_id,name:n,title:tt,desc:desc,enable:ben,script:script,run_int:runint,on_w_script:on_w_script});
}

</script>
</html>