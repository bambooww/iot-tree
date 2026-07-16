<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page import="java.util.*,
				java.io.*,
				java.util.*,
				org.json.*,
				org.iottree.core.*,
				org.iottree.core.util.*,org.iottree.core.devtree.*,
				java.net.*"%><%!
				
%><%
	if(!Convert.checkReqEmpty(request, out,"treeid","tree_nid","ins_name"))
		return ;
String treeid = request.getParameter("treeid");
String tree_nid = request.getParameter("tree_nid");
String ins_name = request.getParameter("ins_name") ;
DTTree tree = DTTreeManager.getInstance().getTreeById(treeid) ;
	if(tree==null)
	{
		out.print("no tree found with id="+treeid) ;
		return ;
	}
	DTNode dn = tree.findNodeById(tree_nid) ;
	if(dn==null)
	{
		out.print("no tree node found") ;
		return ;
	}
	
	DTRunBlkIns runblk_ins = dn.getRunBlkIns(ins_name) ;
	if(runblk_ins==null)
	{
		out.print("no RunBlk Instance found") ;
		return ;
	}
	//JSONObject blk_jo = blk.toJO(true) ;
%><html>
<head>
<title></title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
</head>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
-moz-user-select : none;
-webkit-user-select: none;
}

.layui-elem-quote {
    padding: 5px;
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
	width:25%;
	bottom:0px;
}

.mid
{
	position: absolute;
	top:0px;
	left:25%;
	border:0px solid;
	right:25%;
	bottom:0px;
	margin-right:0px;
}

.right
{
	position: absolute;
	top:0px;
	right:0px;
	border:0px solid;
	width:25%;
	bottom:0px;
}

.list
{
	position0: absolute;position:relative;
	display:block;
	top:10px;
	bottom:0px;
	width:100%;
	border:0px solid;
	border-color:red;
	scrollbar-width: none; /* firefox */
  -ms-overflow-style: none; /* IE 10+ */
  overflow-x: hidden;
  overflow-y: hidden;
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


.oper_l
{
	position:absolute;
	font-size: 15px;
	left:10px;
	bottom:1px;
}

.oper_r
{
	position:absolute;
	font-size0: 15px;
	right:10px;
	bottom:1px;
}


.def_left
{
	position:absolute;
	left:0%;
	width:40%;
	height:20px;
	border:1px solid;
	border-color:#929292;
	margin-top: 7px;
	z-index:10;
	margin-bottom: 20px;
	text-overflow: ellipsis;
	overflow: hidden;
	white-space: nowrap;
}

.def_left:hover {background-color: #cccccc;}

.def_left .t {padding-left:15px; text-overflow: ellipsis;}

.def_left .icon
{
	position:absolute;
	left:0%;
	top:2px;
	border:0px solid;
	color:#ccc;
}

.real_left
{
	position:absolute;
	left:0%;
	width:80%;
	height:20px;
	border:1px solid;
	border-color:#929292;
	margin-top: 7px;
	z-index:10;overflow:hidden;
	margin-bottom: 20px;
}

.real_left .op {position:absolute;
	left:0%;
	border:0px solid;}

.real_left:hover {background-color: #cccccc;}
.real_left .t
{
padding-left:18px;white-space: nowrap;
}

.real_left .icon
{
	position:absolute;
	right:0%;
	top:2px;
	border:0px solid;
	color:#ccc;
}

.def_right
{
	position:absolute;
	right:0%;
	width:40%;
	height:20px;
	border:1px solid;
	border-color:#929292;
	margin-top: 7px;
	z-index:10;
	text-align:right;
	text-overflow: ellipsis;
	margin-bottom: 20px;
	white-space: nowrap;
}

.def_right:hover {background-color: #cccccc;}

.def_right .t {padding-right:15px;}

.def_right .icon
{
	position:absolute;
	right:0%;
	top:2px;
	border:0px solid;
	color:#ccc;
}

.real_right
{
	position:absolute;
	right:0%;
	width:80%;
	height:20px;
	border:1px solid;
	border-color:#929292;
	margin-top: 7px;
	z-index:10;
	margin-bottom: 20px;
	white-space: nowrap;
}

.real_right:hover {background-color: #cccccc;}

.real_right .t {padding-left:15px;}
.real_right .op {position:absolute;
	right:0%;
	border:0px solid;}
.real_right .icon
{
	position:absolute;
	left:0%;
	top:2px;
	border:0px solid;
	color:#ccc;
}

.real_right .rt
{
	position:absolute;
	left:0%;
	top:2px;
	border:0px solid;
	color:red;
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
	border:2px solid blue;background-color: #cccccc;
}

.bk_icon
{
	width:100%;height:100%;position: absolute;left:0px;top:0px;
	opacity: 0.1;z-index:0px;
}

.blk_mid
{
	border:2px solid #245176;
	margin-left:0px;margin-right:5px;border-radius: 5px;
}

.blk_in_top{font-weight:bold;background-color:#aaaaaa;height:20px;width:100%}
.blk_in_btm{position:absolute;font-size0:20px;bottom:0px;width:100%;background-color:#aaaaaa;"}
</style>
<body marginwidth="0" marginheight="0" style="overflow: hidden;">
<div style="border:0px solid red;position: relative;heigth:35px;">
	<div class="left" onclick="">
	 <blockquote class="layui-elem-quote ">&nbsp;实际参数<button style="visibility: hidden;">&nbsp;</button>
	 <div style="position: absolute;right:10px;top:11px;width:50px;border:0px solid;height:35px;">
	 </div>
	 </blockquote>
	 <%--<div id="run_blk_left" class="list" ></div> --%>
	</div>
	<div class="mid" >
	 <blockquote class="layui-elem-quote ">运行模块 <button onclick='location.reload()'>刷新</button></blockquote>
	 <%--<div id="run_blk_mid" class="list blk_mid" style="height:50px;"></div> --%>
	 </div>
	<div class="right" >
	 <blockquote class="layui-elem-quote ">&nbsp;实际参数<button style="visibility: hidden;">&nbsp;</button>
	 <div style="position: absolute;right:10px;top:11px;width:50px;border:0px solid;height:35px;">
	 </div>
	</blockquote>
	 <%--<div id="run_blk_right" class="list" ></div> --%>
	</div>
</div>
<div id="ccc" style="border:0px solid blue;left:3px;right:3px;position: relative;top:35px;height:100px;overflow-y:auto">
	<div>
		<div class="left" onclick="">
		 <div id="run_blk_left" class="list" ></div> 
		 
		</div>
		<div class="mid" >
		 <div id="run_blk_mid" class="list blk_mid" >
			
		 </div>
		 </div>
		<div class="right" >
		 <div id="run_blk_right" class="list" >
		 </div>
		</div>
	</div>
</div>
<script>
var treeid = "<%=treeid%>" ;
var tree_nid = "<%=tree_nid%>";
var ins_name = "<%=ins_name%>" ;

var cur_io = null ;
var runblk = null ;//
var cur_ioplug_def = null ;
var cur_ioprop_def = null ;
var cur_ioerrdef_def = null ;

function on_plug_def_clk(item)
{
	if(event)
		event.stopPropagation();
	$(".plug_def").removeClass("sel") ;
	$(item).addClass("sel") ;
	//console.log(item) ;
	cur_ioplug_def = $(item) ;
	show_nodes();
}

function on_prop_def_clk(item)
{
	if(event)
		event.stopPropagation();
	$(".prop_def").removeClass("sel") ;
	$(item).addClass("sel") ;
	
	cur_ioprop_def = $(item) ;
	show_nodes();
}

function on_errdef_def_clk(item)
{
	if(event)
		event.stopPropagation();
	$(".errdef_def").removeClass("sel") ;
	$(item).addClass("sel") ;
	
	cur_ioerrdef_def = $(item) ;
	show_nodes();
}

function on_plug_pm_set(plug,def_n,tt,set_in_runner)
{
	if(event) event.stopPropagation();
	if(!b_can_edit)
	{
		dlg.msg("这是继承运行块，请到对应的部件修改") ;
		return ;
	}
	if(!b_runner && set_in_runner==true)
	{
		dlg.msg("只能在具体的运行模块中定义");
		return ;
	}
	
	let editt = `设置模块IO - \${tt}` ;
	let u =`./tn_runblk_ioplug_pm_\${plug}.jsp?runner=\${b_runner}&cuid=\${cuid}&tree_nid=\${tree_nid}&runblk_id=\${runblk_id}&io_n=\${def_n}` ;
	op="edit_runblk_t_d";
	dlg.open(u,{title:editt,w:'500px',h:'400px',input:{}},
			['确定','删除设置','取消'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
		        	     {
							 dlg.msg(ret) ;
							 return ;
		        	     }
						 let pm={op:"set_io_pm",runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id,io_n:def_n,jstr:JSON.stringify(ret)};
						 
						 send_ajax("tn_runblk_ajax.jsp",pm,(bsucc,ret)=>{
							 if(!bsucc || ret.indexOf("succ")!=0)
							 {
								 dlg.msg(ret) ;
								 return ;
							 }
							 dlg.close() ;
							 update_ui()
						 }) ;
				 	});
				},
				function(dlgw)
				{
					let pm={op:"unset_io_pm",runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id,io_n:def_n};
					send_ajax("tn_runblk_ajax.jsp",pm,(bsucc,ret)=>{
						 if(!bsucc || ret.indexOf("succ")!=0)
						 {
							 dlg.msg(ret) ;
							 return ;
						 }
						 dlg.close() ;
						 update_ui()
					 }) ;
					
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function on_prop_pm_set(def_n,tt)
{
	if(event) event.stopPropagation();
	if(!b_can_edit)
	{
		dlg.msg("这是继承运行块，请到对应的部件修改") ;
		return ;
	}
	let editt = `设置模块IO - \${tt}` ;
	let u =`./tn_runblk_ioprop_pm.jsp?runner=\${b_runner}&cuid=\${cuid}&tree_nid=\${tree_nid}&runblk_id=\${runblk_id}&io_n=\${def_n}` ;
	op="edit_runblk_t_d";
	dlg.open(u,{title:editt,w:'500px',h:'400px',input:{}},
			['确定','删除设置','取消'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
		        	     {
							 dlg.msg(ret) ;
							 return ;
		        	     }
						 //console.log(ret) ;
						 let pm={op:"set_io_pm",runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id,io_n:def_n,jstr:JSON.stringify(ret)};
						 
						 send_ajax("tn_runblk_ajax.jsp",pm,(bsucc,ret)=>{
							 if(!bsucc || ret.indexOf("succ")!=0)
							 {
								 dlg.msg(ret) ;
								 return ;
							 }
							 dlg.close() ;
							 update_ui();
						 }) ;
				 	});
				},
				function(dlgw)
				{
					let pm={op:"unset_io_pm",runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id,io_n:def_n};
					 
					 send_ajax("tn_runblk_ajax.jsp",pm,(bsucc,ret)=>{
						 if(!bsucc || ret.indexOf("succ")!=0)
						 {
							 dlg.msg(ret) ;
							 return ;
						 }
						 dlg.close() ;
						 update_ui();
					 }) ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			],(close_ret)=>{
				if(close_ret)
				{
					update_ui() ;
				}
				
			});
}

function on_errdef_pm_set(def_n,tt)
{
	if(event) event.stopPropagation();
	if(!b_can_edit)
	{
		dlg.msg("这是继承运行块，请到对应的部件修改") ;
		return ;
	}
	let editt = `设置模块IO - 故障定义关联 - \${tt}` ;
	let u =`./tn_runblk_ioerrdef_pm.jsp?runner=\${b_runner}&cuid=\${cuid}&tree_nid=\${tree_nid}&runblk_id=\${runblk_id}&io_n=\${def_n}` ;
	dlg.open(u,{title:editt,w:'500px',h:'400px',input:{}},
			['确定','删除设置','取消'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
		        	     {
							 dlg.msg(ret) ;
							 return ;
		        	     }
						 //console.log(ret) ;
						 let pm={op:"set_io_pm",runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id,io_n:def_n,jstr:JSON.stringify(ret)};
						 
						 send_ajax("tn_runblk_ajax.jsp",pm,(bsucc,ret)=>{
							 if(!bsucc || ret.indexOf("succ")!=0)
							 {
								 dlg.msg(ret) ;
								 return ;
							 }
							 dlg.close() ;
							 update_ui();
						 }) ;
				 	});
				},
				function(dlgw)
				{
					let pm={op:"unset_io_pm",runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id,io_n:def_n};
					 
					 send_ajax("tn_runblk_ajax.jsp",pm,(bsucc,ret)=>{
						 if(!bsucc || ret.indexOf("succ")!=0)
						 {
							 dlg.msg(ret) ;
							 return ;
						 }
						 dlg.close() ;
						 update_ui();
					 }) ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			],(close_ret)=>{
				if(close_ret)
				{
					update_ui() ;
				}
				
			});
}

function show_blk_js0(tt,jstxt,b_edit)
{
	let btns = ["关闭"];
	let funcs =[function(dlgw){dlg.close();}];
	if(b_edit)
	{
		btns =['修改','取消'];
		funcs =[
			function(dlgw)
			{
				dlgw.do_submit(function(bsucc,ret){
					 if(!bsucc)
	        	     {
						 dlg.msg(ret) ;
						 return ;
	        	     }
					 let pm={op:"set_ins_js",runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id,jstr:JSON.stringify(ret)};
					 
					 send_ajax("tn_runblk_ajax.jsp",pm,(bsucc,ret)=>{
						 if(!bsucc || ret.indexOf("succ")!=0)
						 {
							 dlg.msg(ret) ;
							 return ;
						 }
						 dlg.close() ;
						 show_nodes() ;
					 }) ;
			 	});
			},
			function(dlgw){dlg.close();}
			];
	}
	let u =`./tn_runblk_js.jsp?cuid=\${cuid}&runner=\${b_runner}&tree_nid=\${tree_nid}&runblk_id=\${runblk_id}` ;
	dlg.open(u,{title:tt,w:'500px',h:'400px',js_txt:jstxt,pm_objs:{}},
			btns,funcs);
}

function show_blk_js(tt,jstxt,op)
{
	let btns =[];
	let funcs=[] ;
	if(op)
	{
		btns.push('修改')
		funcs.push(function(dlgw)
		{
			let editjs = dlgw.get_edited_js();
			//console.log(editjs)
			if(op=="set_tp_js")
			{
				
			}
				 let pm={op:op,runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id,jstxt:editjs};
				 
				 send_ajax("tn_runblk_ajax.jsp",pm,(bsucc,ret)=>{
					 if(!bsucc || ret.indexOf("succ")!=0)
					 {
						 dlg.msg(ret) ;
						 return ;
					 }
					 dlg.close() ;
					 show_nodes() ;
				 }) ;
		});
	}
	
	btns.push('取消')
	funcs.push(function(dlgw){dlg.close();});
	
	let u =`./tn_runblk_js.jsp?cuid=\${cuid}&runner=\${b_runner}&tree_nid=\${tree_nid}&runblk_id=\${runblk_id}` ;
	dlg.open(u,{title:tt,w:'500px',h:'400px',js_txt:jstxt,pm_objs:{}},
			btns,funcs);
}

function show_blk_tp_js(tt)
{
	if(event) event.stopPropagation();
	
	let editt = `类型 JS - \${tt}` ;
	send_ajax("tn_runblk_ajax.jsp",{op:"get_runblk_tp_js",runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("js=")!=0)
		{
			dlg.msg(ret);return ;
		}
		let jstxt = ret.substring(3) ;
		show_blk_js(editt,jstxt,"set_runblk_tp_js") ;
	});
}

function show_blk_tp_js_detail(tt)
{
if(event) event.stopPropagation();
	
	let editt = `类型 JS - \${tt}` ;
	send_ajax("tn_runblk_ajax.jsp",{op:"get_runblk_tp_js_detail",runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("js=")!=0)
		{
			dlg.msg(ret);return ;
		}
		let jstxt = ret.substring(3) ;
		show_blk_js(editt,jstxt,null) ;
	});
}

function show_blk_call_test(tt)
{
	let ttt = `Call测试 - \${tt}` ;
	let btns =[];
	let funcs=[] ;
	btns.push('取消')
	funcs.push(function(dlgw){dlg.close();});
	let u =`./tn_runblk_runner_call_test.jsp?cuid=\${cuid}&runner=\${b_runner}&tree_nid=\${tree_nid}&runblk_id=\${runblk_id}` ;
	dlg.open(u,{title:ttt,w:'500px',h:'400px'},
			['关闭'],[function(dlgw){dlg.close();}]);
}

function show_blk_run_res(tt)
{
if(event) event.stopPropagation();
	
	let editt = `类型 JS - \${tt}` ;
	let u =`./tn_runblk_runner_res.jsp?cuid=\${cuid}&runner=\${b_runner}&tree_nid=\${tree_nid}&runblk_id=\${runblk_id}` ;
	dlg.open(u,{title:tt,w:'500px',h:'400px'},
			['关闭'],[function(dlgw){dlg.close();}]);
}

function edit_blk_ext_js(tt)
{
	if(event) event.stopPropagation();
	let editt = `扩展JS - \${tt}` ;
	send_ajax("tn_runblk_ajax.jsp",{op:"get_runblk_ext_js",runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("js=")!=0)
		{
			dlg.msg(ret);return ;
		}
		let jstxt = ret.substring(3) ;
		show_blk_js(editt,jstxt,"set_ins_js") ;
	});
}

function set_run_blk_pm()
{
	if(!b_device)
	{
		dlg.msg("计算参数只能在具体设备树中设置！");return ;
	}
	let u =`./tn_runblk_pm_edit.jsp?runner=\${b_runner}&cuid=\${cuid}&tree_nid=\${tree_nid}&runblk_id=\${runblk_id}` ;
	dlg.open(u,{title:"设置计算模块参数",w:'500px',h:'400px'},
			['确定','取消'],[
				function(dlgw)
				{
					dlgw.get_pm_vals(function(bsucc,ret){
						 if(!bsucc)
		        	     {
							 dlg.msg(ret) ;
							 return ;
		        	     }
						 
						 let pm={op:"set_runblk_pm",runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id,jstr:JSON.stringify(ret)};
						 
						 send_ajax("tn_runblk_ajax.jsp",pm,(bsucc,ret)=>{
							 if(!bsucc || ret.indexOf("succ")!=0)
							 {
								 dlg.msg(ret) ;
								 return ;
							 }
							 dlg.close() ;
							 update_ui();
						 }) ;
				 	});
				},
				function(dlgw){dlg.close();}
				]);
}

function set_run_blk_init(tt)
{
if(event) event.stopPropagation();
	
	let editt = `初始化运行模块 - \${tt}` ;
	let u =`./tn_runblk_runner_init.jsp?cuid=\${cuid}&runner=\${b_runner}&tree_nid=\${tree_nid}&runblk_id=\${runblk_id}` ;
	dlg.open(u,{title:editt,w:'500px',h:'400px'},
			['关闭'],[function(dlgw){dlg.close();}]);
}

function show_blk_detail()
{
	send_ajax("tn_runblk_ajax.jsp",{op:"get_runblk_detail",runner:b_runner,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id},function(bsucc,ret){
		if(!bsucc || ret.indexOf("{")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let blkdetail = null;
		eval("blkdetail="+ret) ;
	//show_detail(blkdetail);
		runblk = blkdetail ;
		//console.log(runblk) ;
		show_nodes() ;
	}) ;
}
	
function update_ui()
{
	show_blk_detail()
}
var Y_DIV = 24 ;

let l_def_real_eles = [] ;
let r_def_real_eles = [] ;

function show_nodes()
{
	if(!runblk) return ;

	let run_blk_mid = $("#run_blk_mid") ;
	let run_blk_left = $("#run_blk_left") ;
	let run_blk_right = $("#run_blk_right") ;
	let btn_pm_css = runblk.blk_pm_ok?"":"color:red;" ;
	let btn_init_css = "";
	let btn_init_tt = "" ;
	if(runblk.blk_init_ok!=true)
	{
		btn_init_css="color:red;";
		btn_init_tt="有未初始化参数，影响运行";
	}
	else if(runblk.blk_ioprop_all_set!=true)
	{
		btn_init_css="color:#ce9178;";
		btn_init_tt="有未初始化参数，不影响运行";
	}
		
	
	let d = "" ;
	if(runblk.ref_blk)
		d = runblk.ref_blk.d ;
	//console.log(runblk) ;
	let ss_mid=`<div class='blk_in_top' title="\${d}">&nbsp;\${runblk.en==false?"<span style='color:#ce7349'>未使能</span>":""}&nbsp;\${runblk.t} - \${runblk._tpt} [\${runblk._tp}]  </div>
		<div style="position:absolute;font-size0:20px;top:0px;right:5px;;text-align:center">
		</div>`
		if(b_runner)
		{
			ss_mid += `<div class='blk_in_btm' >
				<button onclick="show_blk_run_res('\${runblk.t} - \${runblk._tpt}')">运行结果</button>
				<span id="rt_run_inf" style="left:50px;position:absolute;border:0px solid;white-space:nowrap;"></span>
			</div>`
		}
		ss_mid += `<div style="font-size0:20px;height:25px;top:30px;background-color:#f2f2f2">
		 <div style="text-align:center;left:0px;position:absolute;">
		
		
		<button onclick="show_blk_tp_js('\${runblk.t} - \${runblk._tpt}')">继承JS</button>
		<button onclick="edit_blk_ext_js('\${runblk.t} - \${runblk._tpt}')">扩展JS</button>`
	if(b_runner)
	{
		ss_mid += `
			<button onclick="set_run_blk_pm()" style="\${btn_pm_css}" >参数设置</button>
			<button onclick="set_run_blk_init('\${runblk.t} - \${runblk._tpt}')" title="\${btn_init_tt}" style="\${btn_init_css}">初始化</button>
			<button onclick="debug_run_blk()"><i class="fa fa-bug"></i>运行调试</button>
			<button onclick="show_blk_tp_js_detail('\${runblk.t} - \${runblk._tpt}')"><i class="fa fa-bug"></i>详细JS</button>
			<button onclick="show_blk_call_test('\${runblk.t} - \${runblk._tpt}')"><i class="fa fa-bug"></i>Call测试</button>`;
	}
	
	ss_mid += `</div></div>`;
	let ss_left='';
	let ss_right="" ;
	
	let pos = 0 ;
	let ypos = 38 ;
	
	
	let ioplug_left_ns = [] ;
	let ioplug_right_ns=[] ;
	let ioprop_defns = [] ;
	let ioerrdef_defns = [] ;
	
	for(let rprop of runblk.io_props)
	{
		pos = ypos ;
		let def = rprop ;
		let color =  rprop.ins_pm_rd?"green":'red' ;
		
		let b_static = false;
		let b_local_auto = false;
		if(rprop.TP)
		{
			b_static = rprop.TP.def_pm?.static;
			b_local_auto= rprop.TP.def_pm?.local_auto;
		}
		let need_star="<span style='color:red' title='needed'>*</span>";
		let static_mark = "";
		let local_auto_mk = "" ;
		if(b_static)
			static_mark = "<span style='color:green' title='global static var'>G</span>";
		if(b_local_auto)
			local_auto_mk="<span style='color:green' title='local auto var'>L</span>";
		if(!rprop.needed)
		{
			need_star = "" ;
			if(!rprop.ins_pm_rd)
				color="#b46c24";
		}
		ss_mid += `<div id="ioprop_\${def.n}" def_n="\${def.n}"  title="\${def.t} [\${def.n}] &#13;&#10;\${def.ins_pm_err}" class="def_left jout prop_def" style="top:\${pos}px;cursor:pointer;border-color:\${color}" onclick="on_prop_def_clk(this)">
					<span class="t">\${static_mark}\${local_auto_mk} \${need_star} \${def.t} [\${def.n}]</span>
					<span class="icon" style="color:blue"><i class="fa-solid fa-circle-dot"></i></span>
					</div>`;
		let ins_pm = rprop.ins_pm ;
		if(ins_pm && Object.keys(ins_pm).length > 0)
		{
			ss_left += `<div id="ioprop_pm_\${def.n}" def_n="\${def.n}"  title="\${rprop.ins_pm_tt}" class="real_left jout" style="top:\${pos}px;cursor:pointer;border-color:\${color}" onclick="">
				<span class="op" ><button onclick="on_prop_pm_set('\${def.n}','\${def.t}')"><i class="fa-solid fa-pencil"></i></button></span>
				<span class="t">\${rprop.ins_pm_tt}</span>
				<span class="icon" style="color:\${color}"><i class="fa-solid fa-circle-dot"></i></span>
				</div>`;
			ioprop_defns.push(def.n) ;
		}
		else if(cur_ioprop_def && cur_ioprop_def.attr("def_n")==def.n && !b_local_auto)
		{
			ss_left += `<div id="ioprop_pm_\${def.n}" def_n="\${def.n}"  title="\${def.t}" class="real_left jout" style="top:\${pos}px;cursor:pointer;border:1px solid #ccc;" onclick="">
				<span class="op" ><button onclick="on_prop_pm_set('\${def.n}','\${def.t}')"><i class="fa-solid fa-pencil"></i></button></span>
				</div>`;
			ioprop_defns.push(def.n) ;
		}
		ypos += Y_DIV ;
	}
	
	ypos += Y_DIV/2 ;
	
	for(let plug of runblk.io_left_plugs)
	{
		pos = ypos ;
		
		let def = plug ;
		let color = (plug.ins_pm_need==false || plug.ins_pm_rd)?"green":'red' ;
		if(!plug.needed)
		{
			if(plug.ins_pm_need && !plug.ins_pm_rd)
				color="#b46c24";
		}
		ss_mid += `<div id="leftplug_\${def.n}" def_n="\${def.n}"  title="\${def.t} [\${def.n}] &#13;&#10;\${def.plug_t} &#13;&#10;\${def.ins_pm_err}" class="def_left jout plug_def" style="top:\${pos}px;cursor:pointer;border-color:\${color}" onclick="on_plug_def_clk(this)">
					<span class="t">\${def.t} [\${def.n}]</span>
					<span class="icon" style="color:blue"><i class="fa-solid fa-tag"></i></span>
					</div>`;
		if(plug.ins_pm_need)
		{
			let ins_pm = plug.ins_pm ;
			if(ins_pm && Object.keys(ins_pm).length > 0)
			{
				
				ss_left += `<div id="leftplug_pm_\${def.n}" ins_pm_in_runner="\${def.ins_pm_in_runner}" def_n="\${def.n}"  title="\${plug.ins_pm_tt}" class="real_left jout" style="top:\${pos}px;cursor:pointer;border-color:\${color}" onclick="">
						<span class="op" ><button onclick="on_plug_pm_set('\${def.plug}','\${def.n}','\${def.t}',\${def.ins_pm_in_runner})"><i class="fa-solid fa-pencil"></i></button></span>	
					<span class="t">\${plug.ins_pm_tt}</span>
					<span class="icon" style="color:\${color}"><i class="fa-solid fa-tag"></i></span>
					</div>`;
				//
				ioplug_left_ns.push(def.n) ;
			}
			else if(cur_ioplug_def && cur_ioplug_def.attr("def_n")==def.n)
			{
				ss_left += `<div id="leftplug_pm_\${def.n}" def_n="\${def.n}"  title="\${def.t}" class="real_left jout" style="top:\${pos}px;cursor:pointer;border:1px solid #ccc;" onclick="">
					<span class="op" ><button onclick="on_plug_pm_set('\${def.plug}','\${def.n}','\${def.t}',\${def.ins_pm_in_runner})"><i class="fa-solid fa-pencil"></i></button></span>
					</div>`;
				ioplug_left_ns.push(def.n) ;
			}
		}
		
		ypos += Y_DIV ;
	}
	
	let ypos_r = 38 ;
	for(let real_errdef of runblk.io_errdefs)
	{
		pos = ypos_r ;
		let def = real_errdef ;
		let color =  def.ins_pm_rd?"green":'red' ;
		if(!def.needed)
		{
			if(!def.ins_pm_rd)
				color="#b46c24";
		}
		ss_mid += `<div id="ioerrdef_\${def.n}" def_n="\${def.n}"  title="\${def.t}" class="def_right jout errdef_def" style="top:\${pos}px;cursor:pointer;" onclick="on_errdef_def_clk(this)">
					<span class="t">\${def.t} [\${def.n}]</span>
					<span class="icon" style="color:blue"><i class="fa-solid fa-bell"></i></span>
					</div>`;
		let ins_pm = real_errdef.ins_pm ;
		
		if(ins_pm && Object.keys(ins_pm).length >= 0)
		{
			let edef_id = ins_pm.def_id||"" ;
			
			ss_right += `<div id="ioerrdef_pm_\${def.n}" def_n="\${def.n}"  title="\${def.ins_pm_tt}" class="real_right jout" style="top:\${pos}px;cursor:pointer;" onclick="">
				<span class="op" ><button onclick="on_errdef_pm_set('\${def.n}','\${def.t}')"><i class="fa-solid fa-pencil"></i></button></span>
				<span class="t">\${def.ins_pm_tt}</span>
				<span class="icon" style="color:\${color}"><i class="fa-solid fa-circle-dot"></i></span>
				<span class="rt" id="ioerrdef_rt_\${edef_id}"></span>
				</div>`;
			ioerrdef_defns.push(def.n) ;
		}
		else if(cur_ioerrdef_def && cur_ioerrdef_def.attr("def_n")==def.n)
		{
			ss_right += `<div id="ioerrdef_pm_\${def.n}" def_n="\${def.n}"  title="\${def.t}" class="real_right jout" style="top:\${pos}px;cursor:pointer;border:1px solid #ccc;" onclick="">
				<span class="op" ><button onclick="on_errdef_pm_set('\${def.n}','\${def.t}')"><i class="fa-solid fa-pencil"></i></button></span>
				</div>`;
			ioerrdef_defns.push(def.n) ;
		}
		ypos_r += Y_DIV ;
	}
	
	ypos_r += Y_DIV/2 ;
	
	for(let plug of runblk.io_right_plugs)
	{
		pos = ypos_r ;
		//console.log(plug) ;
		let def = plug ;
		let color =  (plug.ins_pm_need==false || plug.ins_pm_rd)?"green":'red' ;
		if(!plug.needed)
		{
			if(plug.ins_pm_need && !plug.ins_pm_rd)
				color="#b46c24";
		}
		ss_mid += `<div id="rightplug_\${def.n}" def_n="\${def.n}"  title="\${def.t}" class="def_right jout plug_def" style="top:\${pos}px;cursor:pointer;border-color:\${color}" onclick="on_plug_def_clk(this)">
					<span class="t">\${def.t} [\${def.n}]</span>
					<span class="icon" style="color:\${color}"><i class="fa-solid fa-tag"></i></span>
					</div>`;
		let ins_pm = plug.ins_pm ;
		
		if(ins_pm && Object.keys(ins_pm).length > 0)
		{
			ss_right += `<div id="rightplug_pm_\${def.n}" def_n="\${def.n}"  title="\${def.t}" class="real_right jout" style="top:\${pos}px;cursor:pointer;border-color:\${color}" onclick="">
				<span class="op" ><button onclick="on_plug_pm_set('\${def.plug}','\${def.n}','\${def.t}',\${def.ins_pm_in_runner})"><i class="fa-solid fa-pencil"></i></button></span>
				<span class="t">\${plug.ins_pm_tt}</span>
				<span class="icon" style="color:\${color}"><i class="fa-solid fa-tag"></i></span>
				</div>`;
			//
			ioplug_right_ns.push(def.n) ;
		}
		else if(cur_ioplug_def && cur_ioplug_def.attr("def_n")==def.n)
		{
			ss_right += `<div id="rightplug_pm_\${def.n}" def_n="\${def.n}"  title="\${def.t}" class="real_right jout" style="top:\${pos}px;cursor:pointer;border:1px solid #ccc;" onclick="">
				<span class="op" ><button onclick="on_plug_pm_set('\${def.plug}','\${def.n}','\${def.t}',\${def.ins_pm_in_runner})"><i class="fa-solid fa-pencil"></i></button></span>
				</div>`;
				ioplug_right_ns.push(def.n) ;
		}
		ypos_r += Y_DIV ;
	}
	
	run_blk_mid.html(ss_mid) ;
	run_blk_left.html(ss_left) ;
	run_blk_right.html(ss_right) ;
	
	let yp = Math.max(ypos_r,ypos) ;
	run_blk_mid.css("height",(yp+34)+"px") ;
	//$("#ccc").css("height",(yp+54)+"px") ;
	//$("body").css("height",(yp+254)+"px") ;
	 l_def_real_eles =[];
	 for(let defn of ioplug_left_ns)
	 {
		 l_def_real_eles.push({defele:$("#leftplug_"+defn),realele:$("#leftplug_pm_"+defn)}) ;
	 }
	 
	 for(let defn of ioprop_defns)
	 {
		 l_def_real_eles.push({defele:$("#ioprop_"+defn),realele:$("#ioprop_pm_"+defn)}) ;
	 }
	 
	 r_def_real_eles =[] ;
	 for(let defn of ioplug_right_ns)
	 {
		 r_def_real_eles.push({defele:$("#rightplug_"+defn),realele:$("#rightplug_pm_"+defn)}) ;
	 }
	 for(let defn of ioerrdef_defns)
	 {
		 r_def_real_eles.push({defele:$("#ioerrdef_"+defn),realele:$("#ioerrdef_pm_"+defn)}) ;
	 }
	draw_conns() ;
}

function draw_conns(b_height,b_clear)
{
	draw_left_conns(b_height,b_clear);
	draw_right_conns(b_height,b_clear);
}

function draw_left_conns(b_height,b_clear)
{
	if(!l_def_real_eles) return ;
	
	let pdiv = $("#run_blk_left") ;
	let can = pdiv.find("#left_can") ;
	let cxt ;
	if(!can || can.length==0)
	{
		cxt = document.createElement('canvas').getContext('2d');
		can = $(cxt.canvas);
		can.attr("id","left_can") ;
		can.css("position", "relative");
		can.css("left", "0%");
		can.css("top", "0px");
		can.css("display","");
		can.css("z-index","0");
		pdiv.append(can);
		can.attr('width', pdiv[0].offsetWidth) ;
		can.attr('height', pdiv[0].offsetHeight+200) ;
		pdiv.resize(()=>{
			let w = pdiv[0].offsetWidth;
			let h = pdiv[0].scrollHeight+10;
			//console.log(w,h)
			//can.attr('width',w) ;
			//can.attr('height', h) ;
			draw_left_conns();
		});
	}
	else
	{
		cxt = can[0].getContext('2d');
	}
	
	if(b_height)
	{
		let w = pdiv[0].offsetWidth;
		let h = pdiv[0].scrollHeight+10;
		can.attr('width',w) ;
		can.attr('height', h) ;
	}
	
	if(b_clear)
		cxt.clearRect(0,0,can.width,can.height) ;

	for(let dr of l_def_real_eles)
	{
		//console.log(dr) ;
		let defele = dr.defele ;
		let realele = dr.realele ;
		
		draw_left_line(cxt,realele,defele) ;
	}
}

function draw_right_conns(b_height,b_clear)
{
	if(!r_def_real_eles) return ;
	let pdiv = $("#run_blk_right") ;
	let can = pdiv.find("#right_can") ;
	let cxt ;
	if(!can || can.length==0)
	{
		cxt = document.createElement('canvas').getContext('2d');
		can = $(cxt.canvas);
		can.attr("id","right_can") ;
		can.css("position", "relative");
		can.css("left", "0%");
		can.css("top", "0px");
		can.css("display","");
		can.css("z-index","0");
		pdiv.append(can);
		can.attr('width', pdiv[0].offsetWidth) ;
		can.attr('height', pdiv[0].offsetHeight+200) ;
		pdiv.resize(()=>{
			let w = pdiv[0].offsetWidth;
			//let h = pdiv[0].scrollHeight+10;
			//console.log(w,h)
			//can.attr('width',w) ;
			//can.attr('height', h) ;
			draw_right_conns();
		});
	}
	else
	{
		cxt = can[0].getContext('2d');
	}
	
	if(b_height)
	{
		let w = pdiv[0].offsetWidth;
		let h = pdiv[0].scrollHeight+10;
		can.attr('width',w) ;
		can.attr('height', h) ;
	}
	
	if(b_clear)
		cxt.clearRect(0,0,can.width,can.height) ;

	for(let dr of r_def_real_eles)
	{
		let defele = dr.defele ;
		let realele = dr.realele ;
		
		draw_right_line(cxt,defele,realele) ;
	}
}

function draw_left_line(cxt,l_ele,r_ele)
{
	let lx = l_ele.offset().left +l_ele.width();
	let ly = l_ele.offset().top-40 +l_ele.height()/2;
	let rx = r_ele.offset().left;
	let ry = ly;// r_ele.offset().top-50 ;
	
	cxt.beginPath();
	cxt.moveTo(lx,ly);
	cxt.lineTo(rx,ry);
	cxt.stroke() ;
}

function draw_right_line(cxt,l_ele,r_ele)
{
	let rx = r_ele.position().left;
	let ry = r_ele.offset().top-40+r_ele.height()/2 ;
	
	let lx = 0;// l_ele.offset().left +l_ele.width();
	let ly = ry;//l_ele.offset().top-48 +l_ele.height()/2;
	
	
	cxt.beginPath();
	cxt.moveTo(lx,ly);
	cxt.lineTo(rx,ry);
	cxt.stroke() ;
}

show_blk_detail();
var last_hpx = -1 ;
function fit_height()
{
	var hpx =($(window).height()-35);
	if(last_hpx==hpx)
		return ;
	//console.log(hpx);
	last_hpx = hpx;
	$("#ccc").css("height",hpx+"px")
}
fit_height();
$(window).resize(function(){
	fit_height();
});

function debug_run_blk()
{
	dlg.loading(true) ;
	send_ajax("tn_runblk_runners_ajax.jsp",{op:"debug_run_blk_once",runner:true,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id},function(bsucc,ret){
		dlg.loading(false) ;
		dlg.msg(ret) ;
	}) ;
}

function update_run_rt()
{
	let pm = {op:"blkr_rt_info",runner:true,cuid:cuid,tree_nid:tree_nid,runblk_id:runblk_id};
	send_ajax("tn_runblk_runners_ajax.jsp",pm,function(bsucc,ret){
		if(!bsucc && ret.indexOf("{")!=0)
		{
			$("#rt_run_inf").html(ret) ;
			return ;
		}
		let ob = null;
		eval("ob="+ret) ;
		let tmps = "" ;
		//console.log(ob);
		if(ob.last_dt)
		{
			let dt = new Date(ob.last_dt).toShortGapNow() ;
			let inf ;
			if(ob.last_run_ok)
				inf = `<span style="color:green;white-space:nowrap;" title="\${ob.last_run_inf}">√ \${ob.last_run_inf}</span>`;
			else
				inf = `<span style="color:red;white-space:nowrap;" title="\${encode_entity(ob.last_run_inf)}">× \${ob.last_run_inf}</span>`;
			tmps += `\${dt} \${inf}` ;
		}
		$("#rt_run_inf").html(tmps) ;
		//console.log(ob.err_reals) ;
		if(ob.err_reals)
		{//show err def
			for(let err of ob.err_reals)
			{
				let eleob=  $(`#ioerrdef_rt_\${err.id}`) ;
				
				if(err.triggered)
				{
					eleob.html('<i class="fa-solid fa-bolt"></i>')
				}
				else
				{
					eleob.html('')
				}
			}
		}
	}) ;
}
	
function encode_entity(str)
{if(!str) return str ;
	  return str.replace(/[<>&"']/g, (match) => {
	    return {
	      '<': '&lt;',
	      '>': '&gt;',
	      '&': '&amp;',
	      '"': '&quot;',
	      "'": '&#39;'
	    }[match];
	  });
}

if(b_runner && b_device_run)
	setInterval(update_run_rt,3000) ;
</script>

</body>
</html>