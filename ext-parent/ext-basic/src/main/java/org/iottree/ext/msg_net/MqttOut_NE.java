package org.iottree.ext.msg_net;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class MqttOut_NE extends MNNodeEnd
{
	@Override
	public String getTP()
	{
		return "mqtt_out";
	}

	@Override
	public String getTPTitle()
	{
		return "MQTT Out";
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
		return "#debed7";
	}

	@Override
	public String getIcon()
	{
		return "\\\\uf1eb-270";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JSONObject getParamJO()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
