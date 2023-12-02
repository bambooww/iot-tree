<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*
	"%><%!

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
	border:1px solid;
	width:200px;
	bottom:0px;
}

.mid
{
	position: absolute;
	top:0px;
	left:200px;
	border:1px solid;
	right:200px;
	bottom:0px;
}

.right
{
	position: absolute;
	top:0px;
	right:0px;
	border:1px solid;
	width:200px;
	bottom:0px;
}
.alert_item
{
	margin-left:30px;
}

.h_item
{
	position:relative;
	left:10%;
	width:80%;
	height:40px;
	border:1px solid;
	margin-bottom: 10px;
}
.h_item .n
{
	position:absolute;
	font-size: 18px;
	left:5px;
}
.h_item .t
{
	position:absolute;
	font-size: 20px;
	top:5px;
	left:10px;
}
.h_item .tp
{
	position:absolute;
	font-size: 25px;
	top:10px;
	right:10px;
}
.h_item .tpt
{
	position:absolute;
	font-size: 15px;
	top:1px;
	right:5px;
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
	left:10%;
	width:80%;
	height:60px;
	border:1px solid;
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
	position:absolute;
	font-size: 20px;
	top:18px;
	left:10px;
}
.out_item .tp
{
	position:absolute;
	font-size: 25px;
	top:10px;
	right:10px;
}
.out_item .tpt
{
	position:absolute;
	font-size: 15px;
	top:1px;
	right:5px;
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

.chk_alert_c
{
visibility: hidden;
}
.chk_out_c
{
visibility: hidden;
}
</style>
<body marginwidth="0" marginheight="0">
<div class="left">
 <blockquote class="layui-elem-quote ">Alert Tags</blockquote>
 <div id="tag_list">
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
%><div class="alert_item"><span class="chk_alert_c"><input type="checkbox" id="<%=id %>"  class="chk_alert" onclick="on_chk_alert('<%=id%>')"/></span><%=tt %></div>

<%
		}
%>
</div>
<%
	}
%>
 </div>
</div>
<div class="mid">
 <blockquote class="layui-elem-quote ">Alert Handlers
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 <button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="save_h_inout_ids('<%=prjid %>')">Save </button>
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_edit_h('<%=prjid %>',null)">+Add </button>
 	<button class="layui-btn layui-btn-sm layui-border-blue"  onclick="import_alert()"><i class="fa-solid fa-file-import"></i>&nbsp;Import</button>
 </div>
</blockquote>
 <div id="handler_list" style="height:100%;" onclick="on_handler_clk()">
 </div>
</div>
<div class="right">
 <blockquote class="layui-elem-quote ">Outputs
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_edit_o('<%=prjid %>',null)">+Add </button>
 </div>
</blockquote>
 <div id="out_list">
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
					<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_out('\${prjid}','\${ob.id}')" title="delete"><i class="fa-regular fa-rectangle-xmark"></i></button>
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
			tmps += `<div id="h_\${ob.id}" class="h_item" h_id="\${ob.id}" t="\${ob.t}" onclick="on_handler_clk(this)" out_ids="\${ob.out_ids}" alert_uids="\${ob.alert_uids}">
				<span class="t">\${ob.t}</span>
				<span class="trigger_en \${ob.trigger_en?'en':''}"></span>
				<span class="release_en \${ob.release_en?'en':''}"></span>
				<span class="oper">
					<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_h('\${prjid}','\${ob.id}')"><i class="fa fa-pencil"></i></button>
					<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_handler('\${prjid}','\${ob.id}')" title="delete"><i class="fa-regular fa-rectangle-xmark"></i></button>
				</span>
				</div>` ;
		}
		$("#handler_list").html(tmps) ;
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
	});
}
	
function del_handler(prjid,id)
{
	event.stopPropagation();
	dlg.confirm('delete this handler?',{btn:["Yes","Cancel"],title:"Delete Confirm"},function ()
		    {
					send_ajax("prj_alert_ajax.jsp",{op:"del_h",prjid:prjid,id:id},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("del err:"+ret) ;
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
	var tt = "Add Alert Handler";
	if(id)
	{
		tt = "Edit Alert Handler";
	}
	if(id==null)
		id = "" ;
	dlg.open("prj_alert_h_edit.jsp?prjid="+prjid+"&id="+id,
			{title:tt},
			['Ok','Cancel'],
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
	dlg.confirm('delete this output?',{btn:["Yes","Cancel"],title:"Delete Confirm"},function ()
		    {
					send_ajax("prj_alert_ajax.jsp",{op:"del_o",prjid:prjid,id:id},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("del err:"+ret) ;
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
	var tt = "Add Alert Output";
	if(id)
	{
		tt = "Edit Alert Output";
	}
	if(id==null)
		id = "" ;
	dlg.open("prj_alert_o_edit.jsp?prjid="+prjid+"&id="+id,
			{title:tt},
			['Ok','Cancel'],
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


function import_dc_txt(prjid,cid)
{
	var tt = "Import Data Node";
	dlg.open("prj_dict_dc_imp_txt.jsp?prjid="+prjid+"&cid="+cid,
			{title:tt},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="dc_imp_txt" ;
						 ret.prjid=prjid;
						 ret.cid = cid ;
						 var pm = {
									type : 'post',
									url : "./prj_dict_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								document.location.href=document.location.href;
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

function add_or_edit_dn(prjid,cid,id)
{
	event.stopPropagation();
	var tt = "Add Data Node";
	if(id)
	{
		tt = "Edit Data Node";
	}
	if(id==null)
		id = "" ;
	dlg.open("prj_dict_dn_edit.jsp?prjid="+prjid+"&cid="+cid+"&id="+id,
			{title:tt},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="add_dn" ;
						 if(id)
							 ret.op = "edit_dn";
						 ret.prjid=prjid;
						 ret.cid = cid ;
						 ret.id = id ;
						 var pm = {
									type : 'post',
									url : "./prj_dict_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								document.location.href=document.location.href;
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


function task_act_del(prjid,taskid,actid)
{
	layer.confirm('delete selected action?', function(index)
		    {
		    	send_ajax("prj_task_ajax.jsp","prjid="+prjid+"&op=act_del&taskid="+taskid+"&actid="+actid,function(bsucc,ret){
		    		if(bsucc&&ret=='succ')
		    		{
			    		document.location.href=document.location.href;
		    		}
		    		else
		    			layer.msg("del err:"+ret) ;
		    	}) ;
		      
		      
		    });
}


function start_stop(b,taskid)
{
	var op = "start" ;
	if(!b)
		op = "stop";
	$.ajax({
        type: 'post',
        url:'prj_task_ajax.jsp',
        data: {op:op,prjid:prjid,taskid:taskid},
        async: true,  
        success: function (result) {  
        	if("ok"==result)
        	{
        		document.location.href=document.location.href ;
        	}
        	else
        	{
        		dlg.msg(result) ;
        	}
        },
        error:function(req,err,e)
        {
        	dlg.msg(e);
        }
    });
}

</script>

</body>
</html>