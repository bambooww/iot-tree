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
		boolean bind_tag_only = "true".equalsIgnoreCase(request.getParameter("bind_tag_only")) ;
				// bind_tag_only = true;
%>
<html>
<head>
<title>Edit</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/oc/oc.js"></script>
<style type="text/css">
.h_mid{
    float: left;
    display: inline-block;
    padding: 9px 15px;
    font-weight: 400;
    line-height: 20px;
    text-align: right;
}
</style>
<script>
dlg.resize_to(650,500);
</script>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label">Item:</label>
    <div class="layui-input-inline" style="width:400px;">
      <input type="text" id="binded_id" name="binded_id" readonly="readonly" lay-verify="required" autocomplete="off" class="layui-input" >
    </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-block">
      <input id="exp_false" type="radio" lay-filter="bexp" name="bexp" value="false" title="context tag"  checked >
<%
if(!bind_tag_only)
{
%>
      <input id="exp_true" type="radio" lay-filter="bexp" name="bexp" value="true" title="js express" >
<%
}
%>
      <input id="exp_unbind" type="radio" lay-filter="bexp" name="bexp" value="unbind" title="unbind" >

    </div>
  </div>
<div id="divtag">
   <div class="layui-form-item" >
    <label class="layui-form-label">Tag:</label>
    <div class="layui-input-inline" style="width:250px;">
      <input type="text" id="tag" name="tag" lay-verify="required" autocomplete="off" class="layui-input" placeholder="Select via the button on the right">
    </div>
    <div class="layui-form-mid"><button onclick="sel_tag()" class="layui-btn layui-btn-sm layui-btn-primary">...</button></div>
    <div class="layui-input-inline" style="width:200px;">
    	<input type="checkbox" class="layui-input" lay-skin="primary" id="need_tag_cached" />
    	<span class="h_mid">Need Cached Data</span>
    </div>
  </div>
  <div class="layui-form-item" >
    <label class="layui-form-label">Trans JS:</label>
    <div class="layui-input-inline" style="width:450px;">
    ($V,$this)=>{
      <textarea id="trans" name="trans" placeholder="e.g. if($V<30) return 'green'; else return 'red';" class="layui-textarea" rows="8" ondblclick="on_client_js_edit('trans')"></textarea>
      }
    </div>
  </div>
</div>
  
  <div class="layui-form-item" id="divtjs" style="display:none;">
    <label class="layui-form-label">Client Script:</label>
    <div class="layui-input-inline" style="width:400px;">
    ()=>{
      <textarea id="js" name="js" placeholder="" class="layui-textarea" rows="10" ondblclick="on_client_js_edit('js')"></textarea>
      }
      <br/><div class="layui-form-mid layui-word-aux" onclick="insert_tag()">insert tag</div>
    </div>
    
  </div>
  
 </form>
</body>
<script type="text/javascript">

var form = null ;

var bind_val = "false";
var bind_tag_only = <%=bind_tag_only%>;

layui.use('form', function(){
	  form = layui.form;
	  
	  form.on('radio(bexp)', function (data) {
		　
		　　var value = data.value;
			sel_exp_or_not(value);
		});
	  
	  form.render() ;
});

var ow = dlg.get_opener_w() ;
var path = null ;
var plugpm = ow.editor_plugcb_pm;
var js_cxt = null ;
if(plugpm!=null)
{
	// {editor:editorname,editor_id:cxtnodeid,path:path,di:di,name:name,val:val,cxtnodeid:cxtnodeid} ;
	//console.log(plugpm) ;
	var di = plugpm.di ;
	let pb = plugpm.val;
	path = plugpm.path ;
	
	if(pb)
		js_cxt = pb.JS_getCxt();
	var pdf = di.findPropDefItemByName(plugpm.name) ;
	$("#binded_id").val(pdf.title+"["+plugpm.name+"] ") ;
	$("#name").val(plugpm.name) ;
	var vv = plugpm.val ;
	if(vv)
	{
		document.getElementById("exp_"+vv.bExp).checked = true ;
		sel_exp_or_not(vv.bExp?"true":"false") ;
		$("#trans").val(vv.binderTrans)
		if(vv.bExp)
			$("#js").val(vv.binderTxt) ;
		else
			$("#tag").val(vv.binderTxt) ;
		$("#need_tag_cached").prop("checked",vv.need_tag_cached)
	}
	form.render() ;
}

function sel_tag()
{
	if(plugpm==null)
		return ;
	//var cxtnodeid = plugpm.cxtnodeid ;
	var tmpv = $("#tag").val() ;
	dlg.open("../ua_cxt/di_cxt_tag_selector.jsp?path="+plugpm.path+"&val="+tmpv+"&bind_tag_only="+bind_tag_only,
			{title:"Select Tag in Context",w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					var ret = dlgw.get_val() ;
					if(ret==null)
						return ;
					$("#tag").val(ret) ;
					 dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function insert_tag()
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
					insertAtCursor("js", ret);
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

function sel_exp_or_not(v)
{
	bind_val = v ;
	if(v=="true")
	{
		$("#divtjs").css("display","") ;
		$("#divtag").css("display","none") ;
	}
	else if(v=="false")
	{
		$("#divtjs").css("display","none") ;
		$("#divtag").css("display","") ;
	}
	else if(v=="unbind")
	{
		$("#divtjs").css("display","none") ;
		$("#divtag").css("display","none") ;
	}
}

	
function win_close()
{
	dlg.close(0);
}


function editplug_get(cb)
{
	var tag = $("#tag").val() ;
	var js =  $("#js").val() ;
	let trans = $("#trans").val();
	var jstxt=null ;
	var bexp = false;
	var bunbind=false;
	var need_tag_cached = $("#need_tag_cached").prop("checked") ;
	if(bind_val=="true")
	{
		js = trim(js) ;
		if(js==null||js=="")
		{
			cb(false,'please input js expression') ;
			return ;
		}
		bexp=true;
		jstxt= js ;
	}
	else if(bind_val=='false')
	{
		if(tag==null)
		{
			cb(false,'please input tag path in context!') ;
			return ;
		}
		jstxt = tag ;
	}
	else if(bind_val=='unbind')
	{
		bunbind=true;
	}
	
	return {bexp:bexp,jstxt:jstxt,unbind:bunbind,trans:trans,need_tag_cached:need_tag_cached};
}

function on_client_js_edit(id)
{
	let txt = $("#"+id).val();
	
	dlg.open("../ua_cxt/client_script.jsp?dlg=true&opener_txt_id=js&path=",
			{title:"Edit Client JS",w:'600px',h:'400px',js_cxt:txt},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					let jstxt = dlgw.get_edited_js();
					
					$("#"+id).val(jstxt) ;
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