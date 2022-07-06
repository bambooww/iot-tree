<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,org.iottree.core.res.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
	if(!Convert.checkReqEmpty(request, out, "path","op"))
		return;
	String op = request.getParameter("op");
	String path=request.getParameter("path");
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
	if("load".equals(op))
	{
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
	}
	else if("save".equals(op))
	{
		if(!Convert.checkReqEmpty(request, out, "txt"))
			return;
		String txt = request.getParameter("txt");
		h.saveHmiUITxt(txt);
		out.print("save ok");
	}
	else if("main".equals(op))
	{
		if(h.setMainInPrj())
			out.print("set as main ui ok");
		else
			out.print("set as main ui failed");
	}
%>


