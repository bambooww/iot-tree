<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return ;

String prjid = request.getParameter("prjid");

%><html>
<head>
<title></title>
<jsp:include page="../head.jsp"></jsp:include>
</head>
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
-moz-user-select : none;
-webkit-user-select: none;
border:0px;
}


.layui-tab
{
	margin:0px;
}

.layui-tab-content {
    padding: 0px;
}

.layui-tab-card>.layui-tab-title .layui-this
{
	background-color: #aeecff;
}

ol,ul
{
	margin-bottom: 1px;
}

</style>
<body marginwidth="0" marginheight="0">
<div class="layui-tab layui-tab-card" lay-filter="test-hash" style="top:0px;">
  <ul class="layui-tab-title">
    <li class="layui-this" lay-id="11">Internal Recorder</li>
    <li lay-id="22">Data Source Handler and Output</li>
    
  </ul>
  <div class="layui-tab-content">
    <div class="layui-tab-item layui-show">
      <Iframe id="f1" style="width:100%;height:500px;border:0px;overflow: hidden;" ></Iframe>
	</div>
    <div class="layui-tab-item">
    	<Iframe id="f2" style="width:100%;height:500px;border:0px;overflow: hidden;" ></Iframe>
	</div>
  </div>
</div>

</body>
<script type="text/javascript">
var prjid = "<%=prjid%>"
var element ;
layui.use(function(){
	 
	element = layui.element;
});

function resize_h()
{
	var h = $(window).height()-55;
	$("#f1").css("height",h+"px") ;
	$("#f2").css("height",h+"px") ;
}

$(window).resize(function(){
	resize_h();
	if(element)
		element.render();
});
resize_h();
$("#f1").attr("src","rec_mgr.jsp?prjid="+prjid);
$("#f2").attr("src","store.jsp?prjid="+prjid);
</script>
</html>