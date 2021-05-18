<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="org.iottree.user.*,org.iottree.system.*
		" %><%! 

%><%

	
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
  <link rel="stylesheet" href="/_js/layui/css/layui.css" media="all">
    <script src="/_js/jquery.min.js"></script>
    <link  href="/_js/icon/css/font-awesome.min.css"  rel="stylesheet" type="text/css" >
	<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
	<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
    <link   href="/_js/layui/css/layui.css" rel="stylesheet" />
    <script src="/_js/ajax.js"></script>
    <script src="/_js/layer/layer.js"></script>
    <script src="/_js/dlg_layer.js"></script>
    <script src="/_js/layui/layui.js"></script>
</head>
<style>
  .layui-form-label{
       width: 120px;
   }
   .layui-input-block {
       margin-left: 140px;
       min-height: 36px;
       width:240px;
   }
   .layui-input-inline {
       margin-left: 140px;
       min-height: 36px;
       width:140px;
   }
</style>
<body>
  <form id="up_form" class="layui-form" action="#" >
    
  <div class="layui-form-item">
    <label class="layui-form-label">数据库类型</label>
    <div class="layui-input-block">
      <select id="db_type" name="db_type" lay-verify="required">
        <option value="mysql">MySql</option>
        <option value="oracle">Oracle</option>
        <option value="sqlserver">SqlServer</option>
        <option value="db2">DB2</option>
      </select>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">地址/IP</label>
    <div class="layui-input-block">
      <input type="text" id="addr" name="addr"   lay-verify="required" placeholder="请输入数据库服务器地址" autocomplete="off" class="layui-input" value="192.168.1.10">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">端口/Port</label>
    <div class="layui-input-block">
      <input type="text" id="port" name="port" required  lay-verify="required|number" placeholder="请输入数据库端口" autocomplete="off" class="layui-input" value="3306">
    </div>
  </div>
 <div class="layui-form-item">
    <label class="layui-form-label">用户</label>
    <div class="layui-input-block">
      <input type="text" id="user" name="user" required  lay-verify="required" placeholder="请输入账号" autocomplete="off" class="layui-input" value="user1">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">密码</label>
    <div class="layui-input-block">
      <input type="password" id="psw" name="psw" required lay-verify="required" placeholder="请输入密码"  autocomplete="new-password"  class="layui-input" value="user1">
    </div>
    
  </div>
  <button id="btn_submit" class="layui-btn" lay-filter="form-submit" lay-submit  style="display:none"></button>
  </form>

<script>

var verify={
		
};

var dlg_cb=null;
layui.use('form', function(){
    var form = layui.form;
    //form.verify(verify);
    form.on('submit(form-submit)',function(data){
        dlg_cb(true,data.field);
        return false;
       
    });
});

function edit_submit(cb)
{
	dlg_cb=cb;
	$("#btn_submit").click();
	
}
</script>
</body>
</html>