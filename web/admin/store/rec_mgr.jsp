<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*
	"%><%!

%><%
if(!Convert.checkReqEmpty(request, out, "prjid"))
	return ;

String prjid = request.getParameter("prjid");
UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
if(prj==null)
{
	out.print("no prj found") ;
	return ;
}
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
}

select option
{
font-size: 12px;
}

.oc-toolbar .toolbarbtn
{
width:40px;height:40px;margin: 5px;
font-size: 13px;
background-color: #eeeeee
}

.rmenu_item:hover {
	background-color: #373737;
}



</style>
<body marginwidth="0" marginheight="0">
 <blockquote class="layui-elem-quote ">Tag Recorder
 <div style="float: right;margin-right:10px;font: 15px solid;color:#fff5e2">
 	<button type="button" class="layui-btn layui-btn-sm layui-border-blue" onclick="add_or_edit_dc('<%=prjid %>',null)">+Add Data Class</button>

							
 </div>
</blockquote>

<div id="rt_info">
	<span id=""></span>
</div>
 	
<script>
var prjid = "<%=prjid%>" ;

function rt_get_info()
{
	send_ajax("rec_ajax.jsp",{op:"rt_data",prjid:prjid},(bsucc,ret)=>{
		if(!bsucc)
		{
			console.log(ret) ;
			return ;
		}
		let ob = null ;
		eval("ob="+ret) ;
		$("#rt_info").html("<pre>"+ret+"</pre>") ;
	}); 
}

setInterval(rt_get_info,3000) ;

</script>

</body>
</html>