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
	if(!Convert.checkReqEmpty(request, out, "repid","id"))
		return ;
	//String op = request.getParameter("op");
	String repid = request.getParameter("repid");
	String id = request.getParameter("id");
	UARep rep = UAManager.getInstance().getRepById(repid);
	if(rep==null)
	{
		out.print("no rep found!");
		return;
	}
	UAHmi uahmi = rep.findHmiById(id) ;
	if(uahmi==null)
	{
		out.print("no hmi node found") ;
	}
	
	
%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>IOTTree HMI Editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
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
	left: 0px;
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
    height:35px;
}
.layui-tab-title
{
	margin-bottom:0px;
	height:35px;
	background-color: #cccccc;
}
.layui-tab-content {
    padding: 0px;
}

.layui-tab-title .layui-this{
border-bottom0: 3px solid #3f7dff;
background-color: #aaaaaa;
top:0px;
margin:0px;
padding-top:0px;
padding-bottom:0px;
height:35px;
}

ul.layui-tab-title li:first-child i{
display: none;
}

.layui-tab-title .layui-this:after {
	border-width: 0px;
	    height: 35px;
}
</style>

</head>
<script type="text/javascript">


</script>
<body class="layout-body">
<div class="top " style="background-color: #cccccc">
 <div style="float: left;position:relative;left:0px;margin-left:5px;top:2px;font: 30px solid;color:#0092c8">IOTTree HMI Editor</div>
 <div style="float: left;position:relative;margin-left:10px;top:10px;font: 20px solid;color:#fb0003"></div>
			<i class="fa fa-cube fa-3x top_btn" id="topm_filter_op" style="" onclick="do_apply()"></i>
			<i class="fa fa-floppy-o fa-3x top_btn" onclick="tab_save()" ></i>
			<i class="fa fa-cog fa-3x  top_btn" onclick="add_tab('11','ttt','hmi_editor_comp.jsp?catid=<%=repid %>&id=<%=id%>')"></i>
			<i id="edit_panel_btn"  class="fa fa-pencil-square-o fa-3x top_btn"></i>
		    <i id="lr_btn_fitwin"  class="fa fa-crosshairs fa-3x top_btn"></i>
		</div>
		
		<div class="mid" style="width:100%;height:100%">
			<div class="layui-tab"  lay-filter="tab_hmi_editor" lay-allowclose="true">
			  <ul class="layui-tab-title">
			    <li class="layui-this">[<%=uahmi.getNodePathTitle() %>]</li>
			  </ul>
			  <div class="layui-tab-content" style="bottom:0px">
			    <div class="layui-tab-item layui-show" style="position: fixed;height0:700px;left:0px;bottom:0px;top:80px;right:0px">
			      <iframe id="if_main" src="hmi_editor_ui.jsp?tabid=main&repid=<%=repid %>&id=<%=id%>" style="width:100%;height:100%"></iframe>
				</div>
			    
			  </div>
			</div>
		</div>
<script>


var layuiEle ;
var repid="<%=repid%>";
var hmiid = "<%=id%>"

var curTabIF =  $("#if_main")[0] ;

layui.use('element', function(){
	layuiEle = layui.element;
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
				console.log(curTabIF) ;
			}
		});
	layuiEle.render();
});

function add_tab(id,title,u)
{
	layuiEle.tabAdd('tab_hmi_editor', {
		    id:id,title: title
		    ,content:"<iframe src='"+u+"' style='width:100%;height:100%'></iframe>"
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
	    ,content:"<iframe id='if_"+itemid+"' src='hmi_editor_comp.jsp?tabid="+itemid+"&catid="+catid+"&id="+itemid+"' style='width:100%;height:100%'></iframe>"
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
</body>
</html>