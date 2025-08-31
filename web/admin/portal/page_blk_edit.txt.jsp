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
	
	PageBlkTxt pblk_txt = null ;
	PageBlk pblk = pp.getPageBlk(blkn) ;
	String txt = "" ;
	boolean bhtml = true ;
	if(pblk!=null && pblk instanceof PageBlkTxt)
	{//no set page blk
		pblk_txt = (PageBlkTxt)pblk ;
		txt = pblk_txt.getTxt() ;
		bhtml = pblk_txt.isHtml() ;
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
    <label class="layui-form-label">Txt:</label>
    <div class="layui-input-inline"  style="width:500px;">
      <textarea id="txt" lay-filter="txt" style="width:480px;height:250px;"><%=txt %></textarea>
    </div>
  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">HTML:</label>
    <div class="layui-input-inline"  style="width:300px;">
      <input type="checkbox" id="bhtml" name="bhtml" <%=bhtml?"checked":"" %>>
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
	let txt = $('#txt').val();
	let bhtml = $('#bhtml').prop("checked") ;
	cb(true,{txt:txt,bhtml:bhtml});
}

</script>
</html>                                                                                                                                                                                                                            