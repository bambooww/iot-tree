package org.iottree.core.conn.html;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * related to html element, represent inner ui block,which contains some sub content
 * 
 * @author jason.zhu
 *
 */
public class HtmlBlockLocator
{
	public static class TracePoint
	{
		String traceTxt = null ;
		
		boolean bMustHave = false;
		
		public TracePoint(String tracetxt,boolean bmusthave)
		{
			this.traceTxt = tracetxt ;
			this.bMustHave = bmusthave ;
		}
		
		public String getTraceTxt()
		{
			return this.traceTxt ;
		}
		
		public boolean isMustHave()
		{
			return bMustHave ;
		}
		
		public JSONObject toJsonObj()
		{
			JSONObject r = new JSONObject() ;
			r.put("txt", traceTxt) ;
			r.put("mh", bMustHave) ;
			return r;
		}
	}
	
	
	public static TracePoint transJObj2TracePt(JSONObject jo)
	{
		String ktxt = jo.optString("txt") ;
		if(Convert.isNullOrEmpty(ktxt))
			return null ;
		boolean bnd = jo.optBoolean("mh", false) ;
		return new TracePoint(ktxt,bnd) ;
	}
	
	public static List<TracePoint> transJArr2TracePts(JSONArray jarr)
	{
		int n = jarr.length() ;
		ArrayList<TracePoint> rets =new ArrayList<>() ;
		for(int i = 0 ; i < n ; i ++)
		{
			TracePoint tp = transJObj2TracePt(jarr.getJSONObject(i)) ;
			if(tp==null)
				continue ;
			
			rets.add(tp) ;
		}
		return rets ;
	}
	
	public static class ExtractPoint
	{
		String name ;
		
		String title ;
		
		String path ;
		
		UAVal.ValTP valTp = UAVal.ValTP.vt_str;
		
		private transient XPath xp = null ;
		
		public ExtractPoint(String n,String t,String path,UAVal.ValTP vt)
		{
			this.name = n ;
			this.title = t ;
			this.path = path ;
			this.valTp = vt ;
		}
		
		public String getName()
		{
			return name ;
		}
		
		public String getTitle()
		{
			return title ;
		}
		
		public String getPath()
		{
			return path ;
		}
		
		public XPath getXPath()
		{
			if(xp!=null)
				return xp ;
			
			if(Convert.isNullOrEmpty(path))
				return null ;
			
			StringBuilder sb = new StringBuilder() ;
			xp = XPath.parseFromStr(path, sb) ;
			return xp ;
		}
		
		public UAVal.ValTP getValTp()
		{
			return valTp ;
		}
		
		public JSONObject toJsonObj()
		{
			JSONObject r = new JSONObject() ;
			r.put("n", name) ;
			if(title!=null)
				r.put("t", title) ;
			r.put("path", path) ;
			r.put("vt", valTp.getStr()) ;
			return r;
		}
	}
	
	private static ExtractPoint transJObj2ExtractPt(JSONObject jo)
	{
		String n = jo.optString("n") ;
		if(Convert.isNullOrEmpty(n))
			return null ;
		String t = jo.optString("t") ;
		String p = jo.optString("xp") ;
		if(Convert.isNullOrEmpty(p))
			return null ;
		String tpstr = jo.optString("vt");
		UAVal.ValTP vt = UAVal.getValTp(tpstr) ; 
		return new ExtractPoint(n,t,p,vt) ;
	}
	
	String uid = null ;
	
	String name = null ;
	
	String title = null ;
	
	/**
	 * be used to location html element that contains trace point,
	 * in which inner html content tree will stable
	 */
	private List<TracePoint> tracePts = null ;
	
	/**
	 * trace pts located single element，then Ancestor node hierarchy level based on this node
	 */
	private int traceUpLvl = 0 ;
	/**
	 * with located html element as root,some txt or data will be extract
	 * every extracted pt is represented as path.
	 */
	private LinkedHashMap<String,ExtractPoint> extractPts = null ;
	
	
	private transient HtmlParser htmlP = null ;
	
	private transient Element tracePtsRoot = null ;
	
	private transient Element blockRoot = null ;
	
	public HtmlBlockLocator()
	{}
	
	public HtmlBlockLocator(String name,String title) //String uid,
	{
		if(Convert.isNullOrEmpty(name))
			throw new IllegalArgumentException("name cannot be null or empty") ;
		
		//this.uid = uid ;
		this.name = name ;
		this.title = title ;
	}
	
	public String getId()
	{
		return uid ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		if(Convert.isNullOrEmpty(this.title))
			return this.name ;
		return title ;
	}
	
	public List<TracePoint> getTracePts()
	{
		return tracePts;
	}
	
	public int getTraceUpLvl()
	{
		return this.traceUpLvl ;
	}
	
	public LinkedHashMap<String,ExtractPoint> getExtractPts()
	{
		return extractPts;
	}
	
	public ExtractPoint getExtractPt(String name)
	{
		if(extractPts==null)
			return null ;
		return extractPts.get(name) ;
	}
	
	public JSONObject toJsonObj()
	{
		JSONObject ret = new JSONObject();
		ret.put("id", this.uid);
		ret.put("n", this.name);
		if(Convert.isNotNullEmpty(this.title))
			ret.put("t", this.title) ;
		if(tracePts!=null)
		{
			JSONArray jas = new JSONArray() ;
			for(TracePoint lk : this.tracePts)
			{
				jas.put(lk.toJsonObj()) ;
			}
			ret.put("trace_pts", jas) ;
		}
		
		ret.put("trace_up_lvl", traceUpLvl) ;
		
		if(extractPts!=null)
		{
			JSONArray jas = new JSONArray() ;
			for(ExtractPoint lk : this.extractPts.values())
			{
				jas.put(lk.toJsonObj()) ;
			}
			ret.put("extract_pts", jas) ;
		}
		return ret ;
	}
	
	
	public static HtmlBlockLocator fromJsonObj(JSONObject jo)
	{
//		String id = jo.optString("id") ;
//		if(Convert.isNullOrEmpty(id))
//			return null ;
		String n = jo.optString("n") ;
		if(Convert.isNullOrEmpty(n))
			return null ;
		String t = jo.optString("t") ;
		
		HtmlBlockLocator hbl = new HtmlBlockLocator(n,t);
		JSONArray jos = jo.optJSONArray("trace_pts") ;
		if(jos!=null)
		{
			ArrayList<TracePoint> lks = new ArrayList<>() ;
			int len = jos.length() ;
			for(int k = 0 ; k < len ; k ++)
			{
				JSONObject tmpjo = jos.getJSONObject(k) ;
				TracePoint lk = transJObj2TracePt(tmpjo) ;
				if(lk!=null)
					lks.add(lk) ;
			}
			hbl.tracePts = lks ;
		}
		
		hbl.traceUpLvl = jo.optInt("trace_up_lvl", 0);
		
		jos = jo.optJSONArray("extract_pts") ;
		if(jos!=null)
		{
			LinkedHashMap<String,ExtractPoint> eps = new LinkedHashMap<>() ;
			int len = jos.length() ;
			for(int k = 0 ; k < len ; k ++)
			{
				JSONObject tmpjo = jos.getJSONObject(k) ;
				ExtractPoint lk = transJObj2ExtractPt(tmpjo) ;
				if(lk!=null)
					eps.put(lk.name,lk) ;
			}
			hbl.extractPts = eps ;
		}
		return hbl ;
	}
	
	
	public HtmlParser getHtmlParser()
	{
		return this.htmlP;
	}
	
	public Element getBlockRoot()
	{
		return blockRoot;
	}
	
	public String calNodePathInBlock(Node n)
	{
		if(blockRoot==null)
			return null ;
		
		return HtmlParser.calXPathByNode(blockRoot,n) ;
	}
	
	public HtmlBlockLocator locateToBlock(HtmlParser hp) throws Exception
	{
		return locateToBlock(hp,this.tracePts,this.traceUpLvl) ;
	}
	
	public static HtmlBlockLocator locateToBlock(String url,boolean runjspage,long runjsto,List<TracePoint> tps,int uplvl) throws Exception
	{
		HtmlParser hp = new HtmlParser(url,runjspage,runjsto) ;
		hp.navigate();
		return  locateToBlock(hp, tps,uplvl);
	}
	
	public static HtmlBlockLocator locateToBlock(File f,List<TracePoint> tps,int uplvl) throws Exception
	{
		if(!f.exists())
			return null ;
		HtmlParser hp = new HtmlParser(f) ;
		return  locateToBlock(hp, tps,uplvl);
	}
	
	public static HtmlBlockLocator locateToBlock(HtmlParser hp,List<TracePoint> tps,int uplvl) throws Exception
	{
		Element rele = hp.findBlockRootByTracePts(tps) ;
		if(rele==null)
		{
			return null ;
		}
		
		Element brele = rele ;
		if(uplvl>0)
		{
			for(int i = 0 ; i < uplvl ; i ++)
			{
				brele = (Element)brele.parent() ;
				if(brele==null)
					return null ;
			}
		}
		
		HtmlBlockLocator hbl = new HtmlBlockLocator();
		hbl.tracePts = tps ;
		hbl.blockRoot = brele ;
		hbl.tracePtsRoot = rele ;
		hbl.traceUpLvl = uplvl;
		hbl.htmlP = hp ;
		
		hp.getNodeUID(brele) ;
		
		return hbl ;
	}
	
	public boolean setTraceUpLvl(int uplvl)
	{
		if(uplvl<0)
			return false;
		
		if(this.tracePtsRoot!=null)
		{
			Element brele = tracePtsRoot ;
			if(uplvl>0)
			{
				for(int i = 0 ; i < uplvl ; i ++)
				{
					brele = (Element)brele.parent() ;
					if(brele==null)
						return false ;
				}
			}
			
			this.blockRoot = brele ;
		}
		
		this.traceUpLvl = uplvl ;
		return true;
	}
	
	public static List<String> cutExtractableStr(String txt)
	{
		return null ;
	}
	
	public static class ExtractableItem
	{
		String txt ;
		String xpath ;
		
		public ExtractableItem(String txt,String xpath)
		{
			this.txt = txt ;
			this.xpath = xpath ;
		}
		
		public JSONObject toJSONObj()
		{
			JSONObject ret = new JSONObject() ;
			ret.put("txt", txt) ;
			ret.put("xp", this.xpath) ;
			List<String> ss = XPFuncSeg.splitSegs(txt, false) ;
			if(ss!=null&&ss.size()>1)
			{
				ret.put("segs", ss) ;
			}
			return ret ;
		}
	}
	
	public List<ExtractableItem> listExtractableItemsByNode(String nid)
	{
		if(this.blockRoot==null||htmlP==null)
			return null ;
		Node n = htmlP.getNodeByUID(nid) ;
		if(n==null)
			return null;
		return listExtractableItemsByNode(n);
	}
	
	public List<ExtractableItem> listExtractableItemsByNode(Node n)
	{
		List<TextNode> tns = htmlP.listTextNodes(n) ;
		if(tns==null)
			return null ;
		
		ArrayList<ExtractableItem> rets = new ArrayList<>() ;
		for(TextNode tn:tns)
		{
			String wtxt = tn.getWholeText();
			if(Convert.isNullOrTrimEmpty(wtxt))
				continue;
			wtxt = wtxt.trim();
			String xp = this.calNodePathInBlock(tn) ;
			ExtractableItem ei = new ExtractableItem(wtxt, xp) ;
			rets.add(ei);
		}
		return rets ;
	}
}
