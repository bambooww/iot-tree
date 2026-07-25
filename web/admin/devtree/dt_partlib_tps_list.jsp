<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,org.iottree.core.devtree.*,
	org.iottree.core.comp.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%!

%><%
	
	List<DTDevPartLib> libs = DTDevPartManager.getInstance().listLibs() ;
if(libs==null||libs.size()<0)
{
	out.print("no lib set ");return;
}
	boolean bdlg = "true".equalsIgnoreCase(request.getParameter("dlg"));
	boolean bsel = "true".equalsIgnoreCase(request.getParameter("sel")) ;
	String libid = request.getParameter("libid") ;
	DTDevPartLib lib = null;
	if(Convert.isNullOrEmpty(libid))
	{
		if(!bsel)
		{
			out.print("no libid input") ;return ;
		}
		lib = libs.get(0) ;
		libid = lib.getLibId() ;
	}
	else
	{
		lib = DTDevPartManager.getInstance().getLibById(libid) ;
	}
	if(lib==null)
	{
		out.print("no lib found with id="+libid) ;
		return ;
	}

	JSONArray tp_jarr = new JSONArray() ;
	if(lib!=null)
	{
		for(DTDevPartTP tp:lib.listPartTPs())
		{
			tp_jarr.put(tp.toJO()) ;
		}
	}
	
	
	boolean bedit = "true".equals(request.getParameter("edit")) ;
%>
<html>
<head>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
</head>
<style>
body
{
	font-size: 12px;
}

table
{
font-size: 12px;
}

.layui-elem-field legend {
font-size:12px;
}

.layui-table td, .layui-table th {
    min-height: 22px;
    line-height: 18px;
    font-size: 12px;
    padding: 4px 15px;
}
.layui-border-blue
{
	border:1px solid #43a4fa ;
	color:#43a4fa;
}
.layui-border-red
{
	border:1px solid #ff7859 ;
	color:#ff7859;
}
.layui-table-cell {
padding-left:2px;
}

.layui-btn-group .layui-btn
{
	margin-left:5px;
}

.layui-form-checkbox
{
	margin-left:10px;
	padding-left:10px;
}

blockquote
{
	font-size: 12px;
}

.layui-elem-quote
{
	height0:22px;
}

.layui-btn-group .layui-btn-primary
{
	border:0px;
	
}
.layui-btn-group .layui-btn-primary:first-child
{
border-left:0px;
}

.layui-input, .layui-select, .layui-textarea {
    height: 30px;
}

.layui-tab
{
	margin-top:0px;
}

.layui-tab-title li {
font-size: 12px;
}

.layui-table-view
{
	margin-top: 1px;
}
  .layui-table-cell {
    height: auto;
    line-height: 18px;
}

.seled
{
background-color:#f2f2f2;
color:green;
font-weight: bold;
}


</style>
<link rel="stylesheet" href="/_js/selectmenu/selectmenu.css" />
<script src="/_js/selectmenu/selectmenu.min.js"></script>
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
        	margin-top: 30px;
        }
          .layui-table-cell {
            height: auto;
            line-height: 18px;
        }
    </style>
</head>
<body>
    <div style="float:left;margin-left:8px;margin-top:4px;">
      <table id="top_oper">
          <tr>
          <td style="padding-left: 5px;"><wbt:g>devpart,lib</wbt:g>:</td>
          <td style0="width:115px;">
          <form class="layui-form" action="" onsubmit="return false;">
           <select id="lib_ids" lay-filter="lib_ids" lay-ignore0 style="width:100px;">
<%
for(DTDevPartLib lib0:libs)
{
	String seled = lib0.getLibId().equals(libid)?"selected":"" ;
%>
<option value="<%=lib0.getLibId()%>" <%=seled %>><%=lib0.getTitle() %></option>
<%
}
%>
           </select>
             </form>
              </td>
              <td style="padding-left: 15px;">
<%
if(bedit)
{
%>
<button id="top_oper_add_tp" type="button" class="layui-btn layui-btn-xs layui-btn-primary" onclick="edit_parttp()"><i class="fa fa-plus"></i><wbt:g>add,parttp</wbt:g></button>
<%
}
%>
              </td>
          </tr>
      </table>
  </div>
<br>
<table id="tp_list"  lay-filter="tp_list"  lay-size="sm" lay-even="true">

</table>
  <script type="text/html" id="row_toolbar">
<div class="layui-btn-group">

  <button type="button" class="layui-btn layui-btn-xs layui-btn-normal" lay-event="edit"><i class="fa fa-pencil"></i></button>
  
<button type="button" class="layui-btn layui-btn-xs layui-btn-danger"  lay-event="del" title="delete"><i class="fa-regular fa-rectangle-xmark"></i></button>
  </div>
</script>
<script>

var bedit = <%=bedit%>;
var bsel = <%=bsel%>;
var form;
var table ;
var libid = "<%=libid%>" ;
var bdlg = <%=bdlg%>;

var tp_jarr = <%=tp_jarr%> ;

function reload_pg(lib_id)
{
	location.href=`dt_partlib_tps_list.jsp?libid=\${lib_id}&edit=\${bedit}&sel=\${bsel}`
}

layui.use(['form','table'], function(){
	  form = layui.form;
	  form.on('select(lib_ids)', function(obj){
		  libid = $("#lib_ids").val();
		  reload_pg(libid)
	   });
	  
	  form.render();
	  
	  table = layui.table;
	  
	  let cols = [];
	 if(bedit)
	 {
		 cols.push({field: 't', title: '<wbt:g>parttp,title</wbt:g>', width:'75%'});
		 cols.push({field: 'Oper', title: 'Oper', width:"20%" ,toolbar: '#row_toolbar'}) ;
	 }
	 else
	 {
		 cols.push({field: 't', title: '<wbt:g>parttp,title</wbt:g>', width:'95%'});
	 }
	  table.render({
	    elem: '#tp_list'
	    ,height: "full-60"
	    ,cols: [cols],page: false,even: false,limit:100000,
	    data:tp_jarr,
	    text: {
		      none: 'No Data'
		  }
	  });
	  
	  table.on('tool(tp_list)', function(obj){ // lay-filter="mc_acc_list"
		  var data = obj.data; //cur d
		  var lay_evt = obj.event; // lay-event
		  var tr = obj.tr; //tr DOM
		 
		  if(lay_evt === 'detail'){ //
		    //do somehing
		    
		  }
		  else if(lay_evt === 'del')
		  {
			  lib_del_cat(data.id);
		  }
		  else if(lay_evt === 'edit')
		  { 
			  lib_add_edit_cat(data.id) ;
		  }
		  
		});
	  
	  table.on('row(tp_list)', function(obj)
			  {
		  var libid = $("#lib_ids").val() ;
				  var data = obj.data; //cur d
				  on_sel_tp(data)
			  });
});

function tb_reload_tps(data)
{
	table.reload("tp_list",{data:data});
}

function refresh_table()
{
	tb_reload_tps(tp_jarr)
}

function on_sel_tp(d)
{//console.log(d);
	let fwin = FindFrameWin('dev_right');
	  if(fwin)
	  	fwin.location.href="dt_partlib_part_list.jsp?libid="+libid+"&parttpid="+d.parttp_id+"&edit="+bedit;
	  
	  if(parent && parent.on_selected_parttp)
		  parent.on_selected_parttp(libid,d.parttp_id,d.t)
}


function edit_parttp(d)
{
	if(event)
		event.stopPropagation();
	let t="" ;
	let id=""
	if(d)
	{
		t = d.t
		id = d.id ;
	}
	dlg.open("../util/dlg_input_txt.jsp?v="+t,
			{title:"<wbt:g>input,parttp</wbt:g>",txt_title:"<wbt:g>title</wbt:g>"},['Ok','Cancel'],
			[
				function(dlgw)
				{
					let vt = dlgw.get_input() ;
					if(!vt){dlg.msg("<wbt:g>pls,input,title</wbt:g>");return;}
					send_ajax("dt_part_ajax.jsp",{op:"edit_parttp",libid:libid,title:vt,parttpid:id},(bsucc,ret)=>{
						if(!bsucc||ret!='succ'){dlg.msg(ret);return;}
						location.reload();
						dlg.close() ;
					});
					
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function lib_edit()
{
	event.stopPropagation();
	lib_add_or_edit(true)
}


function lib_del_cat(cid)
{
	//var libid = $("#lib_ids").val() ;
	dlg.confirm('删除这个分类?',{btn:["确定","关闭"],title:"删除确认"},function ()
		    {
					send_ajax("lib_ajax.jsp","op=del_cat&libid="+libid+"&catid="+cid,function(bsucc,ret){
			    		if(!bsucc || ret!='succ')
			    		{
			    			dlg.msg("删除错误:"+ret) ;
			    			return ;
			    		}
			    		//
						refresh_table();
			    	}) ;
				});
}

function lib_add_edit_cat(catid)
{
	if(!catid)
		catid ="" ;
	//var libid = $("#lib_ids").val() ;
	dlg.open("cat_edit.jsp?libid="+libid+"&catid="+catid,
			{title:"编辑分类"},
			['确认','关闭'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="edit_cat" ;
						 var pm = {
									type : 'post',
									url : "./lib_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if(ret.indexOf("succ=")!=0)
								{
									dlg.msg(ret) ;
									return ;
								}
								dlg.close();
								refresh_table();
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

//$("#top_oper").on("click","#top_oper_edit_lib",lib_edit);
//

$("#top_oper").on("keydown","#search_txt",function(evt){
		if(evt.keyCode==13) do_search() ;
	});

</script>
</body>
</html>