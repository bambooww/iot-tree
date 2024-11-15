<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.store.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.station.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%>
<%
	String id = request.getParameter("id") ;

if(id==null)
	id="" ;
String tt="" ;
String key="" ;

PStation ps = null ;
if(Convert.isNotNullEmpty(id))
{
	ps = PlatInsManager.getInstance().getStationById(id) ;
	if(ps==null)
	{
		out.print("no pstation found") ;
		return ;
	}
	tt = ps.getTitle() ;
	key = ps.getKey() ;
}
%>
<html>
<head>
<title>remote station editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(500,300);
</script>
<style>
</style>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Station Id:</label>
    <div class="layui-input-inline" style="width: 250px;">
      <input type="text" id="id" name="id" value="<%=id%>"  autocomplete="off"  class="layui-input" <%=(ps!=null?"readonly":"") %>>
    </div>
    
	  <%--
	  <div class="layui-form-mid"><w:g>enable</w:g>:</div>
	  <div class="layui-input-inline" style="width: 150px;">
	    <input type="checkbox" id="enable" name="enable" <%=chked%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
	   --%>
 </div>
  <div class="layui-form-item">
<div class="layui-form-label"><w:g>title</w:g>:</div>
	  <div class="layui-input-inline" style="width: 250px;">
	    <input type="text" id="tt" name="tt" value="<%=tt%>"  autocomplete="off" class="layui-input">
	  </div>
</div>
  <div class="layui-form-item">
<div class="layui-form-label">Key:</div>
	  <div class="layui-input-inline" style="width: 250px;">
	    <input type="text" id="key" name="key" value="<%=key%>"  autocomplete="off" class="layui-input">
	  </div>
</div>
 </form>
</body>
<script type="text/javascript">
var id = "<%=id%>" ;

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
	var id = $('#id').val();
	if(!id)
	{
		cb(false,'<w:g>pls,input</w:g>Station ID') ;
		return ;
	}
	var tt = $('#tt').val();
	if(tt==null||tt=='')
	{
		tt = n;
	}
	

	var ben = $("#enable").prop("checked") ;
	
	var key = $('#key').val();
	if(!key)
	{
		cb(false,'<w:g>pls,input</w:g>Key') ;
		return ;
	}
	
	cb(true,{id:id,tt:tt,enable:ben,key:key});
}

</script>
</html>