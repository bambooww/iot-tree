<%@ page contentType="text/json;charset=UTF-8" isELIgnored="false"%><%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
				java.net.*"%><%
	if(!Convert.checkReqEmpty(request, out, "repid","id"))
		return ;
	//String op = request.getParameter("op");
	String repid = request.getParameter("repid");
	String id = request.getParameter("id");
	UARep rep = UAManager.getInstance().getRepById(repid);
	if(rep==null)
	{
		out.print("{error:true,info:\"no rep found!\"");
		return;
	}
	UANode n = rep.findNodeById(id) ;
	if(n==null)
	{
		out.print("{error:true,info:\"no node found\"");
		return ;
	}

	String node_path = n.getNodePath() ;
	if(!(n instanceof UANodeOCTags))
	{
		out.print("{error:true,info:\"not node oc tags\"");
		return ;
	}
	UANodeOCTags ntags = (UANodeOCTags)n ;
	List<UATag> tags = ntags.listTagsAll() ;
%>{}