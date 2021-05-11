<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="org.iottree.core.*
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
    <label class="layui-form-label">名称</label>
    <div class="layui-input-block">
      <input type="text" id="name" name="name"   lay-verify="required" placeholder="请输入连接名称" autocomplete="off" class="layui-input" value="">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">标题</label>
    <div class="layui-input-block">
      <input type="text" id="title" name="title"   placeholder="请输入连接标题" autocomplete="off" class="layui-input" value="">
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">连接类型</label>
    <div class="layui-input-block">
      <select id="conntp" name="conntp" lay-verify="required">
        <option value="tcpserver">TcpServer</option>
        <option value="tcpclient">TcpClient</option>
        <option value="com">COM</option>
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
    <label class="layui-form-label">描述</label>
    <div class="layui-input-block">
      <input type="text" id="desc" name="desc"   placeholder="" autocomplete="off" class="layui-input" value="">
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