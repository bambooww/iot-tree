package org.iottree.core.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * local node config inf,node id and title is assigned by platform
 * @author jason.zhu
 *
 */
public class PlatNode
{
//	String nodeId ;
//	
//	String nodeTitle ;
	
	boolean openForPlat = true ;
	
	ArrayList<String> openPrjNames = null;
	
	ArrayList<String> limitPlatIPs = null ;
	
	private transient JSONObject relatedJO = null ;
	
	private PlatNode()
	{
		//this.nodeId = nodeid ;
	}
	
//	public String getId()
//	{
//		return this.nodeId ;
//	}
//	
//	public String getTitle()
//	{
//		return this.nodeTitle ;
//	}
	
	public boolean isOpenForPlat()
	{
		return this.openForPlat ;
	}
	
	public List<String> getOpenPrjNames()
	{
		return this.openPrjNames ;
	}
	
	public void setOpenPrjNames(ArrayList<String> ss)
	{
		this.openPrjNames = ss ;
	}
	
	public List<String> getLimitPlatIPs()
	{
		return this.limitPlatIPs ;
	}
	
	public boolean checkIPInLimit(String in_ip)
	{
		if(this.limitPlatIPs==null||this.limitPlatIPs.size()<=0)
			return true ;
		
		for(String ip:this.limitPlatIPs)
		{
			if(in_ip.equals(ip))
				return true ;
			if(ip.endsWith("*"))
			{
				if(in_ip.startsWith(ip.substring(0,ip.length()-1)))
					return true ;
			}
		}
		if("127.0.0.1".equals(in_ip))
			return true;
		return false;
	}
	
	public JSONObject toJO()
	{
		return relatedJO;
	}
	
	public static PlatNode fromJO(JSONObject jo)
	{
		//String node_id = jo.optString("node_id") ;
		//if(Convert.isNullOrEmpty(node_id))
		//	return null ;
		PlatNode pn = new PlatNode() ;
		pn.relatedJO = jo ;
		//pn.nodeTitle = jo.optString("node_tt") ;
		pn.openForPlat = jo.optBoolean("open_for_plat", true) ;
		pn.openPrjNames = Convert.optJOStrList(jo, "open_prjs") ;
		pn.limitPlatIPs = Convert.optJOStrList(jo, "limit_plat_ip") ;
		return pn ;
	}
	
	
	// -- user right controlled by platform
	
	
		/**
		 * 
		 * @author zzj
		 *
		 */
		public static class UserRight
		{
			public String token ;
			
			public String userName ; // user login name
			
			public String userDisName ;
			
			public boolean bAdmin ; //true = admin
			
			public long upDT  = System.currentTimeMillis() ;
			
			
			public long timeoutMS = 1800000 ;
			
			public String lan = "cn";
			
			public boolean checkRight(String req_path)
			{
				return true ;//check request right by path  /prjn/ui_x
			}
			
			public boolean isTimeout()
			{
				return System.currentTimeMillis() - upDT > this.timeoutMS ;
			}
			
			public JSONObject toJO()
			{
				JSONObject jo = new JSONObject() ;
				jo.put("token",this.token) ;
				jo.put("user_name",this.userName) ;
				jo.put("user_dis_name",this.userDisName) ;
				jo.put("admin",this.bAdmin) ;
				jo.putOpt("lan", this.lan) ;
				//jo.put("token",this.token) ;
				return jo ;
			}
			
			public static UserRight fromJO(JSONObject jo)
			{
				String token =  jo.optString("token") ;
				String user_name =  jo.optString("user_name") ;
				if(Convert.isNullOrEmpty(token) || Convert.isNullOrEmpty(user_name))
					return null ;
				UserRight ret = new UserRight() ;
				ret.token = token ;
				ret.userName = user_name ;
				ret.userDisName =  jo.optString("user_dis_name") ;
				ret.bAdmin =  jo.getBoolean("admin") ;
				ret.lan = jo.optString("lan","cn") ;
				return ret;
			}
		}
		
		public static final String PN_TOKEN = "_plat_token_" ;
		
		private HashMap<String,UserRight> user2right = new HashMap<>() ;
		
		private void updateUserRight(UserRight ur)
		{
			user2right.put(ur.userName,ur) ;
		}
		
		
		public UserRight getRightByToken(String token)
		{
			for(UserRight ru:this.user2right.values())
			{
				if(ru.token.equals(token))
				{
					if(ru.isTimeout())
						return null ;
					return ru ;
				}
			}
			return null ;
		}
		
//		public boolean checkUserRight(String token)
//		{
//			UserRight getRightByToken(String token)
//		}
		
		public void updateUserRight(JSONArray jarr)
		{
			if(jarr==null)
				return ;
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				UserRight ur = UserRight.fromJO(tmpjo) ;
				if(ur==null)
					continue ;
				updateUserRight(ur) ;
			}
		}
}
