<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*"%><%!
	private static UATag addOrEditTag(UANode n,HttpServletRequest request) throws Exception
	{
		if(!(n instanceof UANodeOCTags))
			return null ;
		UANodeOCTags nt = (UANodeOCTags)n;
		String id = request.getParameter("id") ;
		String name=request.getParameter("name");
		String title = request.getParameter("title");
		String desc = request.getParameter("desc");
		String addr = request.getParameter("addr");
		boolean bmid = "true".equalsIgnoreCase(request.getParameter("mid")) ;

		int vt = Convert.parseToInt32(request.getParameter("vt"),1);
		UAVal.ValTP dt = UAVal.getValTp(vt) ;
		long srate = Convert.parseToInt64(request.getParameter("srate"),100);
		String strcanw = request.getParameter("canw") ;
		boolean canw = "true".equalsIgnoreCase(strcanw);

		float x = Convert.parseToFloat(request.getParameter("x"), 0.0f);
		float y = Convert.parseToFloat(request.getParameter("y"), 0.0f);

		UATag ret = nt.addOrUpdateTag(id,bmid,name, title, desc,addr,dt,canw,srate) ;
		return ret ;
	}
%><%
if(!Convert.checkReqEmpty(request, out, "op","path"))
	return;
String op = request.getParameter("op") ;
String path = request.getParameter("path") ;
UANode n = UAUtil.findNodeByPath(path);
if(n==null)
{
	out.print("no node with path="+path) ;
	return ;
}

UATag tag = null;
switch(op)
{
case "add_tag":
case "edit_tag":
	try
	{
		tag = addOrEditTag(n,request) ;
		out.print("succ="+tag.getId()) ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage());
		return ;
	}
	break ;

case "del_tag":
	String tagid = request.getParameter("id") ;
	if(Convert.isNullOrEmpty(tagid))
	{
		out.print("no tag id input") ;
		break ;
	}
	if(!(n instanceof UANodeOCTags))
	{
		out.print("not tags node") ;
		break ;
	}
	
	UATag t = ((UANodeOCTags)n).getTagById(tagid) ;
	if(t==null)
	{
		out.print("no tag found") ;
		break ;
	}
	boolean b =t.delFromParent();
	if(!b)
	{
		out.print("del err") ;
	}
	else
	{
		out.print("succ="+tagid) ;
	}
	
	break ;
case "copy":
case "paste":
	break;
case "rt":
	if(!(n instanceof UANodeOCTags))
	{
		out.print("not tags node") ;
		break ;
	}
	
	List<UATag> tgs = ((UANodeOCTags)n).listTags() ;
	out.print("[");
	boolean bf = true ;
	for(UATag tg:tgs)
	{
		UAVal val = tg.RT_getVal() ;
		if(val==null)
			continue ;
		
		if(bf) bf=false;
		else out.print(",") ;
		
		boolean valid = val.isValid() ;
		long dt = val.getValDT();
		Object obv = val.getObjVal() ;
		String strv = "";
		if(obv!=null)
			strv = ""+obv ;
		out.print("{\"tag_id\":\""+tg.getId()+"\",\"dt\":"+dt+",\"valid\":"+valid+",\"strv\":\""+strv+"\"}") ;
	}
	out.print("]");
	break ;
}

%>