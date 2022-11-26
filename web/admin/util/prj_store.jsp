<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.*,
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
PrjDataClass pdc = DictManager.getInstance().getPrjDataClassByPrjId(prjid) ;
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
 <blockquote class="layui-elem-quote ">Data Store List
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_edit_store('<%=prjid %>',null)">+Add Database</button>
 </div>
</blockquote>
<%
List<Store> sts = StoreManager.getInstance(prjid).listStores() ;

for(Store st:sts)
{
	String storeid = st.getId() ;
	String name = st.getName() ;
	String title = st.getTitle() ;
%>
<table class="layui-table" style="width:100%" id="tb_<%=storeid%>">
  <colgroup>
    <col width="20">
    <col width="200">
    <col>
  </colgroup>
  <thead>
    <tr onclick="show_or_hide('<%=storeid%>')" >
      <th >+</th>
      <th><%=name %></th>
      <th><%=title %></th>
      <th><%=st.getStoreTpTitle() %></th>
	  <th>
	  <a onclick="add_or_edit_dc('<%=prjid %>','<%=""%>')"><i title="Add Data Node" class="fa fa-plus fa-lg " aria-hidden="true"></i></a>
	  <a onclick="del_dc('<%=prjid %>','<%=""%>')"><i title="Delete Data Node" class="fa fa-times fa-lg " aria-hidden="true"></i></a>
                                         
      <a href="javascript:import_dc_txt('<%=prjid %>','<%=""%>')" title="Import by Txt">
          <i class="fa-solid fa-file-import"></i>
       </a>
	  </th>
    </tr> 
  </thead>
  <tbody id="bd_<%=storeid%>"  b_show="false" b_load="false">
   
  </tbody>
</table>
<%
}
%>

 	
<script>
var prjid = "<%=prjid%>" ;

function show_or_hide(cid,bshow)
{
	var bdo = $("#bd_"+cid) ;
	if(bdo.attr("b_load")!='true')
	{
		send_ajax("prj_store_ajax.jsp","prjid="+prjid+"&op=list_datamap&prjid="+prjid+"&cid="+cid,function(bsucc,ret){
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

function del_store(prjid,cid)
{
	event.stopPropagation();
	layer.confirm('Delete this Store?', function(index)
		    {
		    	send_ajax("prj_store_ajax.jsp","prjid="+prjid+"&op=del_dc&cid="+cid,function(bsucc,ret){
		    		if(bsucc&&ret=='succ')
		    			$("#tb_"+cid).remove();
		    		else
		    			layer.msg("del err:"+ret) ;
		    	}) ;
		      layer.close(index) ;
		    });
}

function export_task(prjid,taskid)
{
	window.open("prj_task_ajax.jsp?op=export&prjid="+prjid+"&taskid="+taskid) ;
}

function add_or_edit_store(prjid,id)
{
	event.stopPropagation();
	var tt = "Add Database connection";
	if(id)
	{
		tt = "Edit Database connection";
	}
	if(id==null)
		id = "" ;
	dlg.open("prj_store_edit_jdbc.jsp?prjid="+prjid+"&id="+id,
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
						 
						 ret.op="set_store" ;
						 ret.prjid=prjid;
						 ret.cid = id ;
						 ret.jstr = JSON.stringify(ret) ;
						 var pm = {
									type : 'post',
									url : "./prj_store_ajax.jsp",
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