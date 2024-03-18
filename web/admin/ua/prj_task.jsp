<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
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

.rmenu_item:hover {
	background-color: #373737;
}



</style>
<body marginwidth="0" marginheight="0">
 <blockquote class="layui-elem-quote "><wbt:g>task,list</wbt:g>
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_edit_task('<%=prjid %>',null)">+<wbt:g>add,task</wbt:g></button>
 	
 	 <button class="layui-btn layui-btn-sm layui-border-blue"  onclick="import_task()">
							 <i class="fa-solid fa-file-import"></i>&nbsp;<wbt:g>imp,task</wbt:g>
							</button>
							
 </div>
</blockquote>
<%
List<Task> jts = TaskManager.getInstance().getTasks(prjid);
for(Task jt:jts)
{
	String run_c = "grey" ;
	String run_t = "disabled" ;
	
	if(jt.RT_isRunning())
	{
		run_c = "green" ;
		run_t = "running" ;
	}
	else
	{
		run_c = "red" ;
		run_t = "stopped" ;
	}
	
	List<TaskAction> tas = jt.getActions(); 
%>
<form class="layui-form" action="">
<div>
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 	
 </div>
 </div>
<table class="layui-table">
  <colgroup>
    <col width="150">
    <col width="200">
    <col>
  </colgroup>
  <thead>
    <tr>
      <th><%=jt.getName() %></th>
      <th><%=jt.getTitle() %></th>
      
      <th>
      
      <%
if(jt.isEnable())
{
%>
<span id="" style="width:20px;height:20px;background-color: <%=run_c %>;" >&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;<wbt:g><%=run_t %></wbt:g>
      
<%
	if(jt.RT_isRunning())
	{
%>

		 <i id="prj_btn_stop"  class="fa fa-pause fa-lg" style="color:red" title="<wbt:g>stop,task</wbt:g>" onclick="start_stop(false,'<%=jt.getId()%>')"></i>
		 
<%
	}
	else
	{
%>
<i id="prj_btn_start"  class="fa fa-play fa-lg" style="color:green" title="<wbt:g>start,task</wbt:g>" onclick="start_stop(true,'<%=jt.getId()%>')"></i>
<%
	}
}
else
{
%><span id="" style="width:20px;height:20px;background-color: grey;" >&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;<wbt:g>disabled</wbt:g>
<%
}
%>
        
        
        
      </th>
       <th>
		<a href="javascript:add_or_edit_task('<%=prjid %>','<%=jt.getId()%>')"><i title="<wbt:g>edit,task</wbt:g>" class="fa fa-pencil-square fa-lg " aria-hidden="true"></i></a>
	   <a href="javascript:task_del('<%=prjid %>','<%=jt.getId()%>')" style="color:red"><i title="<wbt:g>del,task</wbt:g>" class="fa fa-times fa-lg " aria-hidden="true"></i></a>
	  </th>
	  <th>
<a href="javascript:add_or_edit_task_act('<%=prjid %>','<%=jt.getId()%>',null)"><i title="add action" class="fa fa-plus fa-lg " aria-hidden="true"></i></a>
      
      <a href="javascript:export_task('<%=prjid %>','<%=jt.getId()%>')" title="export">
             <i class="fa-solid fa-file-export"></i>
           </a>
           
          
	  </th>
    </tr> 
  </thead>
<%
if(tas!=null&&tas.size()>0)
{
%>
  <tbody>
<%
	for(TaskAction ta:tas)
	{
		
%>
    <tr>
       <td><%=ta.getName() %></td>
      
      <td colspan="3">
      	<button onclick="edit_task_js('<%=jt.getId() %>','<%=ta.getId() %>','init')" class="layui-btn layui-btn-<%=(ta.hasInitScript()?"normal":"primary") %> layui-border-blue layui-btn-sm"><wbt:g>init_js</wbt:g></button>
      	<button onclick="edit_task_js('<%=jt.getId() %>','<%=ta.getId() %>','run')" class="layui-btn layui-btn-<%=(ta.hasRunScript()?"normal":"primary") %> layui-border-blue layui-btn-sm" ><wbt:g>loop_js</wbt:g></button>
      	<button onclick="edit_task_js('<%=jt.getId() %>','<%=ta.getId() %>','end')" class="layui-btn layui-btn-<%=(ta.hasEndScript()?"normal":"primary") %> layui-border-blue layui-btn-sm"><wbt:g>end_js</wbt:g></button>
      	
      </td>
      <td>
      <a href="javascript:add_or_edit_task_act('<%=prjid %>','<%=jt.getId()%>','<%=ta.getId()%>')"><i title="edit task action" class="fa fa-pencil-square fa-lg " aria-hidden="true"></i></a>
	   <a href="javascript:task_act_del('<%=prjid %>','<%=jt.getId()%>','<%=ta.getId()%>')" style="color:red"><i title="delete task action" class="fa fa-times fa-lg " aria-hidden="true"></i></a>
	 
      </td>
    </tr>
<%
	}
%>
  </tbody>
<%
}
%>
</table>
<%
}
%>
</form>
<div style="display:none">
 <textarea id="ta_js"></textarea>
</div>
<script>
var prjid = "<%=prjid%>" ;
var prjpath = "<%=prj.getNodePath()%>" ;
var form = null;
layui.use('form', function(){
	  form = layui.form;
	  form.render();
});

var taskact_js = null;
var taskact_js_txt = '' ;

function show_script()
{
	var tt = "Edit JS" ;
	switch(taskact_js.jstp)
	{
	case "init":
		tt = "Edit Init Script (run once)" ;
		break ;
	case "run":
		tt = "Edit Run Script (run interval)" ;
		break;
	case "end":
		tt = "Edit End Script (run once)" ;
		break;
	}
	dlg.open("../ua_cxt/cxt_script.jsp?op=task&path="+prjpath+"&taskid="+taskact_js.taskid+"&opener_txt_id=ta_js",
			{title:tt},['Ok','Cancel'],
			[
				function(dlgw)
				{
					var jstxt = dlgw.get_edited_js() ;
					 if(jstxt==null)
						 jstxt='' ;
					 taskact_js.op='act_js_write';
					 taskact_js.jstxt=jstxt;
					 
						send_ajax("prj_task_ajax.jsp",taskact_js,function(bsucc,ret){
							if(bsucc&&ret.indexOf('succ')!=0)
							{
								dlg.msg(ret) ;
								return ;
							}
							dlg.close() ;
							document.location.href=document.location.href;
						}) ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function edit_task_js(taskid,actid,jstp)
{
	event.preventDefault();
	
	taskact_js = {prjid:prjid,op:'act_js_read',taskid:taskid,actid:actid,jstp:jstp} ;
	
	send_ajax("prj_task_ajax.jsp",taskact_js,function(bsucc,ret){
		if(bsucc&&ret.indexOf('succ=')!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		
		$("#ta_js").val(ret.substring(5)) ;
		show_script();
	}) ;
	
	
}

function task_del(prjid,id)
{
	layer.confirm('delete selected task?', function(index)
		    {
		    	send_ajax("prj_task_ajax.jsp","prjid="+prjid+"&op=del&taskid="+id,function(bsucc,ret){
		    		if(bsucc&&ret=='succ')
		    			obj.del();
		    		else
		    			layer.msg("del err:"+ret) ;
		    	}) ;
		      
		      document.location.href=document.location.href;
		    });
}

function export_task(prjid,taskid)
{
	window.open("prj_task_ajax.jsp?op=export&prjid="+prjid+"&taskid="+taskid) ;
}

function add_or_edit_task(prjid,id)
{
	var tt = "<wbt:g>add,prj,task</wbt:g>";
	if(id)
	{
		tt = "<wbt:g>edit,prj,task</wbt:g>";
	}
	if(id==null)
		id = "" ;
	dlg.open("prj_task_edit.jsp?prjid="+prjid+"&taskid="+id,
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
						 
						 ret.op="add" ;
						 if(id)
							 ret.op = "edit";
						 ret.prjid=prjid;
						 ret.taskid = id ;
						 var pm = {
									type : 'post',
									url : "./prj_task_ajax.jsp",
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


function add_or_edit_task_act(prjid,taskid,id)
{
	var tt = "<wbt:g>add,task,action</wbt:g>";
	if(id)
	{
		tt = "<wbt:g>edit,task,action</wbt:g>";
	}
	if(id==null)
		id = "" ;
	dlg.open("prj_task_act_edit.jsp?prjid="+prjid+"&taskid="+taskid+"&actid="+id,
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
						 
						 ret.op="act_add" ;
						 if(id)
							 ret.op = "act_edit";
						 ret.prjid=prjid;
						 ret.taskid = taskid ;
						 ret.actid = id ;
						 var pm = {
									type : 'post',
									url : "./prj_task_ajax.jsp",
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