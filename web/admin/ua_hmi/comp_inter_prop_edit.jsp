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
dlg.resize_to(600,710);
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
    <div class="layui-input-inline" style="width:100px">
      <select id="val_tp" name="val_tp" class="layui-input">
      	  <option value="number">number</option>
      	  <option value="bool">bool</option>
      	  <option value="str">string</option>
      	  <option value="str_m">string multi</option>
      	  <option value="color">color</option>
      </select>
    </div>
    <label class="layui-form-label">Editor assist:</label>
    <div class="layui-input-inline" style="width:100px">
      <select id="edit_plug" name="edit_plug" lay-filter="edit_plug" title="with string input">
      	  <option value=""> --- </option>
      	  <option value="color">color</option>
      	  <option value="fill">fill</option>
      	  <option value="opt">option</option>
      </select>
    </div>
  </div>
  <div class="layui-form-item" id="c_limit_diss">
    <label class="layui-form-label">Limit Diss</label>
    <div class="layui-input-block" style="text-align: left;color:green;">
    	<input type="text" id="limit_diss" name="limit_diss" value=""   autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">OnGetJS:</label>
    <div class="layui-input-block" style="text-align: left;color:green;">
    ($this)=>
      <textarea id="onGetJS" name="onGetJS" placeholder="" class="layui-textarea" rows="8" ondblclick="on_client_get_js_edit()" title="double click to open js editor"></textarea>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">OnSetJS:</label>
    <div class="layui-input-block" style="text-align: left;color:green;">
    ($this,$value)=>
      <textarea id="onSetJS" name="onSetJS" placeholder="" class="layui-textarea" rows="10"  ondblclick="on_client_set_js_edit()" title="double click to open js editor"></textarea>
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">

var ctrl_n = "<%=n%>" ;
var hmiView = null ;
var path="" ;
if(ctrl_n!="")
{
	var ow = dlg.get_opener_w() ;
	hmiView = ow.hmiView;
	var ctrlitem = ow.loadLayer.getCompInter().getInterPropByName(ctrl_n) ;
	if(ctrlitem!=null)
	{
		$("#name").val(ctrlitem.n) ;
		$("#title").val(ctrlitem.t) ;
		$("#val_tp").val(ctrlitem.tp);
		$("#edit_plug").val(ctrlitem.editplug) ;
		$("#onGetJS").val(ctrlitem.onGetJS) ;
		$("#onSetJS").val(ctrlitem.onSetJS) ;
		$("#limit_diss").val(ctrlitem.getLimitDissStr())
	}
}

function update_ui()
{
	 let v = $("#edit_plug").val() ;
	  $("#c_limit_diss").css("display",(v=='opt')?"":"none") ;
}

layui.use('form', function(){
	  var form = layui.form;
	  form.on("select(edit_plug)",function(obj){
		  update_ui();
	  }) ;
	  
	  update_ui();
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
	var editplug = $("#edit_plug").val();
	var gjs = $('#onGetJS').val();
	var sjs = $('#onSetJS').val();
	if(n==null||n=='')
	{
		cb(false,'please input name') ;
		return ;
	}
	let limit_diss = $("#limit_diss").val() ;
	if(limit_diss)
		limit_diss = limit_diss.split(",") ;
	cb(true,{n:n,t:tt,tp:tp,editplug:editplug,onGetJS:gjs,onSetJS:sjs,limit_diss:limit_diss});
}

function on_client_get_js_edit()
{
	//if(!path)
	//	return ;
	let js_cxt = hmiView.JS_getCxt();
	dlg.open("../ua_cxt/client_script.jsp?dlg=true&opener_txt_id=onGetJS&path="+path,
			{title:"Edit Property Interface OnGetJS",w:'600px',h:'400px',js_cxt:js_cxt},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					
					$("#onGetJS").val(jstxt) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function on_client_set_js_edit()
{
	//if(!path)
	//	return ;
	let js_cxt = hmiView.JS_getCxt();
	dlg.open("../ua_cxt/client_script.jsp?dlg=true&opener_txt_id=onSetJS&path="+path,
			{title:"Edit Property Interface OnSetJS",w:'600px',h:'400px',js_cxt:js_cxt},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					
					$("#onSetJS").val(jstxt) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}
</script>
</html>