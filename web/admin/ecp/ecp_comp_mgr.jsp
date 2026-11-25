<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,org.json.*,
	java.util.*,
	java.net.*,org.iottree.core.ecp.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
String title = "" ;
ECPCompManager.getInstance().getLibMap();
boolean b_dlg = "true".equalsIgnoreCase(request.getParameter("dlg")) ;
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script src="/_js/ecp/ecp.js?v=1"></script>
<style>
body {background-color: #fff;}
table {table-layout: fixed;width:100%;border:1px solid #ccc;}
.layui-table-view .layui-table td, .layui-table-view .layui-table th {
    padding: 1px 0;
}
.seled {color:green;font-weight: bold;}
.right_op {width:28%;margin-left:2px;position: absolute;right:3px;top:10px;}
.right_op .ext {width:105px;height:105px;border:1px solid #888;margin:3px;}
</style>
</head>
<body style="overflow:hidden;">
<div class="toolbar" style="width:100%;height:30px;line-height:30px;border:1px solid #ccc;text-align: center;">
	<wbt:g>comp</wbt:g>
</div>
<div style="display: flex;flex-wrap:wrap;gap:5px;">
<%
	String css1 ="bottom:310px;top:35px;position: absolute;left:2px;right:2px;" ;
	String css2 = "bottom:0px;height:300px;position: absolute;left:2px;right:2px;" ;
	if(b_dlg)
	{
		css1 = "width:400px;height:500px;" ;
		css2 = "width:400px;height:400px;position:relative" ;
	}
%>
<fieldset class="layui-elem-field" style="<%=css1%>">
  <legend><wbt:g>comp,list</wbt:g>-<button id="" class="layui-btn layui-btn-xs layui-btn-primary" onclick="edit_comp()" title="&nbsp;新增计算模块"><i class="fa fa-plus"></i></button>
<button id="" class="layui-btn layui-btn-xs layui-btn-primary" onclick="location.reload()" title="&nbsp;刷新"><i class="fa fa-refresh"></i></button>
  
	</legend>
  <div class="layui-field-box" id="" >
	<div class="layui-form">
		<select id="libid" lay-filter="libid">
<%
    for(ECPCompLib complib:ECPCompManager.getInstance().getLibMap().values())
	{
			String cssstr = "" ;
			String tmpid = "" ;
			String clibid = complib.getLibId() ;
			String tt = complib.getLibTitle() ;
			%><option value="<%=clibid%>"><%=tt %></option><%
	}
%>
      </select>
      
	</div>
	<table id="comps_list"  lay-filter="comps_list"  lay-size="sm" lay-even="true" style="top:1px;width:99%;">
</table>
</div>
</fieldset>

<fieldset class="layui-elem-field"  style="<%=css2%>">
  <legend><wbt:g>preview</wbt:g>-</legend>
  <div class="layui-field-box" style="overflow: hidden;">
  	 <div  id="preview" style="background-color: #333;width:70%;border:1px solid red;height:90%;"></div>
  	 <div class="right_op" style="">
  	 <button class="layui-btn layui-btn-sm layui-btn-primary" style="margin:2px;" onclick="edit_comp_cur()"><wbt:g>edit</wbt:g></button>
<%
if(!b_dlg)
{
%><button class="layui-btn layui-btn-sm layui-btn-primary" style="margin:2px;" onclick="place_comp()"><wbt:g>place</wbt:g><i class="fa fa-arrow-right"></i></button>
<%
}
%>
  	 	<div class="ext">
  	 	</div>
  	 	<div class="ext" style="margin-top:7px;">
  	 	</div>
  	 </div>
	</div>
 </fieldset>
</div>
</body>
<script type="text/html" id="p_toolbar">
<div class="layui-btn-group">
  <button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit"><i class="fa-solid fa-pencil"></i></button>
&nbsp;
  <button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="del"><i class="fa-solid fa-times"></i></button>
  </div>
</script>
<script type="text/javascript">
var b_dlg = <%=b_dlg%> ;
if(b_dlg)
	dlg.resize_to(860,660);
var ecp_shower = new ecp.ECPShower("preview");
var form;
var table;
var cur_comp = null ;
layui.use(['form','table'], function(){
	  form = layui.form;
	  form.on("select(libid)",function(obj){
		  
		  update_lib_comps()
	  })
	  form.render();
	  
	  table = layui.table;
	  render_tb()
	  
	  update_lib_comps();
});

function render_tb()
{
	  let cols = [];
	 cols.push({field:'t',title: '<wbt:g>title</wbt:g>', width:'40%'});
	 cols.push({field: 'dt', title: '<wbt:g>update,dt</wbt:g>', width:'40%',templet: function(d) {
	      let dt = new Date(d.dt)
	      return dt.format_local('yyyy-MM-dd hh:mm');
	    }});
	 
	 cols.push({field: 'Oper', title: '<wbt:g>oper</wbt:g>', width:"20%" ,toolbar: '#p_toolbar'}) ;
	 
	table.render({
	    elem: '#comps_list'
    	    ,height: "520px",className: 'ellipsis-left'
    	    ,page: false,even: true,limit:100000
    	    ,cols: [cols]
    		  ,data:[]
    		  ,text: {
    		      none: 'No Data'
    		  }
    	  });
	  
	  table.on('tool(comps_list)', function(obj){ // lay-filter="mc_acc_list"
		  var data = obj.data; //cur d
		  var lay_evt = obj.event; // lay-event
		  var tr = obj.tr; //tr DOM

		  if(lay_evt === 'del')
		  {
			  del_prop(data);
		  }
		  else if(lay_evt === 'edit')
		  {
			  edit_comp(data) ;
		  }
		  else if(lay_evt==='edit_ok')
		  {
			  set_prop_row_ok(data)
		  }
		  else if(lay_evt === 'up')
		  {
			  updown_runblk(data,true) ;
		  }
		  else if(lay_evt === 'down')
		  {
			  updown_runblk(data,false) ;
		  }
		});
	  table.on('row(comps_list)', function(obj)
			  {
		  var trs = $(".layui-table-body.layui-table-main tr");
		  trs.each(function(){
			  $(this).removeClass("seled") ;
		  })
		  obj.tr.addClass("seled");
				  var data = obj.data; //cur d
				  	on_sel_comp(data)
			  });
	  
	  //refresh_table(true);
}

function on_sel_comp(d)
{
	//console.log(d)
	cur_comp = d ;
	send_ajax("./ecp_comp_ajax.jsp",{op:"comp_load_by_uid",comp_uid:d.uid},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("{")!=0) {dlg.msg(ret);return;}
		let data = null;
		eval("data="+ret) ;
		preview_comp(data)
	})
}

function preview_comp(d)
{
	ecp_shower.drawComp(d) ;
}

function edit_comp_detail(d)
{
	window.open("./ecp_net_detail.jsp?comp=true&net_uid="+d.uid)
}

function edit_comp_cur()
{
	if(!cur_comp) {dlg.msg("no comp selected");return;}
	edit_comp_detail(cur_comp);
}

function edit_comp(d)
{
	if(event)
		event.stopPropagation();
	
	let comp_id = d?d.id:"";
	let libid = $("#libid").val() ;
	let tt = "<wbt:g>edit,comp</wbt:g>" ;
	let op = comp_id?"edit_comp":"comp_new" ;
	dlg.open("ecp_comp_edit.jsp?libid="+libid+"&compid="+(comp_id||""),
			{title:tt},
			['<wbt:g>ok</wbt:g>','<wbt:g>close</wbt:g>'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op=op ;
						 ret.compid = comp_id||"";
						 send_ajax("./ecp_comp_ajax.jsp",ret,(bsucc,ret)=>{
							 if(ret.indexOf("succ=")!=0)
								{
									dlg.msg(ret) ;
									return ;
								}
								dlg.close();
								location.reload() ;
						 })
				 	});
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}


function tb_reload(data)
{
	table.reload("comps_list",{data:data});
	
	$('.pe').on('input change', function() {
		set_dirty(true)
	});
}

function update_lib_comps()
{
	let libid = $("#libid").val()
	send_ajax("./ecp_comp_ajax.jsp",{op:"comp_list_by_libid",libid:libid},(bsucc,ret)=>{
		if(!bsucc || ret.indexOf("[")!=0)
		{
			dlg.msg(ret);return ;
		}
		let hds = null;
		eval("hds="+ret) ;
		tb_reload(hds)
	})
}


function set_dirty(b)
{
	if(b)
	{
		b_dirty = true;
		$("#btn_set_to_node").css("background-color","yellow") ;
	}
	else
	{
		b_dirty = false;
		$("#btn_set_to_node").css("background-color","") ;
	}
	
}

function place_comp()
{
	let comp_def = ecp_shower.getECPCompDef();
	if(!comp_def) {dlg.msg("<wbt:g>pls,select,comp</wbt:g>");return;}
	if(parent && parent.placeCompIns)
		parent.placeCompIns(comp_def)
}

function win_close()
{
	dlg.close(0);
}

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
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