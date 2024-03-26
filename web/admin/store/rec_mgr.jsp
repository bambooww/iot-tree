<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.record.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return ;

String prjid = request.getParameter("prjid");
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
RecManager recmgr = RecManager.getInstance(prj) ;
%><html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
</head>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
-moz-user-select : none;
-webkit-user-select: none;
}

select option
{
font-size: 12px;
}

.oc-toolbar .toolbarbtn
{
width:40px;height:40px;margin: 5px;
font-size: 13px;
background-color: #eeeeee
}

.left
{
	position: absolute;
	top:0px;
	left:0px;
	border:0px solid;
	width:20%;
	bottom:0px;
}

.mid
{
	position: absolute;
	top:0px;
	left:20%;
	border:0px solid;
	width:40%;
	bottom:0px;
}

.right
{
	position: absolute;
	top:0px;
	right:0px;
	border:0px solid;
	width:40%;
	bottom:0px;
}



.list
{
	position: absolute;
	top:60px;
	bottom:0px;
	width:100%;
	border:0px solid;
	border-color:red;
	scrollbar-width: none; /* firefox */
  -ms-overflow-style: none; /* IE 10+ */
  overflow-x: hidden;
  overflow-y: auto;
}

.list::-webkit-scrollbar {
  display: none; /* Chrome Safari */
}

.tag_item
{
	position:relative;
	margin-bottom:3px;
	height:25px;
	font-size:15px;
	margin-left:10px;
	border:1px solid;
	right:0px;
	//display:none;
}

.tag_item .tt
{
	position:absolute;
	left:20px;right:20px;
	text-overflow:ellipsis;
	overflow:hidden;
	white-space:nowrap;
}

.tag_item .en
{
position:absolute;
	top:3px;
	left:2px;
}

.chk_tag_c
{
	position:absolute;
	right:0px;
	margin-top:0px;
	//visibility: hidden;
}

.p_item
{
	position:absolute;
	left:10%;
	width:80%;
	height:60px;
	border:1px solid;
	margin-bottom: 10px;
}

.p_item .t
{
	position:absolute;
	font-size: 18px;
	top:2px;
	left:20px;
}

.p_item .rt
{
	position:absolute;
	font-size: 15px;
	top:2px;
	right:76px;
}


.p_item .f
{
	position:absolute;
	font-size: 15px;
	left:3px;
	color:#00988b;
	cursor:pointer;
	bottom:7px;
}
.p_item .s
{
	position:absolute;
	font-size: 15px;
	left:140px;
	color:#00988b;
	cursor:pointer;
	bottom:7px;
}
.p_item .save_btn
{
	position:absolute;
	font-size: 15px;
	right:100px;
	color:#00988b;
	cursor:pointer;
	visibility:hidden;
	top:0px;
}

.enable_c
{
	font-size: 15px;
}

.p_item .oper
{
	position:absolute;
	font-size: 15px;
	right:10px;
	top:7px;
}


.sel
{
	border:2px solid;
	border-color: blue;
}



.in_conn_panel
{
	position: absolute;
	left:0px;width:10%;top:0px;bottom:0px;
	border:0px solid;
	border-color: red;
	
}

.out_conn_panel
{
	position: absolute;
	right:0px;width:10%;top:0px;bottom:0px;
	border:0px solid;
	border-color: red;
}
</style>
<body marginwidth="0" marginheight="0">
<div class="left">
 <blockquote class="layui-elem-quote "><wbt:g>rec_tags</wbt:g></blockquote>
 <div class="list" onscroll="on_list_scroll()">
<%
List<RecTagParam> params = recmgr.listRecTagParams();
int tags_num = params.size() ;
	for(RecTagParam pm:params)
	{
		UATag tag = pm.getUATag() ;
		String tagid = tag.getId() ;
		String np = tag.getNodeCxtPathIn(prj) ;
		String tt = "["+tag.getValTp()+"]  "+tag.getNodePathName()+"&#13;"+tag.getNodePathTitle() ;
		String en_c = true?"green":"gray" ;
		String en_t = true?"enabled":"disabled" ;
%><div class="tag_item" title="<%=tt%>" tag_id="<%=tagid%>" tag_np="<%=np %>" id="tag_<%=tagid%>" ><i class="fa fa-square en" style="color:<%=en_c%>" title="<wbt:g><%=en_t%></wbt:g>"></i>
	<span class="tt"><%=np %></span><span class="chk_tag_c"><input type="checkbox"  tag_id="<%=tagid %>" class="chk_tag" onclick="on_chk_tag(this,'<%=tagid%>')"/></span>
	</div>
<%
	}
%>
	<div style="height:60%;"></div>
 </div>
 </div>
<div class="mid" onclick="on_pro_clk()">
 <blockquote class="layui-elem-quote "><wbt:g>processor</wbt:g> L1
 <div style="position: absolute;right:10px;top:11px;width:100px;border:0px solid;height:35px;">
 <button type="button" style="top:3px;" class="layui-btn layui-btn-sm " onclick="add_pro_sel(false)">+<wbt:g>add</wbt:g></button>
 </div>
</blockquote>

 <div id="pro_l1_list"  class="list" onscroll="on_list_scroll()">
 	
 </div>
 <div style="height:20%;"></div>
 <div id="in_conn_panel" class="in_conn_panel"></div>
 <div id="out_conn_panel" class="out_conn_panel"></div>
</div>

<div class="right">

 <blockquote class="layui-elem-quote "><wbt:g>processor</wbt:g> L2
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 	<button type="button" style="top:3px;" class="layui-btn layui-btn-sm " onclick="add_pro_sel(true)">+<wbt:g>add</wbt:g></button>
 </div>
</blockquote>

 <div id="out_list" class="list" onscroll="on_list_scroll()">
 </div>
 <div style="height:20%;"></div>
</div>

<script>
var prjid = "<%=prjid%>" ;
var tags_num=<%=tags_num%>;

var pros = null ;

var cur_pro = null ;
var cur_p = null ;

function get_p_by_id(id)
{
	if(!pros)
		return null;
	for(let h of pros)
	{
		if(id==h.id)
			return h ;
	}
	return null ;
}

function chk_cur_p_filter_fit(tag_path)
{
	return true ;
}

function on_pro_clk(item)
{
	event.stopPropagation();
	cur_pro = item ;
	if(cur_pro==null)
	{
		cur_p = null ;
	}
	else
	{
		let id = $(item).attr("hid") ;
		cur_p = get_p_by_id(id);
		if(!cur_p)
		{
			dlg.msg("no pro with id found ["+id+"]") ;
			return ;
		}
	}
	
	$(".p_item").removeClass("sel") ;
	if(item)
		$(item).addClass("sel") ;
	
	update_tags();
	
}

function show_tags(ids)
{
	$(".tag_item").each(function(){
		let me = $(this) ;
		let tagid = me.attr("tag_id") ;
		if(!(ids==null || ids.indexOf(tagid)>=0))
		{
			me.css("display","none") ;
			return;
		}
		
		me.css("display","") ;
		
		let inp = me.find("input");
		if(!cur_p) //cur_p.sel_all)
		{
			let inp = me.find("input");
			inp.prop("checked",false);
			inp.prop("disabled",true);
			return ;
		}
		
		inp.prop("disabled",false);
		inp.prop("checked",cur_p.sel_tags.indexOf(tagid)>=0);
		
	}) ;
	
	redraw_conn();
}


function update_tags()
{
	//console.log(cur_p) ;
	if(!cur_p)
	{
		//$(".tag_item").css("display","") ;
		show_tags(null) ;
		return ;
	}
	
	send_ajax("rec_ajax.jsp",{op:"pro_fit_tagids",proid:cur_p.id,prjid:prjid},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let ids = null ;
		eval("ids="+ret) ;
		show_tags(ids) ;
	}) ;
	//console.log(cur_p) ;
	/*
	
	*/
}

update_tags();

function set_dirty(hid,b)
{
	let id = hid ;
	if(b)
	{
		$("#btn_save_"+id).removeClass("layui-btn-primary");
		$("#btn_save_"+id).addClass("layui-btn-warm");
		$("#btn_save_"+id).css("visibility","visible") ;
	}
	else
	{
		$("#btn_save_"+id).removeClass("layui-btn-warm");
		$("#btn_save_"+id).addClass("layui-btn-primary");
		$("#btn_save_"+id).css("visibility","hidden") ;
	}
	
	redraw_conn();
}

function on_chk_tag(inp_ele,tagid)
{
	if(!cur_p)
		return ;
	
	let chked = $(inp_ele).prop("checked");
	
	if(chked)
	{
		if(cur_p.sel_tags.indexOf(tagid)<0)
			cur_p.sel_tags.push(tagid) ;
	}
	else
	{
		let k = cur_p.sel_tags.indexOf(tagid) ;
		if(k>=0)
		{
			cur_p.sel_tags.splice(k,1) ;
		}
	}
	
	set_dirty(cur_p.id,true)
}


function update_outs()
{
	let tmps="" ;
}

function update_pros()
{
	send_ajax("rec_ajax.jsp",{op:"list_p",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let os = null ;
		eval("os="+ret) ;
		pros = os ;
		
		update_outs();
		
		let tmps="" ;
		let y = 10 ;
		for(let ob of os)
		{
			let en_c = ob.en?"green":"gray" ;
			let en_t = ob.en?"<wbt:g>enabled</wbt:g>":"<wbt:g>disabled</wbt:g>" ;
			tmps += `<div id="p_\${ob.id}" style="top:\${y}px" class="p_item" hid="\${ob.id}" n="\${ob.n}" t="\${ob.t}" tp="\${ob.tp}" onclick="on_pro_clk(this)" alert_uids="\${ob.alert_uids}">
				<span class="t">\${ob.t} [\${ob.n}]</span>
				<span class="enable_c"><i class="fa fa-square en" style="color:\${en_c}" title="\${en_t}"></i></span>
				<span class="f" ><i class="fa fa-gear" style="font-size:16px;"></i>\${ob.tpt}</span>
				<button id="btn_save_\${ob.id}"  type="button" class="layui-btn layui-btn-sm layui-border-blue layui-btn-primary save_btn" onclick="save_p_tag_ids('\${prjid}','\${ob.id}')">Save</button>
				<span class="oper">
					<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="access_p('\${ob.tp}','\${ob.t}','\${ob.id}')"><i class="fa fa-paper-plane"></i></button>
					<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_p('\${ob.tp}','\${ob.t}','\${ob.id}')"><i class="fa fa-pencil"></i></button>
					<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_pro('\${prjid}','\${ob.id}')" title="delete"><i class="fa-regular fa-rectangle-xmark"></i></button>
				</span>

				</div>` ;
			//cc ++ ;
			y = y + 70;
		}
		$("#pro_l1_list").html(tmps) ;
		
		redraw_conn();
	}) ;
}

update_pros();

function save_p_tag_ids(prjid,proid)
{
	let h = get_p_by_id(proid) ;
	let idstr = "" ;
	if(h.sel_tags)
		idstr = h.sel_tags.join() ;
	
	send_ajax("rec_ajax.jsp",{op:"set_p_sel_tagids",prjid:prjid,id:proid,idstr:idstr},(bsucc,ret)=>{
		dlg.msg(ret) ;
		set_dirty(proid,false) ;
	});
}
	
function del_pro(prjid,id)
{
	//event.stopPropagation();
	let ob = $("#p_"+id);
	dlg.confirm('<wbt:g>del,this,processor</wbt:g> ['+ob.attr("n")+']?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
		    {
					send_ajax("rec_ajax.jsp",{op:"del_p",prjid:prjid,id:id},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
			    			return ;
			    		}
			    		//
						update_pros();
			    	}) ;
				});
}

function add_pro_sel(b_l2)
{
	let u = "./rec_pro_sel.jsp?l1=true";
	if(b_l2)
		u = "./rec_pro_sel.jsp?l2=true";
	
	dlg.open(u,{title:"<wbt:g>select,processor,type</wbt:g>"},
			['<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			],(ret)=>{
				if(!ret)
					return ;
				add_or_edit_p(ret.tp,ret.tt,null) ;
			});
}

function add_or_edit_p(tp,t,id)
{
	let tt = "<wbt:g>add,processor</wbt:g> ["+t+"]";
	if(id)
	{
		tt = "<wbt:g>edit,processor</wbt:g> ["+t+"]";
	}
	if(id==null)
		id = "" ;
	dlg.open("rec_pro_"+tp+"_edit.jsp?prjid="+prjid+"&id="+id,
			{title:tt},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 let op="set_pro" ;
						 ret.prjid=prjid;
						 ret.id = id ;
						 ret.tp = tp ;
						 var pm = {
									type : 'post',
									url : "./rec_ajax.jsp",
									data :{op:op,prjid:prjid,jstr:JSON.stringify(ret)}
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								update_pros();
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

function access_p(tp,tt,id)
{
	event.stopPropagation();
	dlg.msg("TODO") ;
}

/*
 
 
function del_out(prjid,hid,id)
{
	event.stopPropagation();
	dlg.confirm('<wbt:g>delete,this,output</wbt:g>?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
		    {
					send_ajax("store_ajax.jsp",{op:"del_o",prjid:prjid,hid:hid,id:id},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
			    			return ;
			    		}
			    		//
						update_pros() ;
			    	}) ;
				});
}

function on_clk_filter(hid,filter_str)
{
	if(!filter_str)
	{
		dlg.open("./filter_sel.jsp",
				{title:"<wbt:g>select,filter,type</wbt:g>"},
				['<wbt:g>cancel</wbt:g>'],
				[
					function(dlgw)
					{
						dlg.close();
					}
				],(ret)=>{
					if(!ret)
						return ;
					add_or_edit_filter(hid,ret.tp,null) ;
				});
		return ;
	}
	
	let k = filter_str.indexOf("-") ;
	let tp = filter_str.substring(0,k) ;
	let sub_str = filter.substring(k+1) ;
	add_or_edit_filter(hid,tp,sub_str)
}


function add_o(prjid,hid)
{
	dlg.open("./o_sel.jsp?prjid="+prjid+"&hid="+hid,
			{title:"<wbt:g>select,sor,type</wbt:g>"},
			['<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			],(ret)=>{
				if(!ret)
					return ;
				add_or_edit_o(prjid,ret.tp,hid,null) ;
			});
}

function add_or_edit_o(prjid,tp,hid,id)
{
	if(event)
		event.stopPropagation();
	var tt = "<wbt:g>add,handler,output</wbt:g> ["+tp+"]";
	if(id)
	{
		tt = "<wbt:g>edit,handler,output</wbt:g> ["+tp+"]";
	}
	if(id==null)
		id = "" ;
	dlg.open("o_"+tp+"_edit.jsp?prjid="+prjid+"&hid="+hid+"&id="+id,
			{title:tt},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 let op="set_o" ;
						 ret.prjid=prjid;
						 ret.hid = hid ;
						 ret.id = id ;
						 var pm = {
									type : 'post',
									url : "./store_ajax.jsp",
									data :{op:op,prjid:prjid,jstr:JSON.stringify(ret)}
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								update_pros();
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

function view_o(prjid,tp,hid,id)
{
	if(event)
		event.stopPropagation();
	var tt = "<wbt:g>view,output,data</wbt:g> ["+tp+"]";
	dlg.open("o_"+tp+"_view.jsp?prjid="+prjid+"&hid="+hid+"&id="+id,
			{title:tt},
			['<wbt:g>close</wbt:g>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
}
function init_o(prjid,hid,id)
{
	dlg.loading(true) ;
	send_ajax("store_ajax.jsp",{op:"init_o",prjid:prjid,hid:hid,id:id},(bsucc,ret)=>{
		dlg.loading(false) ;
		dlg.msg(ret) ;
	});
}
*/




var in_panel = null ;
var in_can = null ;
var in_cxt = null ;
var in_w = null ;

var out_panel = null ;
var out_can = null ;
var out_cxt = null ;
var out_w = null ;

function conn_init_in()
{
	in_cxt = document.createElement('canvas').getContext('2d');
	let can = $(in_cxt.canvas);
	in_can = can ;
	can.css("position", "relative");
	can.css("left", "0px");
	can.css("top", "0px");
	can.css("display","");
	in_panel= $("#in_conn_panel");
	can.attr('width', in_panel[0].offsetWidth) ;
	can.attr('height', in_panel[0].offsetHeight-5) ;
	in_panel.append(can);
	in_w = in_panel[0].offsetWidth;
	in_panel.resize(()=>{
		in_w = in_panel[0].offsetWidth;
		var h = in_panel[0].offsetHeight;
		//console.log(w,h)
		can.attr('width',in_w) ;
		can.attr('height', h) ;
		redraw_conn();
	});
}

function conn_init_out()
{
	out_cxt = document.createElement('canvas').getContext('2d');
	let can = $(out_cxt.canvas);
	out_can = can ;
	can.css("position", "relative");
	can.css("left", "0px");
	can.css("top", "0px");
	can.css("display","");
	out_panel= $("#out_conn_panel");
	can.attr('width', out_panel[0].offsetWidth) ;
	can.attr('height', out_panel[0].offsetHeight-5) ;
	out_panel.append(can);
	out_w = out_panel[0].offsetWidth;
	out_panel.resize(()=>{
		in_w = out_panel[0].offsetWidth;
		var h = out_panel[0].offsetHeight;
		//console.log(w,h)
		can.attr('width',in_w) ;
		can.attr('height', h) ;
		redraw_conn();
	});
}

function conn_init()
{
	conn_init_in();
	conn_init_out();
}


function redraw_conn()
{
	if(!in_can || !out_can)
		return ;
	in_can[0].width = in_can[0].width;
	out_can[0].width = out_can[0].width;
	$(".p_item").each(function(){
		redraw_conns_p(this) ;
	}) ;
	//if(cur_p)
	//	redraw_conns_h(cur_pro) ;
}

function redraw_conns_p(pro)
{
	let pro_id = $(pro).attr("hid") ;
	let h_y = $(pro).offset().top + $(pro).height()/2;

	in_cxt.save();
	out_cxt.save();
	//in_cxt.
	if(pro==cur_pro)
	{
		in_cxt.lineWidth = 1;
		in_cxt.strokeStyle = "blue";
		out_cxt.lineWidth = 2;
		out_cxt.strokeStyle = "blue";
		
		$(".chk_tag").each(function(){
			
			if(!cur_p) return ;
			let me = $(this);
			
			if(me.is(':hidden'))
				return ;
			let tagid = me.attr("tag_id") ;
			let chked = cur_p.sel_all || cur_p.sel_tags.indexOf(tagid)>=0
			if(!chked) return ;
			
			let y = me.offset().top+me.height()/2;
			//if(y==0) return ;
			//console.log(y) ;
			in_cxt.beginPath() ;
			in_cxt.moveTo(0,y);
			in_cxt.lineTo(in_w/2,y);
			in_cxt.lineTo(in_w/2,h_y);
			in_cxt.lineTo(in_w,h_y);
			in_cxt.stroke() ;
		});
		
		in_cxt.lineWidth = 2;
		in_cxt.strokeStyle = "blue";
		out_cxt.lineWidth = 2;
		out_cxt.strokeStyle = "blue";
	}
			
	
	
	$(".chk_out").each(function(){
		let me = $(this);
		let hid = me.attr("hid") ;
		//let id = me.attr("id") ;
		if(hid!=pro_id)
			return ;
		let y = me.offset().top+me.height()/2;
		out_cxt.beginPath() ;
		out_cxt.moveTo(out_w,y);
		out_cxt.lineTo(0,h_y);
		out_cxt.stroke() ;
	});
	
	in_cxt.restore();
	out_cxt.restore();
}

conn_init();

function on_list_scroll()
{
	redraw_conn()
}


function rt_update()
{
	send_ajax("store_ajax.jsp",{op:"rt_data",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc&&ret.indexOf("{")!=0)
		{
			//show err
			return;
		}
		let ob = null;
		eval("ob="+ret) ;
		for(let h of ob.pros)
		{
			let c = "grey" ;
			if(h.en)
				c = h.run?"green":"red" ;
			
			if(h.run)
				$("#h_run_"+h.id).addClass("fa-spin").css("color",c);
			else
				$("#h_run_"+h.id).removeClass("fa-spin").css("color",c);
			
			for(let o of h.outs)
			{
				let init_ok = o.init_ok ;
				let init_c = "grey" ;
				let init_t="" ;
				if(h.run && h.en)
				{
					init_c = init_ok?"green":"red" ;
					init_t = init_ok?"<wbt:g>init,ok</wbt:g>":"<wbt:g>init,failed</wbt:g>" ;
				}
					
				let run_ok = o.run_ok;
				let run_c = "grey";
				let run_t="" ;
				if(!h.run)
				{
					$("#o_rt_"+o.id).css("visibility","hidden") ;
					return ;
				}
				
				$("#o_rt_"+o.id).css("visibility","visible") ;
				if(h.run && h.en)
				{
					run_c = run_ok?"green":"red" ;
					run_t = run_ok?"<wbt:g>run,ok</wbt:g>":"<wbt:g>run,err</wbt:g>" ;
				}
					
				$("#o_init_"+o.id).css("color",init_c).attr("title",init_t) ;
				$("#o_run_"+o.id).css("color",init_c).attr("title",run_t) ;
				if(h.run && h.en && o.err)
					$("#o_err_"+o.id).css("color","red").css("visibility","visible").attr("title",o.err) ;
				else
					$("#o_err_"+o.id).css("visibility","hidden") ;
			}
		}
		
	}) ;
}

function rt_get_info()
{
	send_ajax("rec_ajax.jsp",{op:"rt_data",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc)
		{
			console.log(ret) ;
			return ;
		}
		let ob = null ;
		eval("ob="+ret) ;
		$("#rt_info").html("<pre>"+ret+"</pre>") ;
	}); 
}

//setInterval(rt_get_info,3000) ;
//setInterval(rt_update,7000) ;

</script>

</body>
</html>