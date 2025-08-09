package org.iottree.ext.armlinux;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.iottree.core.util.jt.JSONTemp;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.ext.armlinux.ArmLinuxConf.GPIOPin;
import org.json.JSONObject;

public class GPIOOut_NE extends MNNodeEnd
{
	static ILogger log = LoggerManager.getLogger(GPIOOut_NE.class) ;
			
	public static final String TP = "gpio_out";

	int gpioNum = -1;
	
	private transient GPIOPin gpioPin = null ;
	
	private transient boolean bPinInit = false;
	
	@Override
	protected boolean ENV_check()
	{
		return ArmLinuxConf.isGPIOOk();
	}

	@Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPTitle()
	{
		return "GPIO Out";
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
	public String getColor()
	{
		return "#7f66ca";
	}

	@Override
	public String getIcon()
	{
		return "\\uf2db";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if (this.gpioNum < 0)
		{
			failedr.append("no led gpio number set");
			return false;
		}

		LinkedHashMap<Integer,GPIOPin> n2p = ArmLinuxConf.getNum2Pin() ;
		gpioPin = n2p.get(this.gpioNum) ;
		if(gpioPin==null)
		{
			failedr.append("no gpio pin found with number="+this.gpioNum);
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
		jo.putOpt("gpio_num", this.gpioNum);
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.gpioNum = jo.optInt("gpio_num", -1);
		clearPin() ;
	}

	@Override
	protected void onAfterLoaded()
	{

	}
	
	private boolean initPin()
	{
		if(gpioPin==null)
			return false;
		
		if(log.isDebugEnabled())
			log.debug("initPin "+ArmLinuxConf.GPIO_EXPORT_PATH+"="+gpioPin.num);
		
		try (FileWriter writer = new FileWriter(ArmLinuxConf.GPIO_EXPORT_PATH))
		{
			writer.write(""+gpioPin.num);
			//return true;
		}
		catch ( IOException e)
		{
			if(log.isErrorEnabled())
				log.error("initPin "+ArmLinuxConf.GPIO_EXPORT_PATH,e);
			// System.err.println("Error controlling LED: " + e.getMessage());
			this.RT_DEBUG_INF.fire("gpio_out", "initPin", e);
			return false;
		}
		
		String path_dir = ArmLinuxConf.GPIO_PATH + gpioPin.num + "/direction";
		if(log.isDebugEnabled())
			log.debug("initPin path_dir=out");
		try (FileWriter writer = new FileWriter(path_dir))
		{
			writer.write("out");
			return true;
		}
		catch ( IOException e)
		{
			if(log.isErrorEnabled())
				log.error("initPin "+path_dir,e);
			// System.err.println("Error controlling LED: " + e.getMessage());
			this.RT_DEBUG_INF.fire("gpio_out", "initPin", e);
			return false;
		}
		
	}
	
	private boolean clearPin()
	{
		if(gpioPin==null)
			return false;
		
		if(Convert.isNullOrEmpty(ArmLinuxConf.GPIO_UNEXPORT_PATH))
			return false;
		
		if(log.isDebugEnabled())
			log.debug("clearPin "+ArmLinuxConf.GPIO_UNEXPORT_PATH+"="+gpioPin.num);
		try (FileWriter writer = new FileWriter(ArmLinuxConf.GPIO_UNEXPORT_PATH))
		{
			writer.write(""+gpioPin.num);
			return true;
		}
		catch ( IOException e)
		{
			if(log.isErrorEnabled())
				log.error("clearPin "+ArmLinuxConf.GPIO_UNEXPORT_PATH,e);
			// System.err.println("Error controlling LED: " + e.getMessage());
			this.RT_DEBUG_ERR.fire("gpio_out", "clearPin", e);
			return false;
		}
		finally
		{
			this.bPinInit = false;
		}
	}

	private boolean writeOut(boolean state)
	{
		if(!this.bPinInit)
		{
			initPin() ;
			this.bPinInit = true ;
		}
		
		if(gpioPin==null)
			return false;
		String path_val = ArmLinuxConf.GPIO_PATH + gpioPin.num + "/value";
		if(log.isDebugEnabled())
			log.debug("writeOut valuePath="+state);
		try (FileWriter writer = new FileWriter(path_val))
		{
			writer.write(state ? "1" : "0");
			return true;
		}
		catch ( IOException e)
		{
			// System.err.println("Error controlling LED: " + e.getMessage());
			if(log.isErrorEnabled())
				log.error("writeOut "+path_val,e);
			
			this.RT_DEBUG_ERR.fire("gpio_out", "writeOut "+path_val, e);
			return false;
		}
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		Object pld = msg.getPayload() ;
		if(pld==null)
			return null ;
		if(pld instanceof Boolean)
		{
			writeOut((Boolean)pld) ;
		}
		else if(pld instanceof Number)
		{
			writeOut(((Number)pld).intValue()>0) ;
		}
		// this.RT_DEBUG_INF.fire("msg_in","topic="+this.topic+" out
		// ",msg.getPayloadStr());
		return null;
	}

	@Override
	protected void RT_onAfterNetStop()
	{
		clearPin() ;
	}
}
