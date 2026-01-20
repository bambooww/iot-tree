package org.iottree.core.msgnet.nodes;

import org.iottree.core.util.*;
import org.iottree.core.msgnet.MNManager;
import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.msgnet.MNNodeResCaller;
import org.iottree.core.msgnet.ResCaller;
import org.json.JSONObject;

public class NRES_Common extends MNNodeResCaller
{
	String callerUID = null ;
	
	@Override
	public String getTP()
	{
		return "res_common";
	}

	@Override
	public String getTPTitle()
	{
		return g("res_common");
	}

	@Override
	public String getColor()
	{
		return "#dddddd";
	}

	@Override
	public String getIcon()
	{
		return "\\uf25d";
	}
	

	public String getResCallerUID()
	{
		return this.callerUID ;
	}
	

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(getResCaller()==null)
		{
			failedr.append("no caller set or found") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		return new JSONObject().putOpt("caller_uid", this.callerUID) ;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.callerUID = jo.optString("caller_uid") ;
	}
	
	
	
	@Override
	public ResCaller getResCaller()
	{
		if(Convert.isNullOrEmpty(this.callerUID))
			return null ;
		return MNManager.getResCallerByUID(this.callerUID) ;
	}

}
