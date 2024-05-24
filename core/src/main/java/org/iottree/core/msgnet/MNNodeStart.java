package org.iottree.core.msgnet;

import java.util.List;

import org.json.JSONObject;

/**
 * triggered by net outer 1) no input 2) has output
 * 
 * @author jason.zhu
 *
 */
public abstract class MNNodeStart extends MNNode
{
	private boolean bOnOff=true;
	
	@Override
	public final boolean supportInConn()
	{
		return false;
	}

	public boolean supportInOnOff()
	{
		return false;
	}
	
	public boolean getInOnOff()
	{
		return bOnOff ;
	}

	@Override
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO();
		jo.put("in_onoff_sup", this.supportInOnOff()) ;
		if(supportInOnOff())
			jo.put("in_onoff", this.bOnOff) ;
		return jo;
	}
	
	@Override
	public boolean fromJO(JSONObject jo)
	{
		if(!super.fromJO(jo))
			return false;
		this.bOnOff = jo.optBoolean("in_onoff",false) ;
		return true ;
	}
	
	@Override
	protected boolean fromJOBasic(JSONObject jo,StringBuilder failedr)
	{
		super.fromJOBasic(jo, failedr) ;
		
		this.bOnOff = jo.optBoolean("in_onoff",false) ;
		return true ;
	}
	
	@Override
	protected final RTOut RT_onMsgIn(MNConn in_conn,MNMsg msg)
	{
		throw new RuntimeException("start has no in") ;
	}
	

	public abstract boolean RT_trigger(StringBuilder failedr) ;
}
