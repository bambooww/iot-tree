<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	com.xxx.app.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
"%><!DOCTYPE html>
<html>
<head>
<title></title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<link  href="../inc/1.css"  rel="stylesheet" type="text/css" >
</head>
<style>
.layui-form-label
{
	width:150px;
}
</style>
<body>
<div class="top_nav">
	<span class="nav_item " style="background-color: #2d363c;width:300px;">IOT-Tree Main Demo</span>
</div>
  <div style="margin:0 auto;margin-top:100px;width:520px;height:500px; line-height:100px; text-align:center; border:0px solid #F00">

	<h3>User Login</h3>
  <form class="layui-form" onsubmit="return false;">
  <div class="layui-form-item">
    <label class="layui-form-label">User</label>
    <div class="layui-input-inline" style="width:300px;">
      <input type="text" id="username" name="username" value="" required lay-verify="required" autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Password</label>
    <div class="layui-input-inline" style="width:300px;">
      <input type="password" id="psw" name="psw" value=""   autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <div class="layui-input-block">
      <button class="layui-btn"  lay-filter="login" onclick="do_login()">Login</button>
      <button type="reset" class="layui-btn layui-btn-primary">Reset</button>
    </div>
  </div>
</form>
</div>

</body>
<script type="text/javascript">
layui.use('form', function(){
	  var form = layui.form;

	 
	  form.render();
	});

function do_login()
{
	let usr =  $("#username").val() ;
	if(!usr)
    {
    	dlg.msg("Please input user") ;
    	return false;
    }
	var psw = $("#psw").val() ;
    if(!psw)
    {
    	dlg.msg("Please input password") ;
    	return false;
    }
    
	$.ajax({
        type: 'post',
        url:'./login_ajax.jsp',
        data: {op:"login",user:usr,psw:psw},
        async: true,  
        success: function (result) {
        	console.log(result) ;
        	
        	if("ok"==result)
        	{
        		document.location.href="../index.jsp" ;
        	}
        	else
        	{
        		dlg.msg("Login Failed") ;
        	}
        },
        error:function(req,err,e)
        {
        	dlg.msg(e);
        }
    });
}

$("#username").focus();
</script>
</html>
