package org.iottree.core;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public abstract class UANodeFilter
{
	public abstract boolean acceptNode(UANode n) ;
	
	public static void JSON_renderMidNodesWithTagsByExtName(Writer w,UANodeOCTags pn,String mid_node_ext,String tag_ext) throws IOException
	{
		UANodeFilter ff = new UANodeFilter() {

			@Override
			public boolean acceptNode(UANode n)
			{
				Object ob = n.getExtAttrValue(mid_node_ext) ;
				return ob!=null;
			}} ;
			
		UANodeFilter tagf = null;
		
		if(Convert.isNotNullEmpty(tag_ext))
		{
			tagf = new UANodeFilter() {
	
				@Override
				public boolean acceptNode(UANode n)
				{
					if(!(n instanceof UATag))
						return false;
					Object ob = n.getExtAttrValue(tag_ext) ;
					return ob!=null;
				}} ;
		}	
		LinkedHashMap<UANodeOCTags,List<UATag>> ns2tags = filterMidNodesWithTags(pn,ff,tagf) ;
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
		w.write(",\"t\":\"" + n.getTitle() + "\"");
		w.write(",\"p\":\"" + n.getNodeCxtPathIn(pn,".") + "\"");
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
			bchg=true;

			if (!bfirst)
				w.write(",");
			else
				bfirst = false;
			// w.write("\""+tg.getName()+"\":");
			tg.renderJson(in_node,w);
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
	public static LinkedHashMap<UANodeOCTags,List<UATag>> filterMidNodesWithTags(UANodeOCTags pn,UANodeFilter mid_node_filter,UANodeFilter tag_filter)
	{
		LinkedHashMap<UANodeOCTags,List<UATag>> rets = new LinkedHashMap<UANodeOCTags,List<UATag>>() ;
		for(UANode subn:pn.getSubNodes())
		{
			if(!(subn instanceof UANodeOCTags))
				continue ;
			UANodeOCTags suboct = (UANodeOCTags)subn ;
			filterMidNodesWithTags(rets,suboct,mid_node_filter,tag_filter) ;
		}
		return rets ;
	}
	
	private static void filterMidNodesWithTags(LinkedHashMap<UANodeOCTags,List<UATag>> rets,UANodeOCTags cur_oct,UANodeFilter mid_node_filter,UANodeFilter tag_filter)
	{
		if(mid_node_filter.acceptNode(cur_oct))
		{
			ArrayList<UATag>ret_tags = new ArrayList<>() ;
			listDescendantsTags(ret_tags,cur_oct) ;
			if(tag_filter!=null)
			{
				ArrayList<UATag>ftags = new ArrayList<>() ;
				for(UATag t:ret_tags)
				{
					if(tag_filter.acceptNode(t))
						ftags.add(t) ;
				}
				rets.put(cur_oct, ftags) ;
			}
			else
			{
				rets.put(cur_oct, ret_tags) ;
			}
			return ;
		}
		
		for(UANode subn:cur_oct.getSubNodes())
		{
			if(!(subn instanceof UANodeOCTags))
				continue ;
			UANodeOCTags suboct = (UANodeOCTags)subn ;
			filterMidNodesWithTags(rets,suboct,mid_node_filter,tag_filter) ;
		}
	}
	
	private static void listDescendantsTags(ArrayList<UATag> ret_tags,UANodeOCTags cur_oct)
	{
		List<UATag> tags = cur_oct.listTags() ;
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
