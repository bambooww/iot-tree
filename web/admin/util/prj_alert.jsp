<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*
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
	right:20%;
	bottom:0px;
}

.right
{
	position: absolute;
	top:0px;
	right:0px;
	border:0px solid;
	width:20%;
	bottom:0px;
}

.list
{
	position: absolute;
	top:50px;
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

.alert_item
{
	position:relative;
	margin-bottom:3px;
	height:25px;
	font-size:15px;
	margin-left:10px;
	border:1px solid;
	right:0px;
	
}

.alert_item .tt
{
	position:absolute;
	left:20px;right:20px;
	text-overflow:ellipsis;
	overflow:hidden;
	white-space:nowrap;
}

.alert_item .en
{
position:absolute;
	top:3px;
	left:2px;
}

.chk_alert_c
{
	position:absolute;
	right:0px;
	margin-top:0px;
	visibility: hidden;
}

.h_item
{
	position:relative;
	left:10%;
	width:80%;
	height:60px;
	border:1px solid;
	margin-top: 7px;
	margin-bottom: 20px;
}

.h_item .t
{
	position:absolute;
	font-size: 20px;
	top:5px;
	left:10px;
}
.h_item .trigger_c
{
	position:absolute;
	font-size: 20px;
	border:1px solid;
	top:5px;
	right:170px;
}

.h_item .release_c
{
	position:absolute;
	font-size: 20px;
	top:5px;
	border:1px solid;
	right:80px;
}

.h_item .record_c
{
	position:absolute;
	font-size: 13px;
	bottom:1px;
	left:30px;
}

.h_item .oper
{
	position:absolute;
	font-size: 15px;
	right:10px;
	bottom:7px;
}


.out_item
{
	position:relative;
	left:0%;
	width:90%;
	min-height:60px;
	border:1px solid;
	display: flex;
	margin-top: 7px;
	margin-bottom: 10px;
}
.out_item .n
{
	position:absolute;
	font-size: 18px;
	left:5px;
}
.out_item .t
{
	font-size: 20px;
	top:18px;
	margin-left:0px;
	left:0px;
	border:0px solid;
	word-break:break-all;
	margin-bottom: 30px;
}
.out_item .tp
{
	position:absolute;
	font-size: 25px;
	bottom:10px;
	left:10px;
}
.out_item .tpt
{
	position:absolute;
	font-size: 15px;
	bottom:1px;
	left:5px;
}
.out_item .oper
{
	position:absolute;
	font-size: 15px;
	right:10px;
	bottom:5px;
}

.sel
{
	border:2px solid;
	border-color: blue;
}

.chk_out_c
{
visibility: hidden;
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
 <blockquote class="layui-elem-quote "><wbt:g>alert,sors</wbt:g></blockquote>
 <div class="list">
<%
	for(UATag tag:prj.listTagsAll())
	{
		List<ValAlert> vas = tag.getValAlerts() ;
		if(vas==null||vas.size()<=0)
			continue ;
		String np = tag.getNodePath() ;
		
%>
<div class="tag_item" id="np"><%=np %>
<%
		for(ValAlert va:vas)
		{
			String id = va.getUid() ;
			String tt = Convert.plainToHtml(va.toTitleStr()) ;
			String en_c = va.isEnable()?"green":"gray" ;
			String en_t = va.isEnable()?"enabled":"disabled" ;
%><div class="alert_item" title="<%=tt%>"><i class="fa fa-square en" style="color:<%=en_c%>" title="<wbt:g><%=en_t%></wbt:g>"></i>
	<span class="tt"><%=tt %></span><span class="chk_alert_c"><input type="checkbox" id="<%=id %>"  class="chk_alert" onclick="on_chk_alert('<%=id%>')"/></span>
	</div>

<%
		}
%>
</div>
<%
	}
%>
 </div>
</div>
<div class="mid" onclick="on_handler_clk()">
 <blockquote class="layui-elem-quote "><wbt:g>alert,handlers</wbt:g>
 <div style="position: absolute;right:10px;top:11px;width:220px;border:0px solid;height:35px;">
 <button id="btn_save" type="button" class="layui-btn layui-btn-sm layui-border-blue layui-btn-primary" onclick="show_alert_his('<%=prjid %>')"><wbt:g>history</wbt:g></button>
 <button id="btn_save" type="button" class="layui-btn layui-btn-sm layui-border-blue layui-btn-primary" onclick="save_h_inout_ids('<%=prjid %>')"><wbt:g>save</wbt:g></button>
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_edit_h('<%=prjid %>',null)">+<wbt:g>add</wbt:g> </button>
 </div>
</blockquote>
 <div id="handler_list" class="list">
 	
 </div>
 <div id="in_conn_panel" class="in_conn_panel"></div>
 <div id="out_conn_panel" class="out_conn_panel"></div>
</div>
<div class="right">
 <blockquote class="layui-elem-quote "><wbt:g>outputs</wbt:g>
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_edit_o('<%=prjid %>',null)">+<wbt:g>add</wbt:g> </button>
 </div>
</blockquote>
 <div id="out_list" class="list">
 </div>
</div>
 	
<script>
var prjid = "<%=prjid%>" ;

var cur_out = null ;

var cur_handler = null ;


function on_out_clk(item)
{
	this.cur_out = item ;
	$(".out_item").removeClass("sel") ;
	$(item).addClass("sel") ;
}

function on_handler_clk(item)
{
	event.stopPropagation();
	this.cur_handler = item ;
	$(".h_item").removeClass("sel") ;
	if(item)
		$(item).addClass("sel") ;
	
	update_chks();
	redraw_conn();
}

function update_chks()
{
	
	if(!cur_handler)
	{
		$(".chk_alert_c").css("visibility","hidden") ;
		$(".chk_out_c").css("visibility","hidden") ;
		return ;
	}
	//console.log(cur_handler)
	$(".chk_alert_c").css("visibility","visible") ;
	$(".chk_out_c").css("visibility","visible") ;
	
	let out_ids_str = $(cur_handler).attr("out_ids") ;
	let alert_uids_str = $(cur_handler).attr("alert_uids") ;
	
	let out_ids = out_ids_str.split(',');
	let alert_uids = alert_uids_str.split(',');
	$(".chk_alert").each(function(){
		let id = $(this).attr("id") ;
		let k = alert_uids.indexOf(id);
		$(this).prop("checked",k>=0);
	});
	
	$(".chk_out").each(function(){
		let id = $(this).attr("id") ;
		let k = out_ids.indexOf(id);
		$(this).prop("checked",k>=0);
	});
}

function set_dirty(b)
{
	if(b)
	{
		$("#btn_save").removeClass("layui-btn-primary");
		$("#btn_save").addClass("layui-btn-warm");
	}
	else
	{
		$("#btn_save").removeClass("layui-btn-warm");
		$("#btn_save").addClass("layui-btn-primary");
	}
	
	redraw_conn();
}

function on_chk_alert(alert_uid)
{
	if(!cur_handler)
		return ;
	
	let ele = document.getElementById(alert_uid) ;
	let chked = $(ele).prop("checked");
	
	let alert_uids_str = $(cur_handler).attr("alert_uids") ;
	let alert_uids = alert_uids_str.split(',');
	
	if(chked)
	{
		if(alert_uids.indexOf(alert_uid)>=0)
			return ;
		alert_uids.push(alert_uid) ;
		alert_uids_str = alert_uids.join(',') ;
		$(cur_handler).attr("alert_uids",alert_uids_str) ;
	}
	else
	{
		let k = alert_uids.indexOf(alert_uid) ;
		if(k<0)
			return ;
		alert_uids.splice(k,1) ;
		alert_uids_str = alert_uids.join(',') ;
		$(cur_handler).attr("alert_uids",alert_uids_str) ;
	}
	
	set_dirty(true)
}

function on_chk_out(out_id)
{
	if(!cur_handler)
		return ;
	
	let ele = document.getElementById(out_id) ;
	let chked = $(ele).prop("checked");
	
	let out_ids_str = $(cur_handler).attr("out_ids") ;
	let out_ids = out_ids_str.split(',');
	let k = out_ids.indexOf(out_id) ;
	if(chked)
	{
		if(k>=0)
			return ;
		out_ids.push(out_id) ;
		out_ids_str = out_ids.join(',') ;
		$(cur_handler).attr("out_ids",out_ids_str) ;
	}
	else
	{
		
		if(k<0)
			return ;
		out_ids.splice(k,1) ;
		out_ids_str = out_ids.join(',') ;
		$(cur_handler).attr("out_ids",out_ids_str) ;
	}
	
	set_dirty(true);
}

function update_outs(end_cb)
{
	send_ajax("prj_alert_ajax.jsp",{op:"list_o",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let os = null ;
		eval("os="+ret) ;
		let tmps="" ;
		for(let ob of os)
		{
			//console.log(ob) ;
			tmps += `<div id="out_\${ob.id}" class="out_item" tp="\${ob.tp}" t="\${ob.t}" onclick0="on_out_clk(this)" out_ids="">
				<span class="chk_out_c"><input type="checkbox" id="\${ob.id}"  class="chk_out" onclick="on_chk_out('\${ob.id}')"/></span>
				<span class="t">\${ob.t}</span>
				<span class="tpt">\${ob.tpt}</span>
				<span class="oper">
					<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_o('\${prjid}','\${ob.id}')"><i class="fa fa-pencil"></i></button>
					<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_out('\${prjid}','\${ob.id}')" title="<wbt:g>del</wbt:g>"><i class="fa-regular fa-rectangle-xmark"></i></button>
				</span>
				</div>` ;
		}
		$("#out_list").html(tmps) ;
		if(end_cb)
			end_cb() ;
	}) ;
	
}



function update_handlers()
{
	send_ajax("prj_alert_ajax.jsp",{op:"list_h",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let os = null ;
		eval("os="+ret) ;
		let tmps="" ;
		for(let ob of os)
		{
			//console.log(ob) ;
			let trigger_c = ob.trigger_c?'background-color:'+ob.trigger_c:'' ;
			let trigger_dis = ob.trigger_en?'':'display:none' ;
			let release_c = ob.release_c?'background-color:'+ob.release_c:'' ;
			let release_dis = ob.release_en?'':'display:none' ;
			let inner_record = ob.b_inner_record?"√":"×";
			let outer_record = ob.b_outer_record?"√":"×";
			
			tmps += `<div id="h_\${ob.id}" class="h_item" h_id="\${ob.id}" t="\${ob.t}" onclick="on_handler_clk(this)" out_ids="\${ob.out_ids}" alert_uids="\${ob.alert_uids}">
				<span class="t">\${ob.t}</span>
				<span class="trigger_c" style="\${trigger_c};\${trigger_dis}">Trigger [\${ob.lvl}]&nbsp;</span>
				<span class="release_c" style="\${release_c};\${release_dis}">Release</span>
				<span class="record_c" >Inner Record:\${inner_record} Outter Record:\${outer_record}</span>
				<span class="oper">
					<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_h('\${prjid}','\${ob.id}')"><i class="fa fa-pencil"></i></button>
					<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_handler('\${prjid}','\${ob.id}')" title="delete"><i class="fa-regular fa-rectangle-xmark"></i></button>
				</span>
				</div>` ;
		}
		$("#handler_list").html(tmps) ;
		
		redraw_conn();
	}) ;
}

update_outs(()=>{
	update_handlers();
});

function save_h_inout_ids(prjid)
{
	let ss = [] ;
	$(".h_item").each(function(){
		let id = $(this).attr("h_id") ;
		let out_ids = $(this).attr("out_ids") ;
		let alert_uids = $(this).attr("alert_uids") ;
		ss.push({id:id,out_ids:out_ids,alert_uids:alert_uids}) ;
	});
	send_ajax("prj_alert_ajax.jsp",{op:"save_h_ids",prjid:prjid,jstr:JSON.stringify(ss)},(bsucc,ret)=>{
		dlg.msg(ret) ;
		set_dirty(false) ;
	});
}
	
function del_handler(prjid,id)
{
	event.stopPropagation();
	dlg.confirm('<wbt:g>del,this,handler</wbt:g>?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
		    {
					send_ajax("prj_alert_ajax.jsp",{op:"del_h",prjid:prjid,id:id},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
			    			return ;
			    		}
			    		//
						update_handlers();
			    	}) ;
				});
}

function add_or_edit_h(prjid,id)
{
	event.stopPropagation();
	var tt = "<wbt:g>add,alert,handler</wbt:g>";
	if(id)
	{
		tt = "<wbt:g>edit,alert,handler</wbt:g>";
	}
	if(id==null)
		id = "" ;
	dlg.open("prj_alert_h_edit.jsp?prjid="+prjid+"&id="+id,
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
						 
						 let op="add_h" ;
						 if(id)
							 op = "edit_h";
						 
						 if(id)
						 {
							 ret.out_ids = $("#h_"+id).attr("out_ids") ;
							 ret.alert_uids = $("#h_"+id).attr("alert_uids") ;	 
						 }
						 
						 ret.prjid=prjid;
						 ret.id = id ;
						 var pm = {
									type : 'post',
									url : "./prj_alert_ajax.jsp",
									data :{op:op,prjid:prjid,jstr:JSON.stringify(ret)}
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								update_handlers();
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

function del_out(prjid,id)
{
	event.stopPropagation();
	dlg.confirm('<wbt:g>del,this,output</wbt:g>?',{btn:["Yes","Cancel"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
		    {
					send_ajax("prj_alert_ajax.jsp",{op:"del_o",prjid:prjid,id:id},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
			    			return ;
			    		}
			    		//
						update_outs() ;
			    	}) ;
				});
}

function add_or_edit_o(prjid,id)
{
	event.stopPropagation();
	var tt = "<wbt:g>add,alert,output</wbt:g>";
	if(id)
	{
		tt = "<wbt:g>edit,alert,output</wbt:g>";
	}
	if(id==null)
		id = "" ;
	dlg.open("prj_alert_o_edit.jsp?prjid="+prjid+"&id="+id,
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
						 
						 let op="add_o" ;
						 if(id)
							 op = "edit_o";
						 ret.prjid=prjid;
						 ret.id = id ;
						 var pm = {
									type : 'post',
									url : "./prj_alert_ajax.jsp",
									data :{op:op,prjid:prjid,jstr:JSON.stringify(ret)}
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								update_outs();
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

function show_alert_his(prjid)
{
	event.stopPropagation();
	dlg.open("/prj_alert_his.jsp?prjid="+prjid,
			{title:"<wbt:g>alert,history</wbt:g>"},
			['<wbt:g>close</wbt:g>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

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
	in_can[0].width = in_can[0].width;
	out_can[0].width = out_can[0].width;
	$(".h_item").each(function(){
		redraw_conns_h(this) ;
	}) ;
}

function redraw_conns_h(handler)
{
	let h_y = $(handler).offset().top + $(handler).height()/2;
	//let h_x = $(handler).offset().left ;
	let out_ids_str = $(handler).attr("out_ids") ;
	let alert_uids_str = $(handler).attr("alert_uids") ;
	
	let out_ids = out_ids_str.split(',');
	let alert_uids = alert_uids_str.split(',');
	in_cxt.save();
	out_cxt.save();
	//in_cxt.
	if(handler==cur_handler)
	{
		in_cxt.lineWidth = 2;
		in_cxt.strokeStyle = "blue";
		out_cxt.lineWidth = 2;
		out_cxt.strokeStyle = "blue";
	}
			
	$(".chk_alert").each(function(){
		let me = $(this);
		let id = me.attr("id") ;
		let k = alert_uids.indexOf(id);
		if(k<0) return ;
		
		let y = me.offset().top+me.height()/2;
		
		in_cxt.beginPath() ;
		in_cxt.moveTo(0,y);
		in_cxt.lineTo(in_w,h_y);
		in_cxt.stroke() ;
		
	});
	
	$(".chk_out").each(function(){
		let me = $(this);
		let id = me.attr("id") ;
		let k = out_ids.indexOf(id);
		if(k<0) return ;
		
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



</script>

</body>
</html>