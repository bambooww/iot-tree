<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
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
UAPrj rep  = UAManager.getInstance().getPrjById(prjid) ;
if(rep==null)
{
	out.print("no prj found");
	return ;
}
PrjDataClass pdc = DictManager.getInstance().getPrjDataClassByPrjId(prjid) ;
String id = request.getParameter("id") ;

String name = "" ;
String title = "" ;
String desc = "" ;

if(Convert.isNotNullEmpty(id))
{
	DataClass dc = pdc.getDataClassById(id) ;
	if(dc==null)
	{
		out.println("no PrjDataClass found");
		return ;
	}
	
	name = dc.getClassName() ;
	title = dc.getClassTitle() ;
}
%>
<html>
<head>
<title>DataClass editor</title>
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
    <label class="layui-form-label"></label>
    <div class0="layui-input-block" class="layui-input-inline">
      
    </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Text</label>
    <div class="layui-input-block">
      <textarea name="txt" id="txt" cols="60" rows="10" class0="layui-input" title="name1 title1&#10;name2 title2&#10;..."></textarea>
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
	var tt = $('#txt').val();
	tt = trim(tt) ;
	if(tt==null||tt=='')
	{
		cb(false,'请输入导入文本') ;
		return ;
	}
	
	var desc = "";//document.getElementById('desc').value;
	if(desc==null)
		desc ='' ;
	cb(true,{txt:tt});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>