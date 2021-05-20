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
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<!-- 
<script src="/_js/echarts/echarts.min.js"></script>
 -->
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
		 
		 <input class="layui-btn layui-btn-primary layui-btn-sm" name='save' type='button' value='保存模板' onclick="btn_save_temp()" title="ctrl+b" />
		 <input class="layui-btn layui-btn-primary layui-btn-sm" name='save' type='button' value='保存内容' onclick="btn_save_cont()" title="ctrl+b" />
		 
		 <input type="button" value="Apply" onclick="do_apply()" class="layui-btn layui-btn-primary layui-btn-sm" />
	</div>

	<div id="p_info" style="background-color: grey; height: 20">&nbsp;</div>

<div class="layui-tab"><span id="topm_filter_x" class="top_menu_close" style="position:absolute;top:1px,right:10px">X</span>
  <ul class="layui-tab-title">
    <li class="layui-this">属性</li>
    <li>事件</li>
    <li>操作</li>
  </ul>
  <div class="layui-tab-content">
    <div class="layui-tab-item layui-show">
	  <div id='edit_props' style="height: 450px;overflow: auto;" ></div>
	</div>
    <div class="layui-tab-item">
      <div id='edit_events' style="height: 100%;overflow: auto;"></div>
	</div>
    <div class="layui-tab-item">
    	
    	<div id="tabs-3" style="overflow: scroll; height: 200px">
		
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Group" onclick="do_add_di('oc.DrawItemGroup')" />
        <input type="button" value="Win" onclick="do_add_di('oc.iott.Win')" />
        <input type="button" value="Add Line" onclick="do_add_di('oc.di.DILine')" />
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Add Rect" onclick="do_add_di('oc.di.DIRect')" />
		<input type="button" value="Add Txt" onclick="do_add_di('oc.di.DITxt')" />
        <input type="button" value="Add Img" onclick="do_add_di('oc.di.DIImg')" />
        <input type="button" value="Add Icon" onclick="do_add_di('oc.di.DIIcon')" />
        <input type="button" value="Add Div" onclick="do_add_di('oc.iott.DIDivList')" />
        <input type="button" value="Add Div Comp" onclick="do_add_di('oc.DIDivComp')" />
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Add Arc" onclick="do_add_di('oc.di.DIArc')" />
		<input type="button" value="Add Pts Rect" onclick="do_add_di('oc.di.DIBasic',{pts_tp:'rect'})" />
	    <input type="button" value="Add Pts Diamond" onclick="do_add_di('oc.di.DIBasic',{pts_tp:'diamond'})" /> <br>
		<input class="layui-btn layui-btn-primary layui-btn-sm" type="button" value="Add Unit Ins [u1]" onclick="do_add_unit_ins('u1')" />
		
		
	</div>
    </div>
  </div>
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
    <div class="layui-tab-item layui-show" >
    	<iframe id="plug_unit" width="95%" height="510" src="ua/ui_unit_list.jsp" style="overflow: hidden;width:100%;margin: 0"> </iframe>
	</div>
    <div class="layui-tab-item">
      <iframe id="plug_conn" width="95%" height="510" src="ua/ui_conn_list.jsp" style="overflow: hidden;width:100%;margin: 0"> </iframe>
	</div>
    <div class="layui-tab-item">
    	<iframe id="plug_icon" width="95%" height="510" src="pic/icon_fa.jsp" style="overflow: hidden;width:100%;margin: 0"> </iframe>
    </div>
  </div>
</div>
	
</div>

<script>

var repid="<%=id%>";
var repname="<%=name%>" ;

layui.use('element', function(){
  var element = layui.element;
  
  //…
});

var panel = null;
var editor = null ;

var loadLayer = null ;
var intedit =null;

var iottModel=null;
var iottView=null;

function on_panel_mousemv(p,d)
{
	$("#p_info").html("["+p.x+","+p.y+"] - ("+Math.round(d.x*10)/10+","+Math.round(d.y*10)/10+")");
}

function init_iottpanel()
{
	iottModel = new oc.iott.IOTTModel({
		temp_url:"ua/ui_temp_ajax.jsp?op=load&id="+repid,
		unit_url:"ua/ui_unit_ajax.jsp?op=load_all",
		cont_url:"ua/ui_cont_ajax.jsp?op=load&id="+repid,
		dyn_url:"ua/ui_dyn_ajax.jsp?id="+repid
	});
	
	panel = new oc.DrawPanel("main_panel",{
		on_mouse_mv:on_panel_mousemv
	});
	editor = new oc.DrawEditor("edit_props","edit_events",panel,{
		
	}) ;
	//repid,""
	iottView = new oc.iott.IOTTView(iottModel,panel,editor,{
		copy_paste_url:"util/copy_paste_ajax.jsp"
	});
	
	oc.PopMenu.setMenuTp2Items({
		"layer":[
			//{op_name:"new_tag_exp",op_title:"New Tag Middle",op_icon:"fa fa-compass fa-lg",action:act_dev_new_tag_mid},
			{op_name:"new_ch",op_title:"<wbt:lang>new_ch</wbt:lang>",op_icon:"fa fa-random fa-lg",action:act_ch_new_ch},
			{op_name:"open_cxt",op_title:"<wbt:lang>data_cxt</wbt:lang>",op_icon:"fa fa-list-alt fa-lg",action:act_open_data_cxt},
			{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o fa-lg",action:act_prop,default:true}
		],
		"unit-rep":[
			{op_name:"new_ch",op_title:"<wbt:lang>new_ch</wbt:lang>",op_icon:"fa fa-random fa-lg",action:act_ch_new_ch},
			{op_name:"new_hmi",op_title:"<wbt:lang>new_hmi</wbt:lang>",op_icon:"fa fa-puzzle-piece  fa-lg",action:act_win_new_hmi},
			{op_name:"new_tag_exp",op_title:"<wbt:lang>new_tag_mid</wbt:lang>",op_icon:"fa fa-compass fa-lg",action:act_dev_new_tag_mid},
			{op_name:"open_cxt",op_title:"<wbt:lang>data_cxt</wbt:lang>",op_icon:"fa fa-list-alt fa-lg",action:act_open_data_cxt},
			{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o fa-lg",action:act_prop,default:true},
			{op_name:"start_stop",op_title:"<wbt:lang>start/stop</wbt:lang>",op_icon:"fa fa-refresh fa-lg",action:act_rep_start_stop},
			{op_name:"mem_add_conn",op_title:"<wbt:lang>add_conn</wbt:lang>",op_icon:"fa fa-refresh fa-lg",action:act_win_new_conn}
		],
		"unit-ch":[
			{op_name:"new_dev",op_title:"<wbt:lang>new_dev</wbt:lang>",op_icon:"fa fa-tasks fa-lg",action:act_ch_new_dev},
			{op_name:"del_ch",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times fa-lg",action:act_ch_del},
			{op_name:"new_hmi",op_title:"<wbt:lang>new_hmi</wbt:lang>",op_icon:"fa fa-puzzle-piece fa-lg",action:act_win_new_hmi},
			{op_name:"new_tag_exp",op_title:"<wbt:lang>new_tag_mid</wbt:lang>",op_icon:"fa fa-tag",action:act_dev_new_tag_mid},
			{op_name:"open_cxt",op_title:"<wbt:lang>data_cxt</wbt:lang>",op_icon:"fa fa-list-alt fa-lg",action:act_open_data_cxt},
			{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o fa-lg",action:act_prop,default:true}
		],
		"unit-dev":[
			{op_name:"new_tag",op_title:"<wbt:lang>new_tag</wbt:lang>",op_icon:"fa fa-tag fa-lg",action:act_dev_new_tag},
			{op_name:"new_tag_exp",op_title:"<wbt:lang>new_tag_mid</wbt:lang>",op_icon:"fa fa-tag  fa-lg",action:act_dev_new_tag_mid},
			{op_name:"new_tagg",op_title:"<wbt:lang>new_tag_group</wbt:lang>",op_icon:"fa fa-tags  fa-lg",action:act_new_tagg},
			{op_name:"del_dev",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times fa-lg",action:act_dev_del},
			{op_name:"new_hmi",op_title:"<wbt:lang>new_hmi</wbt:lang>",op_icon:"fa fa-puzzle-piece  fa-lg",action:act_win_new_hmi},
			{op_name:"open_cxt",op_title:"<wbt:lang>data_cxt</wbt:lang>",op_icon:"fa fa-list-alt  fa-lg",action:act_open_data_cxt},
			{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o  fa-lg",action:act_prop,default:true}
		],
		"unit-tagg":[
			{op_name:"new_tag",op_title:"<wbt:lang>new_tag</wbt:lang>",op_icon:"fa fa-tag fa-lg",action:act_dev_new_tag},
			{op_name:"new_tagg",op_title:"<wbt:lang>new_tag_group</wbt:lang>",op_icon:"fa fa-tags fa-lg",action:act_new_tagg},
			{op_name:"del_tagg",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times fa-lg",action:act_tagg_del}
		],
		"tag_list":[
			{op_name:"new_tag_exp",op_title:"<wbt:lang>new_tag_mid</wbt:lang>",op_icon:"fa fa-compass fa-lg",action:act_dev_new_tag_mid},
		],
		"list_item":[
			//{op_name:"new_tag",op_title:"New Tag",op_icon:"fa fa-compass fa-lg",action:act_dev_new_tag},
			//{op_name:"new_tag_exp",op_title:"New Tag Middle",op_icon:"fa fa-compass fa-lg",action:act_dev_new_tag_mid},
			{op_name:"del_tag",op_title:"<wbt:lang>delete</wbt:lang>",op_icon:"fa fa-times fa-lg",action:act_tag_del},
			{op_name:"w_tag",op_title:"<wbt:lang>write_val</wbt:lang>",op_icon:"fa fa-compass fa-lg",action:act_tag_write},
			{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o  fa-lg",action:act_prop,default:true}
		],
		"div_list":[
			{op_name:"new_tag",op_title:"New Tag",action:act_dev_new_tag},
		],
		"win-conn":[
			{op_name:"new_conn",op_title:"New Conn",action:act_win_new_conn},
		],
		"win-hmi":[
			{op_name:"new_hmi",op_title:"New HMI",op_icon:"fa fa-puzzle-piece",action:act_win_new_hmi},
		],
		"win-store":[
			{op_name:"new_store",op_title:"New Store",action:act_win_new_store},
		],
		"unit-hmi":[
			{op_name:"edit_ui",op_title:"<wbt:lang>edit_ui</wbt:lang>",op_icon:"fa fa-puzzle-piece fa-lg",action:act_hmi_edit_ui},
			{op_name:"view_ui",op_title:"<wbt:lang>view_ui</wbt:lang>",op_icon:"fa fa-puzzle-piece fa-lg",action:act_hmi_view_ui},
			{op_name:"prop",op_title:"<wbt:lang>properties</wbt:lang>",op_icon:"fa fa-newspaper-o  fa-lg",action:act_prop,default:true}
		],
		"unit-store":[
			
			{op_name:"prop",op_title:"Properties",action:act_prop,default:true}
		]
	});
	
	oc.iott.Unit.setActionTp2Item({
		"unit-ch":{ajax_url:"ua/ch_action_view.jsp?repid="+repid,width:100,height:50,pos:0,refresh_interval:500}
	});
	
	iottView.init();
	
	loadLayer = iottView.getLayer();
	intedit = iottView.getInteract();
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

function act_rep_start_stop(n,op,pxy,dxy)
{
	var pm = {
			type : 'post',
			url : "./ua/rep_action_ajax.jsp",
			data :{repid:repid,op:"start_stop"}
		};
	$.ajax(pm).done((ret)=>{
		console.log(ret) ;
		dlg.msg(ret);
		if(ret=='start ok')
			ws_conn() ;
		else
			ws_disconn() ;
	}).fail(function(req, st, err) {
		dlg.msg(err);
	});
}

function act_prop(n,op,pxy,dxy)
{
	dlg.open_win("ua/ui_prop.jsp?repid="+repid+"&id="+n.getId(),
			{title:"Properties",w:'800',h:'535'},
			['Ok',{title:'Apply',style:"warm",enable:false},{title:'Cancel',style:"primary"},{title:'Help',style:"primary"}],
			[
				function(dlgw)
				{
					if(!dlgw. isDirty())
					{
						dlg.close();
					}
					save_prop(dlgw,n.getId(),()=>{
						iottModel.loadOrUpdate();
						dlg.close();
					});
				},
				function(dlgw)
				{
					save_prop(dlgw,n.getId(),()=>{
						dlg.btn_set_enable(1,false);
						dlgw.setDirty(false);
						iottModel.loadOrUpdate();
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

function act_hmi_edit_ui(u,op,pxy,dxy)
{
	//window.open("ua_hmi/hmi_editor.jsp?repid="+repid+"&id="+u.getId()) ;
	parent.add_tab(u.getId(),u.getTitle(),"ua_hmi/hmi_editor_ui.jsp?tabid="+u.getId()+"&repid="+repid+"&id="+u.getId()) ;
}

function act_hmi_view_ui(u,op,pxy,dxy)
{
	window.open("iottree_show.jsp?tp=hmi&repid="+repid+"&id="+u.getId()) ;
}

function act_open_data_cxt(u,op,pxy,dxy)
{
	dlg.open("ua_cxt/cxt_var_lister.jsp?repid="+repid+"&id="+u.getId(),
			{title:"Data Context",w:'850px',h:'600px'},
			['Cancel'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function act_ch_new_ch(u,op,pxy,dxy)
{
	dlg.open("ua/ch_add.jsp",
			{title:"<wbt:lang>add_ch</wbt:lang>",w:'500px',h:'400px'},
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
						 ret.x = dxy.x;
						 ret.y = dxy.y ;
						 //console.log(ret);
							send_ajax('ua/ch_add_do.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.msg(ret);
								dlg.close();
								iottModel.loadOrUpdate();
							},false);
							
						 
						 //document.location.href=document.location.href;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
	//console.log(u.getId()+" "+op);
	//console.log(dxy);
}

function act_ch_del(u,op,pxy,dxy)
{
	dlg.confirm("make sure to delete it？",{btn:["Yes","Cancel"],title:"Delete Confirm"},function ()
    {
				send_ajax('ua/ch_del_do.jsp',{repid:repid,chid:u.getId()},function(bsucc,ret){
					dlg.msg(ret) ;
					iottModel.loadOrUpdate();
					//dlg.msg(ret);
					//dlg.close();
				});
     });
}

function act_dev_del(u,op,pxy,dxy)
{
	dlg.confirm("make sure to delete it？",{btn:["Yes","Cancel"],title:"Delete Confirm"},function ()
	{
		send_ajax('ua/dev_del_do.jsp',{repid:repid,devid:u.getId()},function(bsucc,ret){
			iottModel.loadOrUpdate();
			//dlg.msg(ret);
		});
	});
}

function act_tagg_del(u,op,pxy,dxy)
{
	dlg.confirm("make sure to delete it？",{btn:["Yes","Cancel"],title:"Delete Confirm"},function ()
			{
				send_ajax('ua/tagg_del_do.jsp',{repid:repid,id:u.getId()},function(bsucc,ret){
					iottModel.loadOrUpdate();
					//dlg.msg(ret);
				});
			});
}

function act_tag_del(u,op,pxy,dxy)
{
	dlg.confirm("make sure to delete it？",{btn:["Yes","Cancel"],title:"Delete Confirm"},function ()
			{
				send_ajax('ua/tag_del_do.jsp',{repid:repid,id:u.getId()},function(bsucc,ret){
					iottModel.loadOrUpdate();
					//dlg.msg(ret);
				});
			});
}

function act_ch_new_dev(u,op,pxy,dxy)
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
						 ret.chid = u.getId();
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
								iottModel.loadOrUpdate();
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

function act_new_tagg(u,op,pxy,dxy)
{
	dlg.open("ua/tagg_add.jsp",
			{title:"Add Tag Group",w:'500px',h:'400px'},
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
						 ret.repid=repid ;
						 ret.pid = u.getId();
						 //console.log(ret);
						 send_ajax('ua/tagg_add_do.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.msg(ret);
								dlg.close();
								iottModel.loadOrUpdate();
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

function act_dev_new_tag_mid(u,op,pxy,dxy)
{
	dlg.open("ua/tag_add.jsp?mid=true",
			{title:"Add Tag Middle",w:'500px',h:'400px'},
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
						 ret.repid=repid ;
						 ret.pid = u.getId();
						 //console.log(ret);
						 send_ajax('ua/tag_add_do.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.msg(ret);
								dlg.close();
								iottModel.loadOrUpdate();
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

function act_dev_new_tag(u,op,pxy,dxy)
{
	dlg.open("ua/tag_add.jsp",
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
						 ret.repid=repid ;
						 ret.pid = u.getId();
						 //console.log(ret);
						 send_ajax('ua/tag_add_do.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.msg(ret);
								dlg.close();
								iottModel.loadOrUpdate();
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

function act_win_new_conn(u,op,pxy,dxy)
{
	dlg.open("./rep/conn_edit.jsp",
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
						 ret.repid= repid;
						 $.ajax({
						        type: 'post',
						        url:'./rep/conn_ajax.jsp',
						        data: ret,
						        async: true,  
						        success: function (result) {  
						        	if(result.indexOf("succ=")==0)
						        	{
						        		dlg.msg(result);
						        		dlg.close();
						        	}
						        	else
						        		dlg.msg(result) ;
						        },
						        error:function(req,err,e)
						        {
						        	dlg.msg(e);
						        }
						    });
						 
						 
						 dlg.close();
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function act_win_new_hmi(u,op,pxy,dxy)
{
	dlg.open("ua/hmi_add.jsp",
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
						 
						 //var pms = "repid="+repid+"&drv="+ret.drv+"&name="+ret.name+'&title='+ret.title+"&desc="+ret.desc ;
						 ret.repid=repid ;
						 ret.pid = u.getId();
						 ret.x = dxy.x;
						 ret.y = dxy.y ;
						 //console.log(ret);
						 send_ajax('ua/hmi_add_do.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.msg(ret);
								dlg.close();
								iottModel.loadOrUpdate();
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

function act_win_new_store(u,op,pxy,dxy)
{
	dlg.open("ua/store_add.jsp",
			{title:"Add Store",w:'500px',h:'400px'},
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
						 ret.repid=repid ;
						 ret.pid = u.getId();
						 ret.x = dxy.x;
						 ret.y = dxy.y ;
						 //console.log(ret);
						 send_ajax('ua/store_add_do.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.msg(ret);
								dlg.close();
								iottModel.loadOrUpdate();
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

function act_tag_write(u,op,pxy,dxy)
{
	dlg.open("ua/tag_write.jsp?repid="+repid+"&id="+u.getId(),
			{title:"Write Tag Value",w:'500px',h:'300px'},
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
						 ret.repid=repid ;
						 ret.id = u.getId();
						 //ret.strv=ret;
						 //console.log(ret);
						 send_ajax('ua/tag_write_ajax.jsp',ret,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								//dlg.msg(ret);
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


function btn_save_temp()
{
	var pm = {} ;
	pm.op="save" ;
	pm.id=repid;
	pm.txt = JSON.stringify(loadLayer.extract(null)) ;
	oc.util.doAjax("ua/ui_temp_ajax.jsp",pm,(bsucc,ret)=>{
        dlg.msg(ret);
    });
}

function btn_save_cont()
{
	var pm = {} ;
	pm.op="save" ;
	pm.id=repid;
	pm.txt = JSON.stringify(iottView.extractContJSON()) ;
	oc.util.doAjax("ua/ui_cont_ajax.jsp",pm,(bsucc,ret)=>{
        dlg.msg(ret);
    });
}


function draw_fit()
{
	if(loadLayer==null)
		return ;
	loadLayer.ajustDrawFit();
}

var r = 0 ;
var i = 0,j=0 ;
function set_dyn_dt()
{
	if(loadLayer==null)
		return ;
	r += 0.1 ;
	i += 10;
	j++;
	if(i>=255)
		i = 0 ;
	var c="rgb(0,0,0)";
	if(j>8)
		c = "rgb(0,255,0)";
	if(j>10)
		j=0;
	var rv={rotate:r}
	var rvr={rotate:-r}
	var v = {dir:rv,r1:rv,r2:rvr,"天":{
		_unit:{
		"title":{txt:"计数"+i},
		txt2:{txt:"192.168.0.1"},
		st:{fillColor:"rgb("+i+",0,0)"}
		}},
		"啊撒打发":{
			_unit:{
			title:{txt:"计数"+j},
			txt2:{txt:"port=8080"},
			st:{fillColor:c}
			}}
	};
	var v0 = {dir:rv,r1:rv,r2:rvr};
	loadLayer.setDynData(v);
	
	
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
		iottModel.refreshDyn(function(){
			lastRefreshDT = new Date().getTime();
			bInRefresh = false;
		});
	}
	finally
	{
		
	}
}

//setInterval("iottModel.refreshDyn();",5000);
setInterval("refresh_dyn()",2000);

function btn_load_unit()
{
	send_ajax("t_ajax.jsp","id=u_u1",function(bsucc,ret){
		//alert(ret);
		oc.DrawUnit.addUnitByJSON(ret);
	}) ;
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