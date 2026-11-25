<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.ecp.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
if(!Convert.checkReqEmpty(request, out, "net_uid"))
	return ;
boolean b_comp = "true".equalsIgnoreCase(request.getParameter("comp")) ;
String net_uid = request.getParameter("net_uid") ;

ECPComp comp = null ;
ECPNet net = null ;
String net_tt = "";
if(b_comp)
{
	comp = ECPCompManager.getInstance().getCompByUID(net_uid) ;
	if(comp==null)
	{
		out.print("no comp found with uid="+net_uid) ;
		return ;
	}
	net_tt = comp.getTitle() ;
}
else
{
	net = ECPManager.getNetByUID(net_uid) ;
	if(net==null)
	{
		out.print("no net found with uid="+net_uid) ;
		return ;
	}
	net_tt = net.getTitle() ;
}
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>IOT-Tree ECP:[<%=net_tt %>] </title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="tab"/>
</jsp:include>
<script type="text/javascript" src="../js/tab.js" ></script>
<link rel="stylesheet" href="../js/tab.css" />
<script src="/_js/ecp/ecp.js?v=1"></script>
<link type="text/css" href="/_js/mn/ot.css" rel="stylesheet" />
<script type="text/javascript" src="/_js/bignumber.min.js"></script>
<script type="text/javascript" src="/_js/jquery.json.js"></script>
<script type="text/javascript" src="./ecp_plug.js"></script>
<style>
.prompt_p
{
	position: absolute;
	min-height: 40px;
	min-width:100px;
	bottom0: 10px;
	border:1px solid;
	display:none;
	color:#00ffdd;
	background-color: #003935;
	z-index:1001;
}

.rt_debug_list
{
	position:absolute;
	left:0px;right:0px;
	top:20px;
	bottom:2px;
	overflow-y:auto;
}
.debug_msg
{
	border-bottom: 1px solid #dddddd;
	margin-left:2px;
	margin-bottom:2px;
}
.debug_msg .msg_meta
{
	font-size: 12px;
	margin-left:5px;
	font-style: italic;
}
.debug_msg .msg_meta span
{
	white-space: nowrap;
}
.debug_msg .msg
{
	margin-left:10px;
	color:green;
}

.sub_span_nowrap span
{
display: inline-block; 
white-space: nowrap;
}
.pop_tt_edit {position: absolute;border:2px solid blue;min-width:150px;min-height:20px;display:none;z-index: 61000;}
.pop_tt_edit #tt {width:100%;}
.btn_nor {background-color: #ccc;}
.btn_act {background-color: blue;}
</style>
</head>
<body style="border: 0px solid #000;margin:0px; width: 100%; height: 100%; overflow: hidden;user-select:none;" >
<div id="left_bar" class="left_bar">
	
</div>
<div id="left" class="left" >
	<div class="hd" ><span class="hd_t"></span> <span class="close" onclick="dt_plugs.hide_item(1)" ><i class="fa fa-times"></i></span></div>
	<div class="panel_if" style="overflow:hidden;">
		<iframe id="if_left" class="if" src="" style="width:100%;height:100%;overflow:hidden;"></iframe>
	</div>
</div>

<div id="mid" class="mid" >
 <div class="bd">
	<div class="hd"  >ECP
	<%
	if(b_comp)
	{
	%> - <wbt:g>comp</wbt:g><%
	}
%>
	: [<%=net_tt %>]
		<span id="net_btns" style="text-align: left;min-width:350px;border:0px solid red;"></span>
		<span style="float:right;"><button onclick="save_net()" style="background-color:#ee9236">&nbsp;<i class="fa-solid fa-save"></i>&nbsp;<wbt:g>save</wbt:g></button>&nbsp;&nbsp;</span></div>
	<div id="panel_main" tabindex="-1"  style="background-color:#1e1e1e;">
		<div id="prompt_p" class="prompt_p"></div>
		<div class="pop_tt_edit"  id="pop_tt_edit" 
			onmousedown="event.stopPropagation();" onmousemove="event.stopPropagation();"
			onkeydown="event.stopPropagation();"><input id="tt" type="text" onkeydown="on_tt_edit_keydown()" onblur="on_tt_edit_blur()"/></div>
	</div>
  </div> <!-- bd -->
</div>
<div id="right_bar" class="right_bar">
	
</div>
<div id="right" class="right" >
	<div class="hd"  style="text-align: right;"><span class="hd_t" ></span> <span class="close"  style="left:5px;" onclick="dt_plugs.hide_item(3)" ><i class="fa fa-times"></i></span></div>
	<div class="panel_if">
		<iframe class="if" src="" ></iframe>
		<div id="edit_props" class="not_if" style="display:none;height:97%;width:100%;border:0px;background-color: #ffffff"></div>
	</div>
</div>

<div id="p_info" style="display:none"></div>
<div id="edit_events"  style="display:none"></div>
<div id="edit_toolbar"  style="display:none"></div>
	 <div id="cont_props" class="sub_win" style="top:80px;width:350px;bottom:10px;overflow: auto;display:none;position: absolute;">
		<div class="title">Properties</div>
		
	</div>
</body>

<script>
//dlg.dlg_top=true;
var net_uid = "<%=net_uid%>" ;
var b_comp = <%=b_comp%>;
var ulang = "<%=Lan.getUsingLang()%>" ;
//toolbox_init("#toolbar_basic");
//toolbox_init("#orders_not_assigned");
var panel = null;
var editor = null ;

var loadLayer = null ;
var intedit =null;

var hmiModel=null;
var hmiView=null;

var cur_resolution = 1 ;

var dt_plugs = null;

function prompt_f(msg)
{
	dlg.msg(msg) ;
}

ecp.util.prompt_reg(prompt_f,prompt_f);

function on_panel_mousemv(p,d)
{
	$("#p_info").html("["+p.x+","+p.y+"] - ("+Math.round(d.x*10)/10+","+Math.round(d.y*10)/10+") res="+cur_resolution);
}

function on_panel_resolution(r)
{
	cur_resolution =r;
}

function on_model_chg()
{
	//tab_notify();
}

function editor_plugcb(jq_ele,tp,di,pn_def,name,val)
{
	
}

function on_editor_prompt(m)
{
	dlg.msg(m) ;
}

function draw_fit()
{
	if(loadLayer==null)
		return ;
	loadLayer.ajustDrawFit();
}

function init_iottpanel()
{
	ecp.DrawLayer.RULE_SHOW=false;
	ecp.DrawLayer.GRID_SHOW=true;
	
	hmiModel = new ecp.ECPModel();
	
	panel = new ecp.DrawPanel("panel_main",{
		on_mouse_mv:on_panel_mousemv,
		on_resolution_chg:on_panel_resolution,
		on_model_chg:on_model_chg,
		on_item_sel_chg:on_item_sel_chg
	});
	panel.setInEdit(true);
	editor = new ecp.DrawEditor("edit_props","edit_events","edit_toolbar",panel,{
		plug_cb:editor_plugcb,
		on_prompt_msg:on_editor_prompt
	}) ;
	hmiView = new ecp.ECPView(hmiModel,panel,editor,{
		copy_paste_url:`./util/copy_paste_ajax.jsp`,
		on_model_loaded:()=>{
			panel.updatePixelSize();
			//draw_fit()
			//setTimeout("draw_fit()",1000)
		},
		onDelSelectItemsTrigger:(sis)=>{
			on_del_items(sis) ;
		},
		onDINodeOpen:(node)=>{
			on_item_open(node);
		},
		onDINodeDBClk:(node,in_area,ecp,in_prect)=>{
			on_item_dbclk(node,in_area,ecp,in_prect);
		},
		onDINodeKeyDown:(node,keycode,e)=>{
			on_item_keydown(node,keycode,e);
		},
		onMoveAppendSub:(pn,idx,mvnd,bcopy)=>{
			on_mv_append_sub(pn,idx,mvnd,bcopy)
		},
		onDIDNodePlugClick:(node,plug_n)=>{
			on_node_plug_clk(node,plug_n) ;
		},
	});
	
	hmiView.init();
	
	loadLayer = hmiView.getLayer();
	intedit = hmiView.getInteract();

	reload_net(true,true);
}



function draw_fit()
{
	if(loadLayer==null)
		return ;
	if(b_comp)
		loadLayer.ajustDrawFit(1.8);
	else
		loadLayer.ajustDrawFit(1.1);
}

function zoom(v)
{
	panel.ajustDrawResolution(0,0,v) ;
}

function reload_net(reload,bfit,end_cb)
{
	if(b_comp)
	{
		send_ajax("ecp_comp_ajax.jsp",{op:"comp_load_by_uid",comp_uid:net_uid},(bsucc,ret)=>{
			if(!bsucc || ret.indexOf("{")!=0)
			{
				dlg.msg(ret);
				return ;
			}
			var ob =null;
			eval("ob="+ret) ;
			ob.reload=reload||false;
			intedit.clearSelectedItems();
			//console.log(ob);
			hmiModel.load_comp_def(ob) ;
			if(bfit)
				draw_fit();
			if(end_cb)
				end_cb();
		});
	}
	else
	{
		send_ajax("ecp_ajax.jsp",{op:"load_net",net_uid:net_uid},(bsucc,ret)=>{
			if(!bsucc || ret.indexOf("{")!=0)
			{
				dlg.msg(ret);
				return ;
			}
			var ob =null;
			eval("ob="+ret) ;
			ob.reload=reload||false;
			intedit.clearSelectedItems();
			//console.log(ob);
			hmiModel.load_net_def(ob) ;
			if(bfit)
				draw_fit();
			if(end_cb)
				end_cb();
		});
	}
	
}
	
	

var cur_tt_node = null ;

function on_tt_edit_keydown()
{
	if(!event) return;
	
	if(event.keyCode==13)
	{//return
		event.stopPropagation();
		let newt = $("#tt").val();
		if(!newt) {dlg.msg("title is empty");return;}
		
		$("#pop_tt_edit").css("display","none") ;
		//if(newt==cur_tt_node.getTitle())
		//	return ;
		cur_tt_node.setTitle(newt);
		cur_tt_node.MODEL_fireChged(["title"])
	}
}

function on_tt_edit_blur()
{
	$("#pop_tt_edit").css("display","none") ;
	$("#panel_main").focus() ;
}

function trigger_node_tt_edit(node,in_prect)
{
	let r = in_prect;
	if(!r)
		r = node.getPixelRect() ;
	if(!r) {dlg.msg("no bound rect found");return;}
	let t = node.getTitle() ||"";
	cur_tt_node = node ;
	$("#pop_tt_edit").css("top",r.y+r.h/6+"px").css("left",r.x+"px").css("width",r.w+"px").css("font-size","12px").css("display","block") ;
	$("#tt").val(t||"").focus() ;
}

function on_item_dbclk(node,in_area,ecp,in_rect)
{
	//console.log("dbclk ->",node.getPixelXY(),node,in_area,ecp);
	if("title"==in_area)
		trigger_node_tt_edit(node,in_rect)
	//$("#pop_tt_edit").css("display","block") ;
}

function on_item_keydown(node,keycode,e)
{
	//console.log("key ->",node);
	switch(keycode)
	{
	case 13:
		if(e.ctrlKey)
		{
			add_new_node(node,3) ;return;
		}
		if(e.shiftKey)
		{
			add_new_node(node,2) ;return;
		}
		add_new_node(node,1) ;
		return;
	case 45: //insert
		add_new_node(node,0) ;return;
	}
}

function save_net()
{
	if(b_comp)
	{
		let net = hmiView.extractECPComp();
		send_ajax("ecp_comp_ajax.jsp",{op:"comp_save_by_uid",comp_uid:net_uid,jstr:JSON.stringify(net)},(bsucc,ret)=>{
			if(!bsucc || ret!="succ") { dlg.msg(ret);return;}
			dlg.msg("done") ;
		})
	}
	else
	{
		let net = hmiView.extractECPNet();
		send_ajax("ecp_ajax.jsp",{op:"net_save_basic",net_uid:net_uid,jstr:JSON.stringify(net)},(bsucc,ret)=>{
			if(!bsucc || ret!="succ") { dlg.msg(ret);return;}
			dlg.msg("done") ;
		})
	}
	
}

function del_node(nodeid)
{
	dlg.confirm('<wbt:g>del,this,node</wbt:g>?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
	{
		send_ajax("ecp_ajax.jsp",{op:"del_node",net_id:net_id,tree_nid:node.getId()},(bsucc,ret)=>{
			if(!bsucc || ret!="succ")
			{
				dlg.msg(ret);return;
			}
			reload_net(true,false,()=>{}) ;
		})
	});
}



function on_mv_append_sub(pn,idx,mvnd,bcopy)
{
	//console.log(pn,idx,mvnd);
	send_ajax("ecp_ajax.jsp",{op:"mv_node_to",net_id:net_id,pn_id:pn.getId(),idx:idx,tree_nid:mvnd.getId(),copy:bcopy},(bsucc,ret)=>{
		if(!bsucc || ret!="succ")
		{
			dlg.msg(ret);return;
		}
		reload_net(true,false,()=>{}) ;
	})
}

var cur_single_sel_nd = null ;
var cur_over_win = null ;

function on_node_action(dn,op,pxy,dxy)
{
	//console.log("on_node_action",dn,op);
	event.stopPropagation();
	switch(op)
	{
	case "add_node":
		add_new_node(dn,1);return;
	case "add_joint":
		add_joint(dn,0);return;
	case "add_inout":
		add_inout(dn,true);return;
	case "add_plugin":
		add_plugin(dn,true);return;
	case "add_node_ahead":
		add_new_node(dn,2);return;
	case "del_node":
		on_del_items([dn])
		return ;
	case "set_nd_to_comp":
		on_set_nd_to_comp(dn) ;
		return ;
	case "set_io_inout":
		set_io_inout(dn,true,true);return ;
	case "set_io_out":
		set_io_inout(dn,false,true);return ;
	case "set_io_in":
		set_io_inout(dn,true,false);return ;
	case "set_io_null":
		set_io_inout(dn,false,false);return ;
	case "set_io_title":
		trigger_node_tt_edit(dn);return ;
	
	
	case "set_runblk":
	}
}

function add_joint(dn)
{
	intedit.setOperAddJoint(dn);
}

function add_inout(dn,ignore_first_up)
{
	intedit.setOperAddInOut(dn,null,ignore_first_up);
	$("#panel_main").focus();
}

function add_plugin(dn,ignore_first_up)
{
	intedit.setOperAddPlugin(dn,null,ignore_first_up);
	$("#panel_main").focus();
}

function set_io_inout(dn,b_in,b_out)
{
	dn.setIOInOut(b_in,b_out);
	$("#panel_main").focus();
}

function do_copy()
{
	intedit.doCopy();
	$("#panel_main").focus();
}

function do_paste()
{
	intedit.doPaste();
	$("#panel_main").focus();
}

function on_node_plug_clk(node,plug_n)
{
	dt_plugs.show_item(plug_n,false) ;
}

function on_set_nd_to_comp(dn)
{
	//console.log(dn) ;
	var tt = "<wbt:lang>edit,comp</wbt:lang>" ;
	dlg.open("./ecp_comp_edit.jsp?libid=",
			{title:tt},
			['<wbt:lang>ok</wbt:lang>','<wbt:lang>cancel</wbt:lang>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="comp_add_by_node" ;
						 ret.jstr = JSON.stringify(dn.extractECPNodeJO());
						 var pm = {
									type : 'post',
									url : "./ecp_comp_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if(ret.indexOf("succ=")!=0)
								{
									dlg.msg(ret) ;
									return ;
								}
								dlg.msg("done")
								dlg.close();
							}).fail(function(req, st, err) {
								dlg.msg(err);
							});
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

var menu2item = {
		//add_joint:{op_name:'add_joint',op_title:"<wbt:g>add,joint</wbt:g>",op_tip:"<wbt:g>add_joint_d</wbt:g>",op_key:"Enter",op_icon:"<i class='fa-solid fa-circle'></i>",action:on_node_action},
		add_inout:{op_name:'add_inout',op_title:"<wbt:g>add,io_inout</wbt:g>",op_tip:"<wbt:g>add_inout_d</wbt:g>",op_key:"",op_icon:"<i class='fa-solid fa-map-pin'></i>",action:on_node_action},
		add_plugin:{op_name:'add_plugin',op_title:"<wbt:g>add,plugin</wbt:g>",op_tip:"<wbt:g>add_plugin_d</wbt:g>",op_key:"",op_icon:"<i class='fa-solid fa-plug'></i>",action:on_node_action},
		
		del_node:{op_name:'del_node',op_title:"<wbt:g>del,node</wbt:g>",op_tip:"",op_key:"",op_icon:"<i class='fa fa-times'></i>",action:on_node_action},
		
		set_io_inout:{op_name:'set_io_inout',op_title:"<wbt:g>io_inout</wbt:g>",op_tip:"",op_key:"",op_icon:"<i class='fa fa-caret-left'></i><i class='fa fa-caret-right'></i>",action:on_node_action},
		set_io_out:{op_name:'set_io_out',op_title:"<wbt:g>io_out</wbt:g>",op_tip:"",op_key:"",op_icon:"<i class='fa fa-caret-right'></i>",action:on_node_action},
		set_io_in:{op_name:'set_io_in',op_title:"<wbt:g>io_in</wbt:g>",op_tip:"",op_key:"",op_icon:"<i class='fa fa-caret-left'></i>",action:on_node_action},
		set_io_null:{op_name:'set_io_null',op_title:"<wbt:g>io_null</wbt:g>",op_tip:"",op_key:"",op_icon:"<i class='fa fa-minus'></i>",action:on_node_action},
		set_io_title:{op_name:'set_io_title',op_title:"<wbt:g>set,title</wbt:g>",op_tip:"",op_key:"",op_icon:"<i class='fa fa-pencil'></i>",action:on_node_action},
		
		set_nd_to_comp:{op_name:'set_nd_to_comp',op_title:"<wbt:g>set,node_to_comp</wbt:g>",op_tip:"",op_key:"",op_icon:"<i class='fa fa-arrow-right'></i><i class='fa fa-cube'></i>",action:on_node_action},
		
		copy_sel:{op_name:'copy_node',op_title:"<wbt:g>copy_node</wbt:g>",op_tip:"",op_icon:"<i class='fa fa-copy'></i>",action:on_node_action},
		paste_node:{op_name:'paste_node',op_title:"<wbt:g>paste_node</wbt:g>",op_tip:"",op_icon:"<i class='fa fa-paste'></i>",action:on_node_action},
}

function on_ecp_menu(nd)
{//console.log("on ecp menu ",nd) ;
	if(!nd) return null ;
	let baseitem = nd.baseItem ;
	
    let ret = []
    //if(nd.addECPJoint)
	//	ret.push(menu2item.add_joint);

    if(nd.addECPInOut)
    	ret.push(menu2item.add_inout);
    if(nd.addECPPlugin)
    	ret.push(menu2item.add_plugin);
    ret.push("")
    
    
    switch(nd.getClassName())
    {
    	case ecp.view.ECPCompIns.CN:
    	{
    		ret.push(menu2item.set_nd_to_comp);
    		break;
    	}
	    case ecp.view.ECPInOut.CN:
	    {
	    	ret.push(menu2item.set_io_inout);
	    	ret.push(menu2item.set_io_out);
	    	ret.push(menu2item.set_io_in);
	    	ret.push(menu2item.set_io_null);
	    	ret.push(menu2item.set_io_title);
	    	break ;
	    }
    }
    
    ret.push("")
    //ret.push(menu2item.copy_node);
    //ret.push(menu2item.paste_node);
    //ret.push("");
    //ret.push(menu2item.del_node);
    
    return ret ;
}

ecp.PopMenu.OnMenuItemsByNode=function(nd)
{
	if(nd instanceof ecp.view.ECPBase)
	{
		return on_ecp_menu(nd)
	}
	
	return [];
}

function on_cur_single_node_action(op)
{
	if(!cur_single_sel_nd) return ;
	let item = menu2item[op] ;
	if(!item) return;
	item.action(cur_single_sel_nd,op,null,null) ;
}


function on_item_sel_chg(items)
{
	cur_single_sel_nd = null ;
	if(!items||items.length<=0)
	{
		update_top_action_btns();
		dt_plugs.on_ecp_seled(null) ;
		return ;
	}
	if(!items || items.length!=1)
	{
		update_top_action_btns();
		dt_plugs.on_ecp_seled(null) ;
		return ;
	}
	let item = items[0] ;

	if(item instanceof ecp.view.ECPBase)
	{
		cur_single_sel_nd = item ;
		dt_plugs.on_ecp_seled(item) ;
	}
	
	update_top_action_btns();
}

function update_top_action_btns()
{
	//<button onclick="add_joint(null)" style="background-color:#3d5c93">&nbsp;<i class='fa-solid fa-circle'></i>&nbsp;<wbt:g>add,joint</wbt:g></button>&nbsp;&nbsp;
	//<button onclick="add_line('bz')" style="background-color:#3d5c93">&nbsp;<i class="fa-solid fa-arrow-turn-down fa-rotate-270"></i>&nbsp;<wbt:g>add_line</wbt:g></button>&nbsp;&nbsp;
	//	<button onclick="add_line('seg')" style="background-color:#3d5c93">&nbsp;<i class="fa-solid fa-arrow-trend-up"></i>&nbsp;<wbt:g>add_line</wbt:g></button>&nbsp;&nbsp;
	let ss = `<button onclick="draw_fit()" style="">&nbsp;<i class="fa fa-crosshairs" title="fit windows"></i>&nbsp;</button>
		<button onclick="zoom(-1)" style="">&nbsp;<i class="fa-solid fa-magnifying-glass-plus" title="zoom up"></i>&nbsp;</button>
		<button onclick="zoom(1)" style="">&nbsp;<i class="fa-solid fa-magnifying-glass-minus" title="zoom down"></i>&nbsp;</button>&nbsp;&nbsp;
		`
		
	if(!b_comp)
	{
		ss += `
			<button onclick="do_copy()" style="background-color:#3d5c93" title="<wbt:g>copy</wbt:g>">&nbsp;<i class="fa-solid fa-copy"></i>&nbsp;</button>
			<button onclick="do_paste()" style="background-color:#3d5c93" title="<wbt:g>paste</wbt:g>">&nbsp;&nbsp;<i class="fa-solid fa-paste"></i>&nbsp;&nbsp;</button>
		    <button onclick="add_node_unit()" style="background-color:#3d5c93" title="<wbt:g>add_unit</wbt:g>">&nbsp;&nbsp;<i class="fa-solid fa-plus" title=""></i>&nbsp;&nbsp;</button>
			<button onclick="add_conn()" style="background-color:#3d5c93" title="<wbt:g>conn</wbt:g>">&nbsp;<i class="fa-solid fa-arrow-trend-up"></i>&nbsp;</button>
			`;
	}
		
	if(cur_single_sel_nd)
	{
		let mitems = ecp.PopMenu.getMenuItemsByNode(cur_single_sel_nd);
		if(mitems)
		{
			for(let m of mitems)
			{
				if(typeof(m)=='string')
					continue;
				ss += `<button onclick="on_cur_single_node_action('\${m.op_name}')" title="\${m.op_title} - \${m.op_tip}" style="min-width:30px;">\${m.op_icon}</button>` ;
			}
		}
	}
	$("#net_btns").html(ss) ;
}

update_top_action_btns();

function add_node_unit()
{
	if(intedit==null)
		return;
	if(!intedit.setOperAddECPCompIns())
	{
		dlg.msg("set oper error");
		return;
	}
}

function placeCompIns(comp_def)
{
	if(intedit==null)
		return;

	intedit.placeCompInsByComp(comp_def)
}

function add_conn()
{
	if(intedit==null)
		return;
	//if(!intedit.setOperAddECPLine({}))
	if(!intedit.setOperAddECPConn())
	{
		dlg.msg("set oper error");
		return;
	}
}

function on_item_open(mn)
{
	
}


function on_editor_prompt(m)
{
	dlg.msg(m) ;
}


function on_del_items(sis)
{console.log(sis);
	if(sis==null||sis.length<=0)
		return ;

	let ids = "" ;
	for(let si of sis)
	{
		ids+= si.id ; 
	}
	if(ids=="")
		return ;
	dlg.confirm("Are you sure to delete selected items?",{btn:["Sure","Cancel"],title:"Warn"},()=>
    {
		send_ajax("./ecp_ajax.jsp",{op:"del_by_ids",mgr_id:mgr_id,net_id:net_id,ids:ids},
                (bsucc,ret)=>{
                	if(!bsucc||ret!="succ")
                	{
                		dlg.msg(ret) ;
                		return ;
                	}
                	reload_net(true,false);
                }) ;
    });
}

function show_hide(n,w)
{
	if(!w)
		w = 300 ;
	let obj_if = $("#"+n+"_if") ;
	let src = obj_if.attr("src");
	if(!src)
	{
		obj_if.attr("src","rn_"+n+".jsp?net_id="+net_id) ;
	}
	slide_toggle($("#"+n),w+"px") ;
}

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



//ecp.load_res("dt_tree_imgs_list.jsp",()=>{

//}) ;

init_iottpanel();
dt_plugs = new DTPlugs(panel,net_uid,ulang) ;
let plug_items=[]
plug_items.push({pos:3,name:"props",title_en:"Node Properties",title_cn:"节点属性",icon:"<i class='fa-solid fa-table-list'></i>",width:300,url:"",div_id:"edit_props",open:true})
if(b_comp)
{
	plug_items.push({pos:1,name:"comp_models",title_en:"Component Models",title_cn:"组件型号",icon:"<i class='fa fa-cube'></i>",width:350,url:"./plugs/ecp_comp_models.jsp?",refresh_seled:false})
}
else
{
	plug_items.push({pos:1,name:"comps",title_en:"Components",title_cn:"组件",icon:"<i class='fa fa-cube'></i>",width:450,url:"./ecp_comp_mgr.jsp?",refresh_seled:false})
	plug_items.push({pos:1,name:"tags",title_en:"Local Project Tags",title_cn:"本地项目标签",icon:"<i class='fa fa-tags'></i>",width:450,url:"plugs/tn_runtags.jsp?net_id="+this.netid})
	plug_items.push({pos:1,name:"staticdata",title_en:"Static Data",title_cn:"静态数据",icon:"<i class='fa-regular fa-file-lines'></i>",width:450,url:"plugs/tn_staticdata.jsp?treeid="+this.netid})
	plug_items.push({pos:3,name:"runblks",title_en:"Node Run Blocks",title_cn:"运行模块",icon:"<i class='fa-solid fa-gear'></i>",width:600,url:"plugs/tn_runblks.jsp?treeid="+this.netid})
}
plug_items.push()
dt_plugs.init(plug_items);

function resize_zz()
{
	var h = $(window).height();
	$("#left_pan_iframe").css("height",(h-38)+"px");
	
}

var resize_cc = 0 ;
$(window).resize(function(){
	panel.updatePixelSize() ;
	resize_cc ++ ;
	if(resize_cc<=1)
		draw_fit();
	resize_zz();
});

resize_zz();

//cxt

// rt

function rt_flow_start_stop(b_start)
{
	let op = "rt_flow_start" ;
	if(!b_start)
		op = "rt_flow_stop" ;
	send_ajax("./mn_ajax.jsp",{op:op,container_id:container_id,netid:netid},
            (bsucc,ret)=>{
            	if(!bsucc||ret!="succ")
            	{
            		dlg.msg(ret) ;
            		return ;
            	}
            	dlg.msg("done") ;
            }) ;
}


var ws = null;
var ws_last_chk = -1 ;
var ws_opened = false;

function log(str)
{
	console.log(str);
}

function ws_conn()
{
	let pn = window.location.pathname ;
	let k = pn.indexOf("/",1) ;
	pn = pn.substring(0,k) ;
	
	var bhttps = location.protocol === 'https:';
    var url = (bhttps?'wss://':'ws://') + window.location.host + pn+'/_ws/net_msg/'+container_id+"/"+netid;
    if ('WebSocket' in window) {
        ws = new WebSocket(url);
    } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket(url);
    } else {
        log('WebSocket is not supported by this browser.');
        return false ;
    }
    
    ws.onopen = function () {
        //setConnected(true);
        log('Info: WebSocket connection opened.');
        ws_opened = true;
    };
    ws.onmessage = function (event) {

    	let str = event.data ;
    	let d ;
    	eval("d="+str) ;
  		let debug_nid = d.nodeid ;
  		if(!debug_nid) return ;
  		
  		push_debug_item(debug_nid,d) ;
    };
    
    ws.onclose = function (event) {
    	ws_disconn();
        log('Info: WebSocket connection closed, Code: ' + event.code + (event.reason == "" ? "" : ", Reason: " + event.reason));
    };
    
    return true;
}	

function ws_disconn() {
	
    if (ws != null) {
        ws.close();
        ws = null;
    }
    ws_opened = false;
}


function check_ws()
{
	if(ws!=null&&ws_opened)
	{
		ws_last_chk = new Date().getTime();
		return ;
	}

	if(ws==null)
	{
		ws_disconn();
		ws_conn();
		ws_last_chk = new Date().getTime();
		return ;
	}
	
	//ws_opened==false;
	var dt = new Date().getTime();
	if(dt-ws_last_chk<20000)
		return ;
	//time out
	ws_disconn();
	ws_conn();
	ws_last_chk = new Date().getTime();
	return ;
}

//check_ws();
//setInterval(check_ws,5000) ;


</script>

</html>