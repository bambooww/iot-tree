package org.iottree.core.alert;

import java.util.HashMap;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Alert Level definition
 * 
 * @author jason.zhu
 *
 */
public class AlertDef
{
	
	public static class Lvl
	{
		int lvl = 0 ; //0 default  1-5
		
		String color ;
		
		String sound = null ;
		
		boolean blink = false;
		
		private Lvl()
		{}
		
		
		private Lvl(int lvl,String color,String sound,boolean blink)
		{
			this.lvl = lvl ;
			this.color = color ;
			this.sound = sound ;
			this.blink = blink ;
		}
		
		public int getLvl()
		{
			return this.lvl ;
		}
		
		public String getColor()
		{
			if(Convert.isNotNullEmpty(this.color))
				return color ;
			return getDefaultLvlColor(this.lvl) ;
		}
		
		public String getSound()
		{
			return sound ;
		}
		
		public boolean isBlink()
		{
			return blink ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.put("lvl", this.lvl);
			jo.putOpt("color",color) ;
			jo.putOpt("sound", this.sound) ;
			jo.putOpt("blink", this.blink) ;
			return jo ;
		}
	}
	
	static Lvl fromLvlJO(JSONObject jo)
	{
		int lvl = jo.optInt("lvl",-1) ;
		if(lvl<0)
			return null ;
		Lvl ret = new Lvl() ;
		ret.lvl = lvl ;
		ret.color = jo.optString("color", "yellow") ;
		ret.sound = jo.optString("sound") ;
		ret.blink = jo.optBoolean("blink",false) ;
		return ret;
	}
	
	/**
	 * Alert mask definition
 *  some tag alert may be ignored by some reson
	 * @author jason.zhu
	 *
	 */
	public class Mask
	{
		String tagId = null ;
		
		String tagPathPrefix = null ;
		
		
	}
	
	private static String getDefaultLvlColor(int lvl)
	{
		switch(lvl)
		{
		case 1:
			return "#ff5630";
		case 2:
			return "#cc6c1d";
		case 3:
			return "#f2d51b" ;
		case 4:
			return "#81ec21";
		default:
			return "#54afee";
		}
	}
	
	private static Lvl[] DEF_LVLS = new Lvl[5] ;
	static
	{
		DEF_LVLS[0] = new Lvl(1,getDefaultLvlColor(1),"",true) ;
		DEF_LVLS[1] = new Lvl(2,getDefaultLvlColor(2),"",false) ;
		DEF_LVLS[2] = new Lvl(3,getDefaultLvlColor(3),"",false) ;
		DEF_LVLS[3] = new Lvl(4,getDefaultLvlColor(4),"",false) ;
		DEF_LVLS[4] = new Lvl(5,getDefaultLvlColor(5),"",false) ;
	}
	
	
	
	private Lvl[] lvls = new Lvl[5] ;
	
	private int defaultLvl = 3;
	
	public AlertDef()
	{
		for(int i = 0 ; i < 5 ; i ++)
			lvls[i] = DEF_LVLS[i] ;
	}
	
	public int getDefaultLvl()
	{
		return this.defaultLvl ;
	}
	
	public Lvl getLvl(int lvl)
	{
		if(lvl<=0||lvl>5) lvl = this.defaultLvl ;
		return lvls[lvl-1] ;
	}
	
	public Lvl[] getLvls()
	{
		return this.lvls ;
	}
	
	public JSONObject LVL_toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("def_lvl",this.defaultLvl) ;
		JSONArray jarr = new JSONArray() ;
		jo.put("lvls",jarr) ;
		for(Lvl lvl:this.lvls)
		{
			JSONObject tmpjo = lvl.toJO() ;
			jarr.put(tmpjo) ;
		}
		return jo ;
	}
	
	public boolean LVL_fromJO(JSONObject jo)
	{
		this.defaultLvl = jo.optInt("def_lvl", 3) ;
		if(this.defaultLvl<=0 || this.defaultLvl>5)
			this.defaultLvl = 3 ;
		
		JSONArray jarr = jo.optJSONArray("lvls") ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				Lvl lvl = fromLvlJO(tmpjo) ;
				if(lvl==null)
					continue ;
				int lv = lvl.getLvl() ;
				if(lv<=0||lv>5)
					continue ;
				lvls[lv-1] = lvl ;
			}
		}
		return true ;
	}
}
