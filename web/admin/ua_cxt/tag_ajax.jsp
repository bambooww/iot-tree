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
		String indicator = request.getParameter("indicator") ;
		String unit = request.getParameter("unit") ;
		//boolean canw = "true".equalsIgnoreCase(strcanw);

		boolean b_val_filter = "true".equalsIgnoreCase(request.getParameter("b_val_filter")) ;
		String min_val_str = request.getParameter("min_val_str") ;
		String max_val_str = request.getParameter("max_val_str") ;
		//String alert_low = request.getParameter("alert_low") ;
		//String alert_high = request.getParameter("alert_high") ;
		String alert_jstr = request.getParameter("alerts") ;
		String mid_w_js = request.getParameter("mid_w_js") ;
		
		// float x = Convert.parseToFloat(request.getParameter("x"), 0.0f);
		//float y = Convert.parseToFloat(request.getParameter("y"), 0.0f);
		
		String trans = request.getParameter("trans") ;

		UATag ret = nt.addOrUpdateTagInMem(id,bmid,name, title, desc,addr,dt,dec_digits,strcanw,srate,trans,mid_w_js) ;
		ret.asLocal(bloc, loc_defv, bloc_autosave);
		ret.asUnit(unit).asIndicator(indicator) ;
		ret.asFilter(b_val_filter) ;
		ret.asMinMax(min_val_str, max_val_str);
		//ret.asAlertLowHigh(alert_low, alert_high) ;
		ret.setValAlerts(alert_jstr);
		nt.save();
		return ret ;
	}
	
	private static List<UATag> importTagJarr(UANodeOCTags nt ,JSONArray jarr) throws Exception
	{
		ArrayList<UATag> rets = new ArrayList<>() ;
		int n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject jo = jarr.getJSONObject(i) ;
			String name = jo.optString("n") ;
			if(Convert.isNullOrEmpty(name))
				continue ;
			String tpstr = jo.optString("tp") ;
			UAVal.ValTP dt = UAVal.getValTp(tpstr) ;
			if(dt==null)
				continue ;
			String title = jo.optString("t") ;
			String desc = jo.optString("d") ;
			String addr = jo.optString("addr","") ;
			int digist = jo.optInt("dec_digits",-1) ;
			long srate = jo.optLong("srate",100) ;
			String canw_str = jo.optString("canw") ;
			String trans = jo.optString("trans") ;
			String unit = jo.optString("unit") ;
			String ind = jo.optString("ind") ;
			//String tagid, boolean bmid, String name, String title, String desc, String addr,
			// UAVal.ValTP vt, int dec_digits, String canw_str, long srate, String trans,String mid_w_js
			UATag ret = nt.addOrUpdateTagInMem(null,false,name, title, desc,addr,dt,digist,canw_str,srate,trans,null) ;
			ret.asUnit(unit).asIndicator(ind) ;
			
			rets.add(ret) ;
		}
		return rets ;
	}
	
	private static List<UATag> impTag(UANode n,String txt,boolean has_addr,StringBuilder failedr) throws Exception
	{
		if(!(n instanceof UANodeOCTags))
			return null ;
		if(txt==null)
			return null ;
		txt=txt.trim() ;
		if(Convert.isNullOrEmpty(txt))
			return null ;
		UANodeOCTags nt = (UANodeOCTags)n;
		if(txt.startsWith("["))
		{
			JSONArray jarr = new JSONArray(txt) ;
			return importTagJarr(nt,jarr) ;
		}
		BufferedReader br = new BufferedReader(new StringReader(txt)) ;
		String ln = null ;
		ArrayList<UATag> rets = new ArrayList<>() ;
		while((ln=br.readLine())!=null)
		{
			ln=ln.trim() ;
			if(Convert.isNullOrEmpty(ln) || ln.startsWith("#"))
				continue ;
			List<String> ss = Convert.splitStrWith(ln, " \t") ;
			if(ss.size()<3)
				continue ;
			String name= ss.remove(0) ;
			String tpstr = ss.remove(0) ;
			UAVal.ValTP dt = UAVal.getValTp(tpstr) ;
			if(dt==null)
				continue ;
			String addr = null ;
			if(has_addr)
				addr = ss.remove(0) ;
			
			String title = Convert.combineStrWith(ss, " ") ;
			UATag ret = nt.addOrUpdateTagInMem(null,false,name, title, "",addr,dt,-1,null,100,null,null) ;
			rets.add(ret) ;
		}
		nt.save();
		return rets ;
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
	
	private static boolean chkAddr(UANode n,HttpServletRequest request,Writer out) throws Exception
	{
		String canw = request.getParameter("canw") ;
		String addr = request.getParameter("addr");
		
		int vt = Convert.parseToInt32(request.getParameter("vt"),-1);
		
		if(Convert.isNullOrEmpty(addr))
			return false ;
		if(!(n instanceof UANodeOCTags))
			return false ;
		UAVal.ValTP vtp = null;
		if(vt>=0)
			vtp = UAVal.getValTp(vt) ;
		
		UANodeOCTags nt = (UANodeOCTags)n;
		UADev dev = nt.getBelongToDev() ;
		IDevDriverable ddable = nt.getDevDriverable() ;
		if(ddable==null)
			return false ;
		DevDriver dd = ddable.getRelatedDrv() ;
		if(dd==null)
			return false;

		DevAddr da = dd.getSupportAddr() ;
		if(da.isSupportGuessAddr())
		{
			DevAddr gda = da.guessAddr(dev, addr, vtp) ;
			if(gda!=null)
			{
				gda.writeGuessAdjOut(out) ;
				return true ;
			}
		}
		
		DevAddr.ChkRes chkres = dd.checkAddr(dev,addr, vtp) ;
		if(chkres==null)
			return false;
		out.write(chkres.toJsonStr()) ;
		return true ;
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
case "imp_tag":
	if(!Convert.checkReqEmpty(request, out, "txt"))
		return;
	try
	{
		String txt = request.getParameter("txt") ;
		StringBuilder failedr = new StringBuilder() ;
		List<UATag> newtags = impTag(n,txt,true,failedr) ;
		out.print("succ="+newtags.size()) ;
	}
	catch(Exception e)
	{
		//e.printStackTrace();
		out.print(e.getMessage());
		return ;
	}
	return ;
case "chk_alert":
	try
	{
		String alert_jstr = request.getParameter("alert") ;
		out.print("succ="+tag.getId()) ;
	}
	catch(Exception e)
	{
		e.printStackTrace();
		out.print(e.getMessage());
		return ;
	}
	break;
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
case "move":
	if(!Convert.checkReqEmpty(request, out, "tar"))
		return ;
	if(!(n instanceof UANodeOCTags))
	{
		out.print("not tags node") ;
		break ;
	}
	UANodeOCTags sorn = (UANodeOCTags)n ;
	String tar = request.getParameter("tar") ;
	tagidsstr = (String)request.getParameter("tagids") ;
	tagids = Convert.splitStrWith(tagidsstr, ",") ;
	if(tagids==null||tagids.size()<=0)
	{
		out.print("no tag id input") ;
		break ;
	}
	int moved_n = sorn.moveTagsTo(tagids, tar) ;
	out.print("succ="+moved_n) ;
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
		boolean b_alert_triggered = tg.RT_hasAlertTriggered();
		out.print("{\"tag_id\":\""+tg.getId()+"\",\"dt\":"+dt+",\"valid\":"+valid+",\"strv\":\""+strv+"\",\"alert_trigger\":"+b_alert_triggered+"}") ;
	}
	out.print("]");
	break ;
case "chk_addr":
	//DevAddr.ChkRes chkres = chkAddr(n,request,out) ;
	boolean b = chkAddr(n,request,out) ;
	if(!b)
	{
		out.print("{}");
		return ;
	}
	return ;
case "list_for_sel":
	return ;
}

%>