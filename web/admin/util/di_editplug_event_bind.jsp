<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.basic.*,
				 org.iottree.core.util.xmldata.*"%><%
	 boolean bsjs = !"false".equals(request.getParameter("sjs")) ;
%><html>
<head>
<title>Edit</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
<script>
dlg.resize_to(500,580);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Name:</label>
    <div class="layui-input-block">
      <input type="text" id="name" name="name" required  lay-verify="required" placeholder="Pls input name" readonly="readonly" class="layui-input">
    </div>
  </div>
  
  <div class="layui-form-item">
    <label class="layui-form-label">Client JS:</label>
    <div class="layui-input-block" style="text-align: left;color:green;">
    ($server,$util,$this)=&gt;{
      <textarea id="clientjs" name="clientjs" placeholder="" class="layui-textarea" rows="6" onclick="on_client_js_edit()"></textarea>
      }
      <%--
      <div class="layui-form-mid layui-word-aux" onclick="insert_tag('clientjs')">insert tag</div>
       --%>
    </div>
    
  </div>
<%
if(bsjs)
{
%>
  <div class="layui-form-item">
    <label class="layui-form-label">Server JS:</label>
    <div class="layui-input-block" style="text-align: left;color:green;">
      $event.fire_to_server() must be called in client js to be triggered.
      ($input)=&gt;{
      <textarea id="serverjs" name="serverjs" placeholder="" class="layui-textarea" rows="6" onclick="on_js_edit()"></textarea>
      }
      <div class="layui-form-mid layui-word-aux" onclick="insert_tag('serverjs')">insert tag</div>
    </div>
  </div>
<%
}
%>
 </form>
</body>
<script type="text/javascript">

var ow = dlg.get_opener_w() ;
var plugpm = ow.editor_plugcb_pm;
var path = null ;
var eventb = null ;
if(plugpm!=null)
{
	console.log(plugpm);
	$("#name").val(plugpm.name) ;
	path = plugpm.path ;
	eventb = plugpm.val ;
	if(eventb!=null)
	{
		$("#clientjs").val(eventb.getClientJS()) ;
		$("#serverjs").val(eventb.getServerJS()) ;
	}
}

layui.use('form', function(){
	  var form = layui.form;
});
	
function win_close()
{
	dlg.close(0);
}


function editplug_get(cb)
{
	var n = $('#name').val();
	var cjs = $('#clientjs').val();
	var sjs = $('#serverjs').val();
	if(n==null||n=='')
	{
		cb(false,'please input name') ;
		return ;
	}
	return {n:n,clientjs:cjs,serverjs:sjs};
}

function insert_tag(txtid)
{
	if(plugpm==null)
		return ;
	var cxtnodeid = plugpm.cxtnodeid ;
	var tmpv = $("#tag").val() ;
	dlg.open("../ua_cxt/di_cxt_tag_selector.jsp?path="+plugpm.path+"&val="+tmpv,
			{title:"Select Tag in Context",w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					var ret = dlgw.get_val() ;
					insertAtCursor(txtid, ret);
					 dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function insertAtCursor(txtarea_id, txt)
{
	var txtarea = document.getElementById(txtarea_id) ;
	if(txtarea==null)
		return ;
	if (document.selection)
	{//IE support
		txtarea.focus();
		sel = document.selection.createRange();
		sel.text = txt;
		sel.select();
	}
	else if(txtarea.selectionStart || txtarea.selectionStart == '0')
	{//MOZILLA/NETSCAPE support
		var startPos = txtarea.selectionStart;
		var endPos = txtarea.selectionEnd;
		// save scrollTop before insert www.keleyi.com
		var restoreTop = txtarea.scrollTop;
		txtarea.value = txtarea.value.substring(0, startPos) + txt + txtarea.value.substring(endPos, txtarea.value.length);
		if (restoreTop > 0) {
		txtarea.scrollTop = restoreTop;
		}
		txtarea.focus();
		txtarea.selectionStart = startPos + txt.length;
		txtarea.selectionEnd = startPos + txt.length;
	}
	else
	{
		txtarea.value += txt;
		txtarea.focus();
	}
}

function on_client_js_edit()
{
	if(!path)
		return ;
	let js_cxt = eventb.JS_getCxt();
	dlg.open("../ua_cxt/client_script.jsp?dlg=true&opener_txt_id=clientjs&path="+path,
			{title:"Edit Client JS",w:'600px',h:'400px',js_cxt:js_cxt},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					
					$("#clientjs").val(jstxt) ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function on_js_edit()
{
	if(!path)
		return ;
	let txt = $("#serverjs").val() ;
	dlg.open("../ua_cxt/cxt_script.jsp?dlg=true&opener_txt_id=serverjs&path="+path,
			{title:"Edit JS",w:'600px',h:'400px',},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					
					$("#serverjs").val(jstxt) ;
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