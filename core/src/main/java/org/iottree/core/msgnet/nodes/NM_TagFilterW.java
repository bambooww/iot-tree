package org.iottree.core.msgnet.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.msgnet.IMNRunner;
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

public class NM_TagFilterW  extends MNNodeMid implements IMNRunner
{
	static ILogger log = LoggerManager.getLogger(NM_TagWriter.class) ;
	
	ArrayList<UATag> writeTags = new ArrayList<>() ;
	
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
		jo.put("asyn", this.asynMode) ;
		jo.put("tagids", getTagIds()) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		if(jo==null)
			return ;
		this.asynMode = jo.optBoolean("asyn", true) ;
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
	
	private transient boolean bAsynRun = false;
	private transient AsynTh asynTh = null ;
	
	private static class WTag
	{
		UATag tag ;
		
		Object writeVal ;
		
		long delay= 0 ;
		
		public WTag(long delay,UATag tag,Object wval)
		{
			this.delay = delay ;
			this.tag = tag ;
			this.writeVal = wval ;
		}
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		if(bAsynRun)
		{
			this.RT_DEBUG_WARN.fire("on_msg_in","node is running,ignore this msg",msg.toJO().toString());
			return null ; //ignore all in msg
		}
		
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
		
		StringBuilder failedr = new StringBuilder() ;
		List<WTag> wtags = checkCmdValid(jo,failedr) ;
		if(wtags==null)
		{
			this.RT_DEBUG_WARN.fire("on_msg_in",failedr.toString(),jo.toString());
			return null ;
		}
		
		
		if(!this.asynMode)
		{
			return runSyn(msg,wtags) ;
		}
		
		//asyn
		synchronized(this)
		{
			bAsynRun = true ;
			asynTh = new AsynTh(msg,wtags) ;
			asynTh.start();
		}
		
		return null ;
	}
	
	private List<WTag> checkCmdValid(JSONObject pldjo,StringBuilder failedr)
	{
		String cmd = pldjo.optString("cmd") ;
//		if(Convert.isNullOrEmpty(cmd))
//		{
//			failedr.append("no cmd found") ;
//			return null ;
//		}
		long ts = pldjo.optLong("cmd_ts", -1) ;
		long to = pldjo.optLong("cmd_to",-1) ;
		if(to>0)
		{
			if(System.currentTimeMillis()-ts>to)
			{
				failedr.append("payload write cmd is timeout") ;
				return null;
			}
		}
		
		if("write_tags".equals(cmd))
		{
			JSONArray jarr = pldjo.optJSONArray("tags") ;
			int n = 0 ;
			if(jarr==null || (n=jarr.length())<=0)
			{
				failedr.append("not tags found");
				return null ;
			}
			ArrayList<WTag> rets = new ArrayList<>(n) ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				WTag wtag = parseJoToWTag(tmpjo,failedr) ;
				if(wtag==null)
					return  null ;
				rets.add(wtag) ;
			}
			return rets ;
		}
		else //("write_tag".equals(cmd))
		{
			WTag wtag = parseJoToWTag(pldjo,failedr) ;
			if(wtag==null)
				return  null ;
			return Arrays.asList(wtag);
		}
		
		//return null ;
	}
	
	private WTag parseJoToWTag(JSONObject pldjo,StringBuilder failedr)
	{
		String path = pldjo.optString("tag") ;
		if(Convert.isNullOrEmpty(path))
		{
			failedr.append("write_tag jo has not tag prop");
			return null;
		}
		Object objv = pldjo.opt("value") ;
		if(objv==null)
		{
			failedr.append("write_tag jo has not value prop");
			return null;
		}
		
		UATag tag = this.getBelongTo().getPrj().getTagByPath(path) ;
		if(tag==null)
		{
			failedr.append("not tag with path="+path);
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
			failedr.append("tag is not set to be write path="+path);
			return null;
		}
		
		long delay = pldjo.optLong("delay", 0) ;
		
		return new WTag(delay,write_tag,objv) ;
	}
	
	private RTOut runSyn(MNMsg msg,List<WTag> wtags)
	{
		for(WTag tag:wtags)
		{
			if(tag.delay>0)
			{
				try
				{
					Thread.sleep(tag.delay);
				}catch(Exception eee) {}
			}
			
			StringBuilder failedr = new StringBuilder() ;
			if(!tag.tag.RT_writeVal(tag.writeVal, failedr))
			{
				this.RT_DEBUG_ERR.fire("write_tag","RT_onInWriteTag warn:"+failedr);
				return null;
			}
		}
		this.RT_DEBUG_ERR.clear("write_tag");
		return RTOut.createOutAll(msg);
	}
	
//	private RTOut runSynWTag(MNMsg msg,JSONObject recvjo)
//	{
//		String cmd = recvjo.optString("cmd") ;
//		if("write_tag".equals(cmd))
//		{
//			
//			StringBuilder failedr = new StringBuilder() ;
//			log.warn("RT_onInWriteTag RT_writeVal path="+path+" val="+objv);
//			if(!write_tag.RT_writeVal(objv, failedr))
//			{
//				this.RT_DEBUG_ERR.fire("write_tag","RT_onInWriteTag warn:"+failedr);
//				return null;
//			}
//			
//		}
//		
//		this.RT_DEBUG_ERR.fire("write_tag","RT_onInWriteTag warn: unknown recved JSON",recvjo.toString());
//		return null ;
//	}
	
	
	private class AsynTh extends Thread
	{
		MNMsg msg ;
		List<WTag> wtags ;
		
		public AsynTh(MNMsg msg,List<WTag> wtags)
		{
			this.msg = msg ;
			this.wtags = wtags ;
		}
		
		public void run()
		{
			try
			{
				RTOut rout = runSyn(msg,this.wtags) ;
				if(rout!=null)
				{
					NM_TagFilterW.this.RT_sendMsgOut(rout);
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
		failedr.append("no support") ;
		return false;
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
	
	private static String IN_STR = "" ;
	static
	{
		JSONObject tmpjo = new JSONObject() ;
		tmpjo.put("tag", "ch1.gg1.tag11") ;
		tmpjo.put("value", true) ;
		tmpjo.put("cmd_ts", 1231244535) ;
		tmpjo.put("cmd_to", 20000) ;
		IN_STR += "<pre>"+tmpjo.toString(4)+"</pre><br>OR<br>" ;
		
		tmpjo = new JSONObject("{\r\n" + 
				"    \"cmd\":\"write_tags\",\"cmd_ts\":12312445345,\"cmd_to\":20000,\r\n" + 
				"    \"tags\":[\r\n" + 
				"        {\"delay\":0,   \"tag\":\"ch1.gg1.tag11\",\"value\":true},\r\n" + 
				"        {\"delay\":2000,\"tag\":\"ch1.gg1.tag11\",\"value\":false}\r\n" + 
				"    ]\r\n" + 
				"}") ;
		IN_STR += "<pre>"+tmpjo.toString(4)+"</pre>" ;
	}
	
	@Override
	public String RT_getInTitle()
	{
		return IN_STR ;
	}
}
