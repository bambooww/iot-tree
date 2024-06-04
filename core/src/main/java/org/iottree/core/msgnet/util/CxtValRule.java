package org.iottree.core.msgnet.util;

import org.iottree.core.msgnet.MNCxtPkTP;
import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class CxtValRule
{
	
	ValOper oper = ValOper.ALL[0];
	
	MNCxtValSty pm2ValSty = MNCxtValSty.vt_str;
	String pm2SubN = "" ;
	
	String ruleTT = "" ;
	
	public CxtValRule()
	{}
	
//	public MNCxtPkTP getPm1PkTP()
//	{
//		return pm1Pk ;
//	}
//	
//	public String getPm1SubN()
//	{
//		return pm1SubN ;
//	}
	
	public ValOper getOper()
	{
		return oper ;
	}
	
	public MNCxtValSty getPm2ValSty()
	{
		return this.pm2ValSty ;
	}
	
	public String getPm2SubN()
	{
		return this.pm2SubN ;
	}
	
	public String getRuleTitle()
	{
		//return this.ruleTT ;
		return oper.getTitle()+""+pm2SubN ;
	}
	
	public boolean isValid(StringBuilder failedr)
	{

		if(oper.isNeedPm2())
		{
			if(Convert.isNullOrEmpty(pm2SubN))
			{
				failedr.append("oper ["+oper.getTitle()+"] must has sub name") ;
				return false;
			}
		}
		return true;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;

		jo.put("op", oper.getName()) ;
		jo.put("pm2_valsty", this.pm2ValSty.name()) ;
		jo.putOpt("pm2_subn", this.pm2SubN) ;
		jo.putOpt("rtt", this.ruleTT) ;
		return jo ;
	}
	
	public static CxtValRule fromJO(JSONObject jo)
	{
		CxtValRule ret = new CxtValRule() ;

		ret.oper = ValOper.getOperByName(jo.optString("op","eq")) ;
		ret.pm2ValSty  = MNCxtValSty.valueOf(jo.getString("pm2_valsty")) ;
		ret.pm2SubN = jo.optString("pm2_subn") ;
		ret.ruleTT = jo.optString("rtt","") ;
		return ret;
	}
	
	public boolean RT_onMsgInRun(MNNode node,MNMsg msg,Object pm1val) //,StringBuilder failedr)
	{
		Object pm2val = this.pm2ValSty.RT_getValInCxt(this.pm2SubN,node.getBelongTo(), node, msg) ;
		return this.oper.checkMath(pm1val, pm2val) ;
	}
}
