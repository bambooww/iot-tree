package org.iottree.core;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public abstract class UANodeFilter
{
	public abstract boolean acceptNode(UANode n) ;
	
	public static void JSON_renderMidNodesWithTagsByExtName(Writer w,UANodeOCTags pn,String extname) throws IOException
	{
		UANodeFilter ff = new UANodeFilter() {

			@Override
			public boolean acceptNode(UANode n)
			{
				Object ob = n.getExtAttrValue(extname) ;
				return ob!=null;
			}} ;
			
			LinkedHashMap<UANodeOCTags,List<UATag>> ns2tags = filterMidNodesWithTags(pn,ff) ;
			renderNodeList2Tags(w,pn,ns2tags) ;
	}
	
	private static void renderNodeList2Tags(Writer w,UANodeOCTags pn,LinkedHashMap<UANodeOCTags,List<UATag>> nodelist2tags) throws IOException
	{
		w.write("[");
		boolean bfirst=true;
		for(Map.Entry<UANodeOCTags,List<UATag>> n2tags:nodelist2tags.entrySet())
		{
			UANodeOCTags n = n2tags.getKey() ;
			List<UATag> tags = n2tags.getValue() ;
			if (!bfirst)
				w.write(",");
			else
				bfirst = false;
			renderNodeTags(w, pn,n,tags) ;
		}
		w.write("]");
	}
	
	private static void renderNodeTags(Writer w,UANodeOCTags pn,UANodeOCTags n,List<UATag> tags) throws IOException
	{
		w.write("{\"id\":\"" + n.id + "\",\"n\":\"" + n.getNodeCxtPathIn(pn,"_")+ "\"");
		JSONObject jo = n.getExtAttrJO() ;
		if(jo!=null)
		{
			w.write(",\"ext\":" + jo.toString() );
		}
		renderJsonTags(w,n, tags) ; 
		w.write("}");
	}
	
	private static boolean renderJsonTags(Writer w, UANodeOCTags in_node,List<UATag> tags) throws IOException
	{
		boolean bchg=false;
		w.write(",\"tags\":[");
		boolean bfirst = true;
		for (UATag tg : tags)
		{
			UAVal val = tg.RT_getVal();
			// if(val==null)
			// continue ;

			boolean bvalid = false;
			String strv = "";
			long dt = -1;
			long dt_chg = -1;
			String str_err = "";

			if (val != null)
			{
				bvalid = val.isValid();
				Object v = val.getObjVal();
				strv = val.getStrVal(tg.getDecDigits());
				dt = val.getValDT();// Convert.toFullYMDHMS(new
									// Date(val.getValDT())) ;
				dt_chg = val.getValChgDT();// Convert.toFullYMDHMS(new
											// Date(val.getValChgDT())) ;
				//if(dt>maxdt)
				//	maxdt = dt ;
//				if(dt_chg>maxdt)
//					maxdt = dt_chg ;
				
				str_err = val.getErr();
				if (str_err == null)
					str_err = "";
			}
			else
			{
				dt_chg = System.currentTimeMillis();
			}

//			if(tag2lastdt!=null)
//			{
//				Long lastdt = tag2lastdt.get(tg) ;
//				if (lastdt!=null && lastdt > 0 && dt_chg <= lastdt)
//					continue;
//				
//				lastdt = dt_chg ;
//				tag2lastdt.put(tg, lastdt);
//			}
//			else if(g_lastdt>0)
//			{
//				if (dt_chg <= g_lastdt)
//					continue;
//			}
			
			bchg=true;

			if (!bfirst)
				w.write(",");
			else
				bfirst = false;
			// w.write("\""+tg.getName()+"\":");
			w.write("{\"n\":\"");
			//w.write(tg.getName());
			w.write(tg.getNodeCxtPathIn(in_node, "_"));
			ValTP vtp = tg.getValTp();
			if (bvalid)
			{
				if (vtp.isNumberVT() || vtp == ValTP.vt_bool)
					w.write("\",\"valid\":" + bvalid + ",\"v\":" + strv + ",\"strv\":\"" + strv + "\",\"dt\":" + dt
							+ ",\"chgdt\":" + dt_chg );
				else
					w.write("\",\"valid\":" + bvalid + ",\"v\":\"" + strv + "\",\"strv\":\"" + strv + "\",\"dt\":" + dt
							+ ",\"chgdt\":" + dt_chg );
			}
			else
			{
				w.write("\",\"valid\":" + bvalid + ",\"v\":null,\"dt\":" + dt + ",\"chgdt\":" + dt_chg + ",\"err\":\""
						+ Convert.plainToJsStr(str_err)+"\"" );
			}

			JSONObject jo = tg.getExtAttrJO() ;
			if(jo!=null)
			{
				w.write(",\"ext\":" + jo.toString() );
			}
			
			w.write("}");
			//bchged = true;
		}
		w.write("]");
		return bchg;
	}
	/**
	 * ignore root node
	 * check all middle node(may has leaf tag node).
	 * and linked all leaf tag nodes with accept middle nodes
	 * @return
	 */
	public static LinkedHashMap<UANodeOCTags,List<UATag>> filterMidNodesWithTags(UANodeOCTags pn,UANodeFilter f)
	{
		LinkedHashMap<UANodeOCTags,List<UATag>> rets = new LinkedHashMap<UANodeOCTags,List<UATag>>() ;
		
		for(UANode subn:pn.getSubNodes())
		{
			if(!(subn instanceof UANodeOCTags))
				continue ;
			UANodeOCTags suboct = (UANodeOCTags)subn ;
			filterMidNodesWithTags(rets,suboct,f) ;
		}
		return rets ;
	}
	
	private static void filterMidNodesWithTags(LinkedHashMap<UANodeOCTags,List<UATag>> rets,UANodeOCTags cur_oct,UANodeFilter f)
	{
		if(f.acceptNode(cur_oct))
		{
			ArrayList<UATag>ret_tags = new ArrayList<>() ;
			listDescendantsTags(ret_tags,cur_oct) ;
			rets.put(cur_oct, ret_tags) ;
			return ;
		}
		
		for(UANode subn:cur_oct.getSubNodes())
		{
			if(!(subn instanceof UANodeOCTags))
				continue ;
			UANodeOCTags suboct = (UANodeOCTags)subn ;
			filterMidNodesWithTags(rets,suboct,f) ;
		}
	}
	
	private static void listDescendantsTags(ArrayList<UATag> ret_tags,UANodeOCTags cur_oct)
	{
		
		List<UATag> tags = cur_oct.listTagsAll() ;
		if(tags!=null)
			ret_tags.addAll(tags) ;
		for(UANode subn:cur_oct.getSubNodes())
		{
			if(!(subn instanceof UANodeOCTags))
				continue ;
			UANodeOCTags suboct = (UANodeOCTags)subn ;
			listDescendantsTags(ret_tags,suboct);
		}
		
	}
}
