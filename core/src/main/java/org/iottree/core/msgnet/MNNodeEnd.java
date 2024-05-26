package org.iottree.core.msgnet;

import java.util.List;

import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public abstract class MNNodeEnd extends MNNode
{
	private boolean bOnOff=true;
	
	@Override
	public boolean supportInConn()
	{
		return true;
	}

	public boolean supportOutOnOff()
	{
		return false;
	}
	
	public boolean getOutOnOff()
	{
		return bOnOff ;
	}
	
	@Override
	public final int getOutNum()
	{
		return 0;
	}

	@Override
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO();
		jo.put("out_onoff_sup", this.supportOutOnOff()) ;
		if(supportOutOnOff())
			jo.put("out_onoff", this.bOnOff) ;
		return jo;
	}
	
	@Override
	public boolean fromJO(JSONObject jo)
	{
		if(!super.fromJO(jo))
			return false;
		this.bOnOff = jo.optBoolean("out_onoff",false) ;
		return true ;
	}
	
	@Override
	protected boolean fromJOBasic(JSONObject jo,StringBuilder failedr)
	{
		super.fromJOBasic(jo, failedr) ;
		
		this.bOnOff = jo.optBoolean("out_onoff",false) ;
		return true ;
	}
	
}
