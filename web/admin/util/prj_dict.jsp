<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%!

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

.left
{
	position: absolute;
	left:0px;width:50%;top:50px;
	bottom:0px;
	overflow: auto;
	border:0px solid #aaaaaa;
}

.right
{
	position: absolute;
	right:0px;width:50%;top:50px;
	bottom:0px;
	
	border:1px solid #aaaaaa;
}

.dc_item
{
	position: relative;
	margin-top:10px;
	margin-bottom: 5px;
	left:5%;
	width:90%;
	height:40px;
	border:1px solid #aaaaaa;
}

.dc_item .t
{
	position:absolute;
	font-size: 15px;
	top:2px;
	left:10px;
}

.dc_item .bs
{
	position:absolute;
	font-size: 12px;
	bottom:1px;
	left:10px;
}

.dc_item .op
{
	position:absolute;
	font-size: 15px;
	right:10px;
	bottom:7px;
}

.dc_item:hover {
	background:#aaaaaa;
}

.dn_top
{
	width:100%;
	height:30px;
	background:#aaaaaa;
}

.dn_item
{
	position: relative;
	margin-top:2px;
	margin-bottom: 2px;
	left:5%;
	width:90%;
	height:40px;
	border:1px solid #aaaaaa;
}

.dn_item .t
{
	position:absolute;
	font-size: 14px;
	top:1px;
	left:10px;
}

.dn_item .n
{
	position:absolute;
	font-size: 12px;
	bottom:1px;
	left:10px;
}

.dn_item .op
{
	position:absolute;
	font-size: 15px;
	right:10px;
	bottom:2px;
}

.dn_top
{
	position:relative;
	width:100%;
	height:30px;
	top:0px;
	font-size:15px;
	font-weight:bold;
	background:#f2f2f2;
}

.dn_list
{
	position:absolute;
	width:100%;
	top:40px;
	bottom:0px;
	overflow: auto;
}

.sel
{
	border-color:blue ;
}
</style>
<body marginwidth="0" marginheight="0">
 <blockquote class="layui-elem-quote "><w:g>dict,mgr</w:g>
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_edit_dc(null)">+<w:g>add,data,class</w:g></button>
 	
 	 <button class="layui-btn layui-btn-sm layui-border-blue"  onclick="import_task()">
							 <i class="fa-solid fa-file-import"></i>&nbsp;<w:g>import,data,class</w:g>
							</button>
							
 </div>
</blockquote>
 <div class="left">
<%
Collection<DataClass> dcs = pdc.getDataClassAll();
for(DataClass dc:dcs)
{
	String cid = dc.getClassId() ;
	String dc_name = dc.getClassName() ;
	String dc_title = dc.getClassTitle() ;
	DataClass.BindStyle bs = dc.getBindStyle() ;
%>
	<div class="dc_item" id="cid_<%=cid %>" dc_id="<%=cid %>" onclick="on_dc_clk(this)" is_input="<%=bs.isInput()%>" dc_title="<%=dc_title%>" dc_name="<%=dc_name %>" bs_title="<%=bs.getTitle() %>">
	  <span class="n"></span>
	  <span class="t"><%=dc_title %> - <%=dc_name %></span>
	  <span class="bs"> [<%=bs.getTitle() %>]</span>
	  <span class="op">
	  	<button class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_dc('<%=dc.getClassId()%>')" title="Edit Data Class"><i class="fa fa-pencil"></i></button>
	      <button class="layui-btn layui-btn-xs layui-btn-normal"  onclick="import_dc_txt('<%=dc.getClassId()%>')" title="Import by Txt"><i class="fa-solid fa-file-import"></i></button>
	      <button class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_dc('<%=dc.getClassId()%>')" title="<w:g>del,data,class</w:g>"><i class="fa-regular fa-rectangle-xmark"></i></button>
	  </span>
	</div> 
<%
}
%>
</div>
<div class="right" id="dn_list" style="">

</div>

 	
<script>
var prjid = "<%=prjid%>" ;

var cur_dc_id = null ;
var cur_dc_ob =null ;

function on_dc_clk(ele)
{
	let ob = $(ele) ;
	let cid = ob.attr('dc_id') ;
	cur_dc_id = cid ;
	cur_dc_ob = ob ;
	$(".dc_item").removeClass('sel') ;
	ob.addClass("sel") ;
	update_dns(cid,ob) ;
}

function update_dns(cid,dc_ob)
{
	if(!dc_ob)
		dc_ob = $("#cid_"+cid) ;
	send_ajax("prj_dict_ajax.jsp","prjid="+prjid+"&op=list_dns&prjid="+prjid+"&cid="+cid,function(bsucc,ret){
		if(!bsucc || ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let dns=null;
		eval("dns="+ret) ;
		show_dns(cid,dns,dc_ob);
	}) ;
}

function show_dns(cid,dns,dc_ob)
{
	if(dc_ob.attr("is_input")=='true')
	{
		let tmps=`<div style="width:90%;height:100px;border:2px solid #00ba7d;font-size:15px;">
			 \${dc_ob.attr("dc_title")} -\${dc_ob.attr("dc_name")} [\${dc_ob.attr("bs_title")}] <br>  <w:g>input_by_user</w:g>
			</div>`
		$("#dn_list").html(tmps) ;
		return ;
	}
	let tmps=`<div class="dn_top"> \${dc_ob.attr("dc_title")} - \${dc_ob.attr("dc_name")} [\${dc_ob.attr("bs_title")}] 
		 <button class="layui-btn layui-btn-xs " style="top:5px;right:5px;position: absolute;" onclick="add_or_edit_dn('\${cid}','')" title="<w:g>add,data,item</w:g>" style="position:absolute;right:10px;"><i class="fa fa-plus " aria-hidden="true"></i><w:g>add,data,item</w:g></button>
		</div><div class="dn_list">` ;
	for(let dn of dns)
	{
		let n = dn.n ;
		let t = dn.t ;
		tmps += `
			<div class="dn_item" dc_id="\${cid}" dn_n="\${n}">
			  <span class="n">\${n}</span>
			  <span class="t">\${t}</span>
			  <span class="op">
			  	<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_dn('\${cid}','\${n}')" title="Edit Data Class"><i class="fa fa-pencil"></i></button>
				<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_dn('\${cid}','\${n}')" title="Delete Data Node"><i class="fa-regular fa-rectangle-xmark"></i></button>
			  </span>
			</div> 
			`;
	}
	tmps +"</div>"
	$("#dn_list").html(tmps) ;
}

function del_dc(cid)
{
	event.stopPropagation();
	dlg.confirm('<w:g>del,this,data,class</w:g>?',{btn:["<w:g>yes</w:g>","<w:g>cancel</w:g>"],title:"<w:g>del,confirm</w:g>"},function ()
	{
    	send_ajax("prj_dict_ajax.jsp","prjid="+prjid+"&op=del_dc&cid="+cid,function(bsucc,ret){
    		if(bsucc&&ret=='succ')
    			$("#tb_"+cid).remove();
    		else
    			layer.msg("del err:"+ret) ;
    	}) ;
		dlg.close();
		location.reload();
	});
}

function add_or_edit_dc(id)
{
	event.stopPropagation();
	var tt = "<w:g>add,data,class</w:g>";
	if(id)
	{
		tt = "<w:g>edit,data,class</w:g>";
	}
	if(id==null)
		id = "" ;
	dlg.open("prj_dict_dc_edit.jsp?prjid="+prjid+"&id="+id,
			{title:tt},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="add_dc" ;
						 if(id)
							 ret.op = "edit_dc";
						 ret.prjid=prjid;
						 ret.cid = id ;
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
								location.reload();
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


function import_dc_txt(cid)
{
	event.stopPropagation();
	var tt = "<w:g>import,data,item</w:g>";
	dlg.open("prj_dict_dc_imp_txt.jsp?prjid="+prjid+"&cid="+cid,
			{title:tt},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
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
								location.reload();
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

function add_or_edit_dn(cid,n)
{
	event.stopPropagation();
	var tt = "<w:g>add,data,item</w:g>";
	if(n)
	{
		tt = "<w:g>edit,data,item</w:g>";
	}
	if(!n) n = "" ;
	dlg.open("prj_dict_dn_edit.jsp?prjid="+prjid+"&cid="+cid+"&name="+n,
			{title:tt},
			['<w:g>ok</w:g>','<w:g>cancel</w:g>'],
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
						 if(n)
							 ret.op = "edit_dn";
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
								update_dns(cid);
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

function del_dn(cid,name)
{
	event.stopPropagation();
	dlg.confirm('<w:g>del,this,data,item</w:g>?',{btn:["<w:g>yes</w:g>","<w:g>cancel</w:g>"],title:"<w:g>del,confirm</w:g>"},function ()
	{
    	send_ajax("prj_dict_ajax.jsp",{prjid:prjid,op:"del_dn",cid:cid,name:name},function(bsucc,ret){
    		if(!bsucc || ret!='succ')
    		{
    			dlg.msg("<w:g>del,err</w:g>:"+ret) ;
    			return;
    		}
    		update_dns(cur_dc_id,cur_dc_ob);
    	}) ;
	});
}

</script>

</body>
</html>