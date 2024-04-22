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
	
	String chk_en="" ;
	if(jc.isTransJSEnable())
		chk_en = "checked";
%><%@ taglib uri="wb_tag" prefix="w"%>
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
    dlg.resize_to(550,520);
    </script>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">
<div class="layui-form-item">
    <label class="layui-form-label"><w:g>enable</w:g>:</label>
	  <div class="layui-input-inline" style="width:50px;">
	    <input type="checkbox" id="en" name="en" <%=chk_en%> lay-skin="switch"  lay-filter="enable" class="layui-input">
	  </div>
	  
 </div>
 <div class="layui-form-item">
    <label class="layui-form-label">JS:</label>
	  
	  <div class="layui-input-inline" style="width: 400px;">
	    ($input)=>{
<textarea id="jstxt" style="width:100%;height:280px;" class="layui-input"><%=jstxt %></textarea>
&nbsp;&nbsp;}
</div>
 </div>
</form>
<script>

layui.use('form', function(){
	  form = layui.form;
	  
	  form.render();
});

function do_submit(cb)
{
	let en_js = $("#en").prop("checked") ;
	let jstxt = $("#jstxt").val() ;
	jstxt = trim(jstxt) ;
	if(!jstxt)
		jstxt="" ;
	cb(true,{js:jstxt,en_js:en_js}) ;
}
</script>
</body>
</html>
