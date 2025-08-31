<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %>
<%
	if(!Convert.checkReqEmpty(request, out, "page_uid","blkn"))
		return ;
	String page_uid = request.getParameter("page_uid") ;
	String blkn = request.getParameter("blkn") ;
	
	Page pp = PortalManager.getInstance().getPageByUID(page_uid) ;
	if(pp==null)
	{
		out.print("no page found") ;
		return ;
	}
	TPageBlk tblk = pp.getTempletBlk(blkn) ;
	if(tblk==null)
	{
		out.print("no templet page block found with name="+blkn) ;
		return ;
	}
	
	PageBlkVTxtDUrl pblk_vu = null ;
	PageBlk pblk = pp.getPageBlk(blkn) ;
	String vtxt = "" ;
	String ddyn_url = "" ;
	if(pblk!=null && pblk instanceof PageBlkVTxtDUrl)
	{//no set page blk
		pblk_vu = (PageBlkVTxtDUrl)pblk ;
		vtxt = pblk_vu.getVTxt() ;
		ddyn_url = pblk_vu.getDDynURL() ;
	}
%>
<html>
<head>
<title></title>
<jsp:include page="../head.jsp">
	<jsp:param value="true" name="simple"/>
</jsp:include>
<script>
dlg.resize_to(650,450);
</script>
</head>
<body>
<form class="layui-form" action="" onsubmit="return false;">

  <div class="layui-form-item" id="cont_desc">
    <label class="layui-form-label">View Txt:</label>
    <div class="layui-input-inline"  style="width:500px;">
      <textarea id="vtxt" lay-filter="vtxt" style="width:480px;height:250px;"><%=vtxt %></textarea>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Ajax URL:</label>
    <div class="layui-input-inline"  style="width:300px;">
      
      <input type="text" id="ddyn_url" name="ddyn_url" value="<%=ddyn_url %>"  autocomplete="off" class="layui-input">
    </div>
  </div>
 </form>
</body>
<script type="text/javascript">
var input = dlg.get_opener_opt("input") ;
var page_uid = "<%=page_uid%>" ;
var blkn = "<%=blkn%>" ;
if(input)
{
	
}
var form ;
layui.use('form', function(){
	  form = layui.form;
	  form.render() ;
});
	
function win_close()
{
	dlg.close(0);
}

function do_submit(cb)
{
	let vtxt = $('#vtxt').val();
	let ddyn_url = $("#ddyn_url").val();
	cb(true,{vtxt:vtxt,ddyn_url:ddyn_url});
}

</script>
</html>                                                                                                                                                                                                                            