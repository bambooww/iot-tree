<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.store.record.*,
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
RecManager recm = RecManager.getInstance(prj) ;

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


table, th, td
{
border:1px solid;
}
th
{
	font-size: 12px;
	font-weight: bold;
}
td
{font-size: 12px;
}


.left_t
{
	position:absolute;
	left:0px;
	top:0px;
	bottom:30%;
	width:45%;
	border:0px solid;
}

.left_b
{
	position:absolute;
	left:0px;
	top:70%;
	bottom:8px;
	width:45%;
	border:0px solid;
}

.right
{
	position:absolute;
	left:45%;
	top:0px;
	bottom: 8px;
	right:0px;
	border:1px solid;
	margin-left:3px;
}

.blk_top
{
	position:relative;
	left:0px;
	top:0px;
	right:0px;
	width:100%;
	height:50px;
	font-size:18px;
	background-color: #f2f2f2;
	
}
.blk_c
{
	position:relative;
	left:0px;
	top:0px;
	right:0px;
	width:100%;
	height:calc(100% - 43px);
	border:0px solid;
	border-color: red;
	overflow-y:auto;
	overflow-x: hidden;
}

.sel
{
background-color: #0078d7;
color:#ffffff;
}


.p_item
{
	position:relative;
	left:2%;
	width:95%;
	height:60px;
	border:1px solid;
	margin-top: 5px;
	margin-bottom: 5px;
}

.p_item .t
{
	position:absolute;
	font-size: 18px;
	top:2px;
	left:60px;
}

.p_item img
{
position:absolute;
	top:5px;
	left:5px;
	border:1px solid;
}
.p_item .rt
{
	position:absolute;
	font-size: 15px;
	top:2px;
	right:76px;
}


.p_item .f
{
	position:absolute;
	font-size: 15px;
	left:63px;
	color:#00988b;
	cursor:pointer;
	bottom:7px;
}
.p_item .s
{
	position:absolute;
	font-size: 15px;
	left:140px;
	color:#00988b;
	cursor:pointer;
	bottom:7px;
}
.p_item .save_btn
{
	position:absolute;
	font-size: 15px;
	right:100px;
	color:#00988b;
	cursor:pointer;
	visibility:hidden;
	top:0px;
}

.enable_c
{
	font-size: 15px;
}

.p_item .oper
{
	position:absolute;
	font-size: 15px;
	right:10px;
	top:7px;
}
</style>
<body marginwidth="0" marginheight="0">


<div class="left_t">
     <div class="blk_top">
      <blockquote class="layui-elem-quote "><wbt:g>tags</wbt:g>
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">

 </div>
</blockquote>
</div>
     <div class="blk_c" id="tags_list">
     <table style="width:100%;border:0px" class='besel'>
    		<thead>
    			<tr style="background-color: #f0f0f0">
    				<td width="30%"><wbt:g>tag</wbt:g></td>
    				<td width="30%"><wbt:g>title</wbt:g></td>
    				<td width="30%"><wbt:g>addr</wbt:g></td>
    				<td width="20%"><wbt:g>val,type</wbt:g></td>
    			</tr>
    		</thead>
    		<tbody id="tag_tb_body" style="height0:390px">
<%
for(RecTagParam rtp:recm.getRecTagParams().values())
{
	UATag tag = rtp.getUATag() ;
	String tagid = tag.getId() ;
	String tagp = tag.getNodeCxtPathInPrj() ;
	String addr = tag.getAddress() ;
	if(addr.length()>15)
		addr = addr.substring(0,15)+"..." ;
	UAVal.ValTP vt = tag.getValTp() ;
%>
<tr id="tag_<%=tagid%>" tagid="<%=tagid %>" tagp="<%=tagp%>" class="tag_row">
    				<td><%=tagp %></td>
    				<td><%=tag.getTitle() %></td>
    				<td title="<%=tag.getAddress() %>"><%=addr %></td>
    				<td><%=vt %></td>
    			</tr>
<%
}
%>
    		</tbody>
		</table>
     </div>
  </div>
 <div class="left_b" >
     <div class="blk_top">
     <blockquote class="layui-elem-quote ">UI <wbt:g>temps</wbt:g>
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
	
 </div>
</blockquote>
</div>
     <div class="blk_c" id="temps_list">
     	<table style="width:100%;border:0px" class='besel'>
    		<thead>
    			<tr style="background-color: #f0f0f0">
    			<td width="10%"></td>
    				<td width="20%"><wbt:g>name</wbt:g></td>
    				<td width="30%"><wbt:g>title</wbt:g></td>
    				<td width="20%"><wbt:g>tag,num</wbt:g></td>
    				<td width="20%"><wbt:g>size</wbt:g></td>
    			</tr>
    		</thead>
    		<tbody id="temps_tb_body">
    		
    		</tbody>
    	</table>
     </div>
  </div>
<div class="right" >
  <div class="blk_top">
  <blockquote class="layui-elem-quote ">UI <wbt:g>items</wbt:g>
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
	<button id="btn_add_item" type="button" class="layui-btn layui-btn-sm layui-border-blue layui-btn-disabled" onclick="add_or_edit_item(null)">+<wbt:g>add</wbt:g></button>
 </div>
 </blockquote></div>
  <div class="blk_c" id="items_list">
  
  </div>
</div>
<script>
var prjid = "<%=prjid%>" ;

var cur_tagids = null ;
var cur_tempn = null ;

var items = null ;
function update_items()
{
	send_ajax("ui_ajax.jsp",{op:"list_items",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		eval("items="+ret) ;
		update_items_ui();
	}) ;
}

function update_items_ui()
{
	let tmps = "" ;
	if(items!=null)
	{
		for(let ob of items)
		{
			let en_c = ob.en?"green":"gray" ;
			let en_t = ob.en?"<wbt:g>enabled</wbt:g>":"<wbt:g>disabled</wbt:g>" ;
			let y = 0 ;
			tmps += `<div id="p_\${ob.id}" style0="top:\${y}px" class="p_item" hid="\${ob.id}" n="\${ob.n}" t="\${ob.t}" tp="\${ob.tp}" onclick="on_pro_clk(this)" alert_uids="\${ob.alert_uids}">
			    <img src="\${ob.icon||''}" width="50" height="50" style="border:1px solid;"/>
				<span class="t">\${ob.t} [\${ob.n}]</span>
				
				<span class="f" ><i class="fa fa-gear" style="font-size:16px;"></i>111</span>

				
				<span class="oper">
					<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_or_edit_item('\${ob.id}')"><i class="fa fa-pencil"></i></button>
					<button type="button" class="layui-btn layui-btn-xs layui-btn-danger" onclick="del_item('\${ob.id}')" title="<wbt:g>del</wbt:g>"><i class="fa-regular fa-rectangle-xmark"></i></button>
				</span>

				</div>` ;
		}
	}
	$("#items_list").html(tmps) ;
}

update_items() ;

$(".tag_row").click(function(){
	event.stopPropagation();
	$(".tag_row").removeClass("sel") ;
	$(this).addClass("sel") ;
	let tagid = $(this).attr("tagid");
	cur_tagids = [tagid] ;
	
	update_temps();
});


var cur_temp_jos = [] ;

function update_temps()
{
	if(!cur_tagids || cur_tagids.length<=0)
		return ;
	send_ajax("ui_ajax.jsp",{op:"list_temps",prjid:prjid,tagids:cur_tagids.join(',')},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		eval("cur_temp_jos="+ret) ;
		update_temps_ui() ;
	}) ;
}

function update_temps_ui()
{
	if(!cur_temp_jos || cur_temp_jos.length<=0)
		return ;
	
	let tmps ="" ;
	for(let tmpjo of cur_temp_jos)
	{
		let tag_n = "("+tmpjo.min_tag_n+","+tmpjo.max_tag_n+")" ;
		tmps += `<tr class="temp_row" tempn="\${tmpjo.n}" title="\${tmpjo.d}" onclick="on_temp_row_clk(this)">
		    <td><img src="\${tmpjo.icon||''}" style="width:25px;height:25px"/></td>
			<td>\${tmpjo.n}</td>
			<td>\${tmpjo.t}</td>
			<td>\${tag_n}</td>
			<td>\${tmpjo.w} X \${tmpjo.h}</td>
			</tr>`;
	}
	$("#temps_tb_body").html(tmps) ;
	cur_tempn = null ;
	$("#btn_add_item").addClass("layui-btn-disabled");
}

function on_temp_row_clk(tr)
{
	event.stopPropagation();
	$(".temp_row").removeClass("sel") ;
	$(tr).addClass("sel") ;
	let tempn = $(tr).attr("tempn");
	cur_tempn = tempn ;
	
	$("#btn_add_item").removeClass("layui-btn-disabled");
}

$("#tags_list").click(function(){
	$(".tag_row").removeClass("sel") ;
	$("#temps_tb_body").html("") ;
	cur_tagids = null ;
	cur_tempn = null ;
	$("#btn_add_item").addClass("layui-btn-disabled");
}) ;
$("#temps_list").click(function(){
	$(".temp_row").removeClass("sel") ;
	cur_tempn = null ;
	$("#btn_add_item").addClass("layui-btn-disabled");
}) ;


function add_or_edit_item(id)
{
	event.stopPropagation();
	
	let tagids = cur_tagids ;
	let tempn = cur_tempn ;
	
	let tagids_str = "" ; 
	let tt = "<wbt:g>add,ui,item</wbt:g>";
	if(id)
	{
		tt = "<wbt:g>edit,ui,item</wbt:g>";
		tagids_str="";
		tempn="" ;
	}
	else
	{
		
		if(!tagids||tagids.length<=0||!tempn)
		{
			dlg.msg("<wbt:g>pls_sel_tag_uitemp_fst</wbt:g>");
			return ;
		}
		tagids_str=tagids.join(',') ;
	}
	
	
	if(id==null)
		id = "" ;
	dlg.open("ui_item_edit.jsp?prjid="+prjid+"&tagids="+tagids_str+"&tempn="+tempn+"&id="+id,
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
						 
						 ret.id = id ;
						 send_ajax("./ui_ajax.jsp",{op:"set_item",prjid:prjid,jstr:JSON.stringify(ret)},(bsucc,rrr)=>{
							 if(!bsucc || rrr!='succ')
							 {
								 dlg.msg(rrr) ;
								 return ;
							 }
							 update_items();
							 dlg.close();						 
						 }) ;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function del_item(id)
{
	if(event)
		event.stopPropagation();
	let ob = $("#p_"+id);
	dlg.confirm('<wbt:g>del,this,ui,item</wbt:g> ['+ob.attr("n")+']?',{btn:['<wbt:g>yes</wbt:g>','<wbt:g>cancel</wbt:g>'],title:"<wbt:g>del,confirm</wbt:g>"},function ()
		    {
					send_ajax("ui_ajax.jsp",{op:"del_item",prjid:prjid,id:id},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
			    			return ;
			    		}
						update_items();
			    	}) ;
				});
}
</script>

</body>
</html>