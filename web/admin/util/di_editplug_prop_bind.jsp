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
				 org.iottree.core.util.xmldata.*"%>
<html>
<head>
<title>Edit</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script>
dlg.resize_to(400,500);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Item:</label>
    <div  class="layui-input-block">
      <input type="text" id="binded_id" name="binded_id" readonly="readonly" lay-verify="required" autocomplete="off" class="layui-input" >
    </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-block">
      <input id="exp_false" type="radio" lay-filter="bexp" name="bexp" value="false" title="context tag"  checked >
      <input id="exp_true" type="radio" lay-filter="bexp" name="bexp" value="true" title="js express" >
    </div>
  </div>
   <div class="layui-form-item" id="divtag">
    <label class="layui-form-label">Tag:</label>
    <div class="layui-input-inline">
      <input type="text" id="tag" name="tag" required  lay-verify="required" autocomplete="off" class="layui-input" onclick="sel_tag()">
    </div>
  </div>
  <div class="layui-form-item" id="divtjs" style="display:none;">
    <label class="layui-form-label">Script:</label>
    <div class="layui-input-inline">
      <textarea id="js" name="js" placeholder="" class="layui-textarea" rows="10"></textarea>
    </div>
    <div class="layui-form-mid layui-word-aux" onclick="insert_tag()">insert tag</div>
  </div>
  
 </form>
</body>
<script type="text/javascript">

var form = null ;

var bExp = false;

layui.use('form', function(){
	  form = layui.form;
	  
	  form.on('radio(bexp)', function (data) {
		　
		　　var value = data.value;
			sel_exp_or_not(value=='true');
		});
	  
	  form.render() ;
});

var ow = dlg.get_opener_w() ;
var plugpm = ow.editor_plugcb_pm;
if(plugpm!=null)
{
	// {editor:editorname,editor_id:cxtnodeid,path:path,di:di,name:name,val:val,cxtnodeid:cxtnodeid} ;
	console.log(plugpm) ;
	var di = plugpm.di ;
	var pdf = di.findProDefItemByName(plugpm.name) ;
	$("#binded_id").val(pdf.title+"["+plugpm.name+"] ") ;
	$("#name").val(plugpm.name) ;
	var vv = plugpm.val ;
	if(vv)
	{
		document.getElementById("exp_"+vv.bExp).checked = true ;
		sel_exp_or_not(vv.bExp) ;
		if(vv.bExp)
			$("#js").val(vv.binderTxt) ;
		else
			$("#tag").val(vv.binderTxt) ;
	}
	form.render() ;
}

function sel_tag()
{
	if(plugpm==null)
		return ;
	//var cxtnodeid = plugpm.cxtnodeid ;
	var tmpv = $("#tag").val() ;
	dlg.open("../ua_cxt/di_cxt_tag_selector.jsp?path="+plugpm.path+"&val="+tmpv,
			{title:"Select Tag in Context",w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					var ret = dlgw.get_val() ;
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

function sel_exp_or_not(bexp)
{
	bExp = bexp ;
	if(bexp)
	{
		$("#divtjs").css("display","") ;
		$("#divtag").css("display","none") ;
	}
	else
	{
		$("#divtjs").css("display","none") ;
		$("#divtag").css("display","") ;
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
	var jstxt ;
	if(bExp)
	{
		js = trim(js) ;
		if(js==null||js=="")
		{
			cb(false,'please input js expression') ;
			return ;
		}
		jstxt= js ;
	}
	else
	{
		if(tag==null)
		{
			cb(false,'please input tag path in context!') ;
			return ;
		}
		jstxt = tag ;
	}
	
	return {bexp:bExp,jstxt:jstxt};
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}
</script>
</html>