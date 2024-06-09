package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class NM_TagFilterW  extends MNNodeMid
{
	static ILogger log = LoggerManager.getLogger(NM_TagWriter.class) ;
	
	ArrayList<UATag> writeTags = new ArrayList<>() ;
	
	@Override
	public String getColor()
	{
		return "#a1cbde";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf02c";
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

	@Override
	public String getTP()
	{
		return "tag_filter_w";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_filter_w");
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(this.writeTags==null||this.writeTags.size()<=0)
		{
			failedr.append("no write tag set") ;
			return false;
		}
		return true ;
	}
	
	private ArrayList<String> getTagIds()
	{
		ArrayList<String> rets = new ArrayList<>() ;
		if(writeTags!=null)
		{
			for(UATag tag:this.writeTags)
				rets.add(tag.getId()) ;
		}
		return rets ;
	}
	
	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("tagids", getTagIds()) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		if(jo==null)
			return ;
		JSONArray tagids_jarr = jo.optJSONArray("tagids") ;
		ArrayList<String> tagids = new ArrayList<>() ;
		if(tagids_jarr!=null)
		{
			int n = tagids_jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String tid = tagids_jarr.getString(i) ;
				tagids.add(tid) ;
			}
		}
		setTagsByIds(tagids) ;
	}
	
	private void setTagsByIds(List<String> ids)
	{
		UAPrj prj = this.getBelongTo().getPrj();
		ArrayList<UATag> tags = new ArrayList<>() ;
		for(String id:ids)
		{
			UATag tag = prj.findTagById(id) ;
			if(tag==null)
				continue ;
			if(!tag.isCanWrite())
				continue ;
			tags.add(tag) ;
		}
		this.writeTags = tags ;
	}
	
	// --------------

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		Object ob = msg.getPayload() ;
		if(ob==null)
		{
			this.RT_DEBUG_WARN.fire("on_msg_in","msg payload is null");
			return null ;
		}

		JSONObject jo = msg.getPayloadJO(null) ;
		if(jo==null)
		{
			this.RT_DEBUG_WARN.fire("on_msg_in","msg payload is not JSONObject");
			return null ;
		}
		UATag wtag = RT_onInWriteTag(jo) ;
		if(wtag==null)
			return null;//RTOut.createOutAll(msg) ;
		JSONObject tmpjo = new JSONObject() ;
		tmpjo.put("tag_id", wtag.getId()) ;
		tmpjo.put("tag_title", wtag.getTitle()) ;
		tmpjo.put("cmd", ob) ;
		return RTOut.createOutAll(new MNMsg().asPayload(tmpjo)) ;
	}
	
	private UATag RT_onInWriteTag(JSONObject recvjo)
	{
		String cmd = recvjo.optString("cmd") ;
		if("write_tag".equals(cmd))
		{
			String path = recvjo.optString("tag") ;
			if(Convert.isNullOrEmpty(path))
			{
				this.RT_DEBUG_ERR.fire("write_tag","RT_onInWriteTag warn: write_tag jo has not tag prop",recvjo.toString());
				return null;
			}
			Object objv = recvjo.opt("value") ;
			if(objv==null)
			{
				this.RT_DEBUG_ERR.fire("write_tag","RT_onInWriteTag warn: write_tag jo has not value prop",recvjo.toString());
				return null;
			}
			long dt = recvjo.optLong("dt",-1) ;
			long timeout = recvjo.optLong("timeout",-1) ;
			if(dt<=0 || timeout<=0)
			{
				this.RT_DEBUG_ERR.fire("write_tag","RT_onInWriteTag warn: write_tag jo has no dt or timeout value prop",recvjo.toString());
				return null;
			}
			if(System.currentTimeMillis()>dt+timeout)
			{
				this.RT_DEBUG_ERR.fire("write_tag","RT_onInWriteTag warn: write_tag is timeout and discard",recvjo.toString());
				return null;
			}
			
			UATag tag = this.getBelongTo().getPrj().getTagByPath(path) ;
			if(tag==null)
			{
				this.RT_DEBUG_ERR.fire("write_tag","RT_onInWriteTag warn: not tag with path="+path);
				return null;
			}
			UATag write_tag = null ;
			for(UATag wt : this.writeTags)
			{
				if(wt.getId().equals(tag.getId()))
				{
					write_tag = wt ;
					break ;
				}
			}
			if(write_tag==null)
			{
				this.RT_DEBUG_ERR.fire("write_tag","RT_onInWriteTag warn: tag is not set to be write path="+path);
				return null;
			}
			
			StringBuilder failedr = new StringBuilder() ;
			log.warn("RT_onInWriteTag RT_writeVal path="+path+" val="+objv);
			if(!write_tag.RT_writeVal(objv, failedr))
			{
				this.RT_DEBUG_ERR.fire("write_tag","RT_onInWriteTag warn:"+failedr);
				return null;
			}
			return write_tag;
		}
		
		this.RT_DEBUG_ERR.fire("write_tag","RT_onInWriteTag warn: unknown recved JSON",recvjo.toString());
		return null ;
	}
}
