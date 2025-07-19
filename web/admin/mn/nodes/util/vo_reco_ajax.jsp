<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.store.*,
	org.iottree.core.msgnet.*,
	org.iottree.ext.vo.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.msg_net.*
	"%><%
	if(!Convert.checkReqEmpty(request, out, "op"))
		return ;
	
	String op = request.getParameter("op") ;
	String model = request.getParameter("model") ;
	String words = request.getParameter("words") ;
	List<String> wds = Convert.splitStrWith(words, ",|，") ;
	StringBuilder failedr = new StringBuilder() ;
	switch(op)
	{
	case "chk_err_words":
		if(!Convert.checkReqEmpty(request, out, "model","words"))
			return ;
		
		List<String> errwds = VoReco_NM.checkErrorWords(model, wds,failedr);
		if(errwds==null)
		{
			out.print(failedr.toString()) ;
			return ;
		}
		JSONArray outjarr = new JSONArray(errwds) ;
		outjarr.write(out) ;
		return ;
	default:
		out.print("unknown op="+op) ;
		return ;
	}
	 
%>