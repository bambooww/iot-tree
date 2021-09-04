package org.iottree.core.conn;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	
	private ArrayList<String> bindList = new ArrayList<>() ;
	
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
		bindList.addAll(bindids);
	}
	
	public final List<String> getBindList()
	{
		return bindList;
	}
	
	@Override
	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData();
		
		
		// xd.setParamValue("opc_app_name", this.appName);
		if(this.bindParams!=null&&bindParams.size()>0)
		{
			XmlData tmpxd = xd.getOrCreateSubDataSingle("bind_pm") ;
			tmpxd.fromNameStrValMap(bindParams);
		}
		
		if(bindList!=null&&bindList.size()>0)
		{
			xd.setParamValues("bind_list", bindList);
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
		return r ;
	}
	
	@Override
	protected void injectByJson(JSONObject jo) throws Exception
	{
		super.injectByJson(jo);
		
		//for conn pt edit not support edit bind
	}
	/**
	 * override to implements id to path.
	 * it will be used to create group and tag for joined channel
	 * 
	 * @param bindid
	 * @return
	 */
	public List<String> transBindIdToPath(String bindid)
	{
		return null ;
	}
	
	public  void writeBindBeSelectedTreeJson(Writer w) throws Exception
	{
		
	}
}
