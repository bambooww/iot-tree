<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				  java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.util.xmldata.*"%><%!
%><%
	String editorname = request.getParameter("editor") ;
	String editorid =  request.getParameter("editor_id") ;
	if(Convert.isNullOrEmpty(editorname))
	{
		out.print("no editor input") ;
		return ;
	}
	
	IResCxtRelated rcr = ResCxtManager.getInstance().getResCxtRelated(editorname,editorid);
	if(rcr==null)
	{
		out.print("no ResCxt Related") ;
		return ;
	}
	
	List<ResCxt> rcs = rcr.getResCxts() ;
	out.print("[");
	boolean bfirst = true;
	for(ResCxt rc:rcs)
	{
		String cxtid = rc.getCxtId() ;
		String t = rc.getTitle();
		if(bfirst) bfirst= false;
		else
			out.print(",");
		out.print("{cxtid:\""+cxtid+"\",title:\""+t+"\"}");
	}
	out.print("]");
%>