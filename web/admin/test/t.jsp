<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				java.net.*"%>
<%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	String rid = request.getParameter("rid");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title></title>

<script src="/_js/jquery.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>


<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/app_qx/data/js/jquery-3.4.1.min.js"></script>
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
<body class0="layout-body">

	<div class="layout ">
		<div class="left " style="background-color: #333333">
			<i class="fa fa-cog fa-3x lr_btn"></i>
			<i class="fa fa-cog fa-3x lr_btn"></i>
			<i class="fa fa-database fa-3x lr_btn"></i>
		</div>
		<div class="mid">
			<div id="main_panel" style="border: 0px solid #000; width: 100%; height: 100%; background-color: #1e1e1e">
				<div id="win_act_store" style="position: absolute; display: none; background-color: #cccccc;z-index:1">
				<div class="layui-btn-group">
  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" title="新增数据库"  onclick="store_add_db()">
    <i class="layui-icon">&#xe654;</i>
  </button>
  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm">
    <i class="layui-icon">&#xe642;</i>
  </button>
  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm">
    <i class="layui-icon">&#xe640;</i>
  </button>
</div>
				</div>

					<div id="win_act1"  class="oc-toolbar" style="display: none;width:;z-index:1" >
						<div class="titlebar" >
							<span class="i18n">store</span><div class="collapse icon-eda-fold"></div>
						</div>
						<div class="btns">
							<div title="图纸设置" cmd="setSheet" icon="icon-eda-sheet"
								class="toolbarbutton icon-eda-sheet" data-i18n-attr="title">
								xx
								</div>
							<div title="线条" cmd="draw(polyline)" icon="icon-eda-line"
								class="toolbarbutton icon-eda-line" data-i18n-attr="title">ab</div>
							<div title="贝塞尔曲线" cmd="draw(bezier)" icon="icon-eda-bezier"
								class="toolbarbutton icon-eda-bezier" data-i18n-attr="title"></div>
							<div title="圆弧" cmd="draw(arc)" icon="icon-eda-arc"
								class="toolbarbutton icon-eda-arc" data-i18n-attr="title"></div>
							<div title="箭头" cmd="place_part(arrowhead)" icon="icon-eda-arrow"
								class="toolbarbutton icon-eda-arrow" data-i18n-attr="title"></div>
							<div title="文本" cmd="place_part(annotation)" icon="icon-eda-text"
								class="toolbarbutton icon-eda-text" data-i18n-attr="title"></div>
							<div title="自由绘制" cmd="draw(freedraw)" icon="icon-eda-draw"
								class="toolbarbutton icon-eda-draw" data-i18n-attr="title"></div>
							<div title="矩形" cmd="draw(rect)" icon="icon-eda-rect"
								class="toolbarbutton icon-eda-rect" data-i18n-attr="title"></div>
							<div title="多边形" cmd="draw(polygon)" icon="icon-eda-polygon"
								class="toolbarbutton icon-eda-polygon" data-i18n-attr="title"></div>
							<div title="椭圆" cmd="draw(ellipse)" icon="icon-eda-ellipse"
								class="toolbarbutton icon-eda-ellipse" data-i18n-attr="title"></div>
							<div title="饼形" cmd="draw(pie)" icon="icon-eda-pie"
								class="toolbarbutton icon-eda-pie" data-i18n-attr="title"></div>
							<div title="图片" cmd="editImageHref" icon="icon-eda-image"
								class="toolbarbutton icon-eda-image" data-i18n-attr="title"></div>
							<div title="拖移" cmd="dragMoveSwitch" icon="icon-eda-drag"
								class="toolbarbutton icon-eda-drag" data-i18n-attr="title"></div>
							<div title="画布原点" cmd="draw(origin)" icon="icon-eda-origin"
								class="toolbarbutton icon-eda-origin" data-i18n-attr="title"></div>
						</div>
					</div>
				
				<div id="win_conn"
					style="position: absolute; display: none; background-color: #cccccc">conn</div>
			</div>
		</div>
		<div class="right " style="background-color: #333333">
		  <i id="edit_panel_btn"  class="fa fa-pencil-square-o fa-3x lr_btn"></i>
		
			
		</div>

	</div>


<div id='edit_panel' style="display:none;border: 1; font: 15; position: absolute; top: 3px; width: 30%; height: 90%; right: 50px; background-color: window; z-index: 60000; overFlow0: auto">
	<div style="background-color: rgb(200, 200, 200); border: 1; border-bottom-style: inset; margin: 1">
		[main]</div>
	<div
		style="background-color: olive; color: white; border: 1; border-bottom-style: inset; margin: 1; text-align: left">
		名称<input class="layui-btn layui-btn-primary layui-btn-sm" id="new_name" name="new_name" type="text" size="5" />
		 <input class="layui-btn layui-btn-primary layui-btn-sm" name='save' type='button' value='新建' onclick="add_new()" title="ctrl+n" /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		 <input class="layui-btn layui-btn-primary layui-btn-sm" name='save' type='button' value='保存' onclick="btn_save()" title="ctrl+b" />
		 <input class="layui-btn layui-btn-primary layui-btn-sm" name='load' type='button' value='装载' onclick="btn_load()" title="ctrl+b" />
		 <input class="layui-btn layui-btn-primary layui-btn-sm" name='load' type='button' value='装载Unit' onclick="btn_load_unit()" title="ctrl+b" /> 
		 <input class="layui-btn layui-btn-primary layui-btn-sm" name='load' type='button' value='Fit Win' onclick="draw_fit()" title="ctrl+b" />

	</div>

	<div id="p_info" style="background-color: grey; height: 20">&nbsp;</div>

	<div id="tabs-3" style="overflow: scroll; height: 90%">
		<input type="button" value="Apply" onclick="do_apply()" class="layui-btn layui-btn-primary layui-btn-sm" />
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Group"
			onclick="do_add_di('oc.DrawItemGroup')" /> <input type="button"
			value="Win" onclick="do_add_di('oc.iott.Win')" /> <input
			type="button" value="Add Line" onclick="do_add_di('oc.di.DILine')" />
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Add Rect"
			onclick="do_add_di('oc.di.DIRect')" /> <input type="button"
			value="Add Txt" onclick="do_add_di('oc.di.DITxt')" /> <input
			type="button" value="Add Img" onclick="do_add_di('oc.di.DIImg')" />
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Add Arc"
			onclick="do_add_di('oc.di.DIArc')" /> <input type="button"
			value="Add Pts Rect"
			onclick="do_add_di('oc.di.DIPts',{pts_tp:'rect'})" /> <input
			type="button" value="Add Pts Diamond"
			onclick="do_add_di('oc.di.DIPts',{pts_tp:'diamond'})" /> <br>
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Add Unit Ins [u1]"
			onclick="do_add_unit_ins('u1')" />
		<div id='edit_props' style="height: 100%"></div>
	</div>
</div>
			
	<script>
var panel = null;
var editor = null ;

var loadLayer = null ;
var intedit =null;


function on_panel_mousemv(p,d)
{
	$("#p_info").html("["+p.x+","+p.y+"] - ("+Math.round(d.x*10)/10+","+Math.round(d.y*10)/10+")");
}

function init_panel()
{
	panel = new oc.DrawPanel("main_panel",{
		on_mouse_mv:on_panel_mousemv
	});
	
	if(panel.init_panel)
		panel.init_panel();

	var lay1 = new oc.DrawLayer("lay1") ;
	var di0 = new oc.di.DIRect();
	di0.inject({id:"id1",x:70,y:30,w:20,h:200})
	var d0 = new oc.di.DIRect();
	d0.inject({id:"id2",x:170,y:30,w:100,h:20,fillColor:"rgba(0,213,230,0.5)",border:1})
	var d00 = new oc.di.DITxt();
	d00.inject({id:"id3",x:170,y:30,txt:"文本文本"})
	var ds = new oc.DrawItems({id:"g1"});
	ds.addItem(di0);
	ds.addItem(d0);
	ds.addItem(d00);
	lay1.addItem(ds) ;
	//panel.addLayer(lay1) ;
	
	//var str = JSON.stringify(lay1.extract()) ;
	//console.log(str);
	
	var lay2 = new oc.DrawLayer("lay2") ;
	var di1=new oc.di.DIRect();
	di1.inject({id:"id2",x:20,y:30,w:200,h:400,fillColor:"rgba(0,213,20,0.5)",border:0});
	//di1.inject_item({x:20,y:30}) ;
	
	lay2.addItem(di1) ;
	//panel.addLayer(lay2) ;
	
	//str = JSON.stringify(lay2.extract()) ;
	//console.log(str);
	
	panel.on_draw();
	
	
	
	//test_load();
	
	
}

if(oc.chart_init)
{
	oc.chart_init(function(){
		init_panel();
	});
}
else
{
	init_panel();
}

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

function store_add_db()
{		
		dlg.open("./store/db_edit.jsp",
				{title:"新增数据库",w:'500px',h:'400px'},
				['确定','取消'],
				[
					function(dlgw)
					{
						dlgw.edit_submit(function(bsucc,ret){
							 if(!bsucc)
							 {
								 dlg.msg(ret) ;
								 enable_btn(true);
								 return;
							 }
							 console.log(ret);
							 dlg.close();
							 
					 	});
					},
					function(dlgw)
					{
						dlg.close();
					}
				]);
}


function btn_save()
{
	var pm = {} ;
	pm.op="save" ;
	pm.id="t1";
	pm.txt = JSON.stringify(loadLayer.extract()) ;
	$.ajax({
        type: 'post',
        url:'t_ajax.jsp',
        data: pm,
        async: true,  
        success: function (result) {  
        	//layer.close(index);
        	//dlg.msg(result) ;
        	layer.msg(result); 
        },
        error:function(req,err,e)
        {
        	dlg.msg(e);
        }
    });
}

function btn_load()
{
	send_ajax("t_ajax.jsp","id=t1",function(bsucc,ret){
		//alert(ret);
		var lay = new oc.DrawLayer();
		//alert(ret);
		lay.inject(ret) ;
		//console.log(JSON.stringify(lay.extract())) ;
		panel.addLayer(lay) ;
		
		editor = new oc.DrawEditor("edit_props",panel,{
			
		}) ;
		editor.init_editor();
		
		intedit = new oc.interact.InteractEditLayer(panel,lay);
		//intedit.setSelectedListener(editor);
		panel.setInteract(intedit);
		loadLayer = lay;
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

function set_dyn_dt()
{
	if(loadLayer==null)
		return ;
	r += 0.1 ;
	var v = {dir:{rotate:r}};
	loadLayer.setDynData(v);
}

setInterval("set_dyn_dt()",100);

function btn_load_unit()
{
	send_ajax("t_ajax.jsp","id=u_u1",function(bsucc,ret){
		//alert(ret);
		oc.DrawUnit.addUnitByJSONStr(ret);
		
	}) ;

}


$(function() {
	//$( "#tabs" ).tabs();
});
//////////edit panel
$(document).ready(function()
	{

  		//$('#edit_panel').hide();
  		$('#edit_panel_btn').click(function()
  		{
			$('#edit_panel').slideToggle();
			$(this).toggleClass("cerrar");
    	});

  		btn_load();
	});





function txt_auto_clk()
{
	//alert(DrawUtil.create_new_tmp_id()) ;
	var r =new oc.base.Rect(10,10,20,50);
	console.log(r.toStr());
	r = new oc.base.Rect(1,2,5,5);
	console.log(r.toStr()) ;
	r.x=100;
	r = new oc.base.Rect(r);
	console.log(r.toStr()) ;
	
	//var pts = new oc.base.Pts();
	var pts = new oc.base.Pts([
		[100,100],[10,20],[30,30]
	]);
	console.log(pts.toStr()+" rect="+pts.getBoundingBox().toStr());
	
	var py = new oc.base.Polygon([
		[10,10],[10,20],[30,30]
		]);
	console.log(py.toStr()+" rect="+py.getBoundingBox().toStr());
	console.log("(10,15) in py="+py.contains(10,15));
	console.log("(5,15) in py="+py.contains(5,15));
	console.log("(11,15) in py="+py.contains(11,15));
	console.log("(14.9999999,15) in py="+py.contains(14.999999,15));
	console.log("(15.0,15) in py="+py.contains(15.0,15));
}

$(window).resize(function(){
	panel.updatePixelSize() ;
	});
</script>
</body>
</html>