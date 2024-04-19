package org.iottree.core.router;

import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.Lan;
import org.json.JSONObject;

/**
 * Router Outer Adapter 
 *   which will be extends by Outer Plugin
 *   
 * @author jason.zhu
 *
 */
public abstract class RouterOuterAdp extends RouterNode
{
	static Lan lan = Lan.getLangInPk(RouterOuterAdp.class) ;
	
	
	/**
	 * 
	 * @author jason.zhu
	 *
	 */
	public static class LeftIn
	{
		
	}
	
	public static class LeftOut
	{
		
	}
		
	public RouterOuterAdp(RouterManager rm)
	{
		super(rm) ;
	}
	
	
	public String getId()
	{
		return id ;
	}
	
	public abstract String getTp() ;
	
	public final String getTpTitle()
	{
		return lan.g("adp_"+this.getTp()) ;
	}
	
	public abstract RouterOuterAdp newInstance(RouterManager rm) ;
	
	final void sendTxtOutToJoinOut(JoinOut jo,String txt)
	{
		for(JoinConn jc : this.belongTo.CONN_getROA2RICMap().values())
		{
			String fid = jc.getFromId() ;
			if(fid.equals(jo.getFromId()))
			{
				JoinIn ji = jc.getToJI() ;
				sendTxtOutToConn(jo,jc,ji,txt) ;
			}
		}
	}
	
	final void sendTxtOutToConn(JoinOut jo,JoinConn jc,JoinIn ji,String txt)
	{
		String ret = jc.RT_doTrans(txt) ;
		if(ret==null)
			return ;//error
		
		RouterInnCollator roa = (RouterInnCollator)ji.getBelongNode() ;
		roa.RT_onRecvedFromJoinIn(ji,ret) ;
	}
	
	protected abstract void RT_onRecvedFromJoinIn(JoinIn ji,String recved_txt) throws Exception;
	
	public abstract void RT_start();
	
	public abstract void RT_stop();
	
	public abstract boolean RT_isRunning() ;
	
		
//	public JSONObject toJO()
//	{
//		JSONObject jo = super.toJO() ;
//		jo.put("id", id) ;
//		
//		//jo.put("_tp", this.getTp()) ;
//		jo.put("en", this.bEnable) ;
//		return jo ;
//	}
//	
//	
//	
//	protected boolean fromJO(JSONObject jo,StringBuilder failedr)
//	{
//		super.fromJO(jo, failedr) ;
//		this.id = jo.getString("id") ;
//		
//		this.bEnable = jo.optBoolean("en",true) ;
//		return true ;
//	}
	
	public static RouterOuterAdp transFromJO(RouterManager rm,JSONObject jo,StringBuilder failedr)
	{
		String tp = jo.getString("_tp") ;
		RouterOuterAdp ro = RouterOuterAdpCat.getAdpByTP(tp) ;
		if(ro==null)
		{
			failedr.append("no RouterOuter found with tp="+tp) ;
			return null;
		}

		RouterOuterAdp ret = ro.newInstance(rm) ;
		if(!ret.fromJO(jo,failedr))
			return null ;
		return ret ;
	}
}
