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
UARep rep = null ;
if(Convert.isNotNullEmpty(reppath))
{
	rep  = (UARep)UAUtil.findNodeByPath(reppath) ;
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
      <input type="text" name="name" id="name" value="<%=name %>" placeholder="<wbt:lang>pls_input</wbt:lang><wbt:lang>name</wbt:lang>" autocomplete="off" class="layui-input"/>
    </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>title</wbt:lang></label>
    <div class="layui-input-block">
      <input type="text" name="title" id="title" value="<%=title %>" placeholder="<wbt:lang>pls_input</wbt:lang><wbt:lang>title</wbt:lang>" autocomplete="off" class="layui-input"/>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>driver</wbt:lang></label>
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="text" name="drv_title" id="drv_title" value="<%=drv_tt %>" onclick="select_drv()" placeholder="<wbt:lang>pls_input</wbt:lang><wbt:lang>driver</wbt:lang>" autocomplete="off" class="layui-input"/>
      <input type="hidden" name="drv" id="drv" value="<%=drv_name %>" />
      <%--
      <select name="drv" id="drv" >
<%
for(DevDriver dd:dds)
{
%>
			<option value="<%=dd.getName()%>"><%=dd.getTitle() %></option>
<%
}
%>
		</select>
		<div class="layui-form-mid layui-word-aux" onclick="select_drv()">select</div>
		 --%>
		
    </div>
  </div>
  <div class="layui-form-item layui-form-text">
    <label class="layui-form-label"><wbt:lang>description</wbt:lang></label>
    <div class="layui-input-block">
      <textarea name="desc" id="desc" placeholder="<wbt:lang>pls_input</wbt:lang><wbt:lang>description</wbt:lang>" class="layui-textarea"><%=desc %></textarea>
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
	dlg.open_win("drv_selector.jsp?edit=true",
			{title:"<wbt:lang>sel_drv_title</wbt:lang>",w:'400',h:'535'},
			[{title:'<wbt:lang>ok</wbt:lang>',style:""},{title:'<wbt:lang>cancel</wbt:lang>',style:"primary"}],
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
					dlg.close();
				}
			]);
}

function do_submit(cb)
{
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'请输入名称') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'请输入标题') ;
		return ;
	}
	var drv = $('#drv').val() ;
	if(drv==null||drv=='')
	{
		cb(false,'请选择驱动') ;
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