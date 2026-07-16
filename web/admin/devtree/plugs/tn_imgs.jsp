<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,org.json.*,
	java.util.*,
	java.net.*,org.iottree.core.devtree.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request, out, "treeid"))
	return ;

String treeid = request.getParameter("treeid") ;
String tree_nid = request.getParameter("tree_nid") ;
DTTree tree = DTTreeManager.getInstance().getTreeById(treeid);
if(tree==null)
{
	out.println("no device tree found") ;
	return ;
}
String title = "" ;
DTNode node = null;
JSONArray jarr = new JSONArray() ;
if(Convert.isNotNullEmpty(tree_nid))
{
	node = tree.findNodeById(tree_nid);
	if(node==null)
	{
		out.print("no tree node found") ;
		return ;
	}
	title = node.getTitle() ;
	LinkedHashMap<String,DTRunTag> tags_map = node.getRunTagsMap() ;
	
	for(DTRunTag rt:tags_map.values())
	{
		jarr.put(rt.toJO(true)) ;
	}
}

if(node==null)
{
	response.sendRedirect("tn_null.jsp") ;
	return;
}
%>
<html>
<head>
<title></title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style>
body {background-color: #fff;}
table {table-layout: fixed;width:100%;border:1px solid #ccc;}
.layui-table-view .layui-table td, .layui-table-view .layui-table th {
    padding: 1px 0;
}
.seled {color:green;font-weight: bold;}
</style>
</head>
<body style="overflow:hidden;">
<div class="toolbar" style="width:100%;height:30px;line-height:30px;border:1px solid #ccc;text-align: center;">
	<wbt:g>node</wbt:g>:[<%=title %>] <wbt:g>pics</wbt:g>
	<button id="" class="layui-btn layui-btn-xs layui-btn-primary" onclick="add_runblk_ins()" title="&nbsp;新增计算模块"><i class="fa fa-plus"></i></button>

</div>
<table id="pics_list"  lay-filter="pics_list"  lay-size="sm" lay-even="true" style="top:1px;width:99%;">

</table>

</body>
<script type="text/html" id="rb_toolbar">
<div class="layui-btn-group">
	<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit"><i class="fa-solid fa-pencil"></i></button>
  <button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="del"><i class="fa-solid fa-times"></i></button>
  </div>
</script>
<script type="text/javascript">
var treeid= "<%=treeid%>";
var tree_nid = "<%=tree_nid%>" ;
var tags_list = <%=jarr%>;
var form;
var table;
layui.use(['form','table'], function(){
	  form = layui.form;
	  form.on('select(lib_ids)', function(obj){
		   refresh_table()
	   });
	  
	  form.render();
	  
	  table = layui.table;
	  
	  render_tb();
});


function render_tb()
{
	  let cols = [];
	 cols.push({title: '<wbt:g>title</wbt:g>', width:'45%',templet:function(res){
		 if(res.ready)
		{
			 let runner_ex = "" ;
			 if(res.runner && res.blk_ioprop_all_set!=true)
				 runner_ex = `<span style="color:#ce8256">(有空属性)</span>`;
			 	return res.ins_title+`&nbsp;<span style="color:green">配置完备 \${runner_ex}</span>`;
	 	}
		else
		{
			 return res.ins_title+`&nbsp;<span style="color:red" title='\${res.err}'>配置缺失</span>`;
		}
			
	 }});
	 cols.push({field: 'runblk_tt', title: '<wbt:g>type</wbt:g>', width:'30%'});
	 cols.push({title: '<wbt:g>mode</wbt:g>', width:'15%',templet:function(res){
		 //console.log(res) ;
		 let ret =  res.m_t ;
		 if(res.min_intv>0)
			 ret += "&nbsp;"+res.min_intv+"ms" ;
		 return ret;
	 }});
	cols.push({field: 'Oper', title: '<wbt:g>oper</wbt:g>', width:"10%" ,toolbar: '#rb_toolbar'}) ;
	 
	table.render({
	    elem: '#runblk_ins_list'
	    ,height: "240px"
	    ,url: "../util/runblk_ajax.jsp?op=list_runblk_inss&treeid="+treeid+"&tree_nid="+tree_nid
	    ,page: false,even: true,limit:100000
	    ,cols: [cols],text:{none: 'No Data'}
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
	  
	  table.on('tool(runblk_ins_list)', function(obj){ // lay-filter="mc_acc_list"
		  var data = obj.data; //cur d
		  var lay_evt = obj.event; // lay-event
		  var tr = obj.tr; //tr DOM

		  if(lay_evt === 'del')
		  {
			  del_runblk_ins(data);
		  }
		  else if(lay_evt === 'edit')
		  {
			  edit_runblk_ins(data) ;
		  }
		  else if(lay_evt === 'up')
		  {
			  //updown_runblk(data,true) ;
		  }
		  else if(lay_evt === 'down')
		  {
			  //updown_runblk(data,false) ;
		  }
		});
	  table.on('row(runblk_ins_list)', function(obj)
			  {
		  var trs = $(".layui-table-body.layui-table-main tr");
		  trs.each(function(){
			  $(this).removeClass("seled") ;
		  })
		  obj.tr.addClass("seled");
				  var data = obj.data; //cur d
				  	on_sel_blk(data)
			  });
	  
	  //refresh_table(true);
}

function refresh_table()
{
	table.reload("runblk_ins_list",{ url: "../util/runblk_ajax.jsp?op=list_runblk_inss&treeid="+treeid+"&tree_nid="+tree_nid});
	parent.reload_tree();
}

function add_runblk_ins()
{
	let editt = `<wbt:g>add,runblk</wbt:g>-<wbt:g>sel,runblk_tp</wbt:g>` ;
	let u = "../util/runblk_list.jsp?sel=true";
	dlg.open(u,{title:editt,w:'500px',h:'400px'},
			['<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlg.close();
				}
			],(ret)=>{
				if(!ret)
					return ;
				//console.log("add runblk ret",ret) ;
				new_runblk_ins(ret)
			});
}

function new_runblk_ins(runblk)
{
	dlg.open("../util/runblk_ins_basic_edit.jsp",{title:"<wbt:g>add,runblk</wbt:g> - "+runblk.tt,w:'500px',h:'400px'},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
		        	     {
							 dlg.msg(ret) ;return ;
		        	     }
						 ret.runblk_uid = runblk.uid;
						 let pm = {op:"set_runblk_ins_basic",add:true,treeid:treeid,tree_nid:tree_nid,jstr:JSON.stringify(ret)} ;
						 //console.log(pm) ;
						 send_ajax("../util/runblk_ajax.jsp",pm,(bsucc,ret)=>{
							 if(!bsucc || ret.indexOf("succ")!=0)
							 {
								 dlg.msg(ret) ;
								 return ;
							 }
							 dlg.close() ;
							 refresh_table();
						 }) ;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function edit_runblk_ins(runblk)
{
	if(event)
		event.stopPropagation();

	dlg.open("../util/runblk_ins_basic_edit.jsp",{title:"<wbt:g>edit,runblk</wbt:g> - "+runblk.tt,w:'500px',h:'400px',input:runblk},
			['<wbt:g>ok</wbt:g>','<wbt:g>cancel</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(bsucc,ret){
						 if(!bsucc)
		        	     {
							 dlg.msg(ret) ;return ;
		        	     }
						 ret.runblk_uid = runblk.uid;
						 let pm = {op:"set_runblk_ins_basic",treeid:treeid,tree_nid:tree_nid,jstr:JSON.stringify(ret)} ;
						 //console.log(pm) ;
						 send_ajax("../util/runblk_ajax.jsp",pm,(bsucc,ret)=>{
							 if(!bsucc || ret.indexOf("succ")!=0)
							 {
								 dlg.msg(ret) ;
								 return ;
							 }
							 refresh_table();
							 dlg.close() ;
						 }) ;
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function del_runblk_ins(runblk)
{
	if(event)
		event.stopPropagation();
	//console.log(runblk);
	dlg.confirm('<wbt:g>del,runblk</wbt:g>-'+runblk.ins_title,{btn:["<wbt:g>yes</wbt:g>","<wbt:g>cancel</wbt:g>"],title:"<wbt:g>del,confirm</wbt:g>"},function ()
		    {
					send_ajax("../util/runblk_ajax.jsp",{op:"del_runblk_ins",treeid:treeid,tree_nid:tree_nid,ins_name:runblk.ins_name},function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("<wbt:g>del,err</wbt:g>:"+ret) ;
			    			return ;
			    		}
			    		refresh_table();
			    	}) ;
		});
}




function resize_iframe_h()
{
	   var h = $(window).height() -330;
	   $("#if_blk_detail").css("height",h+"px");
}

var resize_cc = 0 ;
$(window).resize(function(){
	resize_iframe_h();
	
	});
resize_iframe_h()
</script>
</html>                                                                                                                                                                                                                            