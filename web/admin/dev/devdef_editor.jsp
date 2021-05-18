<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.net.*,
				org.iottree.core.*,
				org.iottree.core.util.*
				"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out, "drv","cat","id"))
		return ;
	String catname = request.getParameter("cat");
	String drvname = request.getParameter("drv");
	String id = request.getParameter("id");
	DevDriver dd = DevManager.getInstance().getDriver(drvname) ;
	if(dd==null)
	{
		out.print("no driver found") ;
		return ;
	}
	DevCat cat = dd.getDevCatByName(catname) ;
	if(cat==null)
	{
		out.print("no cat found") ;
		return ;
	}
	DevDef dev = cat.getDevDefById(id);
	if(dev==null)
	{
		out.print("no dev found") ;
		return ;
	}
	String path = dev.getNodePath() ;
%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>IOTTree Device Definition Editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
<link rel="stylesheet" href="/_js/jstree/themes/default/style.min.css" />
	<script src="/_js/jstree/jstree.min.js"></script>
<script src="../js/ua_tree.js"></script>
<script src="../js/ua.js"></script>
<script src="../js/split.css"></script>
<script src="../js/vue.min.js"></script>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
-moz-user-select : none;
-webkit-user-select: none;
}
.top {
	position: fixed;
	
	left: 0;
	top: 0;
	bottom: 0;
	z-index0: 999;
	height: 45px;
	width:100%;
	text-align: left;
	margin:0px;
	padding:0px;
	overflow: hidden
}


.left {
	position: fixed;
	left: 0;
	top: 45px;
	bottom: 0;
	width: 245px;
	z-index:0;
	overflow-x: hidden
}

.divider
{
	position: fixed;
	left: 245px;
	top: 45px;
	bottom: 0;
	width: 5px;
	z-index:0;
	overflow-x: hidden;
	background-color: #111111;
	cursor: e-resize;
}


.mid {
	position: fixed;
	left: 250px;
	top: 45px;
	bottom: 0;
	width: auto;
	overflow: hidden;
	box-sizing: border-box;
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

.top_btn
{
	color:#fff5e2;
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
	z-index0: 998;

	overflow-y: auto;
	vertical-align:top;
	box-sizing: border-box
}

.props_panel_pos
{
	position: absolute;
	bottom: 50px;
	
	z-index0: 998;
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
	position:absolute;z-index0: 50000;width: 25;height:25;TOP:100px;right:0px;
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
z-index0:10000;
}

.left_panel_win
{
position:absolute;display:none;z-index0:1000;left:45px;
background-color: #eeeeee;
top:45px;height:100%;
}
.left_panel_bar
{
height:30px;
}

.layui-tab {
    margin: 0px;
    padding:0px;
    text-align: left!important;
    height:40px;
}
.layui-tab-title
{
margin-top:0px;
	margin-bottom:0px;
	height:40px;
	background-color: #ffffff
}
.layui-tab-content {
    padding: 0px;
}
.layui-tab-title .layui-this:after {
	border-width: 0px;
	    height: 35px;
}

ul.layui-tab-title li:nth-child(1) i{
display: none;
}

ul.layui-tab-title li:nth-child(2) i{
display: none;
}

ul.layui-tab-title li:nth-child(3) i{
display: none;
}

.layui-nav
{
	background-color: #f2f2f2;
}

.layui-nav-itemed>.layui-nav-child
{
background-color: #ffffff;
}

.hj-wrap {
    position: fixed;//relative;
    top:45px;
    width: 100%;
    height0: 600px;
    bottom:0px;
    margin0: 10px auto;
    clear: both;
    overflow: hidden;
}


.hj-transverse-split-div {
    position: relative;
    float: left;
    height: 100%;
    padding: 0px;
    overflow: hidden;
}

.hj-wrap .hj-transverse-split-label {
    position: absolute;
    right: 0;
    top: 0;
    float: left;
    width: 2px;
    height: 100%;
    display: block;
    cursor: ew-resize;
    background-color: #ccc;
    z-index: 9;
}

.hj-vertical-split-div {
    position: relative;
    border: 0px solid red;
    width: 99.9%;
    margin: 0 auto;

}

.hj-vertical-split-label {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 2px;
    display: block;
    cursor: ns-resize;
    background-color: #fff;
    z-index: 9;
}

.subwin{
      width:100%;
      height:100%;
      display:flex;
      flex-direction: column;
}
.subwin_toolbar{
        width:100%;
        height: 40px;
        border:solid 1px;
        font-size: medium;
		line-height: 40px;
 }
 .subwin_content{
        width:100%;
        flex:1;
}

.subitem{
      width:100%;
}
.subitem_toolbar{
        width:100%;
        height: 30px;
        color:#fff;
        border:solid 1px;
        font-size: small;
		line-height: 30px;
		background-color: #a9a9a9;
 }
 .subitem_content{
        width:100%;
}

#tree { float:left; min-width:319px; border-right:1px solid silver; overflow:auto; padding:0px 0; }
#tree .icon_dev {background:url('../inc/sm_icon_dev.png') 0 0 no-repeat;width:24px;height:24px; }
#tree .icon_ch {background:url('../inc/sm_icon_ch.png') 0 0 no-repeat; width:24px;height:24px;}
#tree .icon_tagg {background:url('../inc/sm_icon_tagg.png') 0 0 no-repeat; width:24px;height:24px;}

</style>

</head>
<script type="text/javascript">

</script>
<body class="layout-body">
<div class="top " style="background-color: #007ad4;color:#ffffff;">
<div style="float: left;position:relative;left:0px;margin-left:5px;top:2px;font: 30px solid;font-weight:600;font-size:16px;color:#d6ccd4">
 <img src="../inc/logo1.png" width="40px" height="40px"/>IOTTree Device Definition Editor </div>
 <div style="float: left;position:relative;left:100px;margin-left:5px;top:2px;font: 25px solid">
		<%=dev.getTitle()%>-<%=dev.getName() %>
		</div>
 <div style="float: right;margin-right:10px;margin-top:10px;font: 20px solid;color:#fff5e2">
			<i class="fa fa-floppy-o fa-lg top_btn" onclick="tab_save()" ></i>
			<i class="fa fa-cogs  fa-lg  top_btn" onclick="list_comps()"></i><span style="font: 20px solid">UI Library</span>
		    <i id="lr_btn_fitwin"  class="fa fa-crosshairs fa-lg top_btn"></i>
</div>
</div>
<div class='hj-wrap'>
		
        <div class="hj-transverse-split-div subwin" style="width:20%">
           <div class="subwin_toolbar">Browser</div>
           <div id="tree" class="subwin_content"></div>
            <label class="hj-transverse-split-label"></label>
        </div>
        
        <div class="hj-transverse-split-div" style="width:80%;background-color: #ebeef3">
           <div style="padding-left:10px;padding-right:0px;marign0:10px;height:100%;position:inherit;">
           
			<div class="layui-tab layui-tab-brief"  lay-filter="tab_hmi_editor" lay-allowclose="true" style="width:100%;height:100%">
			  <ul class="layui-tab-title">
			    <li class="layui-this">[Tags]</li>
			    <li >Properties</li>
			    <li>Device</li>
			    
			  </ul>
			  <div class="layui-tab-content" style="position:relative;bottom:0px;height:100%">
			    <div class="layui-tab-item layui-show"  style="position:relative;top:0px;bottom:0px;width:100%;height:100%">
			      <iframe id="if_tags" src="../tag/node_tags.jsp?tabid=main&path=<%=path %>" style="width:100%;height:100%;border:0px"></iframe>
				</div>
			    <div class="layui-tab-item"  style="position:relative;top:0px;bottom:0px;width:100%;height:100%">
			      <iframe id="if_prop" src="../ua/ui_prop.jsp?dlg=false&&tabid=drv&path=<%=path %>" style="width:100%;height:100%;border:0px"></iframe>
				</div>
				<div class="layui-tab-item"  style="position:relative;top:0px;bottom:0px;width:100%;height:100%">
			      <iframe id="if_device" src="../dev_device.jsp?tabid=dev&path=<%=path %>" style="width:100%;height:100%;border:0px"></iframe>
				</div>
				
 
			  </div>
			</div>
			 
		    </div>
            <label class="hj-transverse-split-label"></label>
        </div>
        
    </div>
    
<script>
var drv_name="<%=drvname%>";
var cat_name="<%=catname%>";
var id="<%=id%>";

var layuiEle ;

var layuiEle ;
var curTabIF =  $("#if_main")[0] ;

layui.use('element', function(){
	layuiEle = layui.element;
	layuiEle.init();
	layuiEle.on('tab', function(data)
		{
			var ifrm= null ;
			if(data.index==0)
			{//main hmi
				ifrm = $("#if_main")[0] ;
			}
			else
			{
				var id = $(this).attr('lay-id');
				ifrm = $("#if_"+id)[0] ;
			}
			if(ifrm!=null)
			{
				curTabIF = ifrm ;
				//console.log(curTabIF) ;
			}
		});
	layuiEle.render();
});

var cxt_menu = {
	"dev":[
		{op_name:"new_tag",op_title:"<wbt:lang>new_tag</wbt:lang>",op_icon:"fa fa-tag",op_action:act_node_new_tag},
		{op_name:"new_tag_exp",op_title:"<wbt:lang>new_tag_mid</wbt:lang>",op_icon:"fa fa-tag",op_action:""},
		{op_name:"new_tagg",op_title:"<wbt:lang>new_tag_group</wbt:lang>",op_icon:"fa fa-tags",op_action:act_new_tagg},
		{op_name:"del_dev",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times",op_action:""},
		{op_name:"new_hmi",op_title:"<wbt:lang>new_hmi</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:act_new_hmi},
		{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o",op_action:act_prop,default:true}
	],
	"tagg":[
		{op_name:"new_tag",op_title:"<wbt:lang>new_tag</wbt:lang>",op_icon:"fa fa-tag",op_action:act_node_new_tag},
		{op_name:"new_hmi",op_title:"<wbt:lang>new_hmi</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:act_new_hmi},
		{op_name:"modify_tagg",op_title:"<wbt:lang>modify</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:act_edit_tagg},
		{op_name:"new_tagg",op_title:"<wbt:lang>new_tag_group</wbt:lang>",op_icon:"fa fa-tags",op_action:act_new_tagg},
		{op_name:"del_tagg",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times",op_action:act_del_tagg}
	],
	"hmi":[
		
		{op_name:"edit_ui",op_title:"<wbt:lang>edit_ui</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:act_hmi_edit_ui},
		{op_name:"modify_ui",op_title:"<wbt:lang>modify</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:act_edit_hmi},
		{op_name:"del_ui",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times",op_action:act_del_hmi}
	]
}

function on_tree_node_selected(tn)
{
	$("#if_prop").attr("src","../ua/ui_prop.jsp?dlg=false&&tabid=drv&path="+tn.path) ;
	$("#if_tags").attr("src","../tag/node_tags.jsp?tabid=main&path="+tn.path);
}

var ua_tree = new UATree({
		eleid:"tree",
		data_url:"devdef_tree_ajax.jsp?drv="+drv_name+"&cat="+cat_name+"&id="+id,
		cxt_menu:cxt_menu,
		on_selected:on_tree_node_selected
		}) ;
ua_tree.init() ;

function refresh_ui()
{
	ua_tree.init();
}


function act_prop(n,op)
{
	dlg.open_win("ua/ui_prop.jsp?repid="+repid+"&id="+n.id,
			{title:"Properties",w:'800',h:'535'},
			['Ok',{title:'Apply',style:"warm",enable:false},{title:'Cancel',style:"primary"},{title:'Help',style:"primary"}],
			[
				function(dlgw)
				{
					if(!dlgw. isDirty())
					{
						dlg.close();
					}
					save_prop(dlgw,n.id,()=>{
						refresh_ui();
						dlg.close();
					});
				},
				function(dlgw)
				{
					save_prop(dlgw,n.id,()=>{
						dlg.btn_set_enable(1,false);
						dlgw.setDirty(false);
						refresh_ui();
					});
					
				},
				function(dlgw)
				{
					dlg.close();
				},
				function(dlgw)
				{
					alert("help");
				}
			]);
}


function save_prop(dlgw,id,succcb)
{
	var pm={} ;
	 pm.repid = repid ;
		pm.id = id ;
		pm.op="save";
		pm.txt=dlgw.get_prop_vals(); ;
		send_ajax("ua/ui_prop_ajax.jsp",pm,function(bsucc,ret){
			if(!bsucc)
			{
				dlg.msg(ret) ;
				return ;
			}
			if(succcb!=undefined&&succcb!=null)
				succcb() ;
			
		});
}

function act_node_new_tag(n,op)
{
	dlg.open("../tag/tag_edit.jsp",
			{title:"Add Tag",w:'500px',h:'400px'},
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
						 
						 //var pms = "repid="+repid+"&drv="+ret.drv+"&name="+ret.name+'&title='+ret.title+"&desc="+ret.desc ;
						 ret.path=n.path ;
						 ret.op = "add_tag";
						 //console.log(ret);
						 send_ajax('../tag/tag_ajax.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.msg(ret);
								dlg.close();
								var url = $("#if_tags").attr("src") ;
								$("#if_tags").attr("src",url);
							},false);
							
						 
						 //document.location.href=document.location.href;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function act_new_hmi(n,op)
{
	hmi_add_edit(false,n.path,()=>{
		refresh_ui() ;
	});
}
function act_edit_hmi(n,op)
{
	hmi_add_edit(true,n.path,()=>{
		refresh_ui() ;
	});
}

function act_del_hmi(n,op)
{
	hmi_del(n.path,()=>{
		refresh_ui() ;
	});
}

function act_hmi_edit_ui(n,op)
{
	//window.open("ua_hmi/hmi_editor.jsp?repid="+repid+"&id="+u.getId()) ;
	add_tab(n.id,n.title,"/admin/ua_hmi/hmi_editor_ui.jsp?tabid="+n.id+"&path="+n.path) ;
}
function act_del_tagg(n,op)
{
	tagg_del(n.path,()=>{
		refresh_ui() ;
	}) ;
}

function act_new_tagg(n,op)
{
	tagg_add_edit(false,n.path,()=>{
		refresh_ui() ;
	});
}

function act_edit_tagg(n,op)
{
	tagg_add_edit(true,n.path,()=>{
		refresh_ui() ;
	});
}

function add_tab(id,title,u)
{
	var oldif = document.getElementById("if_"+id) ;
	if(oldif!=null)
	{
		layuiEle.tabChange('tab_hmi_editor', id);
		return ;
	}

	layuiEle.tabAdd('tab_hmi_editor', {
		    id:id,title: title
		    ,content:"<iframe id='if_"+id+"' src='"+u+"' style='width:100%;height:100%'></iframe>"
		});
	resize_iframe_h();
	layuiEle.tabChange('tab_hmi_editor', id);
}

function set_comp_editor_tab(catid,itemid,title)
{
	var oldif = document.getElementById("if_"+itemid) ;
	if(oldif!=null)
	{
		layuiEle.tabChange('tab_hmi_editor', itemid);
		return ;
	}
	
	layuiEle.tabAdd('tab_hmi_editor', {
	    id:itemid,title: title
	    ,content:"<iframe id='if_"+itemid+"' src='ua_hmi/hmi_editor_comp.jsp?tabid="+itemid+"&catid="+catid+"&id="+itemid+"' style='width:100%;height:100%'></iframe>"
	});
	resize_iframe_h();
	layuiEle.tabChange('tab_hmi_editor', itemid);
}

function tab_notify(tab_id)
{
	if("if_"+tab_id!=curTabIF.id)
		return ;
	
	var tabst = curTabIF.contentWindow.tab_st() ;
	//console.log(tabst);
}

function tab_save()
{
	curTabIF.contentWindow.tab_save() ;
}

function btn_save_hmi()
{
	var pm = {} ;
	pm.op="save" ;
	pm.repid=repid;
	pm.hmiid=hmiid;
	pm.txt = JSON.stringify(loadLayer.extract(null)) ;
	oc.util.doAjax("hmi_ajax.jsp",pm,(bsucc,ret)=>{
        dlg.msg(ret);
    });
}

function draw_fit()
{
	if(curTabIF==null)
		return ;
	curTabIF.contentWindow.draw_fit() ;
}


function dev_lib()
{
	dlg.open_win("dev/dev_lib_mgr.jsp",
			{title:"Device Library",w:'1000',h:'560'},
			[{title:'Close',style:"primary"},{title:'Help',style:"primary"}],
			[
				function(dlgw)
				{
					dlg.close();
				},
				function(dlgw)
				{
					dlg.msg("help is under dev");
				}
			]);
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
});

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

function resize_iframe_h()
{
	   var h = $(window).height() -80;
	   $("iframe").css("height",h+"px");
}

var resize_cc = 0 ;
$(window).resize(function(){
	resize_iframe_h();
	//panel.updatePixelSize() ;
	resize_cc ++ ;
	if(resize_cc<=1)
		draw_fit();
	});
</script>
<script src="../js/split.js"></script>
</body>
</html>