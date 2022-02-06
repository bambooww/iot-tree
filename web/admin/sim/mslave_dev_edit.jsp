<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.task.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.core.sim.*,
				org.iottree.driver.common.modbus.sim.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
	if(!Convert.checkReqEmpty(request, out,"insid","chid"))
	return ;

	String insid=request.getParameter("insid");
	SimInstance ins = SimManager.getInstance().getInstance(insid) ;
	if(ins==null)
	{
		out.print("no instance found") ;
		return ;
	}
	
	String chid=request.getParameter("chid");
	String devid=request.getParameter("devid");
	
	SimChannel sch = ins.getChannel(chid);
	if(sch==null)
	{
		out.print("no channel found") ;
		return ;
	}
	SimDev dev = null ;
	if(Convert.isNotNullEmpty(devid))
	{
		dev = sch.getDev(devid) ;
		if(dev==null)
		{
	out.print("no device found") ;
	return ;
		}
	}
String name = "" ;
String title = "" ;
String desc = "" ;
String init_sc = "" ;
String run_sc = "" ;
String end_sc = "" ;

boolean benable = true ;

if(dev!=null)
{
		name = dev.getName() ;
		title = dev.getTitle() ;
		benable = dev.isEnable() ;
}

if(name==null)
	name = "" ;
if(title==null)
	title = "" ;
if(desc==null)
	desc = "" ;

String chked_en = "" ;

if(benable)
	chked_en = "checked=checked";
%>
<html>
<head>
<title>task editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(550,300);
</script>
</head>
<body>
<form class="layui-form" action="">
<div class="layui-form-item">
     <label class="layui-form-label">Name</label>
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="text" name="name" id="name" value="<%=name%>" autocomplete="off" class="layui-input"/>
    </div>
	  <div class="layui-form-mid">Enable:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	  <input type="checkbox" id="enable" name="enable" <%=chked_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Title</label>
    <div class="layui-input-block" >
      <input type="text" name="title" id="title" value="<%=title %>" autocomplete="off" class="layui-input"/>
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

function do_submit(cb)
{
	var n = document.getElementById('name').value;
	if(n==null||n=='')
	{
		cb(false,'<wbt:lang>pls_input</wbt:lang><wbt:lang>name</wbt:lang>') ;
		return ;
	}
	
	var title = document.getElementById('title').value;
	if(title==null)
		title ='' ;
		var ben = $("#enable").prop("checked") ;
	var init_sc = $("#ta_init_sc").val();
	var run_sc = $("#ta_run_sc").val();
	var end_sc = $("#ta_end_sc").val();
	//var dbname=document.getElementById('db_name').value;
	cb(true,{"dx_/enable:bool":ben,"dx_/name:string":n,"dx_/title:string":title})
	
	//document.getElementById('form1').submit() ;
}


function insert_tag()
{
	dlg.open("../ua_cxt/di_cxt_tag_selector.jsp?path="+prj_path,//+"&val="+tmpv,
			{title:"Select Tag in Context",w:'500px',h:'400px'},
			['Ok','Cancel'],
			[
				function(dlgw)
				{
					var ret = dlgw.get_val() ;
					insertAtCursor("ta_init_sc", ret);
					 dlg.close();
				},
				function(dlgw)
				{
					dlg.close();
				}
			]);
}

function insertAtCursor(txtarea_id, txt)
{
	var txtarea = document.getElementById(txtarea_id) ;
	if(txtarea==null)
		return ;
	if (document.selection)
	{//IE support
		txtarea.focus();
		sel = document.selection.createRange();
		sel.text = txt;
		sel.select();
	}
	else if(txtarea.selectionStart || txtarea.selectionStart == '0')
	{//MOZILLA/NETSCAPE support
		var startPos = txtarea.selectionStart;
		var endPos = txtarea.selectionEnd;
		// save scrollTop before insert www.keleyi.com
		var restoreTop = txtarea.scrollTop;
		txtarea.value = txtarea.value.substring(0, startPos) + txt + txtarea.value.substring(endPos, txtarea.value.length);
		if (restoreTop > 0) {
		txtarea.scrollTop = restoreTop;
		}
		txtarea.focus();
		txtarea.selectionStart = startPos + txt.length;
		txtarea.selectionEnd = startPos + txt.length;
	}
	else
	{
		txtarea.value += txt;
		txtarea.focus();
	}
}
</script>
</html>