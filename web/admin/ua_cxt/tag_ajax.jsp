<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%><%@ page import="
	org.iottree.core.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
	java.io.*,
	java.util.*,
	org.json.*,
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
		
		boolean bloc = "true".equalsIgnoreCase(request.getParameter("bloc")) ;
		String loc_defv = request.getParameter("loc_defv") ;
		boolean bloc_autosave = "true".equalsIgnoreCase(request.getParameter("bloc_autosave")) ;
		
		boolean bmid = "true".equalsIgnoreCase(request.getParameter("mid")) ;

		int vt = Convert.parseToInt32(request.getParameter("vt"),1);
		int dec_digits = Convert.parseToInt32(request.getParameter("dec_digits"),-1);
		UAVal.ValTP dt = UAVal.getValTp(vt) ;
		long srate = Convert.parseToInt64(request.getParameter("srate"),100);
		String strcanw = request.getParameter("canw") ;
		boolean canw = "true".equalsIgnoreCase(strcanw);

		boolean b_val_filter = "true".equalsIgnoreCase(request.getParameter("b_val_filter")) ;
		// float x = Convert.parseToFloat(request.getParameter("x"), 0.0f);
		//float y = Convert.parseToFloat(request.getParameter("y"), 0.0f);
		
		String trans = request.getParameter("trans") ;

		UATag ret = nt.addOrUpdateTagInMem(id,bmid,name, title, desc,addr,dt,dec_digits,canw,srate,trans) ;
		ret.asLocal(bloc, loc_defv, bloc_autosave);
		ret.asFilter(b_val_filter) ;
		nt.save();
		return ret ;
	}
	
	private static UATag renameTag(UANode n,HttpServletRequest request) throws Exception
	{
		if(!(n instanceof UANodeOCTags))
			return null ;
		UANodeOCTags nt = (UANodeOCTags)n;
		if(!nt.isRefedNode())
		{
			return null ;
		}
		String id = request.getParameter("id") ;
		String name=request.getParameter("name");
		String title = request.getParameter("title");
		String desc = request.getParameter("desc");
		
		String trans = request.getParameter("trans") ;

		UATag ret = nt.renameRefedTag(id,name, title, desc) ;
		return ret ;
	}
	
	private static DevAddr.ChkRes chkAddr(UANode n,HttpServletRequest request)
	{
		String addr = request.getParameter("addr");
		if(Convert.isNullOrEmpty(addr))
			return null ;
		if(!(n instanceof UANodeOCTags))
			return null ;
		UANodeOCTags nt = (UANodeOCTags)n;
		IDevDriverable ddable = nt.getDevDriverable() ;
		if(ddable==null)
			return null ;
		DevDriver dd = ddable.getRelatedDrv() ;
		if(dd==null)
			return null;
		
		int vt = Convert.parseToInt32(request.getParameter("vt"),1);
		UAVal.ValTP vtp = UAVal.getValTp(vt) ;
		return dd.checkAddr(addr, vtp) ;
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
		e.printStackTrace();
		out.print(e.getMessage());
		return ;
	}
	break ;

case "del_tag":
	if(!Convert.checkReqEmpty(request, out, "id"))
		return;
	String tagid = request.getParameter("id") ;
	List<String> tagids = Convert.splitStrWith(tagid, ",") ;
	if(tagids==null||tagids.size()<=0)
	{
		out.print("no tag id input") ;
		break ;
	}
	if(!(n instanceof UANodeOCTags))
	{
		out.print("not tags node") ;
		break ;
	}
	for(String tid:tagids)
	{
		UATag t = ((UANodeOCTags)n).getTagById(tid) ;
		if(t==null)
		{
			continue ;
		}
		boolean b =t.delFromParent();
	}
	out.print("succ="+tagid) ;
	break ;
case "rename_tag":
	try
	{
		tag = renameTag(n,request) ;
		out.print("succ="+tag.getId()) ;
	}
	catch(Exception e)
	{
		out.print(e.getMessage());
		return ;
	}
	break ;
case "copy":
	String tagidsstr = (String)request.getParameter("tagids") ;
	tagids = Convert.splitStrWith(tagidsstr, ",") ;
	if(tagids==null||tagids.size()<=0)
	{
		out.print("no tag id input") ;
		break ;
	}
	
	session.setAttribute("tags_copied", new String[]{path,tagidsstr} ) ;
	out.print("succ") ;
	break;
case "paste":
	String tag_ids = request.getParameter("tag_ids") ;
	String cp_path =null;
	if(Convert.isNotNullEmpty(tag_ids))
	{
		tagids = Convert.splitStrWith(tag_ids, ",") ;
		cp_path = path ;
	}
	else
	{
		String[] copied = (String[])session.getAttribute("tags_copied") ;
		if(copied==null)
		{
			out.print("no tags copied") ;
			break ;
		}
		cp_path = copied[0] ;
		tagids = Convert.splitStrWith(copied[1], ",") ;
		if(tagids==null||tagids.size()<=0)
		{
			out.print("no tag id copied") ;
			break ;
		}
	}
	UANode cp_node = UAUtil.findNodeByPath(cp_path);
	if(cp_node==null||!(cp_node instanceof UANodeOCTags))
	{
		out.print("no node copied") ;
		break ;
	}
	UANodeOCTags cp_ntags = (UANodeOCTags)cp_node ;
	if(!(n instanceof UANodeOCTags))
	{
		out.print("not tags node") ;
		break ;
	}
	
	UANodeOCTags cur_ntags = (UANodeOCTags)n ;
	String newids = "" ;
	for(String cp_tagid:tagids)
	{
		UATag cptag = cp_ntags.getTagById(cp_tagid) ;
		if(cptag==null)
			continue ;
		UATag uat = cur_ntags.addTagByCopy(cptag);
		if(Convert.isNotNullEmpty(newids))
			newids += ",";
		newids += uat.getId();
	}
	out.print("succ="+newids) ;
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
		//Object obv = val.getObjVal() ;
		
		String strv = val.getStrVal(tg.getDecDigits()) ;
		
		out.print("{\"tag_id\":\""+tg.getId()+"\",\"dt\":"+dt+",\"valid\":"+valid+",\"strv\":\""+strv+"\"}") ;
	}
	out.print("]");
	break ;
case "chk_addr":
	DevAddr.ChkRes chkres = chkAddr(n,request) ;
	if(chkres==null)
	{
		out.print("{}");
		return ;
	}
	else
	{
		out.print(chkres.toJsonStr()) ;
	}
	break;
}

%>