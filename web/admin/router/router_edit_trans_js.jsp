<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
	java.io.*,
	org.iottree.core.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.router.*,
	org.iottree.core.comp.*
	"%><%!

%><%
	if(!Convert.checkReqEmpty(request, out, "prjid","nodetp","fid","tid"))
		return ;
	String nodetp = request.getParameter("nodetp") ;
	String fid = request.getParameter("fid") ;
	String tid = request.getParameter("tid") ;
	
	String prjid = request.getParameter("prjid");
	UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
	if(prj==null)
	{
		out.print("no prj found") ;
		return ;
	}
	RouterManager rmgr= RouterManager.getInstance(prj) ;
	JoinConn jc = null ;
	switch(nodetp)
	{
	case "ric":
		jc = rmgr.CONN_RIC_getConn(fid, tid) ;
		break ;
	case "roa":
		jc = rmgr.CONN_ROA_getConn(fid, tid) ;
		break ;
	default:
		out.print("unknown nodetp="+nodetp) ;
		return ;
	}
	
	if(jc==null)
	{
		out.print("no Conn found ") ;
		return ;
	}
	String jstxt = jc.getTransJS() ;
	if(jstxt==null)
		jstxt= "" ;
%><%@ taglib uri="wb_tag" prefix="wbt"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
<jsp:include page="../head.jsp"></jsp:include>
    <style>
.sel_item
{
	width:80%;
	margin: 20px;
	margin-left:60px;
	align-content: center;
}
    </style>
    <script type="text/javascript">
    dlg.resize_to(450,430);
    </script>
</head>
<body>
<div style="width:100%;height:300px;border:0px solid;">
($input)=>{
<textarea id="jstxt" style="width:100%;height:280px;"><%=jstxt %></textarea>
&nbsp;&nbsp;}
</div>
<script>
function do_submit(cb)
{
	let jstxt = $("#jstxt").val() ;
	jstxt = trim(jstxt) ;
	if(!jstxt)
		jstxt="" ;
	cb(true,jstxt) ;
}
</script>
</body>
</html>
