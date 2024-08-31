package org.iottree.core.msgnet.modules;

import java.util.Arrays;
import java.util.List;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.RTOut;
import org.json.JSONObject;

public class MemMultiQueue extends MNModule implements IMNRunner
{
//	static List<MNNode> SUP_NS = Arrays.asList(new MemQueue_NM()) ;
//	@Override
//	protected List<MNNode> getSupportedNodes()
//	{
//		return SUP_NS;
//	}

	@Override
	public String getTP()
	{
		return "mem_multi_que";
	}

	@Override
	public String getTPTitle()
	{
		return g("mem_multi_que");
	}

	@Override
	public String getColor()
	{
		return "#f0a566";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf141";
	}
	
	protected boolean supportCxtVars()
	{
		return false;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
	}
	
	// ------------- queue
	
	
	// rt ---
	
	private boolean bRun = false;
	
	private Thread procTh = null ;
	
	synchronized void RT_onQueMsgIn(MemQueue_NM que,MNMsg inmsg)
	{
		que.queMsgList.addLast(inmsg);
		this.notifyAll();
	}
	
	
	private Runnable runner = new Runnable()
	{
		public void run()
		{
			try
			{
				while(bRun)
				{
					procModeNormal();
				}
			}
			finally
			{
				procTh = null ;
				bRun = false;
			}
		}
	};
	
	private void procModeNormal()
	{
		List<MNNode> ns = this.getRelatedNodes() ;
		if(ns==null)
		{
			UTIL_sleep(10);
			return  ;
		}
		
		boolean has_more = false;
		do
		{
			has_more = false;
			for(MNNode n:ns)
			{
				MemQueue_NM mqn = (MemQueue_NM) n;
				int len ;
				if((len=mqn.queMsgList.size())<=0)
					continue ;
				
				if(len>1)
					has_more = true ;
				MNMsg qi = mqn.queMsgList.getFirst() ;
				
				RT_sendMsgByRelatedNode(mqn,RTOut.createOutIdx().asIdxMsg(0, qi));
				
				synchronized(this)
				{
					mqn.queMsgList.removeFirst() ;
				}
			}
		}
		while(has_more) ;
		
		synchronized(this)
		{
			try
			{
				wait();
			}
			catch (InterruptedException ie)
			{}
		}
	}

	@Override
	public synchronized boolean RT_start(StringBuilder failedr)
	{
		if (bRun)
			return true;

		bRun = true;
		procTh = new Thread(runner);
		procTh.start();
		return true;
	}

	@Override
	public synchronized void RT_stop()
	{
		Thread th = procTh;
		if (th != null)
			th.interrupt();
		bRun = false;
		procTh = null;
	}

	@Override
	public boolean RT_isRunning()
	{
		return bRun;
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
		return true ;
	}
	
	/**
	 * true will not support manual trigger to start
	 * @return
	 */
	public boolean RT_runnerStartInner()
	{
		return false;
	}
}
