<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				 java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.util.xmldata.*"%><%!
%><%
	if(!Convert.checkReqEmpty(request, out, "res_lib_id","name"))
	return ;
	
	String ref_lib_id = request.getParameter("ref_lib_id") ;
	String resname = request.getParameter("name") ;
	String res_lib_id = request.getParameter("res_lib_id") ;
	String res_id = request.getParameter("res_id") ;
	
	//System.out.println("res.jsp ref="+ref_lib_id+" res_libid="+res_lib_id+" res_id="+res_id+" name="+resname) ;

	ResItem ri = ResManager.getInstance().getResItemWithRef(ref_lib_id,res_lib_id,res_id, resname)  ;
	if(ri==null)
	{
		return ;
	}
	ri.renderOut(request, response);
	/*
	File rf = ri.getResFile();
	if(!rf.exists())
		return ;
	try(FileInputStream fis= new FileInputStream(rf))
	{
		WebRes.renderFile(response, ri.getFileName(), fis, true);
	}
	*/
	%>