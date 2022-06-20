package org.iottree.core.conn.html;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.iottree.core.conn.html.HtmlBlockLocator.TracePoint;
import org.iottree.core.util.Convert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class HtmlParser
{
	String url = null ;
	
	Document doc = null ;
	
	private HashMap<String,Node> uid2node = null;
	
	public HtmlParser(String url)
	{
		this.url = url ;
	}
	
	public HtmlParser(Document doc)
	{
		setDoc(doc);
	}
	
	public HtmlParser()
	{}
	
	public void setDoc(Document doc)
	{
		this.doc=  doc ;
		uid2node = new HashMap<>() ;
		clearHtml();
	}
	
	public String getUrl()
	{
		return url ;
	}
	
	public Document navigate() throws IOException
	{
		doc = Jsoup.connect(url)
				  .userAgent("Mozilla")
				  //.data("query", "Java")
				 // .cookie("auth", "token")
				  .timeout(10000)
				  .get(); //post();
		
		uid2node = new HashMap<>() ;
		clearHtml();
		return doc ;
	}
	
	private void clearHtml()
	{
		if(doc==null)
			return ;
		
		clearNode(doc) ;
	}
	
	private void clearNode(Node curn)
	{
		List<Node> nodes = curn.childNodes();
		if(nodes.size()<=0)
			return ;
		ArrayList<Node> delnodes = new ArrayList<>() ;
		for (Node node : nodes)
		{
			if(node instanceof TextNode)
			{
				TextNode tn = (TextNode)node ;
				if(Convert.isNullOrTrimEmpty(tn.getWholeText()))
				{
					delnodes.add(node) ;
					continue ;
				}
			}
			
			if(node instanceof Element)
			{
				Element ele = (Element)node ;
				String nn = ele.nodeName().trim().toLowerCase() ;
				if("script".equals(nn))
				{//clear all script
					delnodes.add(node) ;
					continue ;
				}
			}
			
			clearNode(node) ;
		}


		for(Node dn:delnodes)
		{
			dn.remove();
		}
	}
	
	public Document getDoc()
	{
		return doc ;
	}
	
	public Node getNodeByUID(String uid)
	{
		if(uid2node==null)
			return null ;
		return uid2node.get(uid) ;
	}

	private boolean chkWriteNode(Node n)
	{
		if(n instanceof Element)
			return true ;
		
		if(n instanceof TextNode)
		{
			TextNode tn = (TextNode)n ;
			if(Convert.isNullOrTrimEmpty(tn.getWholeText()))
					return false;
			return true ;
		}
		return false;
	}

	public void writeTreeRoot(Writer w,String nid) throws Exception
	{
		if (doc == null)
		{
			throw new Exception("no doc found");
		}
		List<Node> nodes = null;
		String tt = null ;
		if(Convert.isNullOrEmpty(nid))
		{
			nid = UUID.randomUUID().toString();
			nodes = doc.childNodes();
			tt = this.getUrl();
		}
		else
		{
			Node nn =  uid2node.get(nid) ;
			if(nn==null)
				return ;
			nodes = nn.childNodes() ;
			tt = nn.nodeName() ;
		}
		w.write("{\"id\":\"" + nid + "\"");
		w.write(",\"nc\":0");
		w.write(",\"icon\": \"fa fa-sitemap fa-lg\"");

		w.write(",\"text\":\"" + tt + "\"");
		w.write(",\"state\": {\"opened\": true}");

		
		if (nodes != null && nodes.size() > 0)
		{
			w.write(",\"children\":[");
			//
			boolean bfirst = true;
			for (Node node : nodes)
			{
				if(!chkWriteNode(node))
					continue ;
				
				if (bfirst)
					bfirst = false;
				else
					w.write(',');

				writeTreeNode(w, node);
			}
			w.write("]");
		}
		w.write("}");
	}

	public void writeTreeSub(Writer w, String puid) throws Exception
	{
		if (doc == null)
		{
			throw new Exception("no doc found");
		}
		Node pn = uid2node.get(puid) ;
		if (pn == null)
			return;
		List<Node> nodes = pn.childNodes() ;
		w.write("[");
		if (nodes != null && nodes.size() > 0)
		{

			//
			boolean bfirst = true;
			for (Node node : nodes)
			{
				if(!chkWriteNode(node))
					continue ;
				
				if (bfirst)
					bfirst = false;
				else
					w.write(',');

				writeTreeNode(w, node);
			}

		}

		w.write("]");
	}

	public void writeTreeNode(Writer w, Node n) throws Exception
	{
		String uid = getObjUID(n) ;
		uid2node.put(uid, n) ;
		// boolean bvar = n instanceof UaVariableNode;
		w.write("{\"id\":\"" + uid + "\"");
		
		if (n instanceof TextNode)
		{
			w.write(",\"tp\": \"tag\"");
			w.write(",\"icon\": \"fa fa-tag fa-lg\"");

			w.write(",\"text\":\"#text\"");
		}
		else if(n instanceof Element)
		{
			w.write(",\"tp\": \"tagg\"");
			w.write(",\"icon\": \"fa fa-folder fa-lg\"");
			w.write(",\"children\": true ");
			
			w.write(",\"text\":\"" + n.nodeName() + "\"");
		}
		w.write(",\"nc\":\"" + n.nodeName()+"\"}");
	}
	
	public static String getObjUID(Object ob)
	{
		return ob.getClass().getName()+"_"+System.identityHashCode(ob) ;
	}
	
	public String getNodeUID(Node ob)
	{
		String uid = getObjUID(ob) ;
		uid2node.put(uid, ob) ;
		return uid ;
	}
	
	public List<Node> findNodesByTxt(String txt)
	{
		if(doc==null)
			return null ;
		
		ArrayList<Node> rets = new ArrayList<>() ;
		findNodesByTxt(doc,rets,txt);
		if(rets.size()<=0)
			return null ;
		
		return rets ;
	}
	
	private void findNodesByTxt(Node curn,List<Node> ns,String txt)
	{
		if(curn instanceof Element)
		{
			List<Node> nodes = curn.childNodes() ;
			if (nodes == null || nodes.size() <= 0)
				return;
			for (Node node : nodes)
			{
				findNodesByTxt(node,ns,txt) ;
			}
		}
		
		if(curn instanceof TextNode)
		{
			TextNode tn = (TextNode)curn ;
			String wtxt = tn.getWholeText();
			if(Convert.isNullOrTrimEmpty(wtxt))
					return ;
			if(wtxt.contains(txt))
				ns.add(curn) ;
		}
	}
	
	private void findNodesByTracePts(Node curn,HashMap<TracePoint,List<Node>> tp2ns,List<TracePoint> tps)
	{
		if(curn instanceof Element)
		{
			List<Node> nodes = curn.childNodes() ;
			if (nodes == null || nodes.size() <= 0)
				return;
			for (Node node : nodes)
			{
				findNodesByTracePts(node,tp2ns,tps) ;
			}
		}
		
		if(curn instanceof TextNode)
		{
			TextNode tn = (TextNode)curn ;
			String wtxt = tn.getWholeText();
			if(Convert.isNullOrTrimEmpty(wtxt))
					return ;
			
			
			for(TracePoint tp:tps)
			{
				if(wtxt.contains(tp.getTraceTxt()))
				{//System.out.println(" find txt="+wtxt+" contains="+tp.getTraceTxt()) ;
					List<Node> ns = tp2ns.get(tp) ;
					if(ns==null)
					{
						ns = new ArrayList<>() ;
						tp2ns.put(tp, ns) ;
					}
					ns.add(curn) ;
				}
			}
			
		}
	}
	
	public List<TextNode> listTextNodes(Node rootele)
	{
		ArrayList<TextNode> rets = new ArrayList<>() ;
		listTextNodes(rootele,rets);
		return rets ;
	}
	
	private void listTextNodes(Node curn,List<TextNode> ns)
	{
		if(curn instanceof Element)
		{
			List<Node> nodes = curn.childNodes() ;
			if (nodes == null || nodes.size() <= 0)
				return;
			for (Node node : nodes)
			{
				listTextNodes(node,ns) ;
			}
		}
		
		if(curn instanceof TextNode)
		{
			TextNode tn = (TextNode)curn ;
			ns.add(tn) ;
		}
	}
	
	private static class EleDis implements Comparable<EleDis>
	{
		
		Node ele = null;
		
		//sum of every subnode distance  
		int distance  = -1 ;
		
		public EleDis()
		{}
		
		public EleDis(Node n,int dis)
		{
			this.ele = n ;
			this.distance = dis ;
		}

		@Override
		public int compareTo(EleDis o)
		{
			return this.distance-o.distance;
		}
	}
	
	private EleDis findParentElementBy2Node(Node n1,Node n2)
	{
		int dis1 = 0,dis2 = 0 ;
		Node pn2 = null;
		while(n1!=null)
		{
			n1 = n1.parent();
			if(n1==null)
				return null ;
			dis1 ++ ;
			pn2 = n2;//.parent() ;
			dis2 = 0;
			while(pn2!=null)
			{
				pn2 = pn2.parent() ;
				if(pn2==null)
					break;
				dis2 ++ ;
				if(n1 == pn2)
				{
					return new EleDis(n1,dis1+dis2) ;
				}
			}
		}
		
		return null ;
	}
	
	private EleDis findParentElementByNodes(List<Node> ns)
	{
		int s = ns.size() ;
		if(s<=1)
			return null ;
		
		Node n1 = ns.get(0) ;
		EleDis ret = new EleDis() ;
		ret.ele = n1 ;
		ret.distance = 0 ;
		for(int i = 1 ; i < s ; i ++)
		{
			Node n2 = ns.get(i) ;
			EleDis eledis = findParentElementBy2Node(n1,n2);
			if(eledis==null)
				return null ;
			n1 = ret.ele = eledis.ele;
			ret.distance += eledis.distance ;
		}
		
		return ret ;
	}
	/**
	 * 
	 * @param tps
	 * @return
	 */
	public Element findBlockRootByTracePts(List<TracePoint> tps)
	{
		if(tps==null||tps.size()<1)
			throw new IllegalArgumentException("trace point must bigger than 1");
		if(this.doc==null)
			throw new RuntimeException("no html doc found") ;
		
		HashMap<TracePoint,List<Node>> tp2ns = new HashMap<>() ;
		findNodesByTracePts(doc,tp2ns,tps) ;
		
		for(TracePoint tp:tps)
		{
			List<Node> fns = tp2ns.get(tp) ;
			if(fns==null)
			{
				if(tp.isMustHave())
					return null ;
			}
		}
		
		if(tp2ns.size()<=1)
		{
			return null ;//must 2 tracepoint which has node found 
		}
		//find block root
		
		int n = 1;//total enum number
		for(List<Node> ns:tp2ns.values())
		{
			n *= ns.size() ;
		}
		
		ArrayList<EleDis> elediss = new ArrayList<>() ;
		for(int i = 0 ; i < n ; i ++)
		{
			int left_n = n ;
			int left_v = i ;
			ArrayList<Node> cc_nodes = new ArrayList<>(tp2ns.size()) ;
			for(Map.Entry<TracePoint,List<Node>> t2i:tp2ns.entrySet())
			{
				//TracePoint tp = t2i.getKey() ;
				List<Node> ns = t2i.getValue() ;
				int s = ns.size() ;
				
				left_n = left_n/s ;
				int idx = left_v / left_n ;//every tracepoint node idx with i
				left_v = left_v % left_n ;
				
				cc_nodes.add(ns.get(idx)) ;
			}
			
			//cc_nodes is a set which has node in every tracepoint
			// no find one parent element node contain all cc_nodes
			EleDis eledis = findParentElementByNodes(cc_nodes);
			if(eledis!=null)
				elediss.add(eledis) ;
		}
		
		if(elediss.size()<=0)
			return null ;
		
		Collections.sort(elediss);
		
		Element r = (Element) (elediss.get(0).ele) ;
		
		String uid = getObjUID(r) ;
		uid2node.put(uid, r) ;
		
		return  r;
	}
	
	public static void readUrl() throws IOException
	{
		Document doc = Jsoup.connect("http://www.weather.com.cn/")
				  .data("query", "Java")
				  .userAgent("Mozilla")
				  .cookie("auth", "token")
				  .timeout(3000)
				  .get();
		
		Elements eles = doc.getElementsContainingText("我的天气") ;
		List<String> txts = eles.eachText() ;
		
		
	}
	
	/**
	 * 
	 * @param rootele
	 * @param n
	 * @return null if node is not rootele
	 */
	public static String calXPathByNode(Element rootele,Node n)
	{
		if(rootele==n)
			return "/"+n.nodeName();
		
		Node curn = n ;
		Node pn = n ;
		String ret = null ;
		while(pn!=null)
		{
			String curnn = curn.nodeName() ;
			pn = curn.parent() ;
			if(pn==null)
				return null ;
			
			List<Node> cns = pn.childNodes() ;
			int s = cns.size() ;
			int c = 0 ;
			for(int i = 0 ; i < s ; i ++)
			{
				Node tmpcn = cns.get(i) ;
				if(tmpcn==curn)
				{//idx found
					c ++ ;
					break ;
				}
				
				if(tmpcn.nodeName().equals(curnn))
					c ++ ;
			}
			
			ret = "/"+curnn+"["+c+"]"+(ret==null?"":ret);
			
			if(pn==rootele)
			{
				return "/"+pn.nodeName()+ret;
			}
			
			curn = pn ;
		}
		return null ;
	}
	
	public static Node findSubNodeByXPath(Element rootele,String xpath)
	{
		if(rootele==null)
			return null ;
		StringBuilder sb = new StringBuilder() ;
		XPath xp = XPath.parseFromStr(xpath, sb);
		if(xp==null)
			return null ;
		return findSubNodeByXPath(rootele,xp);
	}
	
	
	public static Node findSubNodeByXPath(Element rootele,XPath xp)
	{
		List<XPathItem> pis = xp.getPathItems();
		int s = pis.size() ;
		//List<String> ss = Convert.splitStrWith(xpath, "/") ;
		if(!pis.get(0).getName().equals(rootele.nodeName()))
			return null ;
		Node tmpn = rootele ;
		for(int i = 1 ; i < s ; i ++)
		{
			XPathItem pi = pis.get(i) ;
			String n = pi.getName() ;
			int idx = pi.getIdx() ;
//			if(n.endsWith("]"))
//			{
//				int k = n.indexOf('[') ;
//				if(k<=0)
//					return null ;
//				n = n.substring(0,k) ;
//				String idxstr = n.substring(k+1) ;
//				idxstr = idxstr.substring(0,idxstr.length()-1) ;
//				idx = Integer.parseInt(idxstr) ;
//				if(idx<=0)
//					return null ;
//			}
			
			List<Node> cns = tmpn.childNodes() ;
			if(cns==null||cns.size()<=0)
				return null ;
			int c = 0 ;
			boolean bgit = false;
			for(Node cn:cns)
			{
				if(n.equals(cn.nodeName()))
				{
					c ++;
					if(c==idx)
					{
						tmpn = cn ;
						bgit = true ;
						break ;
					}
				}
			}
			
			if(!bgit)
				return null ;
		}
		return tmpn ;
	}
	
	/**
	 * xpath may
	 * 
	 * 1)end with @xxx ,which is attr
	 * 2)end with [substr()]. which is inner str value
	 * 
	 * @param rootele
	 * @param xpath
	 * @return
	 */
	public static String findStrValByXPath(Element rootele,XPath xpath)
	{
		Node n = findSubNodeByXPath(rootele,xpath) ;
		if(n==null)
		{
			return null ;
		}
		
		String ret = null ;
		if(n instanceof TextNode)
		{
			TextNode tn = (TextNode)n;
			ret = tn.getWholeText() ;
		}
		
		List<XPathExt> pes = xpath.getPathExts();
		int s = 0 ;
		if(pes==null||(s=pes.size())<=0)
		{
			return ret ;
		}
		
		XPathExt xpe = pes.get(0) ;
		int i = 0 ;
		if(xpe instanceof XPathAttr)
		{
			if(!(n instanceof Element))
			{
				return null ;
			}
			
			Element ele = (Element)n;
			ret = ele.attr(((XPathAttr)xpe).getAttrName()) ;
			i = 1 ;
		}
		
		if(ret==null)
			return null ;
		
		for ( ; i< s ; i ++)
		{
			xpe = pes.get(i) ;
			if(!(xpe instanceof XPathFunc))
				return null ;
			
			XPathFunc pf = (XPathFunc)xpe ;
			ret = pf.runFunc(ret) ;
			if(ret==null)
				return null ;
		}
		return ret ;
	}
}
