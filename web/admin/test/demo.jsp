<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				java.net.*"%>
<%
	
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
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>

<script src="/_js/dlg_layer.js"></script>
	<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script src="/opencharts/dist/oc.js"></script>

<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
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

.top_menu_close {
    font-family: Tahoma;
    border: solid 2px #ccc;
    padding: 0px 5px;
    text-align: center;
    font-size: 12px;
    color: blue;
    position: absolute;
    top: 2px;
    line-height: 14px;
    height: 14px;
    width: 26px;
    border-radius: 14px;
    -moz-border-radius: 14px;
    background-color: white;
}

.top_menu_left{
	position:absolute;z-index: 50000;width: 25;height:25;TOP:100px;right:0px;
	text-align: center;
	font-size: 12px;
 font-weight: bold;
 background-color:#4770a1;
 color: #eeeeee;
 line-height: 35px;
 border:2px solid;
border-radius:5px;
//box-shadow: 5px 5px 2px #888888;
}

.top_win_left
{
border:solid 3px gray;		
background-color:silver;
top:0;
left:30;
height:230;
width:830;
padding:1px;
line-height:21px;
border-radius:15px;
-moz-border-radius:15px;
box-shadow:0 5px 27px rgba(0,0,0,0.3);
-webkit-box-shadow:0 5px 27px rgba(0,0,0,0.3);
-moz-box-shadow:0 5px 27px rgba(0,0,0,0.3);
_position:absolute;
_display:block;
z-index:10000;
}
</style>

</head>
<script type="text/javascript">


</script>
<body class="layout-body">

		<div class="left " style="background-color: #333333">
			<i class="fa fa-cube fa-3x lr_btn" id="topm_filter_op"></i>
			
			<i class="fa fa-database fa-3x lr_btn" ></i>
			<i class="fa fa-cog fa-3x lr_btn_btm"></i>
		</div>
		<div class="mid">
			<div id="main_panel" style="border: 0px solid #000; width: 100%; height: 100%; background-color: #1e1e1e" ondrop0="drop(event)" ondragover0="allowDrop(event)">
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
				
				<div id="win_act_conn" style="position: absolute; display: none; background-color: #cccccc;z-index:1">
					<div class="layui-btn-group" style="width:40px">
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" title="新增接入"  onclick="conn_add()">
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

					
				
		</div>
		<div class="right " style="background-color: #333333">
		  <i id="edit_panel_btn"  class="fa fa-pencil-square-o fa-3x lr_btn"></i>
		  <i id="lr_btn_fitwin"  class="fa fa-crosshairs fa-3x lr_btn"></i>
			
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
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Group" onclick="do_add_di('oc.DrawItemGroup')" />
        <input type="button" value="Win" onclick="do_add_di('oc.iott.Win')" />
        <input type="button" value="Add Line" onclick="do_add_di('oc.di.DILine')" />
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Add Rect" onclick="do_add_di('oc.di.DIRect')" />
		<input type="button" value="Add Txt" onclick="do_add_di('oc.di.DITxt')" />
        <input type="button" value="Add Img" onclick="do_add_di('oc.di.DIImg')" />
        <input type="button" value="Add Icon" onclick="do_add_di('oc.di.DIIcon')" />
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Add Arc" onclick="do_add_di('oc.di.DIArc')" />
		<input type="button" value="Add Pts Rect" onclick="do_add_di('oc.di.DIPts',{pts_tp:'rect'})" />
	    <input type="button" value="Add Pts Diamond" onclick="do_add_di('oc.di.DIPts',{pts_tp:'diamond'})" /> <br>
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Add Unit Ins [u1]" onclick="do_add_unit_ins('u1')" />
		<div id='edit_props' style="height: 100%"></div>
	</div>
</div>


<div id='topm_filter_panel' class="top_win_left" style="position:absolute;display:none;z-index:1000;left:45px;" pop_width="430">

<div class="layui-tab"><span id="topm_filter_x" class="top_menu_close" style="position:absolute;top:1px,right:10px">X</span>
  <ul class="layui-tab-title">
    <li class="layui-this">图元</li>
    <li>连接</li>
    <li>图标</li>
  </ul>
  <div class="layui-tab-content">
    <div class="layui-tab-item layui-show">
    	<iframe id="plug_unit" width="95%" height="510" src="unit/unit_list.jsp" style="overflow: hidden;width:100%;margin: 0"> </iframe>
	</div>
    <div class="layui-tab-item">
      <iframe id="plug_conn" width="95%" height="510" src="unit/unit_list.jsp" style="overflow: hidden;width:100%;margin: 0"> </iframe>
	</div>
    <div class="layui-tab-item">
    	<iframe id="plug_icon" width="95%" height="510" src="pic/icon_fa.jsp" style="overflow: hidden;width:100%;margin: 0"> </iframe>
    </div>
  </div>
</div>
	
</div>

<script>

layui.use('element', function(){
  var element = layui.element;
  
  //…
});

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
	
	panel.init_panel();

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

function conn_add()
{		
	dlg.open("./conn/conn_edit.jsp",
			{title:"新增接入",w:'500px',h:'400px'},
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

function load_all_unit()
{
	send_ajax("unit/unit_ajax.jsp","op=load_all",function(bsucc,ret){
		if(!bsucc&&ret.indexOf("[")!=0)
		{
			dlg.msg("load allunit err:"+ret);
			return ;
		}
		var ob = null ;
		eval("ob="+ret);
		var items=[];
		for(var item of ob)
		{
			oc.DrawUnit.addUnitByJSON(item);
		}
		//cb();
	}) ;

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
		
		load_all_unit();
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
	var rv={rotate:r}
	var rvr={rotate:-r}
	var v = {dir:rv,r1:rv,r2:rvr};
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


//////////edit panel
$(document).ready(function()
{
	$('#edit_panel_btn').click(function()
	{
		$('#edit_panel').slideToggle();
		$(this).toggleClass("cerrar");
   	});
 		
	$('#lr_btn_fitwin').click(function()
	{
		draw_fit();
   	});

 	btn_load();
 	
 	init_top_menu();
});

function slide_toggle(obj)
{
	if(obj.attr('topm_show')=='1')
	{
		obj.animate({width: '0px', opacity: 'hide'}, 'normal',function(){ obj.hide();});
		obj.attr('topm_show',"0") ;
		return 0 ;
	}
	else
	{
		obj.animate({width: obj.attr('pop_width'), opacity: 'show'}, 'normal',function(){ obj.show();});
		obj.attr('topm_show',"1") ;
		return 1 ;
	}
}

function init_top_menu()
{
	$('#topm_filter_panel').hide();
	$('#topm_filter_op').click(function()
	{
		top_menu_hide_other('filter');
		//$('#topm_filter_panel').slideToggle();
		var r = slide_toggle($('#topm_filter_panel'));
		$(this).toggleClass("top_menu_tog");
		//fire_gis_plug_showhide('filter',r)
  	});
  	$('#topm_filter_x').click(function()
	{
		slide_toggle($('#topm_filter_panel'));
		//$(this).toggleClass("top_menu_tog");
		//fire_gis_plug_showhide('filter',0)
  	});

}

function top_menu_hide_other(pn)
{
	if($('#topm_filter_panel').attr('topm_show')=='1' && 'filter'!=pn)
	{
		slide_toggle($('#topm_filter_panel'));
	}
}
var resize_cc = 0 ;
$(window).resize(function(){
	panel.updatePixelSize() ;
	resize_cc ++ ;
	if(resize_cc<=1)
		draw_fit();
	});
</script>
</body>
</html>