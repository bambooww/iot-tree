<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.res.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%>
<%
if(!Convert.checkReqEmpty(request, out, "op"))
	return;
String libid =request.getParameter("libid") ;
CompLib lib = null;
if(Convert.isNotNullEmpty(libid))
{
	lib = CompManager.getInstance().getCompLibById(libid) ;
	if(lib==null)
	{
		out.print("no lib found") ;
		return ;
	}
}
String op = request.getParameter("op") ;
CompManager compmgr = CompManager.getInstance() ;
switch(op)
{

case "comp_list":
	long st = System.currentTimeMillis() ;
	if(!Convert.checkReqEmpty(request, out, "catid","libid"))
		return;
	String catid = request.getParameter("catid") ;
	CompCat cc = lib.getCatById(catid) ;
	if(cc==null)
	{
		out.print("no cat found!") ;
		return ;
	}
	
	List<CompItem> items = cc.getItems() ;
	out.print("[") ;
	boolean bfirst = true;
	for(CompItem tmpci : items)
	{
		if(bfirst)bfirst = false;
		else out.print(",") ;
		out.print("{id:'"+tmpci.getId()+"',title:'"+Convert.plainToJsStr(tmpci.getTitle())+"'}") ;
	}
	out.print("]");
	//System.out.println("cose==="+ (System.currentTimeMillis()-st)) ;
	break ;
case "comp_load":
	if(!Convert.checkReqEmpty(request, out, "compid"))
		return;
	
	String ref_lib_id = request.getParameter("ref_lib_id") ;
	String compid = request.getParameter("compid") ;
	int k = compid.indexOf('-');
	String reslibid = null ;
	if(k<=0)
	{//out.print("invalid comp id") ;
		reslibid = "c_basic" ;
	}
	else
	{
		reslibid = compid.substring(0,k) ;
		compid = compid.substring(k+1) ;
	}
	
	CompItem ci = ResManager.getInstance().getCompItem(ref_lib_id, reslibid, compid) ;
	
	if(ci==null)
	{
		out.print("no comp found") ;
		return;
	}
	String txt = ci.getOrLoadCompData(); //ResManager.getInstance().getCompTxt(ref_lib_id,reslibid,compid);
	if(txt==null)
	{
		out.print("no comp found") ;
		return;
	}
	
	out.print("{res_ref_id:\""+ref_lib_id+"\",res_lib_id:\""+reslibid+"\",\"res_id\":\""+compid+"\"}\r\n");
	out.print(txt) ;
	break;
case "comp_txt":
	if(!Convert.checkReqEmpty(request, out, "catid","id","libid"))
		return;
	catid = request.getParameter("catid") ;
	String id = request.getParameter("id") ;
	cc = lib.getCatById(catid) ;
	if(cc==null)
	{
		out.print("no cat found!") ;
		return ;
	}
	ci = cc.getItemById(id);
	if(ci==null)
	{
		out.print("no comp found") ;
		return ;
	}
	txt = ci.getOrLoadCompData() ;
	String resnid = lib.getResLibId();
	String resid = id ;
	out.print("{res_lib_id:\""+resnid+"\",\"res_id\":\""+resid+"\"}\r\n");
	out.print(txt) ;
	break;

}

%>