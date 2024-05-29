package org.iottree.core.msgnet.nodes;

import org.iottree.core.msgnet.IMNOnOff;
import org.iottree.core.msgnet.IMNRunner;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class ManualTrigger  extends MNNodeStart implements IMNOnOff
{

	@Override
	public JSONTemp getInJT()
	{
		// TODO Auto-generated method stub
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

	@Override
	public String getTP()
	{
		return "manual";
	}

	@Override
	public String getTPTitle()
	{
		return g("manual");
	}

	@Override
	public String getColor()
	{
		return "#9fbccf";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf0a4";
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
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		
	}

	public boolean RT_triggerByOnOff(StringBuilder failedr)
	{
		if(!this.supportInOnOff())
		{
			failedr.append("not support") ;
			return false;
		}
		
		MNMsg msg = new MNMsg() ;
		
		RT_sendMsgOut(RTOut.createOutAll(msg)) ;
		return true;
	}


}
