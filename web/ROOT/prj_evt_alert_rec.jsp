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
	org.iottree.core.msgnet.*,
	org.iottree.core.msgnet.store.evt_alert.*,
	org.iottree.core.comp.*
	"%><%@ taglib uri="wb_tag" prefix="wbt"%><%! 

%><%
if(!Convert.checkReqEmpty(request, out,"prjid","uid"))
	return ;
String prjid = request.getParameter("prjid") ;
String uid = request.getParameter("uid") ;
UAPrj prj = UAManager.getInstance().getPrjById(prjid);
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
MNManager mnmgr = MNManager.getInstance(prj) ;
EvtAlertTb tb = mnmgr.getEvtAlertTbByUID(uid) ;
if(tb==null)
{
	out.print("no EvtAlertTb found") ;
	return ;
}
String title = tb.getNodeAlertTrigger().getTitle() ;

%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="./head.jsp"></jsp:include>
    <style>
   body {font-size: 12px;}
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

    </style>
</head>
<body  style="overflow: hidden;">
<form class="layui-form"  onsubmit="return false;">
<table style="width:100%;height:40px;border-bottom: 1px solid #e6e6e6;">
	<tr>
		<td style="width:70%;padding-left:5px;font-weight: bold;"><wbt:g>evt_alert,list</wbt:g> <span id="top_tt"><%=title %></span></td>
		<td style="padding:5px;">

      </td>
		<td style="text-align: right;padding-right:5px;width:100px;">
		<%--
		<button id="top_oper_add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="add_device(false)" title="&nbsp;新增设备"><i class="fa fa-plus"></i></button>
		<button id="add" class="layui-btn layui-btn-sm layui-btn-primary" onclick="add_device(true)" title="&nbsp;根据类型新增设备"><i class="fa fa-plus"></i><i class="fa fa-cubes"></i></button>
		 --%>
		</td>
	</tr>
</table>
</form>
<table id="alert_list"  lay-filter="alert_list"  lay-size="sm" lay-even="true" style="top:1px;width:99%;">

</table>
<script type="text/html" id="row_toolbar">
<div class="layui-btn-group">
<button type="button" class="layui-btn layui-btn-xs layui-btn-primary" lay-event="detail"><i class="fa fa-archive"></i></button>
<%

%>
  
</div>
</script>

<script>
dlg.resize_to(1000,600) ;
var uid = "<%=uid%>" ;
var form ;
var table ;
var table_cur_page = 1 ;

function render_tb()
{
	
	  let cols = [];
	
	 //cols.push({field: 'n', title: '<wbt:g>name</wbt:g>', width:'25%'}) ;
	 cols.push({field: 'Level', title: '<wbt:g>lvl</wbt:g>', width:'10%'});
	 cols.push({field: 'TriggerDT', title: '<wbt:g>trigger_dt</wbt:g>', width:'20%'});
	 cols.push({field: 'ReleaseDT', title: '<wbt:g>release_dt</wbt:g>', width:'20%'});
	 cols.push({title: '<wbt:g>dur_tm</wbt:g>', width:'10%',templet:function(ob){
		 if(!ob.dur_ms) return "" ;
		 return ob.dur_ms/1000 +"s" ;
	 }});
	 cols.push({field: 'Prompt', title: '<wbt:g>prompt</wbt:g>', width:'25%'});
	 cols.push({field: 'AlertTP', title: '<wbt:g>type</wbt:g>', width:'15%'});
	 
	table.render({
	    elem: '#alert_list'
	    ,height: "full-40"
	    ,url: "prj_evt_alert_ajax.jsp?op=list_items&uid="+uid
	    ,page: {layout:['prev', 'page', 'next'],limit:25,theme:"#c00"} //open page
	    ,cols: [cols]
	  ,parseData:function(res){
			if(res.data.length==0){
				return{
					'code':'201',
					'msg':'<wbt:g>no_evt_alert</wbt:g>'
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
	  
	  table.on('tool(alert_list)', function(obj){ // lay-filter="mc_acc_list"
		  var data = obj.data; //cur d
		  var lay_evt = obj.event; // lay-event
		  var tr = obj.tr; //tr DOM
		 
		});
	  
	  table.on('row(alert_list)', function(obj)
			  {
		  var trs = $(".layui-table-body.layui-table-main tr");
		  trs.each(function(){
			  $(this).removeClass("seled") ;
		  })
		  obj.tr.addClass("seled");
				  var data = obj.data; //cur d
				  //
			  });
	  refresh_table(true);
}

function refresh_table()
{
	table.reload("alert_list",{url:"prj_evt_alert_ajax.jsp?op=list_items&uid="+uid,page:{curr:1}});
	//table.reload("alert_list",{});
}



layui.use(['table','form'], function()
		{
	form = layui.form;
	    table = layui.table;
		  render_tb() ;
		});


</script>
</body>
</html>