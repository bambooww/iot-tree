<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.store.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return ;
	String prjid = request.getParameter("prjid") ;
	String storeid = request.getParameter("id") ;
UAPrj rep  = UAManager.getInstance().getPrjById(prjid) ;
if(rep==null)
{
	out.print("no prj found");
	return ;
}

StoreManager stmgr = StoreManager.getInstance(prjid) ;
Store st = stmgr.getStoreById(storeid) ;
if(st==null)
{
	out.print("no store found") ;
	return ;
}

if(Convert.isNotNullEmpty(id))
{
}
%>
<html>
<head>
<title>jdbc editor</title>
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
    <label class="layui-form-label">Name</label>
    <div class="layui-input-block" class="layui-input-inline">
      <input type="text" name="name" id="name" value="" class="layui-input"/>
    </div>
    <div class="layui-form-mid">Enable</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable"  lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Title</label>
    <div class="layui-input-inline">
      <input type="text" name="title" id="title" value="" class="layui-input"/>
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
	var n = $('#name').val();
	if(n==null||n=='')
	{
		cb(false,'please input name') ;
		return ;
	}
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		tt = n;
	}
	
	var desc = "";//document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	
	var bind_for='' ;
	$('input[type=checkbox]:checked').each(function() {
		var bf = $(this).val() ;
	      if(bf)
	    	  bind_for+=','+bf;
	    });
	
	if(bind_for!='')
		bind_for = bind_for.substr(1) ;
	var bind_style=$("input[name='bind_style']:checked").val();
	if(!bind_style)
		bind_style="" ;
	var ben = $("#enable").prop("checked") ;
	cb(true,{name:n,title:tt,enable:ben,desc:desc,bind_for:bind_for,bind_style:bind_style});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>