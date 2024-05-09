<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,org.iottree.core.plugin.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.util.*,
	org.iottree.core.util.web.*,
	org.iottree.core.util.xmldata.*
"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	boolean has_setpsw = LoginUtil.checkAdminSetPsw() ;
    if(!has_setpsw)
    {
    	Boolean b = (Boolean)session.getAttribute("accept_license_terms");
    	if(b==null||!b)
    	{
    		response.sendRedirect("license_terms.jsp");
    		return ;
    	}
    }
    
    PlugAuth pa = PlugManager.getInstance().getPlugAuth() ;
    if(pa!=null)
    	has_setpsw = true ;
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
.top_right
{
	position: absolute;
	right:20px;
	
}
.layui-form-label
{
	width:150px;
}
</style>
<body>
  <div id="header" style="white-space:nowrap;top:0;width:100%;height:70px;background-color: #dfdfdf">
  	<img src="../inc/logo3.png"/>
  	<div class="top_right"><%=Lan.getUsingLang()%></div>
  </div>
  <div style="margin:0 auto;margin-top:100px;width:520px;height:500px; line-height:100px; text-align:center; border:0px solid #F00">
  <%
if(!has_setpsw)
{
%>
   <h3><wbt:lang>yms_admin_psw</wbt:lang></h3>
<%
}
else
{
	%>
	<h3><wbt:lang>admin_login</wbt:lang></h3>
	<%
}
%>
  <form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>user</wbt:lang></label>
    <div class="layui-input-inline" style="width:300px;">
      <input type="text" id="username" name="username" value="admin" required autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>psw</wbt:lang></label>
    <div class="layui-input-inline" style="width:300px;">
      <input type="password" id="psw" name="psw" value=""   autocomplete="off" class="layui-input">
    </div>
  </div>
<%
if(!has_setpsw)
{
%>
  
 <div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>repsw</wbt:lang></label>
    <div class="layui-input-inline" style="width:300px;">
      <input type="password" id="repsw"  name="repsw" autocomplete="off" class="layui-input">
    </div>
  </div>
    <div class="layui-form-item">
    <label class="layui-form-label"></label>
    <div class="layui-input-inline" style="width:300px;">
      <button class="layui-btn" lay-submit lay-filter="setpsw_login"><wbt:lang>set_psw_login</wbt:lang></button>
      <button type="reset" class="layui-btn layui-btn-primary"><wbt:lang>reset</wbt:lang></button>
    </div>
  </div>
<%
}
else
{
%>
<%--
<div class="layui-form-item">
    <label class="layui-form-label"><wbt:lang>use_lang</wbt:lang></label>
    <div class="layui-input-block" style0="width:500px;">
		<input type="radio" name="lan" value="en" title="English" checked>
       <input type="radio" name="lan" value="cn" title="中文">
    </div>
  </div>
   --%>
  <div class="layui-form-item">
    <div class="layui-input-block">
      <button class="layui-btn" lay-submit lay-filter="login"><wbt:lang>login</wbt:lang></button>
      <button type="reset" class="layui-btn layui-btn-primary"><wbt:lang>reset</wbt:lang></button>
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
		  var user = $("#username").val() ;
		  if(!user)
		  {
			  dlg.msg("<wbt:lang>pls,input,user</wbt:lang>") ;
			  return false;
		  }
	    var psw = $("#psw").val() ;
	    if(psw==null||psw=="")
	    {
	    	dlg.msg("<wbt:lang>pls_inp_psw</wbt:lang>") ;
	    	return false;
	    }
	    do_login(user,psw);
	    return false;
	  });
	  
	  form.on('submit(setpsw_login)', function(data){
		  var user = $("#username").val() ;
		  if(!user)
		  {
			  dlg.msg("<wbt:lang>pls,input,user</wbt:lang>") ;
			  return false;
		  }
		  var psw = $("#psw").val() ;
		  var repsw = $("#repsw").val() ;
		    if(psw==null||psw=="")
		    {
		    	dlg.msg("<wbt:lang>pls_inp_psw</wbt:lang>") ;
		    	return false;
		    }
		    if(repsw==null||repsw=="")
		    {
		    	dlg.msg("<wbt:lang>pls_inp_repsw</wbt:lang>") ;
		    	return false;
		    }
		    if(psw!=repsw)
		    {
		    	dlg.msg("<wbt:lang>psw_repsw_neq</wbt:lang>") ;
		    	return false;
		    }
		    do_login(user,psw);
		    return false;
		  });
	  form.render();
	});

function do_login(user,psw)
{
	$.ajax({
        type: 'post',
        url:'./login_ajax.jsp',
        data: {op:"login",user:user,psw:psw},
        async: true,  
        success: function (result) {  
        	if("succ"==result)
        	{
        		document.location.href="/admin/" ;
        	}
        	else
        	{
        		dlg.msg("<wbt:lang>login_failed</wbt:lang>") ;
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
