package org.iottree.core.conn;

import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.iottree.core.UACh;
import org.iottree.core.UANode;
import org.iottree.core.UATag;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONObject;

/**
 * Connector will provider tree data. then it can be bind to channel.
 * e.g opc client
 * 
 * 1)channel which join this connpt may has no device. it will use binded info
 *   to create sub group and tags
 * 2)
 * @author jason.zhu
 *
 */
public abstract class ConnPtBinder extends ConnPtMsg //implements IConnPtBinder
{
	public static class BindItem
	{
		String path ;
		
		String vt ;
		
		Object val ;
		
		public BindItem(String p,String vt)
		{
			this.path = p ;
			this.vt = vt ;
		}
		
		public String getPath()
		{
			return path ;
		}
		
		public String getValTp()
		{
			return vt; 
		}
		
		public Object getVal()
		{
			return val ;
		}
		
		public void setVal(Object ov)
		{
			this.val = ov ;
		}
	}
	private HashMap<String,String> bindParams = new HashMap<>() ;
	
	/**
	 * copy from connotor sub tree
	 * 
	 * this will cause tree construct automatic in channel
	 */
	private ArrayList<String> bindList = new ArrayList<>() ;
	
	/**
	 * bind connector tree node - map -  to channel tag
	 * 
	 * channel must has tag defined,then map to connector tree node
	 */
	private LinkedHashMap<String,String> bindMap = new LinkedHashMap<>() ;
	

	private transient Map<String,String> tag2conn = null ;
	private transient Map<String,List<String>> conn2tags = null ;
	
	public ConnPtBinder()
	{}
	
	public ConnPtBinder(ConnProvider cp,String name,String title,String desc)
	{
		super(cp,name,title,desc) ;
	}
	
	
	/**
	 * overrider may use param to setup some special param when connections
	 * this param may send to some special client like agent
	 * @return
	 */
	public HashMap<String,String> getBindParam()
	{
		return bindParams ;
	}
	
	public final void setBindParam(HashMap<String,String> pms)
	{
		bindParams.putAll(pms) ;
	}
	
	public final void setBindList(List<String> bindids)
	{
		bindList.clear();
		if(bindids!=null)
			bindList.addAll(bindids);
	}
	
	public final List<String> getBindList()
	{
		return bindList;
	}
	

	/**
	 * check a tag path has in bind list or not
	 * @param tagp
	 * @return
	 */
	public final boolean isInBindList(String tagp)
	{
		int k = tagp.indexOf(':') ;
		if(k>0)
			tagp = tagp.substring(0,k) ;
		for(String bl:bindList)
		{
			k = bl.indexOf(":") ;
			if(k>0)
				bl = bl.substring(0,k) ;
			if(tagp.equals(bl))
				return true ;
			
			if(tagp.startsWith(bl+"."))
				return true ;
		}
		return false;
	}
	/**
	 * key = node path in ch
	 * value = node path in connector 
	 * @param bm
	 */
	public final void setBindMapTag2Conn(Map<String,String> bm,boolean b_clear_old,boolean create_tag) throws Exception
	{
		UACh ch = null;
		if(create_tag)
		{
			ch = this.getJoinedCh() ;
			if(ch==null)
				throw new Exception("no joined channel") ;
		}
		
		HashMap<String,String> nbm = new HashMap<>() ;
		for(Map.Entry<String,String> n2v:bm.entrySet())
		{
			String tagp = n2v.getKey() ;
			if(ch!=null)
			{
				StringBuilder failedr = new StringBuilder() ;
				UATag t = getOrCreateTagByPath(ch, tagp,failedr) ;
				if(t==null)
					throw new Exception(failedr.toString()) ;
			}
			int k = tagp.lastIndexOf(":") ;
			if(k>0)
				tagp = tagp.substring(0,k) ;
			nbm.put(tagp, n2v.getValue()) ;
		}
		
		if(b_clear_old)
			bindMap.clear(); 
		
		if(bm!=null)
			bindMap.putAll(nbm) ;
		conn2tags = null ;
		tag2conn = null;
	}
	
	
	/**
	 * key = node path in ch
	 * value = node path in connector 
	 * @return
	 */
	public final Map<String,String> getBindMap()
	{
		return this.bindMap;
	}
	
	public final Map<String,String> getBindMapTag2Conn()
	{
		Map<String,String> r = tag2conn ;
		if(r!=null)
			return r ;
		
		r = new HashMap<>();
		for(Map.Entry<String, String> n2v:bindMap.entrySet())
		{
			String k = n2v.getKey() ;
			String v = n2v.getValue() ;
			r.put(k, v);
			int i = k.indexOf(':') ;
			if(i>0)
				r.put(k.substring(0,i), v) ;
		}
		tag2conn = r ;
		return r ;
	}
	
	public final Map<String,List<String>> getBindMapConn2Tags()
	{
		Map<String,List<String>> r = conn2tags ;
		if(r!=null)
			return r ;
		
		r = new HashMap<>() ;
		for(Map.Entry<String, String> t2c:this.bindMap.entrySet())
		{
			String t = t2c.getKey() ;
			String c = t2c.getValue() ;
			List<String> tags = r.get(c) ;
			if(tags==null)
			{
				tags = new ArrayList<>() ;
				r.put(c, tags) ;
				int i = c.indexOf(':') ;
				if(i>0)
					r.put(c.substring(0,i),tags) ;
			}
			if(!tags.contains(t))
				tags.add(t) ;
		}
		conn2tags = r ;
		return r ;
	}
	
	public String exportBindMap() throws Exception
	{
		UACh ch = this.getJoinedCh() ;
		Map<String,String> bm = getBindMap();
		StringBuilder sb = new StringBuilder() ;
		for(Map.Entry<String, String> b2t:bm.entrySet())
		{
			String t = b2t.getKey() ;
			String b = b2t.getValue() ;
			int k = t.lastIndexOf(':') ;
			String p = t;
			if(k>0)
				p = t.substring(0,k);
			if(ch!=null)
			{
				UANode tn = ch.getDescendantNodeByPath(p) ;
				if(tn==null||!(tn instanceof UATag))
				{
					continue ;
				}
				UATag tag = (UATag)tn ;
				if(k<0)
				{
					t += ":"+tag.getValTpRaw().getStr();
				}
			}
			sb.append(b+"="+t+"\r\n") ;
		}
		return sb.toString() ;
	}
	
	private static UATag getOrCreateTagByPath(UACh ch,String tagp,StringBuilder failedr)
	{
		int m = tagp.lastIndexOf(":");
		String p = tagp ;
		String vt = null ;
		if(m>0)
		{
			p = tagp.substring(0,m) ;
			vt = tagp.substring(m+1) ;
		}
		UATag tag = ch.getTagByPath(p) ;
		if(tag!=null)
			return tag ;

		try
		{
			return ch.addTagWithGroupByPath(p, vt, false);
		}
		catch(Exception e)
		{
			failedr.append("bind failed with add tag err="+e.getMessage()+"\r\n") ;
			return null ;
		}
	}
	
	public int importBindMap(String txt,boolean bcreate_tag,StringBuilder result) throws Exception
	{
		UACh ch = this.getJoinedCh() ;
		if(ch==null)
			throw new Exception("no joined channel") ;
		
		List<String> lns = Convert.transMultiLineStrToList(txt, true, true) ;
		boolean b_new_tag = false;
		HashMap<String,String> tagp2bp = new HashMap<>() ;
		int cc = 0 ;
		for(String ln:lns)
		{
			int k = ln.indexOf('=') ;
			if(k<=0)
				continue ;
			String opcp = ln.substring(0,k).trim() ;
			String tagp = ln.substring(k+1).trim() ;
			if("".equals(opcp)||"".equals(tagp))
				continue ;
			
			int m = tagp.lastIndexOf(":");
			String p = tagp ;
			String vt = null ;
			if(m>0)
			{
				p = tagp.substring(0,m) ;
				vt = tagp.substring(m+1) ;
			}
			UATag tag = ch.getTagByPath(p) ;
			if(tag==null)
			{
				if(!bcreate_tag)
				{
					result.append("bind failed with no tag ="+p+"\r\n") ;
					continue ;
				}
				try
				{
					tag = ch.addTagWithGroupByPath(p, vt, false);
					b_new_tag = true;
				}
				catch(Exception e)
				{
					result.append("bind failed with add tag err="+e.getMessage()+"\r\n") ;
					continue ;
				}
			}
			
			tagp2bp.put(tagp, opcp) ;
			cc ++ ;
		}// end of for
		
		if(b_new_tag)
			ch.save();
		
		setBindMapTag2Conn(tagp2bp,false,false);
		this.getConnProvider().save();
		return cc ;
	}
	
	public abstract void RT_writeValByBind(String tagpath,String strv) ;
	
	
	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		
		
		// xd.setParamValue("opc_app_name", this.appName);
		if(this.bindParams!=null&&bindParams.size()>0)
		{
			xd.getOrCreateSubDataSingle("bind_pm")
				.fromNameStrValMap(bindParams);
		}
		
		if(bindList!=null&&bindList.size()>0)
		{
			xd.setParamValues("bind_list", bindList);
		}
		
		if(this.bindMap!=null&&bindMap.size()>0)
		{
			xd.getOrCreateSubDataSingle("bind_map")
					.fromNameStrValMap(bindMap);
		}
		
		return xd;
	}

	@Override
	public boolean fromXmlData(XmlData xd, StringBuilder failedr)
	{
		boolean r = super.fromXmlData(xd, failedr);
		XmlData pmxd = xd.getSubDataSingle("bind_pm") ;
		if(pmxd!=null)
		{
			this.setBindParam(pmxd.toNameStrValMap());
		}
		
		List<String> bs = xd.getParamXmlValStrs("bind_list") ;
		if(bs!=null)
		{
			//bindList.addAll(bs);
		}
		
		pmxd = xd.getSubDataSingle("bind_map") ;
		if(pmxd!=null)
		{
			try
			{
				this.setBindMapTag2Conn(pmxd.toNameStrValMap(),true,false);
			}
			catch(Exception e) 
			{
				e.printStackTrace();
			}
		}
		return r ;
	}
	
	@Override
	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);
		
		//for conn pt edit not support edit bind
	}
	
	/**
	 * trans leaf path or group node path (include all leaf node)
	 *   to all leaf path list
	 * @param bindids
	 * @return
	 */
	public final List<String> transBindIdsToConnLeafPath(List<String> bindids)
	{
		ArrayList<String> rets = new ArrayList<>() ;
		if(bindids==null||bindids.size()<=0)
			return rets ;
		for(String bid:bindids)
		{
			List<String> ss = transBindIdToConnLeafPath(bid) ;
			if(ss==null||ss.size()<=0)
				continue ;
			rets.addAll(ss) ;
		}
		return rets ;
	}
	/**
	 * override to implements id to path.
	 * it will be used to create group and tag for joined channel
	 * 
	 * @param bindid
	 * @return
	 */
	protected List<String> transBindIdToConnLeafPath(String bindid)
	{
		return null ;
	}
	
	public HashMap<String,String> Tmp_readValStrsByPaths(List<String> paths) throws Exception
	{
		return null ;
	}
	
	public abstract void clearBindBeSelectedCache() ;
	
	public abstract List<BindItem> getBindBeSelectedItems() throws Exception;
	
	
	private List<BindItem> searchItems(String sk) throws Exception
	{
		List<BindItem> items = getBindBeSelectedItems() ;
		if(items==null||items.size()<=0)
			return items ;
		sk = sk.toLowerCase() ;
		ArrayList<BindItem> rets = new ArrayList<>() ;
		for(BindItem item:items)
		{
			String fn = item.getPath().toLowerCase() ;
			if(fn.indexOf(sk)>=0)
				rets.add(item) ;
		}
		return rets ;
	}
	

	public final int writeBindBeSelectedListRows(Writer w,String searchkey,int idx,int size) throws Exception
	{
		List<BindItem> dis = null;
		if(Convert.isNullOrEmpty(searchkey))
			dis = getBindBeSelectedItems() ;
		else
			dis = searchItems(searchkey) ;
		int s ;
		
		//if(s<idx)
		if(dis==null||(s = dis.size())<idx)
		{
			w.write("[]");
			return 0 ;
		}
		
		int last = idx+size ;
		int ret = 0 ;
		w.write("[");
		boolean bfirst = true ;
		for(int i = idx ; i < s && i < last ; i++)
		{
			if(bfirst) bfirst=false;
			else w.write(",");
			BindItem did = dis.get(i) ;
			w.write("{\"path\":\"" + did.getPath() + "\"");
			w.write(",\"vt\":\""+did.getValTp()+"\"}");
			ret ++ ;
		}
		w.write("]");
		return ret ;
	}


	public abstract  void writeBindBeSelectedTreeJson(Writer w,boolean list_tags_only,boolean force_refresh) throws Exception;
	
	public boolean supportBindBeSelectTree()
	{
		return false;
	}
	
	public void writeBindBeSelectTreeRoot(Writer w) throws Exception
	{}

	public void writeBindBeSelectTreeSub(Writer w,String pnode_id) throws Exception
	{}
}
