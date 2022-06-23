package org.iottree.core.conn.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UACh;
import org.iottree.core.UATag;
import org.iottree.core.conn.ConnPtMSG;
import org.iottree.core.conn.ConnPtMSG.BindHandler;
import org.iottree.core.conn.ConnPtMSG.PathItem;
import org.iottree.core.conn.html.HtmlBlockLocator.ExtractPoint;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class BindHandlerHtml extends BindHandler
{
	static ILogger log = LoggerManager.getLogger(BindHandlerHtml.class) ;
	
	ArrayList<HtmlBlockLocator> hbLocs = new ArrayList<>() ;
	
	private transient HashMap<HtmlBlockLocator.ExtractPoint,List<String>> ep2tags = null ;
			
	public BindHandlerHtml(ConnPtMSG cpm)
	{
		super(cpm) ;
	}
	
	public ExtractPoint getExtractPt(String blkname,String epname)
	{
		if(hbLocs==null)
			return null ;
		
		for(HtmlBlockLocator hbl:hbLocs)
		{
			if(hbl.getName().equals(blkname))
			{
				return hbl.getExtractPt(epname) ;
			}
		}
		return null ;
	}
	
	@Override
	protected boolean initBind()
	{
		if(Convert.isNullOrEmpty(bindProbeStr))
		{
			bindRunErr = "no bind setup" ;
			return false;
		}
		
		try
		{
			JSONArray bps = new JSONArray(bindProbeStr) ;
			int len = bps.length() ;
			
			for(int i = 0 ; i < len ; i ++)
			{
				JSONObject ob = bps.getJSONObject(i);
				HtmlBlockLocator hbl = HtmlBlockLocator.fromJsonObj(ob) ;
				if(hbl==null)
					continue ;
				hbLocs.add(hbl) ;
			}
					
		
			bps = new JSONArray(this.bindMapStr) ;
			len = bps.length() ;
			ep2tags = new HashMap<>() ;
			
			for(int i = 0 ; i < len ; i ++)
			{
				JSONObject ob = bps.getJSONObject(i);
				String bindp = ob.optString("bindp") ;
				String tagp = ob.optString("tagp") ;
				if(Convert.isNotNullEmpty(bindp)&&Convert.isNotNullEmpty(tagp))
				{
					int k = bindp.indexOf(":") ;
					if(k<=0)
						continue ;
					String pp = bindp.substring(0,k) ;
					String vt = bindp.substring(k+1) ;
					
					List<String> pss = Convert.splitStrWith(pp, "/") ;
					if(pss.size()!=2)
						continue ;
					
					String blkname = pss.get(0);
					String epname = pss.get(1) ;
					ExtractPoint ep = getExtractPt(blkname,epname);
					if(ep==null)
						continue ;
					List<String> tagps = ep2tags.get(ep) ;
					if(tagps==null)
					{
						tagps = new ArrayList<>() ;
						ep2tags.put(ep, tagps) ;
					}
					tagps.add(tagp) ;
				}
			}
			
			if(ep2tags.size()<=0)
			{
				bindRunErr = "no valid bind setup" ;
				return false;
			}
			return true;
		}
		catch(Exception e)
		{
			bindRunErr = "bind init err:"+e.getMessage() ;
			return false;
		}
		
	}
	
	
	public HtmlBlockLocator getBlockLocator(String id)
	{
		for(HtmlBlockLocator bl:hbLocs)
		{
			if(bl.getId().equals(id))
				return bl ;
		}
		return null ;
	}

	@Override
	protected boolean runBind(String topic, String txt) throws Exception
	{
		if(Convert.isNullOrEmpty(txt))
			return false;
		
		Document doc = Jsoup.parse(txt);
		if(doc==null)
			return false;
		
		if(hbLocs==null||hbLocs.size()<=0)
			return false;
		
		UACh joinedch = this.connPtMsg.getJoinedCh() ;
		HtmlParser hp = new HtmlParser() ;
		for(HtmlBlockLocator hbl:this.hbLocs)
		{
			try
			{
				hp.setDoc(doc);
				HtmlBlockLocator tmphbl= hbl.locateToBlock(hp) ;
				if(tmphbl==null)
					continue ;
				Element blkroot= tmphbl.getBlockRoot();// hp.findBlockRootByTracePts(hbl.getTracePts(),hbl.getTraceUpLvl()) ;
				if(blkroot==null)
					continue ;
				
				StringBuilder ressb = new StringBuilder() ;
				LinkedHashMap<String,ExtractPoint> epts = hbl.getExtractPts();
				if(epts!=null)
				{
					
					for(HtmlBlockLocator.ExtractPoint ei:epts.values())
					{
						List<String> tagps = ep2tags.get(ei) ;
						if(tagps==null||tagps.size()<=0)
							continue ;
						
						XPath xp = ei.getXPath() ;
						if(xp==null)
							continue ;
						
						String strv = HtmlParser.findStrValByXPath(blkroot, xp);
						if(Convert.isNullOrEmpty(strv))
							continue ;
						
						if(joinedch!=null)
						{
							for(String tagp:tagps)
							{
								UATag t = joinedch.getTagByPath(tagp) ;
								if(t==null)
								{
									continue ;
								}
								
								ressb.append(ei.getTitle()+"/"+hbl.getName()+"/"+ei.getName()+"]"+ei.getPath()+" → "+tagp+"="+strv+"\r\n") ;
								t.RT_setValRawStr(strv, true, System.currentTimeMillis());
								
							}
						}
					}
					
				}
				bindRunRes = ressb.toString() ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				if(log.isDebugEnabled())
					log.error(e);
			}
		}
		return true;
	}
	
}


