package org.iottree.core.ecp;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.ecp.util.Point;
import org.iottree.core.ecp.util.Size;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Process Unit (过程单元)
 * 
 * @author jason.zhu
 */
public abstract class ECPNode extends ECPBase //extends ECPJtSet
{
	static ECPNode createByTp(ECPNet net,String tp,String id)
	{
		if(Convert.isNullOrEmpty(tp)||Convert.isNullOrEmpty(id))
			return null ;
		switch(tp)
		{
		case ECPCompIns.TP:
		case "unit":
			return new ECPCompIns(net,id) ;
//		case ECPComp.TP:
//			return new ECPComp(net,id) ;
		}
		return null ;
	}
	
	ECPNet net;
	
	float x = 0 ;
	
	float y = 0 ;
	
	float w = 100 ;
	
	float h = 100 ;
	
	boolean bEnable = true ;
	
	private ArrayList<ECPInOut> inouts = new ArrayList<>() ;
	
	private ArrayList<ECPPlugin> plugins = new ArrayList<>() ;
	
	// for client show only,not server using
	private JSONArray inner_drawitems = null;
	
	public ECPNode(ECPNet net,String id,ECPNode cp_nd)
	{
		super(id) ;
		
		this.net = net ;
		
		if(cp_nd!=null)
		{
			this.setBasic(cp_nd.getTitle(), cp_nd.getDesc());
			this.w = cp_nd.w ;
			this.h = cp_nd.h ;
			this.bEnable = cp_nd.bEnable ;
			
			for(ECPInOut io:cp_nd.inouts)
			{
				ECPInOut newio = io.copyMe(this) ;
				inouts.add(newio) ;
			}
		}
	}
	
	
	public final ECPNet getNet()
	{
		return this.net ;
	}
	
//	@Override
//	public String getTP()
//	{
//		return TP;
//	}
	
	public abstract String getNodeTP() ;
	
	public float getX()
	{
		return this.x ;
	}
	
	public float getY()
	{
		return this.y ;
	}
	
	public Point getPos()
	{
		return new Point(this.x,this.y) ;
	}
	
	public float getW()
	{
		return this.w ;
	}
	
	public float getH()
	{
		return this.h ;
	}
	
	public Size getSize()
	{
		return new Size(this.w,this.h) ;
	}
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	

	public List<ECPInOut> getInOuts()
	{
		return this.inouts ;
	}
	
	public ECPInOut getInOutById(String id)
	{
		for(ECPInOut jt:this.inouts)
		{
			if(id.equals(jt.getId()))
				return jt ;
		}
		return null ;
	}
	

	public ECPInOut removeInOut(String id)
	{
		ECPInOut io = getInOutById(id) ;
		if(io==null)
			return null ;
		if(this.inouts.remove(io))
			return io;
		return null ;
	}
	
	public List<ECPPlugin> getPlugins()
	{
		return this.plugins ;
	}
	
	public ECPPlugin getPluginById(String id)
	{
		for(ECPPlugin jt:this.plugins)
		{
			if(id.equals(jt.getId()))
				return jt ;
		}
		return null ;
	}
	
	public ECPPlugin removePlugin(String id)
	{
		ECPPlugin io = getPluginById(id) ;
		if(io==null)
			return null ;
		if(this.plugins.remove(io))
			return io;
		return null ;
	}
	
	protected void onAfterLoaded()
	{}
	
	@Override
	public boolean removeMe()
	{
		return this.getNet().removeNodeById(this.getId())!=null ;
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = super.toJO() ;
		ret.put("tp", this.getNodeTP()).put("x", this.x).put("y", this.y)
				.put("w", this.w).put("h", this.h).put("en", bEnable) ;
		
		JSONArray inout_jarr = new JSONArray() ;
		ret.put("inouts", inout_jarr) ;
		for(ECPInOut io:this.inouts)
			inout_jarr.put(io.toJO()) ;
		
		JSONArray plugins_jarr = new JSONArray() ;
		ret.put("plugins", plugins_jarr) ;
		for(ECPPlugin io:this.plugins)
			plugins_jarr.put(io.toJO()) ;
		
		ret.putOpt("inner_dis", inner_drawitems) ;
		return ret ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		super.fromJO(jo) ;
		
		updateStructByJO(jo);
		return true;
	}
	
	public void updateStructByJO(JSONObject jo)
	{
		//super.updateStructByJO(jo);
		this.x = jo.optFloat("x",0);
		this.y = jo.optFloat("y",0);
		this.w = jo.optFloat("w",100);
		this.h = jo.optFloat("h",100);
		this.bEnable = jo.optBoolean("en",true) ;
		
		JSONArray inout_jarr = jo.optJSONArray("inouts") ;
		ArrayList<ECPInOut> ios = new ArrayList<>() ;
		if(inout_jarr!=null)
		{
			int n = inout_jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = inout_jarr.getJSONObject(i);
				String tmpid = tmpjo.optString("id") ;
				if(Convert.isNullOrEmpty(tmpid))
					continue ;
				ECPInOut io = new ECPInOut(this,tmpid) ;
				if(io.fromJO(tmpjo))
					ios.add(io) ;
			}
		}
		this.inouts = ios ;
		
		JSONArray plugin_jarr = jo.optJSONArray("plugins") ;
		ArrayList<ECPPlugin> pgs = new ArrayList<>() ;
		if(plugin_jarr!=null)
		{
			int n = plugin_jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = plugin_jarr.getJSONObject(i);
				String tmpid = tmpjo.optString("id") ;
				if(Convert.isNullOrEmpty(tmpid))
					continue ;
				ECPPlugin io = new ECPPlugin(this,tmpid) ;
				if(io.fromJO(tmpjo))
					pgs.add(io) ;
			}
		}
		this.plugins = pgs ;
		
		this.inner_drawitems = jo.optJSONArray("inner_dis") ;
	}
}
