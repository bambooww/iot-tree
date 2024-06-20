package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.util.CxtChgRule;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

public class NM_Change extends MNNodeMid implements ILang
{
	ArrayList<CxtChgRule> chgRules = new ArrayList<>() ;
	
	@Override
	public String getColor()
	{
		return "#e6d970";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf074";
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
		return 1;
	}

//	@Override
	public String getTP()
	{
		return "change";
	}

	@Override
	public String getTPTitle()
	{
		return g("change");
	}
	
	@Override
	protected boolean supportCxtVars()
	{
		return true ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(chgRules!=null)
		{
			for(CxtChgRule ccr:this.chgRules)
			{
				if(!ccr.isValid(failedr))
					return false;
			}
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		JSONArray jarr = new JSONArray() ;
		if(chgRules!=null)
		{
			for(CxtChgRule ccr:this.chgRules)
			{
				JSONObject tmpjo = ccr.toJO() ;
				jarr.put(tmpjo) ;
			}
		}
		jo.put("rules",jarr) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("rules") ;
		ArrayList<CxtChgRule> ccrs = new ArrayList<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				StringBuilder failedr = new StringBuilder() ;
				CxtChgRule ccr = CxtChgRule.fromJO(tmpjo, failedr);
				if(ccr!=null)
					ccrs.add(ccr) ;
			}
		}
		this.chgRules = ccrs ;
	}
	
	// --------------

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		if(this.chgRules!=null)
		{
			StringBuilder failedr = new StringBuilder() ;
			int idx = 0 ;
			boolean b_ok=true;
			for(CxtChgRule ccr:this.chgRules)
			{
				if(!ccr.RT_onMsgInRun(this, msg, failedr))
				{
					this.RT_DEBUG_ERR.fire("chg", "Rule "+idx+":"+failedr.toString());
					b_ok=false;
					return null ;
				}
				idx ++ ;
			}
			if(b_ok)
				this.RT_DEBUG_ERR.clear("chg");
		}
		return RTOut.createOutAll(msg) ;
	}
}
