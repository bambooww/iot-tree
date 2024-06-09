package org.iottree.core.msgnet.util;

import org.iottree.core.msgnet.MNCxtPkTP;
import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.msgnet.MNCxtValTP;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class CxtValRule
{
	
	ValOper oper = ValOper.ALL[0];
	
	MNCxtValSty pm2ValSty = MNCxtValSty.vt_str;
	String pm2SubN = "" ;
	
	MNCxtValSty pm3ValSty = MNCxtValSty.vt_str;
	String pm3SubN = "" ;
	
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
	
	public MNCxtValSty getPm3ValSty()
	{
		return this.pm3ValSty ;
	}
	
	public String getPm3SubN()
	{
		return this.pm3SubN ;
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
			MNCxtValTP vtp =  pm2ValSty.getConstantValTP() ;
			if(vtp!=null)
			{
				try
				{
					vtp.transStrToObj(this.pm2SubN);
				}
				catch(Exception ee)
				{
					failedr.append("str "+this.pm2SubN+" trans to "+vtp.getName()+" err:"+ee.getMessage()) ;
					return false;
				}
			}
//			if(!oper.getName().equals(VO_IsType.TP))
//			{
//				if(Convert.isNullOrEmpty(pm2SubN))
//				{
//					failedr.append("oper ["+oper.getTitle()+"] must has sub name") ;
//					return false;
//				}
//			}
		}
//		if(oper.isNeedPm3())
//		{
//			if(Convert.isNullOrEmpty(pm3SubN))
//			{
//				failedr.append("oper ["+oper.getTitle()+"] must has second sub name") ;
//				return false;
//			}
//		}
		return true;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;

		jo.put("op", oper.getName()) ;
		jo.putOpt("pm2_valsty", this.pm2ValSty.name()) ;
		jo.putOpt("pm2_subn", this.pm2SubN) ;
		jo.putOpt("pm3_valsty", this.pm3ValSty.name()) ;
		jo.putOpt("pm3_subn", this.pm3SubN) ;
		jo.putOpt("rtt", this.ruleTT) ;
		return jo ;
	}
	
	public static CxtValRule fromJO(JSONObject jo)
	{
		CxtValRule ret = new CxtValRule() ;

		ret.oper = ValOper.getOperByName(jo.optString("op","eq")) ;
		ret.pm2ValSty  = MNCxtValSty.valueOf(jo.optString("pm2_valsty","vt_str")) ;
		ret.pm2SubN = jo.optString("pm2_subn") ;
		ret.pm3ValSty  = MNCxtValSty.valueOf(jo.optString("pm3_valsty","vt_str")) ;
		ret.pm3SubN = jo.optString("pm3_subn") ;
		ret.ruleTT = jo.optString("rtt","") ;
		return ret;
	}
	
	public boolean RT_onMsgInRun(MNNode node,MNMsg msg,Object pm1val) //,StringBuilder failedr)
	{
		Object pm2val = null;
		if(this.oper.isNeedPm2())
		{
			if(this.oper.getName().equals(VO_IsType.TP))
				pm2val = this.pm2ValSty ;
			else
				pm2val = this.pm2ValSty.RT_getValInCxt(this.pm2SubN,node.getBelongTo(), node, msg) ;
		}
		Object pm3val = null;
		if(this.oper.isNeedPm3())
			pm3val=this.pm3ValSty.RT_getValInCxt(this.pm3SubN,node.getBelongTo(), node, msg) ;
		return this.oper.checkMath(pm1val, pm2val,pm3val) ;
	}
}
