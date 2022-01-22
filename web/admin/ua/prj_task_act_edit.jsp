<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.task.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request, out, "taskid","taskid"))
	return ;
	String prjid=request.getParameter("prjid");
	String taskid=request.getParameter("taskid");
	String actid=request.getParameter("actid");
	
	Task jst = TaskManager.getInstance().getTask(prjid, taskid) ;
	TaskAction ta = null ;
	if(Convert.isNotNullEmpty(actid))
	{
		ta = jst.getActionById(actid) ;
		if(ta==null)
		{
	out.print("no task action found") ;
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

if(ta!=null)
{
		name = ta.getName() ;
		desc = ta.getDesc() ;
		init_sc = ta.getInitScript();
		run_sc =  ta.getRunScript();
		end_sc =ta.getEndScript();
		benable = ta.isEnable() ;
}

if(actid==null)
	actid = "" ;
if(name==null)
	name = "" ;
if(title==null)
	title = "" ;
if(desc==null)
	desc = "" ;

String chked_en = "" ;

if(benable)
	chked_en = "checked=checked";

UAPrj prj = jst.getPrj() ;
String prj_path = prj.getNodePath() ;
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
dlg.resize_to(850,700);
</script>
</head>
<body>
<form class="layui-form" action="">
<div class="layui-form-item">
     <label class="layui-form-label"><wbt:lang>name</wbt:lang></label>
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="text" name="name" id="name" value="<%=name%>" autocomplete="off" class="layui-input"/>
    </div>
	  <div class="layui-form-mid">Enable:</div>
	  <div class="layui-input-inline" style="width: 70px;">
	  <input type="checkbox" id="enable" name="enable" <%=chked_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>desc</wbt:lang></label>
    <div class="layui-input-block" >
      <input type="text" name="desc" id="desc" value="<%=desc %>" autocomplete="off" class="layui-input"/>
    </div>
   
  </div>
   <div class="layui-form-item">
    <label class="layui-form-label">init script</label>
    <div class="layui-input-block" >
      <textarea id="ta_init_sc" rows="5" cols="180" style="width:99%;height:45%"><%=init_sc %></textarea>
      <br/><a class="layui-form-mid layui-word-aux" onclick="insert_tag()">insert tag</a>
      <a class="layui-form-mid layui-word-aux" onclick="test_script()">test</a>
    </div>
  </div>
  <div class="layui-tab" lay-filter="tabDemo">
  <ul class="layui-tab-title">
    
    <li class="layui-this" lay-id="2">run script</li>
    <li lay-id="3">end script</li>
  </ul>
  <div class="layui-tab-content">
    <div class="layui-tab-item layui-show">
   function(){
      <textarea id="ta_run_sc" rows="20" cols="180" style="width:99%;height:20%"><%=run_sc %></textarea>
      }
    </div>
    <div class="layui-tab-item">
      <textarea id="ta_end_sc" rows="20" cols="180" style="width:99%;height:20%"><%=end_sc %></textarea>
	</div>
    
  </div>
</div>
  
</form>
</body>
<script type="text/javascript">
var prjid="<%=prjid%>";
var prj_path = "<%=prj_path%>" ;

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
	
	//var desc = document.getElementById('desc').value;
	//if(desc==null)
		desc ='' ;
		var ben = $("#enable").prop("checked") ;
	var init_sc = $("#ta_init_sc").val();
	var run_sc = $("#ta_run_sc").val();
	var end_sc = $("#ta_end_sc").val();
	//var dbname=document.getElementById('db_name').value;
	cb(true,{"dx_/enable:bool":ben,"dx_/name:string":n,"dx_/desc:string":desc,"dx_/init_script:string":init_sc,"dx_/run_script:string":run_sc,"dx_/end_script:string":end_sc})
	
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