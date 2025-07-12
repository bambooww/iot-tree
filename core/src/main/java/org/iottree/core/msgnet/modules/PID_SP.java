package org.iottree.core.msgnet.modules;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

/**
 * Setpoint
 * @author jason.zhu
 *
 */
public class PID_SP extends MNNodeEnd // implements ILang
{
	public static final String TP = "pid_sp" ;
	
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
		return "Setpoint";
	}
	
	@Override
	public int getMaxNumInModule()
	{
		return 1 ;// limit one
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
//		if(spMin>=this.spMax)
//		{
//			failedr.append("invalid SP min max") ;
//			return false;
//		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject();
//		jo.put("sp_min",this.spMin);
//		jo.put("sp_max",this.spMax);
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		
	}

	// --------------
	

	//private transient long lastMsgOutMS = -1;
	
	private transient PID_M ownerPID_M = null ;
	private transient boolean rtReady = false;
	
	@Override
	protected void RT_onBeforeNetRun()
	{
		StringBuilder failedr = new StringBuilder();
		ownerPID_M = (PID_M)this.getOwnRelatedModule() ;
		if(!ownerPID_M.isParamReady(failedr))
		{
			this.RT_DEBUG_ERR.fire("m", failedr.toString());
			rtReady = false ;
			return ;
		}
		rtReady = true ;
		this.RT_DEBUG_ERR.clear("m");
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		if(!rtReady)
			return null ;
		Number n = msg.getPayloadNumber() ;
		if(n==null)
			return null;
		ownerPID_M.RT_setSetpoint(n.doubleValue()) ;
		return null;
	}

}
