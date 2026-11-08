package org.iottree.core.ai;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UADev;
import org.iottree.core.UANode;
import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.cxt.MNContext;
import org.iottree.core.util.ILang;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class AIRole implements ILang
{
	protected UAPrj ua_prj ;
	
	protected List<UADev> ua_devs = null ;
	
	protected ArrayList<Rule> rules = new ArrayList<>() ;
	
	private String uid = null ;
	
	private AICxt context ;
	
	public AIRole(UAPrj prj,String uid)
	{
		this.ua_prj = prj ;
		this.uid = uid ;
		
		this.context = new AICxt(this) ;
	}
	
	public UAPrj getPrj()
	{
		return this.ua_prj ;
	}
	
	public String getUid()
	{
		return this.uid ;
	}
	
	public abstract String getRoleTP() ;
	
	public String getRoleTPT()
	{
		return g(getRoleTP()) ;
	}
	
	public List<UADev> listUADevs()
	{
		return this.ua_devs ;
	}
	

	public void RT_runInLoop(MNContext cxt)
	{
		List<UADev> devs = this.listUADevs() ;
		if(devs==null)
			return  ;
		
//		String name_sp = JS_getJsNameSp();
//		String fn = "fn_msgin_"+name_sp ;
//		Object pld = msg.getPayload() ;
//		boolean bjson = false;
//		if(pld instanceof JSONObject || pld instanceof JSONArray)
//		{
//			bjson = true ;
//			pld = pld.toString() ;
//		}
//		Object obj = cxt.scriptInvoke(fn,from_nd,msg.getTopic(), msg.getHeadsMap(),bjson,pld,this,this.getBelongTo()) ;
		
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject() ;
		if(ua_devs!=null)
		{
			JSONArray jarr = new JSONArray() ;
			for(UADev uad:this.ua_devs)
			{
				String id = uad.getId() ;
				jarr.put(id) ;
			}
			ret.put("dev_ids",jarr) ;
		}
		return ret ;
	}
	
	void fromJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("dev_ids") ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			this.ua_devs = new ArrayList<>() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String id = jarr.getString(i) ;
				UANode nd = ua_prj.findNodeById(id) ;
				if(nd==null || !(nd instanceof UADev))
					continue ;
				this.ua_devs.add((UADev)nd) ;
			}
		}
	}
	
}
