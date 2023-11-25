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

.left
{
	position: absolute;
	left:0px;
	top:0px;
	width:75%;
	bottom:0px;
	overflow: auto;
	border:1px solid;
}
.right
{
	position: absolute;
	left:75%;
	top:0px;
	right:0px;
	bottom:0px;
	overflow: auto;
	border:1px solid;
}

.sor_item
{
	position:relative;
	left:10%;
	width:80%;
	height:80px;
	border:1px solid;
	margin-bottom: 10px;
}
.sor_item .n
{
	position:absolute;
	font-size: 18px;
	left:5px;
}
.sor_item .t
{
	position:absolute;
	font-size: 20px;
	top:18px;
	left:10px;
}
.sor_item .tp
{
	position:absolute;
	font-size: 25px;
	top:10px;
	right:10px;
}
.sor_item .tpt
{
	position:absolute;
	font-size: 15px;
	top:1px;
	right:5px;
}
.sor_item .oper
{
	position:absolute;
	font-size: 15px;
	right:10px;
	bottom:5px;
}

</style>
<body marginwidth="0" marginheight="0">

<div class="left">
 <blockquote class="layui-elem-quote ">Data Store List
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_edit_store('<%=prjid %>',null)">+Add Database</button>
 </div>
</blockquote>
<%
	List<Source> sts = StoreManager.getInstance(prjid).listSources() ;
	for(Source st:sts)
	{
		//String storeid = st.getId() ;
		String name = st.getName() ;
		String title = st.getTitle() ;
%>
<table class="layui-table" style="width:100%" id="tb_<%=name%>">
  <colgroup>
    <col width="20">
    <col width="200">
    <col>
  </colgroup>
  <thead>
    <tr onclick="show_or_hide('<%=name%>')" >
      <th >+</th>
      <th><%=name %></th>
      <th><%=title %></th>
      <th><%=st.getSorTpTitle()%></th>
	  <th>
	  <a onclick="add_or_edit_dc('<%=prjid %>','<%=""%>')"><i title="Add Data Node" class="fa fa-plus fa-lg " aria-hidden="true"></i></a>
	  <a onclick="del_dc('<%=prjid %>','<%=""%>')"><i title="Delete Data Node" class="fa fa-times fa-lg " aria-hidden="true"></i></a>
                                         
      <a href="javascript:import_dc_txt('<%=prjid %>','<%=""%>')" title="Import by Txt">
          <i class="fa-solid fa-file-import"></i>
       </a>
	  </th>
    </tr> 
  </thead>
  <tbody id="bd_<%=name%>"  b_show="false" b_load="false">
   
  </tbody>
</table>
<%
}
%>
</div>
<div class="right">
<blockquote class="layui-elem-quote ">Data Source
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_sor_sel('<%=prjid %>')">+Add</button>
 </div>
</blockquote>
 <div id="sor_list" style="align-content: center;">
 </div>
</div>

 	
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

function show_sors(objs)
{
	console.log(objs);
	let tmps="" ;
	for(let ob of objs)
	{
		tmps += `<div id="sor_\${ob.n}" class="sor_item" tp="\${ob.tp}" t="\${ob.t}" n="\${ob.n}">
					<span class="n">\${ob.n}</span>
					<span class="t">\${ob.t}</span>
					<span class="tp">\${ob.tp}</span>
					<span class="tpt">\${ob.tpt}</span>
					<span class="oper">
						<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="edit_sor('\${ob.n}')"><i class="fa fa-pencil"></i></button>
						<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_sor('\${ob.n}')" title="delete"><i class="fa-regular fa-rectangle-xmark"></i></button>
					</span>
					</div>` ;
	}
	$("#sor_list").html(tmps) ;
}

function update_sors()
{
	send_ajax("store_ajax.jsp",{op:"list_stores",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret);
			return;
		}
		let objs =null;
		eval("objs="+ret) ;
		show_sors(objs)
	});
}

update_sors();

function del_sor(n)
{
	event.stopPropagation();
	dlg.confirm('Delete this Source ['+n+']?', {btn:["Yes","Cancel"],title:"Delete Confirm"},function ()
	{
		send_ajax("store_ajax.jsp","prjid="+prjid+"&op=del_sor&n="+n,(bsucc,ret)=>{
    		if(!bsucc||ret!="succ")
    		{
    			dlg.msg(ret);
    			return ;
    		}
    		update_sors();
    	}) ;
	});
}

function export_task(prjid,taskid)
{
	window.open("prj_task_ajax.jsp?op=export&prjid="+prjid+"&taskid="+taskid) ;
}

function add_sor_sel(prjid)
{
	dlg.open("store_sor_sel.jsp?prjid="+prjid,
			{title:"Select Source Type"},
			['Cancel'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			],(ret)=>{
				if(!ret)
					return ;
				add_or_edit_source(prjid,ret.tp,ret.tt,null) ;
			});
}

function edit_sor(n)
{
	let ob = $("#sor_"+n);
	let tp = ob.attr("tp");
	let t = ob.attr("t");
	add_or_edit_source(prjid,tp,t,n)
}

function add_or_edit_source(prjid,tp,t,n)
{
	if(event)
		event.stopPropagation();
	tt = "Add Data Source - "+t;
	if(n)
	{
		tt = "Edit Data Source - "+t;
	}
	if(!n)
		n = "" ;
	dlg.open("store_sor_edit_"+tp+".jsp?prjid="+prjid+"&n="+n,
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
						 if(n)
						 	ret.name = n ;
						 ret.jstr = JSON.stringify(ret) ;
						 var pm = {
									type : 'post',
									url : "./store_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								
								dlg.close();
								update_sors();
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