<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				 java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.util.xmldata.*"%><%!
%><%
	String cxtid = request.getParameter("cxtid") ;
	boolean bpic = "true".equalsIgnoreCase(request.getParameter("pic")) ;
	if(Convert.isNullOrEmpty(cxtid))
	{
		return ;
	}
	
	ResCxt rc = ResCxtManager.getInstance().getResCxt(cxtid) ;
	if(rc==null)
	{
		return ;
	}
	
	List<ResItem> ris = rc.listResItems();
	out.print("[");
	boolean bfirst = true;
	for(ResItem ri:ris)
	{
		String rid = ri.getResId();
		String n = ri.getName() ;
		if(bfirst) bfirst= false;
		else
			out.print(",");
		out.print("{resid:\""+rid+"\",name:\""+n+"\"}");
	}
	out.print("]");
%>