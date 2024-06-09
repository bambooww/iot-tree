package org.iottree.core.msgnet.nodes;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNCxtPkTP;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.util.CxtValRule;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

public class NM_OnOff extends MNNodeMid // implements ILang
{
	MNCxtPkTP propPk = MNCxtPkTP.msg ;
	
	String propSubN = "payload" ;
	
	CxtValRule valRule = null ;
	
	long outMinIntv = 0 ;
	
	@Override
	public String getColor()
	{
		return "#e6d970";
	}
	
	@Override
	public String getIcon()
	{
		return "PK_sw_onoff";
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
	
	public String getOutTitle(int idx)
	{
		String ss =  propPk.getTitle()+"."+this.propSubN ;
		if(valRule==null)
			ss += " ?";
		else
			ss += valRule.getRuleTitle() ;
		return ss ;
	}

//	@Override
	public String getTP()
	{
		return "onoff";
	}

	@Override
	public String getTPTitle()
	{
		return g("onoff");
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(propSubN))
		{
			failedr.append("Property must has sub name") ;
			return false;
		}
		
		if(valRule!=null)
		{
			if(!valRule.isValid(failedr))
				return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("prop_pktp", this.propPk.name()) ;
		jo.putOpt("prop_subn", this.propSubN) ;
		
		if(valRule!=null)
		{
			JSONObject tmpjo = valRule.toJO() ;
			jo.put("rule",tmpjo) ;
		}
		jo.put("out_min_intv", outMinIntv) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		propPk = MNCxtPkTP.valueOf(jo.optString("prop_pktp", MNCxtPkTP.msg.name())) ;
		propSubN = jo.optString("prop_subn");
		JSONObject tmpjo = jo.optJSONObject("rule") ;
		if(tmpjo!=null)
			valRule = CxtValRule.fromJO(tmpjo);
		this.outMinIntv = jo.optLong("out_min_intv", 0) ;
	}
	
	// --------------
	
	private transient long lastMsgOutMS = -1 ;

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		StringBuilder failedr = new StringBuilder() ;
		if(!this.isParamReady(failedr))
		{
			return null ;
		}
		
		Object pm1val = this.propPk.RT_getValInCxt(this.propSubN,this.getBelongTo(),this,msg) ;
		if(pm1val==null)
			return null; 
		if(!valRule.RT_onMsgInRun(this, msg, pm1val))
		{
			return null ;
		}
		
		if(this.outMinIntv>0)
		{
			if(System.currentTimeMillis()-lastMsgOutMS<outMinIntv)
				return null ; // in interval ignore msg ;
		}
		try
		{
			return RTOut.createOutAll(msg) ;
		}
		finally
		{
			lastMsgOutMS = System.currentTimeMillis() ;
		}
	}


}
