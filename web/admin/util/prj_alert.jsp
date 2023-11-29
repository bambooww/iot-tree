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
	right:300px;
	bottom:0px;
}

.right
{
	position: absolute;
	top:0px;
	right:0px;
	border:1px solid;
	width:300px;
	bottom:0px;
}
.alert_item
{
	margin-left:30px;
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
%><div class="alert_item"><input type="checkbox" id="<%=id %>" /><%=tt %></div>

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
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_edit_h('<%=prjid %>',null)">+Add </button>
 	<button class="layui-btn layui-btn-sm layui-border-blue"  onclick="import_alert()"><i class="fa-solid fa-file-import"></i>&nbsp;Import</button>
 </div>
</blockquote>
</div>
<div class="right">
 <blockquote class="layui-elem-quote ">Alert Outputs
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_edit_dc('<%=prjid %>',null)">+Add </button>
 	<button class="layui-btn layui-btn-sm layui-border-blue"  onclick="import_alert()"><i class="fa-solid fa-file-import"></i>&nbsp;Import</button>
 </div>
</blockquote>
</div>
 	
<script>
var prjid = "<%=prjid%>" ;

function show_or_hide(cid,bshow)
{
	var bdo = $("#bd_"+cid) ;
	if(bdo.attr("b_load")!='true')
	{
		send_ajax("prj_dict_ajax.jsp","prjid="+prjid+"&op=list_html&prjid="+prjid+"&cid="+cid,function(bsucc,ret){
			bdo.html(ret) ;
			bdo.attr("b_load","true");
			bdo.attr("b_show","true") ;
		}) ;
		return ;
	}
	
	if(bshow==undefined)
	{
		if(bdo.attr("b_show")=='true')
		{
			bdo.css("display",'none') ;
			bdo.attr("b_show","false") ;
		}
		else
		{
			bdo.css("display",'') ;
			bdo.attr("b_show","true") ;
		}
		return ;
	}
	
	
}

function del_dc(prjid,cid)
{
	event.stopPropagation();
	layer.confirm('Delete this DataClass?', function(index)
		    {
		    	send_ajax("prj_dict_ajax.jsp","prjid="+prjid+"&op=del_dc&cid="+cid,function(bsucc,ret){
		    		if(bsucc&&ret=='succ')
		    			$("#tb_"+cid).remove();
		    		else
		    			layer.msg("del err:"+ret) ;
		    	}) ;
		      layer.close(index) ;
		      //document.location.href=document.location.href;
		    });
}

function export_task(prjid,taskid)
{
	window.open("prj_task_ajax.jsp?op=export&prjid="+prjid+"&taskid="+taskid) ;
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
						 
						 ret.op="add_h" ;
						 if(id)
							 ret.op = "edit_h";
						 ret.prjid=prjid;
						 ret.id = id ;
						 var pm = {
									type : 'post',
									url : "./prj_alert_ajax.jsp",
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