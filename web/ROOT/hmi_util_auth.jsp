<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<html>
<head>
<title>auth</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script>
dlg.resize_to(400,250);
</script>
</head>
<body>
<form class="layui-form" action="">
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>user</wbt:lang></label>
    <div class="layui-input-inline">
      <input type="text" name="user" id="user" class="layui-input" autocomplete="new-password"/>
    </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>psw</wbt:lang></label>
    <div class="layui-input-inline">
      <input type="password" name="psw" id="psw" class="layui-input" autocomplete="new-password"/>
    </div>
  </div>
   
</form>
</body>
<script type="text/javascript">
function win_close()
{
	dlg.close(0);
}
function do_submit(cb)
{
	var user = document.getElementById('user').value;
	if(user==null||user=='')
	{
		cb(false,'<wbt:lang>pls_input</wbt:lang><wbt:lang>user</wbt:lang>') ;
		return ;
	}
	var psw = document.getElementById('psw').value;
	if(psw==null||psw=='')
	{
		cb(false,'<wbt:lang>pls_input</wbt:lang><wbt:lang>psw</wbt:lang>') ;
		return ;
	}
	
	cb(true,{user:user,psw:psw});
	
	//document.getElementById('form1').submit() ;
}

</script>
</html>