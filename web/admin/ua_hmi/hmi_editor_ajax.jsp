<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,org.iottree.core.res.*,
	org.iottree.core.comp.*,
	org.iottree.core.util.web.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
	if(!Convert.checkReqEmpty(request, out, "path","op"))
		return;
	String op = request.getParameter("op");
	String path=request.getParameter("path");
	String gn = request.getParameter("gn") ;
	/*
	String hmiid = request.getParameter("hmiid");
	UARep rep = UAManager.getInstance().getRepById(repid) ;
	if(rep==null)
	{
		out.print("no rep found");
		return ;
	}
*/
	UAHmi h = (UAHmi)UAUtil.findNodeByPath(path);//.findHmiById(hmiid) ;
	if(h==null)
	{
		out.print("no hmi found") ;
		return ;
	}
	switch(op)
	{
	case "load": //.equals(op))
		UANode branchn = h.getRefBranchNode();
		if(branchn!=null&&branchn instanceof UAHmi)
			h = (UAHmi)branchn ;
		String txt = h.loadHmiUITxt() ;
		
		UANode topn = h.getTopNode() ;
		String reslibid = "" ;
		String resid = "" ;
		if(topn instanceof IResNode)
		{
			reslibid = ((IResNode)topn).getResLibId();
			//resid = 
		}
		//System.out.println("{\"hmipath\":\""+np+"\",\"refpath\":\""+refpath_cxt+"\"}\r\n") ;
		out.print("{\"path\":\""+""+"\",\"rb_path\":\""+""+"\",\"res_lib_id\":\""+reslibid+"\",\"res_id\":\""+resid+"\"}\r\n") ;
		out.print(txt);
		return ;
	case "save":
		if(!Convert.checkReqEmpty(request, out, "txt"))
			return;
		txt = request.getParameter("txt");
		h.saveHmiUITxt(txt);
		out.print("save ok");
	
		return ;
	case "main":
		if(h.setMainInPrj())
			out.print("set as main ui ok");
		else
			out.print("set as main ui failed");
		return;
	case "help_prompt_lan":
		if(Convert.isNullOrEmpty(gn))
			return ;
		String gv = LangTag.getLangValue(pageContext, gn) ;
		out.print(gv) ;
		return ;
	}
%>


