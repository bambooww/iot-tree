<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page import="java.util.*,
				java.io.*,
				java.util.*,
				java.net.*"%>
<%@ taglib uri="wb_tag" prefix="wbt"%>
<%
String rid = request.getParameter("rid") ;

%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title></title>
		
		    <script src="/_js/jquery.min.js"></script>
	<script src="/_js/bootstrap/js/bootstrap.min.js"></script>

	
		<script type="text/javascript" src="/_js/jquery_ui/js/jquery-ui-1.8.10.custom.min.js"></script>
		<link type="text/css" href="/_js/jquery_ui/css/ui-lightness/jquery-ui-1.8.10.custom.css" rel="stylesheet" />
		<script type="text/javascript" src="/_js/ajax.js"></script>
	<script src="/_js/layer/layer.js"></script>
<script src="/_js/dlg_layer.js"></script>

<script src="/opencharts/dist/oc.js"></script>



<%--
<script src="_chart/oc.js"></script>
 <script src="_chart/draw_panel.js"></script>
 <script src="_chart/draw_layer.js"></script>
 <script src="_chart/draw_util.js"></script>
<script src="_chart/draw_item.js"></script>
<script src="_chart/draw_interact.js"></script>
<script src="_chart/di/di_common.js"></script>
<script src="_chart/di/di_rect.js"></script>
<script src="_chart/di/di_txt.js"></script>
<script src="_chart/di/di_line.js"></script>
<script src="_chart/draw_edit.js"></script>
<script src="_chart/interact/oper_drag.js"></script>
<script src="_chart/interact/interact_editlayer.js"></script>


 --%>


<style>
body {margin:0px;padding:0px;font-size:12px;text-align:center;}
body > div {text-align:left; margin-right:auto; margin-left:auto;}
body > svg {text-align:left; margin-right:auto; margin-left:auto;}
.content{width:100%;}
.content .left{float:left; width:49%;  margin:0px; }
.content .right{float:right;width:49%;margin:0px}
.dragtt{padding:5px;width:95%; margin-bottom:2px; border:2px #ccc; background-color:#eee;}
.draglist{float:left;padding:2px;width0:100px; margin-bottom:2px; border:2px solid #ccc; background-color:#eee; cursor:move;}
.draglist:hover{border-color:#cad5eb; background-color:#f0f3f9;}
</style>
<style>
.left{position:fixed;float:left;left:0;top:0;bottom:0;z-index:999;width:50px;overflow-x:hidden}
.right{position:fixed;float:right;right:0;top:0;bottom:0;z-index:999;width:400px;height:100%;overflow-x:hidden}
.mid{position:absolute;left:50px;right:400px;top:0;bottom:0;z-index:998;width:auto;overflow:hidden;box-sizing:border-box}
.layout-body{overflow:hidden}

.oc_edit_item
{
margin-bottom: 5px;
    clear: both;
}
.oc_edit_item label
{
    float: left;
    display: block;
    padding0: 9px 15px;
    width: 80px;
    font-weight: 400;
    line-height: 20px;
    text-align: right;
}

.oc_edit_item div
{
    margin-left: 110px;
}
</style>
	</head>
<script type="text/javascript">


</script>
<body class="layout-body" >

<div class="layout ">
 <div class="left " style="background-color: #dddddd">
   
 </div>
  <div class="mid">
    <div id="main_panel" style="border:0px solid #000;width:100%;height:100%;background-color:#2f2f2f"></div>
  </div>
<div class="right " style="background-color: #dddddd">

<div id='edit_panel' style="border:1;font:15;position: absolute;top:3;width:99%;height:100%;right:3px;background-color:window;z-index: 60000;overFlow0:auto">
	<div style="background-color:rgb(200,200,200);border: 1;border-bottom-style:inset;margin: 1">
	
		[main]
		
	</div>
	<div style="background-color:olive;color:white;border: 1;border-bottom-style:inset;margin: 1;text-align: left">
    	 名称<input id="new_name" name="new_name" type="text" size="5"/>
    	 <input name='save' type='button' value='新建' onclick="add_new()" title="ctrl+n"/>
    	 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     <input name='save' type='button' value='保存' onclick="btn_save()" title="ctrl+b"/>
     <input name='load' type='button' value='装载' onclick="btn_load()" title="ctrl+b"/>
     <input name='load' type='button' value='装载Unit' onclick="btn_load_unit()" title="ctrl+b"/>
     
     </div>
	<div style="background-color: silver;border: 1;border-bottom-style: outset;margin: 1">
          <!-- input type="button" value='test' onclick='test_fns()'/ -->
     <input type='button' value='增加' onclick="add_item_dlg()"/>
     <input type='button' value='复制' onclick="copy_item()"/>
     <input name='apply' type='button' value='应用' onclick='apply_chg()' title="ctrl+y"/>
     <input name='del' type='button' value='删除' onclick="del_item()" title="ctrl+q"/>
     
	<br/>
     <input id='draggable_lock' type='checkbox' onclick='lock_chg()'/>移动锁定
     <input id='choice_set' type='checkbox' onclick='choice_chg()'/>选中
     <input id='zoom_val' type='text' size=3 value='1'/><input type=button value='放大缩小' onclick='zoom_chg()'/>
     动画<input id='ani_ctrl' type='checkbox' checked='checked' onchange="ani_ctrl_chg()"/>
     <br><span id="xy_info">&nbsp;</span>
   </div>
   <div id="p_info" style="background-color: grey;height: 20">&nbsp;</div>
     <div id="tabs"  style="font-size: 15;height:76%">
	<ul style="background: #808080">
		<li><a href="#tabs-3">属性</a></li>
		<li><a href="#tabs-2">分组</a></li>
		<li><a href="#tabs-4">绑定</a></li>
		<li><a href="#tabs-5">事件</a></li>
		<li><a href="#tabs-1">总参数</a></li>
	</ul>
	<div id="tabs-1">
		<div id='edit_panel_props'>
     	
     </div>
	</div>
	<div id="tabs-2">
	

	</div>
	<div id="tabs-3" style="overflow: scroll;height:90%">
	  <input type="button" value="Apply" onclick="do_apply()" style=""/>
	  <input type="button" value="Add Line" onclick="do_add_di('oc.di.DILine')"/>
	  <input type="button" value="Add Rect" onclick="do_add_di('oc.di.DIRect')"/>
	  <input type="button" value="Add Txt" onclick="do_add_di('oc.di.DITxt')"/>
	  <input type="button" value="Add Img" onclick="do_add_di('oc.di.DIImg')"/>
	  <input type="button" value="Add Arc" onclick="do_add_di('oc.di.DIArc')"/>
	  <br>
	  <input type="button" value="Add Unit Ins [u1]" onclick="do_add_unit_ins('u1')"/>
     <div id='edit_props' style="height:100%">
     	
     </div>
	</div>
	<div id="tabs-4">
     <input type='button' value='dyn band' onclick=""/>
     <div id='dyn_binds'>
     	
     </div>
    </div>
    <div id="tabs-5">
     <div id='event_binds'>
     	
     </div>
    </div>


	</div>
   </div>
</div>

</div>
<script>
var panel = null;
var editor = null ;

var loadLayer = null ;
var intedit =null;


function on_panel_mousemv(p,d)
{
	$("#xy_info").html("["+p.x+","+p.y+"] - ("+d.x+","+d.y+")");
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

function do_add_di(dicn)
{
	if(intedit==null)
		return;
	if(!intedit.setOperAddItem(dicn))
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
        	dlg.msg(result) ;
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
		loadLayer = new oc.DrawLayer();
		//alert(ret);
		loadLayer.inject(ret) ;
		//console.log(JSON.stringify(lay.extract())) ;
		panel.addLayer(loadLayer) ;
		
		editor = new oc.DrawEditor("edit_props",panel,{
			
		}) ;
		editor.init_editor();
		
		intedit = new oc.interact.InteractEditLayer(panel,loadLayer);
		//intedit.setSelectedListener(editor);
		panel.setInteract(intedit);
		
	}) ;

}

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

	$( "#tabs" ).tabs();
    	//document.info_panel_btn
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