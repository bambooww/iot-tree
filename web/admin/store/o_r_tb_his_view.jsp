<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.store.*,
	org.iottree.core.alert.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "prjid","hid","id"))
	return ;

String prjid = request.getParameter("prjid");
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
String hid= request.getParameter("hid");
String id= request.getParameter("id");
StoreManager stom = StoreManager.getInstance(prjid) ;
StoreHandler storeh = stom.getHandlerById(hid) ;
if(storeh==null)
{
	out.print("no handler found") ;
	return ;
}
StoreOutTbHis storeo = (StoreOutTbHis)storeh.getOutById(id) ;
if(storeo==null)
{
	out.print("no outer found") ;
	return ;
}
StringBuilder failedr = new StringBuilder() ;
if(!storeo.checkOrInitOk(failedr))
{
	//out.print("the outer is not enable or init failed!") ;
	out.print(failedr.toString()) ;
	return ;
}
String tagpath = request.getParameter("tagpath") ;
List<UATag> seltags = storeh.listSelectedTags() ;
%><!DOCTYPE html>
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

.layui-table
{
	margin-top: 0px;
}
.layui-table-view
{
	margin-top: 0px;
}
  .layui-table-cell {
    height: auto;
    line-height: 18px;
}

.sel
{
background-color: rgba(00,173,229,0.3);
}
.top
{
position: absolute;
	top:00px;
	left:0px;
	width:100%;
	border:1px solid #c1c1c1;
	height:30px;
}
.main_left
{
	position: absolute;
	top:30px;
	left:0px;
	width:100%;
	border:1px solid #c1c1c1;
	bottom:0px;
	overflow-y:scroll;
}

 td,th
{
white-space: nowrap;
text-overflow: ellipsis;
}


    </style>
</head>
<script type="text/javascript">
dlg.resize_to(860,600) ;
</script>
<body>
<div class="top">
  Tag <select id="tag_sel">
  <option value=""> --- </option>
<%
for(UATag tag:seltags)
{
	String fullp = tag.getNodePathCxt() ;
	String tagp = tag.getNodeCxtPathIn(prj) ;
%><option value="<%=fullp%>"><%=tagp %></option><%
}
%>
  </select>
  Valid
  <select id="valid_sel">
  <option value=""> --- </option>
  <option value="true">true</option>
  <option value="false">false</option>
  </select>
  Start Date
  <input type="datetime-local" id="start_dt" name="start_dt"/>
  End Date
  <input type="datetime-local" id="end_dt" name="end_dt"/>
  <button onclick="do_search()">Search</button>
  <button onclick="do_search_all()">All</button>
</div>
  <div class="main_left" id="main_left">
<table id="" class="layui-table"  lay-filter="apiquote_list"  lay-size="sm" lay-even0="true"  style="width:100%">
  	<colgroup>
    <col width="30">
    <col width="250">
    <col width="100">
    <col width="150">
    <col width="70">
    <col width="100">
    <col width="70">
    <col width="100">
    <col>
  </colgroup>
  <thead>
    <tr style="background-color: #cccccc">
     <th>Tag</th>
      <th>Update Time</th>
      <th>Change Time</th>
      <th>Valid</th>
      <th>Type</th>
      <th>Value</th>
      <th>Alert</th>
    </tr> 
  </thead>
  <tbody id="list_cont" class="list_cont">
   
  </tbody>
</table>
</div>
<script>

var prjid = "<%=prjid%>";
var hid = "<%=hid%>" ;
var id =  "<%=id%>" ;

var table ;
var table_cur_page = 1 ;

var tagpath = "" ;
var valid = "" ;
var start_dt = "" ;
var end_dt = "" ;
layui.use('table', function()
{
	table = layui.table;
});


function show_list(u,pm,bappend) {
	
	cur_list_u = u ;
	pm.tagpath = tagpath||"" ;
	pm.valid=valid||"" ;
	pm.start_dt = start_dt||"" ;
	pm.end_dt = end_dt||"" ;
	send_ajax(u,pm,(bsucc,ret)=>{
		if(!bsucc)
		{
			dlg.msg(ret);
			return ;
		}
		if(bappend)
			$("#list_cont").append(ret) ;
		else
			$("#list_cont").html(ret) ;
	});
}

function update_list()
{
	page_idx=0 ;
	show_list("o_r_tb_his_view_list.jsp",{prjid:prjid,hid:hid,id:id,pageidx:0},false) ;
}


function show_list_more() {
	page_idx++ ;
	show_list("o_r_tb_his_view_list.jsp",{prjid:prjid,hid:hid,id:id,pageidx:page_idx},true) ; 
}

update_list();

function on_row_clk(id)
{}

var allshow=false;

var sdiv = $("#main_left")[0] ;
 $("#main_left").scroll(()=>{
	 var wholeHeight=sdiv.scrollHeight;
	 var scrollTop=sdiv.scrollTop;
	 var divHeight=sdiv.clientHeight;
	 if(divHeight+scrollTop>=wholeHeight)
	 {//reach btm
		 if(!page_has_next)
			{
				if(!allshow)
					dlg.msg("no more list items");
				allshow=true;
				return;
			}
				
			//console.log("show more");
			show_list_more();
		     $("main_left").scroll(scrollTop);
	 }
	 if(scrollTop==0)
	 {//reach top
		
	}
});
 
function do_search()
{
	tagpath = $("#tag_sel").val() ;
	valid = $("#valid_sel").val() ;
	start_dt = $("#start_dt").val() ;
	end_dt = $("#end_dt").val() ;
	update_list() ;
}

function do_search_all()
{
	tagpath="" ;
	$("#tag_sel").val("") ;
	valid = "" ;
	$("#valid_sel").val("") ;
	start_dt = "" ;
	$("#start_dt").val("") ;
	end_dt = "" ;
	 $("#end_dt").val("") ;
	update_list() ;
}
</script>
</body>
</html>