package org.iottree.core.msgnet.nodes;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.iottree.core.msgnet.IMNOnOff;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.util.RepeatTP;
import org.iottree.core.util.Convert;
import org.iottree.core.util.ILang;
import org.iottree.core.util.Lan;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class TimerTrigger_NS extends MNNodeStart implements IMNRunner, IMNOnOff
{
	boolean bDelayExec = false;

	long delayExecMS = 0;

	RepeatTP repeatTp = RepeatTP.intv;

	long intervalMS = 10000;

	LocalTime betweenS = null;

	LocalTime betweenE = null;

	boolean bOnWeek= false;
	int onWeekMark = 0xFF;

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
		return "\\uf017";
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

	// @Override
	// public String getNodeTP()
	// {
	// return "inject";
	// }
	//
	// @Override
	// public String getNodeTPTitle()
	// {
	// return g("inject");
	// }

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		switch (repeatTp)
		{
		case intv:
			if (intervalMS <= 0)
			{
				failedr.append("no_intv_ms");
				return false;
			}
			return true;
		case intv_bt:
			if (intervalMS <= 0)
			{
				failedr.append("no_intv_ms");
				return false;
			}
			return true;
		default:
			return true;
		}
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.put("b_delay", this.bDelayExec);
		jo.put("delay_ms", this.delayExecMS);
		jo.put("repeat_tp", this.repeatTp.getInt());
		jo.put("interval_ms", this.intervalMS);
		if (betweenS != null)
		{
			jo.put("between_s", betweenS.toString()); // betweenS.toSecondOfDay());
		}
		if (betweenE != null)
			jo.put("between_e", betweenE.toString());//.toSecondOfDay());
		jo.put("b_on_week", this.bOnWeek) ;
		jo.put("on_week", onWeekMark);
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		this.bDelayExec = jo.optBoolean("b_delay", false);
		this.delayExecMS = jo.optLong("delay_ms", 0);
		if (this.delayExecMS < 0)
			this.delayExecMS = 0;
		this.repeatTp = RepeatTP.valOfInt(jo.optInt("repeat_tp", RepeatTP.intv.getInt()));
		if (this.repeatTp == null)
			this.repeatTp = RepeatTP.intv;
		this.intervalMS = jo.optLong("interval_ms", 10000);
		String ss = jo.optString("between_s");
		if (Convert.isNotNullEmpty(ss))
			this.betweenS = LocalTime.parse(ss);//.ofSecondOfDay(ss);
		else
			this.betweenS = null ;
		ss = jo.optString("between_e");
		if (Convert.isNotNullEmpty(ss))
			this.betweenE = LocalTime.parse(ss);//.ofSecondOfDay(ss);
		else
			this.betweenE = null ;
		
		this.bOnWeek = jo.optBoolean("b_on_week", false) ;
		this.onWeekMark = jo.optInt("on_week", 0xFF);
	}

	public boolean isDelayExec()
	{
		return this.delayExecMS > 0;
	}

	public long getDelayExecMS()
	{
		return this.delayExecMS;
	}

	public RepeatTP getRepectTp()
	{
		return this.repeatTp;
	}

	public long getIntervalMS()
	{
		return intervalMS;
	}

	public LocalTime getBetweenStart()
	{
		return this.betweenS;
	}

	public LocalTime getBetweenEnd()
	{
		return this.betweenE;
	}

	public String getBetweenDesc()
	{
		if(this.betweenS!=null && this.betweenE!=null)
		{
			if(this.betweenS.compareTo(this.betweenE)<=0)
				return this.betweenS.toString()+g("to_day") + this.betweenE.toString() ;
			else
				return this.betweenS.toString()+ g("to_next_day") + this.betweenE.toString() ;
		}
		
		return this.betweenS!=null?this.betweenS.toString():"00:00" +" -- "+this.betweenE!=null?this.betweenE.toString():"24:00" ;
	}
	/**
	 * 
	 * @param mon_sun
	 *            0 - monday 1 - tue 6 - sunday
	 * @return
	 */
	public boolean checkOnWeek(int mon_sun)
	{
		return (this.onWeekMark & (1 << mon_sun)) > 0;
	}
	
	private static String[] WEEK_N = new String[] {"","sunday",
			"monday",
			"tuesday",
			"wednesday",
			"thursday",
			"friday",
			"saturday"};
	
	public String getOnWeekDesc()
	{
		StringBuilder sb = new StringBuilder() ;
		boolean bfirst = true ;
		//Lan lan = Lan.getLangInPk(this.getClass()) ;
		for(int i = 1 ; i <= 7  ; i ++)
		{
			if((this.onWeekMark & (1<<i))>0)
			{
				if(bfirst) bfirst=false;
				else sb.append(",") ;
				
				sb.append(g(WEEK_N[i])) ;
			}
		}
		return sb.toString() ;
	}

	// ---------------
	
	private boolean bRun = false;
	// private boolean bInSending = false;
	private Thread timerTH = null;
	
	private String pausedBT = null ;

	@Override
	public boolean RT_triggerByOnOff(StringBuilder failedr)
	{
		MNMsg msg = new MNMsg();
		msg.asPayload(System.currentTimeMillis());
		RT_sendMsgOut(RTOut.createOutAll(msg));
		return true;
	}

	@Override
	protected void RT_renderDiv(StringBuilder divsb)
	{
//		if(Convert.isNotNullEmpty(pausedBT))
//			divsb.append("<div class=\"rt_blk\"><span style=\"color:rgba(255,0,0,0.5)\">"+pausedBT+"</div>") ;
		super.RT_renderDiv(divsb);
	}
	// private Timer timer = null ;
	// private TimerTask task = null;

	

	private void runInTh()
	{
		if(this.bDelayExec)
		{
			try
			{
				Thread.sleep(this.delayExecMS);
			}
			catch ( Exception ee)
			{
			}
		}

		do
		{
			if(this.repeatTp==RepeatTP.intv_bt)
			{// check begin time -  end time
				if(!checkBetween())
				{
					pausedBT = g("suspend_out")+" "+getBetweenDesc();
					sleep(this.intervalMS);
					continue ;
				}
				if(!checkWeek())
				{
					pausedBT =  g("suspend_notin")+" "+getOnWeekDesc() ;
					sleep(this.intervalMS);
					continue ;
				}
				
				pausedBT = null ;
			}
			
			synchronized (this)
			{// in here cannot be interrupted
				try
				{
					// System.out.println(System.currentTimeMillis()/1000) ;
					// bInSending = true ;
					MNMsg msg = new MNMsg();
					msg.asPayload(System.currentTimeMillis());
					RT_sendMsgOut(RTOut.createOutAll(msg));
				}
				catch ( Exception ee)
				{
					ee.printStackTrace();
					this.RT_DEBUG_fireErr("TimerTrigger sendMsgOut err:" + ee.getMessage(), ee);
				}
				finally
				{
					// bInSending=false;
				}
			}

			if (!bRun)
				break;
			
			sleep(this.intervalMS);
			
		} while (bRun);
	}
	
	private void sleep(long ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch ( Exception ee)
		{
		}
	}
	
	private boolean checkBetween()
	{
		if(betweenS==null && betweenE==null)
			return true ;
		
		LocalTime nowt = LocalTime.now() ;
		if(betweenS!=null && betweenE==null)
		{
			return nowt.compareTo(betweenS)>=0 ;
		}
		
		if(betweenS==null && betweenE!=null)
		{
			return nowt.compareTo(betweenE)<=0 ;
		}
		
		if(betweenS.compareTo(betweenE)<=0)
		{
			return nowt.compareTo(betweenS)>=0 && nowt.compareTo(betweenE)<=0 ;
		}
		else
		{//span to next day,and two seg  E - 24:00    00:00 - S
			return nowt.compareTo(betweenS)>=0 || nowt.compareTo(betweenE)<=0 ;
		}
	}
	
	private boolean checkWeek()
	{
		if(!bOnWeek) return true ;
		int dayofweek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) ;
		return (onWeekMark & (1<<dayofweek)) >0 ;
	}

	private Runnable runner = new Runnable() {
		@Override
		public void run()
		{
			try
			{
				runInTh();
			}
			finally
			{
				bRun = false;
				timerTH = null;
			}
		}
	};

	@Override
	synchronized public boolean RT_start(StringBuilder failedr)
	{
		if (bRun)
			return true;

		bRun = true;
		timerTH = new Thread(runner);
		timerTH.start();
		return true;
	}

	@Override
	synchronized public void RT_stop()
	{
		Thread th = timerTH;
		if (th != null)
			th.interrupt();
		bRun = false;
		timerTH = null;
	}

	@Override
	public boolean RT_isRunning()
	{
		return bRun;
	}
	
	@Override
	public boolean RT_isSuspendedInRun(StringBuilder reson)
	{
		String pbt = this.pausedBT ;
		if(Convert.isNotNullEmpty(pbt))
		{
			reson.append(pbt);
			return true ;
		}
		return false;
	}
}
