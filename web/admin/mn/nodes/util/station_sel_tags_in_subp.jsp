<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.net.*,
				org.iottree.core.*,
				org.iottree.core.util.*
				"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%

	
boolean b_dlg = "true".equals(request.getParameter("dlg")) ;
String path="" ;
%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title></title>
<jsp:include page="../../../head.jsp">
	<jsp:param value="true" name="tree"/>
	<jsp:param value="true" name="tab"/>
</jsp:include>

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

.pop_menu
{
cursor: pointer;
font-size:small;
vertical-align:middle;
}
.pop_menu:hover
{
color:#03a6ea;
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


ul.layui-tab-title li:nth-child(1) i{
display: none;
}

ul.layui-tab-title li:nth-child(2) i{
display0: none;
}

ul.layui-tab-title li:nth-child(3) i{
display0: none;
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
    //top:45px;
    top:0px;
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
        font-size: medium;
		line-height: 40px;
 }
 .subwin_content{
        width:100%;
        flex:1;
}

.subitem{
    width:100%;
    background-color: #f2f2f2;margin-top:3px;margin-bottom: 3px;
    border-radius:9px;
    padding-bottom:2px;
}

.subitem th,.subitem td{
	//border:1px solid #888;
}

.subitem_toolbar{
        width:100%;
        height0: 25px;
        border:solid 0px;
        font-size: small;
        font-weight:bold;
		line-height: 25px;
		//background-color: #f0f7ff;
 }
 .subitem_content{
        width:100%;
}

 .subitem_li {
 	margin: 5px;
 	border: solid 1px;
 	border-style:dashed;
 	//background-color: #c2c2c2;
 }

.line{
     position:absolute;
     background:green;
     height:1px;
     z-index: 1;
}
.line2{
position:absolute;
  width:1px;
  background-color:red;
}

#conn_ch
{
position:relative;
left:0px;
top:0px;
width:100%;
height:100%;
background-color: #fff ;
}

.tn_warn
{
	background-color: #ffc633;
	height:40px;
	padding-left:10px;
	padding-right:10px;
	margin-left:10px;
	border-radius:10px;
}

.tn_ok
{
	background-color: #17c680;
	height:40px;
	padding-left:10px;
	padding-right:10px;
	margin-left:10px;
	border-radius:10px;
}

#btn_menu_tree span
{
cursor: pointer;
}

.top_toolbox
{
	position:absolute;float:left;margin-right:30px;top:5px;bottom:5px;font: 20px solid;color:#fff5e2;
	border-radius:5px;
	
	padding-top:5px;
	text-align: center;
}

.top_tool
{
background-color: #515658;
	box-shadow: 2px 2px 2px #888888;
}

.top_toolbox span
{
   margin-top:10px;
   cursor: pointer;
}

.left_btm
{
	position: absolute;
	left:0px;width:100%;
	bottom: 0px;
	height:100%;
	border: 1px solid;
	border-color: #cccccc;
}

.left_btm .show_hid
{
	position: absolute;
	right:3px;top:3px;
	width:20px;
	text-align:center;
}

.tab .tab-header-item .close
{
	display:none;
}

.prj_item
{
	position:relative;
	border: 1px solid #5199ee;
	width:90%;
	left:5%;
	height:30px;
	margin-top:5px;
	margin-bottom: 5px;
	cursor:pointer;
}

.prj_item:hover {
	background-color: #555555;
}

.sel
{
	background-color: #999999;
}
</style>

</head>
<script type="text/javascript">

dlg.resize_to(900,600) ;
</script>
<body class0="layout-body" style="overflow-x:hidden;overflow-y:hidden;">

<div class='hj-wrap' style="opacity: 1.0;">
        <div id="div_conn" class="hj-transverse-split-div subwin" style="width:22%">
			<div class="subwin_toolbar">
			<span style="left:20px;display:none" id="btn_left_showhidden">&nbsp;&nbsp;<i class="fa fa-bars fa-lg"></i>&nbsp;&nbsp;</span>
					&nbsp;&nbsp;<i class="fa fa-link fa-fw"></i>站点.项目

		   </div>
		   <div class="subwin_content" style="overflow:hidden;">
<%
	String first_prj_uid = null ;
	if(false)//for(  prj:IOTPlatformManager.getInstance().listPrjs())
	{
		String tmpuid = "" ;
		
%>  <div id="pi_<%=tmpuid %>" class="prj_item" onclick="on_sel_prj('<%=tmpuid %>')"><%=tmpuid %></div>
<%
	}
	if(first_prj_uid==null)
		first_prj_uid = "" ;
%>
	      </div>
	      
	    </div>
       
        <div id="div_brw" class="hj-transverse-split-div subwin" style="width:28%">
           <div class="subwin_toolbar">
          
           <div class="btn-group open"  id="btn_menu_tree">
				  <i class="fa fa-sitemap fa-fw"></i> Browser
				  &nbsp;&nbsp;<span title="refresh"><i onclick="refresh_ui()" class="fa fa-refresh fa-lg" aria-hidden="true"></i></span>
				 </div>
           </div>
           <div class="subwin_content" style="overflow:auto">
           		<div id="tree"  class="tree" style="width:100%;overflow:auto;left:-30px;position:absolute;height:700px"></div>
           		<div style="width:80%;overflow: hidden;height:100px">&nbsp;</div>
           </div>
            <label class="hj-transverse-split-label"></label>
        </div>
         
        <div id="div_content" class="hj-transverse-split-div" style="width:50%;background-color: #ebeef3;border:1px solid;border-color: #ebeef3">
           <div class="left_btm">
    	
    	<div class="left_btm_tab">
    	<ul></ul>
          <div></div>
          </div>
          <div id="show_hid" class="show_hid"><i class="fa fa-angle-double-down" aria-hidden="true" /></i></div>
    </div>
        </div>
        
        
    
    </div>
    
    
<script>

var b_dlg = <%=b_dlg%>;

var jsTree ;

var cur_iottree_uid = "<%=first_prj_uid%>" ;
var cur_tree_nid = null ;

var cur_ob = dlg.get_opener_opt("tags_in_subp") ;

var station_id = null ;
var prj_name = null ;
var cxt_nodep = null ;
var b_all_subt = false;
var tag_subts = null ;


if(cur_ob)
{
	station_id = cur_ob.station_id||"" ;
	prj_name = cur_ob.prj_name||"" ;
	if(cur_ob.station_id && cur_ob.prj_name)
		cur_iottree_uid = cur_ob.station_id+"."+cur_ob.prj_name;
	
	cxt_nodep = cur_ob.cxt_nodep ;
	if(cxt_nodep)
	{
		cur_tree_nid = cxt_nodep.substring(1) ;
		cur_tree_nid = cur_tree_nid.replaceAll("/",'_') ;
	}
	b_all_subt = cur_ob.b_all_subt||false;
	tag_subts = cur_ob.tag_subts||[];
}

function get_selected(cb)
{
	let seltags = $("#btm_tags")[0].contentWindow.get_sel_tag_subpaths() ;
	
	cb(true,{station_id:station_id,prj_name:prj_name,cxt_nodep:cxt_nodep,b_all_subt:b_all_subt,tag_subts:seltags}) ;
}

//var selected_tags=[];

function show_taglist(path)
{
	let u ="/system/plat/cxt_tags4sel.jsp?tabid=main&iottree_uid="+cur_iottree_uid+"&path="+path ;
	if(b_dlg)
		u += "&hiddle_ok=true" ;
	
	$("#btm_tags").attr("src",u);
	//$("#btm_tags")[0].contentWindow.set_selected_tagpaths(tag_subts)
}

function get_sel_tagpaths_4taglist()
{
	return tag_subts;
}

function on_sel_tags_in_taglist(tags)
{
	console.log(tags)
	//console.log(tags) ;
}

function on_tree_node_selected(tn)
{
	cxt_nodep = tn.path ;
	show_taglist(tn.path) ;
}



function set_prj_tree(uid)
{
	$.jstree.destroy();
	cur_iottree_uid = uid ;
	
	$(".prj_item").removeClass("sel") ;
	$(document.getElementById("pi_"+cur_iottree_uid)).addClass("sel") ;
	
	let k = uid.indexOf('.') ;
	station_id = uid.substring(0,k) ;
	prj_name = uid.substring(k+1) ;

	jsTree = $('#tree').jstree(
			{
				'core' : {
					'data' : {
						'url' : "/system/plat/plat_prj_tree_ajax.jsp?uid="+uid,
						"dataType" : "json"
					},
					'check_callback' : function(o, n, p, i, m) {
						if(m && m.dnd && m.pos !== 'i') { return false; }
						if(o === "move_node" || o === "copy_node") {
							if(this.get_node(n).parent === this.get_node(p).id) { return false; }
						}
						return true;
					},
					'themes' : {
						'responsive' : false,
						'stripes' : true
					}
				},
				'types' : {
					'default' : { 'icon' : 'folder' },
					'file' : { 'valid_children' : [], 'icon' : 'file' }
				},
				'unique' : {
					'duplicate' : function (name, counter) {
						return name + ' ' + counter;
					}
				},
				'plugins' : ['state','types','contextmenu','unique'] //,'dnd',
			}
	);

		$("#tree").scroll(()=>{
			if(this.on_tree_scrolled)
				this.on_tree_scrolled() ; 
		});

		jsTree.on('activate_node.jstree',(e,data)=>{
			if(on_tree_node_selected!=undefined&&on_tree_node_selected!=null)
				on_tree_node_selected(data.node.original)
		});
		
		if(cur_tree_nid)
		{
			jsTree.jstree('select_node', cur_tree_nid);
			show_taglist(cxt_nodep);
		}
			
		
}


function on_sel_prj(uid)
{
	set_prj_tree(uid);
}


	
function update_sel_tags()
{
	let tmps="" ;
	for(let t of selected_tags)
	{
		tmps += `<tr><td>\${t.p}</td><td>\${t.t}</td><td><i class="fa fa-times" style="color:red" onclick="del_sel_tag('\${t.p}')"></i></td></tr>`
	}
	
	$("#tags_selected_tb").html(tmps) ;
}
	


function del_sel_tag(p)
{
	for(let i = 0 ; i < selected_tags.length ;  i ++)
	{
		let t = selected_tags[i] ;
		if(t.p==p)
		{
			selected_tags.splice(i,1) ;
			update_sel_tags() ;
			return ;
		}
	}
}

function resize_iframe_h()
{
	   var h = $(window).height()-80+45;
	   $("iframe").css("height",h+"px");
}

function resize_tree()
{
	var h = $(window).height()-120+45;
	$("#tree").css("height",h+"px");
}

resize_tree();
//////////edit panel


var resize_cc = 0 ;
$(window).resize(function(){
	resize_iframe_h();
	resize_tree();
	//panel.updatePixelSize() ;
	resize_cc ++ ;
	//if(resize_cc<=1)
		//draw_fit();
	});
	

function init_left_btm()
{
	$("#show_hid").click(function(){
		let lb = $(".left_btm") ;
		let h = lb.height() ;
		if(h<50)
		{
			$(this).html(`<i class="fa fa-angle-double-down" aria-hidden="true" /></i>`) ;
			let oldh=  $(this).attr("__h") ;
			lb.css("height",oldh+"px") ;
		}
		else
		{
			$(this).html(`<i class="fa fa-angle-double-up" aria-hidden="true" /></i>`) ;
			$(this).attr("__h",""+h) ;
			lb.css("height","30px") ;
		}
	});
	

    $(".left_btm_tab").tab();
    
    let tmps = `<iframe id="btm_tags" src="" style="width:100%;height:100%;border:0px solid;overflow:hidden;"></iframe>`;
	$('.left_btm_tab').tab('addTab', {'title': '数据项Tags', 'id': 'lb_tab_tags', 'content': tmps});
	
	$(".left_btm_tab").tab('selectTab', 'lb_tab_tags');
}


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
	
	init_left_btm() ;
	
	if(cur_iottree_uid)
		set_prj_tree(cur_iottree_uid);
});


/*
var layuiEle ;

var curTabIF =  $("#if_main")[0] ;

var copiedItem = null ;

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


function chg_lan(ln)
{
	send_ajax("./login/login_ajax.jsp",{op:"set_session_lan",lan:ln},(bsucc,ret)=>{
		
		if(!bsucc||ret!='succ')
		{
			dlg.msg(ret) ;
			return;
		}
		location.reload();
	});
}
*/

</script>
</body>
</html>