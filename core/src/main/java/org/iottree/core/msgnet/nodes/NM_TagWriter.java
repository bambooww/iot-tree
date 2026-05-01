package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNCxtValSty;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.nodes.NM_TagReader.TagItem;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 设置一个或多个具体的标签，写入特定的数据
 * 触发的消息格式不限
 * 
 * @author jason.zhu
 *
 */
public class NM_TagWriter extends MNNodeMid implements IMNRunner
{
	public static class TagItem
	{
		String tagPath ;
		
		MNCxtValSty wValSty = MNCxtValSty.vt_bool;
		
		String wSubN = "" ;
		
		long wDelay = 0 ; //delay before do write
		
		boolean ignoreNoInput = false ;
		
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
			
			if(!tag.isCanWrite())
			{
				failedr.append("not writable tag with path ="+this.tagPath) ;
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
			jo.putOpt("ignore_no_input", ignoreNoInput) ;
			return jo ;
		}
		
		public static TagItem fromJO(JSONObject jo)
		{
			TagItem ret = new TagItem() ;
			ret.tagPath = jo.optString("tag") ;
			ret.wValSty  = MNCxtValSty.valueOf(jo.getString("w_valsty")) ;
			ret.wSubN = jo.optString("w_subn") ;
			ret.wDelay = jo.optLong("w_delay",0) ;
			ret.ignoreNoInput = jo.optBoolean("ignore_no_input",false) ;
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
		return 3;
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		switch(idx)
		{
		case 1:
			return "green";
		case 2:
			return "red";
		}
		return null ;
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case 1:
			return "ok";
		case 2:
			return "err";
		}
		return null ;
	}

	@Override
	public String getTP()
	{
		return "tag_writer";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_writer");
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
	
	private transient boolean bAsynRun = false;
	private transient AsynTh asynTh = null ;

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		if(bAsynRun)
		{
			this.RT_DEBUG_WARN.fire("tag_w","node is running,ignore this msg",msg.toJO().toString());
			return null ; //ignore all in msg
		}
		
		if(this.tagItems==null||this.tagItems.size()<=0)
		{
			return RTOut.createOutIdx().asIdxMsg(2, new MNMsg().asPayload(new JSONObject().put("succ", false).put("err", "no item set")));
		}
		
		StringBuilder failedr = new StringBuilder() ;
		if(!isParamReady(failedr))
		{
			RT_DEBUG_ERR.fire("tag_w", failedr.toString());
			return RTOut.createOutIdx().asIdxMsg(2, new MNMsg().asPayload(new JSONObject().put("succ", false).put("err",  failedr.toString())));
		}
		
		HashMap<UATag,Object> tag2val = new HashMap<>() ;
		for(TagItem ti:this.tagItems)
		{
			UATag tag = ti.tag ;
			Object wval = ti.wValSty.RT_getValInCxt(ti.wSubN,this.getBelongTo(), this, msg) ;
			if(wval==null)
			{
				if(ti.ignoreNoInput)
					continue ;
				
				String err = ti.wValSty.getTitle()+" "+ti.wSubN+" return null" ;
				RT_DEBUG_ERR.fire("tag_w",err);
				return RTOut.createOutIdx().asIdxMsg(2, new MNMsg().asPayload(new JSONObject().put("succ", false).put("err", err)));
			}
			tag2val.put(tag,wval) ;
		}
		
		if(!this.asynMode)
		{
			MNMsg rtoo = runSyn(msg,tag2val) ;
			return RTOut.createOutIdx().asIdxMsg(0, rtoo).asIdxMsg(1, new MNMsg().asPayload(new JSONObject().put("succ", true)));
		}
		
		//asyn
		synchronized(this)
		{
			bAsynRun = true ;
			asynTh = new AsynTh(msg,tag2val) ;
			asynTh.start();
		}
		
		return RTOut.createOutIdx().asIdxMsg(1, new MNMsg().asPayload(new JSONObject().put("succ", true)));
	}
	
	
	private MNMsg runSyn(MNMsg msg,HashMap<UATag,Object> tag2val)
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
			if(wval==null)
				continue ;
			if(!tag.RT_writeVal(wval, failedr))
			{
				RT_DEBUG_ERR.fire("tag_w", failedr.toString());
				return null ;
			}
		}
		
		RT_DEBUG_ERR.clear("tag_w");
		//return RTOut.createOutIdx().asIdxMsg(0,msg);
		return msg ;
	}
	
	private class AsynTh extends Thread
	{
		MNMsg msg ;
		HashMap<UATag,Object> tag2val;
		
		public AsynTh(MNMsg msg,HashMap<UATag,Object> tag2val)
		{
			this.msg = msg ;
			this.tag2val = tag2val ;
		}
		
		public void run()
		{
			try
			{
				MNMsg m = runSyn(msg,tag2val) ;
				if(m!=null)
				{
					NM_TagWriter.this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, m));
				}
			}
			catch(Exception ee)
			{
				RT_DEBUG_ERR.fire("tag_w",ee.getMessage(),ee);
			}
			finally
			{
				asynTh = null ;
				bAsynRun = false;
			}
		}
	}

	@Override
	public boolean RT_start(StringBuilder failedr)
	{
		//failedr.append("no support") ;
		//return false;
		return true ;
	}

	@Override
	public synchronized void RT_stop()
	{
		
	}

	@Override
	public boolean RT_isRunning()
	{
		return bAsynRun;
	}

	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		return false;
	}
	
	/**
	 * false will not support runner
	 * @return
	 */
	public boolean RT_runnerEnabled()
	{
		return this.asynMode ;
	}
	
	/**
	 * true will not support manual trigger to start
	 * @return
	 */
	public boolean RT_runnerStartInner()
	{
		return true;
	}
}
