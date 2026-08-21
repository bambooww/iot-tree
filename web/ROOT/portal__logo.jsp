<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				 java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,org.iottree.portal.*,
				 org.iottree.core.res.*,
				 org.iottree.core.util.xmldata.*"%><%!
%><%
if(!Convert.checkReqEmpty(request, out,"prjid"))
	return ;
	String prjid = request.getParameter("prjid") ;
	String nf_id = request.getParameter("nf_id") ;
	String fn = request.getParameter("fn") ;
	PortalManager pmgr = PortalManager.getInstanceByPrjId(prjid) ;
	if(pmgr==null)
		return ;
	
	File rf = null;
	if(Convert.isNotNullEmpty(fn))
	{
		rf = new File(pmgr.getDir(),fn) ;
	}
	else if(Convert.isNotNullEmpty(nf_id))
	{
		NavFrame nf = pmgr.getNavFrameById(nf_id) ;
		if(nf==null)
			return ;
		rf = nf.getLogoFile() ;
	}
	
	if(rf==null || !rf.exists())
		return ;
	
	try(FileInputStream fis= new FileInputStream(rf))
	{
		WebRes.renderFile(response, rf.getName(), fis, true);
	}%>