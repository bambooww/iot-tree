<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.devtree.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
if(!Convert.checkReqEmpty(request, out, "treeid"))
	return ;

String treeid = request.getParameter("treeid") ;

DTTree tree = DTTreeManager.getInstance().getTreeById(treeid) ;
if(tree==null)
{
	out.print("no tree found") ;
	return ;
}
String tree_tt = tree.getTitle() ;

%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="tab"/>
</jsp:include>
<script type="text/javascript" src="../js/tab.js" ></script>
<link rel="stylesheet" href="../js/tab.css" />
<script src="/_js/mn/ot.js?v=1"></script>
<link type="text/css" href="/_js/mn/ot.css" rel="stylesheet" />
<script type="text/javascript" src="/_js/bignumber.min.js"></script>
<script type="text/javascript" src="/_js/jquery.json.js"></script>
<script type="text/javascript" src="./dt_tree.js"></script>
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
	<div class="hd" >Device Tree:[<%=tree_tt %>]<span id="tree_btns"></span><span></span><span></span></div>
	<div id="panel_main" tabindex="-1" >
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
	</div>
</div>

<div id="p_info" style="display:none"></div>
<div id="edit_events"  style="display:none"></div>
<div id="edit_toolbar"  style="display:none"></div>
	 <div id="cont_props" class="sub_win" style="top:80px;width:350px;bottom:10px;overflow: auto;display:none;position: absolute;">
		<div class="title">Properties</div>
		<div id=edit_props style="height:97%;width:100%;border:0px;background-color: #ffffff"></div>
	</div>
</body>

<script>
//dlg.dlg_top=true;
var treeid = "<%=treeid%>" ;

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

ot.util.prompt_reg(prompt_f,prompt_f);

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
	ot.DrawLayer.RULE_SHOW=false;
	ot.DrawLayer.GRID_SHOW=true;
	
	hmiModel = new ot.OTModel();
	
	panel = new ot.DrawPanel("panel_main",{
		on_mouse_mv:on_panel_mousemv,
		on_resolution_chg:on_panel_resolution,
		on_model_chg:on_model_chg,
		on_item_sel_chg:on_item_sel_chg
	});
	panel.setInEdit(true);
	editor = new ot.DrawEditor("edit_props","edit_events","edit_toolbar",panel,{
		plug_cb:editor_plugcb,
		on_prompt_msg:on_editor_prompt
	}) ;
	hmiView = new ot.OTView(hmiModel,panel,editor,{
		//copy_paste_url:`./dt_tree_copy_paste_ajax.jsp?treeid=\${treeid}`,
		on_model_loaded:()=>{
			panel.updatePixelSize();
			draw_fit()
			setTimeout("draw_fit()",1000)
		},
		
		onDelSelectItemsTrigger:(sis)=>{
			on_del_items(sis) ;
		},
		onDINodeOpen:(node)=>{
			on_item_open(node);
		},
		onDINodeDBClk:(node)=>{
			on_item_dbclk(node);
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

	reload_tree(true,true);
}



function draw_fit()
{
	if(loadLayer==null)
		return ;
	loadLayer.ajustDrawFit();
}

function zoom(v)
{
	panel.ajustDrawResolution(0,0,v) ;
}

function reload_tree(reload,bfit,end_cb)
{
	send_ajax("dt_tree_ajax.jsp",{op:"load_tree",treeid:treeid},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("{")!=0)
		{
			dlg.msg(ret);
			return ;
		}
		var ob =null;
		eval("ob="+ret) ;
		ob.reload=reload||false;
		intedit.clearSelectedItems();
		hmiModel.load_def(ob) ;
		if(bfit)
			draw_fit();
		if(end_cb)
			end_cb();
	});
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
		if(newt==cur_tt_node.getTitle())
			return ;
		//cur_tt_node.setTitle(newt) ;
		send_ajax("dt_tree_ajax.jsp",{op:"set_node_title",treeid:treeid,tree_nid:cur_tt_node.getId(),title:newt},(bsucc,ret)=>{
			if(!bsucc || ret!="succ")
			{
				dlg.msg(ret);return;
			}
			reload_tree(true,false,()=>{
				//console.log(cur_tt_node)
				hmiView.setSelectedDINodeById(cur_tt_node.id) ;
				$("#panel_main").focus() ;
			}) ;
		})	
	}
}

function on_tt_edit_blur()
{
	$("#pop_tt_edit").css("display","none") ;
	$("#panel_main").focus() ;
}

function trigger_node_tt_edit(node)
{
	let r = node.getPixelRect() ;
	let t = node.getTitle() ;
	cur_tt_node = node ;
	$("#pop_tt_edit").css("top",r.y+r.h/6+"px").css("left",r.x+"px").css("width",r.w+"px").css("font-size",r.h/2+"px").css("display","block") ;
	$("#tt").val(t||"").focus() ;
}

function on_item_dbclk(node)
{
	console.log("dbclk ->",node.getPixelXY(),node);
	trigger_node_tt_edit(node)
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

function add_new_node(node,sty)
{
	send_ajax("dt_tree_ajax.jsp",{op:"add_sub_grp",treeid:treeid,tree_nid:node.getId(),sty:sty},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("succ=")!=0)
		{
			dlg.msg(ret);return;
		}
		let newid = ret.substring(5) ;
		reload_tree(true,false,()=>{
			let tree = hmiView.getDITree();
			let newnd = tree.findNodeById(newid) ;
			//console.log(newnd) ;
			if(!newnd) return ;
			hmiView.setSelectedDINode(newnd) ;
			trigger_node_tt_edit(newnd)
		}) ;
	})
}

function del_node(nodeid)
{
	dlg.confirm('<wbt:g>del,this,node</wbt:g>?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
	{
		send_ajax("dt_tree_ajax.jsp",{op:"del_node",treeid:treeid,tree_nid:node.getId()},(bsucc,ret)=>{
			if(!bsucc || ret!="succ")
			{
				dlg.msg(ret);return;
			}
			reload_tree(true,false,()=>{}) ;
		})
	});
}



function on_mv_append_sub(pn,idx,mvnd,bcopy)
{
	//console.log(pn,idx,mvnd);
	send_ajax("dt_tree_ajax.jsp",{op:"mv_node_to",treeid:treeid,pn_id:pn.getId(),idx:idx,tree_nid:mvnd.getId(),copy:bcopy},(bsucc,ret)=>{
		if(!bsucc || ret!="succ")
		{
			dlg.msg(ret);return;
		}
		reload_tree(true,false,()=>{}) ;
	})
}

function save_as_parttp(dn)
{
	dlg.open("./util/dlg_partlib_tt.jsp",
			{title:"<wbt:g>save_as_parttp</wbt:g>",txt:dn.title},['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					let vt = dlgw.get_input((bok,ret)=>{
						if(!bok) {dlg.msg(ret);return;}
						send_ajax("dt_part_ajax.jsp",{op:"add_parttp_by_node",...ret,treeid:treeid,tree_nid:dn.id},(bsucc,ret)=>{
							if(!bsucc||ret.indexOf('succ=')!=0){dlg.msg(ret);return;}
							let newlibid = ret.substring(5) ;
							//location.reload();
							dlg.close() ;
						});
					}) ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

var cur_single_sel_nd = null ;
var cur_over_win = null ;

function on_node_action(dn,op,pxy,dxy)
{
	console.log(dn,op);
	event.stopPropagation();
	switch(op)
	{
	case "add_node":
		add_new_node(dn,1);return;
	case "add_node_sub":
		add_new_node(dn,0);return;
	case "add_node_ahead":
		add_new_node(dn,2);return;
	case "del_node":
		on_del_items([dn])
		return ;
	case "copy_node":
		send_ajax("dt_tree_ajax.jsp",{op:'copy_node',treeid:treeid,tree_nid:dn.id},(bsucc,ret)=>{
			if(!bsucc || ret!='succ')
			{
				dlg.msg(ret);return;
			}
			dlg.msg("copied ok");
		})
		return ;
	case "paste_node":
		send_ajax("dt_tree_ajax.jsp",{op:'paste_node',treeid:treeid,tree_nid:dn.id},(bsucc,ret)=>{
			if(!bsucc || ret!='succ')
			{
				dlg.msg(ret);return;
			}
			dlg.msg("paste ok");
			reload_tree();
		})
		return ;
	case "save_as_parttp":
		save_as_parttp(dn);
		return ;
	case "set_runblk":
	case "set_runtags":
		cur_over_win = dlg.show_over_dlg(true,`dt_tree_tn_runtags.jsp?treeid=\${treeid}&tree_nid=\${dn.id}`,{ratio:"400px",h:false,title:"Set Tags",overlay_hide:true,on_close:()=>{
				
			}},
		
				["Ok","Apply"],
				[
					function(dlgw)
					{
						let tags = dlgw.get_tags_list();
						console.log(tags);
						send_ajax("dt_tree_ajax.jsp",{op:'set_node_tags',treeid:treeid,tree_nid:dn.id,jarr:JSON.stringify(tags)},(bsucc,ret)=>{
							if(!bsucc || ret!='succ')
							{
								dlg.msg(ret);return;
							}
							reload_tree();
							dlg.show_over_dlg(false) ;
						})
						
						
					},
					function(dlgw)
					{
						
					}
				]);
		return ;
	}
}

function on_node_plug_clk(node,plug_n)
{
	dt_plugs.show_item(plug_n,false) ;
}

function set_node_by_part_pm(dn,pm,cb)
{
	pm.op="set_node_by_part"
	pm.treeid=treeid
	pm.tree_nid = dn.id;
	send_ajax("dt_tree_ajax.jsp",pm,cb) ;
}

function set_node_by_part(dn,node_self)
{
	let u = "./dt_partlib_list.jsp?edit=false&sel=true";
	dlg.open(u,{title:"<wbt:g>part,node,setup</wbt:g>-<wbt:g>sel,part</wbt:g>"},
			['<wbt:g>sel,part</wbt:g>',"<wbt:g>only,sel,parttp</wbt:g>",'<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.get_selected_part_tp((bok,ret)=>{
						if(!bok) {dlg.msg(ret);return;}
						ret.op="set_node_by_part";
						ret.treeid=treeid;
						ret.tree_nid = dn.id;
						send_ajax("dt_tree_ajax.jsp",ret,(bsucc,rrr)=>{
							if(!bsucc||rrr!="succ"){dlg.msg(rrr);return;}
							reload_tree();
							dlg.close();
						})
					})
				},
				function(dlgw)
				{
					dlgw.get_selected_part((bok,ret)=>{
						if(!bok) {dlg.msg(ret);return;}
						ret.op="set_node_by_part";
						ret.treeid=treeid;
						ret.tree_nid = dn.id;
						send_ajax("dt_tree_ajax.jsp",ret,(bsucc,rrr)=>{
							if(!bsucc||rrr!="succ"){dlg.msg(rrr);return;}
							reload_tree();
							dlg.close();
						})
					})
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function on_add_sub_part(dn,op,pxy,dxy)
{
	set_node_by_part(dn,false)
}

function on_set_self_part(dn,op,pxy,dxy)
{
	set_node_by_part(dn,true)
}

function on_unset_self_part(dn,op,pxy,dxy)
{
}

var menu2item = {
		add_node:{op_name:'add_node',op_title:"<wbt:g>add_node</wbt:g>",op_tip:"<wbt:g>add_node_d</wbt:g>",op_key:"Enter",op_icon:"<i class='fa fa-plus'></i>",action:on_node_action},
		add_node_sub:{op_name:'add_node_sub',op_title:"<wbt:g>add_node_sub</wbt:g>",op_tip:" (Insert)",op_key:"Insert",op_icon:"<i class='fa fa-plus'></i><i class='fa-regular fa-square'></i>",action:on_node_action},
		add_node_ahead:{op_name:'add_node_ahead',op_title:"<wbt:g>add_node_ahead</wbt:g>",op_tip:"   (Shift_Enter)",op_key:"Shift+Enter",op_icon:"<i class='fa-regular fa-square'></i><i class='fa fa-plus'></i>",action:on_node_action},
		del_node:{op_name:'del_node',op_title:"<wbt:g>del,node</wbt:g>",op_tip:"",op_key:"",op_icon:"<i class='fa fa-times'></i>",action:on_node_action},
		
		add_sub_part:{op_name:'add_sub_part',op_title:"<wbt:g>add_sub_part</wbt:g>",op_tip:"",op_key:"",op_icon:"<i class='fa fa-plus'></i><i class='fa fa-cube'></i>",action:on_add_sub_part},
		set_self_part:{op_name:'set_self_part',op_title:"<wbt:g>set_self_part</wbt:g>",op_tip:"",op_key:"",op_icon:"<i class='fa-solid fa-equals'></i><i class='fa fa-cube'></i>",action:on_set_self_part},
		unset_self_part:{op_name:'unset_self_part',op_title:"<wbt:g>unset_self_part</wbt:g>",op_tip:"",op_key:"",op_icon:"<i class='fa fa-not-equal'></i><i class='fa fa-cube'></i>",action:on_unset_self_part},
		
		copy_node:{op_name:'copy_node',op_title:"<wbt:g>copy_node</wbt:g>",op_tip:"",op_icon:"<i class='fa fa-copy'></i>",action:on_node_action},
		paste_node:{op_name:'paste_node',op_title:"<wbt:g>paste_node</wbt:g>",op_tip:"",op_icon:"<i class='fa fa-paste'></i>",action:on_node_action},
		save_as_parttp:{op_name:'save_as_parttp',op_title:"<wbt:g>save_as_parttp</wbt:g>",op_tip:"",op_icon:"<i class='fa-solid fa-arrow-right'></i><i class='fa fa-cube'></i>",action:on_node_action},
}

function on_dinode_menu(nd)
{console.log("on_dinode_menu",nd);
	if(!nd) return null ;
	let baseitem = nd.baseItem ;
	if(baseitem.has_part_ref)
    {
        if(baseitem.part_ref_main)
            return [
        		menu2item.add_node,
        		menu2item.add_node_ahead,
        		"",
        		menu2item.unset_self_part,
        		"",
        		menu2item.copy_node,
        		"",
        		menu2item.del_node,
        	] ;
        
        else if(baseitem.part_ref_sub)
            return [];
        else
            return []
    }
	
    if(nd.isNodeRoot())
        return [
    		menu2item.add_node,
    		menu2item.add_node_sub,
    		menu2item.add_node_ahead,
    		"",
    		menu2item.add_sub_part,
    		"",
    		menu2item.copy_node,
    		menu2item.paste_node,
    	];
    
    if(nd.isNodeLeaf())
	    return [
			menu2item.add_node,
			menu2item.add_node_sub,
			menu2item.add_node_ahead,
			"",
			menu2item.add_sub_part,
			menu2item.set_self_part,
			"",
			menu2item.copy_node,
			menu2item.paste_node,
			menu2item.save_as_parttp,
			"",
			menu2item.del_node,
		];
    
    return [//mid
		menu2item.add_node,
		menu2item.add_node_sub,
		menu2item.add_node_ahead,
		"",
		menu2item.add_sub_part,
		"",
		menu2item.copy_node,
		menu2item.paste_node,
		menu2item.save_as_parttp,
		"",
		menu2item.del_node,
	];
}

ot.PopMenu.OnMenuItemsByNode=function(nd)
{
	if(nd instanceof ot.view.DINode)
	{
		return on_dinode_menu(nd)
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
		dt_plugs.on_tree_node_seled(null) ;
		return ;
	}
	if(!items || items.length!=1)
	{
		update_top_action_btns();
		dt_plugs.on_tree_node_seled(null) ;
		return ;
	}
	let item = items[0] ;
	
	if(item instanceof ot.view.DINode)
	{
		cur_single_sel_nd = item ;
		dt_plugs.on_tree_node_seled(item) ;
	}
	
	update_top_action_btns();
}

function update_top_action_btns()
{
	let ss = `<button onclick="draw_fit()" style="background-color:#ccc">&nbsp;<i class="fa fa-crosshairs" title="fit windows"></i>&nbsp;</button>
		<button onclick="zoom(-1)" style="background-color:#ccc">&nbsp;<i class="fa-solid fa-magnifying-glass-plus" title="zoom up"></i>&nbsp;</button>
		<button onclick="zoom(1)" style="background-color:#ccc">&nbsp;<i class="fa-solid fa-magnifying-glass-minus" title="zoom down"></i>&nbsp;</button>&nbsp;&nbsp;`;
	
	if(cur_single_sel_nd)
	{
		let mitems = ot.PopMenu.getMenuItemsByNode(cur_single_sel_nd);
		if(mitems)
		{
			for(let m of mitems)
			{
				if(typeof(m)=='string')
					continue;
				ss += `<button onclick="on_cur_single_node_action('\${m.op_name}')" title="\${m.op_title} - \${m.op_tip}">&nbsp;\${m.op_icon}&nbsp;</button>` ;
			}
		}
	}
	$("#tree_btns").html(ss) ;
}

update_top_action_btns();



function on_item_open(mn)
{
	
}


function on_editor_prompt(m)
{
	dlg.msg(m) ;
}


function on_del_items(sis)
{
	if(sis==null||sis.length<=0)
		return ;

	let ids = "" ;
	for(let si of sis)
	{
		if(si.getClassName()=='ot.view.DINode')
			continue ;
		if(si.getUID)
			ids+= ((ids!='')?",":"")+si.getUID(); 
	}
	if(ids=="")
		return ;
	dlg.confirm("Are you sure to delete selected items?",{btn:["Sure","Cancel"],title:"Warn"},()=>
    {
		send_ajax("./dt_tree_ajax.jsp",{op:"del_node_by_ids",treeid:treeid,tree_nids:ids},
                (bsucc,ret)=>{
                	if(!bsucc||ret!="succ")
                	{
                		dlg.msg(ret) ;
                		return ;
                	}
                	reload_tree(true,false);
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



ot.load_res("dt_tree_imgs_list.jsp",()=>{
	init_iottpanel();
	
	dt_plugs = new DTPlugs(panel,treeid,ulang) ;
	dt_plugs.init();
}) ;




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