<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%
	List<DevDriver> dds = DevManager.getInstance().getDrivers() ;
	boolean bmgr ="true".equals(request.getParameter("mgr")) ;
	
	String drv = request.getParameter("drv") ;
	if(drv==null)
		drv="" ;
	DevDriver limit_drv = null;
	if(Convert.isNotNullEmpty(drv))
		limit_drv = DevManager.getInstance().getDriver(drv) ;
	boolean hide_drv = "true".equals(request.getParameter("hide_drv")) ;
%><html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
</head>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
-moz-user-select : none;
-webkit-user-select: none;
}

select option
{
font-size: 12px;
}

.oc-toolbar .toolbarbtn
{
width:40px;height:40px;margin: 5px;
font-size: 13px;
background-color: #eeeeee
}

.rmenu_item:hover {
	background-color: #373737;
}



</style>
<body marginwidth="0" marginheight="0">
<table width='100%' height='99%'>
 <tr>
 <%
 if(null==limit_drv || !hide_drv)
 {
 %>
  <td valign="top" width="25%">
 Drivers
   <select id='var_drv' multiple="multiple" style="width: 100%;height: 100%" onchange="drv_sel_chg()">
<%
	if(limit_drv==null)
	{

	for(DevDriver dd:dds)
	{
%><option value="<%=dd.getName() %>"><%=dd.getTitle() %></option><%
	}
%>

 <%
	 }
 else
 {
%><option value="<%=limit_drv.getName() %>" selected="selected"><%=limit_drv.getTitle() %></option><%
 }
 %>   </select>
  </td>
<%
 }
%>
 <td valign="top" width="25%">Category
 <%
 if(bmgr)
 {
 %> 
   <button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_cat()">+Add</button>
<%
 }
%>
   <select id='var_cat' multiple="multiple" style="width: 100%;height: 100%" onchange="cat_sel_chg()">
   </select>
 </td>
 <td id="td_add_comp" valign="top" width="75%" class="oc-toolbar">
 	Devices <span id="selected_prompt"></span>
 <%
 if(bmgr)
 {
 %> 
 	<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_devdef()">+Add</button>
<%
 }
%>
 	<table id="tb_devdefs"  lay-filter="tb_devdefs"  lay-size="sm" lay-even="true" style="width:100%"></table>
 	  <script type="text/html" id="row_toolbar">
<div class="layui-btn-group">
  
			{{#  if(d.id==""){ }}
	<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" lay-event="devdef_add">Add</button>
			{{#  } else{ }}
	<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" lay-event="devdef_edit">Edit</button>			
			{{#  } }}

  </div>
	</script>
	 	  <script type="text/html" id="sel_toolbar">
<div class="layui-btn-group">
{{#  if(d.id!=""){ }}
		<button type="button" class="layui-btn layui-btn-xs layui-btn-normal" lay-event="devdef_sel">Select</button>
{{#  } }}
  </div>
	</script>
<%--
 	<div  id="var_devdefs" class="btns" >
 		<div class="toolbarbtn" onclick="" title=""><img src="" /></div>
 	</div>
 	 --%>
 </td>
 
 </tr>
 <tr height="30">
  <td colspan='3'></td>
 </tr>
</table>
 
<script>
var hide_drv = <%=hide_drv%>
var cur_drv = "<%=drv%>" ;
var cur_drv_tt = "<%=limit_drv.getTitle()%>" ;
var cur_catid = null ;
var bmgr=<%=bmgr%>;

function get_cur_drv_name_title()
{
	if(hide_drv)
		return [cur_drv,cur_drv_tt] ;
	var vv = $("#var_drv").val() ;
	var tt =  $("#var_drv option:selected").text() ;
	if(vv==null||vv==undefined||vv==""||vv.length==0)
	{
		//dlg.msg("please select a Driver!");
		return null;
	}
	vv = vv[0];
	return [vv,tt] ;
}

function get_cur_cat_name_title()
{
	var catid = $("#var_cat").val() ;
	var cattt =  $("#var_cat option:selected").text() ;
	if(catid==null||catid==undefined||catid==""||catid.length==0)
	{
		dlg.msg("please select a category!");
		return;
	}
	catid = catid[0];
	return [catid,cattt] ;
}

function get_cur_cat_name()
{
	return get_cur_cat_name_title()[0];
}


function drv_sel_chg()
{
	var n_t = get_cur_drv_name_title();
	if(n_t==null)
		return;
	cur_drv = n_t[0] ;
	
	var pm = {
			type : 'post',
			url : "./cat_ajax.jsp",
			data :{op:"list",drv:cur_drv}
		};
	$.ajax(pm).done((ret)=>{
		if(typeof(ret)=='string')
		{
			if(ret.indexOf("[")!=0)
			{
				dlg.msg(ret) ;
				return ;
			}
			eval("ret="+ret) ;
		}
		var tmps = "" ;
		for(var a of ret)
		{
			tmps += "<option value='"+a.n+"'>"+a.t+"-["+a.n+"]</option>";
		}
		$("#var_cat").html(tmps) ;
	}).fail(function(req, st, err) {
		dlg.msg(err);
	});
}

function add_cat()
{
	var n_t = get_cur_drv_name_title();
	if(n_t==null)
	{
		dlg.msg("please select a Driver!");
		return;
	}
		
	var drv = n_t[0] ;
	dlg.open("cat_edit.jsp",
			{title:"Add Device Category"},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="add" ;
						 ret.drv=drv;
						 var pm = {
									type : 'post',
									url : "./cat_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								dlg.close();
								drv_sel_chg();
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
					
function get_cur_drv_cat()
{
	var n_t = get_cur_drv_name_title();
	if(n_t==null)
		return null;
	var drv = n_t[0] ;
	var catnt = get_cur_cat_name_title();
	if(catnt==null)
		return null;
	var catn  = catnt[0] ;
	return {drv:drv,cat:catn,cat_tt:catnt[1]} ;
}

function cat_sel_chg()
{
	var drv_cat = get_cur_drv_cat() ;
	if(drv_cat==null)
		return ;
	cur_drv = drv_cat.drv ;
	var catn  = drv_cat.cat ;
	show_table();
	
}

var table = null ;

var cur_selected = null ;

//var on_devdef_selected = null ;

layui.use('table', function()
{
  table = layui.table;
  table.on('tool(tb_devdefs)', function(obj){ // lay-filter="mc_acc_list"
	  var data = obj.data; //cur d
	  var lay_evt = obj.event; // lay-event
	  var tr = obj.tr; //tr DOM
	 
	  if(lay_evt === 'detail'){ //查看
	    //do somehing
	    
	  }
	  else if(lay_evt === 'del')
	  {
	    layer.confirm('delete selected device?', function(index)
	    {
	    	send_ajax("dlcunit_ajax.jsp","op=del&id="+data.Uid,function(bsucc,ret){
	    		if(bsucc&&ret=='succ')
	    			obj.del();
	    		else
	    			layer.msg("del err:"+ret) ;
	    	}) ;
	      
	      layer.close(index);
	    });
	  }
	  else if(lay_evt === 'edit')
	  { 
	    //add_edit(data) ;
	  }
	  else if(lay_evt==='devdef_add')
	  {
		  add_devdef();
	  }
	  else if(lay_evt==='devdef_edit')
	  {
		  var vv = get_cur_drv_cat()
		  window.open("devdef_editor.jsp?drv="+vv.drv+"&cat="+vv.cat+"&id="+data.id) ;
	  }
	  else if(lay_evt==='devdef_sel')
	  {
		  //console.log(data) ;
		  cur_selected = {} ; 
		  cur_selected.id=data.id ;
		  cur_selected.name=data.n ;
		  cur_selected.title=data.t ;
		  
		  var vv = get_cur_drv_cat()
		  //console.log(vv) ;
		  cur_selected.cat_name=vv.cat ;
		  cur_selected.cat_title=vv.cat_tt ;
		  if(parent.on_devdef_selected)
			  parent.on_devdef_selected(cur_selected) ;
		  $("#selected_prompt").html("you select:"+data.t)
	  }
	});
  
  table.on('row(dl_list)', function(obj)
		  {
			  var data = obj.data; //cur d
			  
			});
  
});


function get_selected()
{
	return cur_selected ;
}

function show_table()
{
	var drv_cat = get_cur_drv_cat() ;
	if(drv_cat==null)
		return ;
	
	table.render({
	    elem: '#tb_devdefs'
	    ,height: "full-120"
	    ,url: 'devdef_ajax.jsp?op=list_tb&drv='+drv_cat.drv+'&cat='+drv_cat.cat+"&mgr="+bmgr
	    ,page0: {layout:['prev', 'page', 'next'],limit:10,theme:"#c00"} //open page
	    ,cols: [[ //head
	    	{field: 'n', title: 'Name', width:'40%'}
	    	,{field: 't', title: 'Title', width:'40%'}
<%
if(bmgr)
{
%>
,{field: 'Oper', title: '', width:'20%',toolbar: '#row_toolbar'}
<%
}
else
{
%>
,{field: 'Oper', title: '', width:'20%',toolbar: '#sel_toolbar'}
<%
}
%>
	    ]]
	    ,done:function(res, curr, count){
	   	 table_cur_page = curr ;
	   	 var trs = $(".layui-table-body.layui-table-main tr");
	   	 for(var i = 0 ; i < res.data.length;i++)
	   		 {
	   		    if(i%2==0)
		    		 trs.eq(i).css("color","#1e9fff");
		     }
	   	 }
	  });
}

function list_devdefs0()
{
	var pm = {
			type : 'post',
			url : "./devdef_ajax.jsp",
			data :{op:"list",drv:cur_drv,cat:catn}
		};
	$.ajax(pm).done((ret)=>{
		if(typeof(ret)=='string')
		{
			if(ret.indexOf("[")!=0)
			{
				dlg.msg(ret) ;
				return ;
			}
			eval("ret="+ret) ;
		}
		var tmps = "" ;
		for(var a of ret)
		{
			tmps += "<div id='"+a.id+"' class0=\"toolbarbtn\" style=\"text-align:center;width:50px;height:60px;border:solid 1px\" onclick=\"devdef_clk('"+a.id+"')\" >"
			+"<div id='panel_"+a.id+"' style='background-color: #2f2f2f;width:40px;height:40px'></div>"
			+"<div >"+a.t+"</div></div>";
		}
		$("#var_devdefs").html(tmps) ;
	}).fail(function(req, st, err) {
		dlg.msg(err);
	});
}

function refresh_table()
{
	table.reload("dl_list",{curr: table_cur_page});
}

function devdef_clk()
{
	
}

function add_devdef()
{
	var n_t = get_cur_drv_name_title();
	if(n_t==null)
	{
		dlg.msg("please select a Driver!");
		return;
	}
	var drv = n_t[0] ;
	var catnt = get_cur_cat_name_title() ;
	if(catnt==null)
	{
		dlg.msg("please select a category!");
		return;
	}
	var catn = catnt[0] ;
	
	dlg.open("cat_edit.jsp",
			{title:"Add Device"},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					dlgw.do_submit((bsucc,ret)=>{
						 if(!bsucc)
						 {
							 dlg.msg(ret) ;
							 return;
						 }
						 
						 ret.op="add" ;
						 ret.drv=drv;
						 ret.cat = catn ;
						 var pm = {
									type : 'post',
									url : "./devdef_ajax.jsp",
									data :ret
								};
							$.ajax(pm).done((ret)=>{
								if("succ"!=ret)
								{
									dlg.msg(ret) ;
									return ;
								}
								dlg.close();
								cat_sel_chg();
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


drv_sel_chg();

</script>

</body>
</html>