<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.util.*,
	java.io.*,
	java.net.*,org.iottree.portal.*,
	java.util.*"%><%
	if(!Convert.checkReqEmpty(request, out, "path"))
		return ;
	String path = request.getParameter("path") ;
	Page pp = PortalManager.getInstance().getPageByPath(path) ;
	if(pp==null)
	{
		out.print("no page found") ;
		return ;
	}
	pp.RT_renderPage(out) ;
	String page_uid = pp.getPageUID() ;
%>
<script>
var ___page_uid___ = "<%=page_uid%>" ;

function __on_blk_set__(op,blkn,pblk_tp,pblk_tpt)
{
	if(parent.on_blk_set)
		parent.on_blk_set(op,___page_uid___,blkn,pblk_tp,pblk_tpt);
}
</script>