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
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return ;

String prjid = request.getParameter("prjid");
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}

AlertManager amgr= AlertManager.getInstance(prjid) ;
List<String> outer_sors = amgr.HIS_getRecordOuterSorNames() ;

%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="./head.jsp"></jsp:include>
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
  Source <select id="sor_sel">
  <option value="">Inner</option>
<%
for(String sor:outer_sors)
{
%><option value="<%=sor%>"><%=sor %></option><%
}
%>
  </select>
  Handler
  <select id="handler_sel">
  <option value=""> --- </option>
<%
for(AlertHandler ah:amgr.getHandlers().values())
{
	String ahn = ah.getName() ;
	String aht = ah.getTitle() ;
%><option value="<%=ahn%>"><%=aht %></option><%
}
%>
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
      <th>Trigger Time</th>
      <th>Release Time</th>
      <th>Handler</th>
      <th>Tag</th>
      <th>Type</th>
      <th>Value</th>
      <th>Level</th>
      <th>Prompt</th>
    </tr> 
  </thead>
  <tbody id="list_cont" class="list_cont">
   
  </tbody>
</table>
</div>
<script>

var prjid = "<%=prjid%>";

var table ;
var table_cur_page = 1 ;

var sor = "" ;
var handler = "" ;
var start_dt = "" ;
var end_dt = "" ;
layui.use('table', function()
{
	table = layui.table;
});


function show_list(u,pm,bappend) {
	
	cur_list_u = u ;
	pm.sor = sor||"" ;
	pm.handler=handler||"" ;
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
	show_list("prj_alert_his_list.jsp",{prjid:prjid,pageidx:0},false) ;
}


function show_list_more() {
	page_idx++ ;
	show_list("prj_alert_his_list.jsp",{prjid:prjid,pageidx:page_idx},true) ; 
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
	sor = $("#sor_sel").val() ;
	handler = $("#handler_sel").val() ;
	start_dt = $("#start_dt").val() ;
	end_dt = $("#end_dt").val() ;
	update_list() ;
}

function do_search_all()
{
	sor = $("#sor_sel").val() ;
	handler = "" ;
	$("#handler_sel").val("") ;
	start_dt = "" ;
	$("#start_dt").val("") ;
	end_dt = "" ;
	 $("#end_dt").val("") ;
	update_list() ;
}
</script>
</body>
</html>