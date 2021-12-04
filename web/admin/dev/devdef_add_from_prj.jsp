<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*
	"%><%!

%><%

if(!Convert.checkReqEmpty(request, out, "devpath"))
	return ;
String devpath = request.getParameter("devpath") ;
UADev dev = (UADev)UAUtil.findNodeByPath(devpath);
if(dev==null)
{
	out.print("no device found");
	return ;
}
DevDriver dd = dev.getBelongTo().getDriver() ;
if(dd==null)
{
	out.print("no driver found");
	return ;
}

	String drv = dd.getName() ;
	boolean hide_drv = "true".equals(request.getParameter("hide_drv")) ;
	String drv_tt = dd.getTitle();
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

 <td valign="top" width="25%">Category
 <button type="button" class="layui-btn layui-btn-xs layui-btn-normal" onclick="add_cat()">+Add</button>
 
   <select id='var_cat' multiple="multiple" style="width: 100%;height: 100%" onchange="cat_sel_chg()">
   </select>
 </td>
 <td id="td_add_comp" valign="top" width="75%" class="oc-toolbar">
 	Device Name:<input type="text" id="devdef_name" /> Title<input type="text" id="devdef_title" />

 	<table id="tb_devdefs"  lay-filter="tb_devdefs"  lay-size="sm" lay-even="true" style="width:100%"></table>
 	
 </td>
 
 </tr>
 <tr height="30">
  <td colspan='3'></td>
 </tr>
</table>
 
<script>
var hide_drv = <%=hide_drv%>
var cur_drv = "<%=drv%>" ;
var cur_drv_tt = "<%=drv_tt%>" ;
var cur_catid = null ;

function get_cur_cat_id_title()
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

function get_cur_cat_id()
{
	return get_cur_cat_id_title()[0];
}

function do_submit(cb)
{
	var drv_cat = get_cur_drv_cat() ;
	if(drv_cat==null||drv_cat=='')
	{
		dlg.msg("please select category");
		return ;
	}
	var n = $("#devdef_name").val() ;
	var t = $("#devdef_title").val() ;
	if(n==null||n=='')
	{
		dlg.msg("please input device name") ;
		return ;
	}
	if(t==null||t=='')
		t= n ;
	var pm = {
			type : 'post',
			url : "./devdef_ajax.jsp",
			data :{op:"chk_name",drv:cur_drv,catid:drv_cat.cat,name:n}
		};
	$.ajax(pm).done((ret)=>{
		if(typeof(ret)=='string')
		{
			if(ret=="ok")
			{
				layer.confirm('device with ame = '+n+' already exists, do you want to overwrite it?', function(index)
						{
							cb(true,{drv:cur_drv,catid:drv_cat.cat,name:n,title:t});
						 });
			}
			else
			{
				cb(true,{drv:cur_drv,catid:drv_cat.cat,name:n,title:t});
			}
		}
	});
}


function drv_sel_chg()
{
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
			tmps += "<option value='"+a.id+"'>"+a.t+"-["+a.n+"]</option>";
		}
		$("#var_cat").html(tmps) ;
	}).fail(function(req, st, err) {
		dlg.msg(err);
	});
}

function add_cat()
{		
	var drv = cur_drv;
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

function del_cat()
{
	var drv_cat = get_cur_drv_cat() ;
	if(drv_cat==null)
	{
		dlg.msg("please select driver and category");
		return ;
	}
	var drv = drv_cat.drv ;
	var catid  = drv_cat.cat ;

	if(dlg.confirm("Deleting the category will delete all the devices below. Are you sure?",null,()=>{
		send_ajax("../dev/cat_ajax.jsp",{drv:drv,op:'del',catid:catid},(bsucc,ret)=>{
			if(!bsucc||ret!='succ')
			{
				dlg.msg(ret) ;
				return ;
			}
			drv_sel_chg() ;
		});
	})) ;
}
					
function get_cur_drv_cat()
{
	var drv = cur_drv;
	var catnt = get_cur_cat_id_title();
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
		  window.open("devdef_editor.jsp?drv="+vv.drv+"&catid="+vv.cat+"&id="+data.id) ;
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
  
  table.on('row(tb_devdefs)', function(obj)
		  {
			  var data = obj.data; //cur d
			  
			  $("#devdef_name").val(data.n) ;
			  $("#devdef_title").val(data.t) ;
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
	    ,url: 'devdef_ajax.jsp?op=list_tb&drv='+drv_cat.drv+'&catid='+drv_cat.cat
	    ,page0: {layout:['prev', 'page', 'next'],limit:10,theme:"#c00"} //open page
	    ,cols: [[ //head
	    	{field: 'n', title: 'Name', width:'40%'}
	    	,{field: 't', title: 'Title', width:'40%'}
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
	var catnt = get_cur_cat_id_title() ;
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
						 ret.catid = catn ;
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