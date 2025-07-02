package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.basic.ValEvent;
import org.iottree.core.msgnet.IMNContainer;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.nodes.NS_TagEvtTrigger.MsgOutSty;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class NS_TagChgTrigger extends MNNodeStart 
{
	//String tagId = null ;
	
	boolean ignoreInvalid = true ;
	
	/**
	 * Tag value is not changed ,only updated date
	 */
	boolean ignoreUpOnly = false;
	
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
		return "tag_chg";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_chg");
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
		jo.put("ignore_update",ignoreUpOnly) ;
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
		this.ignoreUpOnly = jo.optBoolean("ignore_update",false) ;
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
	
	public boolean RT_fireDtUpValNotChg(UATag tag)
	{
		if(ignoreUpOnly)
			return false;
		
		HashSet<String> idset = getTagIdSet() ;
		if(idset==null)
			return false;
		if(!idset.contains(tag.getId()))
			return false;
		
		UAVal v = tag.RT_getVal() ;
		if(!v.isValid())
		{
			if(this.ignoreInvalid)
				return false;
		}

		sendTagOut(tag,v) ;
		return true; 
	}
	
	public boolean RT_fireValChg(UATag tag,boolean cur_valid,Object curval)
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
		
		sendTagOut(tag,v) ;
		return true ;
	}
	
	private void sendTagOut(UATag tag,UAVal v)
	{
		if(v==null)
			return ;
		
		boolean valid = v.isValid() ;
		MNMsg msg = new MNMsg();
		JSONObject jo = new JSONObject() ;
		jo.put("tag_id", tag.getId()) ;
		jo.put("tag_path", tag.getNodeCxtPathInPrj()) ;
		jo.putOpt("tag_title", tag.getTitle()) ;
		jo.put("updt", v.getValDT()) ;
		jo.put("chgdt", v.getValChgDT()) ;
		UAVal.ValTP vt = tag.getValTp() ;
		if(vt!=null)
			jo.put("vt", vt.getStr());
		jo.put("valid", valid) ;
		if(valid)
			jo.putOpt("tag_val", v.getObjVal()) ;
		else
			jo.putOpt("tag_err",v.getErr()) ;
		msg.asPayload(jo);
		RT_sendMsgOut(RTOut.createOutAll(msg));
	}
}
