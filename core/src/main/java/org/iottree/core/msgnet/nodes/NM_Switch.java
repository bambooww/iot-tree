package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNCxtPkTP;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.util.CxtChgRule;
import org.iottree.core.msgnet.util.CxtValRule;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

public class NM_Switch extends MNNodeMid // implements ILang
{
	MNCxtPkTP propPk = MNCxtPkTP.msg ;
	String propSubN = "payload" ;
	
	ArrayList<CxtValRule> rules = new ArrayList<>() ;
	
	boolean b_otherwise = false;
	
	@Override
	public String getColor()
	{
		return "#e6d970";
	}
	
	@Override
	public String getIcon()
	{
		return "PK_switch";
	}

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
		if(this.rules==null||rules.size()<=0)
			return 1 ;
		return rules.size()+(b_otherwise?1:0);
	}
	
	@Override
	public String getOutColor(int idx)
	{
		if(b_otherwise && idx==rules.size())
		{
			return "#18807f" ;
		}
		return null ;
	}
	
//	@Override
	public String getTP()
	{
		return "switch";
	}

	@Override
	public String getTPTitle()
	{
		return g("switch");
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(propSubN))
		{
			failedr.append("Property must has sub name") ;
			return false;
		}
		
		if(rules!=null)
		{
			for(CxtValRule r:rules)
			{
				if(!r.isValid(failedr))
					return false;
			}
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("prop_pktp", this.propPk.name()) ;
		jo.putOpt("prop_subn", this.propSubN) ;
		
		JSONArray jarr = new JSONArray() ;
		if(rules!=null)
		{
			for(CxtValRule ccr:this.rules)
			{
				JSONObject tmpjo = ccr.toJO() ;
				jarr.put(tmpjo) ;
			}
		}
		jo.put("rules",jarr) ;
		jo.put("otherwise", this.b_otherwise) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		propPk = MNCxtPkTP.valueOf(jo.optString("prop_pktp", MNCxtPkTP.msg.name())) ;
		propSubN = jo.optString("prop_subn");
		
		JSONArray jarr = jo.optJSONArray("rules") ;
		ArrayList<CxtValRule> ccrs = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				//StringBuilder failedr = new StringBuilder() ;
				CxtValRule ccr = CxtValRule.fromJO(tmpjo);
				if(ccr!=null)
					ccrs.add(ccr) ;
			}
		}
		this.rules = ccrs ;
		this.b_otherwise = jo.optBoolean("otherwise",false) ;
	}
	
	// --------------

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		if(this.rules==null||this.rules.size()<=0)
		{
			if(this.b_otherwise)
				return RTOut.createOutAll(msg) ;
			
			return null ;
		}
		
		int otherwz_idx = this.rules.size() ;
		
		RTOut rtout = RTOut.createOutIdx() ;
		Object pm1val = this.propPk.RT_getValInCxt(this.propSubN,this.getBelongTo(),this,msg) ;
		if(pm1val==null)
		{
			if(this.b_otherwise)
			{
				rtout.asIdxMsg(otherwz_idx, msg) ;
				return rtout ;
			}
			return null;
		}
		//StringBuilder failedr = new StringBuilder() ;
		int rn = this.rules.size() ;
		boolean b_fit_rule = false;
		for(int i = 0 ; i < rn ; i ++)
		{
			CxtValRule ccr = this.rules.get(i) ;
			if(!ccr.RT_onMsgInRun(this, msg, pm1val))
			{
				continue ;
			}
			rtout.asIdxMsg(i, msg) ;
			b_fit_rule = true ;
		}
		
		if(b_fit_rule)
			return rtout ;
		
		if(this.b_otherwise)
		{
			rtout.asIdxMsg(otherwz_idx, msg) ;
			return rtout ;
		}
		
		return null ;
	}


	@Override
	public String RT_getOutTitle(int idx)
	{
		if(rules==null || idx<0 || idx>rules.size())
		{
			return null ;
		}
		if(idx<rules.size())
			return Convert.plainToHtml(propPk.getTitle()+"."+this.propSubN+rules.get(idx).getRuleTitle()) ;
		else
			return g("otherwise") ;
	}

}
