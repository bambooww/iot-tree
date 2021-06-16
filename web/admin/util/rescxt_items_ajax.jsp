<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				  java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.res.*,
				 org.iottree.core.util.xmldata.*"%><%!
%><%
if(!Convert.checkReqEmpty(request, out, "res_node_id"))
	return ;
	String resnodeid = request.getParameter("res_node_id") ;
	//String compid = request.getParameter("compid") ;
	ResDir dr = ResManager.getInstance().getResDir(resnodeid) ;
	
	if(dr==null)
	{
		out.print("no ResDir input") ;
		return ;
	}

	List<ResItem> ris = dr.listResItems() ;
	
	out.print("[");
	boolean bfirst = true;
	for(ResItem ri:ris)
	{
		if(bfirst) bfirst= false;
		else
			out.print(",");
		String name = ri.getName() ;
		out.print("{id:\""+ri.getResId()+"\",name:\""+name+"\"}");
	}
	out.print("]");%>