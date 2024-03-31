<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	String reppath = request.getParameter("rep_path") ;
String chpath = request.getParameter("ch_path") ;
UACh ch= null;
UAPrj rep = null ;
if(Convert.isNotNullEmpty(reppath))
{
	rep  = (UAPrj)UAUtil.findNodeByPath(reppath) ;
	if(rep==null)
	{
		out.print("no rep node found");
		return ;
	}
	
}
else if(Convert.isNotNullEmpty(chpath))
{
	ch = (UACh)UAUtil.findNodeByPath(chpath) ;
	if(ch==null)
	{
		out.print("no channel node found");
		return ;
	}
	rep = ch.getBelongTo() ;
}
else
{
	out.print("no path input") ;
	return ;
}

String drv_name = "" ;
String drv_tt="" ;
String name = "" ;
String title = "" ;
String desc = "" ;

if(ch!=null)
{
	name = ch.getName() ;
	title = ch.getTitle() ;
	desc = ch.getDesc() ;
	if(desc==null)
		desc="" ;
	DevDriver dd = ch.getDriver() ;
	if(dd!=null)
	{
		drv_name = dd.getName() ;
		drv_tt = dd.getTitle() ;
	}
}

	List<DevDriver> dds = DevManager.getInstance().getDrivers() ;
%>
<html>
<head>
<title>channel editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(600,400);
</script>
<style>
</style>
</head>
<body>
<form class="layui-form" action="">
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>name</wbt:lang></label>
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="text" name="name" id="name" value="<%=name %>"  class="layui-input"/>
    </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>title</wbt:lang></label>
    <div class="layui-input-block">
      <input type="text" name="title" id="title" value="<%=title %>"  class="layui-input"/>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>driver</wbt:lang></label>
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="text" name="drv_title" id="drv_title" value="<%=drv_tt %>" onclick="select_drv()"  class="layui-input" autocomplete="off"/>
      <input type="hidden" name="drv" id="drv" value="<%=drv_name %>" />
    </div>
  </div>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label"><wbt:lang>description</wbt:lang></label>
    <div class="layui-input-block">
      <textarea name="desc" id="desc" class="layui-textarea"><%=desc %></textarea>
    </div>
  </div>
</form>
</body>
<script type="text/javascript">

layui.use('form', function(){
	  var form = layui.form;
	  form.render();
	});
	
function win_close()
{
	dlg.close(0);
}

function select_drv()
{
	let drv_name = $("#drv").val() ;
	let drv_tt = $("#drv_title").val() ;
	dlg.open("drv_selector.jsp?edit=true",
			{title:"<wbt:lang>sel_drv_title</wbt:lang>",w:'400',h:'500',drv_name:drv_name,drv_tt:drv_tt},
			//[{title:'Ok',style:""},{title:'Clear',style:"primary"},{title:'Cancel',style:"primary"}],
 			["<wbt:g>ok</wbt:g>","<wbt:g>unselect</wbt:g>","<wbt:g>cancel</wbt:g>"],
			[
				function(dlgw)
				{
					dlgw.do_submit(function(res,ret){
						if(res)
						{
							$("#drv_title").val(ret.title) ;
							$("#drv").val(ret.name) ;
							dlg.close();
						}
						else
						{
							dlg.msg(ret) ;
						}
					}) ;
					
				},
				function(dlgw)
				{
					$("#drv_title").val("") ;
					$("#drv").val("") ;
					dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function do_submit(cb)
{
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'<wbt:g>pls,input,name</wbt:g>') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt = n;
	}
	var drv = $('#drv').val() ;
	if(false)//if(drv==null||drv=='')
	{
		cb(false,'<wbt:g>pls,sel,driver</wbt:g>') ;
		return ;
	}
	var desc = document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	cb(true,{drv:drv,name:n,title:tt,desc:desc});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>