<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.net.*,
				org.iottree.core.*,
				org.iottree.core.util.*
				"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out, "repid"))
		return ;
	//String op = request.getParameter("op");
	String repid = request.getParameter("repid");
	//String id = request.getParameter("id");
	UARep rep = UAManager.getInstance().getRepById(repid);
	if(rep==null)
	{
		out.print("no rep found!");
		return;
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
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
<link rel="stylesheet" href="/_js/jstree/themes/default/style.min.css" />
	<script src="/_js/jstree/jstree.min.js"></script>
<script src="./js/ua_tree.js"></script>

<script src="./js/split.css"></script>
<script src="./js/vue.min.js"></script>
<script src="./js/vue-router.js"></script>
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
/*






ul.layui-tab-title li:first-child i{
display: none;
}


*/
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

.line{
     position:absolute;
     background:green;
     height:1px;
     z-index: 1;
}

#tree { float:left; min-width:319px; border-right:1px solid silver; overflow:auto; padding:0px 0; }
#tree .icon_dev {background:url('./inc/sm_icon_dev.png') 0 0 no-repeat;width:24px;height:24px; }
#tree .icon_ch {background:url('./inc/sm_icon_ch.png') 0 0 no-repeat; width:24px;height:24px;}
#tree .icon_tagg {background:url('./inc/sm_icon_tagg.png') 0 0 no-repeat; width:24px;height:24px;}

</style>

</head>
<script type="text/javascript">

</script>
<body class="layout-body">
<div class="top " style="background-color: #326690;color:#ffffff;">
 <div style="float: left;position:relative;left:0px;margin-left:5px;top:2px;font: 30px solid">IOTTree Editor</div>

 <div style="float: left;position:relative;margin-left:10px;top:10px;font: 20px solid;color:#fff5e2"></div>

			<i class="fa fa-floppy-o fa-3x top_btn" onclick="tab_save()" ></i>
			<i class="fa fa-cogs  fa-3x  top_btn" onclick="dev_lib()"></i><span style="font: 20px solid">Device Library</span>
			<i class="fa fa-cogs  fa-3x  top_btn" onclick="list_comps()"></i><span style="font: 20px solid">UI Library</span>
		    <i id="lr_btn_fitwin"  class="fa fa-crosshairs fa-3x top_btn"></i>
</div>

<div class='hj-wrap'>
		<div class="hj-transverse-split-div subwin" style="width:10%">
			<div class="subwin_toolbar">Connectors
				<button onclick="refresh_conn()">+</button>
		   </div>
           <div id="conn" class="subwin_content" style="overflow: auto;">

               <div class="subitem"  v-for="connector in connectors">
               	 <div class="subitem_toolbar">{{ connector.title }} [{{ connector.tp }}]</div>
               	 <div class="subitem_content">
               	 	<div v-for="connection in connector.connections">
               	 		{{connection.ip}}
               	 	</div>
               	 </div>
               </div>
            </div>
            <label class="hj-transverse-split-label"></label>
        </div>
        
        <div class="hj-transverse-split-div subwin" style="width:20%">
           <div class="subwin_toolbar">Browser</div>
           <div id="tree" class="subwin_content"></div>
            <label class="hj-transverse-split-label"></label>
        </div>
        
        <div class="hj-transverse-split-div" style="width:70%;background-color: #ebeef3">
           <div style="padding-left:10px;padding-right:10px;marign0:10px;height:100%;position:inherit;">
           
			<div class="layui-tab layui-tab-brief"  lay-filter="tab_hmi_editor" lay-allowclose="true" style="width:100%;height:100%">
			  <ul class="layui-tab-title">
			    <li class="layui-this">[<%=rep.getTitle() %>]</li>
			    <li >Graph</li>
			  </ul>
			  <div class="layui-tab-content" style="position:relative;bottom:0px;height:100%">
			    <div class="layui-tab-item layui-show" style="position:relative;top:0px;bottom:0px;width:100%;height:100%">
			      <iframe id="if_rep" border="0" src="rep.jsp?tabid=main&id=<%=repid %>" src0="rep_editor.jsp?tabid=main&id=<%=repid %>" style="width:100%;height:100%;border:0px"></iframe>
				</div>

				<div class="layui-tab-item" style="position:relative;top:0px;bottom:0px;width:100%;height:100%">
			      <iframe id="if_main" src="rep_editor_graph.jsp?tabid=main&id=<%=repid %>" style="width:100%;height:100%;border:0px"></iframe>
				</div>
 
			  </div>
			</div>
			 
		    </div>
            <label class="hj-transverse-split-label"></label>
        </div>
        
    </div>
    
<script>
var repid="<%=repid%>";

var connvue = new Vue({
	el: '#conn',
	data:{
		connectors:[
			
		]
	}
});

function refresh_conn()
{
	var pm = {
			type : 'post',
			url : "./rep_conn_data_ajax.jsp",
			data :{id:repid}
		};
	$.ajax(pm).done((ret)=>{
		connvue.connectors=ret ;
	}).fail(function(req, st, err) {
		dlg.msg(err);
	});
}

refresh_conn();

var layuiEle ;
var repid="<%=repid%>";

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
				//console.log(curTabIF) ;
			}
		});
	layuiEle.render();
});

var cxt_menu = {
	"rep":[
		{op_name:"new_ch",op_title:"<wbt:lang>new_ch</wbt:lang>",op_icon:"fa fa-random",op_action:""},
		{op_name:"new_hmi",op_title:"<wbt:lang>new_hmi</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:""},
		{op_name:"new_tag_exp",op_title:"<wbt:lang>new_tag_mid</wbt:lang>",op_icon:"fa fa-compass",op_action:""},
		{op_name:"open_cxt",op_title:"<wbt:lang>data_cxt</wbt:lang>",op_icon:"fa fa-list-alt",op_action:act_open_data_cxt},
		{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o",op_action:act_prop,default:true},
		{op_name:"start_stop",op_title:"<wbt:lang>start/stop</wbt:lang>",op_icon:"fa fa-refresh",op_action:""},
		{op_name:"mem_add_conn",op_title:"<wbt:lang>add_conn</wbt:lang>",op_icon:"fa fa-refresh",op_action:""}
	],
	"ch":[
		{op_name:"new_dev",op_title:"<wbt:lang>new_dev</wbt:lang>",op_icon:"fa fa-tasks",op_action:act_ch_new_dev},
		{op_name:"del_ch",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times",op_action:""},
		{op_name:"new_hmi",op_title:"<wbt:lang>new_hmi</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:""},
		{op_name:"new_tag_exp",op_title:"<wbt:lang>new_tag_mid</wbt:lang>",op_icon:"fa fa-tag",op_action:""},
		{op_name:"open_cxt",op_title:"<wbt:lang>data_cxt</wbt:lang>",op_icon:"fa fa-list-alt",op_action:act_open_data_cxt},
		{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o",op_action:act_prop,default:true}
	],
	"dev":[
		{op_name:"new_tag",op_title:"<wbt:lang>new_tag</wbt:lang>",op_icon:"fa fa-tag",op_action:""},
		{op_name:"new_tag_exp",op_title:"<wbt:lang>new_tag_mid</wbt:lang>",op_icon:"fa fa-tag",op_action:""},
		{op_name:"new_tagg",op_title:"<wbt:lang>new_tag_group</wbt:lang>",op_icon:"fa fa-tags",op_action:""},
		{op_name:"del_dev",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times",op_action:""},
		{op_name:"new_hmi",op_title:"<wbt:lang>new_hmi</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:""},
		{op_name:"open_cxt",op_title:"<wbt:lang>data_cxt</wbt:lang>",op_icon:"fa fa-list-alt",op_action:act_open_data_cxt},
		{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o",op_action:act_prop,default:true}
	],
	"tagg":[
		{op_name:"new_tag",op_title:"<wbt:lang>new_tag</wbt:lang>",op_icon:"fa fa-tag",op_action:""},
		{op_name:"new_tagg",op_title:"<wbt:lang>new_tag_group</wbt:lang>",op_icon:"fa fa-tags",op_action:""},
		{op_name:"del_tagg",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times",op_action:""}
	]
}

var ua_tree = new UATree("tree",{
		data_url:"rep_tree_ajax.jsp?id="+repid,
		cxt_menu:cxt_menu
		}) ;
ua_tree.init() ;

function refresh_ui()
{
	ua_tree.reload() ;
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

function act_ch_new_dev(n,op)
{
	dlg.open("ua/dev_add.jsp",
			{title:"<wbt:lang>add_dev</wbt:lang>",w:'500px',h:'400px'},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
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
						 ret.repid=repid ;
						 ret.chid = n.id;
						 //console.log(ret);
						 send_ajax('ua/dev_add_do.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.msg(ret);
								dlg.close();
								refresh_ui() ;
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

function act_open_data_cxt(n,op)
{
	dlg.open("ua_cxt/cxt_var_lister.jsp?repid="+repid+"&id="+n.id,
			{title:"Data Context",w:'850px',h:'600px'},
			['Cancel'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
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

function list_comps()
{
	dlg.open_win("ua_hmi/hmi_left_comp.jsp?edit=true",
			{title:"Components List",w:'800',h:'535'},
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
<script src="./js/split.js"></script>
</body>
</html>