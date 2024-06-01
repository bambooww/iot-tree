<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.msgnet.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
if(!Convert.checkReqEmpty(request, out, "prjid","netid"))
	return ;

String prjid = request.getParameter("prjid");
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}

String netid = request.getParameter("netid") ;

MNNet net = MNManager.getInstance(prj).getNetById(netid) ;
if(net==null)
{
	out.print("no net found") ;
	return ;
}
String net_tt = net.getTitle() ;

%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
	<jsp:param value="true" name="tab"/>
	<jsp:param value="true" name="mn"/>
</jsp:include>
<script type="text/javascript" src="../js/tab.js" ></script>
<link rel="stylesheet" href="../js/tab.css" />
<link rel="stylesheet" href="./inc/mn.css" />
<style>
.rt_blk
{
	position:relative;
	width:100%;
	min-height:20px;
	background-color: #92ccdf;
	margin-bottom: 2px;
}

.rt_blk table
{
	width:100%;
}

.rt_blk button
{
	border:1px solid #dddddd;
	color:#aaaaaa;
}
.rt_blk button:hover
{
	background-color: #1bc6a3;
	color:#000;
}

.rt_blk table td
{
	border:1px solid #555555;
}
.rt_sub
{
	position0:absolute;
	left:10px;
}
</style>
</head>
<body style="border: 0px solid #000;margin:0px; width: 100%; height: 100%; overflow: hidden;user-select:none;" >
<div id="leftcat_comp" class="show_hid_icon" style="left:0px;" onclick="show_hiddle_left()" title="show or hidden left panel"><i class="fa fa-cogs fa-lg lr_btn"></i><br>&nbsp; </div>
		<div id="left_panel" class="left_panel_win"  >
			<div class="left_panel_bar" >
				<span id="left_panel_title" style="font-size: 18px;left:35px;position: relative;top:5px;">Nodes</span>
			</div>
			 
			<iframe id="left_pan_iframe" src="mn_node_list.jsp" style="width:100%;top:0px;height:300px;overflow:hidden;margin: 0px;border:0px solid;padding: 0px;" ></iframe>
		</div>
<div id="mid" class="mid">
			<div id="panel_main" style="border: 1px solid #cccccc;margin:0px; width: 100%; height: 100%; background-color: #ffffff;overflow: hidden;" >
			</div>
			
			<div style="right:0px;" class="show_hid_icon" title="show or hide right panel" id="btn_prop_showhidden"><i class="fa fa-bars fa-lg"></i></div>
	<div id='edit_panel'  class="right_panel_win" style0="display:none">
		<div id="tab_title" style="position: absolute;left:10px;top:3px;width:145px;font-size: 18px;border:0px solid;color:#555555;"></div>
	<div class="right_tab" >
      <ul style="white-space: nowrap;"><span style="left:5px;top:-10px;position:relative;border:0px solid;display: inline-block;width:156px"></span>
      </ul>
      <div>
        
      </div>
    </div>
  </div>
</div>
<div id="toolbar_basic" class="toolbox" style="color:#a7cf28">

	<i class="fa fa-crosshairs fa-2x" aria-hidden="true" onclick="draw_fit()"></i>
<%--
	<i id="def_work_prop_showhidden" class="fa fa-align-justify fa-2x"  onclick="tool_prop_show_hidden()"></i>
	<i id="conn_add" class="fa fa-arrow-right fa-2x"  onclick="tool_add_conn()"></i>
	 --%>
	<i id="net_save_basic" class="fa fa-save fa-2x" onclick="net_save_basic()" title="Save"></i>
	<i id="rt_update" class="fa fa-refresh fa-2x" onclick="rt_update()" title="Runtime Update"></i>
	<i id="rt_flow_start" class="fa fa-play fa-2x" onclick="rt_flow_start_stop(true)" title="Start Flow"></i>
	<i id="rt_flow_stop" class="fa fa-stop fa-2x" style="color:red;" onclick="rt_flow_start_stop(false)" title="Stop Flow"></i>
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
var prjid = "<%=prjid%>" ;
var netid = "<%=netid%>" ;

//toolbox_init("#toolbar_basic");
//toolbox_init("#orders_not_assigned");
var panel = null;
var editor = null ;

var loadLayer = null ;
var intedit =null;

var hmiModel=null;
var hmiView=null;

var cur_resolution = 1 ;

function prompt_f(msg)
{
	dlg.msg(msg) ;
}

mn.util.prompt_reg(prompt_f,prompt_f);

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
	
	mn.DrawLayer.RULE_SHOW=true;
	mn.DrawLayer.GRID_SHOW=true;
	
	hmiModel = new mn.MNModel();
	
	panel = new mn.DrawPanel("panel_main",{
		on_mouse_mv:on_panel_mousemv,
		on_resolution_chg:on_panel_resolution,
		on_model_chg:on_model_chg,
		on_item_sel_chg:on_item_sel_chg
	});
	panel.setInEdit(true);
	editor = new mn.DrawEditor("edit_props","edit_events","edit_toolbar",panel,{
		plug_cb:editor_plugcb,
		on_prompt_msg:on_editor_prompt
	}) ;
	hmiView = new mn.MNView(hmiModel,panel,editor,{
		copy_paste_url:"./util/copy_paste_ajax.jsp",
		on_model_loaded:()=>{
			panel.updatePixelSize();
			draw_fit()
			setTimeout("draw_fit()",1000)
			
		},
		onAddNode:(tp,x,y,moduleid)=>{
			send_ajax("./mn_ajax.jsp",{op:"node_add_up",prjid:prjid,netid:netid,"tp":tp,x:x,y:y,moduleid:moduleid||""},
	                (bsucc,ret)=>{
	                	
	                	if(!bsucc||ret!="succ")
	                	{
	                		dlg.msg(ret) ;
	                		return ;
	                	}
	                	reload_net();
	                }) ;
		},
		onAddModule:(tp,x,y)=>{
			send_ajax("./mn_ajax.jsp",{op:"module_add_up",prjid:prjid,netid:netid,"tp":tp,x:x,y:y},
	                (bsucc,ret)=>{
	                	
	                	if(!bsucc||ret!="succ")
	                	{
	                		dlg.msg(ret) ;
	                		return ;
	                	}
	                	reload_net();
	                }) ;
		},
		onAddConn:(out_id,to_nid)=>{
			send_ajax("./mn_ajax.jsp",{op:"conn_set",prjid:prjid,netid:netid,out_id:out_id,to_nid:to_nid},
	                (bsucc,ret)=>{
	                	if(!bsucc||ret!="succ")
	                	{
	                		dlg.msg(ret) ;
	                		return ;
	                	}
	                	console.log(ret) ;
	                	reload_net();
	                }) ;
		},
		onDelSelectItemsTrigger:(sis)=>{
			on_del_items(sis) ;
		},
		onDINodeOpen:(node)=>{
			on_item_open(node);
		},
		onDIModuleOpen:(node)=>{
			on_item_open(node);
		},
		onNodeStartTrigger:(node)=>{
			//console.log("start node trigger",node) ;
			send_ajax("./mn_ajax.jsp",{op:"node_start_trigger",prjid:prjid,netid:netid,"nodeid":node.id},
	                (bsucc,ret)=>{
	                	if(!bsucc||ret!="succ")
	                	{
	                		dlg.msg(ret) ;
	                		return ;
	                	}
	                	dlg.msg(ret) ;
	                }) ;
		}
	});
	
	hmiView.init();
	
	loadLayer = hmiView.getLayer();
	intedit = hmiView.getInteract();

	reload_net(true,true);
}

function reload_net(reload,bfit)
{
	send_ajax("mn_ajax.jsp",{op:"load_net",prjid:prjid,netid:netid},(bsucc,ret)=>{
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
	});
}

init_iottpanel();

function on_item_sel_chg(items)
{
	if(!items||items.length<=0)
	{
		let u1 = `mn_panel.jsp?prjid=\${prjid}&netid=\${netid}`;
		if(u1!=$("#right_info_iframe").attr("src"))
			$("#right_info_iframe").attr("src",u1);
		$("#left_pan_iframe")[0].contentWindow.show_by_module(prjid,netid,null) ;
		return ;
	}
	if(!items || items.length!=1)
	{
		$("#left_pan_iframe")[0].contentWindow.show_by_module(prjid,netid,null) ;
		return ;
	}
	let item = items[0] ;
	if(item.getClassName()==mn.view.DIModule.CN)
	{
		$("#left_pan_iframe")[0].contentWindow.show_by_module(prjid,netid,item.id,item.title) ;
	}
	else
	{
		$("#left_pan_iframe")[0].contentWindow.show_by_module(prjid,netid,null) ;
	}
	
	let u1 = `mn_panel.jsp?prjid=\${prjid}&netid=\${netid}&itemid=\${item.id}`;
	if(u1!=$("#right_info_iframe").attr("src"))
		$("#right_info_iframe").attr("src",u1);
}


function on_item_open(mn)
{
	let url = `mn_param.jsp?prjid=\${prjid}&netid=\${netid}&itemid=\${mn.id}`;
	let tt = "<wbt:lang>param</wbt:lang> - "+mn.title;
	let pm={op:"detail_set",prjid:prjid,netid:netid,itemid:mn.id};

	dlg.open(url,{title:tt,w:'500px',h:'400px'},
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

						 pm.jstr=JSON.stringify(ret) ;
						 send_ajax('mn_ajax.jsp',pm,function(bsucc,ret)
							{
								if(!bsucc || ret.indexOf('succ')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.msg("<wbt:g>done</wbt:g>");
								dlg.close();
								reload_net();
							},false);
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function on_editor_prompt(m)
{
	dlg.msg(m) ;
}

function net_save_basic()
{
	let ob = hmiView.extractNetBasic();
	send_ajax("mn_ajax.jsp",{op:"net_save_basic",prjid:prjid,netid:netid,jstr:JSON.stringify(ob)},(bsucc,ret)=>{
		if(!bsucc||ret!='succ')
		{
			dlg.msg(ret);
			return ;
		}
		dlg.msg("done");
		reload_net(true);
	});
}

function on_del_items(sis)
{
	if(sis==null||sis.length<=0)
		return ;

	let ids = "" ;
	for(let si of sis)
	{
		if(si.getClassName()=='mn.view.DINet')
			continue ;
		if(si.getUID)
			ids+= ((ids!='')?",":"")+si.getUID(); 
	}
	if(ids=="")
		return ;
	dlg.confirm("Are you sure to delete selected items?",{btn:["Sure","Cancel"],title:"Warn"},()=>
    {
		send_ajax("./mn_ajax.jsp",{op:"del_by_ids",prjid:prjid,netid:netid,"ids":ids},
                (bsucc,ret)=>{
                	if(!bsucc||ret!="succ")
                	{
                		dlg.msg(ret) ;
                		return ;
                	}
                	reload_net();
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


var b_left_show = false;
var W = 180 ;
function show_hiddle_left()
{
	w = W+"px" ;
	let p = $('#left_panel')
	if(b_left_show)
	{
		p.hide();
		p.attr('topm_show',"0") ;
		$("#mid").css("left","0px") ;
		b_left_show=false;
		panel.updatePixelSize() ;
		return ;
	}

	$("#mid").css("left",w) ;
	//slide_toggle(p,w);
	p.css('width',w) ;
	p.show();
	b_left_show = true ;
	panel.updatePixelSize() ;
}

show_hiddle_left();

function tool_prop_show_hidden()
{
	let v = $("#cont_props").css("display");
	if(v=='none')
		$("#cont_props").css("display","");
	else
		$("#cont_props").css("display","none");
		
}



// == debug
var debug_intv = null ;
var debug_ins_id = "" ;

function node_param_set(tp,node_id)
{
	let u = "./ui_plug/"+tp+".jsp?net_id="+net_id+"&node_id="+node_id+"&ins_gid="+debug_ins_id;
	dlg.open(u,{title:"Node Param Setup"},
			['Test','Apply','Ok','Cancel'],
			[
				function(dlgw)
				{
					node_param_test(tp,node_id,debug_ins_id,dlgw);
				},
				function(dlgw)
				{
					node_param_apply(node_id,dlgw);
				},
				function(dlgw)
				{
					node_param_apply(node_id,dlgw,()=>{
						dlg.close() ;
						});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}
				
function node_param_apply(node_id,dlgw,cb_ok)
{
	dlgw.get_param_jo((bsucc,ret)=>{
		 if(!bsucc)
		 {
			 dlg.msg(ret) ;
			 return;
		 }
		 
		 let pm={};
		 pm.op="node_pm_set" ;
		 pm.net_id = net_id ;
		 pm.node_id = node_id ;
		 if(debug_ins_id)
			 pm.ins_id = debug_ins_id ;
		 pm.pm_jo = JSON.stringify(ret) ;
		 send_ajax("rn_ajax.jsp",pm,(buscc,ret)=>{
			 if(!bsucc||ret!='succ') //ret.indexOf("{")!=0)
			 {
				 dlg.msg(ret) ;
				 return ;
			 }
			 //eval("ret="+ret) ;
			 dlg.msg("set param ok") ;
			 reload_rn();
			 if(cb_ok)
				 cb_ok();
		 });
	});
}
	
function node_param_test(tp,node_id,debug_ins_id,dlgw)
{
	let u = "./ui_plug/"+tp+"-test.jsp?net_id="+net_id+"&node_id="+node_id+"&ins_gid="+debug_ins_id;
	window.open(u) ;
}

function is_debuging()
{
	return debug_intv!=null;
}

var b_prop_show=false;
$('#btn_prop_showhidden').click(function(){
	if(b_prop_show)
	{
		$("#edit_panel").css("display","none");
		$("#btn_prop_showhidden");//.css("color","#1e1e1e");
		
		b_prop_show=false;
	}
	else
	{
		$("#edit_panel").css("display","");
		$("#btn_prop_showhidden");//.css("color","#ebeef3");
		b_prop_show=true;
	}
});

function init_right()
{
	$(".right_tab").tab({on_selected_tab:(tbid)=>{
		let title = "" ;
		switch(tbid)
		{
		case 'lb_tab_i':
			title=`<i class="fa fa-info fa-lg"></i> Info` ;break ;
		case 'lb_tab_help':
			title=`<i class="fa fa-question fa-lg"></i> Help` ;break ;
		case 'lb_tab_debug':
			title=`<i class="fa fa-bug fa-lg"></i> Debug` ;break ;
		}
		$("#tab_title").html(title) ;
	}});
	
	$('.right_tab').tab('addTab', {'title':`<i class="fa fa-info fa-lg"></i>`, 'id': 'lb_tab_i', 'content': `<iframe id="right_info_iframe" src="mn_panel.jsp?prjid=\${prjid}&netid=\${netid}" style="width:100%;top:0px;height:300px;overflow:hidden;margin: 0px;border:0px solid;padding: 0px;" ></iframe>`});
	$('.right_tab').tab('addTab', {'title':`<i class="fa fa-question fa-lg"></i>`, 'id': 'lb_tab_help', 'content': `<div id="help_cont">help</div>`});
	$('.right_tab').tab('addTab', {'title':`<i class="fa fa-bug fa-lg"></i>`, 'id': 'lb_tab_debug', 'content': `<div id="debug_cont">debug</div>`});
	$(".right_tab").tab('selectTab', 'lb_tab_i');
	
	$("#edit_panel").css("display","none");
	$("#btn_prop_showhidden");//.css("color","#1e1e1e");
	
	b_prop_show=false;
}

init_right();

function resize_zz()
{
	var h = $(window).height();
	$("#left_pan_iframe").css("height",(h-38)+"px");
	$("#right_info_iframe").css("height",(h-38)+"px");
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

function cxt_add_var(n)
{
	
}

function cxt_set_var(n)
{
	
}
// rt

function rt_flow_start_stop(b_start)
{
	let op = "rt_flow_start" ;
	if(!b_start)
		op = "rt_flow_stop" ;
	send_ajax("./mn_ajax.jsp",{op:op,prjid:prjid,netid:netid},
            (bsucc,ret)=>{
            	if(!bsucc||ret!="succ")
            	{
            		dlg.msg(ret) ;
            		return ;
            	}
            	dlg.msg("done") ;
            }) ;
}


function rt_update()
{
	let ids = hmiView.listShowRTDivItemIds();
	send_ajax("./mn_ajax.jsp",{op:"rt_update",prjid:prjid,netid:netid,"div_ids":ids.join(",")},
            (bsucc,ret)=>{
            	if(!bsucc||ret.indexOf("{")!=0)
            	{
            		//dlg.msg(ret) ;
            		console.log(ret) ;
            		return ;
            	}
            	let ob = null ;
            	eval("ob="+ret) ;
            	hmiModel.on_rt_data(ob) ;
            }) ;
}
            
setInterval(rt_update,300) ;
            
function rt_item_runner_start_stop(itemid,b_start)
{
	send_ajax("./mn_ajax.jsp",{op:"rt_item_runner_start_stop",prjid:prjid,netid:netid,itemid:itemid,start_stop:b_start},
            (bsucc,ret)=>{
            	if(!bsucc||ret!='succ')
            	{
            		dlg.msg(ret) ;
            		return ;
            	}
            	dlg.msg("cmd done") ;
            }) ;
}

function debug_in_out_msg(nodeid,outidx)
{
	let op = "rt_debug_msg";

	send_ajax("mn_ajax.jsp",{op:op,prjid:prjid,netid:netid,nodeid:nodeid,outidx:outidx},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let pm;
		eval("pm="+ret) ;
		//console.log(pm) ;
		dlg.open("./mn_debug_msg.jsp",
				{title:'<wbt:g>debug,data</wbt:g>',pm:pm},
				['<wbt:g>cancel</wbt:g>'],
				[
					function(dlgw)
					{
						dlg.close();
					}
				]);
	}) ;
}

function debug_prompt_detail(itemid,lvl) //err warn info
{
	let op = "rt_debug_prompt";

	send_ajax("mn_ajax.jsp",{op:op,prjid:prjid,netid:netid,itemid:itemid,lvl:lvl},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let pm;
		eval("pm="+ret) ;
		//console.log(pm) ;
		dlg.open("./mn_debug_prompt.jsp",
				{title:'<wbt:g>debug,data</wbt:g>',pm:pm},
				['<wbt:g>cancel</wbt:g>'],
				[
					function(dlgw)
					{
						dlg.close();
					}
				]);
	}) ;
}

</script>

</html>