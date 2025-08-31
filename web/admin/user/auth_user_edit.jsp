<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%>
<%

%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(480,400);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">注册名</label>
    <div class="layui-input-inline" style="width:330px;">
      <input type="text" id="username" name="username" value=""  autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">显示名</label>
    <div class="layui-input-inline" style="width:330px;">
      <input type="text" id="disname" name="disname"  autocomplete="off" class="layui-input">
    </div>
  </div>
  <div class="layui-form-item" id="psw_c">
    <label class="layui-form-label">密码</label>
    <div class="layui-input-inline" style="width:330px;">
      <input type="password" id="psw" name="psw" value=""  autocomplete="off" class="layui-input">
    </div>
  </div>
  
 </form>
</body>
<script type="text/javascript">
var pm = dlg.get_opener_opt("pm") ;
if(pm)
{
	$("#username").val(pm.username||"") ;
	if(pm.username)
		$("#username").prop("readonly",true) ;
	$("#disname").val(pm.disname||"") ;
	$("#psw_c").css("display","none") ;
	$("#psw").val(pm.psw||"") ;
	
}

layui.use('form', function(){
	  var form = layui.form;
});
	
function win_close()
{
	dlg.close(0);
}

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
}

function chk_user_name(n,failedr)
{
	const regex = /^1[3-9]\d{9}$/;
    if(regex.test(n))
    	return true ;
    let fr={};
	if(chk_var_name(n,true,fr))
		return true ;
	if(failedr)
		failedr.txt = "注册名必须a-z A-Z开头的无空格字符串";
	return false;
}

function do_submit(cb)
{
	let n = $("#username").val() ;
	if(!n)
	{
		cb(false,"请输入名称") ;
		return ;
	}
	let fr={} ;
	if(!chk_user_name(n,fr))
	{
		cb(false,fr.txt) ;
		return;
	}
	let tt = $('#disname').val();
	let psw = $('#psw').val();
	if(tt==null||tt=='')
	{
		cb(false,'<wbt:g>pls,input,title</wbt:g>') ;
		return ;
	}
	//let email = $('#email').val();
	//let phone = $("#phone").val()||"" ;
	cb(true,{username:n,disname:tt,psw:psw});
}

</script>
</html>                                                                                                                                                                                                                            