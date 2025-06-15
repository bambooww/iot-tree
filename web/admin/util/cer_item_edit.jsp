<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,org.iottree.core.util.cer.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%
//if(!Convert.checkReqEmpty(request, out, "cat","id"))
//	return ;
String cat = request.getParameter("cat") ;
	String id = request.getParameter("id") ;
	CerCat cer_c = null ;
	CerItem cer_i  = null;
	
	String title = "" ;
	if(Convert.isNotNullEmpty(cat) && Convert.isNotNullEmpty(id))
	{
		cer_c = CerManager.getInstance().getCat(cat) ;
		if(cer_c==null)
		{
			out.print("no cat found") ;
			return ;
		}
		cer_i = cer_c.getCerItemById(id) ;
		if(cer_i==null)
		{
			out.print("no item found") ;
			return ;
		}
		
		title = cer_i.getTitle() ;
	}
	
	
%>
<html>
<head>
<title></title>
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
<form class="layui-form" action="" onsubmit="return false;">
 
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:g>title</wbt:g></label>
    <div class="layui-input-inline" style="width:200px;">
      <input type="text" name="title" id="title" value="<%=title %>"  autocomplete="off" class="layui-input"/>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Org</label>
    <div class="layui-input-inline" style="width:200px;">
      <input type="text" name="org" id="org" value=""  autocomplete="off" class="layui-input"/>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Org Unit</label>
    <div class="layui-input-inline" style="width:200px;">
      <input type="text" name="org_unit" id="org_unit" value=""  autocomplete="off" class="layui-input"/>
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
	var tt = $('#title').val();
	if(tt==null||tt=='')
	{
		cb(false,'<wbt:g>pls,input,title</wbt:g>') ;
		tt = n;
	}
	let org = $("#org").val();
	if(!org)
	{
		cb(false,'no org input') ;
		tt = n;
	}
	let org_unit = $("#org_unit").val()||"";
	
	cb(true,{title:tt,org:org,org_unit:org_unit});
}

</script>
</html>