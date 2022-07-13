<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.res.*,
	org.iottree.core.comp.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%
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
String compid = request.getParameter("compid") ;
String catid =request.getParameter("catid") ;
String op = request.getParameter("op") ;
CompManager compmgr = CompManager.getInstance() ;
switch(op)
{
case "cat_add":
case "edit_cat":
	if(!Convert.checkReqEmpty(request, out, "libid"))
		return;
	String title = request.getParameter("title") ;
	if(Convert.isNullOrEmpty(title))
		title = "noname" ;
	CompCat cc = null;
	if(Convert.isNullOrEmpty(catid))
	{
		cc = lib.addCat(title) ;
	}
	else
	{
		cc = lib.updateCat(catid, title) ;
	}
	out.print("{id:'"+cc.getId()+"',title:'"+Convert.plainToJsStr(cc.getTitle())+"'}") ;
	return ;
case "comp_add":
case "comp_edit":
	if(!Convert.checkReqEmpty(request, out, "catid","libid"))
		return;
	compid = request.getParameter("compid") ;
	title = request.getParameter("title") ;
	if(Convert.isNullOrEmpty(title))
		title = "noname" ;
	cc = lib.getCatById(catid) ;
	if(cc==null)
	{
		out.print("no cat found!") ;
		return ;
	}
	CompItem ci = null;
	if(Convert.isNullOrEmpty(compid))
	{
		ci = cc.addComp(title) ;
	}
	else
	{
		ci = cc.updateComp(compid, title);
	}
	out.print("{id:'"+ci.getId()+"',title:'"+Convert.plainToJsStr(ci.getTitle())+"'}") ;
	return ;
case "comp_list":
	long st = System.currentTimeMillis() ;
	if(!Convert.checkReqEmpty(request, out, "catid","libid"))
		return;
	catid = request.getParameter("catid") ;
	cc = lib.getCatById(catid) ;
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
	compid = request.getParameter("compid") ;
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
	ci = ResManager.getInstance().getCompItem(ref_lib_id, reslibid, compid) ;
	
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
case "comp_del":
	if(!Convert.checkReqEmpty(request, out, "catid","libid","compid"))
		return;
	compid = request.getParameter("compid") ;
	cc = lib.getCatById(catid) ;
	if(cc==null)
	{
		out.print("no cat found!") ;
		return ;
	}
	ci = cc.delComp(compid);
	if(ci==null)
	{
		out.print("delete comp failed") ;
		return ;
	}
	out.print("succ") ;
	break ;
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
case "comp_txt_save":
	if(!Convert.checkReqEmpty(request, out, "catid","id","libid"))
		return;
	catid = request.getParameter("catid") ;
	id = request.getParameter("id") ;
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
	txt = request.getParameter("txt") ;
	ci.saveCompData(txt);
	out.print("save ok") ;
	break;
case "lib_cat_tree":
	CompManager.getInstance().renderLibAndCatsTree(out) ;
	break ;
case "lib_del":
	if(!Convert.checkReqEmpty(request, out, "libid"))
		return;
	CompManager.getInstance().delCompLib(libid) ;
	out.print("succ") ;
	break ;
case "comp_copy":
	if(!Convert.checkReqEmpty(request, out, "libid","compid"))
		return;
	CompItem tmpci = CompManager.copyComp(session, libid, compid);
	if(tmpci!=null)
		out.print("succ") ;
	else
		out.print("copy failed") ;
	break ;
case "comp_paste":
	if(!Convert.checkReqEmpty(request, out, "libid","catid"))
		return;
	
	tmpci = CompManager.pasteComp(session, libid, catid);
	if(tmpci!=null)
		out.print("succ") ;
	else
		out.print("paste failed") ;
	break ;
case "up_to_ref":
	if(!Convert.checkReqEmpty(request, out, "libid","compid"))
		return;
	String idstr = request.getParameter("ids") ;
	String idpre = request.getParameter("pre") ;
	if(Convert.isNullOrEmpty(idstr))
	{
		out.print("no ids input") ;
		return ;
	}
	List<String> ids = Convert.splitStrWith(idstr, ",") ;
	tmpci = CompManager.getInstance().getCompItemById(libid, compid);
	if(tmpci==null)
	{
		out.print("no compitem found") ;
		return ;
	}
	int succc = 0 ;
	for(String tmpid:ids)
	{
		switch(idpre)
		{
		case IResCxt.PRE_PRJ:
			CompItem newci = ResManager.getInstance().getCompItem(IResCxt.PRE_PRJ+"_"+tmpid, tmpci.getResLibId(), compid, true);
			if(newci!=null)
				succc ++ ;
		case IResCxt.PRE_DEVDEF:
			DevDef newdd = ResManager.getInstance().getDevDef(IResCxt.PRE_DEVDEF+"_"+tmpid, tmpci.getResLibId(), compid, true);
			if(newdd!=null)
				succc ++ ;
		}
	}
	out.print("succ="+succc) ;
	break ;
default:
	out.print("unkown op") ;
	break ;
}

%>