<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.util.web.*,
	org.iottree.core.util.xmldata.*
"%>
<%@ taglib uri="wb_tag" prefix="wbt"%><%
	boolean has_setpsw = LoginUtil.checkAdminSetPsw() ;
%><!DOCTYPE html>
<html>
<head>
<title>IOT Tree Server</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
</head>
<style>

</style>
<body>
  <div id="header" style="white-space:nowrap;top:0;width:100%;height:70px;background-color: #dfdfdf">
  	<img src="../inc/logo3.png"/>
  </div>
  <div style="margin:0 auto;margin-top:100px;width:520px;height:500px; line-height:100px; text-align:center; border:0px solid #F00">
  <%
if(!has_setpsw)
{
%>
   <h3>You must setup admin's password</h3>
<%
}
else
{
	%>
	<h3>Login to admin panel</h3>
	<%
}
%>
  <form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">User</label>
    <div class="layui-input-block">
      <input type="text" id="username" name="username" value="admin" readonly="readonly" required lay-verify="required" autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Password</label>
    <div class="layui-input-block">
      <input type="password" id="psw" name="psw" value=""  placeholder="Please Input Password" autocomplete="off" class="layui-input">
    </div>
  </div>
<%
if(!has_setpsw)
{
%>
  
 <div class="layui-form-item">
    <label class="layui-form-label">Re Password</label>
    <div class="layui-input-block">
      <input type="password" id="repsw"  name="repsw" placeholder="Please Input Password Again" autocomplete="off" class="layui-input">
    </div>
  </div>
    <div class="layui-form-item">
    <div class="layui-input-block">
      <button class="layui-btn" lay-submit lay-filter="setpsw_login">Set Password and Login</button>
      <button type="reset" class="layui-btn layui-btn-primary">Reset</button>
    </div>
  </div>
<%
}
else
{
%>
  <div class="layui-form-item">
    <div class="layui-input-block">
      <button class="layui-btn" lay-submit lay-filter="login">Login</button>
      <button type="reset" class="layui-btn layui-btn-primary">Reset</button>
    </div>
  </div>
<%
}
%>
</form>
</div>

</body>
<script type="text/javascript">
layui.use('form', function(){
	  var form = layui.form;

	  form.on('submit(login)', function(data){
	    var psw = $("#psw").val() ;
	    if(psw==null||psw=="")
	    {
	    	dlg.msg("Please input password") ;
	    	return false;
	    }
	    do_login(psw);
	    return false;
	  });
	  
	  form.on('submit(setpsw_login)', function(data){
		  var psw = $("#psw").val() ;
		  var repsw = $("#repsw").val() ;
		    if(psw==null||psw=="")
		    {
		    	dlg.msg("Please input password") ;
		    	return false;
		    }
		    if(repsw==null||repsw=="")
		    {
		    	dlg.msg("Please input repassword") ;
		    	return false;
		    }
		    if(psw!=repsw)
		    {
		    	dlg.msg("password and repassword is no equal") ;
		    	return false;
		    }
		    do_login(psw);
		    return false;
		  });
	  
	});

function do_login(psw)
{
	$.ajax({
        type: 'post',
        url:'./login_ajax.jsp',
        data: {op:"login",user:"admin",psw:psw},
        async: true,  
        success: function (result) {  
        	if("succ"==result)
        	{
        		document.location.href="/admin/" ;
        	}
        	else
        	{
        		dlg.msg("Login failed") ;
        	}
        },
        error:function(req,err,e)
        {
        	dlg.msg(e);
        }
    });
}

$("#psw").focus();
</script>
</html>
