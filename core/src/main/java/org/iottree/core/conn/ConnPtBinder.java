package org.iottree.core.conn;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.iottree.core.UACh;
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
public abstract class ConnPtBinder extends ConnPt //implements IConnPtBinder
{
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
	private HashMap<String,String> bindMap = new HashMap<>() ;
	

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
	public final void setBindMapTag2Conn(Map<String,String> bm)
	{
		bindMap.clear(); 
		if(bm!=null)
			bindMap.putAll(bm) ;
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
			bindList.addAll(bs);
		}
		
		pmxd = xd.getSubDataSingle("bind_map") ;
		if(pmxd!=null)
		{
			this.setBindMapTag2Conn(pmxd.toNameStrValMap());
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
	
	public  void writeBindBeSelectedTreeJson(Writer w,boolean list_tags_only) throws Exception
	{
		
	}
}
