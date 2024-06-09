package org.iottree.ext.msg_net;

import org.iottree.core.msgnet.MNNodeStart;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

public class MqttIn_NS extends MNNodeStart
{
	@Override
	public String getTP()
	{
		return "mqtt_in";
	}

	@Override
	public String getTPTitle()
	{
		return "MQTT In";
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

	@Override
	public String getColor()
	{
		return "#debed7";
	}

	@Override
	public String getIcon()
	{
		return "\\\\uf1eb-90";
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
	protected void setParamJO(JSONObject jo)
	{
		// TODO Auto-generated method stub
		
	}

}
