package org.iottree.core.msgnet.util;

import org.iottree.core.msgnet.MNBase;
import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.util.CxtChgRule.Action;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class MsgSetRule
{
	String msgSubN ="payload";
	
	MNCxtValSty sorValSty = MNCxtValSty.vt_str ;
	
	String sorSubN ;
	
	public MsgSetRule()
	{
		
	}
	
	public String getMsgSubN()
	{
		return this.msgSubN ;
	}
	
	public boolean isMsgSubPayload()
	{
		return "payload".equals(this.msgSubN) ;
	}
	
	public boolean isPayloadConstantRule()
	{
		return "payload".equals(this.msgSubN) && sorValSty.isConstant();
	}
	
	public MNCxtValSty getSorValSty()
	{
		return this.sorValSty ;
	}
	
	public String getSorSubN()
	{
		return this.sorSubN ;
	}
	
	public boolean isValid(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(this.msgSubN))
		{
			failedr.append("msg sub name is empty") ;
			return false;
		}
		
		return true ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("msg_subn", this.msgSubN) ;
		jo.putOpt("sor_valsty", sorValSty.name()) ;
		jo.putOpt("sor_subn", this.sorSubN) ;
		return jo ;
	}
	
	public static MsgSetRule fromJO(JSONObject jo,StringBuilder failedr)
	{
		if(jo==null)
			return null ;
		
		MsgSetRule ret = new MsgSetRule() ;
		ret.msgSubN = jo.optString("msg_subn","payload") ;
		String nn = jo.optString("sor_valsty",null) ;
		if(Convert.isNotNullEmpty(nn))
		{
			ret.sorValSty = MNCxtValSty.valueOf(nn) ;
			if(ret.sorValSty==null)
				ret.sorValSty = MNCxtValSty.vt_str ;
		}
		ret.sorSubN = jo.optString("sor_subn","") ;
		return ret ;
	}
	
	public Object RT_getSorVal(MNNet net,MNBase item,MNMsg msg)
	{
		//if(Convert.isNullOrEmpty(this.sorSubN))
		//	return null ;
		return sorValSty.RT_getValInCxt(this.sorSubN, net, item, msg);
	}
}
