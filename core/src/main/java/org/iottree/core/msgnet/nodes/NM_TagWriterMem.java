package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;
import java.util.HashMap;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.nodes.NM_TagWriter.TagItem;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * set tag value in memory. it will not trigger device driver to work
 * @author jason.zhu
 *
 */
public class NM_TagWriterMem extends MNNodeMid
{
	public static class TagItem
	{
		String tagPath ;
		
		MNCxtValSty wValSty = MNCxtValSty.vt_bool;
		
		String wSubN = "" ;
		
		long wDelay = 0 ; //delay before do write
		
		private UATag tag = null ;
		
		public TagItem(String tagpath,MNCxtValSty w_valsty,String w_subn)
		{
			this.tagPath = tagpath ;
			this.wValSty = w_valsty ;
			this.wSubN = w_subn ;
		}
		
		private TagItem()
		{}
		
		
		public boolean isValid(StringBuilder failedr)
		{
			if(Convert.isNullOrEmpty(this.tagPath))
			{
				failedr.append("tag path cannot be null or empty") ;
				return false;
			}
			
			if(tag==null)
			{
				failedr.append("not tag with path ="+this.tagPath) ;
				return false;
			}
			
			if(Convert.isNullOrEmpty(this.wSubN))
			{
				failedr.append("write sub name is null or empty") ;
				return false;
			}
			return true ;
		}
		
		public JSONObject toJO()
		{
			JSONObject jo = new JSONObject() ;
			jo.putOpt("tag", this.tagPath) ;
			jo.putOpt("w_valsty", this.wValSty.name()) ;
			jo.putOpt("w_subn", this.wSubN) ;
			jo.putOpt("w_delay", this.wDelay) ;
			return jo ;
		}
		
		public static TagItem fromJO(JSONObject jo)
		{
			TagItem ret = new TagItem() ;
			ret.tagPath = jo.optString("tag") ;
			ret.wValSty  = MNCxtValSty.valueOf(jo.getString("w_valsty")) ;
			ret.wSubN = jo.optString("w_subn") ;
			ret.wDelay = jo.optLong("w_delay",0) ;
			return ret;
		}
	}
	
	
	ArrayList<TagItem> tagItems = new ArrayList<>() ;
	
	/**
	 * Asynchronous run mode
	 * true it's will run in thread after in msg. this will not block outer push thread.
	 * but it will ignore all in msg when thread running
	 */
	boolean asynMode = true;
	
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
		return "tag_writer_mem";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_writer_mem");
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(tagItems==null||tagItems.size()<=0)
			return true ;
		for(TagItem ti:this.tagItems)
		{
			if(!ti.isValid(failedr))
				return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("asyn", this.asynMode) ;
		
		JSONArray jarr = new JSONArray() ;
		if(tagItems!=null)
		{
			for(TagItem ccr:this.tagItems)
			{
				JSONObject tmpjo = ccr.toJO() ;
				jarr.put(tmpjo) ;
			}
		}
		jo.put("tags",jarr) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.asynMode = jo.optBoolean("asyn", true) ;
		JSONArray jarr = jo.optJSONArray("tags") ;
		ArrayList<TagItem> ccrs = new ArrayList<>() ;
		UAPrj prj = (UAPrj)this.getBelongTo().getContainer() ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				TagItem ccr = TagItem.fromJO(tmpjo);
				if(ccr!=null)
					ccrs.add(ccr) ;
				
				String tagpath = ccr.tagPath ;
				if(Convert.isNotNullEmpty(tagpath))
				{
					UATag tag = prj.getTagByPath(tagpath) ;
					ccr.tag = tag ;
				}
			}
		}
		this.tagItems = ccrs ;
	}

	// --------------
	

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		if(this.tagItems==null||this.tagItems.size()<=0)
			return null ;
		
		StringBuilder failedr = new StringBuilder() ;
		if(!isParamReady(failedr))
		{
			RT_DEBUG_ERR.fire("tag_w", failedr.toString());
			return null ;
		}
		
		HashMap<UATag,Object> tag2val = new HashMap<>() ;
		for(TagItem ti:this.tagItems)
		{
			UATag tag = ti.tag ;
			Object wval = ti.wValSty.RT_getValInCxt(ti.wSubN,this.getBelongTo(), this, msg) ;
			if(wval==null)
			{
				RT_DEBUG_ERR.fire("tag_w",ti.wValSty.getTitle()+" "+ti.wSubN+" return null");
				return null ;
			}
			tag2val.put(tag,wval) ;
		}
		
		return runSyn(msg,tag2val) ;
	}
	
	
	private RTOut runSyn(MNMsg msg,HashMap<UATag,Object> tag2val)
	{
		StringBuilder failedr = new StringBuilder() ;
		for(TagItem ti:this.tagItems)
		{
			if(ti.wDelay>0)
			{
				try
				{
					Thread.sleep(ti.wDelay);
				}catch(Exception ee) {}
			}
			
			UATag tag = ti.tag ;
			Object wval = tag2val.get(tag) ;
			tag.RT_setVal(wval);
		}
		
		return RTOut.createOutAll(msg);
	}
	
}
