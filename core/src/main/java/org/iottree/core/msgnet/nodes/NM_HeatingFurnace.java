package org.iottree.core.msgnet.nodes;

import java.util.List;
import java.util.Random;

import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * Heating Furnace Simulator
 * @author jason.zhu
 *
 */
public class NM_HeatingFurnace extends MNNodeMid implements IMNRunner
{
	public static final String TP = "heating_furn_sim";
	
	
    private double ambientTemp = 25.0; // 环境温度
	
    private double initialTemp = 25.0 ; //initial temp
    
    private double inputSignalLow = 0 ;
    
    private double inputSignalHigh = 10.0 ; //10V 10kw

	private double tempLow = 0 ;
	
	private double tempHigh = 150.0 ;
	
	private double outputSignalLow = 4000 ; // tempLow's signal
	private double outputSignalHigh = 20000 ; //tempHigh's signal
	private long outputIntvMS = 1000 ;//输出监测温度信号时间间隔

	private double inputSignalV = 0 ;
    private double heaterPower= 0 ; // 加热功率(0-10 kw) 
    private long inputDT = -1 ;
    
	private double temperature=25.0 ; // 当前温度(°C)
	
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
		return "#e95757";
	}

	@Override
	public String getIcon()
	{
		return "\\uf06d";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(this.inputSignalLow>=this.inputSignalHigh)
		{
			failedr.append("Input Signal Low cannot >= High") ;
			return false;
		}
		if(this.tempLow>=this.tempHigh)
		{
			failedr.append("Temperature Low cannot >= High") ;
			return false;
		}
		if(this.outputSignalLow>=this.outputSignalHigh)
		{
			failedr.append("Output Signal Low cannot >= High") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("ambient_t",this.ambientTemp) ;
		jo.put("init_t",this.initialTemp) ;
		jo.put("in_signal_low",this.inputSignalLow) ;
		jo.put("in_signal_high",this.inputSignalHigh) ;
		jo.put("temp_low",this.tempLow) ;
		jo.put("temp_high",this.tempHigh) ;
		jo.put("out_signal_low",this.outputSignalLow) ;
		jo.put("out_signal_high",this.outputSignalHigh) ;
		jo.put("out_intv", outputIntvMS) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.ambientTemp = jo.optDouble("ambient_t",25.0) ;
		this.initialTemp = jo.optDouble("init_t",25.0) ;
		this.inputSignalLow = jo.optDouble("in_signal_low",0) ;
		this.inputSignalHigh = jo.optDouble("in_signal_high",10.0) ;
		this.tempLow = jo.optDouble("temp_low",0.0) ;
		this.tempHigh = jo.optDouble("temp_high",150.0) ;
		this.outputSignalLow = jo.optDouble("out_signal_low",4000) ;
		this.outputSignalHigh = jo.optDouble("out_signal_high",20000) ;
		this.outputIntvMS = jo.optLong("out_intv",1000) ;
		if(this.outputIntvMS<=0)
			this.outputIntvMS = 1000 ;
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}
	
	@Override
	protected void onAfterLoaded()
	{
		//this.temperature = this.initialTemp ;
	}
	
	@Override
	protected void RT_onBeforeNetRun()
	{
		inputSignalV = 0 ;
	    heaterPower= 0 ; // 加热功率(0-10 kw) 
	    inputDT = -1 ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		//input heater power
		Number inputn = msg.getPayloadNumber();
		if(inputn==null)
			return null;//invalid
		
		double inputv = inputn.doubleValue();
		if(Double.isNaN(inputv))
			return null ;
		
		this.inputSignalV = inputv; 
		this.heaterPower = calcInput2HeaterPower(this.inputSignalV);
		this.inputDT = System.currentTimeMillis() ;
		return null;
	}

	private double calcInput2HeaterPower(double input_signal_v)
	{
		if(input_signal_v>this.inputSignalHigh)
			input_signal_v = this.inputSignalHigh ;
		if(input_signal_v<this.inputSignalLow)
			input_signal_v = this.inputSignalLow ;

		double a = 10/(inputSignalHigh-inputSignalLow) ;
		double b = -inputSignalLow*a ;
		return a*input_signal_v+b ;
	}
	
	private double calcTemp2Output(double temp_v)
	{
		if(temp_v>this.tempHigh)
			temp_v = this.tempHigh ;
		if(temp_v<this.tempLow)
			temp_v = this.tempLow ;
		
		double a = (this.outputSignalHigh-this.outputSignalLow)/(this.tempHigh-this.tempLow) ;
		double b = this.outputSignalLow - a * this.tempLow ;
		return a*temp_v+b ;
	}
	//
	
    
    private Random random = new Random();
    
    // 更新炉温模型
    /**
     * 
     * @param dt 
     * @param heaterPower 0-10 (kw)
     */
    void RT_update()
    {
    	double dt = outputIntvMS/1000.0 ; //间隔时间秒值
        // 简化的一阶模型：加热和自然冷却
        // 加热效率系数和冷却系数是虚构的，实际系统需要实验测定
        double heatingEffect = heaterPower * 1.5;  // 加热效果系数
        double coolingEffect = 0.08;              // 自然冷却系数
        
        // 温度变化微分方程
        double deltaTemp = (heatingEffect - coolingEffect * (temperature - ambientTemp)) * dt;
        temperature += deltaTemp;
        
        // 添加随机噪声模拟传感器噪声
        temperature += (random.nextDouble() - 0.5) * 1.0; // ±0.5°C噪声
        
        double out_signal_v = calcTemp2Output(temperature);
        MNMsg m = new MNMsg().asPayload(out_signal_v) ;
        RTOut out = RTOut.createOutIdx().asIdxMsg(0, m) ;
        this.RT_sendMsgOut(out);
    }
    
    public double getTemperature() {
        return temperature;
    }
    
    public double getHeaterPower() {
        return heaterPower;
    }
    
    @Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder() ;
		divsb.append("<div tp='pid' class=\"rt_blk\"><span style=\"color:green\">") ;
		divsb.append("<br>input="+Convert.toDecimalDigitsStr(this.inputSignalV,1)+" ("+Convert.toDecimalDigitsStr(heaterPower,2)+")");
		if(inputDT>0)
			divsb.append(Convert.calcDateGapToNow(inputDT)) ;
		divsb.append("<br>Temperature="+Convert.toDecimalDigitsStr(this.temperature,1));
		divsb.append("</span></div>") ;
		divblks.add(new DivBlk("heat_furn",divsb.toString())) ;
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
					UTIL_sleep(outputIntvMS) ;
					
					RT_update();
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
