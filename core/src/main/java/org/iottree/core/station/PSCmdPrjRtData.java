package org.iottree.core.station;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import org.iottree.core.UACh;
import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UANodeOCTagsGCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class PSCmdPrjRtData extends PSCmd
{
	@Override
	public String getCmd()
	{
		return "prj_rtdata";
	}
	
	public PSCmdPrjRtData asStationLocalPrj(UAPrj prj) throws IOException
	{
		this.asParams(Arrays.asList(prj.getName())) ;
		
		String rtjson = prj.JS_get_rt_json();
		this.asCmdData(rtjson.getBytes("utf-8")) ;
		return this ;
	}

	@Override
	public void RT_onRecvedInPlatform(PlatformWSServer.SessionItem si,PStation ps) throws Exception
	{
		String prjname = this.getParamByIdx(0) ;
		if(Convert.isNullOrEmpty(prjname))
			return ;
		
		UAPrj platform_prj = UAManager.getInstance().getPrjByName(prjname) ;
		if(platform_prj==null)
			return ;
		
		JSONObject rt_jo = this.getCmdDataJO() ;
		
		//platform_prj.RT_platform_RTSet(rt_jo) ;
		if(rt_jo!=null)
			updateCxtDyn(platform_prj,rt_jo);
	}
	
	

//	private void onNodeSharePush(String jsonstr) throws Exception
//	{
//		try
//		{
//			if(log.isDebugEnabled())
//				log.debug("onNodeSharePush="+jsonstr);
//			UACh ch = this.getJoinedCh();
//			if(ch==null)
//				return ;
//			
//			if(Convert.isNullOrEmpty(jsonstr))
//				return ;
//			JSONObject jo = new JSONObject(jsonstr);
//			shareWritable = jo.optBoolean("share_writable", false) ;
//			shareDT = jo.optLong("share_dt", -1) ;
//			//if(log.isDebugEnabled())
//			//	log.debug("onNodeSharePush before updateChCxtDyn");
//			updateChCxtDyn(ch,jo);
//		}
//		finally
//		{
//			lastPushDT = System.currentTimeMillis();
//		}
//	}
	
	
	private static final int MAX_NUM = 5 ;
	/**
	 * record tag update info for later timeout checking
	 * @author jason.zhu
	 *
	 */
	private static class Tag2Up
	{
		UATag tag ;
		
		
		LinkedList<UAVal> prevVals = new LinkedList<>() ;
		
		public Tag2Up(UATag tag,UAVal val)
		{
			this.tag = tag ;
			this.prevVals.addLast(val);
		}
		
		public void putVal(UAVal v)
		{
			prevVals.addLast(v);
			
			if(prevVals.size()>MAX_NUM)
				prevVals.removeFirst() ;
		}
		
		public UAVal getLastVal()
		{
			return prevVals.getLast() ;
		}
	}
	
	private transient HashMap<String,Tag2Up> tagp2upMap = new HashMap<>() ;
	
	private void setToBuf(UATag tag,UAVal val)
	{
		String tagp = tag.getNodeCxtPathInPrj() ;
		Tag2Up t2u = tagp2upMap.get(tagp) ;
		if(t2u!=null)
		{
			t2u.putVal(val);
			return ;
		}
		
		t2u = new Tag2Up(tag,val) ;
		tagp2upMap.put(tagp,t2u) ;
		return ;
	}
	
	private void setTagErrInBuf(long dt)
	{
		for(Tag2Up t2u:tagp2upMap.values())
		{
			UAVal lastv = t2u.getLastVal() ;
			if(!lastv.isValid())
				continue ; //
			UATag tag = t2u.tag ;
			
			
			UAVal uav = new UAVal(false, null,dt,dt);
			tag.RT_setUAVal(uav);
			
			setToBuf(tag,uav) ;
		}
	}
	
	private void updateCxtDyn(UANodeOCTagsCxt p,JSONObject curcxt)
	{
		JSONArray jos = curcxt.optJSONArray("tags");
		if(jos!=null)
		{
			for(int i = 0,n = jos.length() ; i<n ; i++)
			{
				JSONObject tg = jos.getJSONObject(i);
				String name = tg.getString("n");
				UATag tag = p.getTagByName(name) ;
				if(tag==null)
					continue ;
				//var tagp =p+n ;
				boolean bvalid = tg.optBoolean("valid",false) ;
				long dt = tg.optLong("dt", -1) ;
				long chgdt = tg.optLong("chgdt",-1) ;
				
				Object ov = tg.opt("v") ;
				String strv = "";
				if(ov!=null&&ov!=JSONObject.NULL)
					strv =""+ov;
				//set to cxt
				ov = UAVal.transStr2ObjVal(tag.getValTp(), strv) ;
				UAVal uav = new UAVal(bvalid, ov,dt,chgdt);
				//tag.RT_setValStr(strv, true);
				tag.RT_setUAVal(uav);
				
				setToBuf(tag,uav) ;
			}
		}
		
		JSONArray subs = curcxt.optJSONArray("subs");
		if(subs!=null)
		{
			for(int i = 0, n = subs.length(); i < n ; i ++)
			{
				JSONObject sub = subs.getJSONObject(i);
				
				String subn = sub.getString("n") ;
				
				UANode uan = p.getSubNodeByName(subn) ;
				if(uan==null)
					continue ;
				if(!(uan instanceof UANodeOCTagsCxt))
					continue ;
				
				updateCxtDyn((UANodeOCTagsCxt)uan,sub) ;
			}
		}
	}
}
