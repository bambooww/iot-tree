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
%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp"></jsp:include>
    <style>
.layui-form-label{
    width: 120px;
}
.layui-input-block {
    margin-left: 140px;
    min-height: 36px;
    width:240px;
}
.layui-table-view
{
	margin-top: 1px;
}
  .layui-table-cell {
    height: auto;
    line-height: 18px;
}

    </style>
</head>
<body  style="overflow: hidden;">

<div class="layui-form-item">
  <label class="layui-form-label">新密码</label>
  <div class="layui-input-inline" style='width:50%'>
    <input type="text" id="new_psw" name="new_psw" lay-verify="" autocomplete="off" " value=""  class="layui-input">
  </div>
</div>

</body>

<script>
dlg.resize_to(500,200) ;
function get_new_psw()
{
	var new_psw = $("#new_psw").val();
	
	if(!new_psw)
	{
		return "新密码不能为空";
	}
	
	return {new_psw:new_psw}
}
</script>
</html>
