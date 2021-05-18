<%@ page contentType="text/html;charset=UTF-8"%><%@page import="org.iottree.system.*,
		org.iottree.user.*" %>
<%@ taglib uri="wb_tag" prefix="wbt"%><%
UserProfile up = UserProfile.getUserProfile(request);
String un = up.getUserInfo().getFullName();
if(Convert.isNullOrEmpty(un))
	un=up.getUserName();
	boolean badmin = up.isAdministrator() ;
	boolean bduty = up.containsRoleName("qx_duty") ;
	boolean baudit = up.containsRoleName("qx_auditor") ;

%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title></title>

<script src="/_js/jquery.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>


<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/app_qx/data/js/jquery-3.4.1.min.js"></script>
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>

<script src="/_js/dlg_layer.js"></script>
	<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script src="/opencharts/dist/oc.js"></script>

<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
	<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
	
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
}

.content {
	width: 100%;
}



.content .right {
	float: right;
	width: 49%;
	margin: 0px
}

.dragtt {
	padding: 5px;
	width: 95%;
	margin-bottom: 2px;
	border: 2px #ccc;
	background-color: #eee;
}

.draglist {
	float: left;
	padding: 2px;
	margin-bottom: 2px;
	border: 2px solid #ccc;
	background-color: #eee;
	cursor: move;
}

.draglist:hover {
	border-color: #cad5eb;
	background-color: #f0f3f9;
}


.lr_btn
{
	margin-top: 20px;
	color:#858585;
	cursor: pointer;
}

.lr_btn_btm
{
	margin-bottom: 20px;
	position:absolute;
	left:5px;
	bottom:20px;
	color:#858585;
	cursor: pointer;
}

.left i:hover{
color: #ffffff;
}

.right i:hover{
color: #ffffff;
}

</style>

</head>
<body style="overflow: hidden;">
<div class="layui-tab" lay-allowClose="true">
  <ul class="layui-tab-title">
    <li class="layui-this">Home</li>
    <li>Rep</li>
    <li>权限分配</li>
    <li>全部历史商品管理文字长一点试试</li>
    <li>订单管理</li>
  </ul>
  <div class="layui-tab-content">
    <div class="layui-tab-item layui-show" style="100%">
      <iframe src="home.jsp" style="width:100%;height:500px;margin: 0;border:0;"></iframe>
    </div>
    <div class="layui-tab-item"><iframe src="rep.jsp" style="width:100%;height:500px;margin: 0;border:0;"></iframe></div>
    <div class="layui-tab-item">3</div>
    <div class="layui-tab-item">4</div>
    <div class="layui-tab-item">5</div>
    <div class="layui-tab-item">6</div>
  </div>
</div>
</body>
<script>

</script>
</html>