<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.dict.*,
				org.iottree.core.store.*,
				org.iottree.core.basic.*,
				org.iottree.core.util.web.*,
				org.iottree.core.store.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="w"%>
<%

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
dlg.resize_to(500,300);
</script>
<style>
.layui-form-label
{
	width:100px;
}
</style>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>oldpsw</w:g>:</label>
    <div class="layui-input-inline" style="width:60%;">
      <input type="password" id="old_psw" name="old_psw" value=""  autocomplete="off"  class="layui-input" />
    </div>
 </div>
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>newpsw</w:g>:</label>
    <div class="layui-input-inline" style="width:60%;">
      <input type="password" id="new_psw" name="new_psw" value=""  autocomplete="off"  class="layui-input" />
    </div>
  </div>
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>repsw</w:g>:</label>
    <div class="layui-input-inline" style="width:60%;">
      <input type="password" id="re_psw" name="re_psw" value=""  autocomplete="off"  class="layui-input" />
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
	let old_psw = $('#old_psw').val();
	let new_psw = $('#new_psw').val();
	let re_psw = $('#re_psw').val();
	if(!old_psw)
	{
		cb(false,'<w:g>pls,input,oldpsw</w:g>') ;
		return ;
	}
	if(!new_psw || !re_psw)
	{
		cb(false,'<w:g>pls,input,newpsw</w:g>') ;
		return ;
	}
	if(new_psw!=re_psw)
	{
		cb(false,'<w:g>newpsw</w:g>!=<w:g>repsw</w:g>') ;
		return ;
	}
	cb(true,{oldpsw:old_psw,newpsw:new_psw,repsw:re_psw});
}

</script>
</html>
