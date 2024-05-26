package org.iottree.core.msgnet.util;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * plug conf for node
 * 
 * "tp":"que_num","tpt":"Query material quantity","class":"com.xxx.app.mn.N_QueryNum",
				"pm_ui_path":"../que_num.jsp","doc_path":"../que_num_doc.html"
				
 * @author jason.zhu
 *
 */
public class ConfItem
{
	String tp = null ;
	
	String tpt = null ;
	
	String classn = null ;
	
	String pm_ui_path = null ;
	
	String doc_path = null ;
	
	ConfItem(JSONObject jo)
	{
		this.tp = jo.optString("tp") ;
		this.tpt = jo.optString("tpt") ;
		this.classn = jo.optString("class") ;
		this.pm_ui_path = jo.optString("pm_ui_path") ;
		this.doc_path = jo.optString("doc_path") ;
	}
	
	public boolean isValid()
	{
		return Convert.isNotNullEmpty(this.tp) && Convert.isNotNullEmpty(this.classn) 
				&& Convert.isNotNullEmpty(this.pm_ui_path) ;
	}
	
	public String getTP()
	{
		return tp ;
	}
	
	public String getTPT()
	{
		return tpt ;
	}
	
	public String getClassName()
	{
		return classn ;
	}
	
	public String getPmUIPath()
	{
		return pm_ui_path ;
	}
	
	public String getDocPath()
	{
		return this.doc_path ;
	}
	
	public static List<ConfItem> parseConfItems(JSONObject msg_net_jo)
	{
		ArrayList<ConfItem> nns = new ArrayList<>() ;
		
		if(msg_net_jo==null)
			return nns ;
		
		JSONArray jarr = msg_net_jo.optJSONArray("items") ;
		if(jarr==null || jarr.length()<=0)
			return nns ;

		int n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject tmpjo = jarr.getJSONObject(i) ;
			ConfItem nn = new ConfItem(tmpjo) ;
			if(!nn.isValid())
				continue ;
			nns.add(nn) ;
		}
		
		return nns;
	}
}
