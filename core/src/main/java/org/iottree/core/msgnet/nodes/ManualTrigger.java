package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;

import org.iottree.core.msgnet.IMNOnOff;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNBase;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNet;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.util.CxtChgRule;
import org.iottree.core.msgnet.util.MsgSetRule;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

public class ManualTrigger  extends MNNodeStart implements IMNOnOff
{
	ArrayList<MsgSetRule> rules = new ArrayList<>() ;
	
	@Override
	public JSONTemp getInJT()
	{
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "manual";
	}

	@Override
	public String getTPTitle()
	{
		return g("manual");
	}

	@Override
	public String getColor()
	{
		return "#9fbccf";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf0a4";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(rules==null||rules.size()<=0)
			return true ;
		for(MsgSetRule r:this.rules)
		{
			if(!r.isValid(failedr))
				return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		JSONArray jarr = new JSONArray() ;
		jo.put("rules", jarr) ;
		if(this.rules!=null)
		{
			for(MsgSetRule r:this.rules)
			{
				jarr.put(r.toJO()) ;
			}
		}
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("rules") ;
		ArrayList<MsgSetRule> ccrs = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				StringBuilder failedr = new StringBuilder() ;
				MsgSetRule ccr = MsgSetRule.fromJO(tmpjo, failedr);
				if(ccr!=null)
					ccrs.add(ccr) ;
			}
		}
		this.rules = ccrs ;
	}

	public boolean RT_triggerByOnOff(StringBuilder failedr)
	{
		if(!this.supportInOnOff())
		{
			failedr.append("not support") ;
			return false;
		}
		
		MNMsg msg = new MNMsg() ;
		if(this.rules!=null)
		{
			for(MsgSetRule r:this.rules)
			{
				Object obj = r.RT_getSorVal(this.getBelongTo(),this,msg);
				msg.CXT_PK_setSubVal(r.getMsgSubN(),obj,null) ;
			}
		}
		
		RT_sendMsgOut(RTOut.createOutAll(msg)) ;
		return true;
	}


}
