package org.iottree.core.router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import javax.script.ScriptException;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.cxt.UAContext;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.IdCreator;
import org.iottree.core.util.Lan;
import org.iottree.core.util.queue.HandleResult;
import org.iottree.core.util.queue.IObjHandler;
import org.iottree.core.util.queue.QueueThread;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * inner collator which organize tags data
 *  or receive outer data to update tags
 * @author jason.zhu
 *
 */
public abstract class RouterInnCollator extends RouterNode implements ILang
{
	static Lan lan = Lan.getLangInPk(RouterInnCollator.class) ;
			
	public static enum OutStyle
	{
		interval(0), on_change(1);

		private final int val;

		OutStyle(int v)
		{
			val = v;
		}

		public int getInt()
		{
			return val;
		}

		public String getTitle()
		{
			return lan.g("os_"+this.name()) ;
		}
		
		public static OutStyle valOfInt(int v)
		{
			switch(v)
			{
			case 1:
				return on_change;
			default:
				return interval ;
			}
		}
	}
	
	public static class TagVal
	{
		UATag tag ;
		UAVal val ;
		
		public TagVal(UATag tag,UAVal val)
		{
			this.tag = tag ;
			this.val = val ;
		}
		
		public UATag getTag()
		{
			return this.tag ;
		}
		
		public UAVal getVal()
		{
			return this.val ;
		}
	}
		
	long outIntervalMS = 30000 ;
	
	Thread thread = null ;
	
	QueueThread<TagVal> queTh =null ;
	
	public RouterInnCollator(RouterManager rm)
	{
		super(rm) ;
	}
	
	public String getTpTitle()
	{
		return g("ric_"+this.getTp()) ;
	}
	
	public long getOutIntervalMS()
	{
		return this.outIntervalMS ;
	}
	
	protected abstract RouterInnCollator newInstance(RouterManager rm) ;
	
	public abstract OutStyle getOutStyle();
	
	public final List<JoinOut> getConnectedJoinOuts()
	{
		List<JoinOut> jos = this.getJoinOutList() ;
		if(jos==null||jos.size()<=0)
			return null ;
		
		HashSet<String> fidset = new HashSet<>() ;
		for(JoinConn jc : this.belongTo.CONN_getROA2RICMap().values())
		{
			String fid = jc.getFromId() ;
			fidset.add(fid) ;
		}
		
		ArrayList<JoinOut> rets = new ArrayList<>() ;
		for(JoinOut jo:jos)
		{
			String fid = jo.getFromId() ;
			if(fidset.contains(fid))
				rets.add(jo) ;
		}
		return rets ;
	}
	
	/**
	 * called by overrider
	 * @param jo
	 * @param data
	 * @throws Exception
	 */
	protected final void RT_sendToJoinOut(JoinOut jo,RouterObj data)// throws Exception
	{
		jo.RT_setLastData(data);
		
		for(JoinConn jc : this.belongTo.CONN_getRIC2ROAMap().values())
		{
			String fid = jc.getFromId() ;
			if(fid.equals(jo.getFromId()))
			{
				JoinIn ji = jc.getToJI() ;
				sendOutToConn(jo,jc,ji,data) ;
			}
		}
	}
	
	private final void sendOutToConn(JoinOut jo,JoinConn jc,JoinIn ji,RouterObj data)// throws Exception
	{
		RouterObj ret = jc.RT_doTrans(data) ;
		if(ret==null)
			return ;//error
		
		RouterOuterAdp roa = (RouterOuterAdp)ji.getBelongNode() ;
		
		ji.RT_setLastData(ret);
		roa.RT_recvedFromJoinIn(ji,ret) ;
	}
	
	protected abstract void RT_onRecvedFromJoinIn(JoinIn ji,RouterObj recved) ;
	//public abstract String pullOut(String join_out_name) throws Exception;
	
	
	public synchronized boolean RT_start()
	{
		OutStyle os = getOutStyle() ;
		switch(os)
		{
		case interval:
			if(thread!=null)
				return true;
			thread = new Thread(this::runInterval) ;
			thread.start();
			return true ;
		case on_change:
			if(queTh!=null)
				return true ;
			queTh = new QueueThread<>(objH) ;
			queTh.start();
			return true ;
		}
		return false;
	}
	
	public synchronized void RT_stop()
	{
		OutStyle os = getOutStyle() ;
		switch(os)
		{
		case interval:
			Thread th = thread ;
			if(th==null)
				return ;
			
			th.interrupt();
			thread = null ;
			return ;
		case on_change:
			//thread = new Thread(this::runOnChange) ;
			QueueThread<?> qt = queTh ;
			if(qt==null)
				return  ;
			qt.stop();
			queTh = null ;
			return ;
		}
	}
	
	public boolean RT_isRunning()
	{
		OutStyle os = getOutStyle() ;
		switch(os)
		{
		case interval:
			return thread!=null ;
		case on_change:
			return queTh!=null && queTh.isRunning() ;
		}
		return false;
	}
	
	private void runInterval()
	{
		while(thread!=null)
		{
			try
			{
				Thread.sleep(this.outIntervalMS);
			}
			catch(Exception e) {}
			
			RT_runInIntvLoop() ;
		}
	}
	
	private IObjHandler<TagVal> objH = new IObjHandler<TagVal>() {

		@Override
		public void initHandler()
		{
			
		}

		@Override
		public int processFailedRetryTimes()
		{
			return 0;
		}

		@Override
		public long processRetryDelay(int retrytime)
		{
			return 0;
		}

		@Override
		public HandleResult processObj(TagVal o, int retrytime) throws Exception
		{
			RT_runOnChgTagVal(o) ;
			return HandleResult.Succ;
		}

		@Override
		public long handlerInvalidWait()
		{
			return 0;
		}

		@Override
		public void processObjDiscard(TagVal o) throws Exception
		{
			
		}

		} ; 
	
	/**
	 * override by sub
	 */
	protected void RT_runInIntvLoop()
	{
		
	}
	
	/**
	 * override by sub
	 */
	protected void RT_runOnChgTagVal(TagVal tv)
	{
		
	}
	
	public boolean DEBUG_triggerOutData(StringBuilder failedr)
	{
		failedr.append("not supported") ;
		return false;
	}
	
	/**
	 * called by inner running when found tag value changed
	 * @param tag
	 * @throws Exception
	 */
	public void fireChangedTagVal(UATag tag) throws Exception
	{
		if(this.queTh==null)
			throw new Exception("no queue thread start or not on_change style") ;
		
		UAVal val = tag.RT_getVal() ;
		TagVal tv = new TagVal(tag,val) ;
		this.queTh.enqueue(tv) ;
	}
	
//	private String rtErr = null ;
//	private long rtDT = -1 ;
	
	private transient UAContext cxt = null ;
	
	
	public UAContext RT_getContext() throws ScriptException
	{
		if(cxt!=null)
			return cxt ;
		
		synchronized(this)
		{
			if(cxt!=null)
				return cxt ;
			
			cxt = new UAContext(this.belongPrj);
			return cxt ;
		}
	}
	
	
	
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		jo.put("out_intv", this.outIntervalMS);
		return jo ;
	}
	
	protected boolean fromJO(JSONObject jo,StringBuilder failedr)
	{
		super.fromJO(jo, failedr) ;
		this.outIntervalMS = jo.optLong("out_intv",30000) ;
		if(this.outIntervalMS<0)
			this.outIntervalMS = 30000 ;
		return true ;
	}
	
	
	
	public static RouterInnCollator transFromJO(RouterManager rm,JSONObject jo,StringBuilder failedr)
	{
		String tp = jo.getString("_tp") ;
		if(Convert.isNullOrEmpty(tp))
			return null ;
		
		RouterInnCollator dp = newInstanceByTp(rm,tp) ;
//		switch(tp)
//		{
//		case RICSelTags.TP:
//			dp = new RICSelTags(rm) ;
//			break ;
//		case RICDef.TP:
//			dp = new RICDef(rm) ;
//			break ;
//		case RICRunTime.TP:
//			dp = new RICRunTime(rm) ;
//			break ;
//		default:
//			break ;
//		}
		
		if(dp==null)
			return null ;
		
		if(!dp.fromJO(jo,failedr))
			return null ;
		return dp ;
	}
	
	static List<RouterInnCollator> RICS = Arrays.asList(new RICSelTags(null),new RICFilterTags(null),new RICDef(null),new RICRunTime(null)) ;
	
	public static List<RouterInnCollator> listRICAll()
	{
		return RICS ;
	}
	
	public static RouterInnCollator newInstanceByTp(RouterManager rm,String tp)
	{
		for(RouterInnCollator ric:RICS)
		{
			if(ric.getTp().equals(tp))
			{
				return ric.newInstance(rm) ;
			}
		}
		return null ;
	}
}
