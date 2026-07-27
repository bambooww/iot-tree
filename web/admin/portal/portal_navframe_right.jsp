<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %><%@ taglib uri="wb_tag" prefix="w"%><%! 

%><%
if(!Convert.checkReqEmpty(request, out, "prjid","nf_id"))
	return ;
String prjid = request.getParameter("prjid") ;
String nf_id = request.getParameter("nf_id") ;
NavFrame navf = NavFrame.getNavFrame(prjid,nf_id) ;
if(navf==null)
{
	out.print("no NavFrame found") ;
	return ;
}
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
    width: 100px;
}
.layui-input-block {
    margin-left: 140px;
    min-height: 36px;
    width:240px;
}
.layui-form-item {
    margin-bottom: 5px;
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
		<td style="width:150px;padding-left:5px;font-weight: bold;"><w:g>nav_frame</w:g> - <span id="top_tt"><%=navf.getTitle() %></span></td>
		<td style="text-align: left;padding-right:5px;width:250px;border:0px solid">
		<button id="btn_save_detail" class="layui-btn layui-btn-sm layui-btn-primary" onclick="save_detail()" ><i class="fa fa-save"></i></button>
		<button id="btn_open_url" class="layui-btn layui-btn-sm layui-btn-primary" onclick="show_page()" ><i class="fa-regular fa-paper-plane"></i></button>
		</td>
		
	</tr>
</table>
  <div class="layui-form-item" id="">
    <label class="layui-form-label"><w:g>layout,temp</w:g>:</label>
    <div class="layui-input-inline"  style="width:200px;">
      <select id="layout" class="layui-input" lay-filter="layout">
      	<option value=""> --- </option>
      	<option value="default">default</option>
      </select>
  </div>
  <div class="layui-form-item" id="">
    <label class="layui-form-label"><w:g>sys,title</w:g>:</label>
    <div class="layui-input-inline"  style="width:200px;">
      <input type="text" id="sys_t" value="<%=navf.getSysTitle()%>" class="layui-input"/>
    </div>
  </div>
</div>
</form>

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

</script>
<script>
var prjid="<%=prjid%>"
var nf_id="<%=nf_id%>"
var form ;
var table ;
var url_path = "<%=navf.getUrlPath(true)%>" ;
layui.use(['table','form'], function()
{
	form = layui.form;
	table = layui.table;
	$("#sys_t").on("input",function(e){
		set_dirty(true);
	});
	 form.on('select(layout)', function(data){   
		 set_dirty(true);
	 });
	 
	form.render();
});

function show_page()
{
	window.open(url_path);
}

var selected_item= null ;

function on_sel_single(item)
{
	selected_item = item ;
	if(parent.on_page_sel)
		parent.on_page_sel(item) ;
	reload_preview();
}

function get_detail()
{
	let ret = {} ;
	ret.sys_t = $("#sys_t").val()||"";
	ret.layout = $("#layout").val()||"default";
	ret.home_url = $("#home_url").val()||"" ;
	
	return ret;
}

function save_detail()
{
	let ob = get_detail() ;
	send_ajax("portal_ajax.jsp",{op:"set_nf_detail",prjid:prjid,nf_id:nf_id,jstr:JSON.stringify(ob)},(bsucc,ret)=>{
		if(!bsucc || ret!='succ')
		{
			dlg.msg(ret);return;
		}
		set_dirty(false);
		reload_preview();
	})
}

function set_dirty(b)
{
	$("#btn_save_detail").css("background-color",b?"yellow":"") ;
}

function reload_preview()
{
	parent.on_page_preview(url_path)
}

reload_preview()

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