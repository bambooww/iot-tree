<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	org.iottree.core.sim.*,
	org.iottree.driver.common.modbus.sim.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out,"insid"))
	return ;
String insid = request.getParameter("insid") ;
SimInstance ins = SimManager.getInstance().getInstance(insid) ;
if(ins==null)
{
	out.print("no instance found") ;
	return ;
}
%><html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
<link rel="stylesheet" href="/_js/selectmenu/selectmenu.css" />
<script src="/_js/selectmenu/selectmenu.min.js"></script>
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
<script type="text/javascript">
dlg.resize_to(700,500) ;
</script>
<body marginwidth="0" marginheight="0">
 <blockquote class="layui-elem-quote ">Simulation Instance [<%=ins.getTitle() %>]
 
      	
      	
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
  <button onclick="edit_ins_js('<%=ins.getId()%>','','init')" class="layui-btn layui-btn-<%=(false?"normal":"primary")%> layui-border-blue layui-btn-sm">init script</button>
  <button onclick="edit_ins_js('<%=ins.getId()%>','','run')" class="layui-btn layui-btn-<%=(false?"normal":"primary")%> layui-border-blue layui-btn-sm" >run in loop script</button>
  <button onclick="edit_ins_js('<%=ins.getId()%>','','end')" class="layui-btn layui-btn-<%=(false?"normal":"primary")%> layui-border-blue layui-btn-sm">end script</button>
   
  <div class="btn-group open "  id="menu_tree_add_ch" style="border:solid 1px;">
	  <a class="btn layui-btn layui-btn-sm" href="#" ><i class="fa fa-exchange fa-lg"></i> +Add Channel(Bus)</a>
	  <a class="btn layui-btn layui-btn-sm"  href="#">
	    <span class="fa fa-caret-down" title="Toggle dropdown menu"></span>
	  </a>
  </div>

 	<%--
 		<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_edit_ch('')">+Add Channel(Bus)</button>
 	 <button class="layui-btn layui-btn-sm layui-border-blue"  onclick="import_task()">
							<span class="fa-stack">
							  <i class="fa fa-square-o fa-stack-2x"></i>
							  <i class="fa fa-arrow-down fa-stack-1x"></i>
							</span>&nbsp;Import Task
							</button>
							 --%>
 </div>
</blockquote>
<table style="width:100%;height:90%;border:solid 1px;" >
  <tr>
    <td style="width:50%;height:100%;" valign="top">
      <div style="width:100%;height:100%;overflow: auto;">
<%
	List<SimChannel> chs = ins.getChannels();
for(SimChannel ch:chs)
{
	String ch_tp = ch.getTp() ;
	String ch_tpt = ch.getTpTitle() ;
	String run_c = "grey" ;
	String run_t = "disabled" ;
	String run_icon="" ;
	
	if(ch.RT_isRunning())
	{
		run_c = "green" ;
		run_t = "running" ;
		run_icon="fa-spin";
	}
	else
	{
		run_c = "red" ;
		run_t = "stopped" ;
	}
	
	List<SimDev> devs = ch.listDevItems() ;
	SimCP conn = ch.getConn() ;
	String connt = "No Conn" ;
	if(conn!=null)
		connt = conn.getConnTitle() ;
%>
<form class="layui-form" action="" >
<table class="layui-table">
  <colgroup>
    <col width="200">
    <col width="150">
    <col>
  </colgroup>
  <thead>
    <tr>
      <th><i class="fa fa-exchange fa-lg"></i><%=ch.getTitle()%> - <%=ch.getName()%>
      <a href="javascript:add_or_edit_ch('<%=ch_tp %>','<%=ch_tpt %>','<%=ch.getId()%>')"><i title="edit channel" class="fa fa-pencil-square fa-lg " aria-hidden="true"></i></a>
      </th>
      <th>
      <%=connt %>
     <a onclick="ch_conn_set('<%=ins.getId()%>','<%=ch.getId() %>')" ><i title="edit channel" class="fa fa-pencil-square fa-lg " aria-hidden="true"></i> </a>
   
      </th>
      <th>
      
      <%
            	if(ch.isEnable())
            {
            %>
<span id="" style="width:20px;height:20px;color: <%=run_c%>;" ><i class="fa fa-cog <%=run_icon %> fa-lg"></i></span>&nbsp;<%=run_t%>
      
<%
      	if(ch.RT_isRunning())
      	{
      %>

		 <i id="prj_btn_stop"  class="fa fa-pause fa-lg" style="color:red" title="stop task" onclick="ch_start_stop(false,'<%=ch.getId()%>')"></i>
		 
<%
		 	}
		 	else
		 	{
		 %>
<i id="prj_btn_start"  class="fa fa-play fa-lg" style="color:green" title="start" onclick="ch_start_stop(true,'<%=ch.getId()%>')"></i>
<%
	}
}
else
{
%><span id="" style="width:20px;height:20px;color: grey;" ><i class="fa fa-cog fa-lg"></i></span>&nbsp;Disabled
<%
	}
%>
      </th>
       <th>
		
	   <a href="javascript:ch_del('<%=ch.getId()%>')" style="color:red"><i title="delete channel" class="fa fa-times fa-lg " aria-hidden="true"></i></a>
	  </th>
	  <th>
<a href="javascript:add_or_edit_dev('<%=ch_tp %>','<%=ch_tpt %>','<%=ch.getId()%>')"><i title="add device" class="fa fa-plus fa-lg " aria-hidden="true"></i></a>
      
      <a href="javascript:export_ch('<%=ch.getId()%>')" title="export">
              <span class="fa-stack">
							  <i class="fa fa-square-o fa-stack-2x"></i>
							  <i class="fa fa-arrow-up fa-stack-1x"></i>
							</span>
           </a>
           
          
	  </th>
    </tr> 
  </thead>
<%
	if(devs!=null&&devs.size()>0)
{
%>
  <tbody>
<%
	for(SimDev ta:devs)
	{
%>
    <tr title="<%=ta.getTitle()%>">
       <td><img src="../inc/sm_icon_dev.png"/><%=ta.getName() %><a href="javascript:add_or_edit_dev('<%=ch_tp %>','<%=ch_tpt %>','<%=ch.getId() %>','<%=ta.getId() %>')"><i title="edit device" class="fa fa-pencil-square fa-lg " aria-hidden="true"></i></a></td>
      
      <td colspan="2">
      <%=ta.getDevTitle() %>
      	<a onclick="dev_setup('<%=ch_tp %>','<%=ch_tpt %>','<%=ch.getId() %>','<%=ta.getId() %>')" ><i title="setup device" class="fa fa-pencil-square fa-lg " aria-hidden="true"></i></a>
      	<button onclick="dev_tags_edit('<%=ch_tp %>','<%=ch_tpt %>','<%=ch.getId() %>','<%=ta.getId() %>')" class="layui-btn layui-btn-<%=(false?"normal":"primary") %> layui-border-blue layui-btn-sm" >Tags</button>
      	
      </td>
      <td>
       <a href="javascript:dev_del(''<%=ch.getId() %>','<%=ta.getId() %>')" title="delete device"  style="color:red"><i class="fa fa-times fa-lg " aria-hidden="true"></i></a>
      </td>
      <td>
      
	  
	   <a id="dev_rt_<%=ta.getId() %>" href="javascript:dev_rt('<%=ch_tp %>','<%=ch.getId() %>','<%=ta.getId() %>')" title="device runtime"  style="color:#00537c"><i id="dev_run_icon" class="fa fa-cog fa-lg"></i></a>
	 
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
  </div>
</td>
    <td style="width:100%;height:100%" valign="top">
    <iframe id="rightf" src="" style="width:100%;height:99%;border:0px"></iframe>
    </td>
  </tr>
</table>
<div style="display:none">
 <textarea id="ta_js"></textarea>
</div>
<script>
var insid="<%=insid%>" ;

var form = null;
layui.use('form', function(){
	  form = layui.form;
	  form.render();
});

var taskact_js = null;
var taskact_js_txt = '' ;

function refresh_me()
{
	document.location.href="sim_ins_mgr.jsp?insid="+insid ;
}

var tree_menu = [
	
	{content:'Modbus Slave',callback:function(){add_or_edit_ch('mslave','ModbusSlave','');}},
	{content:'sm_divider'},
	
];

$('#menu_tree_add_ch').click(function(){
	$(this).selectMenu({
		title : 'Add Channel (bus)',
		regular : true,
		data : tree_menu
	});
});

function show_script()
{
	dlg.open("../ua_cxt/cxt_script.jsp?op=task&path="+prjpath+"&taskid="+taskact_js.taskid+"&opener_txt_id=ta_js",
			{title:'Edit JS'},['Ok','Cancel'],
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

function ch_conn_set(insid,chid)
{
	event.preventDefault();
	dlg.open("sim_ch_conn_edit.jsp?insid="+insid+"&chid="+chid,
			{title:"Set Channel Connection"},
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
						 
						 ret.op="ch_conn" ;
						 ret.chid = chid ;
						 ret.insid = insid ;
						 var pm = {
									type : 'post',
									url : "./sim_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								refresh_me();
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

function ch_del(id)
{
	layer.confirm('delete selected channel?', function(index)
		    {
		    	send_ajax("sim_ajax.jsp","insid="+insid+"&op=ch_del&chid="+id,function(bsucc,ret){
		    		if(bsucc&&ret=='succ')
		    			refresh_me();
		    		else
		    			layer.msg("del err:"+ret) ;
		    	}) ;
		      
		    	refresh_me();
		    });
}

function export_ch(chid)
{
	window.open("mslave_ajax.jsp?op=export&chid="+chid) ;
}

function add_or_edit_ch(tp,tpt,id)
{
	var tt = "Add ["+tpt+"] Channel(bus)";
	if(id)
		tt = "Edit ["+tpt+"] Channel(bus)";

	if(id==null)
		id = "" ;
	dlg.open(tp+"_ch_edit.jsp?insid="+insid+"&chid="+id,
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
						 
						 ret.op="ch_add" ;
						 if(id)
							 ret.op = "ch_edit";
						 ret.chtp=tp;
						 ret.chid = id ;
						 ret.insid = insid ;
						 var pm = {
									type : 'post',
									url : "./sim_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								refresh_me();
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


function add_or_edit_dev(tp,tpt,chid,id)
{
	var tt = "Add ["+tpt+"] Device In Channel";
	if(id)
	{
		tt = "Edit ["+tpt+"] Device In Channel";
	}
	if(id==null)
		id = "" ;
	dlg.open(tp+"_dev_edit.jsp?insid="+insid+"&chid="+chid+"&devid="+id,
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
						 
						 ret.op="dev_add" ;
						 if(id)
							 ret.op = "dev_edit";
						 ret.chtp = tp ;
						 ret.insid = insid ;
						 ret.chid = chid ;
						 ret.devid = id ;
						 var pm = {
									type : 'post',
									url : "./sim_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								refresh_me();
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

function dev_setup(tp,tpt,chid,id)
{
	event.preventDefault();
	var tt = "Setup ["+tpt+"] Device In Channel";
	
	//document.getElementById("rightf").src = tp+"_dev_setup.jsp?insid="+insid+"&chid="+chid+"&devid="+id;
	//if(true)
	//	return ;
	dlg.open(tp+"_dev_setup.jsp?insid="+insid+"&chid="+chid+"&devid="+id,
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
						 
						 ret.op="dev_setup" ;
						 ret.chtp = tp ;
						 ret.insid = insid ;
						 ret.chid = chid ;
						 ret.devid = id ;
						 var pm = {
									type : 'post',
									url : "./sim_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								refresh_me();
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

function dev_del(chid,devid)
{
	layer.confirm('delete selected device?', function(index)
		    {
		    	send_ajax("sim_ajax.jsp","insid="+insid+"&op=dev_del&chid="+chid+"&devid="+devid,function(bsucc,ret){
		    		if(bsucc&&ret=='succ')
		    		{
		    			refresh_me();
		    		}
		    		else
		    			layer.msg("del err:"+ret) ;
		    	}) ;
		      
		      
		    });
}


function ch_start_stop(b,chid)
{
	var op = "ch_start" ;
	if(!b)
		op = "ch_stop";
	$.ajax({
        type: 'post',
        url:'sim_ajax.jsp',
        data: {op:op,insid:insid,chid:chid},
        async: true,  
        success: function (result) {  
        	if("succ"==result)
        	{
        		refresh_me();
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

function dev_rt(tp,chid,id)
{
	
	document.getElementById("rightf").src = tp+"_rt.jsp?insid="+insid+"&chid="+chid+"&devid="+id;	
}

</script>

</body>
</html>