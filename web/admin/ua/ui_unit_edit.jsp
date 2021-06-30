<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				java.net.*,org.iottree.core.*,
				org.iottree.core.util.*"%>
<%
	if(!Convert.checkReqEmpty(request, out, "unitid"))
		return ;
	//String op = request.getParameter("op");
	String unitid = request.getParameter("unitid");
	
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Unit Editor</title>
<script src="/_js/jquery.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
}

.content {
	width: 100%;
}

.content .right {
	float: right;
	width: 49%;
	margin: 0px
}

.dragtt {
	padding: 5px;
	width: 95%;
	margin-bottom: 2px;
	border: 2px #ccc;
	background-color: #eee;
}

.draglist {
	float: left;
	padding: 2px;
	margin-bottom: 2px;
	border: 2px solid #ccc;
	background-color: #eee;
	cursor: move;
}

.draglist:hover {
	border-color: #cad5eb;
	background-color: #f0f3f9;
}


.lr_btn
{
	margin-top: 20px;
	color:#858585;
	cursor: pointer;
}

.lr_btn_btm
{
	margin-bottom: 20px;
	position:absolute;
	left:5px;
	bottom:20px;
	color:#858585;
	cursor: pointer;
}

.left i:hover{
color: #ffffff;
}

.right i:hover{
color: #ffffff;
}

</style>
</head>
<script type="text/javascript">


</script>
<body class="layout-body">
		<div class="mid" style="left:0px;">
			<div id="main_panel" style="border: 0px solid #000; width: 100%; height: 100%; background-color: #1e1e1e;">
		</div>
		<div class="right " style="background-color: #333333">
		  <i id="edit_panel_btn"  class="fa fa-pencil-square-o fa-3x lr_btn"></i>
		  <i id="lr_btn_fitwin"  class="fa fa-crosshairs fa-3x lr_btn"></i>
		</div>
	</div>


<div id='edit_panel' style="display:none;border: 1; font: 15; position: absolute; top: 3px; width: 30%; height: 90%; right: 50px; background-color: window; z-index: 60000; overFlow0: auto">
	<div style="background-color: olive; color: white; border: 1; border-bottom-style: inset; margin: 1; text-align: left">
		  
		 <input class="layui-btn layui-btn-primary layui-btn-sm" name='save' type='button' value='保存' onclick="btn_save()" title="ctrl+b" />
		 <input class="layui-btn layui-btn-primary layui-btn-sm" name='load' type='button' value='装载' onclick="btn_load()" title="ctrl+b" />
		 
		 <input type="button" value="Apply" onclick="do_apply()" class="layui-btn layui-btn-primary layui-btn-sm" />
	</div>

	<div id="p_info" style="background-color: grey; height: 20">&nbsp;</div>

<div class="layui-tab" lay-filter="edit_right">
  <ul class="layui-tab-title">
    <li lay-id="tb_base" class="layui-this">base edit</li>
    <li lay-id="tb_prop">prop edit</li>
    <li lay-id="tb_prop">event edit</li>
  </ul>
  <div class="layui-tab-content">
    <div class="layui-tab-item layui-show">
      <div id="tabs-3" style="height: 90%">
		
		<input type="button" value="Copy" onclick="do_copy()" class="layui-btn layui-btn-primary layui-btn-sm" />
        <input type="button" value="Add Line" onclick="do_add_di('oc.di.DILine')" />
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Add Rect" onclick="do_add_di('oc.di.DIRect')" />
		<input type="button" value="Add Txt" onclick="do_add_di('oc.di.DITxt')" />
        <input type="button" value="Add Img" onclick="do_add_di('oc.di.DIImg')" />
        <input type="button" value="Add Icon" onclick="do_add_di('oc.di.DIIcon')" />
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Add Arc" onclick="do_add_di('oc.di.DIArc')" />
		<input type="button" value="Add Pts Rect" onclick="do_add_di('oc.di.DIPts',{pts_tp:'rect'})" />
	    <input type="button" value="Add Pts Diamond" onclick="do_add_di('oc.di.DIPts',{pts_tp:'diamond'})" /> <br>
		Name:<input type="text" id="unit_name" value=""/> Title:<input type="text" id="unit_title" value=""/><br>
		<div id="unit_props" style="overflow:hidden;"></div>
		
	</div>
	</div>
    <div class="layui-tab-item">
      <div id='edit_props' style="height: 100%"></div>
      
      
    </div>
    <div class="layui-tab-item">
     <div id='edit_events' style="height: 20%"></div>
      
      
    </div>
    
  </div>
</div>

	
</div>

<script>

var uid = "<%=unitid%>" ;
var lay_ele = null;

layui.use('element', function(){
	lay_ele = layui.element;
  
	//lay_ele.on('tab(edit_right)', function(){
	//    location.hash = 'edit_right='+ this.getAttribute('lay-id');
	//  });
});

var panel = null;
var editor = null ;

var loadLayer = null ;
var intedit =null;


var drawUnit = null;


function on_panel_mousemv(p,d)
{
	$("#p_info").html("["+p.x+","+p.y+"] - ("+Math.round(d.x*10)/10+","+Math.round(d.y*10)/10+")");
}
function on_item_sel_chg(item)
{
	if(item!=null)
		lay_ele.tabChange("edit_right","tb_prop");
	else
		lay_ele.tabChange("edit_right","tb_base");
}

function init_panel()
{
	panel = new oc.DrawPanel("main_panel",{
		on_mouse_mv:on_panel_mousemv,
		on_item_sel_chg:on_item_sel_chg
	});
	
	if(panel.init_panel)
		panel.init_panel();

	loadLayer = new oc.DrawLayer("lay") ;
	
	panel.addLayer(loadLayer) ;

	panel.on_draw();

}


init_panel();

function do_apply()
{
	if(!editor.applyUI2SelectedItem())
	{
		dlg.msg("apply failed");
		return ;
	}
	//panel.on_draw();
}

function do_add_di(dicn,opts)
{
	if(intedit==null)
		return;
	if(!intedit.setOperAddItem(dicn,opts))
	{
		dlg.msg("set oper error");
		return;
	}
	
}

function do_add_unit_ins(unitid)
{
	if(intedit==null)
		return;
	if(!intedit.setOperAddUnitIns(unitid))
	{
		dlg.msg("set oper add unit ins error");
		return;
	}
	
}

function do_copy()
{
	if(intedit==null||loadLayer==null)
		return;
	var si = intedit.getSelectedItem();
	if(si==null)
		return;
	loadLayer.copyItem(si.getId()) ;
}


function btn_save()
{
	if(drawUnit==null)
		return ;
	var pm = {} ;
	pm.op="save" ;
	pm.id=uid;
	//console.log("1="+drawUnit.getItemsShow().length);
	drawUnit.setItems(loadLayer.getItemsShow());
	//console.log("2="+drawUnit.getItemsShow().length);
	drawUnit.setId(uid);
	drawUnit.setName($("#unit_name").val());
	drawUnit.setTitle($("#unit_title").val());
	var instmpdefs = oc.DrawUnit.getInsTempDefs();
	var r = oc.DrawEditor.transUI2PropByPdf(instmpdefs);
	drawUnit.setDynData(r,false);
	//console.log("3="+drawUnit.getItemsShow().length);
	pm.txt = JSON.stringify(drawUnit.extract()) ;
	$.ajax({
        type: 'post',
        url:'ui_unit_ajax.jsp?op=save',
        data: pm,
        async: true,  
        success: function (result) {  
        	layer.msg(result); 
        	oc.DrawUnit.setUnit(drawUnit);
        },
        error:function(req,err,e)
        {
        	dlg.msg(e);
        }
    });
}

function load_unit()
{
	send_ajax("ui_unit_ajax.jsp","op=load&id="+uid,function(bsucc,ret){
		
		if(!bsucc||ret.indexOf("{")!=0)
		{
			
		console.log(ret)
			dlg.msg(ret);
			return ;
		}
		drawUnit = new oc.DrawUnit(); 
		drawUnit.inject(ret);
		if(ret=="{}")
			drawUnit.setDrawXY(-drawUnit.w/2,-drawUnit.h/2);
		$("#unit_name").val(drawUnit.getName());
		$("#unit_title").val(drawUnit.getTitle());
		var instmpdefs = oc.DrawUnit.getInsTempDefs();
		var tmps="" ;
		for(var n in instmpdefs)
		{
			var pdf = instmpdefs[n];
			var v = drawUnit[n];
			if(v==null)
				v="" ;
			tmps += oc.DrawEditor.createPropItemEditHtml(n,pdf,v);
		}
		$("#unit_props").html(tmps) ;
		
		var items = drawUnit.getItemsShow();
		loadLayer.setItems(items);
		
		editor = new oc.DrawEditor("edit_props","edit_events",panel,{
			
		}) ;
		editor.init_editor();
		
		intedit = new oc.interact.InteractEditLayer(panel,loadLayer,{copy_paste_url:"../util/copy_paste_ajax.jsp"});
		//intedit.setSelectedListener(editor);
		panel.setInteract(intedit);
		loadLayer.ajustDrawFit();
	}) ;

}

function draw_fit()
{
	if(loadLayer==null)
		return ;
	loadLayer.ajustDrawFit();
}

var r = 0 ;




//////////edit panel
$(document).ready(function()
{
	$('#edit_panel_btn').click(function()
	{
		$('#edit_panel').slideToggle();
		$(this).toggleClass("cerrar");
   	});
	
	$('#edit_panel').slideToggle();
	$(this).toggleClass("cerrar");
 		
	$('#lr_btn_fitwin').click(function()
	{
		draw_fit();
   	});

	load_unit();
 	
});

$(window).resize(function(){
	panel.updatePixelSize() ;
	});
</script>
</body>
</html>