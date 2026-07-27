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
<script src="/_js/jquery.min.js"></script>
<script src="/_js/ajax.js"></script>
<script>
var ___page_uid___ = "<%=page_uid%>" ;

function __on_blk_set__(op,blkn,pblk_tp,pblk_tpt)
{
	if(parent.on_blk_set)
		parent.on_blk_set(op,___page_uid___,blkn,pblk_tp,pblk_tpt);
}
let __portlets = [] ;
let __portlet_idx = 0 ;
function __load_portlet()
{
	if(__portlets==null||__portlets.length<=0 || __portlet_idx>__portlets.length-1)
		return ;
	let plet = __portlets[__portlet_idx] ;
	send_ajax(plet.u,{},(bsucc,ret)=>{
		if(!bsucc)
		{
			$("#"+plet.id).html(`<span style="color:red">\${ret} }</span>`);return;
		}
	//console.log(ret) ;
		$("#"+plet.id).html(ret) ;
		__portlet_idx ++ ;
		__load_portlet();
	});
}

function __on_blk_loaded()
{
	$("._portlet").each(function(){
		let me = $(this) ;
		let id = me.attr("id") ;
		let u = me.attr("_let_url") ;
		//console.log(id,u) ;
		if(!id || !u) return ;
		__portlets.push({id:id,u:u}) ;
	});
	__portlet_idx = 0 ;
	__load_portlet() ;
}
__on_blk_loaded();
</script>