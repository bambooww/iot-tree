<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="
	org.iottree.core.util.*,
	org.iottree.core.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*,
	org.iottree.core.util.web.*,
	org.iottree.core.util.xmldata.*
"%><%@ taglib uri="wb_tag" prefix="lan"%><%
	//response.sendRedirect("/admin") ;
UAManager uamgr = UAManager.getInstance();
List<UAPrj> prjs = uamgr.listPrjs();
if(prjs==null||prjs.size()<=0)
{
	response.sendRedirect("/admin");
	return ;
}

UAPrj uprj = uamgr.getPrjDefault() ;
if(uprj!=null)
{
	UAHmi hmi = uprj.getHmiMain() ;
	if(hmi!=null)
	{
		response.sendRedirect(hmi.getNodePath());
		return ;
	}
}

%><!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  <title></title>
  <jsp:include page="head.jsp"></jsp:include>
  <style type="text/css">
  .overlay
  {
  	position: absolute;
  	background: #555555;
  	top:0px;
  	left:0px;
  	width:100%;
  	height:100%;
  }
  
  .overmsg
  {
  	position:absolute;
	background:#888888;
	opacity0:0.8;
	clear:both;	
	border:solid 3px;
	border-color:#d9e8d9;
	text-align:center;
	vertical-align:middle;
	color:#ffffff;
	font-size:30px;
  }
  
  .over_tt
  {
  border:0px solid;
  width:100%;
  position:absolute;
  text-align:middle;
  color:#f9c01c;
  }
  </style>
</head>
<body class="layui-layout-body">
 	<div class="overlay"></div>
 	<div id="overmsg" class="overmsg">
 		<div id="msg_title" class="over_tt" style="top:20px;"><lan:g>welcome_to</lan:g> IOT-Tree Server</div>
 		<div class="over_tt" style="bottom:20px;"><a href="./admin" style="color:#ff9304;font-size:20px;"><lan:g>enter,admin</lan:g></a></div>
 	</div>
</body>
<script type="text/javascript">

function show()
{

		let ovmsg = $("#overmsg");//;
		var wh = $(window).height();
		var ww = $(window).width();
		var w=500;
		var h=w*(1-0.618);
		var left=ww/2-w/2;
		var top=wh/2-h/2;

		ovmsg.css("top",top+"px");
		ovmsg.css("left",left+"px");
		ovmsg.css("width",w+"px");
		ovmsg.css("height",h+"px");
		
}

show() ;
</script>
</html>