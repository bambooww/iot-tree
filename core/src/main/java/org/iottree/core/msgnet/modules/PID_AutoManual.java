package org.iottree.core.msgnet.modules;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class PID_AutoManual  extends MNNodeEnd // implements ILang
{
	public static final String TP = "pid_auto_manual" ;
	
	double kp, ki, kd, sampleTime;
	
	@Override
	public String getColor()
	{
		return "#e6d970";
	}

	@Override
	public String getIcon()
	{
		return "PK_pid";
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
	
	// @Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPTitle()
	{
		return "Auto Or Manual";
	}
	
	@Override
	public int getMaxNumInModule()
	{
		return 1 ;// limit one
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();

		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.kp = jo.optDouble("kp") ;
		
	}

	// --------------
	

	private transient long lastMsgOutMS = -1;

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		StringBuilder failedr = new StringBuilder();
		if (!this.isParamReady(failedr))
		{
			return null;
		}

		return null;
	}

}
