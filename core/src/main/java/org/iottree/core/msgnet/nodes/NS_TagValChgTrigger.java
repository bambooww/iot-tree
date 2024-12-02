package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.msgnet.IMNContainer;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.nodes.NS_TagEvtTrigger.MsgOutSty;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class NS_TagValChgTrigger extends MNNodeStart 
{
	//String tagId = null ;
	
	boolean ignoreInvalid = true ;
	
	ArrayList<String> tagPaths = null ;
	

	transient private HashSet<String> tagIdSet = null ;
	
	//transient private List<UATag> tags = null ;
	
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
		if(tagPaths==null ||tagPaths.size()<=0)
		{
			failedr.append("no Tags set") ;
			return false;
		}
		
		HashSet<String> idset = getTagIdSet() ;
		if(idset==null||idset.size()<=0)
		{
			failedr.append("no Tag found") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("ignore_invalid",ignoreInvalid) ;
		jo.putOpt("tag_paths", this.tagPaths) ;
		return jo ;
	}
	

	private UAPrj getPrj()
	{
		IMNContainer mnc = this.getBelongTo().getBelongTo().getBelongTo();
		if (mnc == null || !(mnc instanceof UAPrj))
			return null;

		return (UAPrj) mnc;
	}
	

	@Override
	protected void setParamJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("tag_paths") ;
		if(jarr!=null)
		{
			ArrayList<String> subts = new ArrayList<>() ;
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
				subts.add(jarr.getString(i)) ;
			this.tagPaths = subts ;
		}
		ignoreInvalid = jo.optBoolean("ignore_invalid",true) ;
		
		synchronized(this)
		{
			tagIdSet = null ;
			//tags = null;
		}
	}
	
	public boolean isIgnoreInvalid()
	{
		return this.ignoreInvalid ;
	}
	

	public ArrayList<String> getTagPaths()
	{
		return this.tagPaths ;
	}
	
	public boolean checkFitTag(UATag tag)
	{
		if(this.tagPaths==null)
			return false;
		String np = tag.getNodeCxtPathInPrj() ;
		return this.tagPaths.contains(np) ;
	}
	
//	public synchronized List<UATag> getMonTags()
//	{
//		
//	}
	
	private synchronized HashSet<String> getTagIdSet()
	{
		if(tagIdSet!=null)
			return tagIdSet ;
		
		if(tagPaths==null || tagPaths.size()<=0)
			return null ;
		
		UAPrj prj = this.getPrj() ;
		if(prj==null)
			return null ;
		
		//ArrayList<UATag> tags = new ArrayList<>(tagPaths.size()) ;
		HashSet<String> rets = new HashSet<>() ;
		for(String tagp:tagPaths)
		{
			UATag tag = prj.getTagByPath(tagp) ;
			if(tag==null)
				continue ;
			rets.add(tag.getId()) ;
		}
		tagIdSet = rets ;
		return rets ;
	}
	
	
	public boolean RT_fireByChgValTrigger(UATag tag,boolean cur_valid,Object curval)
	{
		HashSet<String> idset = getTagIdSet() ;
		if(idset==null)
			return false;
		if(!idset.contains(tag.getId()))
			return false;
		
		UAVal v = tag.RT_getVal() ;
		if(!cur_valid)
		{
			if(this.ignoreInvalid)
				return false;
		}
		
		MNMsg msg = new MNMsg();
		JSONObject jo = new JSONObject() ;
		jo.put("tag_id", tag.getId()) ;
		jo.put("tag_path", tag.getNodeCxtPathInPrj()) ;
		jo.put("valid", cur_valid) ;
		jo.putOpt("tag_val", curval) ;
		msg.asPayload(jo);
		RT_sendMsgOut(RTOut.createOutAll(msg));
		return true ;
	}
}
