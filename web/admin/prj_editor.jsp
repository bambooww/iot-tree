<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.net.*,
				org.iottree.core.*,
				org.iottree.core.util.*
				"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out, "id"))
		return ;
	//String op = request.getParameter("op");
	String prjid = request.getParameter("id");
	//String id = request.getParameter("id");
	UAPrj rep = UAManager.getInstance().getPrjById(prjid);
	if(rep==null)
	{
		out.print("no prj found!");
		return;
	}
	
	UAHmi hmi_main = rep.getHmiMain() ;
	String hmi_main_path ="" ;
	String hmi_main_id="" ;
	String hmi_main_title="" ;
	if(hmi_main!=null)
	{
		hmi_main_id = hmi_main.getId();
		hmi_main_title = hmi_main.getTitle();
		hmi_main_path = hmi_main.getNodePath();
	}
	String path = rep.getNodePath() ;
%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>IOTTree Project Editor</title>
<jsp:include page="head.jsp"></jsp:include>
<link rel="stylesheet" href="/_js/jstree/themes/default/style.min.css" />
<script src="/_js/jstree/jstree.min.js"></script>
<link rel="stylesheet" href="/_js/selectmenu/selectmenu.css" />
<script src="/_js/selectmenu/selectmenu.min.js"></script>
<script src="./js/ua_tree.js"></script>
<script src="./js/ua.js"></script>
<script src="./js/vue.min.js"></script>
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


ul.layui-tab-title li:nth-child(1) i{
display: none;
}

ul.layui-tab-title li:nth-child(2) i{
display: none;
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

.subitem th,.subitem td{
	//border:1px solid #888;
}

.subitem_toolbar{
        width:100%;
        height0: 30px;
        border:solid 1px;
        font-size: small;
        font-weight:bold;
		line-height: 30px;
		background-color: #f0f7ff;
 }
 .subitem_content{
        width:100%;
}

 .subitem_li {
 	margin: 5px;
 	border: solid 1px;
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

#tree { float:left; min-width:319px; border-right:1px solid silver; overflow:auto; padding:0px 0; }
#tree .icon_dev {background:url('./inc/sm_icon_dev.png') 0 0 no-repeat;width:24px;height:24px; }
#tree .icon_ch {background:url('./inc/sm_icon_ch.png') 0 0 no-repeat; width:24px;height:24px;display:none;}
#tree .icon_tagg {background:url('./inc/sm_icon_tagg.png') 0 0 no-repeat; width:24px;height:24px;}

</style>

</head>
<script type="text/javascript">

</script>
<body class0="layout-body" style="overflow-x:hidden;overflow-y:hidden;">
<div class="top " style="background-color: #007ad4;color:#ffffff;">
 <div style="float: left;position:relative;left:0px;margin-left:5px;top:2px;font: 30px solid;font-weight:600;font-size:16px;color:#d6ccd4">
   <img src="inc/logo1.png" width="40px" height="40px"/>IOTTree Project</div>
		<div style="float: left;position:relative;left:100px;margin-left:5px;top:2px;font: 20px solid" >
		<%=rep.getTitle()%>
		</div>
		 <div style="position:relative;float: right;right:320px;margin-right:30px;width:110px;margin-top:10px;font: 20px solid;color:#fff5e2;background-color: #515658">
		 <i id="prj_btn_start"  class="fa fa-play fa-lg top_btn" style="color:grey" title="start project" onclick="prj_run(true)"></i>
		 <i id="prj_btn_stop"  class="fa fa-stop fa-lg top_btn" style="color:grey" title="stop project" onclick="prj_run(false)"></i>
		</div>
 <div style="float: right;margin-right:10px;margin-top:10px;font: 20px solid;color:#fff5e2">
			<i class="fa fa-server  fa-lg  top_btn" onclick="dev_lib()"></i><span style="font: 20px solid"></span>
			<i class="fa fa-cogs  fa-lg  top_btn" onclick="list_comps()"></i><span style="font: 20px solid"></span>
			<%--
			<i class="fa fa-floppy-o fa-lg top_btn" onclick="tab_save()" ></i>
		    <i id="lr_btn_fitwin"  class="fa fa-crosshairs fa-lg top_btn"></i>
		     --%>
		    </div>
</div>
<div class='hj-wrap' style="opacity: 1.0;">
        <div id="div_conn" class="hj-transverse-split-div subwin" style="width:20%">
			<div class="subwin_toolbar">
			<span style="left:20px;display:none" id="btn_left_showhidden">&nbsp;&nbsp;<i class="fa fa-bars fa-lg"></i>&nbsp;&nbsp;</span>
			<%--
			Connectors
				<button type="button" class="btn btn-default"><i class="fa fa-bars fa-lg"></i>&nbsp;&nbsp;<i class="fa fa-caret-down"></i></button>
				 --%>
				<div class="btn-group open"  id="btn_menu_conn">
				  <a class="btn" href="#"><i class="fa fa-link fa-fw"></i> Connectors</a>
				  <a class="btn" href="#">
				    <span class="fa fa-caret-down" title="Toggle dropdown menu"></span>
				  </a>
				 </div>
		   </div>
		   <div class="subwin_content" style="overflow:hidden;">
		      <div  style="width:75%;height:100%;float:left;overflow:hidden;border:solid 1px;border-color: #cccccc;">
		         <div id="conn" style="width:110%;height:100%;float:left;overflow:auto;">
		           <div class="subitem"  v-for="connector in connectors" style="width:91%">
		               	 <div class="subitem_toolbar" v-bind:id="'cp_'+connector.id" v-bind:cp_id="connector.id" v-bind:cp_tp="connector.tp">
		               	      <table style="width:100%;border: 1">
			               	 		<tr>
										<td width="90%" style="white-space: nowrap;" v-bind:title="connector.static_txt">
											<span style="left:20px;" id="btn_left_showhidden">&nbsp;&nbsp;<i class="fa fa-chevron-down"></i>&nbsp;&nbsp;</span>
											<span style="width:15px;height:15px;background-color: grey;"  v-bind:id="'cp_st_'+connector.id" >&nbsp;&nbsp;&nbsp;&nbsp;</span>
											&nbsp;{{ connector.title }} [{{ connector.tp }}]
										</td>
										<%--
										<td width="25px">
											<a v-bind:href="'javascript:edit_cp(\''+connector.tp+'\',\''+connector.id+'\')'"><i class="fa fa-pencil" aria-hidden="true"></i></a>
										</td>
										 --%>
										<td width="20px" >
											
										</td>
									</tr>
								</table>
		               	  </div>
		               	  <div class="subitem_content">
		               	 	    <div class="subitem_li" v-for="connection in connector.connections" v-bind:id="'conn_' + connection.id" 
		               	 	    		v-bind:cp_id="connector.id" v-bind:cp_tp="connector.tp" v-bind:conn_id="connection.id">
		               	 		<table style="width:100%;">
			               	 		<tr>
										<td width="90%" >
											{{connection.title}} [{{connection.name}}]
										</td>
										<td width="20px">
											<span v-bind:id="'conn_st_'+connection.id"><i class="fa fa-chain-broken fa-lg"></i></span>
										</td>
										<%--
										<td width="25px">
											<div style="width:15px;height:15px;"  v-bind:id="'conn_run_'+connection.id" ><i class="fa fa-cog fa-lg"></i></div>
										</td>
										 --%>
									</tr>
								</table>
		               	 	</div>
		               	 </div>
		               </div>
		             </div>
	            </div>
	            
	            <div id="conn_ch" style="width:25%;height:100%;overflow:hidden;float:left;border-top: solid 1px;border-color: #cccccc;"> </div>
            
	      </div>
	     
	    </div>
       
        <div id="div_brw" class="hj-transverse-split-div subwin" style="width:20%">
           <div class="subwin_toolbar">
          <%--
          Browser
          <button type="button" class="btn btn-default" ><i class="fa fa-bars fa-lg"></i>&nbsp;&nbsp;<i class="fa fa-caret-down"></i></button>
           --%>
           <div class="btn-group open"  id="btn_menu_tree">
				  <a class="btn " href="#"><i class="fa fa-sitemap fa-fw"></i> Browser</a>
				  <a class="btn "  href="#">
				    <span class="fa fa-caret-down" title="Toggle dropdown menu"></span>
				  </a>
				 </div>
           </div>
           <div class="subwin_content" style="overflow:auto">
           		<div id="tree"  style="width:100%;overflow:auto;left:-30px;position:absolute;height:700px"></div>
           		<div style="width:80%;overflow: hidden;height:100px">&nbsp;</div>
           </div>
            <label class="hj-transverse-split-label"></label>
        </div>
         
        <div id="div_content" class="hj-transverse-split-div" style="width:60%;background-color: #ebeef3">
           <div style="padding-left:10px;padding-right:0px;marign0:10px;width:100%;height:100%;position:inherit;background-color: #ebeef3;z-index:60000" id="right_tabs">
             
			<div class="layui-tab layui-tab-brief"  lay-filter="tab_hmi_editor" lay-allowclose="true" style="width:100%;height:100%">
			<span id="right_tabs_btn" style="position:absolute;right:10px;top:10px;z-index:60001"><i class="fa fa-window-restore fa-lg" aria-hidden="true"></i></span>
			  <ul class="layui-tab-title">
			    <li class="layui-this">[Tags]</li>
			    <li >Properties</li>
			    
			  </ul>
			  <div class="layui-tab-content" style="position:relative;bottom:0px;height:100%">
			  	<div class="layui-tab-item layui-show" style="position:relative;top:0px;bottom:0px;width:99%;height:99%">
			      <iframe id="if_tags" src="./ua_cxt/cxt_tags.jsp?tabid=main&path=<%=path %>" style="width:100%;height:100%;border:0px"></iframe>
				</div>
				
				<div class="layui-tab-item"  style="position:relative;top:0px;bottom:0px;width:100%;height:100%">
			      <iframe id="if_prop" src="./ua/ui_prop.jsp?dlg=false&&tabid=drv&path=<%=path %>" style="width:100%;height:100%;border:0px"></iframe>
				</div>
<%--
			    <div class="layui-tab-item" style="position:relative;top:0px;bottom:0px;width:100%;height:100%">
			      <iframe id="if_rep" border="0" src11="rep.jsp?tabid=main&id=<%=repid %>" src0="rep_editor.jsp?tabid=main&id=<%=repid %>" style="width:100%;height:100%;border:0px"></iframe>
			      <iframe id="if_main" src="rep_editor_graph.jsp?tabid=main&id=<%=repid %>" style="width:100%;height:100%;border:0px"></iframe>
				</div>
 --%>
				<%--
			       <iframe id="if_access" src="./ua_cxt/cxt_accessor.jsp?tabid=main&path=<%=path %>" style="width:100%;height:100%;border:0px"></iframe>
			        --%>
		
			  </div>
			</div>
		    </div>
            <label class="hj-transverse-split-label"></label>
        </div>
    </div>
<script>
var repid="<%=prjid%>";
var prjid="<%=prjid%>";
var hmi_main = {id:"<%=hmi_main_id %>",title:"<%=hmi_main_title %>",path:"<%=hmi_main_path %>"};

var connpro_menu = [
	{content:'Connector Provider',header: true},
	{content:'Tcp Server',callback:function(){edit_cp("tcp_server","");}},
	{content:'Tcp Server For Opc Agent',callback:function(){edit_cp("opc_agent","");}},
	{content:'sm_divider'},
	{content:'<i class="fa fa-link"></i> Connector',header: true},
	{content:'Tcp Client',callback:function(){edit_cpt("tcp_client","","");}},
	{content:'COM',callback : function(){edit_cpt("com","","");}},
	{content:'OPC UA',callback:function(){edit_cpt("opc_ua","","");}},
	{content:'OPC DA',callback:function(){edit_cpt("opc_da","","");}},
	{content:'OPC Agent',callback : function(){edit_cpt("opc_agent","","");}},
	{content:'HTTP Url',callback:function(){
		edit_cp("http","");
		//dlg.msg("support later")
	}},
	{content:'MQTT',callback:function(){
		edit_cpt("mqtt","","");
	}},
	{content:'WebSocket Client',callback:function(){
		edit_cpt("ws_client","","");
	}},
	{content:'sm_divider'},
	{content:'IOTTree Node',callback:function(){
		edit_cpt("iottree_node","","");
	}},
	{content:'sm_divider'},
	
	{content:'Others',header: true},
	{content:'Virtual',callback : function(){edit_cpt("virtual","","");}}
];

var tree_menu = [
	{content:'Share',header: true},
	{content:'Share as a node',callback:function(){share_as_node();}},
	{content:'sm_divider'},
	
];

$('#btn_menu_conn').click(function(){
	$(this).selectMenu({
		title : 'Add Connector ',
		regular : true,
		data : connpro_menu
	});
});

function share_as_node()
{
	dlg.open("./ua/prj_share.jsp?prjid="+prjid,{title:"Share project as a node",w:'500px',h:'400px'},
			['Ok','Close'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,txt)=>{
						if(!bsucc)
						{
							dlg.msg(txt);
							return ;
						}
						dlg.close();
					});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function task_setup()
{
	add_tab("___task","Tasks","./ua/prj_task.jsp?prjid="+prjid) ;
	/*
	dlg.open("./ua/prj_task.jsp?prjid="+prjid,{title:"setup project task",w:'800px',h:'400px'},
			['Close'],
			[
				
				function(dlgw)
				{
					dlg.close();
				}
			]);
	*/
}

function resize_iframe_h()
{
	   var h = $(window).height()-80;
	   $("iframe").css("height",h+"px");
}

function resize_tree()
{
	var h = $(window).height()-90;
	$("#tree").css("height",h+"px");
}

resize_tree();

$("#right_tabs_btn").click(function(){
	if("fixed"==$("#right_tabs").css("position"))
	{//position:
		$("#right_tabs").css("position","inherit") ;
		resize_iframe_h();
	}
	else
	{
		$("#right_tabs").css("position","fixed") ;
		$("#right_tabs").css("left","0px") ;
		$("#right_tabs").css("top","0px") ;
		
		var h = $(window).height()-40;
		 $("iframe").css("height",h+"px");
	}
});

var b_left_show=true;
$('#btn_left_showhidden').click(function(){
	if(b_left_show)
	{
		$("#div_conn").css("width","5%");
		$("#div_brw").css("width","5%");
		$("#div_content").css("width","90%");
		b_left_show=false;
	}
	else
	{
		$("#div_conn").css("width","20%");
		$("#div_brw").css("width","20%");
		$("#div_content").css("width","60%");
		b_left_show=true;
	}
});

$('#btn_menu_tree').click(function(){
	$(this).selectMenu({
		title : 'Tree Browser',
		regular : true,
		data : tree_menu
	});
});

document.oncontextmenu = function() {
    return false;
}

function on_conn_ui_showed()
{
	$(".subitem_toolbar").mouseup(function(e) {
	    if (3 == e.which)
	    {
	    	e.stopPropagation();
	    	
	    	//$('body').selectMenu({rightClick : true}) ;//clear outter menu
	    	$('.sm_container').css("display","none") ;
	    	var me = $(this) ;
	        $(this).selectMenu({
	        	regular : true,
	        	rightClick : true,
	        	data : ()=>{
	        		var d=[] ;
	        		var cpid = $(this).attr("cp_id");
	        		var cptp = $(this).attr("cp_tp");
	        		var brun = $(this).attr("cp_run")=='true';
	        		var tt = "<i class='fa fa-play'></i> Start" ;
	        		if(brun)
	        			tt = "<i class='fa fa-stop'></i> Stop" ;
					d.push({ content : tt, callback:()=>{
							rt_cp_start_stop(cpid);
						}});
					d.push({content:'sm_divider'});
					d.push({content:'<i class="fa fa-link"></i> Add Connection',callback:()=>{
						edit_cpt(cptp,cpid,"") ;
					}});
					
					d.push({ content : '<i class="fa fa-pencil"></i> Edit Connector', callback:()=>{
						edit_cp(cptp,cpid) ;
					}});
					if(cptp=='tcp_server')
					{
						d.push({content:'sm_divider'});
						d.push({ content : '<i class="fa fa-magic"></i> Config Wizard', callback:()=>{
							config_wizard(cptp,cpid) ;
						}});
					}
					
					d.push({content:'sm_divider'});
					d.push({ content : '<i class="fa fa-times"></i> Delete Connector', callback:()=>{
						del_cp(cpid);
					}}) ;
					return d;
				}
	        });
	    }
	})
	
	$(".subitem_li").mouseup(function(e) {
	    if (3 == e.which)
	    {
	    	e.stopPropagation();
	    	
	    	//$('body').selectMenu({rightClick : true}) ;//clear outter menu
	    	$('.sm_container').css("display","none") ;
	    	var me = $(this) ;
	        $(this).selectMenu({
	        	regular : true,
	        	rightClick : true,
	        	data : ()=>{
	        		var d=[] ;
	        		var cpid = $(this).attr("cp_id");
	        		var cptp = $(this).attr("cp_tp");
	        		var connid = $(this).attr("conn_id");
	        		var bconn = $(this).attr("conn_ready")=='true';
	        		var tt = "<i class='fa fa-play'></i> Start" ;
	        		if(bconn)
	        			tt = "<i class='fa fa-stop'></i> Stop" ;
					d.push({ content : tt, callback:()=>{
							rt_cpt_start_stop(cpid);
						}});
					d.push({content:'sm_divider'});
					if(cptp=="opc_ua"||cptp=="opc_agent")
					{
						d.push({ content : '<i class="fa fa-pencil"></i> Bind', callback:()=>{
							edit_bind_setup(cptp,cpid,connid) ;
						}});
					}
					if(cptp=="mqtt")
					{
						d.push({ content : '<i class="fa fa-pencil"></i> Test', callback:()=>{
							edit_mqtt_test(cptp,cpid,connid) ;
						}});
					}
					if(cptp=="iottree_node")
					{
						d.push({ content : '<i class="fa fa fa-history"></i> Syn Tree', callback:()=>{
							iottree_node_syn_tree(cptp,cpid,connid) ;
						}});
					}
					d.push({ content : '<i class="fa fa-eye"></i> Monitor', callback:()=>{
						window.open("./conn/cpt_mon.jsp?repid="+repid+"&cpid="+cpid+"&connid="+connid) ;
					}});
					d.push({content:'sm_divider'});
					d.push({ content : '<i class="fa fa-pencil"></i> Edit', callback:()=>{
						edit_cpt(cptp,cpid,connid) ;
					}});
					d.push({ content : '<i class="fa fa-times"></i> Delete', callback:()=>{
						del_cpt(cpid,connid);
					}}) ;
					return d;
				}
	        });
	    }
	})
}



var cxt_menu = {
	"prj":[
		
		{op_name:"paste_ch",op_title:"Paste Channel",op_icon:"fa fa-clipboard",op_action:act_node_paste,op_chk:(tn)=>{
			//console.log(copiedItem);
			return copiedItem!=null&&copiedItem.type=="ch";
		}},
		{op_name:"new_ch",op_title:"<wbt:lang>new_ch</wbt:lang>",op_icon:"fa fa-random",op_action:act_rep_new_ch},
		{op_name:"new_hmi",op_title:"<wbt:lang>new_hmi</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:act_new_hmi},
		{op_name:"new_tag_exp",op_title:"<wbt:lang>new_tag_mid</wbt:lang>",op_icon:"fa fa-compass",op_action:""},
		
		{op_name:"edit_prj",op_title:"Edit Project",op_icon:"fa fa-pencil",op_action:act_edit_prj},
		
		{op_name:"open_cxt",op_title:"<wbt:lang>cxt_script_test</wbt:lang>",op_icon:"fa fa-list-alt",op_action:act_open_cxt_script},
		
		{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o",op_action:act_prop,default:true},
		{op_name:"start_stop",op_title:"<wbt:lang>start/stop</wbt:lang>",op_icon:"fa fa-refresh",op_action:""},
	],
	"ch":[
		{op_name:"paste_dev",op_title:"Paste Device",op_icon:"fa fa-clipboard",op_action:act_node_paste,op_chk:(tn)=>{
			//console.log(copiedItem);
			return copiedItem!=null&&copiedItem.type=="dev";
		}},
		{op_name:"sel_drv",op_title:"<wbt:lang>select_drv</wbt:lang>",op_icon:"fa fa-tasks",op_action:act_ch_sel_drv},
		
		
		{op_name:"new_tagg",op_title:"<wbt:lang>new_tag_group</wbt:lang>",op_icon:"fa fa-tags",op_action:act_new_tagg,op_chk:(tn)=>{
			return !tn.ref_locked;
		}},
		{op_name:"new_dev",op_title:"<wbt:lang>new_dev</wbt:lang>",op_icon:"fa fa-tasks",op_action:act_ch_new_dev},
		{op_name:"edit_ch",op_title:"<wbt:lang>edit_ch</wbt:lang>",op_icon:"fa fa-tasks",op_action:act_edit_ch},
		{op_name:"del_ch",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times",op_action:act_del_ch},
		
		{op_name:"new_hmi",op_title:"<wbt:lang>new_hmi</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:act_new_hmi},
		//{op_name:"new_tag_exp",op_title:"<wbt:lang>new_tag_mid</wbt:lang>",op_icon:"fa fa-tag",op_action:""},
		
		{op_name:"cp_ch",op_title:"Copy",op_icon:"fa fa-files-o",op_action:act_node_copy},
		
		{op_name:"ch_start",op_title:"<wbt:lang>start</wbt:lang>",op_icon:"fa fa-times",op_action:act_ch_start_stop,op_chk:(tn)=>{
			return !tn.run;
		}},
		{op_name:"ch_stop",op_title:"<wbt:lang>stop</wbt:lang>",op_icon:"fa fa-times",op_action:act_ch_start_stop,op_chk:(tn)=>{
			return tn.run;
		}},
		{op_name:"open_cxt",op_title:"<wbt:lang>cxt_script_test</wbt:lang>",op_icon:"fa fa-list-alt",op_action:act_open_cxt_script},
		{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o",op_action:act_prop,default:true}
	],
	"dev":[
		{op_name:"new_hmi",op_title:"<wbt:lang>new_hmi</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:act_new_hmi},
		{op_name:"edit_dev",op_title:"<wbt:lang>edit_dev</wbt:lang>",op_icon:"fa fa-tasks",op_action:act_edit_dev},
		//{op_name:"refresh_dev",op_title:"<wbt:lang>refresh_dev</wbt:lang>",op_icon:"fa fa-tasks",op_action:act_refresh_dev},
		{op_name:"del_dev",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times",op_action:act_del_dev},
		
		{op_name:"cp_dev",op_title:"Copy",op_icon:"fa fa-files-o",op_action:act_node_copy},
		{op_name:"add_to_lib",op_title:"Add To Library",op_icon:"fa fa-files-o",op_action:act_node_add_to_lib},
		{op_name:"new_tagg",op_title:"<wbt:lang>new_tag_group</wbt:lang>",op_icon:"fa fa-tags",op_action:act_new_tagg,op_chk:(tn)=>{
			return !tn.ref_locked;
		}},
		{op_name:"open_cxt",op_title:"<wbt:lang>cxt_script_test</wbt:lang>",op_icon:"fa fa-list-alt",op_action:act_open_cxt_script},
		{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o",op_action:act_prop,default:true}
	],
	"tagg":[
		
		{op_name:"new_hmi",op_title:"<wbt:lang>new_hmi</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:act_new_hmi,op_chk:(tn)=>{
			return !tn.in_dev;
		}},
		{op_name:"modify_tagg",op_title:"<wbt:lang>modify</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:act_edit_tagg,op_chk:(tn)=>{
			return true;// !tn.in_dev;
		}},
		{op_name:"new_tagg",op_title:"<wbt:lang>new_tag_group</wbt:lang>",op_icon:"fa fa-tags",op_action:act_new_tagg,op_chk:(tn)=>{
			return !tn.ref_locked;
		}},
		{op_name:"del_tagg",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times",op_action:act_del_tagg,op_chk:(tn)=>{
			return true;//!tn.ref_locked;
		}}
	],
	"hmi":[
		{op_name:"edit_ui",op_title:"<wbt:lang>edit_ui</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:act_hmi_edit_ui,op_chk:(tn)=>{
			return !tn.ref;
		}},
		{op_name:"modify_ui",op_title:"<wbt:lang>modify</wbt:lang>",op_icon:"fa fa-puzzle-piece",op_action:act_edit_hmi,op_chk:(tn)=>{
			return !tn.ref;
		}},
		{op_name:"del_ui",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times",op_action:act_del_hmi,op_chk:(tn)=>{
			return !tn.ref;
		}},
		{op_name:"access_ui",op_title:"<wbt:lang>access</wbt:lang>",op_icon:"fa fa-paper-plane",op_action:act_access_hmi},
		{op_name:"set_main_ui",op_title:"Set as main ui",op_icon:"fa fa-star",op_action:act_main_hmi,op_chk:(tn)=>{
			return !tn.ref;
		}},
	]
}

function on_tree_node_selected(tn)
{
	$("#if_prop").attr("src","./ua/ui_prop.jsp?dlg=false&&tabid=drv&path="+tn.path) ;
	$("#if_tags").attr("src","./ua_cxt/cxt_tags.jsp?tabid=main&path="+tn.path);
	//$("#if_context").attr("src","./ua_cxt/cxt_accessor.jsp?tabid=main&path="+tn.path);
	//$("#if_context").attr("src","./ua_cxt/cxt_tags.jsp?tabid=main&path="+tn.path);
}

function on_tree_loaded(data)
{
	//console.log("loaded tree",data) ;
}

var ua_panel = new UAPanel(
		{eleid:"conn" ,data_url:"./conn/cp_ajax.jsp?op=list&prjid="+repid,ui_showed:on_conn_ui_showed},
		{eleid:"conn_ch",join_url:"./conn/cp_ajax.jsp?prjid="+repid},
		{eleid:"tree",data_url:"prj_tree_ajax.jsp?id="+repid,cxt_menu:cxt_menu,
				on_selected:on_tree_node_selected,
				on_loaded:on_tree_loaded}
		);
ua_panel.init() ;

function prj_run(b)
{
	var op = "start" ;
	if(!b)
		op = "stop" ;

	var pm = {
			type : 'post',
			url : "./ua/prj_ajax.jsp",
			data :{id:repid,op:op}
		};
		$.ajax(pm).done((ret)=>{
			dlg.msg(ret);
		}).fail(function(req, st, err) {
			dlg.msg(err);
		});
	
}

function update_prj_run(brun)
{
	if(brun)
	{
		$("#prj_btn_start").css("color","grey") ;
		$("#prj_btn_stop").css("color","red") ;
	}
	else
	{
		$("#prj_btn_start").css("color","#8ecf6a") ;
		$("#prj_btn_stop").css("color","grey") ;
	}
}

function refresh_ui()
{
	ua_panel.refresh_ui();
}

function act_node_copy(n,op)
{
	copiedItem = n ;
}

function act_node_paste(n,op)
{
	if(copiedItem==null)
		return ;
	let ppath=n.path ;
	
	if(n.type=="prj")
	{//paste ch
		if(copiedItem.type!='ch')
			return ;
		
	}
	else if(n.type=='ch')
	{
		if(copiedItem.type!='dev')
			return ;
	}
	else
		return ;
	send_ajax("./ua/node_copy_paste_ajax.jsp",{ppath:n.path,path:copiedItem.path},(bsucc,ret)=>{
		if(!bsucc||ret!="succ")
		{
			dlg.msg(ret) ;
			return ;
		}
		refresh_ui();
	});
	
}



function edit_cp(cptp,cpid)
{
	dlg.open_win("conn/cp_edit_"+cptp+".jsp?prjid="+repid+"&cpid="+cpid,
			{title:"["+cptp+"] Connections Provider Editor",w:'800',h:'535'},
			['Ok',{title:'Apply',style:"warm",enable:false},{title:'Cancel',style:"primary"},{title:'Help',style:"primary"}],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						if(!bsucc)
						{
							dlg.msg(ret) ;
							return ;
						}
						ret.tp = cptp;
						set_connprovider(ret,(bok,msg)=>{
							if(!bok)
							{
								dlg.msg(msg) ;
								return ;
							}
							refresh_ui();
							dlg.close();
						}) ;
					});
				},
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						if(!bsucc)
						{
							dlg.msg(ret) ;
							return ;
						}
						set_connprovider(ret,(bok,msg)=>{
							if(!bok)
							{
								dlg.msg(msg) ;
								return ;
							}
							dlg.btn_set_enable(1,false);
							dlgw.setDirty(false);
							ua_panel.ua_conn.refresh_ui();
						}) ;
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



function del_cp(cpid)
{
	var pm = {
			type : 'post',
			url : "./conn/cp_ajax.jsp",
			data :{prjid:repid,op:'cp_del',cpid:cpid}
		};
	if(dlg.confirm("delete this connector?",null,()=>{
		$.ajax(pm).done((ret)=>{
			if(typeof(ret)=='string')
				eval("ret="+ret);
			if(!ret.res)
			{
				dlg.msg(ret.err) ;
				return ;
			}
			ua_panel.ua_conn.refresh_ui();
		}).fail(function(req, st, err) {
			dlg.msg(err);
		});
	})) ;
}

function config_wizard(cptp,cpid)
{
	dlg.open("conn/wizard_ash_to_ch.jsp?prjid="+prjid+"&cpid="+cpid,
			{title:"generation of connector and channel Wizard",w:'500px',h:'400px'},
			['Generate','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						
						ret.prjid=prjid;
						ret.cpid = cpid ;
						let pm = {jsontxt:JSON.stringify(ret)} ;
						
						dlg.loading(true);
						send_ajax('conn/wizard_ash_to_ch_ajax.jsp',pm,function(bsucc,ret)
						{
							dlg.loading(false);
							if(!bsucc || ret.indexOf('succ')<0)
							{
								dlg.msg(""+ret);
								return ;
							}
							dlg.msg(ret);
							dlg.close();
							refresh_ui() ;
						},false);
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function set_connprovider(jo,cb)
{
		var pm = {
				type : 'post',
				url : "./conn/cp_ajax.jsp",
				data :{prjid:repid,op:'cp_set',json:JSON.stringify(jo)}
			};
		$.ajax(pm).done((ret)=>{
			if(typeof(ret)=='string')
				eval("ret="+ret);
			if(ret.res)
				cb(true,"") ;
			else
				cb(false,ret.err) ;
		}).fail(function(req, st, err) {
			dlg.msg(err);
		});
}

function del_cpt(cpid,connid)
{
	var pm = {
			type : 'post',
			url : "./conn/cp_ajax.jsp",
			data :{prjid:repid,op:'conn_del',cpid:cpid,connid:connid}
		};
	if(dlg.confirm("delete this connection?",null,()=>{
		$.ajax(pm).done((ret)=>{
			if(typeof(ret)=='string')
				eval("ret="+ret);
			if(!ret.res)
			{
				dlg.msg(ret.err) ;
				return ;
			}
			ua_panel.ua_conn.refresh_ui();
		}).fail(function(req, st, err) {
			dlg.msg(err);
		});
	})) ;
}

function edit_bind_setup(cptp,cpid,connid)
{
	dlg.open_win("./conn/cpt_bind_sel.jsp?prjid="+repid+"&cptp="+cptp+"&cpid="+cpid+"&connid="+connid,
			{title:"Binding Setting ["+cptp+"]",w:'800',h:'550'},
			['Ok',{title:'Cancel',style:"primary"}],
			[
				function(dlgw)
				{
					var bindids = dlgw.get_selected_vals();
					
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function edit_mqtt_test(cptp,cpid,connid)
{
	dlg.open_win("./conn/ext/cpt_msg_pub_test.jsp?prjid="+repid+"&cptp="+cptp+"&cpid="+cpid+"&connid="+connid,
			{title:"Message Send/Publish Tester ["+cptp+"]",w:'800',h:'550'},
			[{title:'Close',style:"primary"}],
			[
				function(dlgw)
				{
					dlg.close();
				}
				
			]);
}

function iottree_node_syn_tree(cptp,cpid,connid)
{
	dlg.open_win("./conn/ext/cpt_iottree_node_syn.jsp?prjid="+repid+"&cptp="+cptp+"&cpid="+cpid+"&connid="+connid,
			{title:"IOTTree Node Synchroniation ["+cptp+"]",w:'800',h:'550'},
			[{title:'Close',style:"primary"}],
			[
				function(dlgw)
				{
					dlg.close();
				}
				
			]);
}

function edit_cpt(cptp,cpid,connid)
{
	dlg.open_win("conn/cpt_edit_"+cptp+".jsp?prjid="+repid+"&cptp="+cptp+"&cpid="+cpid+"&connid="+connid,
			{title:cptp+" Connection Editor",w:'800',h:'550'},
			['Ok',{title:'Apply',style:"warm",enable:false},{title:'Cancel',style:"primary"},{title:'Help',style:"primary"}],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						if(!bsucc)
						{
							dlg.msg(ret) ;
							return ;
						}
						set_connpt(ret,cptp,cpid,(bok,msg)=>{
							if(!bok)
							{
								dlg.msg(msg) ;
								return ;
							}
							refresh_ui();
							dlg.close();
						}) ;
					});
				},
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						if(!bsucc)
						{
							dlg.msg(ret) ;
							return ;
						}
						set_connpt(ret,cptp,cpid,(bok,msg)=>{
							if(!bok)
							{
								dlg.msg(msg) ;
								return ;
							}
							dlg.btn_set_enable(1,false);
							dlgw.setDirty(false);
							ua_panel.ua_conn.refresh_ui();
						}) ;
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

function act_del_tagg(n,op)
{
	tagg_del(n.path,()=>{
		refresh_ui() ;
	}) ;
}

function set_connpt(jo,cptp,cpid,cb)
{
		var pm = {
				type : 'post',
				url : "./conn/cp_ajax.jsp",
				data :{prjid:repid,cptp:cptp,cpid:cpid,op:'conn_set',json:JSON.stringify(jo)}
			};
		$.ajax(pm).done((ret)=>{
			if(typeof(ret)=='string')
				eval("ret="+ret);
			if(ret.res)
				cb(true,"") ;
			else
				cb(false,ret.err) ;
		}).fail(function(req, st, err) {
			dlg.msg("ajax err="+st);
		});
}


function act_prop(n,op)
{
	//repid="+repid+"&id="+n.id
	dlg.open_win("ua/ui_prop.jsp?path="+n.path,
			{title:"Properties",w:'800',h:'535'},
			['Ok',{title:'Apply',style:"warm",enable:false},{title:'Cancel',style:"primary"},{title:'Help',style:"primary"}],
			[
				function(dlgw)
				{
					if(!dlgw. isDirty())
					{
						dlg.close();
					}
					dlgw.do_apply(()=>{
						refresh_ui();
						dlg.close();
					});
					
					//save_prop(dlgw,n.id,()=>{
					//	refresh_ui();
					//	dlg.close();
					//});
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


function act_rep_new_ch(n,op)
{
	add_or_edit_ch(n.path,null);
}

function act_edit_ch(n,op)
{
	add_or_edit_ch(null,n.path);
}

function act_edit_prj(n,op)
{
	dlg.open("ua/prj_edit.jsp?id="+prjid,
			{title:"Edit Project",w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 //enable_btn(true);
							 return;
						 }
						ret.id = prjid;
						ret.op="edit" ;
						send_ajax('./ua/prj_ajax.jsp',ret,function(bsucc,rr)
						{
							if(!bsucc || rr.indexOf('succ')<0)
							{
								dlg.msg(rr);
								return ;
							}
							 dlg.close();
							 document.location.href=document.location.href;
						},false);
							
						 //console.log(ret);
						
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function act_del_ch(n,op)
{
	dlg.confirm("delete this channel?",null,()=>{
		var ret={} ;
		 ret.ch_path = n.path;
		 ret.op="del" ;
		 send_ajax('ua/ch_ajax.jsp',ret,function(bsucc,ret)
			{
				if(!bsucc || ret.indexOf('succ')<0)
				{
					dlg.msg(ret);
					return ;
				}
				dlg.close();
				refresh_ui() ;
			},false);
	});
	
}

function add_or_edit_ch(rep_path,ch_path)
{
	var tt = null;
	var u = null ;
	var bedit = false ;
	if(rep_path!=null)
	{
		tt = "<wbt:lang>add_ch</wbt:lang>";
		u = "ua/ch_edit.jsp?rep_path="+rep_path ;
	}
	else
	{
		tt = "<wbt:lang>edit_ch</wbt:lang>";
		u = "ua/ch_edit.jsp?ch_path="+ch_path ;
		bedit=true;
	}
	
	dlg.open(u,{title:tt,w:'500px',h:'400px'},
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
						 //ret.repid=repid ;
						 if(bedit)
						 {
							 ret.ch_path = ch_path;
							 ret.op="edit" ;
						 }
						 else
					     {
							 ret.rep_path = rep_path;
							 ret.op="add" ;
					     }
						 
						send_ajax('ua/ch_ajax.jsp',ret,function(bsucc,ret)
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
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function act_ch_sel_drv(n,op)
{
	dlg.open("dev/drv_ch_selector.jsp?repid="+repid+"&chid="+n.id,
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
						 
						 ret.repid=repid ;
						 ret.chid = n.id;
						 ret.op='ch_drv_set';
						 //console.log(ret);
						 send_ajax('dev/drv_ajax.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.close();
								//refresh_ui() ;
								ua_panel.redraw(false,false,true) ;
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

function act_node_add_to_lib(n,op)
{
	dlg.open("dev/devdef_add_from_prj.jsp?devpath="+n.path,
			{title:"Add to Library",w:'500px',h:'400px'},
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
						 
						 ret.devpath=n.path ;
						 ret.op='add_by_prj';
						 //console.log(ret);
						 send_ajax('dev/devdef_ajax.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.msg("add ok");
								dlg.close();
								
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

function act_ch_start_stop(n,op)
{
	var ret={} ;
	 ret.ch_path = n.path;
	 ret.op="start_stop" ;
	 send_ajax('ua/ch_ajax.jsp',ret,function(bsucc,ret)
		{
			if(!bsucc || ret.indexOf('succ')<0)
			{
				dlg.msg(ret);
				return ;
			}
			dlg.msg("cmd issued") ;
			//refresh_ui() ;
		},false);
}

function act_ch_new_dev(n,op)
{
	add_or_edit_dev(n.path,null) ;
}

function act_edit_dev(n,op)
{
	add_or_edit_dev(null,n.path) ;
}

function act_refresh_dev(n,op)
{
	var ret={} ;
	 ret.dev_path = n.path;
	 ret.op="refresh" ;
	 send_ajax('ua/dev_ajax.jsp',ret,function(bsucc,ret)
		{
			if(!bsucc || ret.indexOf('succ')<0)
			{
				dlg.msg(ret);
				return ;
			}
			dlg.close();
			refresh_ui() ;
		},false);
}

function act_del_dev(n,op)
{
	dlg.confirm("delete this device?",null,()=>{
		var ret={} ;
		 ret.dev_path = n.path;
		 ret.op="del" ;
		 send_ajax('ua/dev_ajax.jsp',ret,function(bsucc,ret)
			{
				if(!bsucc || ret.indexOf('succ')<0)
				{
					dlg.msg(ret);
					return ;
				}
				dlg.close();
				refresh_ui() ;
			},false);
	});	
}

function add_or_edit_dev(ch_path,dev_path)
{
	var tt = null;
	var u = null ;
	var bedit = false ;
	if(ch_path!=null)
	{
		tt = "<wbt:lang>add_dev</wbt:lang>";
		u = "ua/dev_edit.jsp?ch_path="+ch_path ;
	}
	else
	{
		tt = "<wbt:lang>edit_dev</wbt:lang>";
		u = "ua/dev_edit.jsp?dev_path="+dev_path ;
		bedit=true;
	}
	dlg.open(u,{title:tt,w:'500px',h:'400px'},
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
					 
					 if(bedit)
					 {
						 ret.dev_path = dev_path;
						 ret.op="edit" ;
					 }
					 else
				     {
						 ret.ch_path = ch_path;
						 ret.op="add" ;
				     }
					 
					 //console.log(ret);
					 send_ajax('ua/dev_ajax.jsp',ret,function(bsucc,ret)
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

function act_access_hmi(n,op)
{
	window.open(n.path);
}

function act_main_hmi(n,op)
{
	var ret={} ;
	 ret.path = n.path;
	 ret.op="main" ;
	 send_ajax('ua_hmi/hmi_editor_ajax.jsp',ret,function(bsucc,ret)
		{
			dlg.msg(ret) ;
		},false);
}

function act_hmi_edit_ui(n,op)
{
	//window.open("ua_hmi/hmi_editor.jsp?repid="+repid+"&id="+u.getId()) ;
	add_tab(n.id,n.title,"/admin/ua_hmi/hmi_editor_ui.jsp?tabid="+n.id+"&path="+n.path) ;
}

/*
function act_new_hmi(n,op)
{
	dlg.open("ua/hmi_edit.jsp",
			{title:"Add HMI",w:'500px',h:'400px'},
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
						 
						 ret.op='add';//repid=repid ;
						 ret.path = n.path;
						 send_ajax('ua/hmi_ajax.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								//dlg.msg(ret);
								dlg.close();
								ua_panel.redraw(false,false,true) ;
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

function act_hmi_edit_ui(n,op)
{
	//window.open("ua_hmi/hmi_editor.jsp?repid="+repid+"&id="+u.getId()) ;
	add_tab(n.id,n.title,"ua_hmi/hmi_editor_ui.jsp?tabid="+n.id+"&path="+n.path) ;
}
*/

function act_open_cxt_script(n,op)
{
	
	dlg.open("ua_cxt/cxt_script.jsp?path="+n.path,
			{title:"Context Script Tester",w:'450px',h:'500px'},
			['Close'],
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
	if(curTabIF.contentWindow.draw_fit)
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
	dlg.open_win("dev/dev_lib_lister.jsp?mgr=true",
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


var resize_cc = 0 ;
$(window).resize(function(){
	resize_iframe_h();
	
	resize_tree();
	//panel.updatePixelSize() ;
	resize_cc ++ ;
	if(resize_cc<=1)
		draw_fit();
	});
	
function clk_share_run()
{
	event.stopPropagation();
	share_as_node();
}

function clk_task_run()
{
	event.stopPropagation();
	task_setup();
}
	
function prj_rt()
{
	send_ajax("./prj_rt_ajax.jsp",{id:repid},(bsucc,ret)=>{
		if(!bsucc)
			return ;
		var v = ret ;
		update_prj_run(ret.run);
		
		var c = "grey" ;
		var t = "not share";
		if(ret.share)
		{
			if(ret.share_run)
			{
				c = "green" ;
				t = "Share and connection ok" ;
			}
			else
			{
				c = "red" ;
				t = "share and connection failure "
			}
		}
		$("#share_run").css("color",c);
		$("#share_run").attr("title",t);
		
		c = "grey" ;
		t = "no task";
		if(ret.task_run_num>0)
		{
			c = "green" ;
			t = ret.task_run_num+" tasks running" ;
			$("#task_run_icon").addClass("fa-spin").css("color",c);
			
		}
		else
		{
			c = "red" ;
			t = "no task running "
			$("#task_run_icon").removeClass("fa-spin").css("color",c);
		}
		
		$("#task_run").attr("title",t);
		
		for(var cp of ret.cps)
		{
			var id =cp.cp_id ;
			var brun = cp.run ;
			$("#cp_st_"+id).css("background-color",brun?"green":"red");
			$("#cp_"+id).attr("cp_run",""+brun) ;
			for(var conn of cp.connections)
			{
				var cid = conn.conn_id;
				var bready = conn.ready ;
				var connerr = conn.conn_err;
				
				$("#conn_st_"+cid).html(bready?"<i class='fa fa-link fa-lg'></i>":"<i class='fa fa-chain-broken fa-lg'></i>");//.css("background-color",bready?"green":"red");
				$("#conn_st_"+cid).css("color",bready?"green":"red");
				$("#conn_st_"+cid).attr("title",bready?"connection is ready":connerr);
				$("#conn_run_"+cid).css("color",bready?"green":"red");
				$("#conn_"+cid).attr("conn_ready",""+bready) ;
				
				var tt = conn.static_txt ;
				if(tt==null||tt=="")
					tt = bready?"connection is ready":connerr ;
				$("#conn_"+cid).attr("title",tt) ;
			}
		}
		
		for(var ch of ret.chs)
		{
			var id = ch.ch_id ;
			var brun = ch.run ;
			if(brun)
				$("#ch_run_"+id).addClass("fa-spin").css("color","#17c680");
			else
				$("#ch_run_"+id).removeClass("fa-spin").css("color","red");
			var tn = ua_panel.ua_tree.get_node_by_id(id) ;
			if(tn!=null)
				tn.run = brun ;
		}
	},false) ;
}

function rt_cp_start_stop(cp_id)
{
	var ele = $("#cp_"+cp_id) ;
	var pm={repid:repid,cp_id:cp_id} ;
	var tt = "stop connector issued" ;
	if("true"==ele.attr("cp_run"))
		pm.op="cp_stop" ;
	else
	{
		pm.op="cp_start" ;
		tt = "start connector issued" ;
	}
	send_ajax("./conn/rt_ajax.jsp",pm,(bsucc,ret)=>{
		if(!bsucc)
			return ;
		dlg.msg(tt) ;
		return ;
	},false) ;
}


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
	


	//if(hmi_main.path!=null&&hmi_main.path!="")
	//	add_tab(hmi_main.id,hmi_main.title,"/admin/ua_hmi/hmi_editor_ui.jsp?tabid="+hmi_main.id+"&path="+hmi_main.path) ;

});

setInterval(prj_rt,3000) ;
</script>
<script src="./js/split.js"></script>
</body>
</html>