<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %><%! 

%><%
//if(!Convert.checkReqEmpty(request, out, "house_id"))
//	return ;

//UserProfile up = UserProfile.getUserProfile(request) ;
//String house_id = request.getParameter("house_id") ;
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
    <style>
.layui-form-label{
    width: 120px;
}
.layui-input-block {
    margin-left: 140px;
    min-height: 36px;
    width:240px;
}

  .layui-table {
  margin:0px;
    font-size: 12px; 
    line-height0: 1.2; 
  }
  .layui-table th, .layui-table td {
    padding: 1px 2px;
  }
    .layui-table-view th, .layui-table-view td {
    padding: 1px 2px;
  }
  
 .cat {margin:5px;position: relative;border:1px solid #ccc;height:35px;min-width:200px;border-radius:3px;cursor:pointer;display:inline-block;}
 .cat:hover {background-color: #ccc;}
 .cat .t {position: absolute;left:5px;top:3px;font-size: 14px;font-weight: bold;}
 .cat .n {position: absolute;left:8px;bottom:1px;font-size: 12px;}
 
.pics {overflow-y:auto;}
.pic_item {border:0px solid;text-align: center;margin: 5px;position: relative;}
.pic_item img {width:100px;height:100px;border:1px solid #ececec;}
.colicon {cursor: pointer;}
    </style>
</head>
<body  style="overflow: hidden;">
<form class="layui-form"  onsubmit="return false;" >
<table style="width:100%;height:40px;border-bottom: 1px solid #e6e6e6;">
	<tr>
		<td style="width:150px;padding-left:5px;font-weight: bold;">页面列表 <span id="top_tt"></span></td>
<%-- 
		<td style="padding:5px;width:30%;">
			<input class="layui-input" id="search_txt" onkeydown="on_search_key()"/>
      </td>
      <td style="padding:5px;width:20%;">
			<button id="top_oper_search" class="layui-btn layui-btn-sm layui-btn-primary" onclick="search_devpart()"><i class="fa fa-search"></i></button>
			<button class="layui-btn layui-btn-sm layui-btn-primary" onclick="refresh_table()"><i class="fa fa-refresh"></i></button>
      </td>
      --%>
		<td style="text-align: right;padding-right:5px;width:250px;border:0px solid">
		<button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="edit_page_cat()" ><i class="fa fa-plus"></i>新增分类</button>
		<button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="edit_page()" ><i class="fa fa-plus"></i>新增页面</button>
		<button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="parent.show_page_list(false)" title="&nbsp;隐藏列表"><i class="fa-solid fa-angle-left"></i></button>
		</td>
		
	</tr>
</table>
</form>
<div style="position:relative ;width:100%;height:20%;overflow-y: auto;">
<%
for(PageCat p:PortalManager.getInstance().listPageCats().values())
{
%><span class="cat" onclick="on_sel_cat('<%=p.getName() %>','<%=p.getTitle() %>')">
	<span class="n"><%=p.getName() %></span>
	<span class="t"><%=p.getTitle() %></span>
</span>
<%	
}
%>
</div>
<div style="position:absolute ;top:20%;width:100%">
<table id="page_list"  lay-filter="page_list"  lay-even="true" class="layui-table" style="top:1px;width:99%;">

</table>
</div>
<script type="text/html" id="row_toolbar">
<div class="layui-btn-group">
<%

%>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit"><i class="fa fa-pencil"></i></button>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="show"><i class="fa-solid fa-paper-plane"></i></button>
&nbsp;<button type="button" class="layui-btn layui-btn-xs layui-btn-primary layui-border-red"  lay-event="del" title="delete"><i class="fa fa-times"></i></button>


<%

%>

<%--
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="setup"><i class="fa fa-gear"></i></button>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="deverr"><i class="fa-solid fa-screwdriver-wrench"></i></button>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary layui-border-red"  lay-event="del" title="delete"><i class="fa fa-times"></i></button>
--%>
</script>
<script>
var form ;
var table ;
var table_cur_page = 1 ;

var cur_page_cat_name = "" ;
var cur_page_cat_title = "" ;

layui.use(['table','form'], function()
{
	form = layui.form;
	table = layui.table;
	render_tb() ;
});

function on_sel_cat(n,t)
{
	cur_page_cat_name = n ;
	cur_page_cat_title = t ;
	refresh_table();
}

function edit_page_cat(catn,catt)
{//cp_partid will be used when add
	let tt =catn?"修改分类":"新增分类";
	let op = catn?"edit_page_cat":"add_page_cat";
	dlg.open("../util/n_t_d_edit.jsp",{title:tt,w:'500px',h:'400px',input:{name:catn||"",title:catt||""}},
				['确定','取消'],
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
							 send_ajax("page_ajax.jsp",{op:op,...ret},(bsucc,ret)=>{
								 if(!bsucc || ret.indexOf("succ")!=0)
								 {
									 dlg.msg(ret) ;
									 return ;
								 }
								 dlg.close() ;
								 location.reload();
							 }) ;
					 	});
					},
					function(dlgw)
					{
						dlg.close();
					}
				]);
}

function edit_page(pg)
{
	if(event)
		event.stopPropagation();
	let tt =pg?"修改页面":"新增页面";
	let op = pg?"edit_page":"add_page";
	let cat ;
	if(!pg)
	{
		if(!cur_page_cat_name)
		{
			dlg.msg("请选择分类") ;
			return ;
		}
		cat = cur_page_cat_name ;
		pg={}
	}
	else
	{
		cat = pg.cat ;
	}
	
	let id = pg.page_id||"";
	let name = pg.page_name||"";
	let pagett = pg.page_title||"";
	let templet_uid = pg.templet_uid||"" ;
	dlg.open("./page_edit.jsp",{title:tt,w:'500px',h:'400px',input:{pageid:id,name:name,title:pagett,templet_uid:templet_uid}},
				['确定','取消'],
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
							 send_ajax("page_ajax.jsp",{op:op,cat:cat,pageid:id,...ret},(bsucc,ret)=>{
								 if(!bsucc || ret.indexOf("succ")!=0)
								 {
									 dlg.msg(ret) ;
									 return ;
								 }
								 dlg.close() ;
								 location.reload();
							 }) ;
					 	});
					},
					function(dlgw)
					{
						dlg.close();
					}
				]);
}

function render_tb()
{
	  let cols = [];
	  cols.push({field: 'cat_title', title: '分类', width:'20%'});
	  cols.push({field: 'page_name', title: '名称', width:'20%'});
	 cols.push({field: 'page_title', title: '标题', width:'25%'});
	 cols.push({field: 'templet_title', title: '模板', width:'20%'});
	 cols.push({field: 'Oper', title: '<wbt:g>oper</wbt:g>', width:"15%" ,toolbar: '#row_toolbar'}) ;
	 
	table.render({
	    elem: '#page_list'
	    ,height: "full-40"
	    ,url: "page_ajax.jsp?op=list_pages&cat="+(cur_page_cat_name||"")
	    ,page0: {layout:['prev', 'page', 'next'],limit:25,theme:"#c00"} //open page
	    ,cols: [cols]
	  ,parseData:function(res){
			if(res.data.length==0){
				return{
					'code':'201',
					'msg':'无内容'
				};
			};
		}
	    ,done:function(res, curr, count){
		   	 table_cur_page = curr ;
		   	 var trs = $(".layui-table-body.layui-table-main tr");
		   	 if(res && res.data)
		   	 {
		   		for(var i = 0 ; i < res.data.length;i++)
		  		 {
		  		    //if(i%2==1)
			    	//	 trs.eq(i).css("background-color","#f2f2f2");
			     }
		   	 }
	   	 }
	  });
	  
	  table.on('tool(page_list)', function(obj){ // lay-filter="mc_acc_list"
		  var data = obj.data; //cur d
		  var lay_evt = obj.event; // lay-event
		  var tr = obj.tr; //tr DOM
		 
		  if(lay_evt === 'barcode'){ //
			  
			  dlg.msg("TODO") ;
		  }
		  
		  else if(lay_evt==='show')
		  {
			 show_page(data);
		  }
		  else if(lay_evt === 'del')
		  {
			  del_page(data);
		  }
		  else if(lay_evt === 'edit')
		  {
			  edit_page(data) ;
		  }
		});
	  
	  table.on('row(page_list)', function(obj)
			  {
		  var trs = $(".layui-table-body.layui-table-main tr");
		  trs.each(function(){
			  $(this).removeClass("seled") ;
		  })
		  obj.tr.addClass("seled");
				  var data = obj.data; //cur d
				  	on_sel_single(data)
			  });
	  table.on("checkbox(page_list)",function(obj){
		  if(selmulti)
			  on_sel_multi_part() ;
	  }) ;
	  //refresh_table(true);
}

function refresh_table()
{
	let search_txt = $("#search_txt").val()||"" ;
	table.reload("page_list",{ url:"page_ajax.jsp?search_txt="+search_txt+"&cat="+(cur_page_cat_name||"")+"&op=list_pages",page0:{curr:1}});
	//table.reload("dev_part_list",{});
}

function del_page(pg)
{
	if(event)
		event.stopPropagation();
	if(!pg)return ;
	let pageid = pg.page_id ;
	let cat = pg.cat ;
	dlg.confirm('确定要删除此页面么?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
		    {
					send_ajax("",{op:"del_page",pageid:pageid,cat:cat,del:true},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
			    			return ;
			    		}
			    		//
						location.reload();
			    	}) ;
		});
}
function show_page(pg)
{
	window.open(`/_portal/\${pg.cat}/\${pg.page_name}`);
}

var selected_item= null ;

function on_sel_single(item)
{
	//console.log(item) ;
	selected_item = item ;
	if(parent.on_page_sel)
		parent.on_page_sel(item) ;
	
}


function fit_height()
{
	var hpx =($(window).height()-80);
	$("#tab_bd").css("height",hpx+"px")
	$("#pics").css("height",(hpx-10)+"px")
}
fit_height();
$(window).resize(function(){
	fit_height();
});

//

</script>
</body>
</html>