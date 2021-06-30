<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%><%@ page import="java.util.*,
				java.io.*,
				java.net.*,
				java.util.*,
				org.iottree.core.*,
				org.iottree.core.util.*,
				java.net.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%if(!Convert.checkReqEmpty(request, out, "id"))
		return;
	String id = request.getParameter("id");
	UAPrj dc = UAManager.getInstance().getPrjById(id);
	if(dc==null)
	{
		out.print("no container found!");
		return;
	}
	
	String name = dc.getName() ;
	//List<DevConnProvider> cps = dc.listConnProviders();%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Repository</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<link rel="stylesheet" href="/_js/jstree/themes/default/style.min.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<!-- 
<script src="/_js/echarts/echarts.min.js"></script>
 -->
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script src="/_js/oc/oc.js"></script>
<link type="text/css" href="/_js/oc/oc.css" rel="stylesheet" />
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
<script src="/_js/jstree/jstree.min.js"></script>
<script src="./js/ua_tree.js"></script>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
-moz-user-select : none;
-webkit-user-select: none;
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



.left {
	position: relative;
	float0: left;
	left: 0;
	top: 0;
	bottom: 0;
	width: 255px;
	overflow-x0: hidden
}

.right {
	position: fixed;
	float: right;
	right: 0;
	top: 0;
	bottom: 0;
	z-index: 999;
	width: 245px;
	height: 100%;
	overflow-x: hidden
}

.mid {
	position: relative;
	left: 255px;
	right: 245px;
	top: 0;
	bottom: 0;
	z-index: 998;
	width: auto;
	overflow: hidden;
	box-sizing: border-box
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


.layui-icon-file:before {
    content0: "";
}

 .tree-txt-active{
    color :#ffffff;
    background-color: #0078d7;
  }
  
</style>

</head>
<script type="text/javascript">


</script>
<body class="layout-body">

		<div id="left_panel" class="left" style0="background-color: #333333">
		  <i class="icon_dev">&nbsp;&nbsp;&nbsp;</i>
			  
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
		

	</div>


<div id='edit_panel' style="display:none;border: 1; font: 15; position: absolute; top: 3px; width: 30%; height: 90%; right: 50px; background-color: window; z-index: 60000; overFlow0: auto">
	<div style="background-color: rgb(200, 200, 200); border: 1; border-bottom-style: inset; margin: 1">
		[main]</div>
	<div
		style="background-color: olive; color: white; border: 1; border-bottom-style: inset; margin: 1; text-align: left">
		 
		 <input class="layui-btn layui-btn-primary layui-btn-sm" name='save' type='button' value='保存模板' onclick="btn_save_temp()" title="ctrl+b" />
		 <input class="layui-btn layui-btn-primary layui-btn-sm" name='save' type='button' value='保存内容' onclick="btn_save_cont()" title="ctrl+b" />
		 
		 <input type="button" value="Apply" onclick="do_apply()" class="layui-btn layui-btn-primary layui-btn-sm" />
	</div>

	<div id="p_info" style="background-color: grey; height: 20">&nbsp;</div>



	
</div>




<script>

var repid="<%=id%>";
var repname="<%=name%>" ;
var tree_data=[] ;

layui.use('element', function(){
  var element = layui.element;

  
});

/*
$(function () {
    // 6 create an instance when the DOM is ready
    $('#jstree').jstree();
    // 7 bind to events triggered on the tree
    $('#jstree').on("changed.jstree", function (e, data) {
      console.log(data.selected);
    });
    // 8 interact with the tree - either way is OK
    $('button').on('click', function () {
      $('#jstree').jstree(true).select_node('child_node_1');
      $('#jstree').jstree('select_node', 'child_node_1');
      $.jstree.reference('#jstree').select_node('child_node_1');
    });
  });
  

  
	$(window).resize(function () {
		var h = Math.max($(window).height() - 0, 420);
		$('#container, #data, #tree, #data .content').height(h).filter('.default').css('lineHeight', h + 'px');
	}).resize();
  */
  
function init_tree()
{
		$('#jstree').jstree({
			'core' : {
				'data' : {
					"url" : "./prj_tree_ajax.jsp?id="+repid,
					"dataType" : "json" // needed only if you do not supply JSON headers
				}
			}
		});
}

  
//init_tree();
  
function load_tree()
{
	$.ajax({
        type: 'post',
        url:'./prj_tree_ajax.jsp',
        data: {id:repid},
        async: true,  
        success: function (result) {  
        	if(result.indexOf('[')!=0)
        	{
        		dlg.msg(result) ;
        		return ;
        	}
        	eval("tree_data="+result) ;
        	tree.render({
        		  elem: '#left_panel'
        		  ,data: tree_data
        		  ,showCheckbox: false
        		  ,onlyIconControl:true
        		  ,showLine:false
        		  ,id: 'demoId1'
        		  ,isJump: true
        		  ,click: function(obj){
        		    var data = obj.data;
        		    dlg.msg('状态：'+ obj.state + '<br>节点数据：' + JSON.stringify(data));
        		  }
        		});
        },
        error:function(req,err,e)
        {
        	dlg.msg(e);
        }
    });
}

//load_tree();

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
	
var tab_id="main";

function tab_save()
{
	btn_save_cont();
}

function tab_notify()
{
	parent.tab_notify(tab_id);
}

function tab_st()
{
	return {tabid:tab_id,dirty:panel.isModelDirty()} ;
}

function log(txt)
{
	console.log(txt) ;
}

var ws = null;


function ws_conn()
{
    var url = 'ws://' + window.location.host + '/admin/ws/cxt_rt/'+repname+"/aaa";
    if ('WebSocket' in window) {
        ws = new WebSocket(url);
    } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket(url);
    } else {
        alert('WebSocket is not supported by this browser.');
        return;
    }
    ws.onopen = function () {
        //setConnected(true);
        log('Info: WebSocket connection opened.');
    };
    ws.onmessage = function (event) {
        //log('Received: ' + event.data);
        //log(event.data.length) ;
        iottModel.fireModelDynUpdated(event.data) ;
    };
    ws.onclose = function (event) {
       
        log('Info: WebSocket connection closed, Code: ' + event.code + (event.reason == "" ? "" : ", Reason: " + event.reason));
    };
}	

function ws_disconn() {
    if (ws != null) {
        ws.close();
        ws = null;
    }
}

//ws_conn();
</script>
</body>
</html>