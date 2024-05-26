package org.iottree.core.msgnet.nodes;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.util.RepeatTP;
import org.iottree.core.util.ILang;
import org.iottree.core.util.Lan;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class TimerTrigger_NS extends MNNodeStart implements IMNRunner
{
	long delayExecMS = -1 ;
	
	RepeatTP repectTp =RepeatTP.intv ;
	
	long intervalMS = 1000 ;
	
	LocalTime betweenS = null ;
	
	LocalTime betweenE = null ;
	
	int onWeekMark = 0xFF ;
	
	@Override
	public String getTP()
	{
		return "timer";
	}

	@Override
	public String getTPTitle()
	{
		return g("timer");
	}

	@Override
	public String getColor()
	{
		return "#b5b5b3";
	}

	@Override
	public String getIcon()
	{
		return "\\uf017" ;
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

//	@Override
//	public String getNodeTP()
//	{
//		return "inject";
//	}
//
//	@Override
//	public String getNodeTPTitle()
//	{
//		return g("inject");
//	}


	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		switch(repectTp)
		{
		case intv:
			if(intervalMS<=0)
			{
				failedr.append("no_intv_ms") ;
				return false;
			}
			return true ;
		case intv_bt:
			if(intervalMS<=0)
			{
				failedr.append("no_intv_ms") ;
				return false;
			}
			return true ;
		default:
			return true ;
		}
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		//jo.put("b_delay", this.bDelayExec) ;
		jo.put("delay_ms", this.delayExecMS) ;
		jo.put("repeat_tp", this.repectTp.getInt()) ;
		if(betweenS!=null)
			jo.put("between_s", betweenS.toSecondOfDay());
		if(betweenE!=null)
			jo.put("between_e", betweenE.toSecondOfDay());
		jo.put("on_week", onWeekMark) ;
		return jo ;
	}

	@Override
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		//this.bDelayExec = jo.optBoolean("b_delay",false) ;
			this.delayExecMS = jo.optLong("delay_ms",-1) ;
			this.repectTp = RepeatTP.valOfInt(jo.optInt("repeat_tp",RepeatTP.intv.getInt())) ;
			if(this.repectTp==null)
				this.repectTp = RepeatTP.intv ;
			int ss = jo.optInt("between_s",-1) ;
			if(ss>0)
				this.betweenS = LocalTime.ofSecondOfDay(ss) ;
			ss = jo.optInt("between_e",-1) ;
			if(ss>0)
				this.betweenE = LocalTime.ofSecondOfDay(ss) ;
			this.onWeekMark = jo.optInt("on_week",0xFF) ;
	}

	public boolean isDelayExec()
	{
		return this.delayExecMS>0;
	}
	
	public long getDelayExecMS()
	{
		return this.delayExecMS ;
	}
	
	public RepeatTP getRepectTp()
	{
		return this.repectTp ;
	}
	
	public  long getIntervalMS()
	{
		return intervalMS ;
	}
	
	public LocalTime getBetweenStart()
	{
		return this.betweenS ;
	}
	
	public LocalTime getBetweenEnd()
	{
		return this.betweenE ;
	}
	
	/**
	 * 
	 * @param mon_sun 0 - monday 1 - tue 6 - sunday
	 * @return
	 */
	public boolean checkOnWeek(int mon_sun)
	{
		return (this.onWeekMark & (1<<mon_sun)) > 0 ;
	}

	//  --------------- 
	

	private Timer timer = null ;
	private TimerTask task = null;
	
	private void onTaskRun()
	{
		MNMsg msg = new MNMsg() ;
		msg.asPayload(System.currentTimeMillis()) ;
		RT_sendMsgOut(RTOut.ALL,msg) ;
	}
	
	@Override
	public boolean RT_init(StringBuilder failedr)
	{
		
		return true;
	}

	@Override
	synchronized public boolean RT_start(StringBuilder failedr)
	{
		if(timer!=null)
			return true ;
		
		timer = new Timer();
        task =new TimerTask() {
            @Override
            public void run() {
            	onTaskRun() ;
            }
        };
        
        switch(repectTp)
        {
        case intv_bt:
        case intv:
        default:
        	timer.scheduleAtFixedRate(task, delayExecMS, this.intervalMS);
        }
		return true;
	}

	@Override
	synchronized public void RT_stop()
	{
		if(timer==null)
			return ;
		try
		{
            task.cancel();
            timer.purge(); // Remove cancelled tasks from the timer
		}
		finally
		{
			task=null;
			timer=null ;
		}
	}

	@Override
	public boolean RT_isRunning()
	{
		return timer!=null;
	}
}
