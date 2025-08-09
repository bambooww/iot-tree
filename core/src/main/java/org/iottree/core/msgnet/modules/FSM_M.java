package org.iottree.core.msgnet.modules;

import java.util.List;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNModule;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNode;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.msgnet.util.PIDController;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * Finite State Machine
 * 
 * @author jason.zhu
 *
 */
public class FSM_M   extends MNModule implements IMNRunner
{
	public static final String TP = "fsm" ;
	
	double kp = 1; //gain
	double ki = 10.0; //minute integral time
	double kd = 0.0 ; //minute derivative time
	double sampleTime = 1.0; //second
	
	double inputMin = 0.0 ;
	double inputMax = 100.0 ;
	
	double outputMin = 0.0 ;
	double outputMax = 10.0 ;
	
	double stopOutputV = 0.0 ; //when PID is not in loop,it will output default ctrl value
	
	double errOutputV = 0.0 ;  //when PID is err ,it will output default ctrl value
	
	@Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPTitle()
	{
		return g(TP);
	}

	@Override
	public String getColor()
	{
		return "#ea95a6";
	}

	@Override
	public String getIcon()
	{
		return "PK_flw";
	}
	
	@Override
	public String getTitleColor()
	{
		return "#333333" ;
	}
//	
//	public PID_PV getPV()
//	{
//		return this.getRelatedNodeFirst(PID_PV.class) ;
//	}
//	
//	public PID_SP getSP()
//	{
//		return this.getRelatedNodeFirst(PID_SP.class) ;
//	}
//	
//	public PID_Output getOutput()
//	{
//		return this.getRelatedNodeFirst(PID_Output.class) ;
//	}
//	
//	public PID_Err getErr()
//	{
//		return this.getRelatedNodeFirst(PID_Err.class) ;
//	}
//	
//	public PID_AutoManual getAutoManual()
//	{
//		return this.getRelatedNodeFirst(PID_AutoManual.class) ;
//	}
//	
//	public PID_ManualOutput getManualOutput()
//	{
//		return this.getRelatedNodeFirst(PID_ManualOutput.class) ;
//	}
//	

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
//		if(getPV()==null)
//		{
//			failedr.append("no PV node set") ;
//			return false;
//		}
//		if(getSP()==null)
//		{
//			failedr.append("no SP node set") ;
//			return false;
//		}
//		if(getOutput()==null)
//		{
//			failedr.append("no Output node set") ;
//			return false;
//		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("kp",this.kp) ;
		jo.put("ki",this.ki) ;
		jo.put("kd",this.kd) ;
		jo.put("st", this.sampleTime) ;
		jo.put("input_min", this.inputMin);
		jo.put("input_max", this.inputMax);
		jo.put("output_min", this.outputMin);
		jo.put("output_max", this.outputMax);
		jo.put("output_stop_v", this.stopOutputV) ;
		jo.put("output_err_v", this.errOutputV) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.kp = jo.optDouble("kp",1) ;
		this.ki = jo.optDouble("ki",10.0) ;
		this.kd = jo.optDouble("kd",0.0) ;
		this.sampleTime = jo.optDouble("st",1.0) ;
		this.inputMin = jo.optDouble("input_min",0.0) ;
		this.inputMax = jo.optDouble("input_max",100.0) ;
		this.outputMin = jo.optDouble("output_min",0.0) ;
		this.outputMax = jo.optDouble("output_max",10.0) ;
		
		this.stopOutputV = jo.optDouble("output_stop_v",0.0) ;
		this.errOutputV = jo.optDouble("output_err_v",0.0) ;
	}
	
	public String getPmTitle()
	{
		return String.format("P=%s I=%s D=%s",this.kp,this.ki,this.kd);
	}
	
	@Override
	protected void onAfterLoaded()
	{
			List<MNNode> nodes = this.getRelatedNodes() ;
			
			if(nodes!=null)
			{
//				for(MNNode n:nodes)
//				{
//					if(n instanceof PID_PV_I)
//					{
//						this.pidPV = (PID_PV_I)n ;
//					}
//					
//					if(n instanceof PID_SP)
//					{
//						this.pidSP = (PID_SP)n ;
//					}
//					
//					if(n instanceof PID_SP)
//					{
//						this.pidSP = (PID_SP)n ;
//					}
//				}
			}
	}

	
	
	
	@Override
	public void checkAfterSetParam()
	{
		
	}
	
	// ----
	
	private transient double RT_sp = Double.NaN ;
	private transient long RT_sp_ms = -1 ;
	
	private transient double RT_pv = Double.NaN ;
	private transient long RT_pv_ms = -1 ;
	
	private transient double RT_lastOut = Double.NaN ;
	private transient long RT_lastMS = -1 ;
	
	private transient PIDController RT_pid_ctrl = null ;
	
	private transient PID_Output RT_output_n = null ;
	private transient PID_Err RT_err_n = null ; 
	

	@Override
	protected void RT_onBeforeNetRun()
	{
		StringBuilder failedr = new StringBuilder() ;
		RT_pid_ctrl = null ;
		if(!isParamReady(failedr))
		{
			return ;
		}
//		RT_output_n = this.getOutput() ;
//		RT_err_n = this.getErr() ;
//		RT_pid_ctrl = new PIDController(kp, ki, kd, inputMin, inputMax, outputMin, outputMax);
//		
//		if(stopOutputV<outputMin)
//			stopOutputV = outputMin ;
//		else if(stopOutputV>outputMax)
//			stopOutputV = outputMax ;
//		
//		if(errOutputV<outputMin)
//			errOutputV = outputMin ;
//		else if(errOutputV>outputMax)
//			errOutputV = outputMax ;
	}

	
	
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		if(RT_sp_ms>0)
		{
			StringBuilder divsb = new StringBuilder() ;
			divsb.append("<div tp='pid' class=\"rt_blk\"><span style=\"color:green\">") ;
			divsb.append("<br>sp="+this.RT_sp+" ("+Convert.toDecimalDigitsStr(RT_pid_ctrl.getNorSetpoint(),1)+")"+Convert.calcDateGapToNow(RT_sp_ms));
			divsb.append("</span></div>") ;
			divblks.add(new DivBlk("pid_sp",divsb.toString())) ;
		}
		
		if(RT_pid_ctrl!=null&&RT_sp_ms>0&&RT_lastMS>0)
		{
			StringBuilder divsb = new StringBuilder() ;
			divsb.append("<div tp='pid' class=\"rt_blk\"><span style=\"color:green\">") ;
			divsb.append("<br>pv="+Convert.toDecimalDigitsStr(this.RT_pv,1)+" ("+Convert.toDecimalDigitsStr(RT_pid_ctrl.getNorInput(),5)+")"+Convert.calcDateGapToNow(RT_lastMS));
			divsb.append("<br>out="+Convert.toDecimalDigitsStr(this.RT_lastOut,1)+" ("+Convert.toDecimalDigitsStr(RT_pid_ctrl.getNorOutput(),5)+")");
			
			divsb.append("</span></div>") ;
			
			divblks.add(new DivBlk("pid",divsb.toString())) ;
		}
		super.RT_renderDiv(divblks);
	}
	
	private boolean bRun = false;
	
	private Thread procTh = null ;

	private Runnable runner = new Runnable()
	{
		public void run()
		{
			try
			{
				while(bRun)
				{
					UTIL_sleep((long)(sampleTime*1000)) ;
					
					//RT_runPID();
				}
			}
			finally
			{
				synchronized(this)
				{
					procTh = null ;
					bRun = false;
				}
			}
		}
	};
	
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
