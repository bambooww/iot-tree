<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
				java.net.*"%>
<%
	if(!Convert.checkReqEmpty(request, out, "tabid","catid","id"))
		return ;
	//String op = request.getParameter("op");
	String tabid = request.getParameter("tabid");
	String catid = request.getParameter("catid");
	String id = request.getParameter("id");
	CompManager cm = CompManager.getInstance() ;
	CompCat cc = cm.getCatById(catid) ;
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
	String resnodeid = ci.getResNodeUID();
%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>HMI Component Editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
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
-moz-user-select : none;
-webkit-user-select: none;
}
.top {
	position: fixed;
	
	left: 0;
	top: 0;
	bottom: 0;
	z-index: 999;
	height: 45px;
	width:100%;
	text-align: left;
	margin:0px;
	padding:0px;
	overflow: hidden
}


.left {
	position: fixed;
	float: left;
	left: 0;
	top: 45px;
	bottom: 0;
	z-index: 999;
	width: 45px;
	overflow-x: hidden
}


.left_pan {
	position: fixed;
	float: left;
	left: 45px;
	top: 45px;
	bottom: 0;
	z-index: 999;
	width: 145px;
	overflow-x: hidden
}

.right {
	position: fixed;
	float: right;
	right: 0;
	top: 45px;
	bottom: 0;
	z-index: 999;
	width: 250px;
	height: 100%;
	overflow-x: hidden
}

.mid {
	position: absolute;
	left: 45px;
	right: 250px;
	top: 45px;
	bottom: 0;
	z-index: 998;
	width: auto;
	overflow: hidden;
	box-sizing: border-box
}

.top_btn
{
	color:#009999;
	margin-top: 5px;
	margin-left:20px;
	cursor: pointer;
}

.top i:hover
{
color: #fdd000;
}

.lr_btn
{
	margin-top: 10px;
	color:#009999;
	cursor: pointer;
}

.lr_btn_div
{
	margin-top: 0px;
	color:#858585;
	background-color:#eeeeee;
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
color: #fdd000;
}

.lr_btn i:hover
{
color: #fdd000;
}

.right i:hover{
color: #ffffff;
}

.props_panel_edit
{
	position0: absolute;
	left: 0px;
	right: 0px;
	top: 18px;
	bottom0: 50px;
	height:80%
	z-index: 998;

	overflow-y: auto;
	vertical-align:top;
	box-sizing: border-box
}

.props_panel_pos
{
	position: absolute;
	bottom: 50px;
	
	z-index: 998;
	box-sizing: border-box
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

.left_panel_win
{
position:absolute;display:none;z-index:1000;left:45px;
background-color: #eeeeee;
top:0px;height:100%;
}
.left_panel_bar
{
height:30px;
}


.layui-tab {
    margin: 0px;
    padding:0px;
    text-align: left!important;
    height:35px;
}


.layui-tab-content {
    padding: 0px;
}

.layui-tab-title .layui-this{
background-color: #aaaaaa;
top:0px;
margin:0px;
padding-top:0px;
padding-bottom:0px;

}

.toolbox
{
		position:absolute;
		top:25px;
		left:25px;
		width:200px;
		z-index:1000;
		-moz-border-radius: 8px 0 0 0;
		-webkit-border-radius: 8px 0 0 0;
		border-radius: 8px 0 0 0;		
}
.toolbox .title
{
	position:relative;
		background: #808080;
		height:30px;
		margin-left:0px;
		margin-top:0px;
		-moz-border-radius: 8px 0 0 0;
		-webkit-border-radius: 8px 0 0 0;
		border-radius: 8px 0 0 0;
	
		}
.toolbox .content
{
	height:100%;
}

</style>
</head>
<script type="text/javascript">


</script>
<body class="layout-body">
<div class="top " style="background-color: #007ad4;color:#ffffff;">
<div style="float: left;position:relative;left:0px;margin-left:5px;top:2px;font: 30px solid;font-weight:600;font-size:16px;color:#d6ccd4">
 <img src="../inc/logo1.png" width="40px" height="40px"/>IOTTree HMI Component Editor </div>
 <div style="float: left;position:relative;left:100px;margin-left:5px;top:2px;font: 25px solid">
		<%=cc.getTitle()%>-<%=ci.getTitle()%>
		</div>
 <div style="float: right;margin-right:10px;margin-top:10px;font: 20px solid;color:#fff5e2">
			<i class="fa fa-floppy-o fa-lg top_btn" onclick="tab_save()" ></i>
		    <i id="lr_btn_fitwin"  class="fa fa-crosshairs fa-lg top_btn"></i>
</div>
</div>

		<div class="left " style="background-color: #aaaaaa">
			<div id="leftcat_basic_di" onclick="leftcat_sel('basic_di','Basic')"><i class="fa fa-circle-o fa-3x lr_btn" ></i><br>Basic</div>
			<div id="leftcat_basic_icon" onclick="leftcat_sel('basic_icon','Basic Icons')"><i class="fa fa-picture-o fa-3x lr_btn"></i><br>Icon</div>
			<div id="leftcat_pic" onclick="leftcat_sel('pic','Pictures Lib',500)"><i class="fa fa-cubes fa-3x lr_btn"></i><br>Pic Lib</div>
		</div>
		<div id="left_panel" class="left_panel_win" pop_width="300px" >
			<div class="left_panel_bar" >
				<span id="left_panel_title" style="font-size: 20px;">Basic Shape</span><div onclick="leftcat_close()" class="top_menu_close"  style="position:absolute;top:1px;right:10px;top:2px;">X</div>
			</div>
			<iframe id="left_pan_iframe" src="" style="width:100%;height:90%;overflow:hidden;margin: 0px;border:0px;padding: 0px" ></iframe>
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
		<div class="right " style0="background-color: #eeeeee;display:flex;flex-direction: column;">
			<div style="position0: absolute; width: 100%; height:100%; border:1 solid;border-color: red">
				<div class="layui-tab">
  <ul class="layui-tab-title">
    <li class="layui-this">Properties</li>
    <li>Events</li>
  </ul>
  <div class="layui-tab-content">
    <div class="layui-tab-item layui-show">
    	<div  id='edit_props'  style="width:100%;height:600px;overflow: auto;"></div>
	</div>
    <div class="layui-tab-item">
      <div  id='edit_events'  style="width:100%;height:600px;overflow: auto;"></div>
	</div>
   
  </div>
</div>
<%--
			    <div id='edit_panel' style="border: 1 solid; font: 15; position: absolute; top: 20px; width: 100%; bottom:0px; background-color: window; z-index: 60000;overFlow: auto;">
					 <div id='edit_props' ></div>
					 <div id='edit_events' ></div>
				</div>
				 --%>
			</div>
			
		</div>
		
		<div id="inter_editor" class="toolbox">
				<div class="title"><h3>Interface</h3></div>
				<div class="content" style="height:300px"> 
							<div id='inter_prop_panel' style="border: 1; font: 15; flex: 1;width: 100%;  background-color: window; z-index: 60000; ">
						<div style="position0:absolute;top:0px;height:20px;background-color: grey;width:100%">
						Interface Properties<button onclick="inter_prop_edit()">+Add</button><button onclick="inter_prop_test()">Test</button></div>
						<div id="inter_prop_list" style="position0:absolute;top:20px;height:120px;width:100%">
							
						</div>
						
					</div>
					<div id='inter_event_panel' style="border: 1; font: 15; flex: 1;width: 100%;  background-color: window; z-index: 60000; ">
						<div style="position0:absolute;top:0px;height:20px;background-color: grey;width:100%">
						Interface Events<button onclick="inter_event_edit()">+add</button></div>
						<div id="inter_event_list" style="position0:absolute;top:20px;height:120px;width:100%">
							
						</div>
						
					</div>
					<div id="p_info" style="position:absolute;bottom:0px;width:100%;background-color: #cccccc; height: 30px" class="props_panel_pos">&nbsp;</div>
				</div>
		</div>
	</div>




<script>

function toolbox_init(which){//which参数指定的是哪一个窗口的id，比如"#dialog"
	var offestLeft;
	var offestTop;
	var right=false;
	$(which).mousedown(function(e){
				var x=e.clientX;
				var y=e.clientY;
				var styleLeft=$(which).css("left");
				var styleTop=$(which).css("top");
				offestLeft=x-parseInt(styleLeft);
				offestTop=y-parseInt(styleTop);
				right=true;
										  });
	
	$(which).mousemove(function(e){
									 if(right){
				var nowLeft=e.clientX-offestLeft;
				var nowTop=e.clientY-offestTop;
				$(which).css("left",nowLeft).css("top",nowTop);}
										  });
	$(which).mouseup(function(e){				   
					right=false;			   
								   });
}
toolbox_init("#inter_editor");

var tab_id = "<%=tabid%>" ;
var catid="<%=catid%>";
var itemid = "<%=id%>"
var editname = "<%=ci.getEditorName()%>" ;
var res_node_id = "<%=resnodeid%>" ;

var ctrl_items=[] ;

layui.use('element', function(){
  var element = layui.element;
  
  //…
});



var panel = null;
var editor = null ;

var loadLayer = null ;
var intedit =null;

var hmiModel=null;
var hmiView=null;

function on_panel_mousemv(p,d)
{
	$("#p_info").html("["+p.x+","+p.y+"] - ("+Math.round(d.x*10)/10+","+Math.round(d.y*10)/10+")");
}

function init_iottpanel()
{
	hmiModel = new oc.hmi.HMICompModel({
		comp_url:"comp_ajax.jsp?op=comp_txt&catid="+catid+"&id="+itemid,
	});
	
	panel = new oc.hmi.HMICompPanel(itemid,res_node_id,"main_panel",{
		on_mouse_mv:on_panel_mousemv,
		on_model_chg:on_model_chg
	});
	editor = new oc.DrawEditor("edit_props","edit_events",panel,{
		plug_cb:editor_plugcb
	}) ;
	hmiView = new oc.hmi.HMICompView(hmiModel,panel,editor,{
		copy_paste_url:"util/copy_paste_ajax.jsp",
		loaded_cb:()=>{
			inter_refresh();
		}
	});

	loadLayer = hmiView.getLayer();
	intedit = hmiView.getInteract();
	hmiView.init();
}

var editor_plugcb_pm=null ;

function editor_plugcb(jq_ele,tp,di,name,val)
{
	editor_plugcb_pm = {editor:editname,editor_id:itemid,catid:catid,compid:itemid,di:di,name:name,val:val} ;

	if(tp.indexOf("event_")==0)
	{
		dlg.open("../util/di_editplug_"+tp+".jsp?sjs=false&compid="+itemid,
				{title:"Edit Event",w:'500px',h:'400px'},
				['Ok','Cancel'],
				[
					function(dlgw)
					{
						var ret = dlgw.editplug_get() ;
						 var cjs = ret.clientjs ;
						 var sjs = ret.serverjs;
						 if(cjs==null)
							 cjs = "" ;
						 if(sjs==null)
							 sjs = "" ;
						 di.setEventBinder(name,cjs,sjs) ;
						 editor.refreshEventEditor();
						 dlg.close();
					},
					function(dlgw)
					{
						dlg.close();
					}
				]);
	}
	else
	{
		dlg.open("../util/di_editplug_"+tp+".jsp?res_node_id="+res_node_id,
				{title:"Edit Properties",w:'500px',h:'400px'},
				['Ok','Cancel'],
				[
					function(dlgw)
					{
						var ret = dlgw.editplug_get() ;
						var v = ret.v ;
						jq_ele.val(v) ;
						editor.applyUI2SelectedItem();
						dlg.close();
					},
					function(dlgw)
					{
						dlg.close();
					}
				]);
	}
	
}

function inter_prop_test()
{
	dlg.open("comp_inter_prop_tester.jsp?catid="+catid+"&id="+itemid,
			{title:"Component Tester",w:'500px',h:'400px'},
			['Close'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function inter_refresh()
{
	var compinter = loadLayer.getCompInter() ;
	var tmps = "" ;
	for(var ci of compinter.getInterProps())
	{
		tmps += "<div>"+ci.t+"["+ci.n+"] <a href='javascript:inter_prop_edit(\""+ci.n+"\")'>edit</a> <a href='javascript:inter_prop_del(\""+ci.n+"\")'>del</a></div>";
	}
	$("#inter_prop_list").html(tmps);
	
	tmps="" ;
	for(var ci of compinter.getInterEvents())
	{
		tmps += "<div>"+ci.t+"["+ci.n+"] <a href='javascript:inter_event_edit(\""+ci.n+"\")'>edit</a> <a href='javascript:inter_event_del(\""+ci.n+"\")'>del</a></div>";
	}
	$("#inter_event_list").html(tmps);
}

function inter_prop_del(n)
{
	loadLayer.getCompInter().setInterProp(n,null);
	inter_refresh();
}
function inter_event_del(n)
{
	loadLayer.getCompInter().setInterEvent(n,null);
	inter_refresh();
}

function inter_prop_edit(n)
{
	var tt = "Add Interface Property" ;
	if(n==undefined||n==null)
		n = "" ;
	else
		tt = "Edit Interface Property" ;
	dlg.open("comp_inter_prop_edit.jsp?n="+n+"&catid="+catid+"&id="+itemid,
			{title:tt,w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 var n = ret.n ;
						 if(n==undefined||n==null||n=="")
						 {
							 dlg.msg("invalid property item") ;
							 return ;
						 }
						 //var tp = ret.tp ;
						 loadLayer.getCompInter().setInterProp(n,ret);
						 inter_refresh();
						 dlg.close();
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function inter_event_edit(n)
{
	var tt = "Add Interface Event" ;
	if(n==undefined||n==null)
		n = "" ;
	else
		tt = "Edit Interface Event" ;
	dlg.open("comp_inter_event_edit.jsp?n="+n+"&catid="+catid+"&id="+itemid,
			{title:tt,w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 var n = ret.n ;
						 if(n==undefined||n==null||n=="")
						 {
							 dlg.msg("invalid event item") ;
							 return ;
						 }
						 loadLayer.getCompInter().setInterEvent(n,ret);
						 inter_refresh();
						 dlg.close();
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
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

function do_add_pts(tp,opts)
{
	if(intedit==null)
		return;
	if(!intedit.setOperAddPts(tp,opts))
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

function on_model_chg()
{
	tab_notify();
}

function tab_save()
{
	var pm = {} ;
	pm.op="comp_txt_save" ;
	pm.catid=catid;
	pm.id=itemid;
	pm.txt = JSON.stringify(loadLayer.extract(null)) ;
	oc.util.doAjax("comp_ajax.jsp",pm,(bsucc,ret)=>{
        dlg.msg(ret);
        if("save ok"==ret)
        {
        	panel.setModelDirty(false) ;
        	tab_notify();
        }
        	
    });
}

function tab_notify()
{
	//parent.tab_notify(tab_id);
}

function tab_st()
{
	return {tabid:tab_id,dirty:panel.isModelDirty()} ;
}

function draw_fit()
{
	if(loadLayer==null)
		return ;
	loadLayer.ajustDrawFit();
}

var bInRefresh=false;
var lastRefreshDT = -1 ;

function refresh_dyn()
{
	if(bInRefresh)
		return ;
	if(new Date().getTime()-lastRefreshDT<2000)
		return ;
	try
	{
		bInRefresh = true;
		hmiModel.refreshDyn(function(){
			lastRefreshDT = new Date().getTime();
			bInRefresh = false;
		});
	}
	finally
	{
		
	}
}


layui.use('form', function(){

});


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

 	init_iottpanel();
 	
 	//init_top_menu();
});

function slide_toggle(obj,w)
{
	if(obj.attr('topm_show')=='1')
	{
		obj.animate({width: '0px', opacity: 'hide'}, 'normal',function(){ obj.hide();});
		obj.attr('topm_show',"0") ;
		return 0 ;
	}
	else
	{
		obj.animate({width: w, opacity: 'show'}, 'normal',function(){ obj.show();});
		obj.attr('topm_show',"1") ;
		return 1 ;
	}
}

function hide_toggle(obj)
{
	obj.hide();
	obj.attr('topm_show',"0") ;
}



var left_cur = null ;

function leftcat_sel(n,t,w)
{
	if(w==undefined)
		w = "300px" ;
	else
		w = w+"px" ;
	if(left_cur!=null)
	{
		//slide_toggle($('#left_panel'));
		hide_toggle($('#left_panel'))
		if(left_cur==n)
		{//close only		
			$('.lr_btn_div').removeClass("lr_btn_div");
			left_cur=null ;
			return ;
		}
	}
	
	//if()
	left_cur=n;
	$('.lr_btn_div').removeClass("lr_btn_div");
	$("#leftcat_"+n).addClass("lr_btn_div") ;
	$("#left_panel_title").html(t) ;
	if("basic_icon"==n)
		document.getElementById("left_pan_iframe").src="../pic/icon_fa.jsp" ;
	else
		document.getElementById("left_pan_iframe").src="hmi_left_"+n+".jsp" ;
	
	//top_menu_hide_other('filter');
	//$('#left_panel').hide();
	//$('#topm_filter_panel').slideToggle();
	var r = slide_toggle($('#left_panel'),w);
	//$(this).toggleClass("top_menu_tog");
}

function leftcat_close()
{
	$('.lr_btn_div').removeClass("lr_btn_div");
	left_cur=null ;
	slide_toggle($('#left_panel'));
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