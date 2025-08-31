<%@ page contentType="text/html;charset=UTF-8"%><%@page 
	import="org.iottree.core.*,org.iottree.portal.*,org.iottree.core.util.*,
		org.json.*,org.w3c.dom.*,java.util.*,org.iottree.core.util.xmldata.*" %><%!

		
%><%
	if(!Convert.checkReqEmpty(request, out, "op"))
		return ;
	//UserProfile up = UserProfile.getUserProfile(request);

	String op=request.getParameter("op") ;
	String cat = request.getParameter("cat") ;
	String pageid = request.getParameter("pageid") ;
	PageCat page_cat = null ;
	Page page_item = null ;
	if(Convert.isNotNullEmpty(cat))
	{
		page_cat = PortalManager.getInstance().getPageCat(cat) ;
		if(page_cat==null)
		{
			out.print("no page cat found") ;
			return ;
		}
		if(Convert.isNotNullEmpty(pageid))
		{
			page_item = page_cat.getPageById(pageid);
		}
	}
	
	String n = request.getParameter("name") ;
	String t = request.getParameter("title") ;
	String templet_uid= request.getParameter("templet_uid") ;
	int pageidx = Convert.parseToInt32(request.getParameter("pageidx"), 0) ;
	int pagesize = Convert.parseToInt32(request.getParameter("pagesize"), -1) ;
	
	String search_txt = request.getParameter("search_txt") ;
	String jstr = request.getParameter("jstr") ;
	JSONObject inputjo = null;
	if(Convert.isNotNullEmpty(jstr))
		inputjo = new JSONObject(jstr) ;
	
	String jarr = request.getParameter("jarr") ;
	JSONArray inputjarr = null;
	if(Convert.isNotNullEmpty(jarr))
		inputjarr = new JSONArray(jarr) ;
	
	StringBuilder failedr = new StringBuilder() ;
	JSONObject tmpjo = null ;
	
	switch(op)
	{
	case "add_page_cat":
		if(!Convert.checkReqEmpty(request, out, "name","title"))
			return ;
		if(PortalManager.getInstance().addPageCat(n, t, failedr)!=null)
			out.print("succ") ;
		else
			out.print("add page cat failed:"+failedr.toString()) ;
		return ;
	case "list_pages":
		//if(!Convert.checkReqEmpty(request, out, "cat"))
		//	return ;
		
		
%>{"code":0,"msg":"",
	"data":[
<%
	boolean bfirst = true;
	int cc = 0 ;
	for(PageCat pc:PortalManager.getInstance().listPageCats().values())
	{
		if(Convert.isNotNullEmpty(cat))
		{
			if(!pc.getName().equals(cat))
				continue ;
		}
		LinkedHashMap<String,Page> pageid2p = pc.getId2PageMap();
		for(Map.Entry<String,Page> id2t:pageid2p.entrySet())
		{
			if(bfirst)
				bfirst=false;
			else
				out.print(",");
			
			tmpjo = id2t.getValue().toListJO() ;
			tmpjo.write(out) ;
			
			cc ++ ;
		}
	}
	int[] total_cc=new int[1] ;
	total_cc[0] = cc ;
	%>
		],"count":<%=total_cc[0]%>
	}
	<%
		return ;
	case "add_page":
		if(!Convert.checkReqEmpty(request, out, "title","templet_uid","cat"))
			return ;
		Page newp = page_cat.addPage(n,t, templet_uid, failedr);
		if(newp!=null)
			out.print("succ") ;
		else
			out.print("add page failed:"+failedr.toString()) ;
		return ;
	case "edit_page":
		if(!Convert.checkReqEmpty(request, out, "pageid","title","templet_uid","cat"))
			return ;
		if(page_cat.editPageBasic(pageid, n, t, templet_uid, failedr))
			out.print("succ") ;
		else
			out.print("edit page failed:"+failedr.toString()) ;
		return ;

	default:
		out.print("unknown op="+op) ;
		return ;
	}
%>