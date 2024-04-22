package org.iottree.core.router;

import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class RouterNode
{
	String id ;
	
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	

	boolean bEnable = true ;
	

	RouterManager belongTo ;
	
	UAPrj belongPrj ;
	
	
	
	public RouterNode(RouterManager rm)
	{
		this.belongTo = rm ;
		if(rm!=null)
			this.belongPrj = rm.belongTo ;
		this.id = IdCreator.newSeqId() ;
	}
	
	public RouterManager getBelongTo()
	{
		return this.belongTo ;
	}
	
	public UAPrj getBelongPrj()
	{
		return this.belongPrj ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	

	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	

	public String getName()
	{
		if(name==null)
			return "" ;
		return name;
	}

	public String getTitle()
	{
		if(title==null)
			return "" ;
		return title;
	}

	public String getDesc()
	{
		if(desc==null)
			return "" ;
		
		return desc;
	}
	
	
	public abstract String getTp() ;
	
	public abstract String getTpTitle();
	

	public abstract List<JoinIn> getJoinInList() ;
	
	public abstract List<JoinOut> getJoinOutList() ;
	
	public JoinIn getJoinInByName(String n)
	{
		List<JoinIn> jis = getJoinInList() ;
		if(jis==null)
			return null ;
		for(JoinIn ji:jis)
		{
			if(n.equals(ji.name))
				return ji ;
		}
		return null ;
	}
	
	public JoinOut getJoinOutByName(String n)
	{
		List<JoinOut> jos = getJoinOutList() ;
		if(jos==null)
			return null ;
		for(JoinOut jo:jos)
		{
			if(n.equals(jo.name))
				return jo ;
		}
		return null ;
	}
	
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", id) ;
		jo.put("n", this.getName()) ;
		jo.putOpt("t", this.title) ;
		jo.putOpt("d", this.desc) ;
		jo.put("_tp", this.getTp()) ;
		jo.put("en", this.bEnable) ;
		return jo ;
	}
	
	
	public JSONObject toListJO()
	{
		JSONObject jo = toJO() ;
		jo.put("_tpt", this.getTpTitle());
		
		List<JoinIn> jis = getJoinInList() ;
		if(jis!=null&&jis.size()>0)
		{
			JSONArray jarr = new JSONArray() ;
			for(JoinIn ji:jis)
			{
				jarr.put(ji.toListJO()) ;
			}
			jo.put("in_joins", jarr) ;
		}
		List<JoinOut> jos = getJoinOutList() ;
		if(jos!=null&&jos.size()>0)
		{
			JSONArray jarr = new JSONArray() ;
			for(JoinOut o:jos)
			{
				jarr.put(o.toListJO()) ;
			}
			jo.put("out_joins", jarr) ;
		}
		
		return jo ;
	}
	
	
	protected boolean fromJO(JSONObject jo,StringBuilder failedr)
	{
		this.id = jo.getString("id") ;
		this.name = jo.getString("n") ;
		this.title = jo.optString("t") ;
		this.desc = jo.optString("d") ;
		this.bEnable = jo.optBoolean("en",true) ;
		return true ;
	}
	
	private long rtLastErrDT =-1;//System.currentTimeMillis() ; ;
	private String rtLastErr = null;//"test err" ;
	private Throwable rtLastExcept = null ;
	
	protected void RT_fireErr(String err,Throwable e)
	{
		this.rtLastErr = err ;
		this.rtLastExcept = e ;
		if(Convert.isNullOrEmpty(err) && e==null)
			this.rtLastErrDT = -1 ;
		else
			this.rtLastErrDT = System.currentTimeMillis() ;
	}
	
	public long RT_getLastErrDT()
	{
		return rtLastErrDT ;
	}
	
	public String getLastErr()
	{
		return rtLastErr ;
	}
	
	public Throwable getLastException()
	{
		return rtLastExcept ;
	}
	
	public JSONObject RT_getRunInf()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id) ;
		jo.put("rt_err_dt", this.rtLastErrDT) ;
		jo.putOpt("rt_last_err", this.rtLastErr) ;
		
		//jo.putOpt(, value)
		return jo ;
	}
}
