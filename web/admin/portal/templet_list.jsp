<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %><%
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
  
  .item {cursor:pointer;}
  
 .cat {margin:10px;position: relative;border:1px solid #ccc;height:35px;border-radius:3px;cursor:pointer;}
 .cat:hover {background-color: #ccc;}
 .cat .t {position: absolute;left:5px;top:3px;font-size: 14px;font-weight: bold;}
 .cat .n {position: absolute;left:8px;bottom:1px;font-size: 12px;}
 .sel {background-color: #ccc;}
    </style>
</head>
<body  style="overflow: hidden;">
<form class="layui-form"  onsubmit="return false;" >
<table style="width:100%;height:40px;border-bottom: 1px solid #e6e6e6;">
	<tr>
		<td style="width:150px;padding-left:5px;font-weight: bold;">模板列表 <span id="top_tt"></span></td>
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
		<%--
		<button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="edit_page_cat()" ><i class="fa fa-plus"></i>新增分类</button>
		<button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="edit_page()" ><i class="fa fa-plus"></i>新增页面</button>
		 --%>
		</td>
	</tr>
</table>
</form>

<div style="position:absolute ;right:0px;width:100%">
<table id="item_list"  lay-filter="item_list"  lay-even="true" class="layui-table" style="top:1px;width:99%;">
<%
	List<Templet> temps = PortalManager.getInstance().listTempletsAll() ;
	for(Templet temp:temps)
	{
		String uid = temp.getUID() ;
		TempletCat tc = temp.getCat() ;
%>
<tr class="item" onclick="on_templet_sel(this,'<%=uid%>')">
	<td><%=tc.getTitle() %></td>
	<td><%=temp.getTitle() %></td>
	<td></td>
	<td></td>
</tr>
<%
	}
%>
</table>
</div>
<script type="text/html" id="row_toolbar">
<div class="layui-btn-group">
<%

%>
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="edit"><i class="fa fa-pencil"></i></button>

<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="barcode"><i class="fa-solid fa-barcode"></i></button>

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
});

function on_sel_cat(n,t)
{
	cur_page_cat_name = n ;
	cur_page_cat_title = t ;
	refresh_table();
}



function on_templet_sel(ele,uid)
{
	$(".item").removeClass("sel") ;
	$(ele).addClass("sel") ;
	
	if(parent.on_templet_sel)
		parent.on_templet_sel(uid) ;
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