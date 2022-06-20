<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,java.io.*,org.json.*,
  org.iottree.core.util.*,
  org.iottree.core.conn.html.*,
  org.jsoup.nodes.*,
	org.iottree.core.util.logger.*
	"%><%
	String op = request.getParameter("op") ;
	if(op==null)
		op="" ;
	String url = request.getParameter("url") ;
	String pnid = request.getParameter("pnid") ;
	String rootid = request.getParameter("rootid") ;
	HtmlBlockLocator hbl = null;
	switch(op)
	{
	case "nav":
		if(!Convert.checkReqEmpty(request, "no nav input", out, "url"))
			return ;
		HtmlParser hp = new HtmlParser(url) ;
		try
		{
			hp.navigate();
			//hp.writeTreeRoot(out) ;
			session.setAttribute("html_parser", hp) ;
			out.print("succ") ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			out.print(e.getMessage()) ;
		}
		break ;
	case "treen":
		
		//hp = (HtmlParser)session.getAttribute("html_parser") ;
		hbl = (HtmlBlockLocator)session.getAttribute("html_bk_loc") ;
		if(hbl==null)
		{
			out.print("no html block found") ;
			return ;
		}
		hp = hbl.getHtmlParser() ;
		if(hp==null)
		{
			out.print("no html parser found") ;
			return ;
		}
		try
		{
			if(Convert.isNotNullEmpty(rootid))
			{
				hp.writeTreeRoot(out,rootid);
			}
			else
			{
				if(Convert.isNullOrEmpty(pnid))
					hp.writeTreeRoot(out,null);
				else
					hp.writeTreeSub(out, pnid) ;
			}
		}
		catch(Exception e)
		{
			out.print(e.getMessage()) ;
		}
		break;
	case "treen_html":
		if(!Convert.checkReqEmpty(request, "no nav input", out, "pnid"))
			return ;
		hbl = (HtmlBlockLocator)session.getAttribute("html_bk_loc") ;
		if(hbl==null)
		{
			out.print("no html block found") ;
			return ;
		}
		hp = hbl.getHtmlParser() ;
		if(hp==null)
			return ;
		Node n = hp.getNodeByUID(pnid) ;
		if(n==null)
		{
			out.print("");
			break ;
		}
		String txt=  n.outerHtml() ;
		out.print(txt) ;
		break ;
	case "trace":
		if(!Convert.checkReqEmpty(request, "no nav input", out, "url"))
			return ;
		if(!Convert.checkReqEmpty(request, "no jstr input", out, "jstr"))
			return ;
		String jstr = request.getParameter("jstr") ;
		JSONObject tmpjo = new JSONObject(jstr) ;
		int uplvl = tmpjo.optInt("trace_up_lvl", 0) ;
		JSONArray jarr = tmpjo.getJSONArray("trace_pts") ;
		List<HtmlBlockLocator.TracePoint> tps = HtmlBlockLocator.transJArr2TracePts(jarr) ;
		
		hbl = HtmlBlockLocator.locateToBlock(url, tps,uplvl);
		if(hbl==null)
		{
			out.print("no block located") ;
			return ;
		}
		session.setAttribute("html_bk_loc", hbl) ;
		hp = hbl.getHtmlParser() ;
		/*
		hp = new HtmlParser(url) ;
		try
		{
			hp.navigate();
			//hp.writeTreeRoot(out) ;
			session.setAttribute("html_parser", hp) ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			out.print(e.getMessage()) ;
			return;
		}
		
		hp = (HtmlParser)session.getAttribute("html_parser") ;
		if(hp==null)
			return ;
		*/
		Element rele = hbl.getBlockRoot();// hp.findBlockRootByTracePts(tps) ;
		if(rele==null)
		{
			out.print("no Element located") ;
			return ;
		}
		String cset = hbl.getHtmlParser().getDoc().charset().toString();
		//hp.writeTreeNode(out, rele) ;
		out.print("{ele_id:\""+hp.getNodeUID(rele)+"\",encode:\""+cset+"\"}") ;
		break ;
	case "trace_up":
	case "trace_down":
		hbl = (HtmlBlockLocator)session.getAttribute("html_bk_loc") ;
		if(hbl==null)
		{
			out.print("no html block found") ;
			return ;
		}
		hp = hbl.getHtmlParser() ;
		int lvl = hbl.getTraceUpLvl() ;
		if(op.equals("trace_down"))
		{
			lvl -- ;
			if(lvl<0)
				lvl = 0 ;
		}
		else
			lvl ++;
		if(!hbl.setTraceUpLvl(lvl))
		{
			out.print("set trace upper level failed") ;
			return ;
		}
		rele  = hbl.getBlockRoot();;
		if(rele==null)
		{
			out.print("no Element located") ;
			return ;
		}
		out.print("{ele_id:\""+hp.getNodeUID(rele)+"\",trace_up_lvl:"+hbl.getTraceUpLvl()+"}") ;
		break;
	case "extractable_data":
		if(!Convert.checkReqEmpty(request, "no nav input", out, "pnid"))
			return ;
		hbl = (HtmlBlockLocator)session.getAttribute("html_bk_loc") ;
		if(hbl==null)
		{
			out.print("no html block found") ;
			return ;
		}
		hp = hbl.getHtmlParser() ;
		if(hp==null)
			return ;
		
		n = hp.getNodeByUID(pnid) ;
		if(n==null)
		{
			out.print("no node found");
			break ;
		}
		
		List<HtmlBlockLocator.ExtractableItem> eis = hbl.listExtractableItemsByNode(n) ;
		String npath = hbl.calNodePathInBlock(n) ;
		if(npath==null)
			npath = "" ;
		JSONObject jo = new JSONObject() ;
		jo.put("np", npath) ;
		JSONArray joarr = new JSONArray() ;
		for(HtmlBlockLocator.ExtractableItem ei:eis)
		{
			tmpjo = ei.toJSONObj() ;
			joarr.put(tmpjo) ;
		}
		jo.put("eis",joarr);
		out.print(jo.toString(2)) ;
		break ;
	default:
		out.print("unknown op") ;
		break ;
	}
//HtmlParser.readUrl() ;
%>