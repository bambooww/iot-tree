package org.iottree.core.msgnet.nodes;

import org.iottree.core.UATag;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.nodes.NS_TagEvtTrigger.MsgOutSty;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class NS_TagValChgTrigger extends MNNodeStart 
{
	String tagId = null ;
	
	boolean ignoreInvalid = true ;
	
	
	
	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "tag_valchg";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_valchg");
	}

	@Override
	public String getColor()
	{
		return "#a1cbde";
	}

	@Override
	public String getIcon()
	{
		return "PK_trigger";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(tagId))
		{
			failedr.append("no tag selected") ;
			return false;
		}
		return true ;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("tagid", this.tagId) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.tagId = jo.optString("tagid") ;
	}

	
	public boolean RT_fireByChgValTrigger(UATag tag,Object curval)
	{
		if(!tag.getId().equals(this.tagId))
			return false;
		
		MNMsg msg = new MNMsg();
		JSONObject jo = new JSONObject() ;
		jo.put("tag_id", this.tagId) ;
		jo.put("tag_path", tag.getNodeCxtPathInPrj()) ;
		jo.putOpt("tag_val", curval) ;
		msg.asPayload(jo);
		RT_sendMsgOut(RTOut.createOutAll(msg));
		return true ;
	}
}
