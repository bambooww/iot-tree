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
	width:40%;
	bottom:0px;
}

.mid
{
	position: absolute;
	top:0px;
	left:40%;
	border:0px solid;
	right:40%;
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


.chk_alert_c
{
	position:absolute;
	right:0px;
	margin-top:0px;
	visibility: hidden;
}

.ric_item
{
	position:absolute;
	left:3%;
	width:60%;
	height:60px;
	border:2px solid #27ba7d;
	margin-top: 7px;
	margin-bottom: 20px;
	z-index:10;
}

.ric_item .t
{
	position:absolute;
	font-size: 20px;
	top:5px;
	left:10px;
}

.ric_item .tpt
{
	position:absolute;
	font-size: 15px;
	bottom:1px;
	color:#aaa9a9;
	left:15px;
}

.ric_item .rt
{
	position:absolute;
	left:2px;
	bottom:2px;
	background-color:red;
}

.ric_item .oper
{
	position:absolute;
	font-size: 15px;
	right:10px;
	bottom:7px;
}

.right_j
{
	position:absolute;
	right:0%;
	width:30%;
	height:40px;
	border:1px solid;
	border-color:#929292;
	margin-top: 7px;
	z-index:10;
	margin-bottom: 20px;
}

.right_j .ricon
{
	position:absolute;
	right:0%;
	top:10px;
	border:0px solid;
	
}

.roa_item
{
	position:absolute;
	left:40%;
	width:60%;
	min-height:60px;
	border:2px solid #27ba7d;
	margin-top: 7px;
	z-index:10;
	margin-bottom: 10px;
}
.roa_item .n
{
	position:absolute;
	font-size: 18px;
	left:5px;
}
.roa_item .t
{
	font-size: 20px;
	top:18px;
	margin-left:0px;
	left:0px;
	border:0px solid;
	word-break:break-all;
	margin-bottom: 30px;
}
.roa_item .tp
{
	position:absolute;
	font-size: 25px;
	bottom:10px;
	left:10px;
}
.roa_item .tpt
{
	position:absolute;
	font-size: 15px;
	bottom:1px;
	left:5px;
}
.roa_item .rt
{
	position:absolute;
	left:2px;
	bottom:2px;
	background-color:red;
}
.roa_item .oper
{
	position:absolute;
	font-size: 15px;
	right:10px;
	bottom:5px;
}

.left_j
{
	position:absolute;
	left:0%;
	width:30%;
	height:40px;
	border:1px solid;
	border-color:#929292;
	margin-top: 7px;
	z-index:10;
	margin-bottom: 20px;
}


.left_j .licon
{
	position:absolute;
	left:0%;
	bottom:10px;
	border:0px solid;
}

.roa_in_chk
{
	visibility: hidden;
}
.ric_in_chk
{
	visibility: hidden;
}


.chk_out_c
{
visibility: hidden;
}


.conn_rt
{
position:absolute;cursor:pointer;
background-color: red ;
z-index:20;
}
.jout
{border:2px solid green;
}

.sel
{
	border:2px solid blue;
}

.bk_icon
{
	width:100%;height:100%;position: absolute;left:0px;top:0px;
	opacity: 0.1;z-index:0px;
}
</style>
<body marginwidth="0" marginheight="0">
<div class="left" onclick="on_ric_clk()">
 <blockquote class="layui-elem-quote "><wbt:g>inner,data,collation</wbt:g>
 <div style="position: absolute;right:10px;top:11px;width:50px;border:0px solid;height:35px;">
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue layui-btn-primary" onclick="add_ric_sel('<%=prjid %>')">+<wbt:g>add</wbt:g> </button>
 </div>
 </blockquote>
 <div id="in_list" class="list">
	
 </div>
</div>
<div class="mid" >
 <blockquote class="layui-elem-quote "><wbt:g>path</wbt:g>/<wbt:g>transfer</wbt:g>
 
</blockquote>
 <div id="path_list" class="list">
 	<div id="trans_btn" style="position: absolute;display: none;z-index:10;"></div>
 </div>
 <div id="conn_panel" class="list"></div>
</div>
<div class="right" onclick="on_roa_clk()">
 <blockquote class="layui-elem-quote "><wbt:g>outer,adapter</wbt:g>
 <div style="position: absolute;right:10px;top:11px;width:50px;border:0px solid;height:35px;">
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue layui-btn-primary" onclick="add_roa_sel('<%=prjid %>')">+<wbt:g>add</wbt:g> </button>
 </div>
</blockquote>
 <div id="out_list" class="list" >
 </div>
</div>

<script>
var prjid = "<%=prjid%>" ;

var cur_out = null ;

var cur_ric = null ;
var cur_ric_jout = null ;
var cur_roa_jout = null ;

var full = null ;

function on_roa_clk(item)
{
	if(event)
		event.stopPropagation();
	$(".roa_item").removeClass("sel") ;
	//$(item).addClass("sel") ;
	$(".left_j").removeClass("sel") ;
	
	cur_roa_jout = null ;
	cur_ric_jout = null ;
	
	update_chks();
	redraw_conns();
}

function on_ric_clk(item)
{
	if(event)
		event.stopPropagation();
	$(".ric_item").removeClass("sel") ;
	$(".right_j").removeClass("sel") ;
	
	cur_roa_jout = null ;
	cur_ric_jout = null ;
	
	update_chks();
	redraw_conns();
}

function on_ric_out_join_clk(item)
{
	if(event)
		event.stopPropagation();
	cur_ric_jout=item ;
	cur_roa_jout = null ;
	$(".left_j").removeClass("sel") ;
	$(".right_j").removeClass("sel") ;
	if(item)
		$(item).addClass("sel") ;
	
	update_chks();
	redraw_conns();
}

function on_roa_out_join_clk(item)
{
	if(event)
		event.stopPropagation();
	cur_roa_jout=item ;
	cur_ric_jout = null ;
	$(".left_j").removeClass("sel") ;
	$(".right_j").removeClass("sel") ;
	if(item)
		$(item).addClass("sel") ;
	
	update_chks();
	redraw_conns();
}

function update_chks()
{
	if(!cur_ric_jout)
	{
		$(".roa_in_chk").css("visibility","hidden") ;
	}
	else
	{
		$(".roa_in_chk").css("visibility","visible") ;
		let fid = $(cur_ric_jout).attr("fid") ;
		$(".roa_in_chk").each(function(){
			let tid = $(this).attr("tid") ;
			let conn = get_ric_conn(fid,tid);
			$(this).prop("checked",conn!=null);
		});
	}
	
	if(!cur_roa_jout)
	{
		$(".ric_in_chk").css("visibility","hidden") ;
	}
	else
	{
		$(".ric_in_chk").css("visibility","visible") ;
		let fid = $(cur_roa_jout).attr("fid") ;
		$(".ric_in_chk").each(function(){
			let tid = $(this).attr("tid") ;
			let conn = get_roa_conn(fid,tid);
			$(this).prop("checked",conn!=null);
		});
	}
}

function get_ric_conn(fid,tid)
{
	if(!full) return null ;
	for(let conn of full.ric_conns)
	{
		if(conn.fid==fid && conn.tid==tid)
			return conn ;
	}
	return null ;
}

function get_roa_conn(fid,tid)
{
	if(!full) return null ;
	for(let conn of full.roa_conns)
	{
		if(conn.fid==fid && conn.tid==tid)
			return conn ;
	}
	return null ;
}

function update_full()
{
	send_ajax("router_ajax.jsp",{op:"list_full",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let os = null ;
		eval("os="+ret) ;

		full = os ;
		show_full();
	}) ;
}

function show_full()
{
	show_rics() ;
	show_roas() ;
	
	redraw_conns() ;
	//show_ric_conns() ;
	//show_roa_conns() ;
}

function update_rics()
{
	send_ajax("router_ajax.jsp",{op:"list_ric",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let os = null ;
		eval("os="+ret) ;
		full.ric_list = os ;
		show_rics() ;
	}) ;
}


function update_roas(end_cb)
{
	send_ajax("router_ajax.jsp",{op:"list_roa",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let os = null ;
		eval("os="+ret) ;
		full.roa_list = os ;
		show_roas() ;
	}) ;
}


function show_rics()
{
	let in_list = $("#in_list") ;
	//let w = in_list[0].offsetWidth ;
	//let h = in_list[0].offsetHeight ;
	//console.log(w,h)
	let tmps='';// `<canvas id="in_can" style="position: absolute;top:0px;left:0px;width:\${w}px;height:\${h}px;border:1px solid red;"></canvas>` ;
	
	let pos = 0 ;
	let ypos = 5 ;
	
	for(let ob of full.ric_list)
	{
		//console.log(rics) ;
		pos = ypos ;

		if(ob.out_joins)
		{
			for(let outj of ob.out_joins)
			{
				tmps += `<div id="ric_out_\${ob.id}-\${outj.n}" ric_id="\${ob.id}" fid="\${ob.id}-\${outj.n}" class="right_j jout" style="top:\${ypos}px;cursor:pointer;" j_n="\${outj.n}" t="\${ob.t}" onclick="on_ric_out_join_clk(this)" ">
					<span class="t">\${outj.t||outj.n}</span>
					<span class="ricon" ><i class="fa-solid fa-right-long"></i></span>
					<span class="oper">
						<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" onclick="debug_join_data(true,'\${ob.id}','\${outj.n}',true)"><i class="fa fa-j"></i></button>
					 </span>
					</div>`;
				ypos += 60 ;
			}
		}
		
		if(ob.in_joins)
		{
			for(let inj of ob.in_joins)
			{
				tmps += `<div id="ric_in_\${ob.id}-\${inj.n}" ric_id="\${ob.id}" class="right_j" style="top:\${ypos}px" j_n="\${inj.n}" t="\${ob.t}">
					<span class="t">\${inj.t||inj.n}</span>
					<span class="ricon" ><input class="ric_in_chk" type="checkbox" id="ric_in_chk_\${ob.id}-\${inj.n}" tid="\${ob.id}-\${inj.n}" onclick="on_ric_in_chk(this)"/><i class="fa-solid fa-left-long"></i></span>
					<span class="oper">
						<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" onclick="debug_join_data(true,'\${ob.id}','\${inj.n}',false)"><i class="fa fa-j"></i></button>
					 </span>
					</div>`;
				ypos += 60 ;
			}
			
			
		}
		
		let bsys = ob.n.startsWith("_") ;
		let oper = "" ;
		//
		oper="";
		
		if(!bsys)
		{
			oper +=`
				<button type="button" class="layui-btn layui-btn-xs " onclick="ric_debug_trigger_data('\${ob.id}')" title="debug trigger data out"><i class="fa fa-asterisk"></i></button>
				<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_ric('\${prjid}','\${ob._tp}','\${ob._tpt}','\${ob.id}')"><i class="fa fa-pencil"></i></button>
				<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_ric('\${prjid}','\${ob.id}')" title="delete"><i class="fa-regular fa-rectangle-xmark"></i></button>
				`;
		}
		
		if(ypos>pos)
			pos = (pos+ypos) / 2 - 40 ;
		else
			pos = ypos + 10 ;
		
		let brd = ob.en?"":"border-color:#aaaaaa"
		tmps += `<div id="ric_\${ob.id}" class="ric_item" style="top:\${pos}px;\${brd}" ricid="\${ob.id}" t="\${ob.t}" onclick="on_ric_clk(this)" out_ids="\${ob.out_ids}" alert_uids="\${ob.alert_uids}">
			<span class="t">\${ob.t}</span>
			<span class="tpt" >\${ob._tpt}</span>
			<span class="rt" id="ric_rt_\${ob.id}"></span>
			<span class="oper">
			 	\${oper}
			 </span>
			</div>` ;

	}
	
	in_list.html(tmps) ;
	draw_ric_node_joins();
	
	redraw_conns();
}

function draw_ric_node_joins()
{
	let in_list = $("#in_list") ;
	let can = in_list.find("#in_can") ;
	let cxt ;
	if(!can || can.length==0)
	{
		cxt = document.createElement('canvas').getContext('2d');
		can = $(cxt.canvas);
		can.attr("id","in_can") ;
		can.css("position", "relative");
		can.css("left", "0%");
		can.css("top", "0px");
		can.css("display","");
		can.css("z-index","0");
		in_list.append(can);
		can.attr('width', in_list[0].offsetWidth) ;
		can.attr('height', in_list[0].offsetHeight+200) ;
		in_list.resize(()=>{
			let w = in_list[0].offsetWidth;
			let h = in_list[0].offsetHeight+200;
			//console.log(w,h)
			can.attr('width',w) ;
			can.attr('height', h) ;
			draw_ric_node_joins();
		});
	}
	else
	{
		cxt = can[0].getContext('2d');
	}

	$(".ric_item").each(function(){
		let ric = $(this) ;
		let ric_id = ric.attr("ricid") ;
		let rx = ric.offset().left +ric.width();
		let ry = ric.offset().top-50 +ric.height()/2;
		let min_y=null,max_y=null ;
		let mx =null;
		$("[ric_id='"+ric_id+"']").each(function(){
			let job = $(this) ;
			
			let x = job.offset().left;
			let h = job.height() ;
			let y = job.offset().top-50+h/2 ;
			if(min_y==null||min_y>y) min_y = y ;
			if(max_y==null||max_y<y) max_y = y ;
			mx = (rx+x)/2 ; 
			cxt.beginPath();
			cxt.moveTo(mx,y);
			cxt.lineTo(x,y);
			cxt.stroke() ;
		});
		
		if(mx)
		{
			cxt.beginPath();
			cxt.moveTo(rx,ry);
			cxt.lineTo(mx,ry);
			cxt.stroke() ;
		}
		
		if(mx!=null && min_y!=null && max_y!=null)
		{
			cxt.beginPath();
			cxt.moveTo(mx,min_y);
			cxt.lineTo(mx,max_y);
			cxt.stroke() ;
		}
	}) ;
}


function show_roas()
{
	let tmps="";
	
	let pos = 0 ;
	let ypos = 5 ;
	
	for(let ob of full.roa_list)
	{
		//console.log(ob) ;
		pos = ypos ;

		if(ob.out_joins)
		{
			for(let outj of ob.out_joins)
			{
				tmps += `<div id="roa_out_\${ob.id}-\${outj.n}" roa_id="\${ob.id}" class="left_j jout" fid="\${ob.id}-\${outj.n}" style="top:\${ypos}px;cursor:pointer;" j_n="\${outj.n}" t="\${ob.t}" onclick="on_roa_out_join_clk(this)">
					<span class="t">\${outj.t||outj.n}</span>
					<span class="licon" ><i class="fa-solid fa-left-long"></i></span>
					<span class="oper">
					<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" onclick="debug_join_data(false,'\${ob.id}','\${outj.n}',true)"><i class="fa fa-j"></i></button>
					 </span>
					</div>`;
				ypos += 60 ;
			}
		}
		
		if(ob.in_joins)
		{
			for(let inj of ob.in_joins)
			{
				tmps += `<div id="roa_in_\${ob.id}-\${inj.n}" roa_id="\${ob.id}"class="left_j" style="top:\${ypos}px" j_n="\${inj.n}" t="\${ob.t}" >
					<span class="t">\${inj.t||inj.n}</span>
					<span class="licon" ><i class="fa-solid fa-right-long"></i><input class="roa_in_chk" type="checkbox" id="roa_in_chk_\${ob.id}-\${inj.n}" tid="\${ob.id}-\${inj.n}" onclick="on_roa_in_chk(this)"/></span>
					<span class="oper">
					<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" onclick="debug_join_data(false,'\${ob.id}','\${inj.n}',false)"><i class="fa fa-j"></i></button>
					 </span>
					</div>`;
				ypos += 60 ;
			}
			
			
		}
		
		let oper = "" ;
		
		oper="";
		
			oper +=`
				<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_roa('\${prjid}','\${ob._tp}','\${ob._tpt}','\${ob.id}')"><i class="fa fa-pencil"></i></button>
				<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_roa('\${prjid}','\${ob.id}')" title="delete"><i class="fa-regular fa-rectangle-xmark"></i></button>
				`;
		
		if(ypos>pos)
			pos = (pos+ypos) / 2 - 40 ;
		else
		{
			pos = ypos + 10 ;
			ypos = pos+80 ;
		}
			
		let brd = ob.en?"":"border-color:#aaaaaa"
		tmps += `<div id="o_\${ob.id}" class="roa_item" roaid="\${ob.id}" style="top:\${pos}px;\${brd}" h_id="\${ob.id}" t="\${ob.t}" onclick="on_roa_clk(this)" out_ids="\${ob.out_ids}">
			<span class="t">\${ob.t||ob.n}</span>
			<div class="bk_icon" style="background:url(./roa/\${ob._tp}.png) no-repeat"></div>
			<span class="rt" id="roa_rt_\${ob.id}"></span>
			<span class="oper">
			 	\${oper}
			 </span>
			</div>` ;

	}
	$("#out_list").html(tmps) ;
	draw_roa_node_joins();
	redraw_conns();
}


function draw_roa_node_joins()
{
	let out_list = $("#out_list") ;
	let can = out_list.find("#out_can") ;
	let cxt ;
	if(!can || can.length==0)
	{
		cxt = document.createElement('canvas').getContext('2d');
		can = $(cxt.canvas);
		can.attr("id","out_can") ;
		can.css("position", "relative");
		can.css("left", "0%");
		can.css("top", "0px");
		can.css("display","");
		can.css("z-index","0");
		out_list.append(can);
		can.attr('width', out_list[0].offsetWidth) ;
		can.attr('height', out_list[0].offsetHeight+200) ;
		out_list.resize(()=>{
			let w = out_list[0].offsetWidth;
			let h = out_list[0].offsetHeight+200;
			//console.log(w,h)
			can.attr('width',w) ;
			can.attr('height', h) ;
			draw_roa_node_joins();
		});
	}
	else
	{
		cxt = can[0].getContext('2d');
	}
	
	
	
	//var out_cxt = $("#out_can")[0].getContext('2d');
	
	$(".roa_item").each(function(){
		let roa = $(this) ;
		let roa_id = roa.attr("roaid") ;
		let rx = roa.offset().left-out_list.offset().left;;
		let ry = roa.offset().top-50 +roa.height()/2;
		let min_y=null,max_y=null ;
		let mx =null;
		$("[roa_id='"+roa_id+"']").each(function(){
			let job = $(this) ;
			
			let x = job.offset().left+job.width()-out_list.offset().left;
			let h = job.height() ;
			let y = job.offset().top-50+h/2 ;
			//console.log(x,y);
			if(min_y==null||min_y>y) min_y = y ;
			if(max_y==null||max_y<y) max_y = y ;
			mx = (rx+x)/2 ; 
			cxt.beginPath();
			cxt.moveTo(mx,y);
			cxt.lineTo(x,y);
			cxt.stroke() ;
		});
		
		if(mx)
		{
			cxt.beginPath();
			cxt.moveTo(rx,ry);
			cxt.lineTo(mx,ry);
			cxt.stroke() ;
		}
		
		if(mx!=null && min_y!=null && max_y!=null)
		{
			cxt.beginPath();
			cxt.moveTo(mx,min_y);
			cxt.lineTo(mx,max_y);
			cxt.stroke() ;
		}
	}) ;
	
	
}


function del_ric(prjid,id)
{
	event.stopPropagation();
	dlg.confirm('<wbt:g>del,this,item</wbt:g>?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
		    {
					send_ajax("router_ajax.jsp",{op:"del_ric",prjid:prjid,id:id},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
			    			return ;
			    		}
			    		//
			    		update_rics();
			    	}) ;
				});
}


function add_ric_sel(prjid)
{
	if(event)
		event.stopPropagation();
	dlg.open("./router_ric_sel.jsp",
			{title:"<wbt:g>select,inner,collator</wbt:g>"},
			['<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			],(ret)=>{
				if(!ret)
					return ;
				add_or_edit_ric(prjid,ret.tp,ret.tt,null) ;
			});
}

function add_or_edit_ric(prjid,tp,tptt,id)
{
	if(event)
		event.stopPropagation();
	var tt = "<wbt:g>add,inner,collator</wbt:g> ["+tptt+"]";
	if(id)
	{
		tt = "<wbt:g>edit,inner,collator</wbt:g> ["+tptt+"]";
	}
	if(id==null)
		id = "" ;
	dlg.open("./ric/"+tp+"_coll_edit.jsp?prjid="+prjid+"&id="+id,
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
						 
						 let op="add_ric" ;
						 if(id)
							 op = "edit_ric";
						 ret.prjid=prjid;
						 ret._tp = tp ;
						 ret.id = id ;
						 var pm = {
									type : 'post',
									url : "./router_ajax.jsp",
									data :{op:op,prjid:prjid,jstr:JSON.stringify(ret)}
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								update_rics();
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


function on_ric_in_chk(chkele)
{
	if(event)
		event.stopPropagation();
	
	let inpchk = $(chkele);
	let tid = inpchk.attr("tid") ;
	if(!cur_roa_jout)
		return ;
	let bchk = inpchk.prop("checked") ;
	let op = bchk?"roa_set_conn":"roa_del_conn" ;
	let fid = $(cur_roa_jout).attr("fid") ;
	let pm= {op:op,prjid:prjid,fid:fid,tid:tid} ;
	send_ajax("router_ajax.jsp",pm,(bsucc,ret)=>{
		if(!bsucc || ret!='succ')
		{
			dlg.msg(ret) ;
			return;
		}
		
		update_roa_conns();
	}) ;
}

function update_roa_conns()
{
	send_ajax("router_ajax.jsp",{op:"list_roa_conns",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		
		let os = null ;
		eval("os="+ret) ;
		full.roa_conns = os ;
		//console.log(os);
		redraw_conns();
	}) ;
	
}

function del_roa(prjid,id)
{
	event.stopPropagation();
	dlg.confirm('<wbt:g>del,this,item</wbt:g>?',{btn:["Yes","Cancel"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
		    {
					send_ajax("router_ajax.jsp",{op:"del_roa",prjid:prjid,id:id},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
			    			return ;
			    		}
			    		//
			    		update_roas();
			    	}) ;
				});
}

function add_roa_sel(prjid)
{
	if(event)
		event.stopPropagation();
	dlg.open("./router_roa_sel.jsp",
			{title:"<wbt:g>select,outer,adapter,type</wbt:g>"},
			['<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			],(ret)=>{
				if(!ret)
					return ;
				add_or_edit_roa(prjid,ret.tp,ret.tt,null) ;
			});
}


function add_or_edit_roa(prjid,tp,tptt,id)
{
	if(event)
		event.stopPropagation();
	var tt = "<wbt:g>add,output,adapter</wbt:g> ["+tptt+"]";
	if(id)
	{
		tt = "<wbt:g>edit,output,adapter</wbt:g> ["+tptt+"]";
	}
	if(id==null)
		id = "" ;
	dlg.open("./roa/"+tp+"_adp_edit.jsp?prjid="+prjid+"&id="+id,
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
						 
						 let op="add_roa" ;
						 if(id)
							 op = "edit_roa";
						 ret.prjid=prjid;
						 ret.id = id ;
						 var pm = {
									type : 'post',
									url : "./router_ajax.jsp",
									data :{op:op,prjid:prjid,jstr:JSON.stringify(ret)}
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								update_roas();
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


function on_roa_in_chk(chkele)
{
	if(event)
		event.stopPropagation();
	
	let inpchk = $(chkele);
	let tid = inpchk.attr("tid") ;
	if(!cur_ric_jout)
		return ;
	let bchk = inpchk.prop("checked") ;
	let op = bchk?"ric_set_conn":"ric_del_conn" ;
	let fid = $(cur_ric_jout).attr("fid") ;
	let pm= {op:op,prjid:prjid,fid:fid,tid:tid} ;
	send_ajax("router_ajax.jsp",pm,(bsucc,ret)=>{
		if(!bsucc || ret!='succ')
		{
			dlg.msg(ret) ;
			return;
		}
		
		update_ric_conns();
	}) ;
}

function update_ric_conns()
{
	send_ajax("router_ajax.jsp",{op:"list_ric_conns",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		
		let os = null ;
		eval("os="+ret) ;
		full.ric_conns = os ;
		//console.log(os);
		redraw_conns();
	}) ;
	
}

var path_panel = null ;
var path_can = null ;
var path_cxt = null ;
var path_w = null ;


function conn_init()
{
	path_cxt = document.createElement('canvas').getContext('2d');
	let can = $(path_cxt.canvas);
	path_can = can ;
	can.css("position", "relative");
	can.css("left", "0px");
	can.css("top", "0px");
	can.css("display","");
	can.css("z-index","2");
	path_panel= $("#path_list");
	can.attr('width', path_panel[0].offsetWidth) ;
	can.attr('height', path_panel[0].offsetHeight-5) ;
	path_panel.append(can);
	path_w = path_panel[0].offsetWidth;
	path_panel.resize(()=>{
		path_w = path_panel[0].offsetWidth;
		var h = path_panel[0].offsetHeight;
		//console.log(w,h)
		can.attr('width',path_w) ;
		can.attr('height', h) ;
		redraw_conns();
	});
}


function redraw_conns()
{
	path_can[0].width = path_can[0].width;
	$("#trans_btn").html("");
	$("#conn_panel").html("") ;
	show_ric_conns();
	show_roa_conns();
}

function show_ric_conns()
{
	
	for(let conn of full.ric_conns)
	{
		show_ric_conn(conn) ;
	}
}

function show_roa_conns()
{
	//$("#conn_panel").html("") ;
	for(let conn of full.roa_conns)
	{
		show_roa_conn(conn) ;
	}
}

function show_ric_conn(conn)
{
	let fid = conn.fid ;
	let tid = conn.tid ;
	let ric_ele = document.getElementById('ric_out_'+fid) ;
	let roa_ele = document.getElementById('roa_in_'+tid) ;
	if(!ric_ele || !roa_ele)
		return ;
	let ric = $(ric_ele) ;
	let roa = $(roa_ele) ;
	let fy = ric.offset().top+ ric.height()/2 - 50;
	let ty = roa.offset().top+ roa.height()/2 - 50;

	path_cxt.save();
	
	path_cxt.lineWidth = 2;
	path_cxt.strokeStyle = "green";
	if(ric_ele==cur_ric_jout)
	{
		path_cxt.strokeStyle = "blue";
	}
	path_cxt.beginPath() ;
	path_cxt.moveTo(0,fy);
	path_cxt.lineTo(path_w,ty);
	path_cxt.stroke() ;
	
	let cx = path_w/2  ;
	let cy = (fy+ty)/2 ;
	
	if(conn.has_js && conn.en_js)
		path_cxt.strokeText('T',cx,cy) ;
	path_cxt.restore();
	
	if(ric_ele==cur_ric_jout)
	{//show T
		
		//console.log(cx,cy) ;
		let tmps = `<button style='width:20px;height:20px;color:blue;cursor:pointer' onclick="edit_trans_js('ric','\${fid}','\${tid}')"><i class="fa fa-pencil"></i></button>` ;
		$("#trans_btn").html(tmps) ;
		$("#trans_btn").css("display","block");
		$("#trans_btn").css("top",(cy-15)+"px").css("left",(cx-5)+"px") ;
	}
	else
	{
		//$("#trans_btn").html("");//css("display","none");
	}
	
	let t = (cy-15+20);
	let l = cx-5 ;
	let tmps = `<div class="conn_rt" style='top:\${t}px;left:\${l}px;' id="rt_\${conn.key}"></div>` ;
	$("#conn_panel").append(tmps) ;
}

function edit_trans_js(node_tp,fid,tid)
{
	if(event)
		event.stopPropagation();
	dlg.open("./router_edit_trans_js.jsp?prjid="+prjid+"&nodetp="+node_tp+"&fid="+fid+"&tid="+tid,
			{title:'<wbt:g>edit,js</wbt:g>'},
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
						 ret.prjid = prjid ;
						 ret.op = node_tp+"_set_trans_js";
						 ret.fid = fid ;
						 ret.tid = tid ;
						 send_ajax("router_ajax.jsp",ret,(buscc,ret)=>{
							 if(!bsucc || ret!='succ')
							{
								 dlg.msg(ret) ;
								 return;
							}
							if(node_tp=='ric')
								update_ric_conns()
							else
								update_roa_conns()
							dlg.close() ; 
						}) ;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function edit_roa_trans_js(fid,tid)
{
	
}

function show_roa_conn(conn)
{
	let fid = conn.fid ;
	let tid = conn.tid ;
	//console.log(conn)
	let roa_ele = document.getElementById('roa_out_'+fid) ;
	let ric_ele = document.getElementById('ric_in_'+tid) ;
	if(!ric_ele || !roa_ele)
		return ;
	let ric = $(ric_ele) ;
	let roa = $(roa_ele) ;
	let fy = ric.offset().top+ ric.height()/2-50;
	let ty = roa.offset().top+ roa.height()/2-50;

	path_cxt.save();
	path_cxt.lineWidth = 2;
	path_cxt.strokeStyle = "green";
	if(roa_ele==cur_roa_jout)
	{
		path_cxt.strokeStyle = "blue";
	}
	//console.log(fy,ty,path_w) ;
	path_cxt.beginPath() ;
	path_cxt.moveTo(0,fy);
	path_cxt.lineTo(path_w,ty);
	path_cxt.stroke() ;
	
	let cx = path_w/2  ;
	let cy = (fy+ty)/2 ;
	
	if(conn.has_js)
		path_cxt.strokeText('T',cx,cy) ;
	
	path_cxt.restore();
	
	if(roa_ele==cur_roa_jout)
	{//show T
		let tmps = `<button style='width:20px;height:20px;color:blue;cursor:pointer;' onclick="edit_trans_js('roa','\${fid}','\${tid}')"><i class="fa fa-pencil"></i></button>` ;
		$("#trans_btn").html(tmps) ;
		$("#trans_btn").css("display","block");
		$("#trans_btn").css("top",(cy-15)+"px").css("left",(cx-5)+"px") ;
	}
	else
	{
		//css("display","none");
	}
	let t = cy-15+20 ;
	let l = cx-5 ;
	let tmps = `<div class="conn_rt" style='top:\${t}px;left:\${l}px;' id="rt_\${conn.key}"></div>` ;
	$("#conn_panel").append(tmps) ;
}

$("#in_list").scroll(function() {
	redraw_conns()
});
$("#out_list").scroll(function() {
	redraw_conns()
});

conn_init();
update_full();


function ric_debug_trigger_data(id)
{
	send_ajax("router_ajax.jsp",{op:"ric_debug_trigger_data",prjid:prjid,id:id},(bsucc,ret)=>{
		dlg.msg(ret) ;
	}) ;
}

function debug_join_data(ric_or_roa,ric_roa_id,n,b_out)
{
	let op = "ric_debug_join_data";
	if(!ric_or_roa)
		op = "roa_debug_join_data";
	
	send_ajax("router_ajax.jsp",{op:op,prjid:prjid,id:ric_roa_id,name:n,out:b_out},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let pm;
		eval("pm="+ret) ;
		console.log(pm) ;
		dlg.open("./router_debug_shower.jsp",
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

function rt_update()
{
	send_ajax("router_ajax.jsp",{op:"rt_inf",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("{")!=0)
		{
			console.log(ret) ;
			return ;
		}
		let ob = null ;
		eval("ob="+ret) ;
		//console.log(ob) ;
		rt_update_in(ob)
	}) ;
}
	
function rt_update_in(ob)
{
	if(ob.rics)
	{
		for(let ric of ob.rics)
		{
			let tmps = "" ;
			if(ric.rt_err_dt>0)
			{
				let dt = new Date(ric.rt_err_dt).format("yyyy-MM-dd hh:mm:ss")
				tmps = `<span title="\${dt}-\${ric.rt_last_err}">X</span>`;
			}
			
			$("#ric_rt_"+ric.id).html(tmps) ;
		}
	}
	
	if(ob.roas)
	{
		for(let ric of ob.roas)
		{
			let tmps = "" ;
			if(ric.rt_err_dt>0)
			{
				let dt = new Date(ric.rt_err_dt).format("yyyy-MM-dd hh:mm:ss")
				tmps = `<span title="\${dt}-\${ric.rt_last_err}">X</span>`;
			}
			$("#roa_rt_"+ric.id).html(tmps) ;
		}
	}
	
	if(ob.ric2roa_conns)
	{
		for(let cc of ob.ric2roa_conns)
		{
			let tmps = "" ;
			if(cc.rt_err_dt>0)
			{
				let dt = new Date(cc.rt_err_dt).format("yyyy-MM-dd hh:mm:ss")
				tmps = `<span title="\${dt}-\${cc.rt_last_err}">X</span>`;
			}
			let ele = document.getElementById("rt_"+cc.key);
			$(ele).html(tmps) ;
		}
	}
	
	if(ob.roa2ric_conns)
	{
		for(let cc of ob.roa2ric_conns)
		{
			let tmps = "" ;
			if(cc.rt_err_dt>0)
			{
				let dt = new Date(cc.rt_err_dt).format("yyyy-MM-dd hh:mm:ss")
				tmps = `<span title="\${dt}-\${cc.rt_last_err}">X</span>`;
			}
			let ele = document.getElementById("rt_"+cc.key);
			$(ele).html(tmps) ;
		}
	}
}

setInterval(rt_update,3000) ;
</script>

</body>
</html>