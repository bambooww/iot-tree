package org.iottree.core.msgnet.nodes;

import java.util.HashSet;

import org.iottree.core.UATag;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Lan;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * trigger by Tag value changing
 *  
 * @author jason.zhu
 */
public class NS_TagEvtTrigger  extends MNNodeStart 
{
	public static enum MsgOutSty
	{
		triggered,
		released,
		both;
		
		public String getTitle()
		{
			Lan lan = Lan.getLangInPk(NS_TagEvtTrigger.class) ;
			return lan.g("outsty_"+this.name()) ;
		}
	}
	
	private HashSet<String> evt_ids = new HashSet<>() ;
	
	private MsgOutSty msgOutSty = MsgOutSty.triggered ;
	
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
		if(this.msgOutSty==MsgOutSty.both)
			return 2;
		else
			return 1 ;
	}
	
	@Override
	public String getOutTitle(int idx)
	{
		switch(this.msgOutSty)
		{
		case triggered:
			if(idx==0)
				return g("triggered_out") ;
			else
				return null ;
		case released:
			if(idx==0)
				return g("released_out") ;
			else
				return null ;
		case both:
			if(idx==0)
				return g("triggered_out") ;
			else if(idx==1)
				return g("released_out") ;
			else
				return null ;
		default:
			return "" ;
		}
	}

	@Override
	public String getTP()
	{
		return "tag_evt_trigger";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_evt_trigger");
	}

	@Override
	public String getColor()
	{
		return "#ff8566";
	}

	@Override
	public String getIcon()
	{
		return "PK_status";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("evt_ids", evt_ids) ;
		jo.put("out_sty", msgOutSty.ordinal()) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("evt_ids") ;
		HashSet<String> ss = new HashSet<>() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String id = jarr.getString(i) ;
				ss.add(id) ;
			}
		}
		this.evt_ids = ss ;
		int od = jo.optInt("out_sty", 0) ;
		if(od<0)
			od= 0 ;
		this.msgOutSty = MsgOutSty.values()[od] ;
	}
	
	public boolean RT_fireByEventTrigger(ValAlert va,Object curval)
	{
		if(this.msgOutSty==MsgOutSty.released)
			return false;
		
		if(this.evt_ids==null||!evt_ids.contains(va.getUid()))
			return false ;
		
		MNMsg msg = new MNMsg();
		JSONObject jo = va.RT_get_triggered_jo() ;
		jo.putOpt("tag_val", curval) ;
		msg.asPayload(jo);
		switch(this.msgOutSty)
		{
		case triggered:
			RT_sendMsgOut(RTOut.createOutAll(msg));
			return true ;
		case released:
			//RT_sendMsgOut(RTOut.createOutAll(msg));
			return false ;
		case both:
			RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, msg));
			return true ;
		default:
			return false ;
		}
		
	}
	
	public boolean RT_fireByEventRelease(ValAlert va,Object curval)
	{
		if(this.msgOutSty==MsgOutSty.triggered)
			return false;
		
		if(this.evt_ids==null||!evt_ids.contains(va.getUid()))
			return false ;
		
		MNMsg msg = new MNMsg();
		JSONObject jo = va.RT_get_release_jo() ;
		jo.putOpt("tag_val", curval) ;
		msg.asPayload(jo);
		
		switch(this.msgOutSty)
		{
		case triggered:
			//RT_sendMsgOut(RTOut.createOutAll(msg));
			return false ;
		case released:
			RT_sendMsgOut(RTOut.createOutAll(msg));
			return true ;
		case both:
			RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(1, msg));
			return true ;
		default:
			return false ;
		}
	}
}
