<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="com.dw.user.*,com.dw.system.*,
		com.dw.user.*,org.json.*,
		com.iottree.platform.dev_org.*,
		com.dw.system.dict.*,
		org.w3c.dom.*,com.dw.web_ui.*,java.util.*,com.dw.system.xmldata.*" %><%@ taglib uri="wb_tag" prefix="wbt"%><%! 

%><%UserProfile up = UserProfile.getUserProfile(request) ;
boolean b_admin = up.isAdministrator() ;
boolean bedit = "true".equalsIgnoreCase(request.getParameter("edit")) ;

List<DevCatDD> catdds = DevPartManager.getInstance().listCatDD() ;
JSONArray jarr = new JSONArray() ;
for(DevCatDD catdd:catdds)
{
	jarr.put(catdd.toListJO()) ;
}

int dc_id = Convert.parseToInt32(request.getParameter("dc_id"), -1) ;
int dn_id = Convert.parseToInt32(request.getParameter("dn_id"), -1) ;
String dn_title="" ;

DevCatDD catdd = null;
DataNode dn = null ;
String top_title = "" ;
if(dc_id>=0)
{
	catdd = DevPartManager.getInstance().getCatDDById(dc_id) ;
	if(catdd==null)
	{
		out.print("no DevCatDD found with id="+dc_id) ;
		return ;
	}
	if(dn_id>0)
	{
		dn = catdd.getDataNodeById(dn_id) ;
	}
	
	if(dn!=null)
	{
		top_title = dn.getNameCN() ;
		dn_title = dn.getNameCN() ;
	}
}

boolean selonly="true".equals(request.getParameter("selonly")) ;
boolean b_wms = "true".equals(request.getParameter("wms")) ;
boolean selmulti = "true".equalsIgnoreCase(request.getParameter("selmulti")) ;%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp"></jsp:include>
    <style>
.layui-form-label{
    width: 120px;
}
.layui-input-block {
    margin-left: 140px;
    min-height: 36px;
    width:240px;
}
.layui-table-view
{
	margin-top: 1px;
}
  .layui-table-cell {
    height: auto;
    line-height: 18px;
}
td {border0:1px solid;}
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
		<td style="width:150px;padding-left:5px;color:#002cfd">部件/组件 列表 <span id="top_tt"><%=top_title %></span></td>
		<td style="padding:5px;width:30%;">
			<input class="layui-input" id="search_txt" onkeydown="on_search_key()"/>
      </td>
      <td style="padding:5px;width:20%;">
			<button id="top_oper_search" class="layui-btn layui-btn-sm layui-btn-primary" onclick="search_devpart()"><i class="fa fa-search"></i></button>
<%
if(!selonly)
{
%>
			<button id="top_oper_search" class="layui-btn layui-btn-sm layui-btn-primary" onclick="mv_selected()" title="&nbsp;移动部件"><i class="fa-solid fa-angles-right"></i></button>
<%
}
%>
      </td>
		<td style="text-align: right;padding-right:5px;width:100px;">
<%
if(!selonly)
{
%>
		<button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="add_dev_part(false)" title="&nbsp;新增部件"><i class="fa fa-plus"></i></button>
		<button id="top_oper_cp_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="add_dev_part(true)" title="&nbsp;复制新增部件"><i class="fa-solid fa-copy"></i><i class="fa fa-plus"></i></button>
		<button id="top_oper_imp" class="layui-btn layui-btn-sm layui-btn-primary" onclick="imp_devpart()" title="&nbsp;导入部件"><i class="fa-solid fa-file-import"></i></button>
		<button id="top_oper_imp" class="layui-btn layui-btn-sm layui-btn-primary" onclick="exp_devpart()" title="&nbsp;导出部件到Xls文件"><i class="fa-solid fa-arrow-right"></i><i class="fa-regular fa-file-excel"></i></button>
<%
}
%>
		</td>
	</tr>
</table>
</form>
<div style="position:absolute ;width:90%">
<table id="dev_part_list"  lay-filter="dev_part_list"  lay-size="sm" lay-even="true" style="top:1px;width:99%;">

</table>
</div>
<div style="position:absolute;width:10%;top:40px;right:0px;bottom:300px;border:1px solid #ececec;">
<div id="pics" class="pics">
	</div>

</div>
<script type="text/html" id="row_toolbar">
<div class="layui-btn-group">
<%
if(up.isAdministrator() && !selonly)
{
%>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit"><i class="fa fa-pencil"></i></button>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="set_inc"><i class="fa-solid fa-object-group"></i></button>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="setup"><i class="fa fa-gear"></i></button>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="barcode"><i class="fa-solid fa-barcode"></i></button>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary layui-border-red"  lay-event="del" title="delete"><i class="fa fa-times"></i></button>
<%
}
%>
<%--<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="deverr"><i class="fa-solid fa-screwdriver-wrench"></i></button>
--%>
</script>
<script>
var selonly = <%=selonly%>;
var selmulti = <%=selmulti%>;
var b_wms = <%=b_wms%>;
var b_admin = <%=b_admin%>;
var catdds = <%=jarr%>;
var bedit = <%=bedit%>;
var form ;
var table ;
var table_cur_page = 1 ;

var dc_id=<%=dc_id%>;
var dn_id=<%=dn_id%> ;
var dn_title = "<%=dn_title%>" ;

function add_dev_part(b_copy)
{
	if(b_copy)
	{
		if(!selected_part)
		{
			dlg.msg("请选择一个现有的部件");return ;
		}
		
		dlg.confirm(`确定要复制备部件[\${selected_part.title}]内容，新建一个部件么？`,{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"请确认"},function ()
		{
			edit_devpart("",selected_part.partid) ;
		});
		
	}
	else
	{
		edit_devpart("","") ;
	}
}


function edit_devpart(partid,cp_partid)
{//cp_partid will be used when add
	let undertt= "" ;
	if(dc_id && dn_id && dn_title)
		undertt = "在"+dn_title+"下 " ;
		let editt = undertt+`新增部件` ;
		let u = "dev_part_edit.jsp?";
		let op="add_devpart";
		if(partid)
		{
			op="edit_devpart";
			editt = undertt+`编辑部件` ;
			 u = "dev_part_edit.jsp?partid="+partid ;
		}
		
		dlg.open(u,{title:editt,w:'500px',h:'400px'},
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
							 send_ajax("dev_part_ajax.jsp",{op:op,partid:partid,...ret,dc_id:dc_id,dn_id:dn_id,cp_partid:cp_partid||""},(bsucc,ret)=>{
								 if(!bsucc || ret.indexOf("succ")!=0)
								 {
									 dlg.msg(ret) ;
									 return ;
								 }
								 dlg.close() ;
								 refresh_table(true);
							 }) ;
					 	});
					},
					function(dlgw)
					{
						dlg.close();
					}
				]);
}

function imp_devpart()
{
	let undertt= "" ;
	if(dc_id && dn_id && dn_title)
		undertt = "在"+dn_title+"下 " ;
	let impt = undertt+`导入部件` ;
		
	dlg.open("dev_part_imp.jsp",{title:impt,w:'500px',h:'400px'},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 let pm={op:"imp_devpart",txt:ret,dc_id:dc_id,dn_id:dn_id};
						 dlg.loading(true)
						 send_ajax('./dev_part_ajax.jsp',pm,function(bsucc,ret)
							{
							 dlg.loading(false)
								if(!bsucc || ret.indexOf('succ=')<0)
								{
									dlg.msg(ret);
									return ;
								}
								dlg.msg("导入数量 "+ret.substring(5)) ;
								dlg.close();
								refresh_table(true);
							},false);
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
]);
}

function exp_devpart()
{
	window.open("dev_part_export.jsp?fmt=csv") ;
}

function render_tb()
{
	
	  let cols = [];
	 if(selonly && !b_wms)
	 {
		 if(selmulti)
			 	cols.push({ type:"checkbox", width:"5%", align: "center" });
		 cols.push({width:"5%", align: "center" ,templet:function(res){
			 return "<i class='fa fa-cube'></i>";
		 }});
		 cols.push({field: 'code', title: '部件编码', width:'10%'});
		 cols.push({field: 't', title: '部件标题', width:'45%'});
		 cols.push({field: 'fac_brand', title: '品牌&厂家', width:'10%'});
		 cols.push({field: 'model', title: '型号', width:'20%'});
		 cols.push({ title: '包含', width:'5%',templet:function(res){
				 let ret = `<span >\${res.inc_num}</span>`;
				 return ret;
		 }});
	 }
	 else if(b_wms)
	 {
		 cols.push({field: 't', title: '部件标题', width:'40%'});
		 cols.push({field: 'fac_brand', title: '品牌&厂家', width:'10%'});
		 cols.push({field: 'model', title: '型号', width:'20%'});
		 cols.push({field: 'unit', title: '单位', width:'10%'});
		 cols.push({field: 'mark_tpt', title: '存储方式', width:'20%'});
	 }
	 else
	 {
		 //if(selmulti)
		 cols.push({ type:"checkbox", width:"5%", align: "center" });
		 cols.push({width:"4%", align: "center" ,templet:function(res){
			 return "<i class='fa fa-cube'></i>";
		 }});
		 cols.push({field: 'code', title: '部件编码', width:'5%'});
		 cols.push({field: 't', title: '部件标题', width:'30%'});
		 cols.push({field: 'fac_brand', title: '品牌&厂家', width:'5%'});
		 cols.push({field: 'model', title: '型号', width:'7%'});
		 cols.push({ title: '包含', width:'5%',templet:function(res){
			 let ret = `<span >\${res.inc_num}</span>`;
			 return ret;
		 }});
		 
		 cols.push({ title: '引用', width:'15%',templet:function(res){
			 let ret = `<button style="color:green" class="layui-btn layui-btn-xs layui-btn-primary" onclick="on_set_ref_partcls('\${res.id}','\${res.t}')">\${res.ref_cls_num}</button>
			 	\${res.ref_cls_tt}
			 `;
			 return ret;
		 }});
		 /*
			 for(let catdd of catdds)
			 {
				 cols.push({title: catdd.dc_name_cn, width:'8%',
					 templet:function(res){
					 	let tt = res['catddpt_'+catdd.dc_id]||"" ;
					 	if(b_admin && !selonly)
					 		return `<span title="\${tt}">\${tt}</span><span onclick="set_catdd('\${catdd.dc_id}','\${res.id}')" class='colicon'><i class="fa fa-pencil"></i></span>` ;
					 	else
					 		return `<span title="\${tt}">\${tt}</span>` ;
				 	}});
			 }*/
			 
			 cols.push({field: 'stt', title: '状态', width:'5%'});
			 //cols.push({field: 'life_tpt', title: '寿命类型', width:'10%'});
			// cols.push({field: 'life_h_t', title: '寿命小时', width:'10%'});
			 cols.push({field: 'Oper', title: '<wbt:g>oper</wbt:g>', width:"18%" ,toolbar: '#row_toolbar'}) ;
	 }
	 
	
	table.render({
	    elem: '#dev_part_list'
	    ,height: "full-40"
	    ,url: "dev_part_ajax.jsp?op=list_dev_parts&dc_id="+dc_id+"&dn_id="+dn_id
	    ,page: {layout:['prev', 'page', 'next'],limit:35,theme:"#c00"} //open page
	    ,cols: [cols]
	  ,parseData:function(res){
			if(res.data.length==0){
				return{
					'code':'201',
					'msg':'无部件内容'
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
	  
	  table.on('tool(dev_part_list)', function(obj){ // lay-filter="mc_acc_list"
		  var data = obj.data; //cur d
		  var lay_evt = obj.event; // lay-event
		  var tr = obj.tr; //tr DOM
		 
		  if(lay_evt === 'barcode'){ //
			  
			  dlg.msg("TODO") ;
		  }
		  else if(lay_evt==='set_inc')
		  {
			  set_inc(data.id);
		  }
		  else if(lay_evt==='setup')
		  {
			  do_setup(data.id);
		  }
		  else if(lay_evt==='deverr')
		  {
			  set_deverr(data);
		  }
		  else if(lay_evt === 'del')
		  {
			  del_devpart(data.id);
		  }
		  else if(lay_evt === 'edit')
		  {
			  edit_devpart(data.id) ;
		  }
		});
	  
	  table.on('row(dev_part_list)', function(obj)
			  {
		  var trs = $(".layui-table-body.layui-table-main tr");
		  trs.each(function(){
			  $(this).removeClass("seled") ;
		  })
		  obj.tr.addClass("seled");
				  var data = obj.data; //cur d
				  	on_sel_single_part(data.id,data.t)
			  });
	  table.on("checkbox(dev_part_list)",function(obj){
		  if(selmulti)
			  on_sel_multi_part() ;
	  }) ;
	  refresh_table(true);
}

function refresh_table()
{
	let search_txt = $("#search_txt").val()||"" ;
	table.reload("dev_part_list",{ url:"dev_part_ajax.jsp?search_txt="+search_txt+"&op=list_dev_parts&dc_id="+dc_id+"&dn_id="+dn_id,page:{curr:1}});
	//table.reload("dev_part_list",{});
}

function search_devpart()
{
	let search_txt = $("#search_txt").val()||"" ;
	//refresh_table();
	$("#top_tt").html("搜索结果") ;
	table.reload("dev_part_list",{url:"dev_part_ajax.jsp?search_txt="+search_txt+"&op=list_dev_parts&dc_id="+(-1)+"&dn_id="+dn_id,page:{curr:1}});
}
function on_search_key()
{
	if(event.keyCode==13)
		search_devpart() ;
}

function del_devpart(partid)
{
	dlg.confirm('确定要删除此设备部件么?',{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
	{
		dlg.loading(true);
		send_ajax("dev_part_ajax.jsp",{op:"del_devpart",partid:partid,del:true},function(bsucc,ret){
			dlg.loading(false) ;
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

function on_set_ref_partcls(partid,tt)
{
	dlg.open("dev_part_ref_cls.jsp?selonly=true&partid="+partid,{title:`设置部件\${tt}引用类别`,w:'500px',h:'400px'},
			['确定','取消'],
			[
				function(dlgw)
				{
					let str = dlgw.get_selected_partcls_ids();
					 //console.log(ret) ;
					 send_ajax("dev_part_ajax.jsp",{op:"set_part_ref_clss",partid:partid,partcls_ids:str},(bsucc,ret)=>{
						 if(!bsucc || ret.indexOf("succ")!=0)
						 {
							 dlg.msg(ret) ;
							 return ;
						 }
						 dlg.close() ;
						 refresh_table(true);
					 }) ;
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

var selected_part= null ;

function on_sel_single_part(id,t)
{
	selected_part = {partid:id,title:t} ;
	
	if(b_wms && parent && parent.on_devpart_sel)
		parent.on_devpart_sel(id,t) ;
		
	//$("#btn_pic_add").css('display',"") ;
	refresh_pics();
	//$("#if_sups")[0].contentWindow.set_partid(selected_part.partid) ;
	//$("#prop_main").attr("src","dev_part_props.jsp?partid="+id) ;
	if(selonly && !selmulti)
		dlg.set_over_dlg({title:"<span style='color:red'>已经选择部件:"+t+"</span>"});
}

function on_sel_multi_part()
{
	if(!(selonly && selmulti))
		return ;
	let ids = get_multi_selected_ids() ;
	dlg.set_over_dlg({title:"<span style='color:red'>已经选择部件数:"+ids.length+"</span>"});
}

function get_multi_selected_ids()
{
	let ids=[] ;
	let seldd = table.checkStatus("dev_part_list").data;
	if(seldd&&seldd.length>0)
	{
		for(let d of seldd)
		{
			ids.push(d.id) ;
		}
	}
	
	let seled = get_selected_part();
	if(seled)
	{
		if(ids.indexOf(seled.partid)<0)
			ids.push(seled.partid) ;
	}
	return ids; 
}

function get_selected_multi_parts()
{
	return table.checkStatus("dev_part_list").data;
}

function get_selected_part()
{
	return selected_part;
}

function set_inc(partid)
{
	event.stopPropagation();
	dlg.show_over_dlg(true,"dev_part_inc.jsp?partid="+partid,{ratio:"90%",h:false,title:"部件包含管理"});
}

function do_setup(partid)
{
	event.stopPropagation();
	dlg.show_over_dlg(true,"dev_part_setup.jsp?partid="+partid,{ratio:"90%",h:false,title:"部件详细设置"});
}

function set_deverr(d)
{
	event.stopPropagation();
	console.log(d) ;
	dlg.show_over_dlg(true,"../dev_err/dev_err_main.jsp?cuid="+d.cuid,{ratio:"90%",h:false,title:"部件["+d.t+"]故障分析及管理"});
}

function set_catdd(dc_id,partid)
{
	dlg.open("../dev_org/dlg_tree.jsp?dc_tp=dev_part&dc_id="+dc_id,{title:"添加引用某个类型设备",w:'500px',h:'400px'},
			['确定','取消'],
			[
				function(dlgw)
				{
					let sel = dlgw.get_selected();
					//console.log(sel) ;
					if(!sel)
					{
						dlg.msg("请选择具体的某个分类,选择根表示去除分类");
						return ;
					}
					
					let dn_id=0 ;
					if(sel.tp=='dn')
					{
						dn_id=sel.nid ;
					}
					
					send_ajax("dev_part_ajax.jsp",{op:"set_catdd",partid:partid,dc_id:dc_id,dn_id:dn_id},(bsucc,ret)=>{
						if(!bsucc || ret!="succ")
						{
							dlg.msg(ret) ;
							return ;
						}
						dlg.close() ;
						 refresh_table();
					});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function mv_selected()
{
	let ids=get_multi_selected_ids() ;
	
	if(ids.length<=0)
	{
		dlg.msg("请在列表中选择需要移动的内容");return ;
	}
	
	dlg.open("../dev_org/dlg_tree.jsp?dc_tp=dev_part&dc_id="+dc_id,{title:"移动到分类",w:'500px',h:'400px'},
			['确定','取消'],
			[
				function(dlgw)
				{
					let sel = dlgw.get_selected();
					//console.log(sel) ;
					if(!sel)
					{
						dlg.msg("请选择具体的某个分类,选择根表示去除分类");
						return ;
					}
					
					let dn_id=0 ;
					if(sel.tp=='dn')
					{
						dn_id=sel.nid ;
					}
					
					let idstr = ids.join(',') ;
					send_ajax("dev_part_ajax.jsp",{op:"set_catdd_multi",partids:idstr,dc_id:dc_id,dn_id:dn_id},(bsucc,ret)=>{
						if(!bsucc || ret.indexOf("succ=")!=0)
						{
							dlg.msg(ret) ;
							return ;
						}
						let resn = parseInt(ret.substring(5)) ;
						if(resn<=0)
						{
							dlg.msg("没有记录被移动新！");return ;
						}
						dlg.close() ;
						 refresh_table();
					});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

layui.use(['table','form'], function()
		{
	form = layui.form;
	  
	  //form.on('select(lib_list)', function(obj){
		//      on_lib_chg();
		//  });
	  
		  table = layui.table;
		  render_tb() ;
		});


function set_cat_dd(dcid,dnid,tt)
{
	
	dc_id = dcid ;
	dn_id = dnid ;
	dn_title = tt ;
	$("#top_tt").html(tt) ;
	refresh_table() ;
}


function refresh_pics()
{
	if(!selected_part)
		return ;
	send_ajax("dev_part_ajax.jsp",{op:"list_part_pics",partid:selected_part.partid},(bsucc,ret)=>{
		if(!bsucc||ret.indexOf("[")!=0)
		{
			dlg.msg(ret) ;
			return ;
		}
		let pics ;
		eval("pics="+ret) ;
		let tmps ="" ;
		for(let pic of pics)
		{
			tmps += `
				<div class="pic_item">
				<span style="position: absolute;top:2px;right:0px;">
				</span>
				<img src="dev_part_pic.jsp?partid=\${pic.partid}&picid=\${pic.picid}"  style=""/>
				<div class="txt">
					\${pic.filen}
				</div>
			</div>
			` ;
		}
		$("#pics").html(tmps) ;
	}) ;
}


function fit_height()
{
	var hpx =($(window).height()-80);
	//dlg.msg(hpx) ;
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