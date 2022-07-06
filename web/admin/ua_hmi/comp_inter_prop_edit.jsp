<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
String libid =  request.getParameter("libid");
String catid = request.getParameter("catid");
String id = request.getParameter("id");
CompManager cm = CompManager.getInstance() ;
CompCat cc = cm.getCompCatById(libid,catid) ;
if(cc==null)
{
	out.print("cat not found") ;
	return ;
}
CompItem ci = cc.getItemById(id);
if(ci==null)
{
	out.print("no item found") ;
	return ;
}
String n = request.getParameter("n") ;
if(n==null)
	n = "" ;
%>
<html>
<head>
<title>Component Ctrl Edit</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(600,650);
</script>
</head>
<body>
<form class="layui-form" action="">
<div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-inline">
      <input type="text" id="name" name="name" value=""   autocomplete="off" class="layui-input">
    </div>
    <div class="layui-form-mid">title:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="text" id="title" name="title" value=""   autocomplete="off" class="layui-input">
	  </div>
  </div>

    <div class="layui-form-item">
    <label class="layui-form-label">Value Type:</label>
    <div class="layui-input-inline">
      <select id="val_tp" name="val_tp" class="layui-input">
      	  <option value="number">number</option>
      	  <option value="bool">bool</option>
      	  <option value="str">string</option>
      	  <option value="color">color</option>
      </select>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">OnGetJS:</label>
    <div class="layui-input-block">
    ($this)=>
      <textarea id="onGetJS" name="onGetJS" placeholder="" class="layui-textarea" rows="10"></textarea>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">OnSetJS:</label>
    <div class="layui-input-block">
    ($this,$value)=>
      <textarea id="onSetJS" name="onSetJS" placeholder="" class="layui-textarea" rows="10"></textarea>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">

var ctrl_n = "<%=n%>" ;
if(ctrl_n!="")
{
	var ow = dlg.get_opener_w() ;
	var ctrlitem = ow.loadLayer.getCompInter().getInterPropByName(ctrl_n) ;
	if(ctrlitem!=null)
	{
		$("#name").val(ctrlitem.n) ;
		$("#title").val(ctrlitem.t) ;
		$("#val_tp").val(ctrlitem.tp);
		$("#onGetJS").val(ctrlitem.onGetJS) ;
		$("#onSetJS").val(ctrlitem.onSetJS) ;
	}
}
layui.use('form', function(){
	  var form = layui.form;
	  
	  form.render();
});
	
function win_close()
{
	dlg.close(0);
}


function do_submit(cb)
{
	var n = $('#name').val();
	var tt = $('#title').val();
	var tp = $("#val_tp").val() ;
	var gjs = $('#onGetJS').val();
	var sjs = $('#onSetJS').val();
	if(n==null||n=='')
	{
		cb(false,'please input name') ;
		return ;
	}
	cb(true,{n:n,t:tt,tp:tp,onGetJS:gjs,onSetJS:sjs});
}

</script>
</html>