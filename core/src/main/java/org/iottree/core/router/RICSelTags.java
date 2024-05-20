package org.iottree.core.router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.router.RouterInnCollator.OutStyle;
import org.iottree.core.router.RouterInnCollator.TagVal;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class RICSelTags extends RouterInnCollator
{
	static ILogger log = LoggerManager.getLogger(RICSelTags.class) ;
	
	public static final String TP="sel_tags";
	
	
	ArrayList<UATag> rtOutTags = new ArrayList<>() ;
	
	/**
	 * for write tag
	 */
	ArrayList<UATag> rtInWriteTags = new ArrayList<>() ;
	
	OutStyle outSty = OutStyle.interval ;
	
	JoinOut jout = new JoinOut(this,"sel_tags_out") ;
	private List<JoinOut> jouts = Arrays.asList(
			jout
			) ;
	
	private static final String J_TAGS_IN = "sel_tags_in";
	
	JoinIn jin = new JoinIn(this,J_TAGS_IN);
	
	private List<JoinIn> jinws = Arrays.asList(
			 jin
			) ;
	
	static String JIN_HELP = "" ;
	static
	{
		StringBuilder sb = new StringBuilder() ;
		
		sb.append("CMD: write_tag\r\n\r\n") ;
		JSONObject helpfmt = new JSONObject() ;
		helpfmt.put("cmd", "write_tag") ;
		helpfmt.put("tag", "tag.path.in.prj") ;
		helpfmt.put("value", "bool,int,float etc") ;
		helpfmt.put("dt", "ms") ;
		helpfmt.put("timeout", "ms") ;
		sb.append(helpfmt.toString(2)).append("\r\n e.g:\r\n {\"cmd\":\"write_tag\", \"tag\":\"ch1.p1.d1.start\", \"value\":true,\"dt\":12345678,\"timeout\":30000}") ;
		JIN_HELP = sb.toString() ;
	}
	
	public RICSelTags(RouterManager rm)
	{
		super(rm);
		
		jin.asHelpTxt(JIN_HELP) ;
	}
	
	@Override
	public String getTp()
	{
		return TP;
	}
	
	protected RouterInnCollator newInstance(RouterManager rm)
	{
		return new RICSelTags(rm) ;
	}

	public OutStyle getOutStyle()
	{
		return outSty ;
	}

	public List<UATag> getRTOutTags()
	{
		return this.rtOutTags ;
	}
	
	public void setRTOutTagsByIds(List<String> ids)
	{
		ArrayList<UATag> tags = new ArrayList<>() ;
		for(String id:ids)
		{
			UATag tag = this.belongPrj.findTagById(id) ;
			if(tag==null)
				continue ;
			tags.add(tag) ;
		}
		this.rtOutTags = tags ;
	}
	
	public List<UATag> getRTInWriteTags()
	{
		return this.rtInWriteTags ;
	}
	
	public void setRTInWriteTagsByIds(List<String> ids)
	{
		ArrayList<UATag> tags = new ArrayList<>() ;
		for(String id:ids)
		{
			UATag tag = this.belongPrj.findTagById(id) ;
			if(tag==null)
				continue ;
			if(!tag.isCanWrite())
				continue ;
			tags.add(tag) ;
		}
		this.rtInWriteTags = tags ;
		
		StringBuilder sb = new StringBuilder() ;
		sb.append(JIN_HELP).append("\r\n\r\nTags:\r\n") ;
		for(UATag tag:tags)
		{
			sb.append(tag.getNodeCxtPathInPrj()+"\r\n") ;
		}
		
		//sb.append("\r\n").append(JIN_HELP) ;
		this.jin.asHelpTxt(sb.toString()) ;
	}

	@Override
	public List<JoinIn> getJoinInList()
	{
		if(rtInWriteTags==null||rtInWriteTags.size()<=0)
			return null ;
		return jinws ;
	}
	
	@Override
	public List<JoinOut> getJoinOutList()
	{
		if(rtOutTags==null||rtOutTags.size()<=0)
			return null ;
		return jouts ;
	}
	
	/**
	 * override by sub
	 */
	@Override
	protected void RT_runInIntvLoop()
	{
		if(rtOutTags==null||rtOutTags.size()<=0)
			return ;
		
		JSONArray jarr = new JSONArray() ;
		for(UATag tag:rtOutTags)
		{
			JSONObject jo = tag.RT_toFlatJson() ;
			jarr.put(jo) ;
		}
		
		RT_sendToJoinOut(jout,new RouterObj(jarr)) ;
	}
	
	/**
	 * override by sub
	 */
	@Override
	protected void RT_runOnChgTagVal(TagVal tv)
	{
		
	}
	
//	public String pullOut(String join_out_name) throws Exception
//	{
//		switch(join_out_name)
//		{
//		case "out":
//			return this.belongTo.belongTo.JS_get_rt_json_flat();
//		default:
//			return null ;
//		}
//	}
	
	@Override
	protected void RT_onRecvedFromJoinIn(JoinIn ji,RouterObj recved)
	{
		if(J_TAGS_IN.equals(ji.getName()))
		{
			JSONObject recvjo = recved.getJSONObject(); //(JSONObject)recved ;
			RT_onInWriteTag(recvjo) ;
		}
	}
	
	private void RT_onInWriteTag(JSONObject recvjo)
	{
		String cmd = recvjo.optString("cmd") ;
		if("write_tag".equals(cmd))
		{
			String path = recvjo.optString("tag") ;
			if(Convert.isNullOrEmpty(path))
			{
				this.RT_fireErr("RT_onInWriteTag warn: write_tag jo has not tag prop \r\n"+recvjo.toString(), null);
				return ;
			}
			Object objv = recvjo.opt("value") ;
			if(objv==null)
			{
				this.RT_fireErr("RT_onInWriteTag warn: write_tag jo has not value prop \r\n"+recvjo.toString(), null);
				return ;
			}
			long dt = recvjo.optLong("dt",-1) ;
			long timeout = recvjo.optLong("timeout",-1) ;
			if(dt<=0 || timeout<=0)
			{
				this.RT_fireErr("RT_onInWriteTag warn: write_tag jo has no dt or timeout value prop \r\n"+recvjo.toString(), null);
				return ;
			}
			if(System.currentTimeMillis()>dt+timeout)
			{
				this.RT_fireErr("RT_onInWriteTag warn: write_tag is timeout and discard \r\n"+recvjo.toString(), null);
				return ;
			}
			UATag tag = this.belongPrj.getTagByPath(path) ;
			if(tag==null)
			{
				this.RT_fireErr("RT_onInWriteTag warn: not tag with path="+path, null);
				return ;
			}
			UATag write_tag = null ;
			for(UATag wt : this.rtInWriteTags)
			{
				if(wt.getId().equals(tag.getId()))
				{
					write_tag = wt ;
					break ;
				}
			}
			if(write_tag==null)
			{
				this.RT_fireErr("RT_onInWriteTag warn: tag is not set to be write path="+path, null);
				return ;
			}
			
			StringBuilder failedr = new StringBuilder() ;
			log.warn("RT_onInWriteTag RT_writeVal path="+path+" val="+objv);
			if(!write_tag.RT_writeVal(objv, failedr))
			{
				this.RT_fireErr("RT_onInWriteTag warn:"+failedr, null);
				return ;
			}
			
			
			
			return ;
		}
		
		this.RT_fireErr("RT_onInWriteTag warn: unknown recved JSON \r\n"+recvjo.toString(), null);
		
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		
		jo.put("out_sty", this.outSty.getInt()) ;
		JSONArray jarr = new JSONArray() ;
		if(rtOutTags!=null)
		{
			for(UATag tag:rtOutTags)
			{
				jarr.put(tag.getId()) ;
			}
		}
		jo.put("out_tagids", jarr) ;
		
		jarr = new JSONArray() ;
		if(this.rtInWriteTags!=null)
		{
			for(UATag tag:rtInWriteTags)
			{
				jarr.put(tag.getId()) ;
			}
		}
		jo.put("in_tagids", jarr) ;
		return jo ;
	}
	

	protected boolean fromJO(JSONObject jo,StringBuilder failedr)
	{
		super.fromJO(jo,failedr);
		
		
		this.outSty = OutStyle.valOfInt(jo.optInt("out_sty",0)) ;
		JSONArray jarr = jo.getJSONArray("out_tagids") ;
		if(jarr!=null)
		{
			ArrayList<String> ids = new ArrayList<>() ;
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String tmpid = jarr.getString(i) ;
				if(Convert.isNullOrEmpty(tmpid))
					continue ;
				ids.add(tmpid) ;
				setRTOutTagsByIds(ids) ;
			}
		}
		
		jarr = jo.getJSONArray("in_tagids") ;
		if(jarr!=null)
		{
			ArrayList<String> ids = new ArrayList<>() ;
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String tmpid = jarr.getString(i) ;
				if(Convert.isNullOrEmpty(tmpid))
					continue ;
				ids.add(tmpid) ;
				setRTInWriteTagsByIds(ids) ;
			}
		}
		return true ;
	}

	
}
