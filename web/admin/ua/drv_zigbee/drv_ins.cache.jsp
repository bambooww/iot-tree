<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.iottree.driver.zigbee.zstack.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
	String path = request.getParameter("path") ;
	StringBuilder failedr = new StringBuilder() ;
	ZStackManager zsm = ZStackManager.getInstanceByPath(path,failedr) ;
	if(zsm==null)
	{
		out.print(failedr.toString()) ;
		return ;
	}
	ZStackCoor zsd = zsm.getCoor() ;
	
%><html>
<head>
<title>zstack driver cache</title>
<jsp:include page="../../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<style type="text/css">
table {width:100%;}
td {border:1px solid #ccc;}
.left {position:absolute;left:0px;top:0px;width:50%;bottom:0px;overflow-y:auto;border:1px solid #ccc; }
.right {position:absolute;right:0px;top:0px;width:50%;bottom:0px;overflow-y:auto;border:1px solid #ccc; }
.l_item {border:1px solid blue;margin:2px;width:99%;cursor: pointer;}
</style>
<script>
dlg.resize_to(650,500);
</script>
</head>
<body>
<div class="left">
<%
for(MsgReqRsp mrr:zsm.listCachedMRR())
{
	String uid = mrr.getUID() ;
%><div class="l_item" onclick="show_mrr_detail('<%=uid%>')"><%=mrr %></div><%
}
%>
</div>
<div class="right" id="right_c">
</div>
</body>
<script type="text/javascript">

var path="<%=path%>";
var form = null;
layui.use('form', function(){
	  form = layui.form;
	  form.render();
	  
});

function show_mrr_detail(uid)
{
	send_ajax("drv_ins.ajax.jsp",{op:"cache_mrr_uid",path:path,uid:uid},(bsucc,ret)=>{
		//dlg.msg(ret) ;
		$("#right_c").html("<pre>"+ret+"</pre>") ;
	})
}
</script>
</html>