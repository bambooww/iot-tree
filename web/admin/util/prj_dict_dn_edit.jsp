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
if(!Convert.checkReqEmpty(request, out, "prjid","cid"))
	return ;
	String prjid = request.getParameter("prjid") ;
UAPrj rep  = UAManager.getInstance().getPrjById(prjid) ;
if(rep==null)
{
	out.print("no prj found");
	return ;
}
String cid = request.getParameter("cid") ;
String nname = request.getParameter("name") ;

PrjDataClass pdc = DictManager.getInstance().getPrjDataClassByPrjId(prjid) ;
DataClass dc = pdc.getDataClassById(cid) ;
if(dc==null)
{
	out.println("no PrjDataClass found");
	return ;
}
String name = "" ;
String title = "" ;
String desc = "" ;
DataNode dn = null;
if(Convert.isNotNullEmpty(nname))
{
	dn = dc.getNodeByName(nname) ;
	if(dn==null)
	{
		out.print("no DataNode found") ;
		return ;
	}
	name = dn.getName() ;
	title = dn.getTitle() ;
}
%>
<html>
<head>
<title>DataNode editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(600,300);
</script>
<style>
</style>
</head>
<body>
<form class="layui-form" action="">
 <div class="layui-form-item">
    <label class="layui-form-label">Name</label>
    <div class0="layui-input-block" class="layui-input-inline">
      <input type="text" name="name" id="name" value="<%=name %>"  autocomplete="off" class="layui-input"/>
    </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label">Title</label>
    <div class="layui-input-inline">
      <input type="text" name="title" id="title" value="<%=title %>"  autocomplete="off" class="layui-input"/>
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
		cb(false,'<wbt:g>pls,input,name</wbt:g>') ;
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
	cb(true,{name:n,title:tt,desc:desc});
	//var dbname=document.getElementById('db_name').value;
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>