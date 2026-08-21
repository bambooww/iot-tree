<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %><%
	if(!Convert.checkReqEmpty(request, out, "prjid","page_uid"))
		return ;
	String prjid = request.getParameter("prjid") ;
	PortalManager pm = PortalManager.getInstanceByPrjId(prjid) ;
	if(pm==null)
	{
		out.print("no portal manager found") ;
		return ;
	}
	String page_uid = request.getParameter("page_uid") ;
	Page pp = pm.getPageByUID(page_uid) ;
	if(pp==null)
	{
		out.print("no page found") ;
		return ;
	}
	pp.renderPageSetup(out) ;
%>
<script>
var ___page_uid___ = "<%=page_uid%>" ;

function __on_blk_set__(op,blkn,pblk_tp,pblk_tpt)
{
	if(parent.on_blk_set)
		parent.on_blk_set(op,___page_uid___,blkn,pblk_tp,pblk_tpt);
}
</script>