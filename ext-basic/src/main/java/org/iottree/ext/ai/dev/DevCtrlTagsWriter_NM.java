package org.iottree.ext.ai.dev;

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
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONArray;
import org.json.JSONObject;

public class DevCtrlTagsWriter_NM extends MNNodeMid //implements IMNRunner
{
	/**
	 * Asynchronous run mode
	 * true it's will run in thread after in msg. this will not block outer push thread.
	 * but it will ignore all in msg when thread running
	 */
	//boolean asynMode = true;
	
	@Override
	public String getTP()
	{
		return "devctrl_tagswriter";
	}

	@Override
	public String getTPTitle()
	{
		return "Device Tags Writer";
	}

	@Override
	public String getColor()
	{
		return "#5B9CD7";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#eeeeee";
	}

	@Override
	public String getIcon()
	{
		return "\\uf2db";
	}

	@Override
	public int getOutNum()
	{
		return 2;
	}
	
	@Override
	public String RT_getOutColor(int idx)
	{
		switch(idx)
		{
		case 0:
			return "green";
		case 1:
			return "red";
		}
		return null ;
	}
	
	@Override
	public String RT_getOutTitle(int idx)
	{
		switch(idx)
		{
		case 0:
			return "ok";
		case 1:
			return "err";
		}
		return null ;
	}

	@Override
	public boolean isShowOutTitle()
	{
		return true;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
//		jo.put("asyn", this.asynMode) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
//		this.asynMode = jo.optBoolean("asyn", true) ;
	}

	// --------------
	
//	private transient boolean bAsynRun = false;
//	private transient AsynTh asynTh = null ;

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		boolean b = runSyn(msg) ;
		if(b)
			return RTOut.createOutIdx().asIdxMsg(0, msg);
		else
			return RTOut.createOutIdx().asIdxMsg(1, msg);
		
//		if(bAsynRun)
//		{
//			this.RT_DEBUG_WARN.fire("tag_w","node is running,ignore this msg",msg.toJO().toString());
//			return null ; //ignore all in msg
//		}
//		
//
//		if(!this.asynMode)
//		{
//			boolean b = runSyn(msg) ;
//			return RTOut.createOutIdx().asIdxMsg(0, new MNMsg().asPayload(new JSONObject().put("succ", true)));
//		}
//		
//		//asyn
//		synchronized(this)
//		{
//			bAsynRun = true ;
//			asynTh = new AsynTh(msg) ;
//			asynTh.start();
//		}
//		
//		return RTOut.createOutIdx().asIdxMsg(1, new MNMsg().asPayload(new JSONObject().put("succ", true)));
	}
	
	
	private boolean runSyn(MNMsg msg)
	{
		StringBuilder failedr = new StringBuilder() ;
		JSONObject pld = msg.getPayloadJO(null) ;
		if(pld==null)
			return false;
		
		String set_str_val = pld.optString("set_str_val") ;
		if(Convert.isNullOrEmpty(set_str_val))
			return false;
		JSONObject pm = pld.optJSONObject("parameter") ;
		if(pm==null)
			return false;
		
		String bind_w_tag = pm.optString("bind_w_tag") ;
		if(Convert.isNullOrEmpty(bind_w_tag))
			return false;
		
		UAPrj prj = this.getPrj() ;
		UATag tag = prj.getTagByPath(bind_w_tag) ;
		if(tag==null)
			return false;

		if(!tag.RT_writeValStr(set_str_val, failedr))
		{
			RT_DEBUG_ERR.fire("tag_w", failedr.toString());
			return false ;
		}
		
		RT_DEBUG_ERR.clear("tag_w");
		//return RTOut.createOutIdx().asIdxMsg(0,msg);
		return true ;
	}
	
//	private class AsynTh extends Thread
//	{
//		MNMsg msg ;
//		
//		public AsynTh(MNMsg msg)
//		{
//			this.msg = msg ;
//		}
//		
//		public void run()
//		{
//			try
//			{
//				boolean b = runSyn(msg) ;
//				if(b)
//				{
//					DevCtrlTagsWriter_NM.this.RT_sendMsgOut(RTOut.createOutIdx().asIdxMsg(0, msg));
//				}
//			}
//			catch(Exception ee)
//			{
//				RT_DEBUG_ERR.fire("tag_w",ee.getMessage(),ee);
//			}
//			finally
//			{
//				asynTh = null ;
//				bAsynRun = false;
//			}
//		}
//	}
//
//	@Override
//	public boolean RT_start(StringBuilder failedr)
//	{
//		//failedr.append("no support") ;
//		//return false;
//		return true ;
//	}
//
//	@Override
//	public synchronized void RT_stop()
//	{
//		
//	}
//
//	@Override
//	public boolean RT_isRunning()
//	{
//		return bAsynRun;
//	}
//
//	@Override
//	public boolean RT_isSuspendedInRun(StringBuilder reson)
//	{
//		return false;
//	}
//	
//	/**
//	 * false will not support runner
//	 * @return
//	 */
//	public boolean RT_runnerEnabled()
//	{
//		return this.asynMode ;
//	}
//	
//	/**
//	 * true will not support manual trigger to start
//	 * @return
//	 */
//	public boolean RT_runnerStartInner()
//	{
//		return true;
//	}

}
