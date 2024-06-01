package org.iottree.core.msgnet;

import org.iottree.core.util.Convert;
import org.iottree.core.util.Lan;
import org.json.JSONObject;

/**
 *  
 * @author jason.zhu
 */
public class MNCxtVar
{
	public static enum KeepTP
	{
		mem(0), save(1);

		private final int val;

		KeepTP(int v)
		{
			val = v;
		}

		public int getInt()
		{
			return val;
		}

		public String getTitle()
		{
			Lan lan = Lan.getLangInPk(MNCxtVar.class);
			return lan.g(name());
		}

		public static KeepTP valOfInt(int i)
		{
			switch (i)
			{
			case 0:
				return mem;
			case 1:
				return save;
			
			default:
				return null;
			}
		}
	}
	
	String name = null ;
	
	MNCxtValTP valTP = null ;
	
	Object defaultV =null ;
	
	KeepTP keepTP = KeepTP.mem ;
	
	public MNCxtVar(String name,MNCxtValTP valtp,Object dev_v,KeepTP kptp)
	{
		if(Convert.isNullOrEmpty(name))
			throw new IllegalArgumentException("name cannot be null or empty") ;
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(name, true, sb))
			throw new IllegalArgumentException(sb.toString()) ;
		this.name = name ;
		this.valTP = valtp ;
		this.defaultV = dev_v ;
		this.keepTP = kptp!=null?kptp:KeepTP.mem ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public MNCxtValTP getValTP()
	{
		return this.valTP ;
	}
	
	public String getValTPT()
	{
		if(this.valTP==null)
			return "" ;
		return this.valTP.getTitle() ;
	}
	
	public Object getDefaultVal()
	{
		return this.defaultV ;
	}
	
	public String getDefaultValStr()
	{
		if(this.defaultV==null)
			return "" ;
		return this.defaultV.toString() ;
	}
//	public Object getDefaultValObj()
//	{
//		if(this.defaultVStr==null)
//			return null ;
//		return this.valTP.transStrToObj(this.defaultVStr) ;
//	}
	
	public KeepTP getKeepTP()
	{
		return this.keepTP ;
	}
	
	public String getKeepTPT()
	{
		return this.keepTP.getTitle() ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("n", this.name) ;
		if(valTP!=null)
			jo.putOpt("vt", valTP.getName()) ;
		jo.putOpt("defv", this.defaultV) ;
		jo.put("ktp", keepTP.getInt()) ;
		return jo  ;
	}
	
	public static MNCxtVar fromJO(JSONObject jo,StringBuilder failedr)
	{
		String n = jo.optString("n") ;
		if(Convert.isNullOrEmpty(n))
		{
			failedr.append("name cannot be null or empty") ;
			return null ;
		}
		StringBuilder sb = new StringBuilder() ;
		if(!Convert.checkVarName(n, true, failedr))
			return null ;
		String vt = jo.optString("vt") ;
		MNCxtValTP vtp = MNCxtValTP.parseFrom(vt) ;
		Object defv = jo.opt("devf") ;
		KeepTP ktp = KeepTP.valOfInt(jo.optInt("ktp",0)) ;
		if(ktp==null)
			ktp = KeepTP.mem ;
		return new MNCxtVar(n,vtp,defv,ktp) ;
	}
}
