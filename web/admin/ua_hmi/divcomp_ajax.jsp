<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
if(!Convert.checkReqEmpty(request, out, "op"))
	return;
String op = request.getParameter("op") ;
DivCompManager compmgr = DivCompManager.getInstance() ;
switch(op)
{

case "comp_list":
	long st = System.currentTimeMillis() ;
	if(!Convert.checkReqEmpty(request, out, "cat"))
		return;
	String cat = request.getParameter("cat") ;
	DivCompCat cc = compmgr.getCat(cat) ;
	if(cc==null)
	{
		out.print("no cat found!") ;
		return ;
	}
	
	List<DivCompItem> items = cc.getItems();
	out.print("[") ;
	boolean bfirst = true;
	for(DivCompItem tmpci : items)
	{
		if(bfirst)bfirst = false;
		else out.print(",") ;
		out.print("{id:'"+tmpci.getUniqueId()+"',title:'"+Convert.plainToJsStr(tmpci.getTitle())+"',icon:'/_iottree/di_div_comps/"+cat+"/comp_"+tmpci.getName()+".png'}") ;
	}
	out.print("]");
	//System.out.println("cose==="+ (System.currentTimeMillis()-st)) ;
	break ;


}

%>